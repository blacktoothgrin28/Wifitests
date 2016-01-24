package com.herenow.fase1.Activities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.herenow.fase1.CardData.ChefData;
import com.herenow.fase1.CardData.CompanyData;
import com.herenow.fase1.CardData.DayFoodMenu;
import com.herenow.fase1.CardData.FoodMenu;
import com.herenow.fase1.CardData.ProductsData;
import com.herenow.fase1.Cards.AirportCard;
import com.herenow.fase1.Cards.BusStopCard;
import com.herenow.fase1.Cards.ChatCard;
import com.herenow.fase1.Cards.ChefCard;
import com.herenow.fase1.Cards.CompanyCard;
import com.herenow.fase1.Cards.Components.CardHeaderCoupon;
import com.herenow.fase1.Cards.Components.CardViewNative2;
import com.herenow.fase1.Cards.DayMenuCard;
import com.herenow.fase1.Cards.JobOffersCard;
import com.herenow.fase1.Cards.Linkedin2;
import com.herenow.fase1.Cards.MenuCard;
import com.herenow.fase1.Cards.NewsCard;
import com.herenow.fase1.Cards.ParadaCard;
import com.herenow.fase1.Cards.ProductsCard;
import com.herenow.fase1.Cards.RestaurantCard;
import com.herenow.fase1.Cards.RetailCard;
import com.herenow.fase1.Cards.ScheduleCard;
import com.herenow.fase1.Cards.TripAdvisorCard;
import com.herenow.fase1.Cards.TwitterCard;
import com.herenow.fase1.FlightData;
import com.herenow.fase1.R;
import com.parse.ParseObject;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardExpand;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.ViewToClickToExpand;
import parse.ParseActions;
import util.GPSCoordinates;
import util.dataExamples;
import util.myLog;
import util.parameters;

import static com.google.android.gms.internal.zzhl.runOnUiThread;
//import static com.google.android.gms.internal.zzip.runOnUiThread;

/**
 * A placeholder fragment containing a simple view.
 */
public class CardsActivityFragment extends Fragment implements cardLoadedListener {
    AirportCard airportCard;
    ScheduleCard scheduleCard;
    JobOffersCard jobCard;
    ProductsCard productCard;
    MenuCard menuCard;
    DayMenuCard dayMenuCard;
    ChatCard chatCard;
    ChefCard chefCard;
    ParadaCard paradaCard;

    private boolean injectJavaScript;
    private String url;
    private HashMap<String, Integer> hashTypes;
    private ScrollView mScrollView;


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

