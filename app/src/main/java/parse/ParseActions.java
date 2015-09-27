package parse;

import android.net.wifi.ScanResult;

import com.herenow.fase1.Activities.MainActivity;
import com.herenow.fase1.Activities.ParseCallback;
import com.herenow.fase1.Notifications.Notifications;
import com.herenow.fase1.Wifi.LogInManagement;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import util.GPSCoordinates;
import util.myLog;
import util.parameters;

/**
 * Created by Milenko on 25/09/2015.
 */
public abstract class ParseActions {

    /***
     * get wifispots from parse in a area and pin them the object includes the weacon
     *
     * @param bLocal if should be queried in local database
     * @param radio  kms
     * @param center center of queried area
     */
    public static void getSpots(final boolean bLocal, final double radio, final GPSCoordinates center) {
        try {
            //TODO ver si tiene sentido leer los weacons de local
            //1.Remove spots and weacons in local
            myLog.add("retrieving SSIDS from local:" + bLocal + " user: " + ParseUser.getCurrentUser());
            ParseObject.unpinAllInBackground(parameters.pinWeacons, new DeleteCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {

                        //2. Load them
                        ParseQuery<WifiSpot> query = ParseQuery.getQuery(WifiSpot.class);
                        query.whereWithinKilometers("GPS", new ParseGeoPoint(center.getLatitude(), center.getLongitude()), radio);
                        query.include("associated_place");
                        query.setLimit(700);

                        if (bLocal) query.fromLocalDatastore();
                        query.findInBackground(new FindCallback<WifiSpot>() {
                            @Override
                            public void done(List<WifiSpot> spots, ParseException e) {
                                if (e == null) {

                                    //3. Pin them
                                    myLog.add("number of SSIDS Loaded for weacons:" + spots.size());
                                    if (!bLocal)
                                        ParseObject.pinAllInBackground(parameters.pinWeacons, spots, new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                if (e == null) {
                                                    myLog.add("Wecaons pinned ok");

                                                } else {
                                                    myLog.add("---Error retrieving Weacons from web: " + e.getMessage());
                                                }
                                            }
                                        });
                                } else {
                                    myLog.add("---ERROR from parse obtienning ssids" + e.getMessage());
                                }
                            }
                        });
                    } else {
                        myLog.add("---error: " + e.getMessage());
                    }
                }
            });
        } catch (Exception e) {
            myLog.add("---Error: failed retrieving SPOTS: " + e.getMessage());
        }
    }

    public static void CheckSpotMatches(final List<ScanResult> sr, ArrayList<String> bssids, ArrayList<String> ssids) {

        //Query BSSID
        ParseQuery<WifiSpot> qb = ParseQuery.getQuery(WifiSpot.class);
        qb.whereContainedIn("bssid", bssids);
        //Query SSID
        ParseQuery<WifiSpot> qs = ParseQuery.getQuery(WifiSpot.class);
        qs.whereContainedIn("ssid", ssids);
        qs.whereEqualTo("relevant", true);
        //Main Query
        List<ParseQuery<WifiSpot>> queries = new ArrayList<>();
        queries.add(qb);
        queries.add(qs);

        ParseQuery<WifiSpot> mainQuery = ParseQuery.or(queries);
        mainQuery.fromPin(parameters.pinWeacons);
        mainQuery.include("associated_place");

        mainQuery.findInBackground(new FindCallback<WifiSpot>() {

            @Override
            public void done(List<WifiSpot> spots, ParseException e) {
                if (e == null) {
                    int n = spots.size();
                    LogInManagement.ReportDetectedSpots(spots);
                    if (n == 0) {
                        myLog.add("MegaQuery no match", "WE");
                    } else { //There are matches
                        myLog.add("From megaquery we have several matches: " + n, "WE");
                        MainActivity.reportScanning(n, sr.size());

                        StringBuilder sb = new StringBuilder("***********\n");
                        for (WifiSpot spot : spots) {
                            sb.append(spot.toString() + "\n");
                            registerHitSSID(spot);
                            WeaconParse we = spot.getWeacon();

                            //TODO  Log in y log out
                            //send a Notification for each one if has
                            if (Notifications.shouldBeLaunched(we)) {
                                Notifications.sendNotification(we);
                            }
                        }
                        sb.append("**********");
                        myLog.add(sb.toString(), "WE");
                    }
                } else {
                    myLog.addError(this.getClass(), e);
                }
            }
        });
    }

    /***
     * save in Parse that the spot has benn hit
     *
     * @param spot
     */
    public static void registerHitSSID(final WifiSpot spot) {
        // Check if already upladed
        myLog.add(spot.toString(), "HIT");
        try {
            ParseQuery query = ParseQuery.getQuery("w_hit");
            query.whereEqualTo("user", ParseUser.getCurrentUser());
            query.whereEqualTo("ssid", spot);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> list, ParseException e) {
                    if (list.size() == 0) { //create
                        ParseObject hit = new ParseObject("w_hit");
                        hit.put("ssid", spot);
                        hit.put("user", ParseUser.getCurrentUser());
                        hit.put("nhits", 1);
                        hit.saveInBackground();
                    } else {//update
                        ParseObject hit = list.get(0);
                        hit.increment("nhits");
                        hit.saveInBackground();
                    }
                }


            });


        } catch (Exception e) {
            myLog.add("--error en subir hit: " + e.getMessage());
        }
    }

    /**
     * Gives a list of places(google) around and write them on screen
     *
     * @param gps
     * @param maxDistance in kilometeres
     */
    public static void getPlacesAround(final GPSCoordinates gps, double maxDistance) {

        ParseQuery<WeaconParse> query = ParseQuery.getQuery(WeaconParse.class);
        query.fromPin(parameters.pinWeacons);
        query.whereWithinKilometers("GPS", new ParseGeoPoint(gps.getLatitude(), gps.getLongitude()), maxDistance);
        query.findInBackground(new FindCallback<WeaconParse>() {
            @Override
            public void done(List<WeaconParse> list, ParseException e) {
                StringBuilder sb = new StringBuilder("**** Places near: " + gps + "\n");
                for (WeaconParse we : list) {
                    sb.append(we.getName() + "\n");
                }
                sb.append("******");
                myLog.add(sb.toString(), "places");
                MainActivity.writeOnScreen(sb.toString());
            }
        });

    }

    /**
     * @param wCompanyDataObId
     * @param parseCallback
     */
    public static void getCompanyData(String wCompanyDataObId, final ParseCallback parseCallback) {
        try {
            myLog.add("getting Company data");
            ParseQuery<ParseObject> query = ParseQuery.getQuery("CardCompany");
            query.get(wCompanyDataObId);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> list, ParseException e) {
                    myLog.add("recibido CompanyCard de Parse");
                    parseCallback.DatafromParseReceived(list);
                }
            });
        } catch (ParseException e) {
//            myLog.addError(this.getClass(), e);
            parseCallback.OnError(e);
        }

    }

    public static void ssidForcedDetection(String ssid) {

        myLog.add("Lanched forced ssid= " + ssid);
        //Query SSID
        ParseQuery<WifiSpot> qs = ParseQuery.getQuery(WifiSpot.class);
        qs.whereEqualTo("ssid", ssid);
        qs.fromPin(parameters.pinWeacons);
        qs.include("associated_place");

        qs.findInBackground(new FindCallback<WifiSpot>() {
            @Override
            public void done(List<WifiSpot> spots, ParseException e) {
                WeaconParse we = spots.get(0).getWeacon();
                Notifications.sendNotification(we);
            }
        });
    }
}