package com.herenow.fase1.Wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import com.herenow.fase1.Activities.MainActivity;
import com.herenow.fase1.Notifications.Notifications;
import com.herenow.fase1.Position;
import com.herenow.fase1.Sapo.SAPO2;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import parse.WeaconParse;
import parse.WifiSpot;
import util.myLog;
import util.parameters;

/**
 * Created by Milenko on 24/09/2015.
 */
public class WifiBoss {
    private static int iScan = 0;
    private final WifiManager mainWifi;
    private final WifiReceiver receiverWifi;

    public WifiBoss(Context context) {
        mainWifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        receiverWifi = new WifiReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        context.registerReceiver(receiverWifi, intentFilter);
    }

    /**
     * From the list of ScanResults, it looks if SSIDS are present in the table of weacons,
     * and launch the notifications
     *
     * @param sr scanResults
     * @return number of matches
     */
    public static void CheckScanResults(final List<ScanResult> sr) {
        iScan++;
        if (iScan % 8 == 0) {
            ReportLocalPlaces();
        }

        ArrayList<String> bssids = new ArrayList<>();
        ArrayList<String> ssids = new ArrayList<>();
        StringBuilder sb = new StringBuilder("+++++++ Scan results:+" + "\n");

        for (ScanResult r : sr) {
            bssids.add(r.BSSID);
            ssids.add(r.SSID);
            sb.append("  '" + r.SSID + "' | " + r.BSSID + " | l= " + r.level + "\n");
        }
        sb.append("+++++++++");
        myLog.add(sb.toString(), "WE");

        //Query BSSID
        ParseQuery<WifiSpot> qb = ParseQuery.getQuery(WifiSpot.class);
        qb.whereContainedIn("bssid", bssids);
        //Query SSID
        ParseQuery<WifiSpot> qs = ParseQuery.getQuery(WifiSpot.class);
        qs.whereContainedIn("ssid", ssids);
        qs.whereEqualTo("relevant", true);
        //Main Query
        List<ParseQuery<WifiSpot>> queries = new ArrayList<>();
        queries.add(qb);
        queries.add(qs);

        ParseQuery<WifiSpot> mainQuery = ParseQuery.or(queries);
        mainQuery.fromPin(parameters.pinWeacons);
        mainQuery.include("associated_place");

        mainQuery.findInBackground(new FindCallback<WifiSpot>() {

            @Override
            public void done(List<WifiSpot> spots, ParseException e) {
                if (e == null) {
                    int n = spots.size();
                    LogInManagement.ReportDetectedSpots(spots);
                    if (n == 0) {
                        myLog.add("MegaQuery no match", "WE");
                    } else { //There are matches
                        myLog.add("From megaquery we have several matches: " + n, "WE");
                        MainActivity.reportScanning(n, sr.size());

                        StringBuilder sb = new StringBuilder("***********\n");
                        for (WifiSpot spot : spots) {
                            sb.append(spot.toString() + "\n");
                            registerHitSSID(spot);
                            WeaconParse we = spot.getWeacon();

                            //TODO  Log in y log out
                            //send a Notification for each one if has
                            if (Notifications.shouldBeLaunched(we)) {
                                Notifications.sendNotification(we);
                            }
                        }
                        sb.append("**********");
                        myLog.add(sb.toString(), "WE");
                    }
                } else {
                    myLog.add("---Error en megaquery Checkresults", "WE");
                }
            }
        });

    }

    /***
     * save in Parse that the spot has benn hit
     *
     * @param spot
     */
    private static void registerHitSSID(final WifiSpot spot) {
        // Check if already upladed
        myLog.add(spot.toString(), "HIT");
        try {
            ParseQuery query = ParseQuery.getQuery("w_hit");
            query.whereEqualTo("user", ParseUser.getCurrentUser());
            query.whereEqualTo("ssid", spot);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> list, ParseException e) {
                    if (list.size() == 0) { //create
                        ParseObject hit = new ParseObject("w_hit");
                        hit.put("ssid", spot);
                        hit.put("user", ParseUser.getCurrentUser());
                        hit.put("nhits", 1);
                        hit.saveInBackground();
                    } else {//update
                        ParseObject hit = list.get(0);
                        hit.increment("nhits");
                        hit.saveInBackground();
                    }
                }


            });


        } catch (Exception e) {
            myLog.add("--error en subir hit: " + e.getMessage());
        }
    }

    /***
     * Ask the position and shows the weacons nearby
     */
    private static void ReportLocalPlaces() {
        MainActivity.mPos.connect(Position.REASON.AwareOfPlaces);

    }

    class WifiReceiver extends BroadcastReceiver {

        public void onReceive(Context c, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                NetworkInfo netInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if ((netInfo.getDetailedState() == (NetworkInfo.DetailedState.CONNECTED))) {
                    myLog.add("*** We just connected to wifi: " + netInfo.getExtraInfo(), "CON");
                    syncAllPinned();
                }

            } else if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                List<ScanResult> sr = mainWifi.getScanResults();
                CheckScanResults(sr);

                if (parameters.isSapoActive) {
                    SAPO2.addSPOTS(sr);
                }
            } else {
                myLog.add("Entering in a different state of network: " + action, "CON");
            }
        }

        /**
         * Upload the pinned info form SAP and from Weacons
         */
        private void syncAllPinned() {
            SAPO2.uploadIfRequired();
        }

    }
}

