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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import util.AppendLog;
import util.Weacon;
import util.parameters;

/**
 * Created by Milenko on 17/07/2015.
 */
public abstract class Notifications {
    private static final String NOTIFICATION_DELETED_ACTION = "NOTIFICATION_DELETED";
    public static HashMap<String, Weacon> weaconsLaunchedTable; //For not relaunche the same weacon {obid, we}
    private static ArrayList<Object> showedNotifications;
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
    public static int CheckScanResults(List<ScanResult> sr) {
        int found = 0;
        for (ScanResult r : sr) {
            try {
                if (MainActivity.BSSIDSTable.containsKey(r.BSSID)) {
                    found++;
                    SPOT spot = MainActivity.BSSIDSTable.get(r.BSSID);
                    String obId = spot.getPlaceId();
                    Weacon we = MainActivity.weaconsTable.get(obId);
                    AppendLog.appendLog("->" + r.SSID + " Int= " + r.level + "->" + we, "WE");
                    if (r.level >= parameters.defaultThreshold && !weaconsLaunchedTable.containsKey(we.getObjectId())) { //levelOld < threshold &&
                        AppendLog.appendLog("***[" + we.getName() + "] [" + r.SSID + "|" + r.BSSID + "] " + spot.getGps(), "WE");
                        Notifications.sendNotification(we);
                    }
                } else {
                    //                    AppendLog.appendLog("**La ssid no se encuentra en la tabla");
                }
            } catch (Exception e) {
//                e.printStackTrace();
                AppendLog.appendLog("---Error: CheckScanResults: result = " + r.toString() + " | error = " + e.getMessage());
            }
        }
        return found;
    }

    public static void sendNotification(Weacon we) {
        try {
            Intent resultIntent;
            PendingIntent resultPendingIntent;
            TaskStackBuilder stackBuilder;

            Intent intent = new Intent(NOTIFICATION_DELETED_ACTION);
            pendingDeleteIntent = PendingIntent.getBroadcast(acti.getBaseContext(), 0, intent, 0);

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
                        .setContentText(((Weacon) showedNotifications.get(0)).getName() + " and others ")
                        .setAutoCancel(true)
                                //                    .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND | Notification.FLAG_SHOW_LIGHTS)
                        .setLights(0x36E629, 100, 100)
                        .setDeleteIntent(pendingDeleteIntent)
                        .setTicker(msg);
                NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
                inboxStyle.setBigContentTitle(msg);
                inboxStyle.setSummaryText("Currently " + Integer.toString(showedNotifications.size() + 5) + " weacons active");//TODO Add a meaningfull summary, probably containing the number of weacons in app
                //            AppendLog.appendLog("15");
                for (Object weacon : showedNotifications) {
                    inboxStyle.addLine(((Weacon) weacon).getName());
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
}
