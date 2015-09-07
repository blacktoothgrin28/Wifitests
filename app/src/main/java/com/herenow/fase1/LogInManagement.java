package com.herenow.fase1;

import com.parse.ParseException;
import com.parse.ParsePush;
import com.parse.SaveCallback;

import java.util.HashMap;
import java.util.List;

import parse.WifiSpot;
import util.AppendLog;
import util.parameters;

/**
 * Created by Milenko on 10/08/2015.
 */
public abstract class LogInManagement {
    private static HashMap<String, Integer> oldSpots = new HashMap<>();
    private static HashMap<String, Integer> newSpots;
    private static HashMap<String, Integer> loggedWeacons = new HashMap<>();

    /***
     * Monitors the scan results to check if logged in a Weacon.
     * Should receive each Scan Result
     *
     * @param spots
     */
    public static void ReportDetectedSpots(List<WifiSpot> spots) {

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

        for (WifiSpot spot : spots) {
            String b = spot.getBSSID();
            if (loggedWeacons.containsKey(b)) {
                int n = loggedWeacons.get(b);
                loggedWeacons.put(b, n + 1);
            }
        }
        for (String b : loggedWeacons.keySet()) {
            int n = loggedWeacons.get(b);
            loggedWeacons.put(b, n - 1);
            if (n - 1 == -3) LogOut(b);
        }


    }

    private static void LogOut(String bssid) {
        //TODO inform parse
        loggedWeacons.remove(bssid);
        AppendLog.appendLog("Logged out:" + bssid);
        ParsePush.unsubscribeInBackground(ChannelName(bssid));
    }

    private static void LogIn(WifiSpot spot) {
        //TODO Register in parse or is automatic to kwnow how many subscribers?
        AppendLog.appendLog("Logged in:" + spot);
        loggedWeacons.put(spot.getBSSID(), 0);

        final String channelName = ChannelName(spot.getBSSID());
        AppendLog.appendLog("channel name=" + channelName);

        //TODo ver si puedo hacer canales nuevos
        ParsePush.subscribeInBackground(channelName, new SaveCallback() {//The channel must start with a letter
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    AppendLog.appendLog("successfully subscribed to the broadcast channel." + channelName);
                } else {
                    AppendLog.appendLog("failed to subscribe for push: " + e.getMessage() + " \n" + e);
                }
            }
        });
    }

    /**
     * Convert the bssid to a string appropiate for channel name
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
