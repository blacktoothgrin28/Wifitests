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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import util.Weacon;


public class MainActivity extends ActionBarActivity {
    public static HashMap<String, Weacon> weaconsTable = new HashMap<String, Weacon>();
    NotificationManager mNotificationManager;
    WebView wb;
    private WifiUpdater wifiUpdater;
    private Timer t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        if (savedInstanceState == null) {
//            getSupportFragmentManager().beginTransaction()
//                    .add(R.id.container, new PlaceholderFragment())
//                    .commit();
//        }

        readWeacons();
    }

    /**
     * Read the file from sd card (dropbox) with weacons description and put them in the hashmap
     */
    private void readWeacons() {
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
                weaconsTable.put(parts[0], we);
            }
            br.close();
        } catch (IOException e) {
            //You'll need to add proper error handling here
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

    public void clickSendNotification(View view) {
        //we wait 3 secs
//        try {
//            Thread.sleep(3000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        PendingIntent resultPendingIntent = null;
        Intent resultIntent = new Intent(this, SecondActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(SecondActivity.class);
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

    public void clickLoadPage(View view) {
        String url = "http://milenko.multiscreensite.com";

        try {
            wb = (WebView) this.findViewById(R.id.webViewTest);
//        wb.setWebChromeClient(new WebChromeClient());
//        wb.setWebViewClient(new WebViewClient());

            wb.getSettings().setJavaScriptEnabled(true);
            wb.loadUrl(url);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("mhp", "ppp" + e.getMessage());
        }

    }

    public void clickStartSearching(View view) {
        t = new Timer();
        wifiUpdater = new WifiUpdater((TextView) findViewById(R.id.fieldStrength), this);


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
