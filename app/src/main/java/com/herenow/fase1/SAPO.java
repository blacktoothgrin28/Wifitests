package com.herenow.fase1;

import android.location.Location;
import android.net.wifi.ScanResult;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.HashMap;
import java.util.List;

import util.AppendLog;

/**
 * Created by Milenko on 18/07/2015.
 */
public abstract class SAPO {
    private static HashMap<String, ParseObject> registeredBSSID = new HashMap<>(); //to store the bssids already seen in this session {bssd,sapo ssid obj}

    public static void addSSIDS(List<ScanResult> sr, final Location location) {
        AppendLog.appendLog("**************SAPO******SSIDS arrived ");

        for (final ScanResult r : sr) {
            if (registeredBSSID.containsKey(r.BSSID)) {
//                AppendLog.appendLog("--location4 of uploadHit");
                uploadHit(r);
            } else {
                //Check if it is online
                ParseQuery<ParseObject> query = ParseQuery.getQuery("SAPO_SSID");
                query.whereEqualTo("bssid", r.BSSID);
                query.findInBackground(new FindCallback<ParseObject>() {
                    public void done(List<ParseObject> bssidList, ParseException e) {
                        ParseObject sapoSSIDobj;
                        if (e == null) {
                            if (bssidList.size() == 0) { //SSID is not online
                                uploadSSIDandHit(r, location);
                            } else {                    //SSID is online
//                                AppendLog.appendLog("We found online this bssid " + bssidList.size() + " times. Its " + r.SSID);
                                sapoSSIDobj = bssidList.get(0);
                                registeredBSSID.put(r.BSSID, sapoSSIDobj);
//                                AppendLog.appendLog("--location3 of uploadHit");
                                uploadHit(r);
                            }

                        } else {
                            Log.d("score", "Error: " + e.getMessage());//TODO Obtain the objectID del SSID
                        }
                    }
                });
            }
        }
    }

    private static void uploadSSIDandHit(final ScanResult r, Location location) {

//        AppendLog.appendLog("We didn't found online this bssid. Its " + r.SSID);

        //Upload SSID
        final ParseObject ssid = new ParseObject("SAPO_SSID");
        ssid.put("ssid", r.SSID);
        ssid.put("bssid", r.BSSID);//SSID pointer
        ParseGeoPoint point = new ParseGeoPoint(location.getLatitude(), location.getLongitude());
        ssid.put("GPS", point);//TODO get location for this
        ssid.put("GPS_error", location.getAccuracy());//In meters
        ssid.saveInBackground(new SaveCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    // Saved successfully.
                    // put in the hash
                    registeredBSSID.put(r.BSSID, ssid);
                    //Now upload the Hit
                    AppendLog.appendLog("   --Uploaded one SSID: "+ r.SSID);
                    uploadHit(ssid, r.level);
                } else {
                    // The save failed.
                    AppendLog.appendLog("Failed when uploadeing the ssid in SAPO, Parse problem" + e.getMessage());
                }
            }
        });
    }

    private static void uploadHit(ScanResult r) {
        uploadHit(registeredBSSID.get(r.BSSID), r.level);
    }

    private static void uploadHit(ParseObject ssidObject, int level) {
        //Todo accumlate 50?(pin) to save battery
        try {
            ParseObject hit = new ParseObject("SAPO_hit");
//            AppendLog.appendLog("level" + level + " ssid =" + ssidObject + " user =" + ParseUser.getCurrentUser());
            hit.put("level", level);
            hit.put("SSID", ssidObject);//SSID pointer
            hit.put("user", ParseUser.getCurrentUser());

            hit.saveInBackground(new SaveCallback() {
                public void done(ParseException e) {
                    if (e == null) {
                        // Saved successfully.
                        Log.d("mhp", "hit uploaded");
                    } else {
                        //Error
                        Log.d("mhp", "In uploadHit, error from parse" + e.getMessage());
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            Log.d("mhp", "In uploadHit" + e.getMessage());
        }

    }
}
