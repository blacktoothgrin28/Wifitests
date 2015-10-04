package com.herenow.fase1;

import android.os.AsyncTask;

import com.herenow.fase1.CardData.GoogleFlight;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

import util.OnTaskCompleted;
import util.myLog;

/**
 * Created by Milenko on 04/10/2015.
 */
public class GetFlightInfo {
    private String mCode;
    private String mRemotecity;

    public  GetFlightInfo(String flightCode, String otherCity, OnTaskCompleted listener) {
        mCode = flightCode;
        mRemotecity = otherCity;
        (new readGoogleFlight(listener)).execute(new String[]{flightCode});
    }

    class readGoogleFlight extends AsyncTask<String, Void, GoogleFlight> {
        private OnTaskCompleted listener;

        readGoogleFlight(OnTaskCompleted listener) {
            this.listener = listener;
        }

        @Override
        protected GoogleFlight doInBackground(String... strings) {
            GoogleFlight googleFlight = null;

            try {
                Connection.Response response = Jsoup.connect("https://www.google.es/search?q=" + strings[0])
                        .ignoreContentType(true)
//                        .userAgent("Mozilla/5.0 (Windows NT 6.3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.85 Safari/537.36")
                        .referrer("http://www.google.com")
                        .timeout(12000)
                        .followRedirects(true)
                        .execute();

                Document doc = response.parse();
                Element googleFlightCard = doc.select("div[class=card-section vk_c]").first();
                Elements datos = googleFlightCard.select("tr[class=_FJg").first().children();
                googleFlight = new GoogleFlight(datos);
                googleFlight.code = strings[0];
                googleFlight.setRemoteCity(mRemotecity);

//                Toast.makeText(mContext, googleFlight.toString(), Toast.LENGTH_SHORT).show();
            } catch (Throwable t) {
                myLog.add("-eeeoo  query a google flifht" + t.getMessage());
            }
            return googleFlight;
        }

        @Override
        protected void onPostExecute(GoogleFlight googleFlight) {

            try {
                super.onPostExecute(googleFlight);
                ArrayList buff = new ArrayList<>();
                buff.add(googleFlight);
                listener.OnTaskCompleted(buff);
            } catch (Exception e) {
                myLog.add("fallo en post execute del google wuery: " + e.getMessage());
            }

        }

    }
}
