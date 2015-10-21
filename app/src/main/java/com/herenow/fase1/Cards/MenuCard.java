package com.herenow.fase1.Cards;

import android.content.Context;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.herenow.fase1.CardData.FoodMenu;
import com.herenow.fase1.CardData.MenuSection;
import com.herenow.fase1.Cards.Components.CardHeader2;
import com.herenow.fase1.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.base.BaseCard;
import it.gmariotti.cardslib.library.prototypes.CardWithList;
import it.gmariotti.cardslib.library.prototypes.LinearListView;
import util.myLog;

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
//            ChangeFoodMenuTo(item.getItemId()); TODO create method to change section
            }
        });

        header.setTitle("Menu");
        header.setSubTitle("Main course");
        header.setDate("Updated at " + mFoodMenuData.getName());
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

        ArrayList<MenuSection.Dish> dishes = mFoodMenuData.getDishesFirstSection();

        myLog.add("La seccion elegida tiene dishes:" + dishes.size());
        //Add an object to the list
        for (MenuSection.Dish dish : dishes) {
            final FoodObject so = new FoodObject(mParentCard, dish);
            myLog.add("este so tiene " + so);

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
        myLog.add("hemos initchildren estos mObjects " + mObjects.size());
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
        return R.layout.food_card_inner_main;
    }


    // -------------------------------------------------------------
    // Object
    // -------------------------------------------------------------

    private void ShowFoodPicture(FoodObject object) {
        String imageUrl = object.getImageUrl();
        //TODO shows an activity with picture and description of the food
    }

    class FoodObject extends DefaultListObject {

        private String name, description = "";
        private double price;
        private String[] ingredients;
        private String imageUrl;


        public FoodObject(Card parentCard) {
            super(parentCard);
            init();
        }

        public FoodObject(Card parentcard, MenuSection.Dish dish) {
            super(parentcard);
            name = dish.name;
            description = dish.description;
            price = dish.price;
            ingredients = dish.ingredients;
            imageUrl = dish.imageUrl;

            myLog.add("** Init: " + this);
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


        public String getPrice() {
            return Double.toString(price);
        }

        public String getDescription() {
            return description;
        }

        public String getIngredients() {
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < ingredients.length - 1; i++) {
                sb.append(ingredients[i] + ", ");
            }
            sb.append(ingredients[ingredients.length - 1]);

            return sb.toString();
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return "FoodObject{" +
                    "name='" + name + '\'' +
                    ", description='" + description + '\'' +
                    ", price=" + price +
                    ", ingredients=" + Arrays.toString(ingredients) +
                    ", imageUrl='" + imageUrl + '\'' +
                    '}';
        }
    }


}