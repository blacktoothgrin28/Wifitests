package com.herenow.fase1.BusStop;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Milenko on 02/02/2016.
 */
public class BusStopStCugat extends BusStopTimes {


    public BusStopStCugat(String response) {
        super(response);
    }

    @Override
    protected HashMap<String, ArrayList<BusLineTimes>> createHash(String response) {
        ArrayList<BusLineTimesStCugat> buses = new ArrayList<>();

        try {
            JSONArray mJsonArray = new JSONArray(response);
            for (int i = 0; i < mJsonArray.length(); i++) {
                JSONObject json = mJsonArray.getJSONObject(i);
                BusLineTimes bus = new BusLineTimesStCugat(json);
//                buses.add(bus);
//                BusLineTimesStCugat bl = new BusLineTimesStCugat(json);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
//
//    @Override
//    protected void createBusStopTimes() {
//        try {
//            ArrayList<BusStCg> buses = new ArrayList<>();
//            for (int i = 0; i < mJsonArray.length(); i++) {
//                JSONObject json = mJsonArray.getJSONObject(i);
//                BusStCg bus = new BusStCg(json);
//                buses.add(bus);
////                BusLineTimesStCugat bl = new BusLineTimesStCugat(json);
//            }
//            busLineTimesHashMap = organizeBuses(buses);
//        } catch (JSONException e) {
//            e.printStackTrace();
//            myLog.add("--error em cretebus");
//        }
//    }
//
//    private HashMap<String, ArrayList<BusLineTimes>> organizeBuses(ArrayList<BusStCg> buses) {
//        ArrayList arr;
//        HashMap<String, ArrayList<BusLineTimes>> tableLines = new HashMap<>();
//
//        if (buses == null) return null;
//
//        for (BusStCg bus : buses) {
//            String lc = bus.lineCode;
//            if (tableLines.containsKey(lc)) {
//                //add a time to the line
//                arr = tableLines.get(lc);
//                arr.add(bus);
//                tableLines.put(lc, arr);
//            } else {
//                //add a line with first time
//                arr = new ArrayList();
//                arr.add(bus);
//                tableLines.put(lc, arr);
//            }
//        }
//        return tableLines;
//    }
//
//    @Override
//    protected String linesSummary() {
//        return null;
//    }
//
//    @Override
//    protected String linesSummaryShort() {
//        return null;
//    }
//
//
//    @Override
//    public ArrayList<BusLineTimes> getLineTimes() {
//        return null;
//    }
//
//    private class BusStCg extends BusLineTimes {
//        public String roundedTime;
//
//        public BusStCg(JSONObject json) {
//            super();
//            try {
//                lineCode = json.getString("lineCode");
//                roundedTime = json.getString("roundedArrivalTime");
//
//                arrivalTimeMins = json.getInt("arrivalTime") / 60;
//                lineCode = json.getString("lineCode");
//                stopCode = String.valueOf(json.getInt("stopCode"));
//                updatedTime = json.getString("updatedTime");
//                arrivalTimeText = json.getString("roundedArrivalTime"); //ej "13 min", "IMMINENT".
//
//                lineState = json.getInt("lineState");
//                routeId = json.getInt("routeId");
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//                myLog.add("error en busstcg");
//            }
//        }
//
//        @Override
//        protected String lineSummary() {
//            return null;
//        }
//
//        @Override
//        protected String shortSummary() {
//            return null;
//        }
//
//        @Override
//        protected void processJson(JSONObject json) throws JSONException {
//
//        }
//    }

}
