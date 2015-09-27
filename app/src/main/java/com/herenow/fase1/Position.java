package com.herenow.fase1;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.herenow.fase1.Activities.MainActivity;
import com.herenow.fase1.Sapo.SAPO2;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.Collection;
import java.util.List;

import parse.ParseActions;
import parse.WeaconParse;
import util.GPSCoordinates;
import util.myLog;
import util.parameters;

/**
 * Created by Milenko on 16/07/2015.
 */
public class Position implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private final static boolean bFromLocalParse = false;
    public Location mLastLocation;
    private String parseClass;
    private Collection<String> obIds;
    private GoogleApiClient mGoogleApiClient;
    private Context context;
    private GPSCoordinates gps;
    private REASON connectionReason;


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
//            mGoogleApiClient.connect();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void connect(REASON reason) {
        connectionReason = reason;
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        myLog.add("Connected to google api. Reason:" + connectionReason);

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        this.mGoogleApiClient.disconnect();

        if (mLastLocation == null) {
            myLog.add("Last location is null");
            return;
        } else {
//            SAPO.loc = new ParseGeoPoint(mLastLocation.getLatitude(), mLastLocation.getLongitude());

//            AppendLog.appendLog("GOT Last location = " + mLastLocation);

        }

        if (connectionReason == REASON.GetWeacons) {
            gps = new GPSCoordinates(mLastLocation);
            ParseActions.getSpots(bFromLocalParse, parameters.radioSpotsQuery, gps);
//            SAPO.downloadSPOTSFromParse();
//            SAPO.downloadHitsFromParse();
        } else if (connectionReason == REASON.SaveSAPOssidswithLocation) {
            //WARN: alguos de los nuevos ssids puede que ya estén. no puedo subir TODOS.
            //MAla cuea, los subo y después resuelvo con script
//            SAPO.saveSpotsAndHits(this.scanResultSSIDS, mLastLocation);//TODO fix this pingpong
        } else if (connectionReason == REASON.AwareOfPlaces) {
            ParseQuery<WeaconParse> query = ParseQuery.getQuery(WeaconParse.class);
            query.fromPin(parameters.pinWeacons);
            query.whereWithinKilometers("GPS", new ParseGeoPoint(mLastLocation.getLatitude(), mLastLocation.getLongitude()), 0.3);
            query.findInBackground(new FindCallback<WeaconParse>() {
                @Override
                public void done(List<WeaconParse> list, ParseException e) {
                    StringBuilder sb = new StringBuilder("**** Places near: " + mLastLocation + "\n");
                    for (WeaconParse we : list) {
                        sb.append(we.getName() + "\n");
                    }
                    sb.append("******");
                    myLog.add(sb.toString(), "places");
                    MainActivity.writeOnScreen(sb.toString());

                }
            });
        } else if (connectionReason == REASON.SetLocationToParseObject) {
            try {
                //Needed: class and objectID(s)

                ParseQuery query = ParseQuery.getQuery(parseClass);
                query.whereContainedIn("objectId", obIds);

                List<? extends ParseObject> parseObjects = null;
                ParseObject.saveAllInBackground(parseObjects, new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            myLog.add("Parse object location has been updated");
                        } else {
                            myLog.add("--- error:Parse object location has been NOT updated " + e.getMessage());
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                myLog.add("---error in Position|onConnected: " + e.getMessage());
            }
        } else if (connectionReason == REASON.JustUpdatLocation) {
//            SAPO.setLoc(mLastLocation);
        } else if (connectionReason == REASON.SaveSAPO2SPOTS) {
            SAPO2.newLocation(mLastLocation);
        }
    }



//    private void retrievePlacesFromParse(final boolean bLocal, Collection weaconsIds) {
////        Log.d("mhp", "retrieving place from local:" + bLocal);
//
//        ParseQuery<ParseObject> query = ParseQuery.getQuery("Weacon");
//        query.whereContainedIn("objectId", weaconsIds);
//        query.setLimit(200);
//
//        if (bLocal) query.fromLocalDatastore();
//        query.findInBackground(new FindCallback<ParseObject>() {
//            @Override
//            public void done(final List<ParseObject> objects, com.parse.ParseException e) {
//                if (e == null) {
//                    AppendLog.appendLog("number of places:" + objects.size());
//                    if (!bLocal)
//                        ParseObject.pinAllInBackground(parameters.pinWeacons, objects, new SaveCallback() {
//                            @Override
//                            public void done(ParseException e) {
//                                if (e == null) {
//                                    AppendLog.appendLog("Ive pinned the places:" + objects.size());
//                                }
//                            }
//                        });
//                    for (ParseObject obj : objects) {
////                        AppendLog.appendLog("converting the parse obj to weacon:"+obj.getString("Name"));
//                        Weacon we = new Weacon(obj);
//                        MainActivity.weaconsTable.put(obj.getObjectId(), we);
//                    }
//                    ParseObject.pinAllInBackground(objects);
//                } else {
//                    AppendLog.appendLog("---Error: failed retrieving places: " + e.getMessage());
//                }
//            }
//        });
//
//    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public enum REASON {GetWeacons, SetLocationToParseObject, SaveSAPOssidswithLocation, SaveSAPO2SPOTS, AwareOfPlaces, JustUpdatLocation}
}
