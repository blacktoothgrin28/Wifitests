package com.herenow.fase1;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseSession;
import com.parse.ParseUser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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
    public static HashMap<String, SSID> SSIDSTable = new HashMap<>(); //list {SSids , SSID

    //TODO Consider also the BSSID in the detection
    public static boolean demoMode; //in demo mode doesn't look for wifi's, it just lunch the events
    public static HashMap<String, Weacon> weaconsLaunchedTable;
    NotificationManager mNotificationManager;
    Intent intentList;
    private WifiUpdater wifiUpdater;
    private Timer t;
    private Switch mySwitch;
    private TextView tv;
    StringBuilder sb = new StringBuilder();
    WifiReceiver receiverWifi;
    List<ScanResult> wifiList;
    WifiManager mainWifi;
    int im = 1;
    private long newTime, oldTime;
    private String msg = "";
    public GoogleApiClient mGoogleApiClient;
    private Position mPos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPos = new Position(this); //Automatically obtain weacons and ssids
        //TODO correct, is awful

        //Write  unhandled exceptions in a log file in the phone
        AppendLog.initialize();
        Thread.currentThread().setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                PrintWriter pw;
                try {
                    pw = new PrintWriter(
                            new FileWriter(Environment.getExternalStorageDirectory() + "/rt.log", true));
                    ex.printStackTrace(pw);
                    pw.flush();
                    pw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


        tv = (TextView) findViewById(R.id.tv_demoStatus);
        mySwitch = (Switch) findViewById(R.id.sw_demo);
        //set the switch to off
        mySwitch.setChecked(false);
        tv.setText("demo OFF");
        //attach a listener to check for changes in state
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
        //TODO initilize Parse in Application class: http://stackoverflow.com/questions/30969612/android-parse-error-when-initializing-activity

        ParseUserLogIn();

        //Wifi
        mainWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        receiverWifi = new WifiReceiver();
        registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

    }

    private void ParseUserLogIn() {
//        Parse.User.current()
        ParseUser curr = ParseUser.getCurrentUser();
        if (curr == null) {
            Log.d("mhp", "sin usert, vamos a loggear");

            ParseUser.logInInBackground("sorrento", "spidey", new LogInCallback() {
                public void done(ParseUser user, ParseException e) {
                    if (user != null) {
                        Log.d("mhp", "Logged in");
                    } else {
                        Log.d("mhp", "Not Logged in");
                    }
                }
            });

        } else {
            Log.d("mhp", "Ya tenia user,");
        }
    }

    class WifiReceiver extends BroadcastReceiver {

        public void onReceive(Context c, Intent intent) {
            sb = new StringBuilder();
            wifiList = mainWifi.getScanResults();
            for (int i = 0; i < wifiList.size(); i++) {
                sb.append(new Integer(i + 1).toString() + ".");
                sb.append((wifiList.get(i)).SSID);
                sb.append("|");
            }
            oldTime = newTime;
            newTime = System.currentTimeMillis();
            long diff = Math.round((newTime - oldTime) / 1000);

            im++;
//            String msg = Integer.toString(im) + ". " +
//            Toast.makeText(getParent().getBaseContext(), Integer.toString(im) + sb, Toast.LENGTH_SHORT).show();
//            mainText.setText(sb);
            msg = Integer.toString(im) + ".(" + Long.toString(diff) + "s|n=" + Integer.toString(wifiList.size()) + ") ";
//            tv.setText(msg);
            tv.append(msg);
        }
    }

    /**
     * Get the weacons from the city, and put in in a hash
     */
    private void DownloadWeacons() {

        //  Two tables, one for SSIDS and other for weacons
        // 1. Ask location to google:

//        GPSCoordinates here = mPos.GetLastPosition();
//
//        // 2. Ask Parse all places in a radius around here, that have places associated
//        ParseQuery<ParseObject> query = ParseQuery.getQuery("SSIDS");
//        query.whereWithinKilometers("GPS",new ParseGeoPoint(here.getLatitude(),here.getLongitude()),5 );
//        query.whereDoesNotExist("associated_place");
//
//
//        query.findInBackground(new FindCallback<ParseObject>() {
//            @Override
//            public void done(List<ParseObject> objects, com.parse.ParseException e) {
//                if (e == null) {
//                    tv.setText("SSIDS downloaded");
//                    for (ParseObject obj : objects) {
////                        Weacon we = new Weacon(obj);
////                        weaconsTable.put(we.getSSID(), we);
//                        SSIDSTable.put(obj.getString("ssid"), obj.getString("associated_place"));
//                    }
////                    objectsWereRetrievedSuccessfully(objects);
//                } else {
////                    objectRetrievalFailed();
//                }
//            }
//            //TODO download the corresponding weacons
//
//        });
    }

    private void modeChange(boolean Demo) {

        t = new Timer();
        wifiUpdater = new WifiUpdater((TextView) findViewById(R.id.tv_demoStatus), this, Demo);

        if (Demo) {
            tv.setText("demo ON");
        } else {
            tv.setText("demo OFF");
        }
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(wifiUpdater);
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
        wifiUpdater = new WifiUpdater((TextView) findViewById(R.id.tv_demoStatus), this, demoMode);
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(wifiUpdater);
            }
        }, 0, 500);
    }

    public void clickDownloadWeacons(View view) {
        try {
            mPos.retrieveSSIDSFromParse(false);
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

    public void clickSendNotification(View view) {
        //we wait 3 secs
//        try {
//            Thread.sleep(3000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        PendingIntent resultPendingIntent = null;
        Intent resultIntent = new Intent(this, BrowserActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(BrowserActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

        NotificationCompat.Action myaction = new NotificationCompat.Action(R.drawable.ic_stat_name, "Call", resultPendingIntent);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setLargeIcon(bm)
                .setContentTitle("Weacons available")
//                .setContentText(getString(R.string.cid))
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND | Notification.FLAG_SHOW_LIGHTS)
//                .setLights(0xff00ff00, 300, 100)
                .setLights(0xE6D820, 300, 100)
                .setTicker("HearNow weacons detected")
                .addAction(myaction);
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        String[] events = {"H&M", "ZARA", "AIA", "USA Ambassay", "Starbucks", "Bus station"};

        // Sets a title for the Inbox in expanded layout
        inboxStyle.setBigContentTitle("Available weacons");

        // Moves events into the expanded layout
        for (String event : events) {
            inboxStyle.addLine(event);
        }
        // Moves the expanded layout object into the notification object.
        mBuilder.setStyle(inboxStyle);
        // Issue the notification here.
        ////////////////////////////

// Creates an explicit intent for an Activity in your app

        mBuilder.setContentIntent(resultPendingIntent);
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        int mId = 1;
        mNotificationManager.notify(mId, mBuilder.build());
    }

    //////////////////////////////////
    //Deprecated

    public void clickStopNotification(View view) {
        mNotificationManager.cancel(1);
    }

    /**
     * Read the file from sd card (dropbox) and put them in cloud
     */
    private void UploadFileWeacons() {
        //1. read file
        File sdcard = new File(Environment.getExternalStorageDirectory(), "HNdata");
        File file = new File(sdcard, "weacons.txt");

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            line = br.readLine();//headers
            while ((line = br.readLine()) != null) {
                if (line.toCharArray()[0] == '/' && line.toCharArray()[1] == '/')
                    continue; //skip comment lines (//)
                String[] parts = line.split("; ");
                Weacon we = new Weacon(parts, this);
//                weaconsTable.put(parts[0], we); //TODO populate this table from internec
                we.upload(this.getBaseContext());
            }
            br.close();
        } catch (IOException e) {
            //You'll need to add proper error handling here
        }
    }

    public void clickStopSearch(View view) {
        t.cancel();
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }
}
