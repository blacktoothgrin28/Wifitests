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
        try {
            //see http://intranet.esade.edu/web1/pkg_pantalles.info_layer?ample=500&alt=901&segons=0&edifici=8
            Document doc = response.parse();
//            myLog.add(doc.html());
            Elements clases = doc.select("table[class=item]");
            for (Element clase : clases) {
                String hora = clase.select("td[class=horatd]").first().text();
//                String title = clase.select("td[valign=top]").first().text();
                String title = clase.select("font[class=programa]").first().text();
                String assig = clase.select("font[class=assig]").first().text();
                Elements otroEl = clase.select("font[class=ealsec]");
                Elements aulaEl = clase.select("td[class=aulas]");
                Elements imgUrlEl = clase.select("img");
                String otro = null, aula = null, imgUrl = null;
                if (otroEl.size() > 0) {
                    otro = otroEl.first().text();
                }
                if (aulaEl.size() > 0) {
                    aula = aulaEl.first().text();

                }

                if (imgUrlEl.size() > 0) {
                    imgUrl = imgUrlEl.first().attr("scr");
                }

                myLog.add("*******titulo " + title + " | " + hora + " | " + aula
                        + " | " + imgUrl + " | " + assig + " | " + otro);
                myLog.add("titulo " + title);


            }
        } catch (Exception e) {
            e.printStackTrace();
            myLog.add("--error fetichin esade " + e.getLocalizedMessage());
        }
        return null;
    }
}
