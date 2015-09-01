package com.herenow.fase1.Cards;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.herenow.fase1.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.prototypes.CardWithList;
import it.gmariotti.cardslib.library.prototypes.LinearListView;

/**
 * Created by Milenko on 12/08/2015.
 */
public class AirportCard extends CardWithList {

    public AirportCard(Context context) {
        super(context);
    }

    public AirportCard(Context context, int innerLayout) {
        super(context, innerLayout);
    }

    public void setData(HashMap flightsData) {//TODO

    }


    @Override
    protected CardHeader initCardHeader() {

        //Add Header
        CardHeader header = new CardHeader(getContext(), R.layout.carddemo_googlenowweather_inner_header);

        header.setTitle("Fligths"); //should use R.string.
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
    protected List<CardWithList.ListObject> initChildren() {

        //Init the list
        List<CardWithList.ListObject> mObjects = new ArrayList<CardWithList.ListObject>();

        //Add an object to the list
        FlightObject w1 = new FlightObject(this);
        w1.city = "London";
        w1.time = "11:30";
//        w1.temperature = 16;
//        w1.weatherIcon = R.drawable.ic_action_cloud;
        w1.setObjectId(w1.city); //It can be important to set ad id
        mObjects.add(w1);

        FlightObject w2 = new FlightObject(this);
        w2.city = "Rome";
        w2.time = "11:33";
//        w2.temperature = 25;
//        w2.weatherIcon = R.drawable.ic_action_sun;
        w2.setObjectId(w2.city);
        w2.setSwipeable(true);

        //Example onSwipe
        /*w2.setOnItemSwipeListener(new OnItemSwipeListener() {
            @Override
            public void onItemSwipe(ListObject object,boolean dismissRight) {
                Toast.makeText(getContext(), "Swipe on " + object.getObjectId(), Toast.LENGTH_SHORT).show();
            }
        });*/
        mObjects.add(w2);

        FlightObject w3 = new FlightObject(this);
        w3.city = "Paris";
        w1.time = "11:39";
//        w3.temperature = 19;
//        w3.weatherIcon = R.drawable.ic_action_cloudy;
        w3.setObjectId(w3.city);
        mObjects.add(w3);

        return mObjects;
    }

    @Override
    public View setupChildView(int childPosition, CardWithList.ListObject object, View convertView, ViewGroup parent) {

        //Setup the ui elements inside the item
        TextView city = (TextView) convertView.findViewById(R.id.carddemo_weather_city);
//        ImageView icon = (ImageView) convertView.findViewById(R.id.carddemo_weather_icon);
        TextView time = (TextView) convertView.findViewById(R.id.carddemo_weather_temperature);

        //Retrieve the values from the object
        FlightObject flightObject = (FlightObject) object;
//        icon.setImageResource(flightObject.weatherIcon);
        city.setText(flightObject.city);
        time.setText(flightObject.time);

        return convertView;
    }

    @Override
    public int getChildLayoutId() {
        return R.layout.carddemo_googlenowweather_inner_main;
    }


    // -------------------------------------------------------------
    // Weather Object
    // -------------------------------------------------------------

    public class FlightObject extends CardWithList.DefaultListObject {

        public String city;
        //        public int weatherIcon;
//        public int temperature;
        public String temperatureUnit = "°C";
        public String time;

        public FlightObject(Card parentCard) {
            super(parentCard);
            init();
        }

        private void init() {
            //OnClick Listener
            setOnItemClickListener(new CardWithList.OnItemClickListener() {
                @Override
                public void onItemClick(LinearListView parent, View view, int position, CardWithList.ListObject object) {
                    Toast.makeText(getContext(), "Click on " + getObjectId(), Toast.LENGTH_SHORT).show();
                }
            });

            //OnItemSwipeListener
            setOnItemSwipeListener(new CardWithList.OnItemSwipeListener() {
                @Override
                public void onItemSwipe(CardWithList.ListObject object, boolean dismissRight) {
                    Toast.makeText(getContext(), "Swipe on " + object.getObjectId(), Toast.LENGTH_SHORT).show();
                }
            });
        }

    }


}
