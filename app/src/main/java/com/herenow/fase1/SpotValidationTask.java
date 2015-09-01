package com.herenow.fase1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import parse.WifiSpot;
import util.AppendLog;

/**
 * Created by Milenko on 08/08/2015.
 */
public class SpotValidationTask extends AsyncTask<String, Boolean, Long> {
    private final Context context;
    private WifiManager mainWifi;
    private WifiReceiver receiverWifi;
    private int iScan = 1;
    private List<ScanResult> oldResults, newResults;
    private String bssid;

    public SpotValidationTask(Context context) {
        this.context = context;
        //Wifi
        mainWifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        receiverWifi = new WifiReceiver();
        IntentFilter intentFilter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
//        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);

        context.registerReceiver(receiverWifi, intentFilter);
    }

    /***
     * Check if the spot is present in old and not in the new list
     * check if at least one other spot is present in both (in the case there is others
     *
     * @param oldResults first scanning
     * @param newResults second scanning
     * @param BSSID      spot to be validated
     * @return
     */
    private static boolean Compare(List<ScanResult> oldResults, List<ScanResult> newResults, String BSSID) {
        ArrayList<String> listOld = new ArrayList<>();
        ArrayList<String> listNew = new ArrayList<>();

        for (ScanResult sr : oldResults) {
            listOld.add(sr.BSSID);
        }
        for (ScanResult sr : newResults) {
            listNew.add(sr.BSSID);
        }

        if (listOld.contains(BSSID) && !listNew.contains(BSSID)) {
            AppendLog.appendLog("se ha apagado correctmente " + BSSID, "VAL");
            if (listOld.size() > 1 || listNew.size() > 0) {
                int n = Intersection(listOld, listNew);
                AppendLog.appendLog("Habia inicialmente " + oldResults.size() + " spots. la interseccion es " + n, "VAL");

                return n > 0;
            } else {
                return true;
            }

        } else {
            AppendLog.appendLog("NO se ha apagado correctmente " + BSSID, "VAL");

        }
        return false;
    }

    private static int Intersection(ArrayList<String> listOld, ArrayList<String> listNew) {
        AppendLog.appendLog("original list has: " + listOld.size());
        listOld.retainAll(listNew);
        AppendLog.appendLog("Now, original list has: " + listOld.size());

        return listOld.size();
    }

    public void Validation(WifiSpot spot) {
//        //1. capture the ssids Now
//        List<ScanResult> oldResults = WifisAround();
//        //2. wait 5 seconds
//
//        //3. capture again
//        List<ScanResult> newResults = WifisAround();
//
//
//        //4. compare: the spot should disappear, but others should remain
//        return Compare(oldResults, newResults, spot);
    }

    /**
     * Override this method to perform a computation on a background thread. The
     * specified parameters are the parameters passed to {@link #execute}
     * by the caller of this task.
     * <p/>
     * This method can call {@link #publishProgress} to publish updates
     * on the UI thread.
     *
     * @param params The parameters of the task.
     * @return A result, defined by the subclass of this task.
     * @see #onPreExecute()
     * @see #onPostExecute
     * @see #publishProgress
     */
    @Override
    protected Long doInBackground(String... params) {

        bssid = params[0];

        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        try {
            wifi.startScan();
            this.wait(5000);
            wifi.startScan();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return null;

    }

    private void ShowListToast(List<ScanResult> results) {
        StringBuilder sb = new StringBuilder("**Detected\n");
        for (ScanResult sr : results) {
            sb.append(sr.SSID + "\n");
        }
        AppendLog.appendLog(sb.toString(), "VAL");
        Toast.makeText(context, sb.toString(), Toast.LENGTH_LONG).show();
    }

    class WifiReceiver extends BroadcastReceiver {
        public void onReceive(Context c, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                if (iScan == 1) {
                    oldResults = mainWifi.getScanResults();
                    iScan++;
                    ShowListToast(oldResults);
                } else {
                    newResults = mainWifi.getScanResults();
                    ShowListToast(newResults);
                    boolean isOwner = Compare(oldResults, newResults, bssid); //TODO como llevar el resultado al UI

                    if (isOwner) {
                        ParseQuery<WifiSpot> query = ParseQuery.getQuery(WifiSpot.class);
                        query.whereEqualTo("bssid", bssid);
                        query.findInBackground(new FindCallback<WifiSpot>() {
                            @Override
                            public void done(List<WifiSpot> list, ParseException e) {
                                if (list.size() == 0) {//create
                                    AppendLog.appendLog("not possible to validate a spot not uplooaded, many regs in parse", "VAL");
                                } else if (list.size() > 1) {//too many
                                    AppendLog.appendLog("not possible to save validation, many regs in parse", "VAL");
                                } else {//update
                                    WifiSpot we = list.get(0);
                                    we.setOwner(ParseUser.getCurrentUser());
                                    we.setValidated(true);
                                    we.Save();
                                }
                            }
                        });

                    }
                }
            } else {
                AppendLog.appendLog("Entering in a different state of network: " + action, "CON");
            }
        }
    }
}
