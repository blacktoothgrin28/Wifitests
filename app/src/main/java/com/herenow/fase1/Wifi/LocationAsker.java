package com.herenow.fase1.Wifi;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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

    LocationCallback mLocationCallback;
    Context mContext;
    GoogleApiClient mGoogleApiClient;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private double mPrecision;

    public LocationAsker(Context ctx, final LocationCallback locationCallback) {
        mContext = ctx;
        mLocationCallback = locationCallback;
        buildGoogleApiClient();
    }

    public LocationAsker(Context ctx, final LocationCallback locationCallback, final double accuracyNeeded) {
        this(ctx, locationCallback);
        myLog.add("entrando en locationascker con precisio", "aut");

        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                myLog.add("ha cambiad loa localcixzacion , dentro de la precision. Tiene vleovidad:" + location.hasSpeed(), "aut");
//                if (location.hasSpeed()) return;
                if (location.hasAccuracy()) {
                    double accuracy = location.getAccuracy();
                    if (accuracy < accuracyNeeded) {

                        String s = "estamos a con precision mejor de 5 mtes " + accuracy;
                        myLog.add(s, "aut");

//                        Toast.makeText(mContext, s, Toast.LENGTH_SHORT).show();
//                        mLocationCallback.LocationReceived(new GPSCoordinates(location), accuracy);
                        removerListener(location);
                    } else {
                        String text = "estamos a con precision peor de 5 mtes " + accuracy;
                        myLog.add(text, "aut");
//                        Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                myLog.add("ha camnbiado del statusl de location: provider" + provider + "|stauss" + status, "aut");
            }

            @Override
            public void onProviderEnabled(String provider) {
                myLog.add("encendido el provider:" + provider, "aut");
            }

            @Override
            public void onProviderDisabled(String provider) {
                myLog.add("apagado el proivder" + provider, "aut");
            }
        };

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }


    private void removerListener(Location location) {
        locationManager.removeUpdates(locationListener);
        mLocationCallback.LocationReceived(new GPSCoordinates(location), location.getAccuracy());
        mGoogleApiClient.disconnect();
    }

    public void DoSomethingWithPosition(Context context) {

        mContext = context;
        buildGoogleApiClient();
    }

    /**
     * @param context
     * @param precision in meters
     */
    public void DoSomethingWithPosition(Context context, double precision) {
        mPrecision = precision;
        DoSomethingWithPosition(context);
    }


    @Override
    public void onConnected(Bundle bundle) {
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        this.mGoogleApiClient.disconnect();

        if (mLastLocation == null) {
            myLog.add("Last location is null");
            mLocationCallback.LocationReceived(new GPSCoordinates(41.474722, 2.086667));
        } else {
            GPSCoordinates gps = new GPSCoordinates(mLastLocation);
            myLog.add("Last location is: " + gps);
            mGoogleApiClient.disconnect();
            mLocationCallback.LocationReceived(gps);
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
