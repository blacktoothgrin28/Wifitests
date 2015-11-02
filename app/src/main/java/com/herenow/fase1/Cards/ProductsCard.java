package com.herenow.fase1.Cards;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.herenow.fase1.CardData.MenuSection;
import com.herenow.fase1.CardData.ProductItem;
import com.herenow.fase1.CardData.ProductsData;
import com.herenow.fase1.Cards.Components.CardHeader2;
import com.herenow.fase1.R;
import com.herenow.fase1.actions.Actions;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.prototypes.CardWithList;
import it.gmariotti.cardslib.library.prototypes.LinearListView;

/**
 * Created by Milenko on 19/09/2015.
 */


public class ProductsCard extends CardWithList {

    private ProductsData mProductData;

    public ProductsCard(Context context) {
        super(context);
    }

    public ProductsCard(Context context, int innerLayout) {
        super(context, innerLayout);
    }

    public void setData(ProductsData productsData) {
        mProductData = productsData;
    }

    @Override
    protected CardHeader initCardHeader() {

        //Add Header
        CardHeader2 header = new CardHeader2(getContext(), R.layout.schedule_inner_header);

        header.setTitle("Products");
        header.setSubTitle("And services");
        header.setDate("");
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
        List<ListObject> mObjects = prepareChildren(mProductData.getProducts());
        return mObjects;
    }

    private List<ListObject> prepareChildren(ArrayList<ProductItem> products) {

        List<ListObject> mObjects = new ArrayList<>();

        //Add an object to the list
        for (ProductItem item : products) {
            final ProductObject so = new ProductObject(mParentCard, item);

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
        TextView description = (TextView) convertView.findViewById(R.id.description);
        ImageView image = (ImageView) convertView.findViewById(R.id.product_image);
        TextView keyWords = (TextView) convertView.findViewById(R.id.key_words);

        //Retrieve the values from the object
        ProductItem pi = ((ProductObject) object).getProductItem();
        name.setText(pi.getName());
        description.setText(pi.getDescription());
        keyWords.setText(pi.getKeyWords());

        if (image != null) {
            Picasso.with(mContext).load(pi.getImageUrl())
                    .error(R.drawable.abc_ic_ab_back_mtrl_am_alpha)
                    .placeholder(R.mipmap.ic_launcher).fit().centerCrop()
                    .into(image);
        }

        //Todo put icons an filter by type of food (vegan, gluten, etc)

        return convertView;
    }

    @Override
    public int getChildLayoutId() {
        return R.layout.products_card_inner_main;
    }


    class ProductObject extends DefaultListObject {


        public ProductItem getProductItem() {
            return productItem;
        }

        private ProductItem productItem;

        public ProductObject(Card parentCard) {
            super(parentCard);
            init();
        }

        public ProductObject(Card parentcard, ProductItem productItem) {
            super(parentcard);
            this.productItem = productItem;

            init();
        }

        private void init() {
            //OnClick Listener
            setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(LinearListView parent, View view, int position, ListObject object) {
//                    Toast.makeText(getContext(), "Click on " + getObjectId(), Toast.LENGTH_SHORT).show();
                    Actions.OpenWebPage(mContext, ((ProductObject) object).getImageUrl());

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


        public String getImageUrl() {
            return productItem.getImageUrl();
        }
    }
}