    private void initRetailCard(CompanyData companyData) {
        int cardLayout = R.layout.retail_card;
        RetailCard retailCard = RetailCard.with(getActivity())
                .setData(companyData)
                .useDrawableUrl(companyData.getMainImageUrl())
                .build();
        addCardToFragment(cardLayout, retailCard);
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

    private void initJobCard() {
        jobCard = new JobOffersCard(getActivity());
        jobCard.setData(dataExamples.getExampleJobOffers());
        jobCard.init();

        addCardToFragment(R.layout.native_cardwithlist_layout2, jobCard);
    }

    private void initVerticalChat() {
        myLog.add("INIT chat card");
        chatCard = new ChatCard(getActivity(), R.layout.chat_card);
//        jobCard.setData(dataExamples.getExampleJobOffers());
        CardHeader cardHeader = new CardHeader(getActivity());
        cardHeader.setTitle("Chat with Creapolis");
        chatCard.addCardHeader(cardHeader);

        addCardToFragment(R.layout.native_cardwithlist_layout, chatCard);
    }

    private void initFoodMenuCard(FoodMenu foodMenu) {
        menuCard = new MenuCard(getActivity());
        menuCard.setData(foodMenu);
        menuCard.init();

        addCardToFragment(R.layout.native_cardwithlist_layout2, menuCard);
    }

    private void initProductsCard(ProductsData productsData) {
        productCard = new ProductsCard(getActivity());
        productCard.setData(productsData);
        productCard.init();

        addCardToFragment(R.layout.native_cardwithlist_layout2, productCard);
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
        Linkedin2 linkedin2 = new Linkedin2(getActivity(), linkedinUrl, "Quien trabaja Aquí", R.layout.linkedin2_inner_main);
        linkedin2.setListener(this);
        linkedin2.init();
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

    private void init_card_expand_inside(int resourseId, String title) {
        try {
            int cardLayout = R.layout.carddemo_example_native_expandinside_card_layout;
            //Create a Card
            Card expandableCard = new Card(getActivity());

            //Create a CardHeader
            CardHeader header = new CardHeader(getActivity(), R.layout.map_inner_header);

            //Set the header title
            header.setTitle(title);

            //Add Header to card
            expandableCard.addCardHeader(header);

            //This provides a simple (and useless) expand area
            CardExpandInside expand = new CardExpandInside(getActivity(), resourseId);
            expandableCard.addCardExpand(expand);

            addCardToFragment(cardLayout, expandableCard);

        } catch (Exception e) {
            myLog.add("XXX errer en init" + e.getLocalizedMessage());
        }
    }

    private void init_custom_card_expand_inside(String imageUrl, String codeImageUrl) {
        try {
            int cardLayout = R.layout.carddemo_example_native_expandinside_card_layout;
            //Create a Card
            Card couponCard = new Card(getActivity());

            //Create a CardHeader
            CardHeaderCoupon header = new CardHeaderCoupon(getActivity(), R.layout.coupon_inner_header);

            //Set the header title
            header.setTitle("Descuento exclusivo para ti");
            header.setSubTitle("25% descuento en Moda Niño");
            header.setImageUrl(imageUrl);

            //Add Header to card
            couponCard.addCardHeader(header);

            //This provides a simple (and useless) expand area
            CardExpandInside expand = new CardExpandInside(getActivity(), codeImageUrl);
            couponCard.addCardExpand(expand);

            addCardToFragment(cardLayout, couponCard);

        } catch (Exception e) {
            myLog.add("XXX errer en init" + e.getLocalizedMessage());
        }
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

        //Case of image expandcard:
        if (cardLayout == R.layout.carddemo_example_native_expandinside_card_layout) {
            myLog.add("Addcard2fragment. tipo imageexpand");
            mScrollView = (ScrollView) getActivity().findViewById(R.id.card_scrollview);

            ViewToClickToExpand viewToClickToExpand =
                    ViewToClickToExpand.builder()
                            .highlightView(false)
                            .setupView(cardView);
            card.setViewToClickToExpand(viewToClickToExpand);

            card.setOnExpandAnimatorEndListener(new Card.OnExpandAnimatorEndListener() {
                @Override
                public void onExpandEnd(Card card) {

                    if (mScrollView != null) {
                        mScrollView.post(new Runnable() {
                            public void run() {
                                mScrollView.scrollTo(0, mScrollView.getBottom());
                            }
                        });
                    }
                }
            });
        }

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
                        //RETAIL
                        if (hashTypes.containsKey("Retail")) initRetailCard(companyData);

                        //RESTAURANT
                        if (hashTypes.containsKey("Restaurant")) initRestaurantCard(companyData);
                        //FOODMENU
                        if (hashTypes.containsKey("FoodMenu")) {
                            initFoodMenuCard(dataExamples.getSampleFoodMenu());//TODO read from parse
                        }
                        //DAYMENU
                        if (hashTypes.containsKey("DayMenu")) {
                            initDayMenuCard(dataExamples.getSampleDayFoodMenu());//TODO read from parse
                        }
                        //CHEF
                        if (hashTypes.containsKey("Chef")) {
                            initChefCard(dataExamples.getExampleChef());
                        }


                        //SCHEDULE  TODO read the schedule from parse
                        if (hashTypes.containsKey("Schedule")) {
                            initScheduleCard();
                        }
                        //Job offer
                        if (hashTypes.containsKey("Job")) {
                            initJobCard();
                        }

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

                        //PRODUCTS
                        if (hashTypes.containsKey("Products")) {
                            initProductsCard(dataExamples.getExampleProducts());
                        }

                        //Coupon
                        if (hashTypes.containsKey("Coupon")) {
//                            init_custom_card_expand_inside("http://vignette3.wikia.nocookie.net/inciclopedia/images/8/8a/Calamardo_Guapo.jpg", "");

                            String imageUrl = "http://3.bp.blogspot.com/_k4x5a3c1b_M/TGrvXN0KhPI/AAAAAAAASWc/8SGJ4EFnxHk/s800/zara3.png";
                            String codeImageUrl = "http://computadorasparacarros.yolasite.com/resources/codebar.png";
                            init_custom_card_expand_inside(imageUrl, codeImageUrl);

                        }

                        //MAP CARD
                        if (hashTypes.containsKey("Map")) {
                            init_card_expand_inside(R.drawable.building_map, "Map of the building");
                        }
                        //CHAT VERTICAL
                        if (hashTypes.containsKey("Chat")) {
                            initVerticalChat();
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

    public void launchBusCard(double wLat, double wLon) {
        String urlGps = "http://www.santqbus.santcugat.cat/consultasae.php?x=" + wLat + "&y=" + wLon;
        BusStopCard busStopCard = new BusStopCard(getActivity(), urlGps, "Paradas cercanas", R.layout.busstop_inner_main);
        busStopCard.setListener(this);
        busStopCard.init();
    }

    class CardExpandInside extends CardExpand {

        private String imageUrl = null;
        private int imageResId = 0;

        public CardExpandInside(Context context, String imageUrl) {
            super(context, R.layout.carddemo_example_expandinside_expand_layout);
            this.imageUrl = imageUrl;
        }

        public CardExpandInside(Context context, @DrawableRes int imageResID) {
            super(context, R.layout.carddemo_example_expandinside_expand_layout);
            this.imageResId = imageResID;

        }

        @Override
        public void setupInnerViewElements(ViewGroup parent, View view) {

            ImageView img = (ImageView) view.findViewById(R.id.carddemo_inside_image);
            RequestCreator rc;

            if (img != null) {
                if (imageUrl != null) {
                    rc = Picasso.with(mContext).load(imageUrl);
                } else {
                    rc = Picasso.with(mContext).load(imageResId);
                }

                rc.error(R.drawable.abc_ic_ab_back_mtrl_am_alpha)
                        .placeholder(R.mipmap.ic_launcher).
                        fit().centerCrop()
                        .into(img);
            }
        }

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
//                runOnUiThread(new Runnable() {
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

