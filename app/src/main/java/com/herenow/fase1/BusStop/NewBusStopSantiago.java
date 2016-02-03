package com.herenow.fase1.BusStop;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Milenko on 03/02/2016.
 */
public class NewBusStopSantiago extends NewBusStop {
    public String description;

    public NewBusStopSantiago(String response) {
        super(response);
    }

    @Override
    protected ArrayList<NewBusLine> createArray(String response) {
        ArrayList<NewBusLine> arr = new ArrayList<>();

        try {
            JSONObject json = new JSONObject(response);
            stopCode = json.getString("paradero");
            description = json.getString("nomett");
            updateTime = json.getString("fechaprediccion") + "|" + json.getString("horaprediccion");

            JSONArray services = json.getJSONArray("servicios");

            for (int i = 0; i < services.length(); i++) {
                JSONObject item = services.getJSONObject(i);
                NewBusLineSantiago line = new NewBusLineSantiago(item);
                arr.add(line);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return arr;
    }
}
