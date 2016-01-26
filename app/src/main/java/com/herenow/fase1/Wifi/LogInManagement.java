package com.herenow.fase1.Wifi;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.herenow.fase1.Notifications.Notifications;
import com.herenow.fase1.fetchers.fetchParadaStCugat;
import com.parse.ParseException;
import com.parse.ParsePush;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import parse.WeaconParse;
import parse.WifiSpot;
import util.MultiTaskCompleted;
import util.myLog;
import util.parameters;
import util.stringUtils;

/**
 * Created by Milenko on 10/08/2015.
 */
public abstract class LogInManagement {
    public static HashSet<WeaconParse> lastWeaconsDetected;
    private static ArrayList<WeaconParse> weaconsToNotify = new ArrayList<>();//Will be notified
    private static HashMap<WeaconParse, Integer> contabilidad = new HashMap<>(); //{we,n=appeared in a row}

    private static boolean anyChange = false;  //is there any changes for send or modify notification?
    private static boolean sound;//should the notification be silent?
    private static ArrayList<WeaconParse> onChat = new ArrayList<>();
    private static boolean anyFetchable;
    private static Context mContext;
    private static boolean lastTimeWeFetched;
    private static int nFetchings;

    //old stuff
//    private static HashMap<String, Integer> oldSpots = new HashMap<>();
//    private static HashMap<String, Integer> newSpots;
//    private static HashMap<String, Integer> loggedWeacons = new HashMap<>();

    /**
     * Informs the weacons detected, in order to send/update/remove  notification
     * and log in /out in the chat
     *
     * @param weaconsDetected
     */
    public static void setNewWeacons(HashSet<WeaconParse> weaconsDetected) {

        lastWeaconsDetected = weaconsDetected;
        anyChange = false;
        sound = false;
        anyFetchable = anyFetchable(weaconsDetected);

        myLog.add("******************", "fetch");

        try {
            //Check differences with last scanning and keep accumulation history
            checkDisappearing();
            checkAppearing();

            boolean shouldFetch = shouldFetch(weaconsDetected);

            myLog.add("conta: " + stringUtils.Listar(contabilidad), "LIM");
            myLog.add("anyChange?" + anyChange + "| anyfetchable?" + anyFetchable + "| should fetch?" + shouldFetch, "LIM");

            //Notify or change notification
            if (anyChange || (anyFetchable && shouldFetch)) {
                Notify();

                // para que cuando deje de fetchear, al siguiente tick mande notificación borrando los tiempos obsolets
                // esta ntificación es igual, pero quita los tiempos
            } else if (!anyChange && anyFetchable && !shouldFetch && lastTimeWeFetched) {
                myLog.add("Notifying WO fetching", "LIM");
                NotifyWOFetching();
            }

            Notifications.notifyContabilidad(stringUtils.Listar(contabilidad));
            myLog.add("****************************\n", "LIM");

        } catch (Exception e) {
            myLog.add("----error in login management: " + e);
            myLog.add("     ---details: \n" + Log.getStackTraceString(e));
        }
    }

    private static void NotifyWOFetching() {
        //removing last info
        myLog.add("Removing info of paradas (last feching) from everyweacon", "LIM");
        for (WeaconParse we : weaconsToNotify) {
            we.resetFetchingResults();
        }

        Notifications.showNotification(weaconsToNotify, false, true);
        lastTimeWeFetched = false;
    }

    private static void Notify() {
        myLog.add("Will Notify: " + stringUtils.Listar(weaconsToNotify), "LIM");
        myLog.add("**Vamos a notificar. Se requiere fetch:" + anyFetchable, "fetch");

        if (!anyFetchable) {
            Notifications.showNotification(weaconsToNotify, sound, anyFetchable);
            lastTimeWeFetched = false;
        } else {
            MultiTaskCompleted listener = new MultiTaskCompleted() {
                int i = 0;

                @Override
                public void OneTaskCompleted() {
                    i += 1;
                    myLog.add("terminada ina task=" + 1 + "/" + nFetchings);

                    if (i == nFetchings) {
                        myLog.add("a lazanr la notificaicno congunta");
                        Notifications.showNotification(weaconsToNotify, sound, anyFetchable);
                        lastTimeWeFetched = true;
                    }
                }

                @Override
                public void OnError(Exception e) {
                    myLog.add("---err " + e.getLocalizedMessage(), "fetch");
                }
            };

            for (final WeaconParse we : weaconsToNotify) {
                if (we.notificationRequiresFetching())
                    (new fetchParadaStCugat(listener, we)).execute();
            }
        }
    }

