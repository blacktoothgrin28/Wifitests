package com.herenow.fase1;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.support.v4.app.NotificationCompat;
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
        Notifications.weaconsLaunchedTable = new HashMap<>();
        showedNotifications = new ArrayList<>(); //Weacons showed in notification
        wifi = (WifiManager) activity.getSystemService(Context.WIFI_SERVICE);
        cont = 1;
        demoCount = 1;
        mNotificationManager = (NotificationManager) act.getSystemService(Context.NOTIFICATION_SERVICE);
    }


    @Override
    public void run() {
        AppendLog.appendLog("Wifiuploader | run. demo =" + demo.toString());
        if (demo) {
            if (demoCount == 1 || demoCount % ntimes == 0) {
                findFakeWeacon();
            }
            demoCount++;
        } else {
//            wifi.startScan();
////            AppendLog.appendLog("Realizando scanner");
            List<ScanResult> sr = wifi.getScanResults();
            Notifications.CheckScanResults(sr);
//            cont++;
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

            AppendLog.appendLog("WifiUpdater |findFakeWeacon. i= " + randomInt);
            we = (Weacon) values[randomInt];
        } while (Notifications.weaconsLaunchedTable.containsKey(we.getObjectId()));

        AppendLog.appendLog("WifiUpdater |findFakeWeacon. lauchin= " + we.getName());
        Notifications.sendNotification(we);
    }
}
