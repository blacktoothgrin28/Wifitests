package com.herenow.fase1.Cards;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.herenow.fase1.CardData.ChefData;
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


public class ChefCard extends CardWithList {

    private ChefData mChefData;

    public ChefCard(Context context) {
        super(context);
    }

    public ChefCard(Context context, int innerLayout) {
        super(context, innerLayout);
    }

    public void setData(ChefData chefData) {
        mChefData = chefData;
    }

    @Override
    protected CardHeader initCardHeader() {

        //Add Header
        CardHeader2 header = new CardHeader2(getContext(), R.layout.day_menu_inner_header);

        header.setTitle("Chef's Recommendation");
        header.setSubTitle(mChefData.getName());
        header.setDate(mChefData.getPrice() + "€");
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


        final RecommendationObject so = new RecommendationObject(mParentCard, mChefData);

        //Example onSwipe
        so.setOnItemSwipeListener(new OnItemSwipeListener() {
            @Override
            public void onItemSwipe(ListObject object, boolean dismissRight) {
//                    Toast.makeText(getContext(), "Downloading presentation\n\"" + so.title + "\" by "
//                            + so.speaker, Toast.LENGTH_SHORT).show();

            }
        });
        mObjects.add(so);

        return mObjects;
    }


    @Override
    public View setupChildView(int childPosition, ListObject object, View convertView, ViewGroup parent) {

        //Setup the ui elements inside the item
        TextView name = (TextView) convertView.findViewById(R.id.name);
        TextView description = (TextView) convertView.findViewById(R.id.description);
        ImageView imageView=(ImageView)convertView.findViewById(R.id.image_dish);

        //Retrieve the values from the object
        RecommendationObject reco = (RecommendationObject) object;
        name.setText(reco.getName());
        description.setText(reco.getDescription());

        return convertView;
    }

    @Override
    public int getChildLayoutId() {
        return R.layout.day_food_card_inner_main;
    }


    // -------------------------------------------------------------
    // Object
    // -------------------------------------------------------------


    class RecommendationObject extends CardWithList.DefaultListObject {

        private String name, imageUrl, description;


        public RecommendationObject(Card parentCard) {
            super(parentCard);
            init();
        }

        public RecommendationObject(Card parentcard, ChefData chefData) {
            super(parentcard);
            name = chefData.getName();
            description = chefData.getDescription();
            imageUrl = chefData.getImageUrl();
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

        public String getDescription() {
            return description;
        }
    }
}