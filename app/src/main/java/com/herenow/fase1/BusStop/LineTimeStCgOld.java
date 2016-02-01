package com.herenow.fase1.BusStop;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Milenko on 21/12/2015.
 */
public class LineTimeStCgOld {
    public String lineCode;
    public String roundedTime;
    int arrivalTime;
    int stopCode;

    public LineTimeStCgOld(JSONObject json) throws JSONException {
        arrivalTime = json.getInt("arrivalTime");
        lineCode = json.getString("lineCode");
        roundedTime = json.getString("roundedArrivalTime");
        stopCode = json.getInt("stopCode");
    }

    @Override
    public String toString() {
        return "LineTime{" +
                "arrivalTimeMins=" + arrivalTime +
                ", lineCode='" + lineCode + '\'' +
                ", roundedTime='" + roundedTime + '\'' +
                ", stopCode=" + stopCode +
                '}';
    }

    public void setRoundedTime(String roundedTime) {
        this.roundedTime = roundedTime;
    }

    public String summary() {
        return lineCode + ": " + roundedTime;
    }

}
