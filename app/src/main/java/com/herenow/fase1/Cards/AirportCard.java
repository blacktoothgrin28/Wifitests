package com.herenow.fase1.Cards;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.herenow.fase1.CardData.FlightData;
import com.herenow.fase1.CardData.GoogleFlight;
import com.herenow.fase1.R;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.prototypes.CardWithList;
import it.gmariotti.cardslib.library.prototypes.LinearListView;
import it.gmariotti.cardslib.library.view.CardViewNative;
import util.OnTaskCompleted;
import util.myLog;
import util.parameters;

import static com.herenow.fase1.R.mipmap.ic_launcher;

/**
 * Created by Milenko on 12/08/2015.
 */
public class AirportCard extends CardWithList implements OnTaskCompleted {

    private static NotificationCompat.Builder notif;
    private final NotificationManager mNotificationManager;
    private List<ListObject> mObjects;
    private CardViewNative mCardViewAir;
    private GoogleFlight mCurrentGoogle, mOldGoogle;
    private FlightData mSelectedFlight;

    public AirportCard(Context context) {
        super(context);
        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
    }


    @Override
    protected CardHeader initCardHeader() {
        //Add Header
        CardHeader header = new CardHeader(getContext(), R.layout.carddemo_googlenowweather_inner_header);

        header.setTitle("Departures " + "BCN");
        return header;
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
            TextView time = (TextView) convertView.findViewById(R.id.time);
            TextView destination = (TextView) convertView.findViewById(R.id.destination);
            TextView companyAndFlight = (TextView) convertView.findViewById(R.id.company_and_flight);
            TextView estimated = (TextView) convertView.findViewById(R.id.estimated);

            //Retrieve the values from the object
            flightObject flightObject = (flightObject) object;

            time.setText(flightObject.scheduledAt);
            destination.setText(flightObject.destination);
            companyAndFlight.setText(flightObject.airline + " " + flightObject.code);

            String est = flightObject.estimated;
            if (est.startsWith("Schedu")) {
                est = "";
            } else if (est.startsWith("Estimat")) {
                est = "Est. " + est.substring(est.length() - 5);
//                estimated.setTextColor();
            } else if (est.startsWith("Dela")) {
                est = "Est. " + est.substring(est.length() - 5);
                myLog.add("hay uno con retraso grande, programado para las: " + flightObject.scheduledAt);
                estimated.setTextColor(getContext().getResources().getColor(R.color.red));
            }

            estimated.setText(est);

        } catch (Exception e) {
            myLog.add("eyyo " + e.getMessage());
        }
        return convertView;
    }

    @Override
    public void init() {
        super.init();
        mCardViewAir.setCard(this);
    }

    public void setView(CardViewNative cardViewAir) {
        mCardViewAir = cardViewAir;
    }

    @Override
    public int getChildLayoutId() {
        return R.layout.flight_card_inner_main;
    }


    public void setData(ArrayList<FlightData> departures) {
        mObjects = new ArrayList<>();

        for (FlightData fl : departures) {
            flightObject fo = new flightObject(mParentCard);
            fo.setData(fl);

            mObjects.add(fo);
        }
    }

    @Override
    public void OnTaskCompleted(ArrayList googleFlights) {
        mCurrentGoogle = (GoogleFlight) googleFlights.get(0);

        myLog.add("::::Hemos recibido la info del vuelo:" + mCurrentGoogle.getSummary());

        Toast.makeText(mContext, mCurrentGoogle.getSummary(), Toast.LENGTH_LONG).show();

        //Start asking google flight each 20 secs:
        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                myLog.add("-----timer execution");
                GetInfoFlightFromGoogleSync(mCurrentGoogle.code); //repeated
            }
        }, 10000, parameters.timeBetweenFlightQueries);

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
                content = mCurrentGoogle.getSummary();
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

    class flightObject extends CardWithList.DefaultListObject {

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

        public flightObject(Card parentCard) {
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
                    mSelectedFlight = ((flightObject) object).flight;
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
            destination = fl.destination;
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
