package com.herenow.fase1.MyServices;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.herenow.fase1.Sapo.SAPO2;

import java.util.ArrayList;
import java.util.List;

import util.myLog;
import util.parameters;

import static parse.ParseActions.CheckSpotMatches;

/**
 * Created by Milenko on 04/10/2015.
 */
public class WifiObserverService extends Service {

    String tag = "wfs";
    private Context mContext;
    private WifiManager mainWifi;
    private WifiReceiver receiverWifi;
    private int iScan = 0;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        myLog.add("Starting service ", tag);

        try {
            mContext = getApplicationContext();
            mainWifi = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
            receiverWifi = new WifiReceiver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
            intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
            mContext.registerReceiver(receiverWifi, intentFilter);
            Toast.makeText(mContext,"Detection ON",Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(mContext,"Not posstible to activate detection ",Toast.LENGTH_LONG).show();
            myLog.add("error starign " + e.getLocalizedMessage());
        }


        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        myLog.add("Destroying", tag);
        try {
            Toast.makeText(mContext,"Detection OFF",Toast.LENGTH_LONG).show();
            unregisterReceiver(receiverWifi);
            super.onDestroy();
        } catch (Exception e) {
            Toast.makeText(mContext,"Not possible to turn off detection",Toast.LENGTH_LONG).show();
            myLog.add("error destroying: " + e.getLocalizedMessage());
        }
    }

    /**
     * From the list of ScanResults, it looks if SSIDS are present in parse list,
     * and launch the notifications
     *
     * @param sr scanResults
     * @return number of matches
     */
    public void CheckScanResults(final List<ScanResult> sr) {
        iScan++;
//        if (iScan % 8 == 0) {
//            ReportLocalPlaces();
//        }

        ArrayList<String> bssids = new ArrayList<>();
        ArrayList<String> ssids = new ArrayList<>();
        StringBuilder sb = new StringBuilder(iScan+"+++++++ Scan results:+" + "\n");

        for (ScanResult r : sr) {
            bssids.add(r.BSSID);
            ssids.add(r.SSID);
            sb.append("  '" + r.SSID + "' | " + r.BSSID + " | l= " + r.level + "\n");
        }

        sb.append("+++++++++");
        myLog.add(sb.toString(), tag);

        CheckSpotMatches(sr, bssids, ssids);

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
                myLog.add("Entering in a different state of network: " + action, tag);
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
