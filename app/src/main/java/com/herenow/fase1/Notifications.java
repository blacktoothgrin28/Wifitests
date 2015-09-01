package com.herenow.fase1;

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
import android.net.wifi.ScanResult;
import android.support.v4.app.NotificationCompat;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import parse.WeaconParse;
import parse.WifiSpot;
import util.AppendLog;
import util.parameters;

/**
 * Created by Milenko on 17/07/2015.
 */
public abstract class Notifications {
    private static final String NOTIFICATION_DELETED_ACTION = "NOTIFICATION_DELETED";
    public static HashMap<String, WeaconParse> weaconsLaunchedTable; //For not relaunche the same weacon {obid, WeaconParse}
    private static ArrayList<WeaconParse> showedNotifications;
    private static NotificationManager mNotificationManager;
    private static Activity acti;
    private static int mIdSingle, mIdGroup;
    private static int currentId = 1;
    private static NotificationCompat.Builder notif;
    private static PendingIntent pendingDeleteIntent;
    private static boolean justDeleted = false;

    private final static BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
//            aVariable = 0; // Do what you want here
            try {
                AppendLog.appendLog("<<<<<<<<<<<<Has borrado una notif>>>>");
                currentId = currentId + 2;
                justDeleted = true;

            } catch (Exception e) {
                e.printStackTrace();
                AppendLog.appendLog("---ERROR<<<<<<<<<<<<Has borrado una notif>>>>" + e.getMessage());
            }
            context.unregisterReceiver(this);
        }
    };
    private static int iScan = 0;

    public static void Initialize(Activity act) {
        acti = act;
        weaconsLaunchedTable = new HashMap<>();
        showedNotifications = new ArrayList<>(); //Weacons showed in notification
        mNotificationManager = (NotificationManager) act.getSystemService(Context.NOTIFICATION_SERVICE);

    }

    /**
     * From the list of ScanResults, it looks if SSIDS are present in the table of weacons,
     * and launch the notifications
     *
     * @param sr scanResults
     * @return number of matches
     */
    public static void CheckScanResults(final List<ScanResult> sr) {
        iScan++;
        if (iScan % 8 == 0) {
            ReportLocalPlaces();
        }

        ArrayList<String> bssids = new ArrayList<>();
        ArrayList<String> ssids = new ArrayList<>();
        StringBuilder sb = new StringBuilder("+++++++ Scan results:+" + "\n");

        for (ScanResult r : sr) {
            bssids.add(r.BSSID);
            ssids.add(r.SSID);
            sb.append("  '" + r.SSID + "' | " + r.BSSID + " | l= " + r.level + "\n");
        }
        sb.append("+++++++++");
        AppendLog.appendLog(sb.toString(), "WE");

        //Query BSSID
        ParseQuery<WifiSpot> qb = ParseQuery.getQuery(WifiSpot.class);
        qb.whereContainedIn("bssid", bssids);
        //Query SSID
        ParseQuery<WifiSpot> qs = ParseQuery.getQuery(WifiSpot.class);
        qs.whereContainedIn("ssid", ssids);
        qs.whereEqualTo("relevant", true);
        //Main Query
        List<ParseQuery<WifiSpot>> queries = new ArrayList<>();
        queries.add(qb);
        queries.add(qs);

        ParseQuery<WifiSpot> mainQuery = ParseQuery.or(queries);
        mainQuery.fromPin(parameters.pinWeacons);
        mainQuery.include("associated_place");

        mainQuery.findInBackground(new FindCallback<WifiSpot>() {

            @Override
            public void done(List<WifiSpot> spots, ParseException e) {
                if (e == null) {
                    int n = spots.size();
                    LogInManagement.ReportDetectedSpots(spots);
                    if (n == 0) {
                        AppendLog.appendLog("MegaQuery no match", "WE");
                    } else { //There are matches
                        AppendLog.appendLog("From megaquery we have several matches: " + n, "WE");
                        MainActivity.reportScanning(n, sr.size());

                        StringBuilder sb = new StringBuilder("***********\n");
                        for (WifiSpot spot : spots) {
                            sb.append(spot.toString() + "\n");
                            registerHitSSID(spot);
                            WeaconParse we = spot.getWeacon();

                            //TODO  Log in y log out
                            //send a Notification for each one if has
                            if (shouldBeLaunched(we)) {
                                sendNotification(we);
                            }
                        }
                        sb.append("**********");
                        AppendLog.appendLog(sb.toString(), "WE");
                    }
                } else {
                    AppendLog.appendLog("---Error en megaquery Checkresults", "WE");
                }
            }
        });

    }

    /***
     * save in Parse that the spot has benn hit
     *
     * @param spot
     */
    private static void registerHitSSID(final WifiSpot spot) {
        // Check if already upladed
        AppendLog.appendLog(spot.toString(), "HIT");
        try {
            ParseQuery query = ParseQuery.getQuery("w_hit");
            query.whereEqualTo("user", ParseUser.getCurrentUser());
            query.whereEqualTo("ssid", spot);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> list, ParseException e) {
                    if (list.size() == 0) { //create
                        ParseObject hit = new ParseObject("w_hit");
                        hit.put("ssid", spot);
                        hit.put("user", ParseUser.getCurrentUser());
                        hit.put("nhits", 1);
                        hit.saveInBackground();
                    } else {//update
                        ParseObject hit = list.get(0);
                        hit.increment("nhits");
                        hit.saveInBackground();
                    }
                }


            });


        } catch (Exception e) {
            AppendLog.appendLog("--error en subir hit: " + e.getMessage());
        }
    }

    /***
     * Ask the position and shows the weacons nearby
     */
    private static void ReportLocalPlaces() {
        MainActivity.mPos.connect(Position.REASON.AwareOfPlaces);

    }

    private static boolean shouldBeLaunched(WeaconParse we) {
        //TODO decide from online info
        boolean b = !weaconsLaunchedTable.containsKey(we.getObjectId());
        return b;
    }

    public static void sendNotification(WeaconParse we) {

        try {

            Intent resultIntent;
            PendingIntent resultPendingIntent;
            TaskStackBuilder stackBuilder;

            Intent intent = new Intent(NOTIFICATION_DELETED_ACTION);
            pendingDeleteIntent = PendingIntent.getBroadcast(acti.getBaseContext(), 0, intent, 0);

            //TODO put in parse that this weacon was notified

            if (showedNotifications.size() == 0 || justDeleted) { //New Notification
                justDeleted = false;
                mIdSingle = currentId;
                showedNotifications.add(we);

                acti.registerReceiver(receiver, new IntentFilter(NOTIFICATION_DELETED_ACTION));

                resultIntent = new Intent(acti.getBaseContext(), BrowserActivity.class);
                //            resultIntent.putExtra("weacon",  we);
                resultIntent.putExtra("wName", we.getName());
                resultIntent.putExtra("wUrl", we.getUrl());
                resultIntent.putExtra("wLogo", we.getLogoRounded());
                stackBuilder = TaskStackBuilder.create(acti.getBaseContext());
                stackBuilder.addParentStack(BrowserActivity.class);
                stackBuilder.addNextIntent(resultIntent);
                resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);


                NotificationCompat.Action myAction = new NotificationCompat.Action(R.drawable.ic_silence, "Turn Off", resultPendingIntent);
                notif = new NotificationCompat.Builder(acti)
                        .setSmallIcon(R.drawable.ic_stat_name_hn)
                        .setLargeIcon(we.getLogoRounded())
                        .setContentTitle(we.getName())
                        .setContentText(we.getMessage())
                        .setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND | Notification.FLAG_SHOW_LIGHTS)
                        .setLights(0xE6D820, 300, 100)
                        .setTicker("HearNow weacon detected\n" + we.getName())
                        .setDeleteIntent(pendingDeleteIntent)
                        .addAction(myAction);
                NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
                bigTextStyle.setBigContentTitle(we.getName());
                bigTextStyle.bigText(we.getMessage());

                notif.setStyle(bigTextStyle);
                notif.setContentIntent(resultPendingIntent);

                mNotificationManager.notify(mIdSingle, notif.build());

            } else { // Update the notification already sent

                mNotificationManager.cancel(mIdSingle);
                mIdGroup = mIdSingle + 1;
                showedNotifications.add(we);
                Bitmap bm = BitmapFactory.decodeResource(acti.getResources(), R.mipmap.ic_launcher);
                String msg = Integer.toString(showedNotifications.size()) + " weacons around you";
                notif = new NotificationCompat.Builder(acti) //TODO put the hour in extended notification
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
                inboxStyle.setSummaryText("Currently " + Integer.toString(showedNotifications.size() + 5) + " weacons active");//TODO Add a meaningfull summary, probably containing the number of weacons in app
                //            AppendLog.appendLog("15");
                for (WeaconParse weacon : showedNotifications) {
                    inboxStyle.addLine(weacon.getName());
                }
                notif.setStyle(inboxStyle);
                //            AppendLog.appendLog("16");
                resultIntent = new Intent(acti.getBaseContext(), WeaconListActivity.class);
                resultIntent.putExtra("wName", we.getName());
                resultIntent.putExtra("wUrl", we.getUrl());
                resultIntent.putExtra("wLogo", we.getLogoRounded());
                stackBuilder = TaskStackBuilder.create(acti.getBaseContext());
                stackBuilder.addParentStack(WeaconListActivity.class);
                stackBuilder.addNextIntent(resultIntent);
                resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                notif.setContentIntent(resultPendingIntent);

                mNotificationManager.notify(mIdGroup, notif.build());
            }

            weaconsLaunchedTable.put(we.getObjectId(), we);
        } catch (Exception e) {
            AppendLog.appendLog("---ERROR in sendNotification: " + e.getMessage());
        }

    }

    //TODO remove this methods if work for weaconParse