    /**
     * Indicates if should fetch. The criteria is "if has been active by more than n scanners, then no.
     *
     * @param weacons
     * @return
     */
    private static boolean shouldFetch(HashSet<WeaconParse> weacons) {
        boolean res = false;
        int repetitionsTurnOffFetching = 3;

        Iterator<WeaconParse> it = weacons.iterator();
        while (it.hasNext() && !res) {
            WeaconParse we = it.next();
            if (we.notificationRequiresFetching() && contabilidad.get(we) < repetitionsTurnOffFetching) {//avoid keep fetching if you live near a bus stop
                res = true;
                myLog.add(we.getName() + " requires feticn. this is the " + contabilidad.get(we) + "time", "fetch");
            }
        }
        return res;
    }

    private static boolean anyFetchable(HashSet<WeaconParse> weacons) {
        nFetchings = nFetchings(weacons);
        return nFetchings > 0;
    }

    private static int nFetchings(HashSet<WeaconParse> weacons) {
        int i = 0;
        for (WeaconParse we :
                weacons) {
            if (we.notificationRequiresFetching()) i++;
        }
        return i;
    }

    /**
     * APPEARING (YES IN NEW)
     * por cada uno de los nuevos
     * si no está o es negativo lo agregamos con un uno
     * si está, le sumamos uno
     */
    private static void checkAppearing() {

        Iterator<WeaconParse> it = lastWeaconsDetected.iterator();
        while (it.hasNext()) {
            WeaconParse we = it.next();

            if (contabilidad.containsKey(we)) {
                int n = contabilidad.get(we);
                // +++ +
                if (n >= 0) {
                    contabilidad.put(we, n + 1);
                    if (n == parameters.repeatedOnToChatOn && !IsInChat(we)) {
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
    }

    /**
     * DISAPPEARING (NOT IN NEW)
     */
    private static void checkDisappearing() {
        Iterator<Map.Entry<WeaconParse, Integer>> itOld = contabilidad.entrySet().iterator();

        while (itOld.hasNext()) {
            Map.Entry<WeaconParse, Integer> entry = itOld.next();

            WeaconParse we = entry.getKey();
            if (!lastWeaconsDetected.contains(we)) {
                int n = entry.getValue();

                // +++ -
                if (n > 0) {
                    entry.setValue(-1);
                    myLog.add("just leaving " + we.getName(), "LIM");

                    // --- -
                } else {
                    entry.setValue(n - 1);

                    if (n < -parameters.repeatedOffToDisappear) {
                        myLog.add("Forget it, too far: " + we.getName(), "LIM");
                        itOld.remove();
                    } else if (n == -we.getRepeatedOffRemoveFromNotification() && IsInNotification(we)) {
                        WeNotificationOut(we); //remove from notification
                    } else if (n == -parameters.repeatedOffToChatOff && IsInChat(we)) {
                        WeChatOut(we); // Log out from chat
                    }
                }
            }
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

    private static void LogInOld(WifiSpot spot) {
        //TODO Register in parse or is automatic to kwnow how many subscribers?
//        loggedWeacons.put(spot.getBSSID(), 0);

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
//            loggedWeacons.remove(bssid);
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
        return weaconsToNotify.contains(we);
    }

    private static void WeNotificationIn(WeaconParse we) {
        contabilidad.put(we, 1);
        myLog.add("Just entering in: " + we.getName(), "LIM");
        anyChange = true;//Just appeared this weacon
        sound = true;
        weaconsToNotify.add(we);
    }

    private static void WeNotificationOut(WeaconParse we) {
        weaconsToNotify.remove(we);
        myLog.add("Remove from notification:" + we.getName(), "LIM");
        anyChange = true;

    }

    public static void refresh() {
        myLog.add("refresing the notification");
        Notify();
    }

//    public HashMap getCurrentlyLogged() {
//        return loggedWeacons;
//    }

}