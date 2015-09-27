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
import android.support.v4.app.NotificationCompat;

import com.herenow.fase1.Activities.CardsActivity;
import com.herenow.fase1.Activities.WeaconListActivity;
import com.herenow.fase1.R;

import java.util.ArrayList;
import java.util.HashMap;

import parse.WeaconParse;
import util.myLog;

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

    public static void Initialize(Activity act) {
        acti = act;
        weaconsLaunchedTable = new HashMap<>();
        showedNotifications = new ArrayList<>(); //Weacons showed in notification
        mNotificationManager = (NotificationManager) act.getSystemService(Context.NOTIFICATION_SERVICE);

    }

    public static boolean shouldBeLaunched(WeaconParse we) {
        //TODO decide from online info
        boolean b = !weaconsLaunchedTable.containsKey(we.getObjectId());
        return b;
    }

    public static void sendNotification(WeaconParse we) {

        try {
            Intent intent = new Intent(NOTIFICATION_DELETED_ACTION);
            pendingDeleteIntent = PendingIntent.getBroadcast(acti.getBaseContext(), 0, intent, 0);

            //TODO put in parse that this weacon was notified
            if (showedNotifications.size() == 0) {
                sendNewNotification(we);
            } else {
                updateNotification(we);
            }

            weaconsLaunchedTable.put(we.getObjectId(), we);
        } catch (Exception e) {
            myLog.add("---ERROR in sendNotification: " + e.getMessage());
        }

    }

    private static void sendNewNotification(WeaconParse we) {

        Intent resultIntent;
        TaskStackBuilder stackBuilder;
        PendingIntent resultPendingIntent;

        mIdSingle = currentId;
        showedNotifications.add(we);

        acti.registerReceiver(receiverDeleteNotification, new IntentFilter(NOTIFICATION_DELETED_ACTION));

        resultIntent = new Intent(acti.getBaseContext(), CardsActivity.class);
//        resultIntent.putExtra("weacon", we);
        resultIntent.putExtra("wName", we.getName());
        resultIntent.putExtra("wUrl", we.getUrl());
        resultIntent.putExtra("wLogo", we.getLogoRounded());
        resultIntent.putExtra("wComapanyDataObId", we.getCompanyDataObjectId());

        stackBuilder = TaskStackBuilder.create(acti.getBaseContext());
        stackBuilder.addParentStack(CardsActivity.class);
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
    }

    private static void updateNotification(WeaconParse we) {

        Intent resultIntent;
        TaskStackBuilder stackBuilder;
        PendingIntent resultPendingIntent;

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

        mNotificationManager.notify(mIdGroup, notif.build());
    }

    private final static BroadcastReceiver receiverDeleteNotification = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                myLog.add("<<<<<<<<<<<<Has borrado una notif>>>>");
                currentId = currentId + 2;
                showedNotifications = new ArrayList<>();

            } catch (Exception e) {
                myLog.add("---ERROR<<<<<<<<<<<<Has borrado una notif>>>>" + e.getMessage());
            }
            context.unregisterReceiver(this);
        }
    };

}
