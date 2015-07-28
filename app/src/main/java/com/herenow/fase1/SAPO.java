package com.herenow.fase1;

import android.location.Location;
import android.net.wifi.ScanResult;

import com.parse.DeleteCallback;
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
import util.parameters;

/**
 * Created by Milenko on 18/07/2015.
 */
abstract class SAPOS {
    private static final String t_SAPO_HITS_USERS = "SAPO_hits_users";
    private static final String col_TypeOfLocation = "typeOfLocation";
    public static ParseGeoPoint loc;
    static boolean someOneHasRealLocation = false;
    static boolean someOneHasCityLocation = false;
    static boolean someOneIsPopular = false;
    private static HashMap<String, ParseObject> registeredBSSID = new HashMap<>(); //to store the bssids already seen in this session {bssd,parse spot ssid obj}
    private static boolean realLocationTaken;
    private static boolean locationIsNew = false;
    private static float locprec;
    private static ParseGeoPoint locationToInduce;
    private static List<ScanResult> srTobeUpdated;
    private static typeLocation currentLocationType = typeLocation.CITY;

    public static void addSPOTS(final List<ScanResult> sr) {
        someOneHasRealLocation = false;
        someOneHasCityLocation = false;
        someOneIsPopular = false;
        currentLocationType = typeLocation.CITY;

        try {

            //1. check if some of them are in parse (pinned)
            for (ScanResult r : sr) {
                registerHit(r);
            }

            // Induce Location
            if (someOneHasRealLocation && someOneHasCityLocation) {
                updateLocations(sr);
            }

            if (someOneIsPopular) {
                askLocation(sr);
            }

//            locationIsNew = false;

        } catch (Exception e) {
            app("---ERROR en addSPOTS: " + e.getMessage());
        }
    }

    //TODO, clear this hash when the people moves, when re do the parsequery of sapoplaces
//    private static HashMap<String, Integer> repeteadBSSID = new HashMap<>(); //to store the spots that repeant continuosly n times
//    private static HashMap<String, Integer> repeteadBSSIDnew = new HashMap<>();// to store the spots that repeant continuosly n times
//    private static int ihit = 0;

//
//    public static void Initialize(){
//        //Load the local places in local
//        ParseQuery<ParseObject> query = ParseQuery.getQuery("SSIDS");
//        query.whereWithinKilometers("GPS", new ParseGeoPoint(gps.getLatitude(), gps.getLongitude()), 5);
//        query.setLimit(700);
//    }

    private static void askLocation(List<ScanResult> sr) {
        app("***updating with real location, cause one is popular");
        MainActivity.mPos.connect(Position.REASON.JustUpdatLocation);
        srTobeUpdated = sr;
    }

    private static void updateLocations(List<ScanResult> sr) throws ParseException {
        ParseGeoPoint location = locationToInduce;
        typeLocation locationType = currentLocationType;

        updateSpotsLocations(sr, location, locationType);
    }

    private static void updateSpotsLocations(List<ScanResult> sr, ParseGeoPoint location, typeLocation locationType) throws ParseException {
        updateSpotsLocations(sr, location, locationType, 1000);
    }

