package com.herenow.fase1;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.support.v4.app.NotificationCompat;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import util.Weacon;

/**
 * Created by Milenko on 27/05/2015.
 */
public class WifiUpdater implements Runnable {
    private static HashMap<String, Weacon> weaconsLaunchedTable;
    private static ArrayList<Object> showedNotifications;
    private static int mId;
    private static NotificationCompat.Builder mBuilder;
    private static NotificationManager mNotificationManager;
    private final Boolean demo;
    WifiManager wifi;
    TextView textView;
    Activity act;
    private int cont;
    private int levelOld = -100;
    private int demoCount;
    private int ntimes = 30;//numbers of ticks for launching a fake weacon

    public WifiUpdater(TextView tv, Activity activity, Boolean demo) {
        this.textView = tv;
        this.act = activity;
        this.demo = demo;
        weaconsLaunchedTable = new HashMap<>();
        showedNotifications = new ArrayList<>(); //Weacons showed in notification
        wifi = (WifiManager) activity.getSystemService(Context.WIFI_SERVICE);
//        this.act = activity;
        cont = 1;
        demoCount = 1;
        mNotificationManager = (NotificationManager) act.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    private static void sendNotification(Activity act, Weacon we) {
//        PendingIntent resultPendingIntent = null;
        Intent resultIntent = new Intent(act.getBaseContext(), BrowserActivity.class);

        resultIntent.putExtra("wName", we.getName());
        resultIntent.putExtra("wUrl", we.getUrl());
        resultIntent.putExtra("wLogo", we.getLogo());


//        Context ctx = act.getBaseContext();
//
//        Intent backIntent = new Intent(ctx, MainActivity.class);
//        backIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//        Intent intent = new Intent(ctx, BrowserActivity.class);
//        intent.putExtra("whatever", "whatever");
//        final PendingIntent pendingIntent = PendingIntent.getActivities(ctx, UNIQUE_REQUEST_CODE++,
//                new Intent[] {backIntent, intent}, PendingIntent.FLAG_ONE_SHOT);
//
//



        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(act.getBaseContext());//TODO puede que necesite un contexto, no una actividad
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(BrowserActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        //example
       /* Bitmap bm = BitmapFactory.decodeResource(act.getResources(), R.mipmap.ic_aurora);
        NotificationCompat.Action myaction = new NotificationCompat.Action(R.drawable.ic_stat_name, "Call", resultPendingIntent);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(act)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setLargeIcon(bm)
                .setContentTitle("Weacons available")
                .setContentText(act.getString(R.string.cid))
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND | Notification.FLAG_SHOW_LIGHTS)
                .setLights(0xff00ff00, 300, 100)
                .setLights(0xE6D820, 300, 100)
                .setTicker("HearNow weacons detected");
                .addAction(myaction);*/
        //NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

      /*  String[] events = {"H&M", "ZARA", "AIA", "USA Ambassay", "Starbucks", "Bus station"};

        // Sets a title for the Inbox in expanded layout
        inboxStyle.setBigContentTitle("Available weacons");

        // Moves events into the expanded layout
        for (String event : events) {
            inboxStyle.addLine(event);
        }
        // Moves the expanded layout object into the notification object.
        mBuilder.setStyle(inboxStyle);

        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) act.getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
       */

        if (showedNotifications.size() == 0) { //New Notification
            mId = 1;
            showedNotifications.add(we);
            NotificationCompat.Action myAction = new NotificationCompat.Action(R.drawable.ic_silence, "Turn Off", resultPendingIntent);//TODO Turn off this notifification
            mBuilder = new NotificationCompat.Builder(act)
                    .setSmallIcon(R.drawable.ic_stat_name_hn)
                    .setLargeIcon(we.getLogo())
                    .setContentTitle(we.getName())
                    .setContentText(we.getMessage())
                    .setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND | Notification.FLAG_SHOW_LIGHTS)
                    .setLights(0xE6D820, 300, 100)
                    .setTicker("HearNow weacon detected\n" + we.getName())
                    .addAction(myAction);
            NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
            bigTextStyle.setBigContentTitle(we.getName());
            bigTextStyle.bigText(we.getMessage());

            // Moves the expanded layout object into the notification object.
            mBuilder.setStyle(bigTextStyle);

            mBuilder.setContentIntent(resultPendingIntent);

        } else { // Update the notification already sent

            mNotificationManager.cancel(mId);
            mId = 2;

            showedNotifications.add(we);

            Bitmap bm = BitmapFactory.decodeResource(act.getResources(), R.mipmap.ic_launcher);
            String msg = Integer.toString(showedNotifications.size()) + " weacons around you";
            mBuilder = new NotificationCompat.Builder(act) //TODO put the hour in extended notification
                    .setSmallIcon(R.drawable.ic_stat_name_dup)
                    .setLargeIcon(bm)
                    .setContentTitle(msg)
                    .setContentText(((Weacon) showedNotifications.get(0)).getName() + " and others ")
                    .setAutoCancel(true)
//                    .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND | Notification.FLAG_SHOW_LIGHTS)
                    .setLights(0x36E629, 100, 100)
                    .setTicker(msg);
            NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
            inboxStyle.setBigContentTitle(msg);
            inboxStyle.setSummaryText("Currently " + Integer.toString(showedNotifications.size() + 5) + " weacons active");//TODO Add a meaningfull summary, probably containing the number of weacons in app

            for (Object weacon : showedNotifications) {
                inboxStyle.addLine(((Weacon) weacon).getName());
            }
            mBuilder.setStyle(inboxStyle);
            mBuilder.setContentIntent(resultPendingIntent);
        }

        mNotificationManager.notify(mId, mBuilder.build());
        weaconsLaunchedTable.put(we.getSSID(), we);
    }

    @Override
    public void run() {
        if (demo) {
            if (demoCount == 1 || demoCount % ntimes == 0) {
                findFakeWeacon();
            }
            demoCount++;
        } else {
            wifi.startScan();
            List<ScanResult> sr = wifi.getScanResults();
            List<String> list = new ArrayList<String>();

            for (ScanResult r : sr) {
//            if (r.SSID.equals("piripiri")) {
//                String intensidad = Integer.toString(r.level);
//                textView.setText(Integer.toString(cont) + "piripiri int=" + intensidad);
//                if (r.level > -75 && levelOld < -75) {
//                    sendNotification(act);
//                }
//                levelOld = r.level;
//                break;
                if (MainActivity.weaconsTable.containsKey(r.SSID) && !weaconsLaunchedTable.containsKey(r.SSID)) {
                    Weacon we = MainActivity.weaconsTable.get(r.SSID);
                    int threshold = we.getLevel();
                    if (levelOld < threshold && r.level >= threshold) {
                        sendNotification(act, we);
                    }
                    levelOld = r.level;
                }
            }
            cont++;
        }
    }

    /**
     * Launch a fake weacon, randomly from the file
     */
    private void findFakeWeacon() {
        Random generator = new Random();
        Weacon we;
        Collection<Weacon> intermediate = MainActivity.weaconsTable.values();
        Object[] values = intermediate.toArray();
        do {
            we = (Weacon) values[generator.nextInt(values.length)];
        } while (weaconsLaunchedTable.containsKey(we.getSSID()));
        sendNotification(act, we);
    }
}
