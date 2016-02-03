package com.herenow.fase1.BusStop;

import android.graphics.Color;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Milenko on 01/02/2016.
 */
public class BusLineTimesStgo extends BusLineTimes {


    public BusLineTimesStgo(JSONObject json) {
        super(json);
    }

    public static boolean isInteger(String s) {
        return isInteger(s, 10);
    }

    public static boolean isInteger(String s, int radix) {
        if (s.isEmpty()) return false;
        for (int i = 0; i < s.length(); i++) {
            if (i == 0 && s.charAt(i) == '-') {
                if (s.length() == 1) return false;
                else continue;
            }
            if (Character.digit(s.charAt(i), radix) < 0) return false;
        }
        return true;
    }

    @Override
    protected String lineSummary() {
        StringBuilder sb = new StringBuilder();
        for (Bus bus : buses) {
            sb.append(bus.arrivalTimeMins + " mins ");
        }
        return sb.toString();
    }

    @Override
    protected String shortSummary() {
        return null;
    }

    @Override
    protected void processJson(JSONObject json) throws JSONException {
        color = Color.parseColor(json.getString("color"));
        lineCode = json.getString("servicio");
        destination = json.getString("destino");

        String arrivalTimeText = json.getString("horaprediccionbus1");
        int arrivalTimeMins = ExtractMinsFromText(arrivalTimeText);
        int distanceMts = Integer.parseInt(json.getString("distanciabus1"));
        String plate = json.getString("ppubus1");
        BusStgo busStgo = new BusStgo(arrivalTimeMins, arrivalTimeText, plate, distanceMts);
        addBus(busStgo);


        if (json.getString("codigorespuesta").equals("00")) { //dos autobuses por línea
            arrivalTimeText = json.getString("horaprediccionbus2");
            arrivalTimeMins = ExtractMinsFromText(arrivalTimeText);
            distanceMts = Integer.parseInt(json.getString("distanciabus2"));
            plate = json.getString("ppubus2");
            busStgo = new BusStgo(arrivalTimeMins, arrivalTimeText, plate, distanceMts);
            addBus(busStgo);
        }
    }

    private int ExtractMinsFromText(String arrivalTimeText) {
        String[] words = arrivalTimeText.split(" ");
        int count = 0;
        int i = 0;
        for (String word :
                words) {
            if (isInteger(word)) {
                i += Integer.valueOf(word);
                count++;
            }
        }

        return i / count;
    }

    class BusStgo extends Bus {

        public String plate;
        public int distanceMts;

        public BusStgo(int arrivalTimeMins, String arrivalTimeText, String plate, int distanceMts) {
            this.arrivalTimeMins = arrivalTimeMins;
            this.arrivalTimeText = arrivalTimeText;

            this.plate = plate;
            this.distanceMts = distanceMts;
        }
    }
}
