package com.herenow.fase1;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;


public class SecondActivity extends ActionBarActivity {

    private String wName;
    private String wUrl;
    private Bitmap wLogo;
    private WebView wb;
//    private TextView tv;
//    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        Bundle b = getIntent().getExtras();
        wName = b.getString("wName");
        wUrl = b.getString("wUrl");
        wLogo = (Bitmap) b.get("wLogo");
//        tv = (TextView) findViewById(R.id.msgSeg);
//        tv.setText(wName); //TEst
        this.setTitle(wName);

        try {
            wb = (WebView) this.findViewById(R.id.webView); //TODO set a progress bar for loading
//        wb.setWebChromeClient(new WebChromeClient()); //TODO fetching or cache
//        wb.setWebViewClient(new WebViewClient());
            wb.getSettings().setJavaScriptEnabled(true);
            wb.loadUrl(wUrl);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("mhp", "ppp" + e.getMessage());
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
//        this.menu = menu;
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_second, menu);
//        MenuItem menuItem = menu.findItem(R.id.browserMenu);
//        menuItem.setTitle(wName);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.browserMenu) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
