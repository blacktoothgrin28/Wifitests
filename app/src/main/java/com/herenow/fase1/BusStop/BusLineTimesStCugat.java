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
    protected void processJson(JSONObject json) throws JSONException {
        arrivalTimeMins = json.getInt("arrivalTime") / 60;
        lineCode = json.getString("lineCode");
        roundedTime = json.getString("roundedArrivalTime");
        stopCode = String.valueOf(json.getInt("stopCode"));
    }
}
