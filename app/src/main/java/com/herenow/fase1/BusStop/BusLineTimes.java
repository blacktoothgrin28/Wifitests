package com.herenow.fase1.BusStop;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import util.myLog;

/**
 * Created by Milenko on 01/02/2016.
 */
public abstract class BusLineTimes {
    protected String lineCode;

    protected String destination;
    protected int color;
    //todoeliminar
    protected String arrivalTimeText;
    protected int arrivalTimeMins;
    protected String roundedTime;
    ArrayList<Bus> buses = new ArrayList<>();
    //END eliminar

    public BusLineTimes(JSONObject json) {
        try {
            processJson(json);
        } catch (JSONException e) {
            myLog.add("Error pardin json en parada", "aut");
            e.printStackTrace();
        }
    }

    public BusLineTimes() {
    }

    protected abstract String lineSummary();

    protected abstract String shortSummary();

    protected void addBus(Bus bus) {
        buses.add(bus);
    }

    protected abstract void processJson(JSONObject json) throws JSONException;

    protected abstract class Bus {
        protected String arrivalTimeText;
        protected int arrivalTimeMins;
    }
}
