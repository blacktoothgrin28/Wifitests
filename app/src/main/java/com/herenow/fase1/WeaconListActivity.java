package com.herenow.fase1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import util.FeedItem;
import util.MyRecyclerAdapter;
import util.Weacon;


public class WeaconListActivity extends ActionBarActivity {
    private RecyclerView mRecyclerView;
    private MyRecyclerAdapter adapter;
    private List<FeedItem> feedItemList = new ArrayList<FeedItem>();
    private Intent intentWeb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weacon_list);


        mRecyclerView = new RecyclerView(this);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        Log.d("mhp", "ponirndole el linear LY");
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Fill the list for the example
        Collection<Weacon> intermediate = MainActivity.weaconsTable.values();
        for (Object obj : intermediate.toArray()) {
            Weacon wec = (Weacon) obj;
            feedItemList.add(new FeedItem(wec.getName(), wec.getMessage(), wec.getPath().toString()));
        }

        adapter = new MyRecyclerAdapter(this, feedItemList);
        adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("mhp", "clickado el " + mRecyclerView.getChildPosition(v));
                intentWeb = new Intent(WeaconListActivity.this, SecondActivity.class);

                //TODO recupear el weacon correcto
                Weacon we = MainActivity.weaconsTable.get("AIA");
                intentWeb.putExtra("wName", we.getName());
                intentWeb.putExtra("wUrl", we.getUrl());
                intentWeb.putExtra("wLogo", we.getLogo());

                WeaconListActivity.this.startActivity(intentWeb);

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

