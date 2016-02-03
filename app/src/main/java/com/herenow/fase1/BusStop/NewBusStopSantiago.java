package com.herenow.fase1.BusStop;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import util.myLog;

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

            JSONObject services = json.getJSONObject("servicios");
            JSONArray items = services.getJSONArray("item");
            myLog.add("tenemos algunso servicios de bus:" + items.length(), "aut");

            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);
                if (!(item.getString("codigorespuesta").equals("00") || item.getString("codigorespuesta").equals("01")))
                    continue;
                myLog.add("oneitem: " + item.toString(), "aut");
                NewBusLineSantiago line = new NewBusLineSantiago(item);
                arr.add(line);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            myLog.add("--error en create array de santaugao de chile " + e.getLocalizedMessage());
        }
        return arr;
    }
}
