package com.herenow.fase1.fetchers;

import com.herenow.fase1.LineTime;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;

import java.util.ArrayList;

import parse.WeaconParse;
import util.MultiTaskCompleted;
import util.myLog;

/**
 * Created by Milenko on 26/01/2016.
 */
public class fetchParadaStCugat extends myFetcher {

    public fetchParadaStCugat(MultiTaskCompleted listener, WeaconParse we) {
        super(listener, we);
    }

    @Override
    protected ArrayList processResponse(Connection.Response response) {
        if (response == null) return null;

        String s = response.body();
        String[] partes = s.split("\\}\\,\\{|\\[\\{|\\}\\]");

        ArrayList lineTimes = new ArrayList();

        for (String parte : partes) {
            if (parte.length() > 3) {
                LineTime lineTime = null;
                try {
                    lineTime = new LineTime(new JSONObject("{" + parte + "}"));
                } catch (JSONException e) {
                    multiTaskCompleted.OnError(e);
                }
                lineTimes.add(lineTime);
                myLog.add(lineTime.toString());
            }
        }
        myLog.add("updated todas las lineas de la parada:" + mWe.getParadaId(), "fetch");
        return lineTimes;
    }
}
