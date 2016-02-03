package com.herenow.fase1.BusStop;

import org.json.JSONObject;

/**
 * Created by Milenko on 02/02/2016.
 */
public abstract class NewBus {
    protected int arrivalTimeMins;
    protected String arrivalTimeText;

    public NewBus(JSONObject json) {
        createBus(json);
    }

    public NewBus() {

    }

    protected abstract void createBus(JSONObject json);
}