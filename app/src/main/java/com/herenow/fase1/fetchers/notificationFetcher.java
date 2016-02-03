package com.herenow.fase1.fetchers;

import android.os.AsyncTask;

import org.json.JSONException;
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
    protected MultiTaskCompleted multiTaskCompleted;
    protected WeaconParse mWe;

    public notificationFetcher(MultiTaskCompleted listener, WeaconParse we) {
        super();
        multiTaskCompleted = listener;
        mWe = we;
    }

    public notificationFetcher() {
        super();
    }

    public notificationFetcher setListener(MultiTaskCompleted listener, WeaconParse we) {
        multiTaskCompleted = listener;
        mWe = we;
        return this;
    }

    @Override
    protected ArrayList doInBackground(Void... params) {
        ArrayList elements = null;
        try {
            myLog.add("URL = " + mWe.getFetchingUrl());
            Connection.Response response = getResponse(mWe.getFetchingUrl());
            elements = processResponse(response);
            if (elements == null) return null;
        } catch (JSONException e) {
            e.printStackTrace();
            myLog.add("--error in doin bg in notification fetcher");
        }

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
    protected abstract ArrayList processResponse(Connection.Response response) throws JSONException;

}