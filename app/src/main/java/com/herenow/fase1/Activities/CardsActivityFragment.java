package com.herenow.fase1.Activities;

import android.app.Application;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.herenow.fase1.CardData.CompanyData;
import com.herenow.fase1.CardViewNative2;
import com.herenow.fase1.FlightData;
import com.herenow.fase1.Cards.AirportCard;
import com.herenow.fase1.Cards.CompanyCard;
import com.herenow.fase1.Cards.LinkedinCard;
import com.herenow.fase1.Cards.NewsCard;
import com.herenow.fase1.Cards.ScheduleCard;
import com.herenow.fase1.R;
import com.parse.ParseObject;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.view.CardViewNative;
import parse.ParseActions;
import util.myLog;
import util.parameters;

import static com.google.android.gms.internal.zzhl.runOnUiThread;

/**
 * A placeholder fragment containing a simple view.
 */
public class CardsActivityFragment extends Fragment implements cardLoadedListener {
    AirportCard airportCard;
    ScheduleCard scheduleCard;
    private boolean injectJavaScript;


    private String url;
    private String[] cardsType;
    private HashMap<String, Integer> hashTypes;

    public CardsActivityFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        injectJavaScript = true;
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

//            myLog.add("ONcreatevire en fragmento del card. hemos recibido el argumento: " + wCompanyDataObId);

            //TODO fix it: it ask parse each time the fragment is created. Make the Company data parcelable
            // so we can ask  parse before...

        } catch (Exception e) {
            e.printStackTrace();
            myLog.addError(this.getClass(), e);

        }
//        return inflater.inflate(R.layout.demo_fragment_cardwithlist_card, container, false);
        return inflater.inflate(R.layout.fragment_cards_blank, container, false);
    }

    private void initCards() {
        try {

//            if (hashTypes.containsKey("Schedule")) initScheduleCard();

        } catch (Exception e) {
            myLog.add("---error init cards: " + e.getMessage());
        }
    }

    private void initAirportCard(AirportCard.TypeOfCard typeOfCard, String airportCode) {


        airportCard = new AirportCard(getActivity(), typeOfCard, airportCode);//departure just for test
        CardViewNative cardViewAirport = (CardViewNative) getActivity().findViewById(R.id.card_view_airport);
        airportCard.setView(cardViewAirport);


        final WebView browser = (WebView) getActivity().findViewById(R.id.wbChanta);
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
//        browser.loadUrl("http://flightradar24.com/airport/bcn/departures");
        url = "http://flightradar24.com/airport/" + airportCode.toLowerCase() + "/" + AirportCard.nameOfType(typeOfCard);
        myLog.add("RADAR 24 url:" + url);
        browser.loadUrl(url);
    }

    private void initScheduleCard() {
        //Schedule card
        scheduleCard = new ScheduleCard(getActivity());
        scheduleCard.setData(parameters.getExampleScheduleData());//it has 11 items
        scheduleCard.init();

        addCardToFragment(R.layout.native_cardwithlist_layout, scheduleCard);
//        CardViewNative cardViewSchedule = (CardViewNative) getActivity().findViewById(R.id.card_view_schedule);
//        cardViewSchedule.setCard(scheduleCard);

    }

    private void initLinkedinCard(String linkedinUrl) {
        //
//            //Linkedin card
        // todo change format of linkedin card
        LinkedinCard linkedinCard = new LinkedinCard(getActivity(), linkedinUrl);
//        CardViewNative cvLinkedin = (CardViewNative) getActivity().findViewById(R.id.card_view_linkedin);
//        linkedinCard.setView(cvLinkedin);
        linkedinCard.setListener(this);
        linkedinCard.init();
    }

    private void initNewsCard(String nameCleanCompany) {
        // News Card
        NewsCard newsCard = new NewsCard(getActivity(), nameCleanCompany);
//        CardViewNative cardViewNews = (CardViewNative) getActivity().findViewById(R.id.card_view_news);
//        newsCard.setView(cardViewNews);
        newsCard.setListener(this);
        newsCard.init();
    }

    private void initCompanyCard(final CompanyData companyData) {
        int cardLayout = R.layout.company_card;
        CompanyCard companyCardtest = CompanyCard.with(getActivity())
                .setData(companyData)
                .useDrawableUrl(companyData.getMainImageUrl())
                .build();
        addCardToFragment(cardLayout, companyCardtest);
    }

    private void addCardToFragment(int cardLayout, Card card) {
        CardViewNative2 cardView = new CardViewNative2(getActivity(), cardLayout);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = 12;
        params.rightMargin = 12;
        params.topMargin = 12;

        cardView.setLayoutParams(params);

        //EmptyView
        View emptyView = new View(getActivity());
        params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, parameters.spaceBetweenCards);
        emptyView.setLayoutParams(params);

        LinearLayout ll = (LinearLayout) getActivity().findViewById(R.id.linear_layout_cards);

        ll.addView(cardView);
        ll.addView(emptyView);

        //todo add animation
        cardView.setCard(card);
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


    public void setCardData(final String wCompanyDataObId, final AirportCard.TypeOfCard mTypeAirportCard) {
        ParseActions.getCompanyData(wCompanyDataObId, new ParseCallback() {
            @Override
            public void DatafromParseReceived(List<ParseObject> datos) {
                myLog.add("etntramos en el callback. datossize=" + datos.size());
                try {
                    ParseObject po = datos.get(0);
                    CompanyData companyData = new CompanyData(po);

                    //COMPANY
                    if (hashTypes.containsKey("Company")) initCompanyCard(companyData);

                    //SCHEDULE  TODO read the schedule from parse
                    if (hashTypes.containsKey("Schedule"))
                        initScheduleCard();

                    //AIRPORT
                    if (companyData.isAirport() || hashTypes.containsKey("Airport")) {
                        initAirportCard(mTypeAirportCard, companyData.getAirportCode());
                    }

                    //NEWS
                    if (hashTypes.containsKey("News")) initNewsCard(companyData.getNameClean());

                    //LINKEDIN
                    if (hashTypes.containsKey("Linkedin")) {
                        initLinkedinCard(companyData.getLinkedinUrl());
                    }


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

    /**
     * When a card that need to wait for data is ready,
     *
     * @param card
     * @param cardLayout
     */
    @Override
    public void OnCardReady(Card card, int cardLayout) {
        try {
            myLog.add("***se ha terminado de cargar una card: " + card.getCardHeader().getTitle());

            addCardToFragment(cardLayout, card);
        } catch (Exception e) {
            myLog.add("error en oncardready " + e.getLocalizedMessage());
        }

    }

    public void setCardsType(String[] cardsType) {
        hashTypes = new HashMap<>();
        Integer i = 1;
        for (String cardType : cardsType) {
            hashTypes.put(cardType, i);
            i++;
        }
    }

    @Override
    public void OnCardErrorLoadingData(Exception e) {
        myLog.add("error en onreading a card:" + e.getLocalizedMessage());

    }


    class MyJavaScriptInterface {

        @JavascriptInterface
        public void extractHtml(String html) {
            ArrayList<FlightData> departures = new ArrayList<>();

            try {
                Document doc = Jsoup.parse(html);
                Elements dep = doc.select("table[class=flightList mainFlightList]").select("tr[class=scheduledFlight]").first().parent().children();

                for (Element item : dep) {
                    if (item.className().equals("codeshareFlights")) continue;
                    departures.add(ProcessHtmlFlights(item)); //todo agregar los codigos compartidos
                }

                myLog.add("tenemos ya procesasdo aviones partiendo//llegando: " + departures.size());

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
                fo.remoteCity = children.get(2).text();
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