//    public static void sendNotification2(Weacon we) {
//        try {
//            Intent resultIntent;
//            PendingIntent resultPendingIntent;
//            TaskStackBuilder stackBuilder;
//
//            Intent intent = new Intent(NOTIFICATION_DELETED_ACTION);
//            pendingDeleteIntent = PendingIntent.getBroadcast(acti.getBaseContext(), 0, intent, 0);
//
//            //TODO put in parse that this weacon was notified
//
//            if (showedNotifications.size() == 0 || justDeleted) { //New Notification
//                justDeleted = false;
//                mIdSingle = currentId;
//                showedNotifications.add(we);
//
//                acti.registerReceiver(receiver, new IntentFilter(NOTIFICATION_DELETED_ACTION));
//
//                resultIntent = new Intent(acti.getBaseContext(), BrowserActivity.class);
//                //            resultIntent.putExtra("weacon",  we);
//                resultIntent.putExtra("wName", we.getName());
//                resultIntent.putExtra("wUrl", we.getUrl());
//                resultIntent.putExtra("wLogo", we.getLogoRounded());
//                stackBuilder = TaskStackBuilder.create(acti.getBaseContext());
//                stackBuilder.addParentStack(BrowserActivity.class);
//                stackBuilder.addNextIntent(resultIntent);
//                resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
//
//
//                NotificationCompat.Action myAction = new NotificationCompat.Action(R.drawable.ic_silence, "Turn Off", resultPendingIntent);
//                notif = new NotificationCompat.Builder(acti)
//                        .setSmallIcon(R.drawable.ic_stat_name_hn)
//                        .setLargeIcon(we.getLogoRounded())
//                        .setContentTitle(we.getName())
//                        .setContentText(we.getMessage())
//                        .setAutoCancel(true)
//                        .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND | Notification.FLAG_SHOW_LIGHTS)
//                        .setLights(0xE6D820, 300, 100)
//                        .setTicker("HearNow weacon detected\n" + we.getName())
//                        .setDeleteIntent(pendingDeleteIntent)
//                        .addAction(myAction);
//                NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
//                bigTextStyle.setBigContentTitle(we.getName());
//                bigTextStyle.bigText(we.getMessage());
//
//                notif.setStyle(bigTextStyle);
//                notif.setContentIntent(resultPendingIntent);
//
//                mNotificationManager.notify(mIdSingle, notif.build());
//
//            } else { // Update the notification already sent
//
//                mNotificationManager.cancel(mIdSingle);
//                mIdGroup = mIdSingle + 1;
//                showedNotifications.add(we);
//                Bitmap bm = BitmapFactory.decodeResource(acti.getResources(), R.mipmap.ic_launcher);
//                String msg = Integer.toString(showedNotifications.size()) + " weacons around you";
//                notif = new NotificationCompat.Builder(acti) //TODO put the hour in extended notification
//                        .setSmallIcon(R.drawable.ic_stat_name_dup)
//                        .setLargeIcon(bm)
//                        .setContentTitle(msg)
//                        .setContentText(((WeaconParse) showedNotifications.get(0)).getName() + " and others ")
//                        .setAutoCancel(true)
//                                //                    .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND | Notification.FLAG_SHOW_LIGHTS)
//                        .setLights(0x36E629, 100, 100)
//                        .setDeleteIntent(pendingDeleteIntent)
//                        .setTicker(msg);
//                NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
//                inboxStyle.setBigContentTitle(msg);
//                inboxStyle.setSummaryText("Currently " + Integer.toString(showedNotifications.size() + 5) + " weacons active");//TODO Add a meaningfull summary, probably containing the number of weacons in app
//                //            AppendLog.appendLog("15");
//                for (WeaconParse weacon : showedNotifications) {
//                    inboxStyle.addLine(((WeaconParse) weacon).getName());
//                }
//                notif.setStyle(inboxStyle);
//                //            AppendLog.appendLog("16");
//                resultIntent = new Intent(acti.getBaseContext(), WeaconListActivity.class);
//                resultIntent.putExtra("wName", we.getName());
//                resultIntent.putExtra("wUrl", we.getUrl());
//                resultIntent.putExtra("wLogo", we.getLogoRounded());
//                stackBuilder = TaskStackBuilder.create(acti.getBaseContext());
//                stackBuilder.addParentStack(WeaconListActivity.class);
//                stackBuilder.addNextIntent(resultIntent);
//                resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
//                notif.setContentIntent(resultPendingIntent);
//
//                mNotificationManager.notify(mIdGroup, notif.build());
//            }
//
////            weaconsLaunchedTable.put(we.getObjectId(), we); TODO restore if we use the method
//        } catch (Exception e) {
//            AppendLog.appendLog("---ERROR in sendNotification: " + e.getMessage());
//        }
//    }
}
