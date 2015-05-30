package com.herenow.fase1;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.support.v4.app.NotificationCompat;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import util.Weacon;

/**
 * Created by Milenko on 27/05/2015.
 */
public class WifiUpdater implements Runnable {
    WifiManager wifi;
    TextView textView;
    Activity act;
    private int cont;
    private int levelOld = -100;

    public WifiUpdater(TextView tv, Activity activity) {
        this.textView = tv;
        this.act = activity;
        wifi = (WifiManager) activity.getSystemService(Context.WIFI_SERVICE);
//        this.act = activity;
        cont = 1;
    }

    private static void sendNotification(Activity act, Weacon we) {
        PendingIntent resultPendingIntent = null;
        Intent resultIntent = new Intent(act, SecondActivity.class);

        //add parameters to pass to the activity
        resultIntent.putExtra("wName", we.getName());
        resultIntent.putExtra("wUrl", we.getUrl());
        resultIntent.putExtra("wLogo", we.getLogo());
        //        resultIntent.putExtra("wMessage", wMessage);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(act);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

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

        NotificationCompat.Action myaction = new NotificationCompat.Action(R.drawable.ic_silence, "Turn Off", resultPendingIntent);//TODO Turn off this notifification
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(act)
                .setSmallIcon(R.drawable.ic_stat_name)//TODO put HN logo
                .setLargeIcon(we.getLogo())
                .setContentTitle(we.getName())
                .setContentText(we.getMessage())
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND | Notification.FLAG_SHOW_LIGHTS)
                .setLights(0xE6D820, 300, 100)
                .setTicker("HearNow weacon detected\n" + we.getName())
                .addAction(myaction);
        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        // Sets a title for the Inbox in expanded layout
        bigTextStyle.setBigContentTitle(we.getName());
        bigTextStyle.bigText(we.getMessage());

        // Moves the expanded layout object into the notification object.
        mBuilder.setStyle(bigTextStyle);

        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) act.getSystemService(Context.NOTIFICATION_SERVICE);
        int mId = 1;
        mNotificationManager.notify(mId, mBuilder.build());
    }

    @Override
    public void run() {
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
            if (MainActivity.weaconsTable.containsKey(r.SSID)) {
                Weacon we = MainActivity.weaconsTable.get(r.SSID);
                int umbral = we.getLevel();
                if (levelOld < umbral && r.level > umbral) {
                    sendNotification(act, we);
                }
                levelOld = r.level;
            }
        }
        cont++;
    }
}
