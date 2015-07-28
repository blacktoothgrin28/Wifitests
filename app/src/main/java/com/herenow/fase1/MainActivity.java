package com.herenow.fase1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import util.AppendLog;
import util.Weacon;


public class MainActivity extends ActionBarActivity {
    public static HashMap<String, Weacon> weaconsTable = new HashMap<String, Weacon>(); //Total list of weacons
    public static HashMap<String, SPOT> BSSIDSTable = new HashMap<>(); //list {SSids , SPOT}

    public static boolean demoMode; //in demo mode doesn't look for wifi's, it just lunch the events
    public static Position mPos; //WARN. Lo he hecho static para poder usarlo en SAPO
    public static Location lastLocation;
    public GoogleApiClient mGoogleApiClient;
    Intent intentList;
    WifiReceiver receiverWifi;
    WifiManager mainWifi;
    int im = 1;
    private WifiUpdater wu;
    private Timer t;
    private Switch mySwitch;
    private TextView tv;
    private long newTime, oldTime;
    private String msg = "";
    private long oldTimeSapo = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Write  unhandled exceptions in a log file in the phone
        AppendLog.initialize();
        Notifications.Initialize(this);

        mPos = new Position(this);
        mPos.connect(Position.REASON.GetWeacons);

        //Log unhandled exceptions
        Thread.currentThread().setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                PrintWriter pw;
                try {
                    pw = new PrintWriter(
                            new FileWriter(Environment.getExternalStorageDirectory() + "/WCLOG//rt.txt", true));
                    ex.printStackTrace(pw);
                    pw.flush();
                    pw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        mySwitch = (Switch) findViewById(R.id.sw_demo);
        mySwitch.setChecked(false);
        tv = (TextView) findViewById(R.id.tv_demoStatus);
        tv.setText("demo OFF");
        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                demoMode = isChecked;
                modeChange(isChecked);
            }
        });

        //check the current state before we display the screen
        demoMode = mySwitch.isChecked();

        //PARSE
        ParseUserLogIn();


        //Wifi
        mainWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        receiverWifi = new WifiReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        registerReceiver(receiverWifi, intentFilter);
    }

    /**
     * Upload the pinned info form SAP and from Weacons
     */
    private void syncAllPinned() {
        SAPO2.uploadIfRequired();
        uploadAllPinnedWeacon();
    }


    /**
     * Upload all pinned data related to weacons: weacon definition , launched, openend, etc.
     */
    private void uploadAllPinnedWeacon() {
        //TODO subir los weacons pinned
    }


    private boolean isConnectedViaWifi() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return mWifi.isConnected();
    }

    private void ParseUserLogIn() {
//        Parse.User.current()
        ParseUser curr = ParseUser.getCurrentUser();
        if (curr == null) {
            AppendLog.appendLog("sin user, vamos a loggear");

            ParseUser.logInInBackground("sorrento", "spidey", new LogInCallback() {
                public void done(ParseUser user, ParseException e) {
                    if (user != null) {
                        AppendLog.appendLog("Logged in");
                    } else {
                        AppendLog.appendLog("Not Logged in");
                    }
                }
            });

        } else {
            AppendLog.appendLog("Ya tenia user,");
        }
    }

    private void modeChange(boolean Demo) {

        t = new Timer();
        wu = new WifiUpdater((TextView) findViewById(R.id.tv_demoStatus), this, Demo);

        if (Demo) {
            tv.setText("demo ON");
        } else {
            tv.setText("demo OFF");
        }
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(wu);
            }
        }, 4000, 3000);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.browserMenuPrincipal) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void clickConnect(View view) {

        String networkSSID = "piripiri";
        String networkPass = "spideyhg3711";

        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = "\"" + networkSSID + "\"";

        //WEP In case of WEP, if your password is in hex, you do not need to surround it with quotes.
//        conf.wepKeys[0] = "\"" + networkPass + "\"";
//        conf.wepTxKeyIndex = 0;
//        conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
//        conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);

        //WPA
        conf.preSharedKey = "\"" + networkPass + "\"";

        //open
//        conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);

        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        wifiManager.addNetwork(conf);

        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();

        for (WifiConfiguration i : list) {
            if (i.SSID != null && i.SSID.equals("\"" + networkSSID + "\"")) {
                wifiManager.disconnect();
                wifiManager.enableNetwork(i.networkId, true);
                wifiManager.reconnect();

                break;
            }
        }

    }

    public void clickStartSearching(View view) {
        t = new Timer();
        wu = new WifiUpdater((TextView) findViewById(R.id.tv_demoStatus), this, demoMode);
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(wu);
            }
        }, 0, 500);
    }

    public void clickDownloadWeacons(View view) {
        try {
            mPos.retrieveSPOTSFromParse(false);
//            intentList = new Intent(this, WeaconListActivity.class);
//            startActivity(intentList);
//            DownloadWeacons(); Deprecated,now load from mPos2
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clickAddWeacon(View view) {
        intentList = new Intent(this, AddWeaconActivity.class);
        startActivity(intentList);
    }

    class WifiReceiver extends BroadcastReceiver {
        public void onReceive(Context c, Intent intent) {
            final String action = intent.getAction();
//            Location loc; TODO put location battery friendly
            AppendLog.appendLog("*BroadcastReceiver: " + action, "CON");

            if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                NetworkInfo netInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if ((netInfo.getDetailedState() == (NetworkInfo.DetailedState.CONNECTED))) {
                    // your wifi is connected, do what you want to do
                    AppendLog.appendLog("*** We just connected to wifi: " + netInfo.getExtraInfo(), "CON");
                    syncAllPinned();
                } else {
//                    AppendLog.appendLog("***We are not connected to wifi:" + netInfo.toString(), "CON");
                }

            } else if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) { //Arrieved new scan
//                AppendLog.appendLog("entramos en WifiManager.SCAN_RESULTS_AVAILABLE_ACTION","CON");
                List<ScanResult> sr = mainWifi.getScanResults();
                int found = Notifications.CheckScanResults(sr);
//                loc = mPos.GetLastPosition(); //TODO no pedir localización? o cerrar conexión?

                oldTime = newTime;
                newTime = System.currentTimeMillis();
                long diff = Math.round((newTime - oldTime) / 1000);
                im++;
                msg = im + ".(" + diff + "s|found=" + found + "/" + sr.size() + ") ";
//            tv.setText(msg);
                tv.append(msg);

                SAPO2.addSPOTS(sr);//, loc);
                AppendLog.appendLog("Automatic scanning: " + msg);
            } else if (action.equals(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION)) { //Connection /disconnection from wifi
                //DEPRECATED
                AppendLog.appendLog("entramos en SUPPLICANT_CONNECTION_CHANGE_ACTION", "CON");

            } else {
                AppendLog.appendLog("entramos en otro: " + action, "CON");
            }

            //DOesn't work'
//            if (action.equals(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION)) {
//                AppendLog.appendLog("**************in SUPlicart");
//                if (intent.getBooleanExtra(WifiManager.EXTRA_SUPPLICANT_CONNECTED, false)) {
//                    //connected
//                    AppendLog.appendLog("Connected to wifi");
//                } else {
//                    // wifi connection was lost
//                }
//            }


        }
    }

}
