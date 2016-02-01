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
    protected void processJson(JSONObject json) throws JSONException {
        arrivalTimeText = json.getString("horaprediccionbus1");
        arrivalTimeMins = ExtractMinsFromText(arrivalTimeText);
        color = Color.parseColor(json.getString("color"));
        lineCode = json.getString("servicio");
        destination = json.getString("destino");
        distanceMts = Integer.parseInt(json.getString("distanciabus1"));
        lineCode = json.getString("servicio");
        plate = json.getString("ppubus1");
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
