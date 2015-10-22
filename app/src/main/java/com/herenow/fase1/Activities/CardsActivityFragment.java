package com.herenow.fase1.Activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.herenow.fase1.CardData.ChefData;
import com.herenow.fase1.CardData.CompanyData;
import com.herenow.fase1.CardData.DayFoodMenu;
import com.herenow.fase1.CardData.FoodMenu;
import com.herenow.fase1.Cards.AirportCard;
import com.herenow.fase1.Cards.ChefCard;
import com.herenow.fase1.Cards.CompanyCard;
import com.herenow.fase1.Cards.Components.CardViewNative2;
import com.herenow.fase1.Cards.DayMenuCard;
import com.herenow.fase1.Cards.LinkedinCard;
import com.herenow.fase1.Cards.MenuCard;
import com.herenow.fase1.Cards.NewsCard;
import com.herenow.fase1.Cards.RestaurantCard;
import com.herenow.fase1.Cards.ScheduleCard;
import com.herenow.fase1.Cards.TripAdvisorCard;
import com.herenow.fase1.Cards.TwitterCard;
import com.herenow.fase1.FlightData;
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
import parse.ParseActions;
import util.dataExamples;
import util.myLog;
import util.parameters;

import static com.google.android.gms.internal.zzhl.runOnUiThread;

/**
 * A placeholder fragment containing a simple view.
 */
public class CardsActivityFragment extends Fragment implements cardLoadedListener {
    AirportCard airportCard;
    ScheduleCard scheduleCard;
    MenuCard menuCard;
    DayMenuCard dayMenuCard;
     ChefCard chefCard;

    private boolean injectJavaScript;
    private String url;
    private HashMap<String, Integer> hashTypes;

    public CardsActivityFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        injectJavaScript = true;
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
//        return inflater.inflate(R.layout.fragment_cards_static, container, false);
        return inflater.inflate(R.layout.fragment_cards_blank, container, false);
    }


    private void initAirportCard(AirportCard.TypeOfCard typeOfCard, String airportCode) {

        airportCard = new AirportCard(getActivity(), typeOfCard, airportCode);//departure just for test
        airportCard.setListener(this);

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
        url = "http://flightradar24.com/airport/" + airportCode.toLowerCase() + "/" + AirportCard.nameOfType(typeOfCard);
        myLog.add("RADAR 24 url:" + url);
        browser.loadUrl(url);
    }

    private void initCompanyCard(final CompanyData companyData) {
        int cardLayout = R.layout.company_card;
        CompanyCard companyCardtest = CompanyCard.with(getActivity())
                .setData(companyData)
                .useDrawableUrl(companyData.getMainImageUrl())
                .build();
        addCardToFragment(cardLayout, companyCardtest);
    }

    private void initRestaurantCard(CompanyData restaurantData) {
        int cardLayout = R.layout.company_card;
        RestaurantCard restaurant = RestaurantCard.with(getActivity())
                .setData(restaurantData)
                .useDrawableUrl(restaurantData.getMainImageUrl())
                .build();
        addCardToFragment(cardLayout, restaurant);
    }

    private void initScheduleCard() {
        scheduleCard = new ScheduleCard(getActivity());
        scheduleCard.setData(dataExamples.getExampleScheduleData());//it has 11 items
        scheduleCard.init();

        addCardToFragment(R.layout.native_cardwithlist_layout2, scheduleCard);
    }

    private void initFoodMenuCard(FoodMenu foodMenu) {
        menuCard = new MenuCard(getActivity());
        menuCard.setData(foodMenu);
        menuCard.init();

        addCardToFragment(R.layout.native_cardwithlist_layout2, menuCard);
    }

    private void initDayMenuCard(DayFoodMenu foodMenu) {
        dayMenuCard = new DayMenuCard(getActivity());
        dayMenuCard.setData(foodMenu);
        dayMenuCard.init();

        addCardToFragment(R.layout.native_cardwithlist_layout2, dayMenuCard);
    }

    private void initChefCard(ChefData chefData) {
        chefCard = new ChefCard(getActivity());
        chefCard.setData(chefData);
        chefCard.init();

        addCardToFragment(R.layout.native_cardwithlist_layout2, chefCard);
    }

    private void initLinkedinCard(String linkedinUrl) {
        // todo change format of linkedin card
        LinkedinCard linkedinCard = new LinkedinCard(getActivity(), linkedinUrl);
        linkedinCard.setListener(this);
        linkedinCard.init();
    }

    private void initTripAdvisorCard(String tripAdvisorUrl) {
        TripAdvisorCard tripAdvisorCard = new TripAdvisorCard(getActivity(), tripAdvisorUrl);
        tripAdvisorCard.setListener(this);
        tripAdvisorCard.init();
    }

    private void initTwitterCard(String twitterUrl) {
        TwitterCard twitterCard = new TwitterCard(getActivity(), twitterUrl);
        twitterCard.setListener(this);
        twitterCard.init();
    }

    private void initNewsCard(String nameCleanCompany) {
        // News Card
        NewsCard newsCard = new NewsCard(getActivity(), nameCleanCompany);
//        CardViewNative cardViewNews = (CardViewNative) getActivity().findViewById(R.id.card_view_news);
//        newsCard.setView(cardViewNews);
        newsCard.setListener(this);
        newsCard.init();
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
        if (wCompanyDataObId != null) {

            ParseActions.getCompanyData(wCompanyDataObId, new ParseCallback() {
                @Override
                public void DatafromParseReceived(List<ParseObject> datos) {
                    myLog.add("etntramos en el callback. datossize=" + datos.size());
                    try {
                        ParseObject po = datos.get(0);
                        CompanyData companyData = new CompanyData(po);

                        //COMPANY
                        if (hashTypes.containsKey("Company")) initCompanyCard(companyData);

                        //RESTAURANT
                        if (hashTypes.containsKey("Restaurant")) initRestaurantCard(companyData);
                        //FOODMENU
                        if (hashTypes.containsKey("FoodMenu"))
                            initFoodMenuCard(dataExamples.getSampleFoodMenu());//TODO read from parse
                        //DAYMENU
                        if (hashTypes.containsKey("DayMenu"))
                            initDayMenuCard(dataExamples.getSampleDayFoodMenu());//TODO read from parse
                        //CHEF
                        if (hashTypes.containsKey("Chef"))
                            initChefCard(dataExamples.getExampleChef());


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

                        //TWITTER
                        if (hashTypes.containsKey("Twitter")) {
                            initTwitterCard(companyData.getTwitterUser());
                        }

                        //TRIPADVISOR
                        if (hashTypes.containsKey("TripAdvisor")) {
                            initTripAdvisorCard(companyData.getTripAdvisorUrl());
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

        } else {
            myLog.add("-__No tiene cardObjectId");

        }
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

