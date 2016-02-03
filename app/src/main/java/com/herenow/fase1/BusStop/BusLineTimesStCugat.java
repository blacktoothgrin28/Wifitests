package com.herenow.fase1.BusStop;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Milenko on 01/02/2016.
 */
public class BusLineTimesStCugat extends BusLineTimes {

    public BusLineTimesStCugat(JSONObject json) {
        super(json);
    }

    @Override
    protected String lineSummary() {
//TODO
        return null;
    }

    @Override
    protected String shortSummary() {
        //TODO
        return null;
    }

    @Override
    protected void processJson(JSONObject json) throws JSONException {

        arrivalTimeMins = json.getInt("arrivalTime") / 60;
        lineCode = json.getString("lineCode");
//        stopCode = String.valueOf(json.getInt("stopCode"));
//        updatedTime = json.getString("updatedTime");
//        arrivalTimeText = json.getString("roundedArrivalTime"); //ej "13 min", "IMMINENT".
//
//        lineState = json.getInt("lineState");
//        routeId = json.getInt("routeId");

//        roundedTime = json.getString("roundedArrivalTime");
    }
}
