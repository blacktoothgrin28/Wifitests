package com.herenow.fase1.Cards;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.herenow.fase1.CardData.GoogleFlight;
import com.herenow.fase1.CardData.flight;
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
import util.AppendLog;

/**
 * Created by Milenko on 12/08/2015.
 */
public class AirportCard extends CardWithList implements OnTaskCompleted {

    private List<ListObject> mObjects;
    private CardViewNative mCardViewAir;
    private GoogleFlight mGoogleFlight;
    private GoogleFlight mCurrentGoogle, mOldGoogle;

    public AirportCard(Context context) {
        super(context);
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
                est = est.substring(est.length() - 5);
//                estimated.setTextColor();
            } else if (est.startsWith("Dela")) {
                est = est.substring(est.length() - 5);
                AppendLog.appendLog("hay uno con retraso grande, programado para las: " + flightObject.scheduledAt);
                estimated.setTextColor(getContext().getResources().getColor(R.color.red));
            }

            estimated.setText(est);

        } catch (Exception e) {
            AppendLog.appendLog("eyyo " + e.getMessage());
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


    public void setData(ArrayList<flight> departures) {
        mObjects = new ArrayList<>();

        for (flight fl : departures) {
            flightObject fo = new flightObject(mParentCard);
            fo.code = fl.code;
            fo.scheduledAt = fl.scheduledAt;
            fo.airline = fl.airline;
            fo.destination = fl.destination;
            fo.status = fl.status;
            fo.title = fl.title;
            fo.estimated = fl.estimated;

            fo.setObjectId(fo.code);

            mObjects.add(fo);
        }
    }

    @Override
    public void OnTaskCompleted(ArrayList googleFlights) {
        mCurrentGoogle = (GoogleFlight) googleFlights.get(0);

        //Start asking google flight each 20 secs:
        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                AppendLog.appendLog("timer execution");
                GetInfoFlightFromGoogleSync(mCurrentGoogle.code); //repeated
            }
        }, 10000, 20000);

    }

    private void GetInfoFlightFromGoogleSync(String code) {

        try {
            AppendLog.appendLog("getoin from google sync (timer)");
            Connection.Response response = Jsoup.connect("https://www.google.es/search?q=" + code)
                    .ignoreContentType(true)
                            //                        .userAgent("Mozilla/5.0 (Windows NT 6.3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.85 Safari/537.36")
                    .referrer("http://www.google.com")
                    .timeout(5000)
                    .followRedirects(true)
                    .execute();

            Document doc = response.parse();
            Element googleFlightCard = doc.select("div[class=card-section vk_c]").first();
            Elements datos = googleFlightCard.select("tr[class=_FJg").first().children();
            mOldGoogle = mCurrentGoogle;
            mCurrentGoogle = new GoogleFlight(datos);
            mCurrentGoogle.code = code;

            AppendLog.appendLog("..primera info de timer: " + mCurrentGoogle.toString());
            //TOdo Compare on e state with the previous and rise notification if disctinnt
        } catch (IOException e) {
            AppendLog.appendLog("---error en sync:" + e.getMessage());
        }

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

        public flightObject(Card parentCard) {
            super(parentCard);
            init();
        }

        private void init() {
            //OnClick Listener
            setOnItemClickListener(new CardWithList.OnItemClickListener() {
                @Override
                public void onItemClick(LinearListView parent, View view, int position, CardWithList.ListObject object) {
//                    Toast.makeText(getContext(), "Click on " + getObjectId(), Toast.LENGTH_SHORT).show();
                    AppendLog.appendLog("asgink google for flight for first time: " + getObjectId());
                    GetInfoFlightFromGoogle(getObjectId()); //First retrieving
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
    }

    class readGoogleFlight extends AsyncTask<String, Void, GoogleFlight> {
        private OnTaskCompleted listener;

        readGoogleFlight(OnTaskCompleted listener) {
            AppendLog.appendLog("asignando listener en parseGoogleflight");
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
//                Toast.makeText(mContext, googleFlight.toString(), Toast.LENGTH_SHORT).show();
                AppendLog.appendLog("primera vista de google:" + googleFlight);
            } catch (Throwable t) {
                AppendLog.appendLog("-eeeoo frist query a google flifht" + t.getMessage());
            }
            return googleFlight;
        }

        @Override
        protected void onPostExecute(GoogleFlight googleFlight) {
            super.onPostExecute(googleFlight);
            ArrayList buff = new ArrayList<>();
            buff.add(googleFlight);
            listener.OnTaskCompleted(buff);
        }

    }
}
