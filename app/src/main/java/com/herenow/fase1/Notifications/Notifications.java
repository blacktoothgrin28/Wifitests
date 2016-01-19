package com.herenow.fase1.Notifications;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.text.SpannableString;

import com.herenow.fase1.Activities.BrowserActivity;
import com.herenow.fase1.Activities.CardsActivity;
import com.herenow.fase1.Activities.ConnectToWifi;
import com.herenow.fase1.Activities.WeaconListActivity;
import com.herenow.fase1.LineTime;
import com.herenow.fase1.R;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import it.gmariotti.cardslib.library.prototypes.CardWithList;
import parse.WeaconParse;
import util.OnTaskCompleted;
import util.formatter;
import util.myLog;

/**
 * Created by Milenko on 17/07/2015.
 */
public abstract class Notifications {
    private static final String NOTIFICATION_DELETED_ACTION = "NOTIFICATION_DELETED";
    public static HashMap<String, WeaconParse> weaconsLaunchedTable; //For not relaunche the same weacon {obid, WeaconParse}
    static String tag = "noti";
    private static ArrayList<WeaconParse> showedNotifications; //list of weacosn currently showed in a notification
    private static NotificationManager mNotificationManager;
    private static Activity acti;
    private static int mIdSingle, mIdGroup;
    private static int currentId = 1;
    private final static BroadcastReceiver receiverDeleteNotification = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                myLog.add("<<<<<<<<<<<<Has borrado una notif>>>>", tag);
                currentId = currentId + 2;
                showedNotifications = new ArrayList<>();

            } catch (Exception e) {
                myLog.add("---ERROR<<<<<<<<<<<<Has borrado una notif>>>>" + e.getMessage());
            }
            context.unregisterReceiver(this);
        }
    };

    private static PendingIntent pendingDeleteIntent;

    public static void Initialize(Activity act) {
        acti = act;
        weaconsLaunchedTable = new HashMap<>();
        showedNotifications = new ArrayList<>(); //Weacons showed in notification
//        myLog.add("showedNotif created in initialization", tag);
        mNotificationManager = (NotificationManager) act.getSystemService(Context.NOTIFICATION_SERVICE);

    }

    public static boolean shouldBeLaunched(WeaconParse we) {
        //TODO decide from online info
        boolean b = false;

        try {
            myLog.add("***hashlaunched has keys : " + weaconsLaunchedTable.keySet().toString());
            myLog.add("buscando enl a tabala de lanzados el el weacon:" + we.getName());
            b = !weaconsLaunchedTable.containsKey(we.getObjectId());
        } catch (Exception e) {
            myLog.add(" --should be launched" + e.getLocalizedMessage());
        }

        return b;
    }

    public static void sendNotification(final WeaconParse we) {
        try {
            if (!we.NotificationRequiresFetching() || we.NotificationAlreadyFetched()) {
                we.setAlreadyFetched(false);
                myLog.add("no requiere o ya fetched", tag);
                Intent intent = new Intent(NOTIFICATION_DELETED_ACTION);
                pendingDeleteIntent = PendingIntent.getBroadcast(acti.getBaseContext(), 0, intent, 0);

                //TODO put in parse that this weacon was notified
                if (showedNotifications == null) {
                    showedNotifications = new ArrayList<>();
                    myLog.add("***hemos debido crear denuevo la showednotifications", tag);
                }

                if (showedNotifications.size() == 0) {
                    sendNewNotification(we);
                } else {
                    updateNotification(we);
                }
                weaconsLaunchedTable.put(we.getObjectId(), we);

            } else {
                //Need to fetch info
                myLog.add("need fetching", tag);
                getInfoBuses(new OnTaskCompleted() {
                    @Override
                    public void OnTaskCompleted(ArrayList elements) {
                        myLog.add("tenemos resultados de consulta de timepos en parada:" + elements.size(), tag);
                        we.setFetchingResults(elements);
                        sendNotification(we);
                    }

                    @Override
                    public void OnError(Exception e) {
                        myLog.add("Not possible to fecth info from parada *" + e);
                    }
                }, we.getParadaId());
            }
        } catch (Exception e) {
            myLog.add("---ERROR in sendNotification: " + e.getMessage());
        }
    }


    private static void getInfoBuses(OnTaskCompleted listener, String paradaId) {
        (new FetchUrl(listener)).execute(paradaId);
    }

    private static void sendNewNotification(WeaconParse we) {

        Intent resultIntent;
        TaskStackBuilder stackBuilder;
        PendingIntent resultPendingIntent;
        NotificationCompat.Builder notification;
        Class<?> cls;

        mIdSingle = currentId;
        showedNotifications.add(we);
        myLog.add("sendign new notif", tag);
        acti.registerReceiver(receiverDeleteNotification, new IntentFilter(NOTIFICATION_DELETED_ACTION));

        if (we.isBrowser()) {
            myLog.add("este weacon es de tipo browser", tag);
            cls = BrowserActivity.class;
        } else {
            cls = CardsActivity.class;
        }

        resultIntent = new Intent(acti.getBaseContext(), cls)
                .putExtra("wUrl", we.getUrl())
                .putExtra("wName", we.getName())
                .putExtra("wLogo", we.getLogoRounded())
                .putExtra("wComapanyDataObId", we.getCompanyDataObjectId())
                .putExtra("wCards", we.getCards())
                .putExtra("typeOfAiportCard", "Departures");

        stackBuilder = TaskStackBuilder.create(acti.getBaseContext());
        stackBuilder.addParentStack(cls);
        stackBuilder.addNextIntent(resultIntent);
        resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT); //Todo solve the stack for going back from cards

        notification = buildSingleNotification(we, resultIntent, resultPendingIntent);

        mNotificationManager.notify(mIdSingle, notification.build());
    }

    private static void updateNotification(WeaconParse we) {

        NotificationCompat.Builder notif;

        mNotificationManager.cancel(mIdSingle);
        mIdGroup = mIdSingle + 1;
        showedNotifications.add(we);

        notif = buildMultipleNotification(we);

        mNotificationManager.notify(mIdGroup, notif.build());
    }

    private static NotificationCompat.Builder buildSingleNotification(WeaconParse we, Intent resultIntent, PendingIntent resultPendingIntent) {
        NotificationCompat.Builder notif;

        NotificationCompat.Action actionSilence = new NotificationCompat.Action(R.drawable.ic_silence, "Turn Off", resultPendingIntent);//TODO to create the silence intent

        notif = new NotificationCompat.Builder(acti)
                .setSmallIcon(R.drawable.ic_stat_name_hn)
                .setLargeIcon(we.getLogoRounded())
                .setContentTitle(we.getName())
                .setContentText(we.getType())
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND | Notification.FLAG_SHOW_LIGHTS)
                .setLights(0xE6D820, 300, 100)
                .setTicker("Weacon detected\n" + we.getName())
                .setDeleteIntent(pendingDeleteIntent)
                .addAction(actionSilence);

        //Airport Buttons
        if (we.isAirport()) {
            Intent arrivalsIntent = new Intent(resultIntent);
            arrivalsIntent.putExtra("typeOfAiportCard", "Arrivals");
            PendingIntent pendingArrivals = PendingIntent.getActivity(acti.getBaseContext(), 1, arrivalsIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Action DepartureAction = new NotificationCompat.Action(R.drawable.ic_flight_takeoff_white_24dp, "Departures", resultPendingIntent);
            NotificationCompat.Action ArrivalAction = new NotificationCompat.Action(R.drawable.ic_flight_land_white_24dp, "Arrivals", pendingArrivals);
            notif.addAction(DepartureAction)
                    .addAction(ArrivalAction);
        }

        //ZARA APP button
        if (we.getName().equals("ZARA")) {
            final String appPackageName = "com.inditex.zara"; // getPackageName() from Context or Activity object
            Intent getAppIntent;

            try {
                getAppIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName));
            } catch (android.content.ActivityNotFoundException anfe) {
                getAppIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName));
                myLog.add("app no found: " + anfe.getLocalizedMessage());
            }

            PendingIntent pendingGetApp = PendingIntent.getActivity(acti.getBaseContext(), 1, getAppIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Action getAppAction = new NotificationCompat.Action(R.drawable.ic_market, "Get App", pendingGetApp);
            notif.addAction(getAppAction);
        }

        //Call Waitress
        if (we.getType().equals("restaurant")) {
            //TODO replace
            Intent connectToWifi = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=hola"));
            PendingIntent pendingWIFIConnect = PendingIntent.getActivity(acti.getBaseContext(), 1, connectToWifi, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Action getAppAction = new NotificationCompat.Action(R.drawable.ic_waiter, "Waiter", pendingWIFIConnect);
            notif.addAction(getAppAction);
        }

        //WIFI APP button
        if (we.getName().startsWith("Conj")) {
            //TODO replace, doesn't do anything
//            Intent connectToWifi = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=hola"));
            Intent connectToWifi = new Intent(acti, ConnectToWifi.class);
            PendingIntent pendingWIFIConnect = PendingIntent.getService(acti.getBaseContext(), 1, connectToWifi, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Action getAppAction = new NotificationCompat.Action(R.drawable.ic_wifi, "Connect", pendingWIFIConnect);
            notif.addAction(getAppAction);
        }

        // Bus stop
        if (we.getType().equals("bus_stop")) {
            formatter form = new formatter(we.getFetchedElements());

            notif.setContentText("BUS STOP. " + form.summarizeAllLines());

            //InboxStyle
            NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
            inboxStyle.setBigContentTitle(we.getMessage());
//            inboxStyle.setSummaryText("Deciding what to put here ");

            for (SpannableString s : form.summarizeByOneLine()) {
                inboxStyle.addLine(s);
            }
            notif.setStyle(inboxStyle);

        } else {
            //Bigtext style
            NotificationCompat.BigTextStyle textStyle = new NotificationCompat.BigTextStyle();
            textStyle.setBigContentTitle(we.getName());
            textStyle.bigText(we.getMessage());
            notif.setStyle(textStyle);
        }

        notif.setContentIntent(resultPendingIntent);

        return notif;
    }

    @NonNull
    private static NotificationCompat.Builder buildMultipleNotification(WeaconParse we) {
        Intent resultIntent;
        TaskStackBuilder stackBuilder;
        PendingIntent resultPendingIntent;
        Bitmap bm = BitmapFactory.decodeResource(acti.getResources(), R.mipmap.ic_launcher);

        String msg = Integer.toString(showedNotifications.size()) + " weacons around you";

        NotificationCompat.Builder notif = new NotificationCompat.Builder(acti) //TODO put the hour in extended notification
                .setSmallIcon(R.drawable.ic_stat_name_dup)
                .setLargeIcon(bm)
                .setContentTitle(msg)
                .setContentText(showedNotifications.get(0).getName() + " and others ")
                .setAutoCancel(true)
                        //                    .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND | Notification.FLAG_SHOW_LIGHTS)
                .setLights(0x36E629, 100, 100)
                .setDeleteIntent(pendingDeleteIntent)
                .setTicker(msg);
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle(msg);
        inboxStyle.setSummaryText("Currently " + Integer.toString(showedNotifications.size() + 5)
                + " weacons active");//TODO Add a meaningfull summary, probably containing the number of weacons in app

        for (WeaconParse weacon : showedNotifications) {
            inboxStyle.addLine(weacon.getName());
        }

        notif.setStyle(inboxStyle);
        resultIntent = new Intent(acti.getBaseContext(), WeaconListActivity.class);
        resultIntent.putExtra("wName", we.getName());
        resultIntent.putExtra("wUrl", we.getUrl());
        resultIntent.putExtra("wLogo", we.getLogoRounded());

        stackBuilder = TaskStackBuilder.create(acti.getBaseContext());
        stackBuilder.addParentStack(WeaconListActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        notif.setContentIntent(resultPendingIntent);

        return notif;
    }


    static class FetchUrl extends AsyncTask<String, Void, ArrayList<CardWithList.DefaultListObject>> {
        private OnTaskCompleted onTaskCompletedListener;

        FetchUrl(OnTaskCompleted listener) {
            this.onTaskCompletedListener = listener;
        }

        @Override
        protected ArrayList<CardWithList.DefaultListObject> doInBackground(String... strings) {
            Connection.Response response = null;
            String paradaId = strings[0];
            String q2 = "http://www.santqbus.santcugat.cat/consultatr.php?idparada=" + paradaId + "&idliniasae=-1&codlinea=-1";

            try {
                response = Jsoup.connect(q2)
                        .ignoreContentType(true)
                        .referrer("http://www.google.com")
                        .timeout(5000)
                        .followRedirects(true)
                        .execute();
            } catch (IOException e) {
//                e.printStackTrace();
                onTaskCompletedListener.OnError(e);
            }

            if (response == null) return null;

            String s = response.body();
            String[] partes = s.split("\\}\\,\\{|\\[\\{|\\}\\]");

            ArrayList lineTimes = new ArrayList();

            for (String parte : partes) {
                if (parte.length() > 3) {
                    LineTime lineTime = null;
                    try {
                        lineTime = new LineTime(new JSONObject("{" + parte + "}"));
                    } catch (JSONException e) {
                        onTaskCompletedListener.OnError(e);
                    }
                    lineTimes.add(lineTime);
                    myLog.add(lineTime.toString());
                }
            }
            myLog.add("updated todas las lineas de la parada:" + paradaId);

            return lineTimes;
        }

        @Override
        protected void onPostExecute(ArrayList<CardWithList.DefaultListObject> elements) {
            super.onPostExecute(elements);
            if (elements != null) onTaskCompletedListener.OnTaskCompleted(elements);
        }
    }

}
