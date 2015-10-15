package com.herenow.fase1.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.herenow.fase1.Notifications.Notifications;
import com.herenow.fase1.Position;
import com.herenow.fase1.R;
import com.herenow.fase1.Wifi.LocationAsker;
import com.herenow.fase1.Wifi.WifiUpdater;
import com.herenow.fase1.MyServices.WifiObserverService;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import parse.ParseActions;
import util.GPSCoordinates;
import util.myLog;
import util.parameters;

import static util.myLog.WriteUnhandledErrors;


public class MainActivity extends ActionBarActivity {

    public static Position mPos; //WARN. Lo he hecho static para poder usarlo en SAPO

    private static TextView tv;
    Intent intentWifiObs;
    private Intent intentAddWeacon;
    private Intent intentCards;
    private WifiUpdater wu;
    private Timer t;
    //    private Switch mySwitch;
    //Todo solve reporting time between scannings
    //Report on screen
    private int im = 1;
    private long newTime, oldTime;
    private String msg = "";
    private Switch swDetection;// start or stot wifiservice detection
    private Spinner spinner;
    private ArrayList<String> lista;
    private ArrayList<String> listaObj;

    /***
     * Write in the main activity
     *
     * @param s text to print
     */
    public static void writeOnScreen(String s) {
        tv.setText(s);
    }
    //    private boolean isSapoActive = true; //TODO activate /deactivate sapo remotely

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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        myLog.add("++++++++++++++++ on save");
    }

    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        super.onActivityReenter(resultCode, data);
        myLog.add("++++++++++++++++ on activity reenter");
    }

    @Override
    protected void onStart() {
        super.onStart();
        myLog.add("++++++++++++++++ On Start");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        myLog.add("++++++++++++++++ on restarrt");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myLog.add("++++++++++++++++ on destroy");
    }

    @Override
    protected void onStop() {
        super.onStop();
        myLog.add("++++++++++++++++ on stop");
    }

    //    private WifiBoss wifiBoss;

    @Override
    protected void onPause() {
        myLog.add("++++++++++++++++ on Pause");
        super.onPause();
    }

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        myLog.add("++++++++++++++++ On create");
        setContentView(R.layout.activity_main);

        WriteUnhandledErrors(true);
        myLog.initialize("/WCLOG/rt.txt"); //Log in a file on the phone
        Notifications.Initialize(this); //TODO: Really needed to initialize?

        retrieveSpotsAround(false, parameters.radioSpotsQuery);

        initializeViews();

        //PARSE
        ParseUserLogIn();

    }

    @Override
    protected void onResume() {
        super.onResume();
        myLog.add("++++++++++++++++ Retornando a la Main");
    }

    private void initializeViews() {

        swDetection = (Switch) findViewById(R.id.sw_detection);
        swDetection.setChecked(false);
        swDetection.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //startWifiService();
                    Context mContext = getApplicationContext();
                    mContext.startService(new Intent(mContext, WifiObserverService.class));
                } else {
                    //stopWifiService();
                    Context mContext = getApplicationContext();
                    mContext.stopService(new Intent(mContext, WifiObserverService.class));
                }
            }
        });

        //SPINNER
        spinner = (Spinner) this.findViewById(R.id.spinner_weacon);
        lista = new ArrayList<String>();
        listaObj = new ArrayList<String>();

        listasAdd("Choose", "");
        listasAdd("Creapolis", "esade");
        listasAdd("AIA", "AIA");
        listasAdd("El Prat", "WiFi");
        listasAdd("Monasterio", "wifip224");
//        listasAdd("Cafeteria", "wifip224");
//        listasAdd("Juan", "wifip224");


        ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, lista);
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adaptador);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    String selected = parent.getItemAtPosition(position).toString();
                    String ssid = listaObj.get(position);
                    Toast.makeText(parent.getContext(), "Seleccionado: " + selected + " | " + ssid, Toast.LENGTH_SHORT).show();

                    ParseActions.ssidForcedDetection(ssid, 3);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });


        //TEXTVIEW
        tv = (TextView) findViewById(R.id.tv_demoStatus);
        tv.setText("demo OFF");
        //check the current state before we display the screen
    }

//    /**
//     * Upload the pinned info form SAP and from Weacons
//     */
//    private void syncAllPinned() {
//        SAPO2.uploadIfRequired();
//    }

    private void listasAdd(String label, String ssid) {
        lista.add(label);
        listaObj.add(ssid);
    }

    /**
     * Load the spots from parse that are around from current postion
     *
     * @param bLocal If they are stored in local database
     * @param radio
     */
    private void retrieveSpotsAround(final boolean bLocal, final double radio) {
        (new LocationAsker()).DoSomethingWithPosition(new LocationCallback() {
            @Override
            public void LocationReceived(GPSCoordinates gps) {
                ParseActions.getSpots(bLocal, radio, gps, getApplicationContext());
            }
        }, this);
    }

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

        //This is for connecting programatelly
//        String networkSSID = "piripiri";
//        String networkPass = "spideyhg3711";
//
//        WifiConfiguration conf = new WifiConfiguration();
//        conf.SSID = "\"" + networkSSID + "\"";
//
//        //WEP In case of WEP, if your password is in hex, you do not need to surround it with quotes.
////        conf.wepKeys[0] = "\"" + networkPass + "\"";
////        conf.wepTxKeyIndex = 0;
////        conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
////        conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
//
//        //WPA
//        conf.preSharedKey = "\"" + networkPass + "\"";
//
//        //open
////        conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
//
//        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
//        if (!wifiManager.isWifiEnabled()) {
//            wifiManager.setWifiEnabled(true);
//        }
//        wifiManager.addNetwork(conf);
//
//        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
//
//        for (WifiConfiguration i : list) {
//            if (i.SSID != null && i.SSID.equals("\"" + networkSSID + "\"")) {
//                wifiManager.disconnect();
//                wifiManager.enableNetwork(i.networkId, true);
//                wifiManager.reconnect();
//
//                break;
//            }
//        }

    }

    public void clickCards(View view) {
        //open the card activity
        intentCards = new Intent(this, CardsActivityOld.class);
        startActivity(intentCards);
    }

    public void clickAddWeacon(View view) {

        intentAddWeacon = new Intent(this, AddWeaconActivity.class);
        startActivity(intentAddWeacon);
    }

    public void launchAll(View view) {
        final Timer t;
        t = new Timer();
        t.schedule(new TimerTask() {
            public int i = 0;

            @Override
            public void run() {
                if (i < listaObj.size()) {
                    myLog.add("**LAnzando automaticamente: " + lista.get(i));
                    String ssid = listaObj.get(i);
                    ParseActions.ssidForcedDetection(ssid, 1);
                    i++;
                } else {
                    t.cancel();
                }
            }
        }, 3000, 4000);


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
