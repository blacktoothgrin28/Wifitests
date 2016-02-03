package com.herenow.fase1.fetchers;

import com.herenow.fase1.BusStop.NewBusStopStCg;

import org.json.JSONException;
import org.jsoup.Connection;

import java.util.ArrayList;

import parse.WeaconParse;
import util.MultiTaskCompleted;

/**
 * Created by Milenko on 26/01/2016.
 */
public class fetchParadaStCugat extends notificationFetcher {

    public fetchParadaStCugat(MultiTaskCompleted listener, WeaconParse we) {
        super(listener, we);
    }

    @Override
    protected ArrayList processResponse(Connection.Response response) throws JSONException {
        if (response == null) return null;

        NewBusStopStCg busStopStCg = new NewBusStopStCg(response.body());

//
//      String[] partes = s.split("\\}\\,\\{|\\[\\{|\\}\\]");

//        ArrayList<BusLineTimesStCugat> lineTimes = new ArrayList();
//
//        BusStopStCugat parada = new BusStopStCugat(s);

//        for (String parte : partes) {
//            if (parte.length() > 3) {
//                BusLineTimesStCugat lineTime = null;
//                try {
////                    lineTime = new LineTimeStCgOld(new JSONObject("{" + parte + "}"));
//                    lineTime = new BusLineTimesStCugat(new JSONObject("{" + parte + "}"));
//                } catch (JSONException e) {
//                    multiTaskCompleted.OnError(e);
//                }
//                lineTimes.add(lineTime);
//                myLog.add(lineTime.toString());
//            }
//        }
//        myLog.add("updated todas las lineas de la parada:" + mWe.getParadaId(), "fetch");
        return busStopStCg.embedInArray();
    }


}
