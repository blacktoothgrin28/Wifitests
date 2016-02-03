package com.herenow.fase1.fetchers;

import com.herenow.fase1.BusStop.NewBusStopSantiago;

import org.jsoup.Connection;

import java.util.ArrayList;

import parse.WeaconParse;
import util.MultiTaskCompleted;
import util.myLog;

/**
 * Created by Milenko on 31/01/2016.
 */
public class fetchParadaSantiago extends notificationFetcher {
    public fetchParadaSantiago(MultiTaskCompleted listener, WeaconParse we) {
        super(listener, we);
    }

    @Override
    protected ArrayList processResponse(Connection.Response response) {
        if (response == null) return null;

        myLog.add("the response of the fetcher is " + response.body(), "aut");
        NewBusStopSantiago busStopSantiago = new NewBusStopSantiago(response.body());
//        ArrayList<BusLineTimes> lineTimes = new ArrayList();
//
//        try {
//            JSONObject j = new JSONObject(response.body());
//            JSONArray jsonArray = j.getJSONObject("servicios").getJSONArray("item");
//
//            for (int i = 0; i < jsonArray.length(); i++) {
//                JSONObject jItem = jsonArray.getJSONObject(i);
//                BusLineTimesStgo blt = new BusLineTimesStgo(jItem);
//                lineTimes.add(blt);
//            }
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        return busStopSantiago.embedInArray();
    }
}
