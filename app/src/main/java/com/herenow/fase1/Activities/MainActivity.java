package com.herenow.fase1.Activities;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.herenow.fase1.Notifications.Notifications;
import com.herenow.fase1.Position;
import com.herenow.fase1.R;
import com.herenow.fase1.Wifi.WifiBoss;
import com.herenow.fase1.Wifi.WifiUpdater;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import util.myLog;

import static util.myLog.WriteUnhandledErrors;


public class MainActivity extends ActionBarActivity {

    public static Position mPos; //WARN. Lo he hecho static para poder usarlo en SAPO


    //Demo
    public static boolean demoMode;
    private static TextView tv;
    private Intent intentAddWeacon;
    private Intent intentCards;
    //Wifi
//    private WifiReceiver receiverWifi;
//    private WifiManager mainWifi;
    private WifiUpdater wu;
    private Timer t;
    private Switch mySwitch;
    //Todo solve reporting time between scannings
    //Report on screen
    private int im = 1;
    private long newTime, oldTime;
    private String msg = "";

    //    private boolean isSapoActive = true; //TODO activate /deactivate sapo remotely
    private WifiBoss wifiBoss;

    /***
     * Write in the main activity
     *
     * @param s text to print
     */
    public static void writeOnScreen(String s) {
        tv.setText(s);
    }

    public static void reportScanning(int found, int total) {
//        oldTime = newTime;
//        newTime = System.currentTimeMillis();
//        long diff = Math.round((newTime - oldTime) / 1000);
//        im++;
//        msg = im + ".(" + diff + "s|found=" + found + "/" + total + ") ";
//            tv.setText(msg);
        tv.append("  .(" + "s|found=" + found + "/" + total + ") ");
        //TODO restructurate notifications package

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WriteUnhandledErrors(true);
        myLog.initialize("/WCLOG/rt.txt"); //Log in a file on thephone
        Notifications.Initialize(this); //TODO: Really neededto initialize?

        mPos = new Position(this);
        mPos.connect(Position.REASON.GetWeacons);


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
        wifiBoss = new WifiBoss(this);
//
//        mainWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
//        receiverWifi = new WifiReceiver();
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
//        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
//        registerReceiver(receiverWifi, intentFilter);
    }

//    /**
//     * Upload the pinned info form SAP and from Weacons
//     */
//    private void syncAllPinned() {
//        SAPO2.uploadIfRequired();
//    }

    private void ParseUserLogIn() {

        ParseUser curr = ParseUser.getCurrentUser();
        if (curr == null) {
            myLog.add("sin user, vamos a loggear");

            ParseUser.logInInBackground("sorrento2", "spidey", new LogInCallback() {
                public void done(ParseUser user, ParseException e) {
                    if (user != null) {
                        myLog.add("Logged in");
                    } else {
                        myLog.add("Not Logged in");
                    }
                }
            });

        } else {
            myLog.add("Ya tenia user,");
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

    /***
     * Experimental: connect to a protected wifi
     *
     * @param view
     */
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

    public void clickCards(View view) {
        //open the card activity
        intentCards = new Intent(this, CardsActivity.class);
        startActivity(intentCards);
    }

    public void clickAddWeacon(View view) {
        intentAddWeacon = new Intent(this, AddWeaconActivity.class);
        startActivity(intentAddWeacon);
    }

//    class WifiReceiver extends BroadcastReceiver {
//
//        public void onReceive(Context c, Intent intent) {
//            final String action = intent.getAction();
//
//            if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
//                NetworkInfo netInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
//                if ((netInfo.getDetailedState() == (NetworkInfo.DetailedState.CONNECTED))) {
//                    myLog.add("*** We just connected to wifi: " + netInfo.getExtraInfo(), "CON");
//                    syncAllPinned();
//                }
//
//            } else if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
//                List<ScanResult> sr = mainWifi.getScanResults();
//                Notifications.CheckScanResults(sr);
//
//                if (isSapoActive) {
//                    SAPO2.addSPOTS(sr);
//                }
//            } else {
//                myLog.add("Entering in a different state of network: " + action, "CON");
//            }
//        }
//    }
}
