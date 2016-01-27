package com.herenow.fase1.fetchers;

import android.os.AsyncTask;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.ArrayList;

import parse.WeaconParse;
import util.MultiTaskCompleted;
import util.myLog;

/**
 * Created by Milenko on 26/01/2016.
 */
public abstract class notificationFetcher extends AsyncTask<Void, Void, ArrayList> {
    protected final MultiTaskCompleted multiTaskCompleted;
    protected WeaconParse mWe;

    public notificationFetcher(MultiTaskCompleted listener, WeaconParse we) {
        super();
        multiTaskCompleted = listener;
        mWe = we;
    }

    @Override
    protected ArrayList doInBackground(Void... params) {
        myLog.add("URL = " + mWe.getFetchingUrl());
        Connection.Response response = getResponse(mWe.getFetchingUrl());
        ArrayList elements = processResponse(response);
        if (elements == null) return null;

        return elements;
    }

    private Connection.Response getResponse(String url) {
        Connection.Response response = null;

        try {
            myLog.add("starting fetching: " + mWe.getName(), "fetch");

            response = Jsoup.connect(url)
                    .ignoreContentType(true)
                    .referrer("http://www.google.com")
                    .timeout(5000)
                    .followRedirects(true)
                    .execute();
        } catch (IOException e) {
            multiTaskCompleted.OnError(e);
        }

        if (response == null) return null;
        return response;
    }

    @Override
    protected void onPostExecute(ArrayList elements) {
        super.onPostExecute(elements);
        mWe.setFetchingResults(elements);
        multiTaskCompleted.OneTaskCompleted();
    }

    /**
     * This transforms the response into an array of objects
     *
     * @param response
     * @return
     */
    protected abstract ArrayList processResponse(Connection.Response response);

}