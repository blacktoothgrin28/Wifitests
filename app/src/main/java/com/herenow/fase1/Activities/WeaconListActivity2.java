package com.herenow.fase1.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.herenow.fase1.Notifications.Notifications;
import com.herenow.fase1.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import parse.WeaconParse;
import util.DividerItemDecoration;
import util.WeaconAdapter;

/***
 * The activity where we list weacons, but using fancy cards
 */
public class WeaconListActivity2 extends ActionBarActivity {
    private RecyclerView mRecyclerView;
    private WeaconAdapter adapter;
    //    private List<WeaconItem> weaconItemList = new ArrayList<WeaconItem>();
    private List<WeaconParse> weaconItemList = new ArrayList<>();
    private Intent intentWeb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weacon_list);
//                trans_left_in, R.anim.trans_left_out);


        mRecyclerView = new RecyclerView(this);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.hasFixedSize();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.abc_list_divider_mtrl_alpha)));


        //Fill the list for the example
//        Collection<Weacon> intermediate = MainActivity.weaconsTable.values();
//        for (Object obj : intermediate.toArray()) {
//            Weacon wec = (Weacon) obj;
////            weaconItemList.add(new WeaconItem(wec.getName(), wec.getMessage(), wec.getImagePath().toString(), wec.getUrl()));
//            weaconItemList.add(wec);
//        }
        //Fill the list with launched
        Collection<WeaconParse> intermediate = Notifications.weaconsLaunchedTable.values();
        for (Object obj : intermediate.toArray()) {
            WeaconParse wec = (WeaconParse) obj;
            weaconItemList.add(wec); //To the showed list.
        }

        adapter = new WeaconAdapter(this, weaconItemList);
        adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WeaconParse we = (WeaconParse) v.getTag();

                intentWeb = new Intent(WeaconListActivity2.this, BrowserActivity.class);
                intentWeb.putExtra("wName", we.getName());
                intentWeb.putExtra("wUrl", we.getUrl());
                intentWeb.putExtra("wLogo", we.getLogoRounded());

                WeaconListActivity2.this.startActivity(intentWeb);
            }
        });

        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_weacon_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}

