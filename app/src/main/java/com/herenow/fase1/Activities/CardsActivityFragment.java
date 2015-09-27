package com.herenow.fase1.Activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.herenow.fase1.CardData.CompanyData;
import com.herenow.fase1.CardData.FlightData;
import com.herenow.fase1.Cards.AirportCard;
import com.herenow.fase1.Cards.CompanyCard;
import com.herenow.fase1.Cards.LinkedinCard;
import com.herenow.fase1.Cards.NewsCard;
import com.herenow.fase1.Cards.ScheduleCard;
import com.herenow.fase1.R;
import com.parse.ParseObject;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import it.gmariotti.cardslib.library.cards.material.MaterialLargeImageCard;
import it.gmariotti.cardslib.library.view.CardViewNative;
import parse.ParseActions;
import util.myLog;
import util.parameters;

import static com.google.android.gms.internal.zzhl.runOnUiThread;

//import com.herenow.fase1.Cards.MaterialLargeImageCard;

/**
 * A placeholder fragment containing a simple view.
 */
public class CardsActivityFragment extends Fragment {
    AirportCard airportCard;
    ScheduleCard scheduleCard;
    private boolean injectJavaScript = true;

    public CardsActivityFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initCards();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //get the cardobjId from activity
        try {
//            String wCompanyDataObId = getArguments().getString("cardObId");

            //TODO fix it: it ask parse each time the fragment is created. Make the Company data parcelable
            // so we can ask  parse before...

        } catch (Exception e) {
            e.printStackTrace();
            myLog.addError(this.getClass(), e);

        }

        return inflater.inflate(R.layout.demo_fragment_cardwithlist_card, container, false);
    }

    private void initCards() {
        try {

//            initCompanyCard(parameters.getExampleCompanyCard());
//            initNewsCard(companyData.getNameClean());
//            initLinkedinCard(companyData.getLinkedinUrl());

//            initScheduleCard();
            initAirportCard();

        } catch (Exception e) {
            myLog.add("---error init cards: " + e.getMessage());
        }
    }

    private void initAirportCard() {

        airportCard = new AirportCard(getActivity());
        CardViewNative cardViewAirport = (CardViewNative) getActivity().findViewById(R.id.card_view_airport);
        airportCard.setView(cardViewAirport);
        // cardViewAirport.setCard(airportCard);


        final WebView browser = (WebView) getActivity().findViewById(R.id.wbChanta);
        //TODO quita la carga de imagenes:
        browser.getSettings().setLoadsImagesAutomatically(false);
        browser.getSettings().setJavaScriptEnabled(true);
        browser.addJavascriptInterface(new MyJavaScriptInterface(), "HTMLOUT");
        browser.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
//             This call inject JavaScript into the page which just finished loading. *//*
                myLog.add("VAMOS leer pagina de radar24 CON JAVASCRIPT INJECT");
                if (injectJavaScript)
                    browser.loadUrl("javascript:window.HTMLOUT.extractHtml('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
                injectJavaScript = false; //To avoid calling this several times
            }
        });
        myLog.add("VAMOS leer pagina de radar24");
        browser.loadUrl("http://flightradar24.com/airport/bcn/departures");
    }

    private void initScheduleCard() {
        //Schedule card
        scheduleCard = new ScheduleCard(getActivity());
        scheduleCard.setData(parameters.getExampleScheduleData());//it has 11 items
        scheduleCard.init();

        CardViewNative cardViewSchedule = (CardViewNative) getActivity().findViewById(R.id.card_view_schedule);
        cardViewSchedule.setCard(scheduleCard);
    }

    private void initLinkedinCard(String linkedinUrl) {
        //
//            //Linkedin card
        // todo change format of linkedin card
        LinkedinCard linkedinCard = new LinkedinCard(getActivity(), linkedinUrl);
        CardViewNative cvLinkedin = (CardViewNative) getActivity().findViewById(R.id.card_view_linkedin);
        linkedinCard.setView(cvLinkedin);
        linkedinCard.init();
    }

    private void initNewsCard(String nameCleanCompany) {
        // News Card
        NewsCard newsCard = new NewsCard(getActivity(), nameCleanCompany);
        CardViewNative cardViewNews = (CardViewNative) getActivity().findViewById(R.id.card_view_news);
        newsCard.setView(cardViewNews);
        newsCard.init();
    }

    private void initCompanyCard(final CompanyData companyCard) {
        // Company Card
        CompanyData companyData = companyCard;
        CompanyCard companyCardtest = CompanyCard.with(getActivity())
                .setData(companyData)
                .useDrawableUrl(companyData.getMainImageUrl())
                .build();

        CardViewNative cardViewCompany = (CardViewNative) getActivity().findViewById(R.id.card_view_company);
        cardViewCompany.setCard(companyCardtest);
    }

    @Override
    public void onPause() {

        try {
            super.onPause();
            if (airportCard != null)
                airportCard.unregisterDataSetObserver();//TODO, VER CoMO VA ESTE ROLLO DEL REGISTRO DE OBSERVER
        } catch (Exception e) {
            myLog.add("--error on Pause cards" + e.getMessage());
        }
    }

    public void setCardData(String wCompanyDataObId) {
        ParseActions.getCompanyData(wCompanyDataObId, new ParseCallback() {
            @Override
            public void DatafromParseReceived(List<ParseObject> datos) {
                myLog.add("etntramos en el callback. datossize="+datos.size());
                try {
                    ParseObject po = datos.get(0);
                    CompanyData companyData = new CompanyData(po);
                    initCompanyCard(companyData);
                    initNewsCard(companyData.getNameClean());
                    initLinkedinCard(companyData.getLinkedinUrl());
                } catch (Exception e) {
                    e.printStackTrace();
                    myLog.add("error aquí." + e.getLocalizedMessage());
                }
            }

            @Override
            public void OnError(Exception e) {

            }
        });

    }


    class MyJavaScriptInterface {

        @JavascriptInterface
        public void extractHtml(String html) {
            ArrayList<FlightData> departures = new ArrayList<>();

            try {
                Document doc = Jsoup.parse(html);
                Elements dep = doc.select("table[class=flightList mainFlightList]").get(1).children().get(1).children();//el segundo tiene la chicha

                for (Element item : dep) {
                    if (item.className().equals("codeshareFlights")) continue;
                    departures.add(ProcessHtmlFlights(item)); //todo agregar los codigos compartidos
                }

                myLog.add("tenemos ya procesasdo aviones partiendo: " + departures.size());

                airportCard.setData(departures);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        airportCard.init();       //stuff that updates ui
                    }
                });
            } catch (Exception e) {
                myLog.add("error en parsing la web aviones:" + e.getMessage());
            }

        }

        private FlightData ProcessHtmlFlights(Element flight) {
            FlightData fo = new FlightData();
            Elements children = flight.children();

            try {
                fo.scheduledAt = children.get(0).text(); //scheduled
                fo.code = children.get(1).text(); //code
                fo.destination = children.get(2).text();
                fo.airline = children.get(3).text(); //airline
                fo.plane = children.get(4).text(); //codigo del modelo de avion
                fo.ignoro = children.get(5).text(); //no se que
                fo.estimated = children.get(6).text(); //en hora,
                fo.status = children.get(6).child(0).attr("class");
                fo.title = children.get(6).child(0).attr("title");
                fo.ignoro2 = children.get(7).text();
            } catch (Exception e) {
                myLog.add("errror capturanfo flight " + e.getMessage());
            }
            return fo;
        }

    }
}

