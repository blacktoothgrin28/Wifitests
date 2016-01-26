package com.herenow.fase1;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Milenko on 21/12/2015.
 */
public class LineTime {
    public String lineCode;
    public String roundedTime;
    int arrivalTime;
    int stopCode;

    public LineTime(JSONObject json) throws JSONException {
        arrivalTime = json.getInt("arrivalTime");
        lineCode = json.getString("lineCode");
        roundedTime = json.getString("roundedArrivalTime");
        stopCode = json.getInt("stopCode");
    }

    @Override
    public String toString() {
        return "LineTime{" +
                "arrivalTime=" + arrivalTime +
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
