package com.herenow.fase1;

import android.location.Location;
import android.net.wifi.ScanResult;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import util.AppendLog;
import util.Hit;
import util.parameters;

/**
 * Created by Milenko on 27/07/2015.
 */
public abstract class SAPO2 {

    private static HashMap<String, Integer> oldSpots = new HashMap<>();
    private static HashMap<String, Integer> newSpots;
    //    private static HashMap<String, String> selectedSpots = new HashMap<>();
    private static List<ScanResult> toBeSaved;
    private static HashMap<String, String> objIds = new HashMap<>();//bssid, objid


    public static void addSPOTS(List<ScanResult> sr) {
        newSpots = new HashMap<>();
        boolean bingo = false; //when one counter is grater than threshold

        //1. All results in the "new" hash, except those that are already selected
        StringBuilder sb = new StringBuilder("\n**************\n");

        for (ScanResult r : sr) {
            String b = r.BSSID;
            if (objIds.containsKey(b)) {
                incrementHit(b);
            } else {
                if (oldSpots.containsKey(b)) {
                    Integer newVal = oldSpots.get(b) + 1;
                    newSpots.put(b, newVal); //bssid and counter
                    sb.append("  - " + +newVal + " | " + r.SSID + newVal + "\n");
                    if (newVal > parameters.hitRepetitions) { //set to 1 for testing
                        app("*****BINGO*** we have repetitions: " + r.SSID);
                        bingo = true;
                        break;
                    }
                } else {
                    newSpots.put(b, 1); //bssid and counter
                    sb.append("  - 1 | " + r.SSID + "\n");
                }
            }
        }
        sb.append("*********************");
        app(sb.toString());


        if (bingo) {
            toBeSaved = sr;
            MainActivity.mPos.connect(Position.REASON.SaveSAPO2SPOTS);
        } else {
            oldSpots = newSpots;
        }
    }

    /**
     * Call from Position when the new position arrived, to sve in Parse the selected
     *
     * @param loc
     */
    public static void newLocation(Location loc) {
        final ArrayList<Hit> hits = new ArrayList<>();

        app("Tenemos estos sr para subir:" + toBeSaved.size());

        for (ScanResult r : toBeSaved) {
//            selectedSpots.put(r.BSSID, "");//it shoulb be the objectId

            final Hit hit = new Hit();
            hit.setBssid(r.BSSID);
            hit.setSsid(r.SSID);
            hit.setLocation(loc);
            if (oldSpots.containsKey(r.BSSID)) {
                hit.setnhits(oldSpots.get(r.BSSID));
            } else {
                hit.setnhits(1);
            }
//            app("juntando hits para upload:" + hit);
            hits.add(hit);
        }
        app("Tenemos estos hits para subir:" + hits.size());
        //Save them together
        ParseObject.saveAllInBackground(hits, new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    app("Se han subidos hits, nos traemos los objsid ");
                    ParseQuery<Hit> query = ParseQuery.getQuery(Hit.class);
                    query.orderByDescending("createdAt");
                    query.setLimit(hits.size());
                    query.findInBackground(new FindCallback<Hit>() {
                        @Override
                        public void done(List<Hit> list, ParseException e) {
                            if (e == null) {
                                StringBuilder sb = new StringBuilder("****** adding to hash objects\n");
                                for (Hit h : list) {
                                    sb.append(h.getBSSID() + " | " + h.getObjectId() + " | created: " + h.getCreatedAt() + "\n");
                                    objIds.put(h.getBSSID(), h.getObjectId());
                                }
                                sb.append("*******");
                                app(sb.toString());

                            } else {
                                app("---ERROR retrieveing obids" + e.getMessage());
                            }

                        }
                    });
                } else {
                    app("---ERROR NO se han subilos hits errer = " + e.getMessage());
                }
            }
        });
