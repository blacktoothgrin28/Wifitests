package com.herenow.fase1;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import parse.WeaconParse;
import util.WeaconAdapter;


public class WeaconListActivity extends ActionBarActivity {
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

                intentWeb = new Intent(WeaconListActivity.this, BrowserActivity.class);
                intentWeb.putExtra("wName", we.getName());
                intentWeb.putExtra("wUrl", we.getUrl());
                intentWeb.putExtra("wLogo", we.getLogoRounded());

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

class DividerItemDecoration extends RecyclerView.ItemDecoration {

    private Drawable mDivider;

    public DividerItemDecoration(Context context, AttributeSet attrs) {
        final TypedArray a = context.obtainStyledAttributes(attrs, new int[]{android.R.attr.listDivider});
        mDivider = a.getDrawable(0);
        a.recycle();
    }

    public DividerItemDecoration(Drawable divider) {
//        mDivider = divider;

//        mDivider = divider.setColorFilter( 0xffff0000, PorterDuff.Mode.MULTIPLY );
//        mDivider = divider.setColorFilter(Color.parseColor("#AE6118"));
        divider.setColorFilter(0xFF5068F2, PorterDuff.Mode.MULTIPLY);
        mDivider = divider;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        if (mDivider == null) return;
        if (parent.getChildPosition(view) < 1) return;

        if (getOrientation(parent) == LinearLayoutManager.VERTICAL)
            outRect.top = mDivider.getIntrinsicHeight();
        else outRect.left = mDivider.getIntrinsicWidth();
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent) {
        if (mDivider == null) {
            super.onDrawOver(c, parent);
            return;
        }

        if (getOrientation(parent) == LinearLayoutManager.VERTICAL) {
            final int left = parent.getPaddingLeft();
            final int right = parent.getWidth() - parent.getPaddingRight();
            final int childCount = parent.getChildCount();

            for (int i = 1; i < childCount; i++) {
                final View child = parent.getChildAt(i);
                final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
                final int size = mDivider.getIntrinsicHeight();
                final int top = child.getTop() - params.topMargin;
                final int bottom = top + size;
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        } else { //horizontal
            final int top = parent.getPaddingTop();
            final int bottom = parent.getHeight() - parent.getPaddingBottom();
            final int childCount = parent.getChildCount();

            for (int i = 1; i < childCount; i++) {
                final View child = parent.getChildAt(i);
                final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
                final int size = mDivider.getIntrinsicWidth();
                final int left = child.getLeft() - params.leftMargin;
                final int right = left + size;
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }
    }

    private int getOrientation(RecyclerView parent) {
        if (parent.getLayoutManager() instanceof LinearLayoutManager) {
            LinearLayoutManager layoutManager = (LinearLayoutManager) parent.getLayoutManager();
            return layoutManager.getOrientation();
        } else
            throw new IllegalStateException("DividerItemDecoration can only be used with a LinearLayoutManager.");
    }

}