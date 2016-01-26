package com.herenow.fase1.fetchers;

import org.jsoup.Connection;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;

import parse.WeaconParse;
import util.MultiTaskCompleted;
import util.myLog;

/**
 * Created by Milenko on 26/01/2016.
 */
public class fetchEsade extends myFetcher {
    public fetchEsade(MultiTaskCompleted listener, WeaconParse we) {
        super(listener, we);
    }

    @Override
    protected ArrayList processResponse(Connection.Response response) {
        if (response == null) return null;
        try {
            Document doc = response.parse();
            myLog.add(doc.html());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
