package com.herenow.fase1.BusStop;

import org.json.JSONObject;

/**
 * Created by Milenko on 03/02/2016.
 */
public class NewBusSantiago extends NewBus {
    public int distanceMts;
    public String plate;

    public NewBusSantiago(JSONObject json) {
        super(json);
    }

    public NewBusSantiago(int arrivalTimeMins, String arrivalTimeText, String plate, int distanceMts) {
        super();
        this.arrivalTimeMins = arrivalTimeMins;
        this.arrivalTimeText = arrivalTimeText;
        this.plate = plate;
        this.distanceMts = distanceMts;
    }

    @Override
    protected void createBus(JSONObject json) {

    }
}
