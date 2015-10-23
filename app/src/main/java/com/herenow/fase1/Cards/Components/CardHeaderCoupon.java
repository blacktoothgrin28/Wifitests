package com.herenow.fase1.Cards.Components;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.herenow.fase1.R;
import com.squareup.picasso.Picasso;

import it.gmariotti.cardslib.library.internal.CardHeader;

/**
 * Created by Milenko on 21/09/2015.
 */
public class CardHeaderCoupon extends CardHeader {
    private String subTitle;

    private String imageUrl;

    public CardHeaderCoupon(Context context) {
        super(context);
    }

    public CardHeaderCoupon(Context context, int innerLayout) {
        super(context, innerLayout);
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        super.setupInnerViewElements(parent, view);
        if (view != null) {
            TextView mSubTitleView = (TextView) view.findViewById(R.id.card_header_inner_simple_subtitle);
            if (mSubTitleView != null) {
                mSubTitleView.setText(this.subTitle);
            }

            ImageView iv = (ImageView) view.findViewById(R.id.image_coupon);
            if (iv != null) {
                Picasso.with(mContext).load(imageUrl)
                        .error(R.drawable.abc_ic_ab_back_mtrl_am_alpha)
                        .placeholder(R.mipmap.ic_launcher).fit().centerCrop()
                        .into(iv);
            }

        }

    }
}
