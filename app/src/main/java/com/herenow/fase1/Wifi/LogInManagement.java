package com.herenow.fase1.Wifi;

import android.content.Context;
import android.widget.Toast;

import com.herenow.fase1.Activities.MainActivity;
import com.parse.ParseException;
import com.parse.ParsePush;
import com.parse.SaveCallback;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import parse.WifiSpot;
import util.myLog;
import util.parameters;

/**
 * Created by Milenko on 10/08/2015.
 */
public abstract class LogInManagement {
    private static HashMap<String, Integer> oldSpots = new HashMap<>();
    private static HashMap<String, Integer> newSpots;
    private static HashMap<String, Integer> loggedWeacons = new HashMap<>();
    private static Context mContext;


    /***
     * Monitors the scan results to check if logged in a Weacon.
     * Should receive each Scan Result
     *
     * @param spots
     */
    public static void ReportDetectedSpots(List<WifiSpot> spots, Context context) {
        mContext = context;
        //1. Check for new login
        newSpots = new HashMap<>();
        for (WifiSpot spot : spots) {
            int n = 0;
            String b = spot.getBSSID();

            if (oldSpots.containsKey(b)) {
                n = oldSpots.get(b);
            }
            newSpots.put(b, n + 1);
            if (n + 1 == parameters.nHitsForLogIn) LogIn(spot);
        }
        oldSpots = newSpots;

        //2. Check for logout
        //los logged que estan en lista, quedan en cero
        //los que no, se les resta uno

        try {
            for (WifiSpot spot : spots) {
                String b = spot.getBSSID();
                if (loggedWeacons.containsKey(b)) {
                    int n = loggedWeacons.get(b);
                    loggedWeacons.put(b, n + 1);
                }
            }

            Iterator<String> it = loggedWeacons.keySet().iterator();
            while (it.hasNext()) {
                String b = it.next();
                int n = loggedWeacons.get(b);
                loggedWeacons.put(b, n - 1);
                if (n - 1 == -3) LogOut(b);
            }

//            for (String b : loggedWeacons.keySet()) {
//                //BUG concurrent modification
//                int n = loggedWeacons.get(b);
//                loggedWeacons.put(b, n - 1);
//                if (n - 1 == -3) LogOut(b);
//            }
        } catch (Exception e) {
            myLog.add("---error en logout: " + e.getLocalizedMessage());
        }
    }

    private static void LogIn(WifiSpot spot) {
        //TODO Register in parse or is automatic to kwnow how many subscribers?
        loggedWeacons.put(spot.getBSSID(), 0);

        final String channelName = ChannelName(spot.getBSSID());
        String msg = "Logged in:" + spot + "\n" + "channel name=" + channelName;
        myLog.add(msg);

        //Bug: al parecer cuando está cerrada la  app, este context da problemas
        //mcontxt viene de parseactions, que viene de main cuando hace el "getspots"

        try {
            Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            myLog.add("-- error al mostar toast: " + e.getLocalizedMessage());
        }

        subscribeAChannel(channelName);
    }

    public static void subscribeAChannel(final String channelName, final Context context) {

        ParsePush.subscribeInBackground(channelName, new SaveCallback() {//The channel must start with a letter
            @Override
            public void done(ParseException e) {
                String text;
                if (e == null) {
                    text = "successfully subscribed to the broadcast channel." + channelName;
                } else {
                    text = "failed to subscribe for push: " + e.getMessage() + " \n" + e;
                }
                myLog.add(text);
                try {
                    Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
//                    MainActivity.writeOnScreen("Substribed to channel:" + channelName);
                } catch (Exception e1) {
                    myLog.add("---error subscribinb cchane: " + e1.getLocalizedMessage());
                }
            }
        });

    }

    private static void subscribeAChannel(final String channelName) {
        subscribeAChannel(channelName, mContext);
    }

    private static void LogOut(String bssid) {
        //TODO inform parse

        try {
            loggedWeacons.remove(bssid);
            myLog.add("Logged out:" + bssid);
            ParsePush.unsubscribeInBackground(ChannelName(bssid));
        } catch (Exception e) {
            myLog.add("---Error loguin out del chat");
        }
    }

    /**
     * Convert the bssid to a string appropiate for channel name
     *
     * @param bs
     * @return
     */
    private static String ChannelName(String bs) {
        return "Z_" + bs.replace(":", "_");
    }

    public HashMap getCurrentlyLogged() {
        return loggedWeacons;
    }
}
