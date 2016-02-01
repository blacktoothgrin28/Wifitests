package com.herenow.fase1.BusStop;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import util.GPSCoordinates;
import util.myLog;

/**
 * Created by Milenko on 03/12/2015.
 */
public class Parada {
    GPSCoordinates gps;
    int id;
    String address;
    double distance;
    String reference;
    ArrayList<LineTime> lineTimes;

    public Parada(JSONObject json) {
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

    public Parada updateTimes(String s) throws JSONException {
        String[] partes = s.split("\\}\\,\\{|\\[\\{|\\}\\]");

        lineTimes = new ArrayList();

        for (String parte : partes) {
            if (parte.length() > 3) {
                LineTime lineTime = new LineTime(new JSONObject("{" + parte + "}"));
                lineTimes.add(lineTime);
                myLog.add(lineTime.toString());
            }
        }
        return this;
    }

    class LineTime {
        int arrivalTime;
        String lineCode;
        String roundedTime;
        int stopCode;

        public LineTime(JSONObject json) throws JSONException {
            arrivalTime = json.getInt("arrivalTimeMins");
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
    }
}
