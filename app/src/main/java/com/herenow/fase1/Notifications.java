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
import android.support.v4.app.NotificationCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import util.AppendLog;
import util.Weacon;

/**
 * Created by Milenko on 17/07/2015.
 */
public abstract class Notifications {
    public static HashMap<String, Weacon> weaconsLaunchedTable;
    private static ArrayList<Object> showedNotifications;
    private static NotificationManager mNotificationManager;
    private static Activity acti;
    private static int mId;
    private static NotificationCompat.Builder mBuilder;

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
            if (MainActivity.SSIDSTable.containsKey(r.SSID)) {
                found++;
                SSID ssid = MainActivity.SSIDSTable.get(r.SSID);
                String obId = ssid.getPlaceId();
                Weacon we = MainActivity.weaconsTable.get(obId);
                int threshold = -100; // TODO to decide if implement the level for detection: ssid.getLevel();
                AppendLog.appendLog("****BingoGood!!: " + r.SSID + " Intensity=" + r.level + " threshold= " + threshold);
                if (r.level >= threshold && !weaconsLaunchedTable.containsKey(r.SSID)) { //levelOld < threshold &&
                    AppendLog.appendLog("we are trying to send the notif for: " + we.getName());
                    Notifications.sendNotification(we);
                }
//                    levelOld = r.level;
            } else {
                //                    AppendLog.appendLog("**La ssid no se encuentra en la tabla");
            }
        }
        return found;
    }

    public static void sendNotification(Weacon we) {
        Intent resultIntent;
        PendingIntent resultPendingIntent;
        TaskStackBuilder stackBuilder;


        if (showedNotifications.size() == 0) { //New Notification
            mId = 1;
            showedNotifications.add(we);

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
            mBuilder = new NotificationCompat.Builder(acti)
                    .setSmallIcon(R.drawable.ic_stat_name_hn)
                    .setLargeIcon(we.getLogoRounded())
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

            mBuilder.setStyle(bigTextStyle);
            mBuilder.setContentIntent(resultPendingIntent);

        } else { // Update the notification already sent

            mNotificationManager.cancel(mId);
            mId = 2;
            showedNotifications.add(we);
            Bitmap bm = BitmapFactory.decodeResource(acti.getResources(), R.mipmap.ic_launcher);
            String msg = Integer.toString(showedNotifications.size()) + " weacons around you";
            mBuilder = new NotificationCompat.Builder(acti) //TODO put the hour in extended notification
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
//            AppendLog.appendLog("15");
            for (Object weacon : showedNotifications) {
                inboxStyle.addLine(((Weacon) weacon).getName());
            }
            mBuilder.setStyle(inboxStyle);
//            AppendLog.appendLog("16");
            resultIntent = new Intent(acti.getBaseContext(), WeaconListActivity.class);
            resultIntent.putExtra("wName", we.getName());
            resultIntent.putExtra("wUrl", we.getUrl());
            resultIntent.putExtra("wLogo", we.getLogoRounded());
            stackBuilder = TaskStackBuilder.create(acti.getBaseContext());
            stackBuilder.addParentStack(WeaconListActivity.class);
            stackBuilder.addNextIntent(resultIntent);
            resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);
        }
        mNotificationManager.notify(mId, mBuilder.build());
        weaconsLaunchedTable.put(we.getObjectId(), we);
    }

}
