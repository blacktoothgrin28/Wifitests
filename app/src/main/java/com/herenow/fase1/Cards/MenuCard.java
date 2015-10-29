package com.herenow.fase1.Cards;

import android.app.Dialog;
import android.content.Context;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.herenow.fase1.CardData.FoodMenu;
import com.herenow.fase1.CardData.MenuSection;
import com.herenow.fase1.Cards.Components.CardHeader2;
import com.herenow.fase1.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.base.BaseCard;
import it.gmariotti.cardslib.library.prototypes.CardWithList;
import it.gmariotti.cardslib.library.prototypes.LinearListView;
import util.stringUtils;

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

        header.setButtonOverflowVisible(true);
        //On item click
        header.setPopupMenuListener(new CardHeader.OnClickCardHeaderPopupMenuListener() {
            @Override
            public void onMenuItemClick(BaseCard card, MenuItem item) {
//                Toast.makeText(mContext, "Click on " + item.getTitle() + "-" +
//                        ((Card) card).getCardHeader().getTitle(), Toast.LENGTH_SHORT).show();
                ChangeFoodMenuTo((String) item.getTitle());
            }
        });

        //Add items to the menu popup
        header.setPopupMenuPrepareListener(new CardHeader.OnPrepareCardHeaderPopupMenuListener() {
            @Override
            public boolean onPreparePopupMenu(BaseCard card, PopupMenu popupMenu) {

                ArrayList<String> sectionsNames = mFoodMenuData.getSectionNames();
                for (String sn : sectionsNames) {
                    popupMenu.getMenu().add(sn);
                }

                return true;
            }
        });

        header.setTitle("Menu");
        header.setSubTitle("Main course");
        header.setDate("Updated at " + mFoodMenuData.getName());
        return header;
    }

    private void ChangeFoodMenuTo(String sectionName) {
        //Update the subtitle
        ((CardHeader2) this.getCardHeader()).setSubTitle(sectionName);

        //Update content
        List<ListObject> mChildren = initChildren(sectionName);
        if (mChildren == null)
            mChildren = new ArrayList<ListObject>();
        mLinearListAdapter = new LinearListAdapter(super.getContext(), mChildren);
        mLinearListAdapter.notifyDataSetChanged();

        this.notifyDataSetChanged();
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
        List<ListObject> mObjects = prepareChildren(mFoodMenuData.getSection(2));
        return mObjects;
    }

    private List<ListObject> prepareChildren(MenuSection section) {

        List<ListObject> mObjects = new ArrayList<>();
        ArrayList<MenuSection.Dish> dishes = section.getDishes();

        //Add an object to the list
        for (MenuSection.Dish dish : dishes) {
            final FoodObject so = new FoodObject(mParentCard, dish);

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

    private List<ListObject> initChildren(String sectionName) {
        List<ListObject> mObjects = prepareChildren(mFoodMenuData.getSection(sectionName));
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
    private void ShowFoodPicture(FoodObject dish) {
        final Dialog d = new Dialog(mContext);
        d.setTitle(dish.name);
        d.setContentView(R.layout.food_preview);
        ImageView iv = (ImageView) d.findViewById(R.id.dish_image);
        TextView tv = (TextView) d.findViewById(R.id.dish_description);

//        Button btCancel = (Button) d.findViewById(R.id.button1);
//        Button bSet = (Button) d.findViewById(R.id.button2);

        iv.setClickable(true);
        tv.setClickable(true);

        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });

        //SET CONTENT
        tv.setText(dish.description);
        if (iv != null) {
            Picasso.with(mContext).load(dish.getImageUrl())
                    .error(R.drawable.abc_ic_ab_back_mtrl_am_alpha)
                    .placeholder(R.mipmap.ic_launcher).fit().centerCrop()
                    .into(iv);
        }

        d.show();
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
            imageUrl = dish.getImageUrl();

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
            return stringUtils.ConcatenateComma(ingredients);
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