package com.herenow.fase1.BusStop;

import org.json.JSONException;
import org.json.JSONObject;

import util.myLog;

/**
 * Created by Milenko on 01/02/2016.
 */
public abstract class BusLineTimes {
    protected String lineCode;
    protected String roundedTime;
    protected int arrivalTimeMins;
    protected String stopCode;

    protected String plate;
    protected String destination;
    protected int color;
    protected int distanceMts;
    protected String arrivalTimeText;


    public BusLineTimes(JSONObject json) {
        try {
            processJson(json);
        } catch (JSONException e) {
            myLog.add("Error pardin json en parada", "aut");
            e.printStackTrace();
        }
    }

    public BusLineTimes(String lineCode, String roundedTime, int arrivalTimeMins, String stopCode) {
        this.lineCode = lineCode;
        this.roundedTime = roundedTime;
        this.arrivalTimeMins = arrivalTimeMins;
        this.stopCode = stopCode;
    }

    @Override
    public String toString() {
        return "BusLineTimes{" +
                "lineCode='" + lineCode + '\'' +
                ", roundedTime='" + roundedTime + '\'' +
                ", arrivalTimeMins=" + arrivalTimeMins +
                ", stopCode='" + stopCode + '\'' +
                '}';
    }

    protected abstract void processJson(JSONObject json) throws JSONException;
}
