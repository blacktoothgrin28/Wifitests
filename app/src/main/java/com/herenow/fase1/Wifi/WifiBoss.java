package com.herenow.fase1.Wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import com.herenow.fase1.Activities.LocationCallback;
import com.herenow.fase1.Sapo.SAPO2;

import java.util.ArrayList;
import java.util.List;

import parse.ParseActions;
import util.GPSCoordinates;
import util.myLog;
import util.parameters;

import static parse.ParseActions.CheckSpotMatches;

/**
 * Created by Milenko on 24/09/2015.
 */
public class WifiBoss {
    private static int iScan = 0;
    private final WifiManager mainWifi;
    private final WifiReceiver receiverWifi;
    private  Context mContext;

    public WifiBoss(Context context) {
        mContext = context;
        mainWifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        receiverWifi = new WifiReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        context.registerReceiver(receiverWifi, intentFilter);
    }

    /**
     * From the list of ScanResults, it looks if SSIDS are present in parse list,
     * and launch the notifications
     *
     * @param sr scanResults
     * @return number of matches
     */
    public  void CheckScanResults(final List<ScanResult> sr) {
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

        CheckSpotMatches(sr, bssids, ssids);

    }

    /***
     * Ask the position and shows the weacons nearby
     */
    private  void ReportLocalPlaces() {

        (new LocationAsker()).DoSomethingWithPosition(new LocationCallback() {
            @Override
            public void LocationReceived(GPSCoordinates gps) {
                ParseActions.getPlacesAround(gps, 0.3);
            }
        }, mContext);
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