    private static void updateSpotsLocations(List<ScanResult> sr, ParseGeoPoint location, typeLocation locationType, double accu) throws ParseException {
        for (final ScanResult r : sr) {
            ParseObject spot = getSpot(r);

            if (spot.getInt("typeOfLocation") == typeLocation.CITY.ordinal()) {
                spot.put("GPS", location);
                spot.put("typeOfLocation", locationType);
                spot.put("GPS_error", accu);
                spot.pinInBackground("SAPO", new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                app(" SPOT updated with induced loc:" + r.SSID);
                            }
                        }
                );
            }
        }
    }

    private static void registerHit(final ScanResult r) {

        try {
            //1. SPOT
            ParseObject spot = getSpot(r); //bring or create
            spot.increment("nhits");
            spot.pinInBackground("SAPO");

            //2. Hits
            ParseObject hit = getHits(spot);
            hit.increment("nhits");

            hit.pinInBackground("SAPO", new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        app("   Saved the Hit for" + r.SSID);
                    } else {
                        app("---ERROR in parse, when pinning Hit" + e.getMessage());
                    }
                }
            });

        } catch (ParseException e) {
            app("---ERROR" + e.getMessage());
        }

    }

    private static ParseObject getHits(ParseObject spot) throws ParseException {
        final String name = spot.getString("ssid");

        //1. check if exists

        app("++Checking Hit with for spot: " + name);
        ParseQuery<ParseObject> query = ParseQuery.getQuery(t_SAPO_HITS_USERS);
        query.whereEqualTo("ssid", spot);
        query.fromPin("SAPO");

        List<ParseObject> hits = query.find();
        int n = hits.size();
        ParseObject hit;

        if (n == 0) {
            app("   there are no hits for :" + name + "| Have to create it");

            hit = new ParseObject(t_SAPO_HITS_USERS);
            hit.put("ssid", spot);
            hit.put("user", ParseUser.getCurrentUser());
            hit.put("nhits", 1);

        } else if (n == 1) {
            app("   It is present in Parse: gonna update");
            hit = hits.get(0);
            hit.increment("nhits");
        } else { //too many

            app("   It is several times: " + n + ". Please Remove duplicates of " + name);
            hit = null;
        }

        return hit;
    }

    private static ParseObject getSpot(ScanResult r) throws ParseException {
        //1. check if exists

        app("**Checking SPOT with bssid = " + r.SSID);
        ParseQuery<ParseObject> query = ParseQuery.getQuery("SAPO_SSID");
        query.whereEqualTo("bssid", r.BSSID);
        query.fromPin("SAPO");

        List<ParseObject> lista = query.find();
        int n = lista.size();
        ParseObject spot;

        if (n == 0) {
            app("   is not in Parse:" + r.BSSID + "| Have to create it");

            StringBuilder sb = new StringBuilder();
            sb.append("****These are the ssid found in parse with the bssid from the ScanResult (" + r.SSID + " | " + r.BSSID + ")");

            for (ParseObject po : lista) {
                sb.append(po.getString("ssid") + "  |" + po.get("bssid") + "\n");
            }
            app(sb.toString());

            spot = new ParseObject("SAPO_SSID");
            spot.put("ssid", r.SSID);
            spot.put("bssid", r.BSSID);
            ParseGeoPoint point = new ParseGeoPoint(loc.getLatitude(), loc.getLongitude());
            spot.put("GPS", point);
            spot.put(col_TypeOfLocation, currentLocationType.ordinal());
            if (locationIsNew) spot.put("GPS_error", locprec);//In meters
            spot.put("nhits", 0);

        } else if (n == 1) {
            app("   It is present in Parse: gonna update");
            spot = lista.get(0);

            if (!someOneHasCityLocation) {//so far
                if (spot.getInt("typeOfLocation") == typeLocation.CITY.ordinal()) {
                    someOneHasCityLocation = true;

                    if (!someOneIsPopular) {//so far
                        if (spot.getInt("nhits") > parameters.hitRepetitions) {
                            someOneIsPopular = true;
                        }
                    }

                }

            }

            if (!someOneHasRealLocation) {//so far
                if (spot.getInt("typeOfLocation") == typeLocation.MEASURED.ordinal()) {
                    someOneHasRealLocation = true;
                    locationToInduce = spot.getParseGeoPoint("GPS");
                    currentLocationType = typeLocation.INDUCED;
                }
            }


        } else { //too many
            app("   It is several times: " + n + ". Remove duplicates of " + r.BSSID + ". We gonna take the first");
            spot = lista.get(0);
        }

        return spot;
    }

    private static void app(String msg) {
        AppendLog.appendLog(msg, "SAPO");
    }

    public static void downloadSPOTSFromParse() {
        try {
            app("D-1 SPOTS downloading from web");
            ParseQuery<ParseObject> query = ParseQuery.getQuery("SAPO_SSID");
            query.whereWithinKilometers("GPS", loc, 5);
            query.setLimit(890);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> spots, ParseException e) {
                    if (e == null) {
                        app("D-2 **Number of SSIDS retrieved from Parse SAPO:" + spots.size());
                        ParseObject.pinAllInBackground("SAPO", spots);
                    } else {
                        app("D-2 ---ERROR from parse obtienning ssids" + e.getMessage());
                    }

                }
            });
        } catch (Exception e) {
            app("D---Error: failed retrieving SPOTS: " + e.getMessage());
        }
    }

    public static void downloadHitsFromParse() {
        try {
            app("C-1 HITS downloading from web (for this user)");
            ParseQuery<ParseObject> query = ParseQuery.getQuery(t_SAPO_HITS_USERS);
            query.whereWithinKilometers("GPS", loc, 5);
            query.whereEqualTo("user", ParseUser.getCurrentUser());
            query.setLimit(890);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> hits, ParseException e) {
                    if (e == null) {
                        app("C-2**Number of hits retrieved web:" + hits.size());
                        ParseObject.pinAllInBackground("SAPO", hits);
                    } else {
                        app("C-2---ERROR from parse downloadinv hits: " + e.getMessage());
                    }
                }
            });
        } catch (Exception e) {
            app("C---Error: failed retrieving hits: " + e.getMessage());
        }
    }

    public static void setLoc(Location mloc) {
        try {
            loc = new ParseGeoPoint(mloc.getLatitude(), mloc.getLongitude());
            locprec = mloc.getAccuracy();

            updateSpotsLocations(srTobeUpdated, loc, typeLocation.MEASURED, locprec);
        } catch (ParseException e) {
            app("---ERRor in setlocation" + e.getMessage());
        }
    }

    private static void updateOrCreateSpots(List<ScanResult> sr) {
//        ParseObject Hit;
        for (ScanResult r : sr) {
            if (registeredBSSID.containsKey(r.BSSID)) {
                AppendLog.appendLog("The spot " + r.SSID + " is already on Parse", "SAPO");
                updateSPOT(registeredBSSID.get(r.BSSID));
            } else {
                AppendLog.appendLog("the spot " + r.SSID + " is not on Parse", "SAPO");
                createAndSaveSpot(r);
            }
        }
    }

    private static void createAndSaveSpot(ScanResult r) {
        AppendLog.appendLog("   Creating spot ", "SAPO");
        final ParseObject spot = new ParseObject("SAPO_SSID");

        try {
            spot.put("ssid", r.SSID);
            spot.put("bssid", r.BSSID);//SPOT pointer
            ParseGeoPoint point = new ParseGeoPoint(loc.getLatitude(), loc.getLongitude());
            spot.put("GPS", point);
            spot.put(col_TypeOfLocation, currentLocationType.ordinal());
            if (locationIsNew) spot.put("GPS_error", locprec);//In meters
            spot.pinInBackground("SAPO", new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        AppendLog.appendLog("new spot pinned. Creating Hit", "SAPO");
                        CreateHit(spot);
                    } else {
                        AppendLog.appendLog("---ERROR en createSpot: " + e.getMessage(), "SAPO");
                    }
                }
            });
        } catch (Exception e) {
            AppendLog.appendLog("---ERROR creating spot: " + e.getMessage(), "SAPO");
        }
    }

    private static void CreateHit(final ParseObject spot) {
        final ParseObject hit = new ParseObject(t_SAPO_HITS_USERS);
        AppendLog.appendLog("Creating a Hit " + spot.getString("ssid"), "SAPO");

        hit.put("user", ParseUser.getCurrentUser());
        hit.put("ssid", spot);
        hit.put("nhits", 1);
        hit.pinInBackground("SAPO", new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    AppendLog.appendLog("   OK:new Hit pinned", "SAPO");
                    AppendLog.appendLog("   Verify in web: Hit=" + hit.getObjectId() + "" +
                            " spot= " + spot.getString("bssid"), "SAPO");
                } else {
                    AppendLog.appendLog("---ERROR Parse en createAndSaveSpot, no se puro pinnear el Hit: " + e.getMessage(), "SAPO");
                }
            }
        });
    }

    private static void updateSPOT(final ParseObject spot) {

        try {
            AppendLog.appendLog("   Updating spot ", "SAPO");
            spot.increment("nhits");
            //if it is not measured, we update the location
            if (!(spot.getInt(col_TypeOfLocation) == typeLocation.MEASURED.ordinal())) {
                spot.put("GPS", loc);
                spot.put(col_TypeOfLocation, currentLocationType.ordinal());
                if (locationIsNew) spot.put("GPS_error", locprec);//In meters
            }
            spot.pinInBackground("SAPO", new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        AppendLog.appendLog("   UPDATED spot pinned", "SAPO");
                        retrieveHitAndUpdate(spot);
                    } else {
                        AppendLog.appendLog("---ERROR en updateSpot: " + e.getMessage(), "SAPO");
                    }
                }
            });
        } catch (Exception e) {
            AppendLog.appendLog("---ERROR updating spot: " + e.getMessage());
        }
    }

    private static void retrieveHitAndUpdate(final ParseObject spot) {

        try {
            AppendLog.appendLog("**Want to update an existing Hit", "SAPO");

            ParseQuery<ParseObject> innerQuery = ParseQuery.getQuery("SAPO_SSID");
            innerQuery.whereEqualTo("objectId", spot.getObjectId());
            ParseQuery<ParseObject> query = ParseQuery.getQuery(t_SAPO_HITS_USERS);
            query.whereMatchesQuery("user", ParseUser.getQuery());
            query.fromPin("SAPO");
            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> hitList, ParseException e) {
                    AppendLog.appendLog("''''''''Resultado de la query con inner (para traer hits): " + hitList.size(), "SAPO");

                    if (e == null) {
                        AppendLog.appendLog("   ok parse query:retrivedHit", "SAPO");
                        if (hitList.size() == 0) {
                            AppendLog.appendLog("no hits retrieved: we wil create it", "SAPO");
                            CreateHit(spot);
                        } else if (hitList.size() > 1) {
                            AppendLog.appendLog("too many hits retrieved.No one updated. it should de one, and we found: " +
                                    hitList.size() + " | for instance, Hit obID=" + hitList.get(0).getObjectId(), "SAPO");
                        } else {
                            AppendLog.appendLog(" SUCCESS: just one Hit was retrieved", "SAPO");
                            final ParseObject hit = hitList.get(0);
                            //TODO verify the pointer to SSID is ok
                            hit.increment("nhits");
                            hit.pinInBackground("SAPO", new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        AppendLog.appendLog("  Hit updated. Verify its ponters: obId= " + hit.getObjectId(), "SAPO");
                                    } else {
                                        AppendLog.appendLog("---error updating Hit " + e.getMessage(), "SAPO");
                                    }
                                }
                            });
                        }
                    } else {
                        AppendLog.appendLog("---ERROR en retrieveHit: " + e.getMessage(), "SAPO");
                    }


                }
            });
        } catch (Exception e) {
            AppendLog.appendLog("---ERROR en retrieveHitAndUpdate: " + e.getMessage(), "SAPO");
        }


    }

    private static void addSPOTStoHash(List<ParseObject> parseSpots) {
        realLocationTaken = false;
        boolean allWithCityPrecision = true;
        boolean someonePopular = false;
        currentLocationType = typeLocation.CITY;

        if (registeredBSSID.size() > 1000) {
            registeredBSSID.clear();
        }

        try {
            for (ParseObject sp : parseSpots) {
                AppendLog.appendLog("..putting in hash registeredBSSID the parse obj= " + sp.getString("ssid"), "SAPO");
                registeredBSSID.put(sp.getString("bssid"), sp);

                // check if one of them has real GPS, to take it
                if (!realLocationTaken) {
                    if (sp.getInt(col_TypeOfLocation) == typeLocation.MEASURED.ordinal()) {
                        AppendLog.appendLog("One of the spots have real location, so we gonna save induced location to news pots", "SAPO");
                        currentLocationType = typeLocation.INDUCED;
                        loc = sp.getParseGeoPoint("GPS");
                        realLocationTaken = true;
                    }
                }
                if (allWithCityPrecision) {
                    allWithCityPrecision = (sp.getInt(col_TypeOfLocation) == typeLocation.CITY.ordinal());
                }
                if (!someonePopular) {
                    someonePopular = (sp.getInt("nhits") > parameters.hitRepetitions);
                }
            }
            if (!realLocationTaken && allWithCityPrecision && someonePopular) {
                //to assign real gps to these points
                MainActivity.mPos.connect(Position.REASON.JustUpdatLocation);

            }

        } catch (Exception e) {
            app("---ERROR en addSPOTStoHash: " + e.getMessage());
        }

    }

    /**
     * Put inlocal store pinned (SAPO_SPOTS)
     *
     * @param sr
     * @param location
     */
    public static void saveSpotsAndHits(List<ScanResult> sr, Location location) {
        for (ScanResult r : sr) {
            ParseGeoPoint point = new ParseGeoPoint(location.getLatitude(), location.getLongitude());
            saveSPOTandHit(r, point);
        }

    }

    private static void saveSPOTandHit(final ScanResult r, ParseGeoPoint geopoint) {

        //SaveSPOT
        ParseObject ssid = new ParseObject("SAPO_SSID");
        try {
            //1. check if it exists already
//            String obId = doesSpotAlreadyExist(r.BSSID);
            if (true) {
                //we get the object

            } else {//we create the Parsrobject
                ssid.put("ssid", r.SSID);
                ssid.put("bssid", r.BSSID);//SPOT pointer
                ssid.put("GPS", geopoint);
                ssid.put("GPS_error", 200);//In meters
                ssid.put("measured", false);
                ssid.increment("nhits");
                ssid.saveInBackground(new SaveCallback() {
                    public void done(ParseException e) {
                        if (e == null) {
                            // Saved successfully.
                            // put in the hash
//                            registeredBSSID.put(r.BSSID, ssid);
                            //Now upload the Hit
                            AppendLog.appendLog("   --Uploaded one SPOT: " + r.SSID);
//                            saveHit(ssid, r.level);
                        } else {
                            // The save failed.
                            AppendLog.appendLog("Failed when uploading the ssid in SAPO, Parse problem" + e.getMessage());
                        }
                    }
                });
            }


        } catch (Exception e) {
            AppendLog.appendLog("---ERRor en savespotandhit" + e.getMessage());
        }
    }

    public static void uploadAllPinned() {

        uploadPinnedHits();
        uploadPinnedSPOTS();

    }

    private static void uploadPinnedHits() {
        try {
            app("B-1****Uploading hits:entering.....");
            //Retrieve all Hit from local
            ParseQuery<ParseObject> query = ParseQuery.getQuery(t_SAPO_HITS_USERS);
            query.fromPin("SAPO");
            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(final List<ParseObject> hits, ParseException e) {

                    if (e == null) {
                        if (hits.size() > 0) {
                            app("B-2   We have read " + hits.size() + " hits from local parse, to upload");

                            ParseObject.saveAllInBackground(hits, new SaveCallback() {
                                public void done(ParseException e) {
                                    if (e == null) {
                                        // Saved successfully.
                                        app("B-3   OK. All hits saved in BG" + hits.size());
                                        ParseObject.unpinAllInBackground(hits, new DeleteCallback() {
                                            public void done(ParseException e) {
                                                if (e == null) {
                                                    // Saved successfully.
                                                    app("B-4   OK. All hits unpinned in BG: they were: " + hits.size());

                                                } else {
                                                    //Error
                                                    app("B-4---Error: In unpinning hits" + e.getMessage());
                                                }
                                                downloadHitsFromParse();
                                            }
                                        });

                                    } else {
                                        //Error
                                        app("B-3---Error: In saving hits, error from parse: " + e.getMessage());
                                        downloadHitsFromParse();
                                    }
                                }
                            });
                        } else {
                            app("B-2   There is no pinned Hit to upload");
                            downloadHitsFromParse();
                        }
                    } else {
                        app("B-2   ---ERROR de parse en uploadPinnedHits: " + e.getMessage());
                        downloadHitsFromParse();
                    }
                }
            });
        } catch (Exception e) {
            app("B---ERROR uploading pinned hits:");
        }
    }

    private static void uploadPinnedSPOTS() {
        try {
            //Retrieve all SSIDS from local
            ParseQuery<ParseObject> query = ParseQuery.getQuery("SAPO_SSID");
            query.fromPin("SAPO");
            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> spots, ParseException e) {
                    app("A-1 ***Uploading Spots: read from local:." + spots.size());

                    if (spots.size() > 0) {

                        ParseObject.saveAllInBackground(spots, new SaveCallback() {
                            public void done(ParseException e) {
                                if (e == null) {
                                    // Saved successfully.
                                    AppendLog.appendLog("A-2   OK. All ssids saved in BG", "SAPO");
                                    ParseObject.unpinAllInBackground("SAPO", new DeleteCallback() {
                                        public void done(ParseException e) {
                                            if (e == null) {
                                                // Saved successfully.
                                                app("A-3   OK. All ssids unpinned in BG");

                                            } else {
                                                //Error
                                                app("A-3---Error: In unpinning ssids" + e.getMessage());
                                            }
                                            downloadSPOTSFromParse();
                                        }
                                    });
                                } else {
                                    //Error
                                    app("A-2---Error: In uploadpinnedSPOTS , error from parse" + e.getMessage());
                                    downloadSPOTSFromParse();
                                }
                            }
                        });

                    } else {
                        app("A-2   There is no SPOT to upload.");
                    }
                }
            });
        } catch (Exception e) {
            AppendLog.appendLog("1   ---ERROR: when uploading spots:" + e.getMessage());
        }

    }

    private enum typeLocation {CITY, MEASURED, INDUCED,}


}
