package com.herenow.fase1;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.support.v4.app.NotificationCompat;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.HashMap;

import parse.WeaconParse;
import util.AppendLog;
import util.parameters;

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
        }
    }

    /**
     * Launch a fake weacon, randomly from LOCAL PARSE
     */
    private void findFakeWeacon() {

        try {
            ParseQuery<WeaconParse> query = ParseQuery.getQuery(WeaconParse.class);
            query.whereNotContainedIn("ObjectId", Notifications.weaconsLaunchedTable.keySet());
            query.fromPin(parameters.pinWeacons);
            query.getFirstInBackground(new GetCallback<WeaconParse>() {
                @Override
                public void done(WeaconParse we, ParseException e) {
                    if (e == null) {
                        AppendLog.appendLog("WifiUpdater | findFakeWeacon. lauching = " + we);
                        Notifications.sendNotification(we);
                    } else {
                        AppendLog.appendLog("---Error in fakeweacon: " + e.getMessage());
                    }
                }
            });
        } catch (Exception e) {
            AppendLog.appendLog("---Error en fake weacon2: " + e.getMessage());
        }


    }
}
