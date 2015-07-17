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

import java.util.List;

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

    public GPSCoordinates GetLastPosition() {
        return gps;
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {
            gps = new GPSCoordinates(mLastLocation);

            ParseQuery<ParseObject> query = ParseQuery.getQuery("SSIDS");
            query.whereWithinKilometers("GPS", new ParseGeoPoint(gps.getLatitude(), gps.getLongitude()), 5);
            query.setLimit(700);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, com.parse.ParseException e) {
                    if (e == null) {
                        for (ParseObject obj : objects) {
                            ParseObject we = new ParseObject("Weacon");
                            we = obj.getParseObject("associated_place");
                            MainActivity.SSIDSTable.put(obj.getString("ssid"), we.getObjectId());
                        }
                        ParseObject.pinAllInBackground(objects);
                        retrivePlaces();
//                    objectsWereRetrievedSuccessfully(objects);
                    } else {
                        objectRetrievalFailed(e);
                    }
                }
            });
        } else {
            gps = new GPSCoordinates(0, 0);
            Log.d("mph", "Not possible to retrieve last place from Google");
        }
    }

    private void retrivePlaces() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Weacon");
        query.whereContainedIn("objectId", MainActivity.SSIDSTable.values());
        query.setLimit(200);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, com.parse.ParseException e) {
                if (e == null) {
                    for (ParseObject obj : objects) {
                        Weacon we = new Weacon(obj);
                        MainActivity.weaconsTable.put(obj.getObjectId(), we);
                    }
                    ParseObject.pinAllInBackground(objects);
//                    objectsWereRetrievedSuccessfully(objects);
                } else {
                    objectRetrievalFailed(e);
                }
            }
        });

    }

    private void objectRetrievalFailed(ParseException e) {
        Log.d("mhp", "eerer " + e.getMessage());
//        Log.d("mhp", "eerer " + e.getMessage());
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
