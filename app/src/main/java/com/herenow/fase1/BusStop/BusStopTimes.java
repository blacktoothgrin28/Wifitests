package com.herenow.fase1.BusStop;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Milenko on 02/02/2016.
 */
public abstract class BusStopTimes {
    protected String stopCode;
    protected String updatedTime;

    //where is organized the info of lines.
    protected HashMap<String, ArrayList<BusLineTimes>> busLineTimesHashMap = new HashMap<>();


    public BusStopTimes(String response) {
        busLineTimesHashMap = createHash(response);
    }

    /**
     * @param response the response of the fetching
     * @return the table {linecode| arraylist obs buses of this line}
     */
    protected abstract HashMap<String, ArrayList<BusLineTimes>> createHash(String response);


//
//    protected abstract void createBusStopTimes();
//
//    protected abstract String linesSummary();
//
//    protected abstract String linesSummaryShort();


//    protected abstract void createBusStopTimes(JSONObject json);

//    public abstract ArrayList<BusLineTimes> getLineTimes();

}
