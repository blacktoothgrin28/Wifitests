package com.herenow.fase1.BusStop;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import util.GPSCoordinates;
import util.myLog;

/**
 * Created by Milenko on 03/12/2015.
 */
public class ParadaOld {
    GPSCoordinates gps;
    int id;
    String address;
    double distance;
    String reference;
    ArrayList<BusLineTimesStCugat> lineTimes;

    public ParadaOld(JSONObject json) {
        try {
            gps = new GPSCoordinates(json.getDouble("CoordX"), json.getDouble("CoordY"));
            id = json.getInt("Id");
            address = json.getString("Denomination");
            distance = json.getDouble("dist");
            reference = json.getString("reference");
        } catch (JSONException e) {
            myLog.add("problema con json");
        }
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Parada{" +
                "gps=" + gps +
                ", id=" + id +
                ", address='" + address + '\'' +
                ", distance=" + distance +
//                ", reference='" + reference + '\'' +
                '}';
    }

    public ParadaOld updateTimes(String s) throws JSONException {
        String[] partes = s.split("\\}\\,\\{|\\[\\{|\\}\\]");

        lineTimes = new ArrayList();

        for (String parte : partes) {
            if (parte.length() > 3) {
                BusLineTimesStCugat lineTime = new BusLineTimesStCugat(new JSONObject("{" + parte + "}"));
                lineTimes.add(lineTime);
                myLog.add(lineTime.toString());
            }
        }
        return this;
    }
}
