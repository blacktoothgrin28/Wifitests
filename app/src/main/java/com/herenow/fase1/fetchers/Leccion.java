package com.herenow.fase1.fetchers;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import util.myLog;

public class Leccion {
    String otro = null, aula = null, imgUrl = null;
    String hora;
    String title;
    String assig;


    public Leccion(Element clase) {

//        hora = clase.select("td[class=horatd]").first().text();
        hora = clase.select("font[class=hora]").first().text();
        title = clase.select("font[class=programa]").first().text();
        assig = clase.select("font[class=assig]").first().text();
        Elements otroEl = clase.select("font[class=ealsec]");
        Elements aulaEl = clase.select("td[class=aulas]");
        Elements imgUrlEl = clase.select("img");
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

    }

    public String getOtro() {
        return otro;
    }

    public String getAula() {
        return aula;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public String getHora() {
        return hora;
    }

    public String getTitle() {
        return title;
    }

    public String getAssig() {
        return assig;
    }
}
