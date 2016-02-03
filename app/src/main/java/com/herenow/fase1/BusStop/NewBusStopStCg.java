package com.herenow.fase1.BusStop;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Milenko on 02/02/2016.
 */
public class NewBusStopStCg extends NewBusStop {
    public NewBusStopStCg(String response) {
        super(response);
    }

    @Override
    protected ArrayList<NewBusLine> createArray(String response) {
        HashMap<String, NewBusLine> tableLines = new HashMap<>();

        try {
            JSONArray mJsonArray = new JSONArray(response);
            for (int i = 0; i < mJsonArray.length(); i++) {
                JSONObject json = mJsonArray.getJSONObject(i);
                NewBusStCg bus = new NewBusStCg(json);

                if (stopCode == null) { //put this info in the stop, only once
                    stopCode = bus.getStopCode();
                    updateTime = bus.getUpdateTime();
                }

                String lineCode = bus.getLineCode();
                if (!tableLines.containsKey(lineCode)) {
                    NewBusLine busLine = tableLines.get(lineCode);
                    busLine.addBus(bus);
                } else {
                    tableLines.put(lineCode, new NewBusLineStCugat(lineCode, bus));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ArrayList<NewBusLine> arr = new ArrayList<>();
        for (NewBusLine line : tableLines.values()) {
            arr.add(line);
        }
        return arr;
    }
}
