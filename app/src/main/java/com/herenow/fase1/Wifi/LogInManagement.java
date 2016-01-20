package com.herenow.fase1.Wifi;

import android.content.Context;
import android.widget.Toast;

import com.herenow.fase1.Notifications.Notifications;
import com.parse.ParseException;
import com.parse.ParsePush;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import parse.WeaconParse;
import parse.WifiSpot;
import util.myLog;
import util.parameters;
import util.stringUtils;

/**
 * Created by Milenko on 10/08/2015.
 */
public abstract class LogInManagement {
    static int repeatedOffToDisappear = 10;
    static int repeatedOffRemoveFromNotification = 5;
    static int repeatedOffToChatOff = 4;
    static int repeatedOnToChatOn = 3;
    private static HashMap<WeaconParse, Integer> contabilidad = new HashMap<>(); //{we,n}
    private static HashMap<String, Integer> oldSpots = new HashMap<>();
    private static HashMap<String, Integer> newSpots;
    private static HashMap<String, Integer> loggedWeacons = new HashMap<>();
    private static Context mContext;
    private static boolean anychange = false;
    private static ArrayList<WeaconParse> onNotification = new ArrayList<>();
    private static ArrayList<WeaconParse> onChat = new ArrayList<>();
    private static boolean sound;

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
            if (n + 1 == parameters.nHitsForLogIn) LogInOld(spot);
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
                if (n - 1 == -3) LogOutOld(b);
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

    private static void LogInOld(WifiSpot spot) {
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

    private static void LogOutOld(String bssid) {
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

    public static void setNewWeacons(HashSet<WeaconParse> weaconsDetected) {

        anychange = false; //is there any changes for send or modify notification?
        sound = false;//should the notification be silent?
        boolean someWeaconRequiresFetching = false;
        try {

            // DISAPPEARING (NOT IN NEW)
            Iterator<WeaconParse> itOld = contabilidad.keySet().iterator();
            while (itOld.hasNext()) {
                WeaconParse we = itOld.next();

                if (!weaconsDetected.contains(we)) {
                    int n = contabilidad.get(we);

                    // +++ -
                    if (n > 0) {
                        contabilidad.put(we, -1);
                        myLog.add("just leaving " + we.getName(), "LIM");

                        // --- -
                    } else {
                        contabilidad.put(we, n - 1);

                        if (n < -repeatedOffToDisappear) {
                            WeForget(we); //remove from consideration
                        } else if (n == -repeatedOffRemoveFromNotification && IsInNotification(we)) {
                            WeNotificationOut(we); //remove from notification
                        } else if (n == -repeatedOffToChatOff && IsInChat(we)) {
                            WeChatOut(we); // Log out from chat
                        }
                    }
                }
            }

            //APPEARING (YES IN NEW)
            // por cada uno de los nuevos
            //si no está o es negativo lo agregamos con un uno
            //si está, le sumamos uno
            Iterator<WeaconParse> it = weaconsDetected.iterator();
            while (it.hasNext()) {
                WeaconParse we = it.next();

                //todo
                // if (we.NotificationAlreadyFetched()) someWeaconRequiresFetching = true;

                if (contabilidad.containsKey(we)) {
                    int n = contabilidad.get(we);
                    // +++ +
                    if (n >= 0) {
                        contabilidad.put(we, n + 1);
                        if (n == repeatedOnToChatOn && !IsInChat(we)) {
                            WeChatIn(we);
                        }
                        // ++-- +
                    } else {
                        contabilidad.put(we, 1);
                        myLog.add("Entering Again: " + we.getName(), "LIM");
                    }
                } else {
                    //First time
                    WeNotificationIn(we);
                }
            }

            myLog.add("conta: " + stringUtils.Listar(contabilidad), "LIM");
            myLog.add("has changed?" + anychange, "LIM");

            //Notify or change notification
            if (anychange) {
//                ArrayList<WeaconParse> notificables = new ArrayList();
//                myLog.add("We Will notify: ", "LIM");
//
//                if (contabilidad.keySet().size() == 0) {
//                    myLog.add("Remove all notified weacons", "LIM");
//                }
//
//                for (WeaconParse we : contabilidad.keySet()) {
//                    if (contabilidad.get(we) > 0) {
//                        notificables.add(we);
//                        myLog.add("     ->" + we.getName(), "LIM");
//                    }
//                }

//                Notifications.showNotification(notificables, someWeaconRequiresFetching, sound);
                Notifications.showNotification(onNotification, someWeaconRequiresFetching, sound);
            }
            myLog.add("****************************", "LIM");

        } catch (Exception e) {
            myLog.add("----error in login management: " + e.getLocalizedMessage());
        }
    }

    public static ArrayList<WeaconParse> getActiveWeacons() {
        ArrayList arr = new ArrayList(contabilidad.keySet());
        return arr;
    }

    //CHAT
    private static boolean IsInChat(WeaconParse we) {
        return onChat.contains(we);
    }

    private static void WeChatIn(WeaconParse we) {
        //TODO LogInChat(we)
        myLog.add("Chat In: " + we.getName(), "LIM");
        onChat.add(we);
    }

    private static void WeChatOut(WeaconParse we) {
        //TODO     LogOutChat(we)
        onChat.remove(we);
        myLog.add("ChatOut " + we.getName(), "LIM");

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

    //NOTIFICATIONS
    private static boolean IsInNotification(WeaconParse we) {
        return onNotification.contains(we);
    }

    private static void WeNotificationIn(WeaconParse we) {
        contabilidad.put(we, 1);
        myLog.add("Just entering in: " + we.getName(), "LIM");
        anychange = true;//Just appeared this weacon
        sound = true;
        onNotification.add(we);

    }

    private static void WeNotificationOut(WeaconParse we) {
        onNotification.remove(we);
        myLog.add("Remove from notification:" + we.getName(), "LIM");
        anychange = true;

    }

    //Global state
    private static void WeForget(WeaconParse we) {
        contabilidad.remove(we);
        myLog.add("Forget it, too far: " + we.getName(), "LIM");

    }

    public HashMap getCurrentlyLogged() {
        return loggedWeacons;
    }


}
