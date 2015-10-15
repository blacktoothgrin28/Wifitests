package com.herenow.fase1.Cards;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.herenow.fase1.Activities.cardLoadedListener;
import com.herenow.fase1.CardData.GoogleFlight;
import com.herenow.fase1.Cards.Components.CardViewNative2;
import com.herenow.fase1.FlightData;
import com.herenow.fase1.GetFlightInfo;
import com.herenow.fase1.R;
import com.herenow.fase1.MyServices.FlightsObserverService;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.prototypes.CardWithList;
import it.gmariotti.cardslib.library.prototypes.LinearListView;
import util.OnTaskCompleted;
import util.myLog;

import static com.herenow.fase1.R.mipmap.ic_launcher;

/**
 * Created by Milenko on 12/08/2015.
 */
public class AirportCard extends CardWithList implements OnTaskCompleted {

    private static NotificationCompat.Builder notif;
    private final NotificationManager mNotificationManager;
    private final TypeOfCard mTypeOfCard;
    private List<ListObject> mObjects;
    private GoogleFlight mCurrentGoogle, mOldGoogle;
    private FlightData mSelectedFlight;
    private String mAirportCode = "Airport code";
    private CardViewNative2 mCardViewAir;
    private cardLoadedListener listener;
//    private cardLoadedListener listener;

