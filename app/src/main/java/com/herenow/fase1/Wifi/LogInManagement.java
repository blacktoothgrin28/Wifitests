package com.herenow.fase1.Wifi;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.herenow.fase1.LineTime;
import com.herenow.fase1.Notifications.Notifications;
import com.parse.ParseException;
import com.parse.ParsePush;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
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
    private static HashSet<WeaconParse> lastWeaconsDetected;
    private static ArrayList<WeaconParse> onNotification = new ArrayList<>();//Will be notified
    private static HashMap<WeaconParse, Integer> contabilidad = new HashMap<>(); //{we,n=appeared in a row}

    private static boolean anychange = false;  //is there any changes for send or modify notification?
    private static boolean sound;//should the notification be silent?
    private static ArrayList<WeaconParse> onChat = new ArrayList<>();
    private static boolean anyFetchable;
    private static Context mContext;
    //old stuff
    private static HashMap<String, Integer> oldSpots = new HashMap<>();
    private static HashMap<String, Integer> newSpots;
    private static HashMap<String, Integer> loggedWeacons = new HashMap<>();

    /**
     * Informs the weacons detected, in order to send/update/remove  notification
     * and log in /out in the chat
     *
     * @param weaconsDetected
     */
    public static void setNewWeacons(HashSet<WeaconParse> weaconsDetected) {

        lastWeaconsDetected = weaconsDetected;
        anychange = false;
        sound = false;
        anyFetchable = anyFetchable(weaconsDetected);

        myLog.add("******************", "fetch");

        try {
            //Check differences with last scanning and keep accumulation history
            checkDisappearing();
            checkAppearing();

            myLog.add("conta: " + stringUtils.Listar(contabilidad), "LIM");
            myLog.add("has changed?" + anychange, "LIM");

            //Notify or change notification
            if (anychange || (anyFetchable && shouldFetch(weaconsDetected))) {
                myLog.add("Will Notify: " + stringUtils.Listar(onNotification), "LIM");
                myLog.add("**Se requiere fetch:" + anyFetchable, "fetch");

                if (!anyFetchable) {
                    Notifications.showNotification(onNotification, sound, anyFetchable);
                } else {
                    MultiTaskCompleted listener = new MultiTaskCompleted() {
                        int i = 0;
                        ArrayList<WeaconParse> updatedWeacons = new ArrayList<>();

                        @Override
                        public void OneTaskCompleted(WeaconParse updatedWeacon) {
                            i += 1;
                            updatedWeacons.add(updatedWeacon);
                            if (updatedWeacons.size() == onNotification.size()) {
//                                // Experimental: sorting updated weacons
//                                ArrayList sortedUpdated = new ArrayList();
//                                for (WeaconParse weBuff : onNotification) {
//                                    sortedUpdated.add(updatedWeacons.get(updatedWeacons.indexOf(weBuff)));
//                                }
                                Notifications.showNotification(updatedWeacons, sound, anyFetchable);
                            }
                        }

                        @Override
                        public void OnError(Exception e) {
                            myLog.add("---err " + e.getLocalizedMessage(), "fetch");
                        }

                    };

                    for (final WeaconParse we : onNotification) {
                        (new FetchWeacon(listener, we)).execute(we.getParadaId());
                    }
                }
            }

            Notifications.notifyContabilidad(stringUtils.Listar(contabilidad));
            myLog.add("****************************", "LIM");

        } catch (Exception e) {
            myLog.add("----error in login management: " + e);
            myLog.add("     ---details: \n" + Log.getStackTraceString(e));
        }
    }

    private static boolean shouldFetch(HashSet<WeaconParse> weacons) {
        boolean res = false;

        Iterator<WeaconParse> it = weacons.iterator();
        while (it.hasNext() && !res) {
            WeaconParse we = it.next();
            if (we.NotificationRequiresFetching() && contabilidad.get(we) < 3) {//avoid keep fetching if you live near a bus stop
                res = true;
                myLog.add(we.getName() + " requires feticn. this is the " + contabilidad.get(we) + "time", "fetch");
            }
        }
        return res;
    }

    private static boolean anyFetchable(HashSet<WeaconParse> weacons) {
        boolean res = false;

        Iterator<WeaconParse> it = weacons.iterator();
        while (it.hasNext() && !res) {
            WeaconParse we = it.next();
            if (we.NotificationRequiresFetching()) {
                res = true;
                myLog.add(we.getName() + " requires fetching", "fetch");
            }
        }
        return res;
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

    //OLD

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

    public HashMap getCurrentlyLogged() {
        return loggedWeacons;
    }

    private static class FetchWeacon extends AsyncTask<String, Void, ArrayList> {
        private final MultiTaskCompleted multiTaskCompleted;
        WeaconParse mWe;

        public FetchWeacon(MultiTaskCompleted listener, WeaconParse we) {
            super();
            multiTaskCompleted = listener;
            mWe = we;
        }

        @Override
        protected ArrayList doInBackground(String... paradasIds) {
            Connection.Response response = null;
            String paradaId = paradasIds[0];
            String q2 = "http://www.santqbus.santcugat.cat/consultatr.php?idparada=" + paradaId + "&idliniasae=-1&codlinea=-1";

            try {
                myLog.add("starting fetching: " + mWe.getName(), "fetch");

                response = Jsoup.connect(q2)
                        .ignoreContentType(true)
                        .referrer("http://www.google.com")
                        .timeout(5000)
                        .followRedirects(true)
                        .execute();
            } catch (IOException e) {
                multiTaskCompleted.OnError(e);
            }

            if (response == null) return null;

            String s = response.body();
            String[] partes = s.split("\\}\\,\\{|\\[\\{|\\}\\]");

            ArrayList lineTimes = new ArrayList();

            for (String parte : partes) {
                if (parte.length() > 3) {
                    LineTime lineTime = null;
                    try {
                        lineTime = new LineTime(new JSONObject("{" + parte + "}"));
                    } catch (JSONException e) {
                        multiTaskCompleted.OnError(e);
                    }
                    lineTimes.add(lineTime);
                    myLog.add(lineTime.toString());
                }
            }
            myLog.add("updated todas las lineas de la parada:" + paradaId, "fetch");

            return lineTimes;
        }

        @Override
        protected void onPostExecute(ArrayList elements) {
            myLog.add("post exe INI: " + mWe.getName(), "fetch");
//            workCounter.taskFinished();
            super.onPostExecute(elements);
            mWe.setFetchingResults(elements);
            myLog.add("post exe FIN: " + mWe.getName(), "fetch");
            multiTaskCompleted.OneTaskCompleted(mWe);
        }
    }
}
