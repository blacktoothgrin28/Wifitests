package com.herenow.fase1.Sapo;

import android.location.Location;
import android.net.wifi.ScanResult;

import com.herenow.fase1.Activities.MainActivity;
import com.herenow.fase1.Position;
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

import parse.HitSapo;
import util.myLog;
import util.parameters;

/**
 * Created by Milenko on 27/07/2015.
 */
public abstract class SAPO2 {

    private static HashMap<String, Integer> oldSpots = new HashMap<>();
    private static HashMap<String, Integer> newSpots;
    private static HashMap<String, Integer> blackList = new HashMap<>();//These should be ignored
    //TODO fill the blacklist from an initial query

    //    private static HashMap<String, String> selectedSpots = new HashMap<>();
    private static List<ScanResult> toBeSaved;
    private static HashMap<String, String> parseHits = new HashMap<>();//bssid, parseHit


    public static void addSPOTS(List<ScanResult> sr) {
        try {
            newSpots = new HashMap<>();
            boolean bingo = false; //when one counter is grater than threshold

            //1. All results in the "new" hash, except those that are already selected
            StringBuilder sb = new StringBuilder("\n**************\n");

            for (ScanResult r : sr) {
                String b = r.BSSID;

                if (true) {//TODO !blackList.containsKey(b)) {
                    if (parseHits.containsKey(b)) {
                        incrementHit(b);
                    } else {
                        if (oldSpots.containsKey(b)) {
                            Integer newVal = oldSpots.get(b) + 1;
                            newSpots.put(b, newVal); //bssid and counter
                            sb.append("  - " + newVal + " | " + r.SSID + " | " + r.BSSID + "\n");
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
            }
            sb.append("*********************");
            app(sb.toString());


            if (bingo) {
                toBeSaved = sr;
                MainActivity.mPos.connect(Position.REASON.SaveSAPO2SPOTS);
            } else {
                oldSpots = newSpots;
            }
        } catch (Exception e) {
            app("---error en sapo addspot:" + e.getMessage());
        }
    }

    /**
     * Call from Position when the new position arrived, to sve in Parse the selected
     *
     * @param loc
     */
    public static void newLocation(Location loc) {
        final ArrayList<HitSapo> hitSapos = new ArrayList<>();

        app("Tenemos estos sr para subir:" + toBeSaved.size());

        for (ScanResult r : toBeSaved) {
//            selectedSpots.put(r.BSSID, "");//it shoulb be the objectId

            final HitSapo hitSapo = new HitSapo();
            hitSapo.setBssid(r.BSSID);
            hitSapo.setSsid(r.SSID);
            hitSapo.setLocation(loc);
            if (oldSpots.containsKey(r.BSSID)) {
                hitSapo.setnhits(oldSpots.get(r.BSSID));
            } else {
                hitSapo.setnhits(1);
            }
//            app("juntando hitSapos para upload:" + hitSapo);
            hitSapos.add(hitSapo);
        }
        app("Tenemos estos hitSapos para subir:" + hitSapos.size());
        //Save them together
        ParseObject.saveAllInBackground(hitSapos, new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    app("Se han subidos hitSapos, nos traemos los objsid ");
                    ParseQuery<HitSapo> query = ParseQuery.getQuery(HitSapo.class);
                    query.orderByDescending("createdAt");
                    query.setLimit(hitSapos.size());
                    query.findInBackground(new FindCallback<HitSapo>() {
                        @Override
                        public void done(List<HitSapo> list, ParseException e) {
                            if (e == null) {
                                StringBuilder sb = new StringBuilder("****** adding to hash objects\n");
                                for (HitSapo h : list) {
                                    sb.append("    " + h.getBSSID() + " | " + h.getObjectId() + " | created: " + h.getCreatedAt() + "\n");
                                    parseHits.put(h.getBSSID(), h.getObjectId());
                                    h.pinInBackground(parameters.pinSapo);
                                }
                                sb.append("*******");
                                app(sb.toString());

                            } else {
                                app("---ERROR retrieveing obids" + e.getMessage());
                            }

                        }
                    });
                } else {
                    app("---ERROR NO se han subilos hitSapos errer = " + e.getMessage());
                }
            }
        });
//        ParseObject.saveAllInBackground(hitSapos, new SaveCallback() {
//            @Override
//            public void done(ParseException e) {
//                if (e == null) {
//                    app("se han subidos hitSapos ");
//                } else {
//                    app("---ERROR NO se han subilos hitSapos errer = " + e.getMessage());
//                }
//            }
//        });

    }

