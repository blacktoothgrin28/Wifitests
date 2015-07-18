package com.herenow.fase1;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import util.AppendLog;
import util.GPSCoordinates;
import util.Weacon;

/**
 * Created by Milenko on 16/07/2015.
 */
public class Position implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private GoogleApiClient mGoogleApiClient;
    private Context context;
    private Location mLastLocation;
    private GPSCoordinates gps;
    private final static boolean bFromLocalParse = true;

    //TODO for now receive location only onces. see http://blog.teamtreehouse.com/beginners-guide-location-android
    public Position(Context context) {
        this.context = context.getApplicationContext();
        buildGoogleApiClient();
    }

    protected synchronized void buildGoogleApiClient() {
        try {
            mGoogleApiClient = new GoogleApiClient.Builder(context)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            mGoogleApiClient.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Location GetLastPosition() {
        try {
            if (!mGoogleApiClient.isConnected()) {
                mGoogleApiClient.connect();
            }

//            AppendLog.appendLog("*****solicitando GetLastPostition");
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
//            AppendLog.appendLog("*****obtenido GetLastPostition:" + mLastLocation);
        } catch (Exception e) {
            e.printStackTrace();
//            AppendLog.appendLog("***error at GetLastPostition" + e.getMessage());
        }
//        return new GPSCoordinates(mLastLocation);
        return mLastLocation;
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        //TODO decide when ask places to Cloud (change in position , etc)
        if (mLastLocation != null) {
            gps = new GPSCoordinates(mLastLocation);
            retrieveSSIDSFromParse(bFromLocalParse);
        } else {
            gps = new GPSCoordinates(0, 0);
            Log.d("mhp", "Not possible to retrieve last place from Google");
            retrieveSSIDSFromParse(true);
        }
    }

    public void retrieveSSIDSFromParse(final boolean bLocal) {
        try {
            Log.d("mhp", "retrieving SSIDS from local:" + bLocal + " user: " + ParseUser.getCurrentUser());
            ParseQuery<ParseObject> query = ParseQuery.getQuery("SSIDS");
            query.whereWithinKilometers("GPS", new ParseGeoPoint(gps.getLatitude(), gps.getLongitude()), 5);
            query.setLimit(700);
            if (bLocal) query.fromLocalDatastore();
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null) {
                        Log.d("mhp", "number of SSIDS:" + objects.size());
                        if (!bLocal) ParseObject.pinAllInBackground(objects);
                        ArrayList ids = new ArrayList();
                        for (ParseObject obj : objects) {
                            SSID ssid = new SSID(obj);
                            MainActivity.SSIDSTable.put(obj.getString("ssid"), ssid);
                            ids.add(ssid.getPlaceId());
                        }
                        retrievePlacesFromParse(bLocal, ids);
                    } else {
                        objectRetrievalFailed(e);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("mhp", "failed retrieving ssids: " + e.getMessage());

        }
    }

    private void retrievePlacesFromParse(final boolean bLocal, Collection weaconsIds) {
        Log.d("mhp", "retrieving place from local:" + bLocal);
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Weacon");
        query.whereContainedIn("objectId", weaconsIds);
        query.setLimit(200);
        if (bLocal) query.fromLocalDatastore();
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, com.parse.ParseException e) {
                if (e == null) {
                    Log.d("mhp", "number of places:" + objects.size());
                    if (!bLocal) ParseObject.pinAllInBackground(objects, new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Log.d("mhp", "Ive pinned the places:");
                            }
                        }
                    });
                    for (ParseObject obj : objects) {
                        Weacon we = new Weacon(obj);
                        MainActivity.weaconsTable.put(obj.getObjectId(), we);
                    }
                    ParseObject.pinAllInBackground(objects);
                } else {
                    objectRetrievalFailed(e);
                }
            }
        });

    }

    private void objectRetrievalFailed(ParseException e) {
        Log.d("mhp", "Failed to retrieve objects " + e.getMessage());
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