//        ParseObject.saveAllInBackground(hits, new SaveCallback() {
//            @Override
//            public void done(ParseException e) {
//                if (e == null) {
//                    app("se han subidos hits ");
//                } else {
//                    app("---ERROR NO se han subilos hits errer = " + e.getMessage());
//                }
//            }
//        });

    }

    private static void incrementHit(final String bssid) {
        if (objIds.containsKey(bssid)) {
            String id = objIds.get(bssid);

            app("vamos a incrmentar en vacio, a ver: " + id);
            Hit hitBuff = ParseObject.createWithoutData(Hit.class, id);
            hitBuff.incrementMe();

        } else { //Shoudn't happen
            app("este NO lo habimos puesto en parse aun: " + bssid);
            ParseQuery<Hit> query = ParseQuery.getQuery(Hit.class);
            query.whereEqualTo("bssid", bssid);
            query.fromPin("SAPO2");
            query.findInBackground(new FindCallback<Hit>() {
                @Override
                public void done(List<Hit> list, ParseException e) {
                    int n = list.size();
                    if (n == 0) {
                        app("no lo encontramos" + bssid);
                    } else if (n == 1) {
                        app("hay solo uno, lo incrementamos" + bssid);
                        ParseObject hit = list.get(0);
//                        Hit hit = list.get(0);
                        app("este es el hit que recuperamos para " + bssid + "|" + hit + " obid=" + hit.getObjectId());
//                        app("este es el hit que recuperamos para " + bssid + "|" + hit + " obid=" + hit.getObjectId());
//                        objIds.put(bssid, hit.getObjectId());
//                        hit.incrementMe();

                    } else {
                        app("hay muchos de este " + bssid);
                    }
                }
            });
        }
    }

    private static void app(String msg) {
        AppendLog.appendLog(msg, "SAPOA");
    }

    public static void uploadIfRequired() {
        try {
            //Anything to upload?
            ParseQuery<Hit> query = ParseQuery.getQuery(Hit.class);
            query.fromPin("SAPO2");
            query.findInBackground(new FindCallback<Hit>() {
                @Override
                public void done(final List<Hit> localhits, ParseException e) {
                    if (e == null) {
                        if (localhits.size() == 0) {
                            app("Nothing to upload");
                        } else {

                            //just to log
                            StringBuilder sb = new StringBuilder("+++hits locales que subiremos:\n");
                            for (Hit h : localhits) {
                                sb.append("->" + h + "\n");
                            }
                            sb.append("+++++++");
                            app(sb.toString());


                            // Check last upload
                            ParseQuery<Hit> query = ParseQuery.getQuery(Hit.class);

                            query.orderByDescending("updatedAt");
                            query.setLimit(1);
                            query.findInBackground(new FindCallback<Hit>() {
                                @Override
                                public void done(List<Hit> list, ParseException e) {
                                    Date lastUpdate = list.get(0).getUpdatedAt();
                                    app("the las update was at t:" + lastUpdate);

                                    int diff = Math.round((System.currentTimeMillis() - lastUpdate.getTime()) / (1000 * 60)); //in mins

                                    app("apparrently have been passed mins:" + diff);
                                    if (diff > parameters.minTimeForUpdates) { //Actually update if has passed enough time
                                        app("uploading: " + localhits.size());

                                        ParseObject.saveAllInBackground(localhits, new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                if (e == null) {
                                                    app("hits saved sucefully");
                                                    ParseObject.unpinAllInBackground(localhits, new DeleteCallback() {
                                                        @Override
                                                        public void done(ParseException e) {
                                                            if (e == null) {
                                                                app("OK: hits unpinned");
                                                            } else {
                                                                app("--_ERROR, cannot unpin hits: " + e.getMessage());
                                                            }
                                                        }
                                                    });
                                                } else {
                                                    app("---ERROR uploadin hits" + e.getMessage());
                                                }
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    } else {
                        app("---error" + e.getMessage());
                    }
                }
            });
        } catch (Exception e) {
            app("---Not possible to upload:" + e.getMessage());
        }
    }
}