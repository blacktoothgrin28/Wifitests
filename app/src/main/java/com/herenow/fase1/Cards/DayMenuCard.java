package com.herenow.fase1.Cards;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.herenow.fase1.CardData.DayFoodMenu;
import com.herenow.fase1.CardData.MenuSection;
import com.herenow.fase1.Cards.Components.CardHeader2;
import com.herenow.fase1.R;

import java.util.ArrayList;
import java.util.List;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.prototypes.CardWithList;
import it.gmariotti.cardslib.library.prototypes.LinearListView;

/**
 * Created by Milenko on 19/09/2015.
 */


public class DayMenuCard extends CardWithList {

    private DayFoodMenu mDayFoodMenuData;

    public DayMenuCard(Context context) {
        super(context);
//        Toast.makeText(context, "The menu will be displayed in " + lang, Toast.LENGTH_SHORT).show();
    }

    public DayMenuCard(Context context, int innerLayout) {
        super(context, innerLayout);
    }

    public void setData(DayFoodMenu foodMenu) {
        mDayFoodMenuData = foodMenu;
    }

    @Override
    protected CardHeader initCardHeader() {

        //Add Header
        CardHeader2 header = new CardHeader2(getContext(), R.layout.day_menu_inner_header);

        header.setTitle("Today's Menu");
        header.setSubTitle(mDayFoodMenuData.getName());
        header.setDate(mDayFoodMenuData.getPrice() + "€");
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

        List<ListObject> mObjects = new ArrayList<>();

        //Add an object to the list
        for (MenuSection section : mDayFoodMenuData.getSections()) {

            final SectionObject so = new SectionObject(mParentCard, section);

            //Example onSwipe
            so.setOnItemSwipeListener(new OnItemSwipeListener() {
                @Override
                public void onItemSwipe(ListObject object, boolean dismissRight) {
//                    Toast.makeText(getContext(), "Downloading presentation\n\"" + so.title + "\" by "
//                            + so.speaker, Toast.LENGTH_SHORT).show();

                }
            });
            mObjects.add(so);
        }
        return mObjects;
    }


    @Override
    public View setupChildView(int childPosition, ListObject object, View convertView, ViewGroup parent) {

        //Setup the ui elements inside the item
        TextView name = (TextView) convertView.findViewById(R.id.name);
        TextView description1 = (TextView) convertView.findViewById(R.id.description1);
        TextView description2 = (TextView) convertView.findViewById(R.id.description2);
        TextView description3 = (TextView) convertView.findViewById(R.id.description3);

        //Retrieve the values from the object
        SectionObject sectionObject = (SectionObject) object;
        name.setText(sectionObject.getName());

        description1.setText(sectionObject.getDescription(0));
        description2.setText(sectionObject.getDescription(1));
        description3.setText(sectionObject.getDescription(2));

        //Todo put icons an filter by type of food (vegan, gluten, etc)

        return convertView;
    }

    @Override
    public int getChildLayoutId() {
        return R.layout.day_food_card_inner_main;
    }


    // -------------------------------------------------------------
    // Object
    // -------------------------------------------------------------


    class SectionObject extends DefaultListObject {

        private ArrayList<String> dishesDescriptions;
        private String dishes;
        private String name = "";
        private double price;


        public SectionObject(Card parentCard) {
            super(parentCard);
            init();
        }

        public SectionObject(Card parentcard, MenuSection section) {
            super(parentcard);
            name = section.getName();
            dishesDescriptions = section.getDishesDescriptions();

            init();
        }

        private void init() {
            //OnClick Listener
            setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(LinearListView parent, View view, int position, ListObject object) {
//                    Toast.makeText(getContext(), "Click on " + getObjectId(), Toast.LENGTH_SHORT).show();
//                    ShowFoodPicture((FoodObject) object);

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


        public String getName() {
            return name;
        }

        public double getPrice() {
            return price;
        }

        public String getDescription(int i) {
            return dishesDescriptions.get(i);
        }
    }
}