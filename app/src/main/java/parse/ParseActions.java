package parse;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.widget.Toast;

import com.herenow.fase1.Activities.ParseCallback;
import com.herenow.fase1.Notifications.Notifications;
import com.herenow.fase1.Wifi.LogInManagement;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import util.GPSCoordinates;
import util.myLog;
import util.parameters;
import util.stringUtils;

/**
 * Created by Milenko on 25/09/2015.
 */
public abstract class ParseActions {

    private static Context mContext;


    /**
     * create in parse the SSID not already created, an assign the weacon of the bustop.
     * Also register the intensities
     *
     * @param paradaId
     * @param sr
     */
    public static void assignSpotsToWeacon(final String paradaId, final List<ScanResult> sr, final GPSCoordinates gps) {
        final ArrayList<String> macs = new ArrayList<>();
        for (ScanResult r : sr) {
            macs.add(r.BSSID);
        }

        ParseQuery<WeaconParse> query = ParseQuery.getQuery(WeaconParse.class);
        query.whereEqualTo("paradaId", paradaId);
        query.getFirstInBackground(new GetCallback<WeaconParse>() {
            @Override
            public void done(WeaconParse weaconParse, ParseException e) {
                if (e == null) {
                    final String weParadaId = weaconParse.getObjectId();

                    // Create only the new ones
                    ParseQuery<WifiSpot> query = ParseQuery.getQuery(WifiSpot.class);
                    query.whereContainedIn("bssid", macs);
                    query.findInBackground(new FindCallback<WifiSpot>() {
                        @Override
                        public void done(List<WifiSpot> list, ParseException e) {
                            List<String> created = new ArrayList<>();
                            final ArrayList<WifiSpot> newOnes = new ArrayList<>();

                            if (e == null) {
                                myLog.add("Detected: " + sr.size() + " alread created: " + list.size());
                                for (WifiSpot ws : list) {
                                    created.add(ws.getBSSID());
                                }

                                final WeaconParse we = (WeaconParse) ParseObject.createWithoutData("Weacon", weParadaId);
                                for (ScanResult r : sr) {
                                    if (!created.contains(r.BSSID)) {
                                        WifiSpot ws = new WifiSpot(r.SSID, r.BSSID, we, gps.getLatitude(), gps.getLongitude());
                                        newOnes.add(ws);
                                    }
                                }

                                //Upload batch
                                WifiSpot.saveAllInBackground(newOnes, new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            myLog.add("subidos varios wifispots " + newOnes.size());

                                            //create the weMeasured
                                            final ParseObject weMeasured = ParseObject.create("WeMeasured");
                                            weMeasured.put("weacon", we);
                                            weMeasured.saveInBackground(new SaveCallback() {
                                                @Override
                                                public void done(ParseException e) {
                                                    if (e == null) {
                                                        final ArrayList<ParseObject> intiensities = new ArrayList<ParseObject>();
                                                        for (ScanResult r : sr) {
                                                            ParseObject intensity = ParseObject.create("Intensities");
                                                            intensity.put("level", r.level);
                                                            intensity.put("weMeasured", weMeasured);
                                                            intensity.put("ssid", r.SSID);
                                                            intensity.put("bssid", r.BSSID);
                                                            intiensities.add(intensity);
                                                        }
                                                        ParseObject.saveAllInBackground(intiensities, new SaveCallback() {
                                                            @Override
                                                            public void done(ParseException e) {
                                                                if (e == null) {
                                                                    myLog.add("saved several intensities " + intiensities.size());
                                                                    // aumentar el n_scannings del weacon  en uno
                                                                    we.increment("n_scanning");
                                                                    we.saveInBackground(new SaveCallback() {
                                                                        @Override
                                                                        public void done(ParseException e) {
                                                                            if (e == null) {
                                                                                myLog.add("incrementado el we de parada en uno");
                                                                            } else
                                                                                myLog.add("--EERROR incrementado el we de parada en uno " + e.getLocalizedMessage());
                                                                        }
                                                                    });
                                                                } else {
                                                                    myLog.add("---error in saving inteisintes " + e.getLocalizedMessage());
                                                                }
                                                            }
                                                        });
                                                    } else {
                                                        myLog.add("error in creating we measured " + e.getLocalizedMessage());
                                                    }
                                                }
                                            });

                                        } else {
                                            myLog.add("error al subir varios wifispots " + e.getLocalizedMessage());
                                        }
                                    }
                                });
                            } else {
                                myLog.add("errro getting bssids from parse " + e.getLocalizedMessage());
                            }
                        }
                    });
                } else {
                    myLog.add("...error getting the id of busstp " + e.getLocalizedMessage());
                }
            }
        });

    }

    /***
     * get wifispots from parse in a area and pin them the object includes the weacon
     *
     * @param bLocal  if should be queried in local database
     * @param radio   kms
     * @param center  center of queried area
     * @param context
     */

    public static void getSpots(final boolean bLocal, final double radio, final GPSCoordinates center, final Context context) {
        try {
            mContext = context;
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
                        query.setLimit(900);

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
                                                    Toast.makeText(context, "Weacons Loaded", Toast.LENGTH_SHORT).show();
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
//                    LogInManagement.ReportDetectedSpots(spots, mContext);
                    HashSet<WeaconParse> weaconHashSet = new HashSet<>();

                    if (n == 0) {
                        myLog.add("MegaQuery no match", "WE");
                    } else { //There are matches
                        myLog.add("From megaquery we have several matches: " + n, "WE");

                        StringBuilder sb = new StringBuilder("***********\n");
                        for (WifiSpot spot : spots) {
//                            sb.append(spot.toString() + "\n");
                            sb.append(spot.summarizeWithWeacon() + "\n");
                            registerHitSSID(spot);
                            WeaconParse we = spot.getWeacon();

                            weaconHashSet.add(we);
                        }
                        myLog.add(sb.toString(), "WE");
                    }
                    myLog.add("Detected spots: " + spots.size() + " | Different weacons: " + weaconHashSet.size(), "LIM");
                    myLog.add(" " + stringUtils.Listar(weaconHashSet), "LIM");

                    LogInManagement.setNewWeacons(weaconHashSet);

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
        try {
            ParseQuery query = ParseQuery.getQuery("w_hit");
            query.whereEqualTo("user", ParseUser.getCurrentUser());
            query.whereEqualTo("ssid", spot);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> list, ParseException e) {
                    if (list != null) {
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
//                MainActivity.writeOnScreen(sb.toString());
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

    public static void ssidForcedDetection(String ssid, final int secWait) {

        myLog.add("Lanched forced ssid= " + ssid);
        //Query SSID
        ParseQuery<WifiSpot> qs = ParseQuery.getQuery(WifiSpot.class);
        qs.whereEqualTo("ssid", ssid);
        qs.include("associated_place");

        qs.findInBackground(new FindCallback<WifiSpot>() {
            @Override
            public void done(List<WifiSpot> spots, ParseException e) {
                if (e == null) {

                    try {
                        Thread.sleep(1000 * secWait);
                        myLog.add("***FORCED se an recuperado:" + spots.size());
                        WeaconParse we = spots.get(0).getWeacon();
                        Notifications.sendNotificationOLD(we);
                    } catch (Exception e1) {
                        myLog.add("error waiting for launcihgn weacon" + e1);
                    }
                } else {
                    myLog.add("***ERROR PARSE FORCESD  error waiting for launcihgn weacon" + e);

                }
            }
        });
    }


    public static void getParadasFree(FindCallback<WeaconParse> call, ParseGeoPoint center) {
        try {
            myLog.add("getting Paradas Done 2  sant cugat");
            ParseQuery<WeaconParse> query = ParseQuery.getQuery(WeaconParse.class);
            query.whereEqualTo("Type", "bus_stop");
            query.whereDoesNotExist("n_scannings");
            query.whereNear("GPS", center);
            query.setLimit(300);

            query.findInBackground(call);

        } catch (Exception e) {
            myLog.add("----ellooos", e.getLocalizedMessage());
        }

    }
}