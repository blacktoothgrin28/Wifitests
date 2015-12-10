package com.herenow.fase1.Activities;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.text.method.ScrollingMovementMethod;
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

import com.herenow.fase1.MyServices.WifiObserverService;
import com.herenow.fase1.Notifications.Notifications;
import com.herenow.fase1.Parada;
import com.herenow.fase1.Position;
import com.herenow.fase1.R;
import com.herenow.fase1.Wifi.LocationAsker;
import com.herenow.fase1.Wifi.LogInManagement;
import com.herenow.fase1.Wifi.WifiUpdater;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import parse.ParseActions;
import util.GPSCoordinates;
import util.myLog;
import util.parameters;

import static util.myLog.WriteUnhandledErrors;


public class MainActivity extends ActionBarActivity implements TextToSpeech.OnInitListener {

    public static Position mPos; //WARN. Lo he hecho static para poder usarlo en SAPO

    private static TextView tv;
    private static int im = 1;
    private static long newTime = System.currentTimeMillis();
    Intent intentWifiObs;
    private Intent intentAddWeacon;


    private Intent intentCards;
    //Todo solve reporting time between scannings
    //Report on screen
    private Switch swDetection;// start or stot wifiservice detection
    private Spinner spinner;
    private ArrayList<String> lista;
    private ArrayList<String> listaObj;
    private TextToSpeech myTTS;

    /***
     * Write in the main activity
     *
     * @param s text to print
     */
    public static void writeOnScreen(String s) {
        tv.append("\n> " + s);
    }
    //    private boolean isSapoActive = true; //TODO activate /deactivate sapo remotely

    public static void reportScanning(int found, int total) {
        long oldTime = newTime;
        newTime = System.currentTimeMillis();
        long diff = Math.round((newTime - oldTime) / 1000);

        writeOnScreen("+++ " + im + "  .(" + +diff + "s|found=" + found + "/" + total + ") ");
        im++;
    }

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        myLog.add("++++++++++++++++ On create");
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            String msg = savedInstanceState.getString("test");
            myLog.add("222recuperando el bundle en oncreate: " + msg);
        }

        WriteUnhandledErrors(true);
        myLog.initialize("/WCLOG/rt.txt"); //Log in a file on the phone
        Notifications.Initialize(this); //TODO: Really needed to initialize?

        retrieveSpotsAround(false, parameters.radioSpotsQuery);

        initializeViews();

        //PARSE
        ParseUserLogIn();

        //speak
//        myTTS = new TextToSpeech(this, this);

        //TEST
//   clickCards(null);

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
        listasAdd("Vermell", "HotelsantcugatP1");
        listasAdd("ZARA", "WLAN_11F8");
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
        tv.setMovementMethod(new ScrollingMovementMethod());
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
                if(gps==null)gps=new GPSCoordinates(41.474722,                2.086667);
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

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void clickCards(View view) {
        //open the card activity
//        intentCards = new Intent(this, CardsActivityOld.class);
//        startActivity(intentCards);

        Intent testIntentCards = new Intent(this, TestCardsActivity.class);
        startActivity(testIntentCards);

//        String s = "Por fin puedo bajar a comer. Menos mal que Milenko me trata muy bien. Os quiero mucho Amalia, Matías y Rebeca.";
//        myTTS.speak(s, TextToSpeech.QUEUE_FLUSH, null, "1");

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

    public void onClickSubscribe(View view) {
        LogInManagement.subscribeAChannel("wifiUniqueName", this.getApplicationContext());
    }

    @Override
    public void onInit(int status) {
        myLog.add("+++speech inicializado");
        myTTS.setLanguage(new Locale("es", "ES"));
    }

    /**
     * Shows parada info in Sant Cugat
     *
     * @param view
     */
    public void onClickParada(View view) {
        //1. get coordinates
        (new LocationAsker()).DoSomethingWithPosition(new LocationCallback() {
            @Override
            public void LocationReceived(GPSCoordinates gps) {
                //2. ask near stops, retrieve data and open activity
//                (new readParada()).execute(new GPSCoordinates[]{gps});

                Intent IntentCards = new Intent(getBaseContext(), CardsActivity.class);
                IntentCards.putExtra("wLatitude", gps.getLatitude());
                IntentCards.putExtra("wLongitude", gps.getLongitude());
                IntentCards.putExtra("wCards", new String[]{"parada"});
                startActivity(IntentCards);


            }
        }, this);

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
