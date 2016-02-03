package com.herenow.fase1.BusStop;

import android.graphics.Color;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Milenko on 03/02/2016.
 */
public class NewBusLineSantiago extends NewBusLine {
    public int color;
    private String destination;

    public NewBusLineSantiago(String lineCode, NewBusStCg bus) {
        super(lineCode, bus);
    }

    public NewBusLineSantiago(JSONObject json) {
        super();

        try {

            if (json.getString("codigorespuesta").equals("01") || json.getString("codigorespuesta").equals("00")) {
                color = Color.parseColor(json.getString("color"));
                lineCode = json.getString("servicio");
                destination = json.getString("destino");

                String arrivalTimeText = json.getString("horaprediccionbus1");
                int arrivalTimeMins = ExtractMinsFromText(arrivalTimeText);
                int distanceMts = Integer.parseInt(json.getString("distanciabus1"));
                String plate = json.getString("ppubus1");

                NewBusSantiago busStgo = new NewBusSantiago(arrivalTimeMins, arrivalTimeText, plate, distanceMts);
                addBus(busStgo);
                if (json.getString("codigorespuesta").equals("00")) { //dos autobuses por línea
                    arrivalTimeText = json.getString("horaprediccionbus2");
                    arrivalTimeMins = ExtractMinsFromText(arrivalTimeText);
                    distanceMts = Integer.parseInt(json.getString("distanciabus2"));
                    plate = json.getString("ppubus2");
                    NewBusSantiago busStgo2 = new NewBusSantiago(arrivalTimeMins, arrivalTimeText, plate, distanceMts);
                    addBus(busStgo2);
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
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
}