    public AirportCard(Context context, TypeOfCard typeOfCard, String airportCode) {
        super(context);
        mAirportCode = airportCode;
        mTypeOfCard = typeOfCard;
        myLog.add("the type of cards is" + typeOfCard);
        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public static String nameOfType(TypeOfCard toc) {
        String st;
        if (toc == TypeOfCard.departure) {
            st = "departures";
        } else {
            st = "arrivals";
        }
        return st;

    }

    @Override
    protected CardHeader initCardHeader() {
        //Add Header
        CardHeader header = new CardHeader(getContext(), R.layout.carddemo_googlenowweather_inner_header);

        if (mTypeOfCard == TypeOfCard.departure)
            header.setTitle("Departures " + mAirportCode.toUpperCase());
        else {
            header.setTitle("Arrivals " + mAirportCode.toUpperCase());
        }
        return header;
    }

    public void setListener(cardLoadedListener listener) {
        this.listener = listener;
    }

    @Override
    protected void initCard() {
        //Set the whole card as swipeable
        setSwipeable(true);
        setOnSwipeListener(new OnSwipeListener() {
            @Override
            public void onSwipe(Card card) {
                Toast.makeText(getContext(), "Swipe on " + card.getCardHeader().getTitle(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected List<ListObject> initChildren() {
        return mObjects;
    }

    @Override
    public View setupChildView(int childPosition, CardWithList.ListObject object, View convertView, ViewGroup parent) {
        try {
            //Setup the ui elements inside the item
            TextView companyAndFlight = (TextView) convertView.findViewById(R.id.company_and_flight);
            TextView time = (TextView) convertView.findViewById(R.id.time);
            TextView estimated = (TextView) convertView.findViewById(R.id.estimated);
            TextView destination = (TextView) convertView.findViewById(R.id.destination);//it's origin when arrival

            if (mTypeOfCard == TypeOfCard.departure) {

                //Retrieve the values from the object
                DepartureObject departureObject = (DepartureObject) object;

                time.setText(departureObject.scheduledAt);
                destination.setText(departureObject.destination);
                companyAndFlight.setText(departureObject.airline + " " + departureObject.code);

                String est = departureObject.estimated;
                if (est.startsWith("Schedu")) {
                    est = "";
                } else if (est.startsWith("Estimat")) {
                    est = "Est. " + est.substring(est.length() - 5);
                    //                estimated.setTextColor();
                } else if (est.startsWith("Dela")) {
                    est = "Est. " + est.substring(est.length() - 5);
                    myLog.add("hay uno con retraso grande, programado para las: " + departureObject.scheduledAt);
                    estimated.setTextColor(getContext().getResources().getColor(R.color.red));
                }

                estimated.setText(est);
            } else {
                //Arrival
                //Retrieve the values from the object
                ArrivalObject arrival = (ArrivalObject) object;

                time.setText(arrival.scheduledAt);
                destination.setText(arrival.origin);
                companyAndFlight.setText(arrival.airline + " " + arrival.code);

                String est = arrival.estimated;
//                if (est.startsWith("Schedu")) {
//                    est = "";
//                } else if (est.startsWith("Estimat")) {
//                    est = "Est. " + est.substring(est.length() - 5);
//                    //                estimated.setTextColor();
//                } else if (est.startsWith("Dela")) {
//                    est = "Est. " + est.substring(est.length() - 5);
//                    myLog.add("hay uno con retraso grande, programado para las: " + arrival.scheduledAt);
//                    estimated.setTextColor(getContext().getResources().getColor(R.color.red));
//                }
//
//                estimated.setText(est);

            }

        } catch (Exception e) {
            myLog.add("eyyo " + e.getMessage());
        }
        return convertView;
    }

    @Override
    public void init() {
        super.init();
//        mCardViewAir.setCard(this);
        try {
            listener.OnCardReady(this, R.layout.native_cardwithlist_layout);
        } catch (Exception e) {
            listener.OnCardErrorLoadingData(e);
        }
    }

    @Override
    public int getChildLayoutId() {
        int innerLayout;
        if (mTypeOfCard == TypeOfCard.departure) {
            innerLayout = R.layout.departure_card_inner_main;
        } else {
            innerLayout = R.layout.arrival_card_inner_main;
        }
        return innerLayout;
    }

//    public void setView(CardViewNative2 cardViewAir) {
//        mCardViewAir = cardViewAir;
//    }

    public void setData(ArrayList<FlightData> departures) {
        mObjects = new ArrayList<>();

        for (FlightData fl : departures) {
            DepartureObject depOb;
            ArrivalObject arrOb;
            if (mTypeOfCard == TypeOfCard.departure) {
                depOb = new DepartureObject(mParentCard);
                depOb.setData(fl);
                mObjects.add(depOb);
            } else {
                arrOb = new ArrivalObject(mParentCard);
                arrOb.setData(fl);
                mObjects.add(arrOb);
            }

        }
    }

    @Override
    public void OnTaskCompleted(ArrayList googleFlights) {
        mCurrentGoogle = (GoogleFlight) googleFlights.get(0);

        myLog.add("::::Hemos recibido la info del vuelo:" + mCurrentGoogle.getSummary(TypeOfCard.departure));

        Toast.makeText(mContext, mCurrentGoogle.getSummary(mTypeOfCard), Toast.LENGTH_LONG).show();


        //Aquí lanzamos el intentservice para preguntar a google periodicamente
        Intent intent = new Intent(mContext, FlightsObserverService.class);

        intent.putExtra("remoteCity", mCurrentGoogle.remoteCity);
        intent.putExtra("code", mCurrentGoogle.code);
        myLog.add("++++++++hemos añadido al intent: " + mCurrentGoogle.remoteCity + " | " + mCurrentGoogle.code);
        mContext.startService(intent);

    }

    private void GetInfoFlightFromGoogleSync(String code) {

        try {
            myLog.add("getting in google sync (timer)");
            Connection.Response response = Jsoup.connect("https://www.google.es/search?q=" + code)
                    .ignoreContentType(true)
                    .referrer("http://www.google.com")
                    .timeout(5000)
                    .followRedirects(true)
                    .execute();

            Document doc = response.parse();
            Element googleFlightCard = doc.select("div[class=card-section vk_c]").first();
            Elements datos = googleFlightCard.select("tr[class=_FJg").first().children();
            mOldGoogle = mCurrentGoogle;
            mCurrentGoogle = new GoogleFlight(datos, mSelectedFlight);
            mCurrentGoogle.code = code;

            myLog.add("..info de timer: " + mCurrentGoogle.toString());

            String notiTitle;
            String content;
            if (mCurrentGoogle.hasChanged(mOldGoogle)) {
                myLog.add("has changed: " + mCurrentGoogle.changesText);
                notiTitle = mCurrentGoogle.changeSummarized;
                content = mCurrentGoogle.getSummary(TypeOfCard.departure);
                myLog.add("******NOTIFICATION: " + notiTitle);
                myLog.add("***NOTIFICATION: " + content);

                //Notification
                sendNotificationFlight(notiTitle, content);
            } else {
                myLog.add("Hasn't changed ");
            }


        } catch (IOException e) {
            myLog.add("---error en sync:" + e.getMessage());
        }

    }

    private void sendNotificationFlight(String notiTitle, String content) {
        myLog.add("|||trying to send notification");

//        NotificationCompat.Action myAction = new NotificationCompat.Action(R.drawable.ic_silence,
//                "Turn Off", resultPendingIntent);
        Bitmap bmImage = BitmapFactory.decodeResource(mContext.getResources(), ic_launcher);//Todo get the image from other source
        notif = new NotificationCompat.Builder(mContext)
                .setSmallIcon(R.drawable.ic_stat_name_hn)
                .setLargeIcon(bmImage)
                .setContentTitle(notiTitle)
                .setContentText(content)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND | Notification.FLAG_SHOW_LIGHTS)
                .setLights(0xE6D820, 300, 100)
                .setTicker("Change in Flight");
//                .setDeleteIntent(pendingDeleteIntent)
//                .addAction(myAction);
        //todo improve the notif
        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.setBigContentTitle("PP" + notiTitle);
        bigTextStyle.bigText("PP" + content);

        notif.setStyle(bigTextStyle);
//        notif.setContentIntent(resultPendingIntent);

        mNotificationManager.notify(1, notif.build());

    }

    private void GetInfoFlightFromGoogle(String flightCode) {
        (new readGoogleFlight(this)).execute(new String[]{flightCode});
    }

    public enum TypeOfCard {departure, arrival}

    class DepartureObject extends CardWithList.DefaultListObject {

        public String code;
        public String scheduledAt;
        public String destination;
        public String airline;
        public String plane;
        public String ignoro;
        public String estimated;
        public String ignoro2;
        public String status;
        public String title;
        public String[] SharedCodes;
        private FlightData flight;

        public DepartureObject(Card parentCard) {
            super(parentCard);
            init();
        }

        private void init() {
            //OnClick Listener
            setOnItemClickListener(new CardWithList.OnItemClickListener() {
                @Override
                public void onItemClick(LinearListView parent, View view, int position, CardWithList.ListObject object) {
                    Toast.makeText(getContext(), "Getting info for " + getObjectId() + "\nAlerts Activated.", Toast.LENGTH_SHORT).show();
                    myLog.add("asgink google for flight for first time: " + getObjectId());
                    mSelectedFlight = ((DepartureObject) object).flight;
//                    GetInfoFlightFromGoogle(getObjectId()); //First retrieving from google
                    GetFlightInfo geto = new GetFlightInfo(getObjectId(), mSelectedFlight.remoteCity, AirportCard.this);
                }
            });

//            //OnItemSwipeListener
//            setOnItemSwipeListener(new OnItemSwipeListener() {
//                @Override
//                public void onItemSwipe(ListObject object, boolean dismissRight) {
//                    Toast.makeText(getContext(), "Swipe on " + object.getObjectId(), Toast.LENGTH_SHORT).show();
//                }
//            });
        }

        public void setData(FlightData fl) {
            flight = fl;

            code = fl.code;
            scheduledAt = fl.scheduledAt;
            airline = fl.airline;
            destination = fl.remoteCity;
            status = fl.status;
            title = fl.title;
            estimated = fl.estimated;

            setObjectId(code);
        }
    }

    class ArrivalObject extends CardWithList.DefaultListObject {

        public String code;
        public String scheduledAt;
        public String origin;
        public String airline;
        public String plane;
        public String ignoro;
        public String estimated;
        public String ignoro2;
        public String status;
        public String title;
        public String[] SharedCodes;
        private FlightData flight;

        public ArrivalObject(Card parentCard) {
            super(parentCard);
            init();
        }

        private void init() {
            //OnClick Listener
            setOnItemClickListener(new CardWithList.OnItemClickListener() {
                @Override
                public void onItemClick(LinearListView parent, View view, int position, CardWithList.ListObject object) {
                    Toast.makeText(getContext(), "Getting info for " + getObjectId() + "\nAlerts Activated.", Toast.LENGTH_SHORT).show();
                    myLog.add("asgink google for flight for first time: " + getObjectId());
                    mSelectedFlight = ((ArrivalObject) object).flight;
                    GetInfoFlightFromGoogle(getObjectId()); //First retrieving from google
                }
            });

//            //OnItemSwipeListener
//            setOnItemSwipeListener(new OnItemSwipeListener() {
//                @Override
//                public void onItemSwipe(ListObject object, boolean dismissRight) {
//                    Toast.makeText(getContext(), "Swipe on " + object.getObjectId(), Toast.LENGTH_SHORT).show();
//                }
//            });
        }

        public void setData(FlightData fl) {
            flight = fl;

            code = fl.code;
            scheduledAt = fl.scheduledAt;
            airline = fl.airline;
            origin = fl.remoteCity;
            status = fl.status;
            title = fl.title;
            estimated = fl.estimated;

            setObjectId(code);
        }
    }

    class readGoogleFlight extends AsyncTask<String, Void, GoogleFlight> {
        private OnTaskCompleted listener;

        readGoogleFlight(OnTaskCompleted listener) {
            myLog.add("asignando listener en parseGoogleflight");
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
                googleFlight = new GoogleFlight(datos, mSelectedFlight);
                googleFlight.code = strings[0];
//                Toast.makeText(mContext, googleFlight.toString(), Toast.LENGTH_SHORT).show();
                myLog.add("primera vista de google:" + googleFlight);
            } catch (Throwable t) {
                myLog.add("-eeeoo frist query a google flifht" + t.getMessage());
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
