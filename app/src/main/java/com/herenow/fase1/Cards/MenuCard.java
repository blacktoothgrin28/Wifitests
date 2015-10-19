package com.herenow.fase1.Cards;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.herenow.fase1.CardData.FoodMenu;
import com.herenow.fase1.Cards.Components.CardHeader2;
import com.herenow.fase1.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.base.BaseCard;
import it.gmariotti.cardslib.library.prototypes.CardWithList;
import it.gmariotti.cardslib.library.prototypes.LinearListView;

/**
 * Created by Milenko on 19/09/2015.
 */


public class MenuCard extends CardWithList {

    private String lang = "en";
    private FoodMenu mFoodMenuData;

    public MenuCard(Context context) {
        super(context);
        lang = Locale.getDefault().getDisplayLanguage();
        Toast.makeText(context, "The menu will be displayed in " + lang, Toast.LENGTH_SHORT).show();
    }

    public MenuCard(Context context, int innerLayout) {
        super(context, innerLayout);
    }

    public void setData(FoodMenu foodMenu) {
        mFoodMenuData = foodMenu;
    }

    @Override
    protected CardHeader initCardHeader() {

        //Add Header
        CardHeader2 header = new CardHeader2(getContext(), R.layout.schedule_inner_header);

        //Add a popup menu. This method sets OverFlow button to visible
        header.setPopupMenu(R.menu.popupmain, new CardHeader.OnClickCardHeaderPopupMenuListener() {
            @Override
            public void onMenuItemClick(BaseCard card, MenuItem item) {
                Toast.makeText(mContext, "Click on " + item.getTitle(), Toast.LENGTH_SHORT).show();
//            ChangeFoodMenuTo(item.getItemId()); TODO create method to change menu
            }
        });

        header.setTitle("Menu"); //should use R.string.
        header.setSubTitle("Main course");
        header.setDate("Upadated at " + mFoodMenuData.getDateString());
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

        //Init the list
        List<ListObject> mObjects = new ArrayList<>();

        //Add an object to the list
        for (FoodMenu.FoodItem it : mFoodMenuData.getData()) {
            final FoodObject so = new FoodObject(mParentCard, it);

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
        TextView price = (TextView) convertView.findViewById(R.id.price);
        TextView description = (TextView) convertView.findViewById(R.id.description);
        TextView ingredients = (TextView) convertView.findViewById(R.id.ingredients);

        //Retrieve the values from the object
        FoodObject foodObject = (FoodObject) object;
        name.setText(foodObject.getName());
        price.setText(foodObject.getPrice());
        description.setText(foodObject.getDescription());
        ingredients.setText(foodObject.getIngredients());

        //Todo put icons an filter by type of food (vegan, gluten, etc)

        return convertView;
    }

    @Override
    public int getChildLayoutId() {
        return R.layout.schedule_card_inner_main;
    }


    // -------------------------------------------------------------
    // Object
    // -------------------------------------------------------------

    private void ShowFoodPicture(FoodObject object) {
        Bitmap image = object.getImage();
        //TODO shows an activity with picture and description of the food
    }

    class FoodObject extends DefaultListObject {

        private String description = "";
        private int name;
        private int price;
        private String ingredients;
        private Bitmap image;


        public FoodObject(Card parentCard) {
            super(parentCard);
            init();
        }

        public FoodObject(Card parentcard, FoodMenu.FoodItem it) {
            super(parentcard);
//            title = it.getTitle();
//            hour = it.getHour();
//            min = it.getMin();
//            h = it.getH();
//            location = it.getPlace();
//            speaker = it.getSpeaker();
//            description = it.getDescription();
//            url = it.getUrl();
//            startInMilli = it.getStartInMilli();
//            endInMilli = it.getEndInMilli();
//            fileUrl = it.getUrlFile();
            init();

        }

        private void init() {
            //OnClick Listener
            setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(LinearListView parent, View view, int position, ListObject object) {
//                    Toast.makeText(getContext(), "Click on " + getObjectId(), Toast.LENGTH_SHORT).show();
                    ShowFoodPicture((FoodObject) object);

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

        public int getName() {
            return name;
        }

        public String getPrice() {
            return Long.toString(price);
        }

        public String getDescription() {
            return description;
        }

        public String getIngredients() {
            return ingredients;
        }

        public Bitmap getImage() {
            //TODO put image
            return image;
        }
    }


}