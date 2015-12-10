package com.herenow.fase1.Wifi;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.herenow.fase1.Activities.LocationCallback;

import util.GPSCoordinates;
import util.myLog;

/**
 * Created by Milenko on 25/09/2015.
 */
public class LocationAsker implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    LocationCallback locationCallback;
    Context mContext;
    GoogleApiClient mGoogleApiClient;

    public void DoSomethingWithPosition(LocationCallback locationCallback, Context context) {

        this.locationCallback = locationCallback;
        mContext = context;
        buildGoogleApiClient();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        this.mGoogleApiClient.disconnect();

        if (mLastLocation == null) {
            myLog.add("Last location is null");
            locationCallback.LocationReceived(new GPSCoordinates(41.474722, 2.086667));
        } else {
            GPSCoordinates gps = new GPSCoordinates(mLastLocation);
            myLog.add("Last location is: " + gps);
            mGoogleApiClient.disconnect();
            locationCallback.LocationReceived(gps);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        myLog.add("Connection to google location was suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        myLog.add("Connection to google location has failed: " + connectionResult);

    }

    protected synchronized void buildGoogleApiClient() {
        try {
            mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            mGoogleApiClient.connect();

        } catch (Exception e) {
            myLog.addError(this.getClass(), e);
        }
    }
}
