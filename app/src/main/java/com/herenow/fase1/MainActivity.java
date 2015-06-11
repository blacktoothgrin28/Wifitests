package com.herenow.fase1;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import util.Weacon;


public class MainActivity extends ActionBarActivity {
    public static HashMap<String, Weacon> weaconsTable = new HashMap<String, Weacon>(); //Total list of weacons
    public static boolean demoMode; //in demo mode doesn't look for wifi's, it just lunch the events
    public static HashMap<String, Weacon> weaconsLaunchedTable;
    NotificationManager mNotificationManager;
    WebView wb;
    Intent intentList;
    private WifiUpdater wifiUpdater;
    private Timer t;
    private Switch mySwitch;
    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "CADa4nX2Lx29QEJlC3LUY1snbjq9zySlF5S3YSVG", "hC9VWCmGEBxb9fSGQPiOjSInaAPnYMZ0t8k3V0UO");

//        UploadFileWeacons(); //From file to cloud
        DownloadWeacons();
    }

    /**
     * takes all weacons from internet and put in local Hash
     */
    private void DownloadWeacons() {
        String name;
        //TODO download few weacons only, in the area
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Weacon");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, com.parse.ParseException e) {
                if (e == null) {
                    for (ParseObject obj : objects) {
                        Weacon we = new Weacon(obj);
                        weaconsTable.put(we.getSSID(), we);
                    }
//                    objectsWereRetrievedSuccessfully(objects);
                } else {
//                    objectRetrievalFailed();
                }
            }

        });
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
        }, 4000, 500);

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
                uploadWeacon(we);
            }
            br.close();
        } catch (IOException e) {
            //You'll need to add proper error handling here
        }
    }

    private void uploadWeacon(Weacon we) {

        //Check if is already present: (now SSID is the key) //TODO change the key

        ParseObject parseWeacon = new ParseObject("Weacon");
        parseWeacon.put("SSID", we.getSSID()); // ="" means no SSID, is like monuments. detect several around. system of rules
        parseWeacon.put("BSSID", we.getBSSID()); //
        parseWeacon.put("Name", we.getName());
        parseWeacon.put("Description", we.getMessage());

        //Upload Logo
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        we.getLogo().compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        ParseFile fileLogo = new ParseFile(we.getImagePath().getName(), byteArray);
        parseWeacon.put("Logo", fileLogo); //TODO check that file to upload is small

        parseWeacon.put("MainUrl", we.getUrl());
//        parseWeacon.put("MultipleURL", we.getUrl()); //TODO think how to store several
        parseWeacon.put("Level", we.getLevel());

        parseWeacon.put("Validated", we.isValidated()); // The check to guarantee that is the propietary has been done

        parseWeacon.put("GPS", we.getParseGps()); //TODO near coordinates
        parseWeacon.put("Type", we.getTypeString());

        parseWeacon.saveInBackground();

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

    public void clickStopNotification(View view) {
        mNotificationManager.cancel(1);
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


        // Turn on wifi if off

        // connect to piripi con la contrasena
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

    public void clickStopSearch(View view) {
        t.cancel();
    }

    public void clickShowListActivity(View view) {
        try {
            intentList = new Intent(this, WeaconListActivity.class);
            startActivity(intentList);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