    private static void incrementHit(final String bssid) {
        if (parseHits.containsKey(bssid)) {
//            HitSapo hitSapoBuff = parseHits.get(bssid);
            String id = parseHits.get(bssid);
            app("vamos a incrmentar en vacio, a ver: " + id);
            HitSapo hitSapoBuff = ParseObject.createWithoutData(HitSapo.class, id);
            hitSapoBuff.incrementMe();

        } else { //Shoudn't happen
            app("este NO lo habimos puesto en parse aun: " + bssid);
            ParseQuery<HitSapo> query = ParseQuery.getQuery(HitSapo.class);
            query.whereEqualTo("bssid", bssid);
            query.fromPin("SAPO2");
            query.findInBackground(new FindCallback<HitSapo>() {
                @Override
                public void done(List<HitSapo> list, ParseException e) {
                    int n = list.size();
                    if (n == 0) {
                        app("no lo encontramos" + bssid);
                    } else if (n == 1) {
                        app("hay solo uno, lo incrementamos" + bssid);
                        ParseObject hit = list.get(0);
//                        HitSapo hit = list.get(0);
                        app("este es el hit que recuperamos para " + bssid + "|" + hit + " obid=" + hit.getObjectId());
//                        app("este es el hit que recuperamos para " + bssid + "|" + hit + " obid=" + hit.getObjectId());
//                        parseHits.put(bssid, hit.getObjectId());
//                        hit.incrementMe();

                    } else {
                        app("hay muchos de este " + bssid);
                    }
                }
            });
        }
    }

    private static void app(String msg) {
        myLog.add(msg, "SAPOA");
    }

    public static void uploadIfRequired() {
        try {
            //Anything to upload?
            ParseQuery<HitSapo> query = ParseQuery.getQuery(HitSapo.class);
            query.fromPin("SAPO2");
            query.findInBackground(new FindCallback<HitSapo>() {
                @Override
                public void done(final List<HitSapo> localhits, ParseException e) {
                    if (e == null) {
                        if (localhits.size() == 0) {
                            app("Nothing to upload");
                        } else {

                            //just to log
                            StringBuilder sb = new StringBuilder("+++hit subidos que actualizaremos:\n");
                            for (HitSapo h : localhits) {
                                sb.append("  ->" + h + "\n");
                            }
                            sb.append("+++++++");
                            app(sb.toString());


                            // Check last upload
                            ParseQuery<HitSapo> query = ParseQuery.getQuery(HitSapo.class);

                            query.orderByDescending("updatedAt");
                            query.setLimit(1);
                            query.findInBackground(new FindCallback<HitSapo>() {
                                @Override
                                public void done(List<HitSapo> list, ParseException e) {
                                    Date lastUpdate = list.get(0).getUpdatedAt();
                                    app("the las update was at t:" + lastUpdate);

                                    int diff = Math.round((System.currentTimeMillis() - lastUpdate.getTime()) / (1000 * 60)); //in mins

                                    app("Apparently have passed mins:" + diff + "/" + parameters.minTimeForUpdates);
                                    if (diff > parameters.minTimeForUpdates || localhits.size() > 10) { //Actually update if has passed enough time
                                        app("uploading: " + localhits.size());

                                        ParseObject.saveAllInBackground(localhits, new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                if (e == null) {
                                                    app("hits saved sucefully");
                                                } else {
                                                    app("---ERROR uploading hits" + e.getMessage());
                                                }
                                            }
                                        });
                                        ParseObject.unpinAllInBackground(parameters.pinSapo, new DeleteCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                if (e == null) {
                                                    app("unpinned el sapo");
                                                } else {
                                                    app("ello" + e.getMessage());
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