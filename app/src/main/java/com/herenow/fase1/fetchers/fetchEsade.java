package com.herenow.fase1.fetchers;

import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

import parse.WeaconParse;
import util.MultiTaskCompleted;
import util.myLog;

/**
 * Created by Milenko on 26/01/2016.
 */
public class fetchEsade extends notificationFetcher {
    public fetchEsade(MultiTaskCompleted listener, WeaconParse we) {
        super(listener, we);
    }

    @Override
    protected ArrayList processResponse(Connection.Response response) {
        if (response == null) return null;
        ArrayList arr = new ArrayList();

        try {
            //see http://intranet.esade.edu/web1/pkg_pantalles.info_layer?ample=500&alt=901&segons=0&edifici=8
            Document doc = response.parse();
            Elements clases = doc.select("table[class=item]");
            for (Element clase : clases) {
                arr.add(new Leccion(clase));
            }
        } catch (Exception e) {
            myLog.add("--error fetichin esade " + e.getLocalizedMessage());
        }
        return arr;
    }
}
