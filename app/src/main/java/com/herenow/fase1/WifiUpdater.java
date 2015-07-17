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
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import util.AppendLog;
import util.Weacon;

/**
 * Created by Milenko on 27/05/2015.
 */
public class WifiUpdater implements Runnable {
    //    private static HashMap<String, Weacon> weaconsLaunchedTable;
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
    private int ntimes = 5;//numbers of ticks for launching a fake weacon

    public WifiUpdater(TextView tv, Activity activity, Boolean demo) {
        this.textView = tv;
        this.act = activity;
        this.demo = demo;
        MainActivity.weaconsLaunchedTable = new HashMap<>();
        showedNotifications = new ArrayList<>(); //Weacons showed in notification
        wifi = (WifiManager) activity.getSystemService(Context.WIFI_SERVICE);
//        this.act = activity;
        cont = 1;
        demoCount = 1;
        mNotificationManager = (NotificationManager) act.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    private static void sendNotification(Activity act, Weacon we) {
        Intent resultIntent;
        PendingIntent resultPendingIntent;
        TaskStackBuilder stackBuilder;


        if (showedNotifications.size() == 0) { //New Notification
            mId = 1;
            showedNotifications.add(we);

            resultIntent = new Intent(act.getBaseContext(), BrowserActivity.class);
//            resultIntent.putExtra("weacon",  we);
            resultIntent.putExtra("wName", we.getName());
            resultIntent.putExtra("wUrl", we.getUrl());
            resultIntent.putExtra("wLogo", we.getLogoRounded());
            stackBuilder = TaskStackBuilder.create(act.getBaseContext());
            stackBuilder.addParentStack(BrowserActivity.class);
            stackBuilder.addNextIntent(resultIntent);
            resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);


            NotificationCompat.Action myAction = new NotificationCompat.Action(R.drawable.ic_silence, "Turn Off", resultPendingIntent);
            mBuilder = new NotificationCompat.Builder(act)
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

            resultIntent = new Intent(act.getBaseContext(), WeaconListActivity.class);
            resultIntent.putExtra("wName", we.getName());
            resultIntent.putExtra("wUrl", we.getUrl());
            resultIntent.putExtra("wLogo", we.getLogoRounded());
            stackBuilder = TaskStackBuilder.create(act.getBaseContext());
            stackBuilder.addParentStack(WeaconListActivity.class);
            stackBuilder.addNextIntent(resultIntent);
            resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            mBuilder.setContentIntent(resultPendingIntent);
        }

        mNotificationManager.notify(mId, mBuilder.build());
        MainActivity.weaconsLaunchedTable.put(we.getObjectId(), we);
    }

    @Override
    public void run() {
        Log.d("mhp", "Wifiuploader | run. demo =" + demo.toString());
        if (demo) {
            if (demoCount == 1 || demoCount % ntimes == 0) {
                findFakeWeacon();
            }
            demoCount++;
        } else {
            wifi.startScan();
            AppendLog.appendLog("Realizando scanner");
            List<ScanResult> sr = wifi.getScanResults();
            List<String> list = new ArrayList<String>();

            AppendLog.appendLog("se obtienen resultados:"+sr.size());
            for (ScanResult r : sr) {
//            if (r.SSID.equals("piripiri")) {
//                String intensidad = Integer.toString(r.level);
//                textView.setText(Integer.toString(cont) + "piripiri int=" + intensidad);
//                if (r.level > -75 && levelOld < -75) {
//                    sendNotification(act);
//                }
//                levelOld = r.level;
//                break;
//                Log.d("mhp", "Wifiuploader |run. scanning ssid's = " + r.toString());
                AppendLog.appendLog("detectada la red "+r.SSID);
                if (MainActivity.SSIDSTable.containsKey(r.SSID)) {//TODO manage the launched && !MainActivity.weaconsLaunchedTable.containsKey(r.SSID)) {
                    String obId = MainActivity.SSIDSTable.get(r.SSID);
                    Weacon we = MainActivity.weaconsTable.get(obId);
//                    Log.d("mhp", "Wifiuploader |run. entrado en doble if = " + r.SSID + "old y new |umbral: " + Integer.toString(levelOld) + " | " + Integer.toString(r.level) +
//                           " |"+ Integer.toString(we.getLevel()));
                    int threshold = we.getLevel();
                    if (levelOld < threshold && r.level >= threshold) {
                        sendNotification(act, we);
                    }
                    levelOld = r.level;
                }else {
                    AppendLog.appendLog("**La ssid no se encuentra en la tabla");
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
            int randomInt = generator.nextInt(values.length);

            Log.d("mhp", "WifiUpdater |findFakeWeacon. i= " + randomInt);
            we = (Weacon) values[randomInt];
        } while (MainActivity.weaconsLaunchedTable.containsKey(we.getObjectId()));

        Log.d("mhp", "WifiUpdater |findFakeWeacon. lauchin= " + we.getName());
        sendNotification(act, we);
    }
}
