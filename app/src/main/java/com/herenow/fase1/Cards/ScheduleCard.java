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
 * Created by Milenko on 19/09/2015.
 */


public class ScheduleCard extends CardWithList {

    public ScheduleCard(Context context) {
        super(context);
    }

    public ScheduleCard(Context context, int innerLayout) {
        super(context, innerLayout);
    }

    public void setData(HashMap scheduleData) {//TODO

    }

    @Override
    protected CardHeader initCardHeader() {

        //Add Header
        CardHeader header = new CardHeader(getContext(), R.layout.carddemo_googlenowweather_inner_header);

        header.setTitle("Schedule"); //should use R.string.
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

        //TODO asignar datos
        //Init the list
        List<CardWithList.ListObject> mObjects = new ArrayList<ListObject>();

        //Add an object to the list
//        FlightObject w1 = new FlightObject(this);
//        w1.city = "London";
//        w1.time = "11:30";
////        w1.temperature = 16;
////        w1.weatherIcon = R.drawable.ic_action_cloud;
//        w1.setObjectId(w1.city); //It can be important to set ad id
//        mObjects.add(w1);
//


        //Example onSwipe
        /*w2.setOnItemSwipeListener(new OnItemSwipeListener() {
            @Override
            public void onItemSwipe(ListObject object,boolean dismissRight) {
                Toast.makeText(getContext(), "Swipe on " + object.getObjectId(), Toast.LENGTH_SHORT).show();
            }
        });*/


        return mObjects;
    }

    @Override
    public View setupChildView(int childPosition, CardWithList.ListObject object, View convertView, ViewGroup parent) {

        //Setup the ui elements inside the item
        TextView title = (TextView) convertView.findViewById(R.id.carddemo_weather_city);
//        ImageView icon = (ImageView) convertView.findViewById(R.id.carddemo_weather_icon);
        TextView time = (TextView) convertView.findViewById(R.id.carddemo_weather_temperature);
//TextView place=(TextView) convertView.findViewById(R.id.carddemo_weather_temperature);
//Todo agregar el place

        //Retrieve the values from the object
        ScheduleItem scheduleItem = (ScheduleItem) object;
//        icon.setImageResource(flightObject.weatherIcon);
        title.setText(scheduleItem.title);
        time.setText(scheduleItem.hour);

        return convertView;
    }

    @Override
    public int getChildLayoutId() {
        return R.layout.carddemo_googlenowweather_inner_main;
    }


    // -------------------------------------------------------------
    // event Object
    // -------------------------------------------------------------

    class ScheduleItem extends CardWithList.DefaultListObject {
        public String url;
        String title, hour, urlImage, location;
        int h, min;

        public ScheduleItem(Card parentCard) {
            super(parentCard);
            init();
        }

        private void init() {
            //OnClick Listener
            setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(LinearListView parent, View view, int position, ListObject object) {
                    Toast.makeText(getContext(), "Click on " + getObjectId(), Toast.LENGTH_SHORT).show();

//                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getObjectId()));
//                    mContext.startActivity(browserIntent);
                    //TODO hacer que se agregue a la agneda o la alerma
                    //TODO ver si poner un botón para una url
                    //TODO ver si se puede descargar algún archivo

                }
            });

            //OnItemSwipeListener
            setOnItemSwipeListener(new OnItemSwipeListener() {
                @Override
                public void onItemSwipe(ListObject object, boolean dismissRight) {
                    Toast.makeText(getContext(), "Swipe on " + object.getObjectId(), Toast.LENGTH_SHORT).show();
                }
            });
        }

    }


}