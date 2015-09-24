package com.herenow.fase1.Cards;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.herenow.fase1.R;

import it.gmariotti.cardslib.library.internal.CardHeader;

/**
 * Created by Milenko on 21/09/2015.
 */
public class CardHeader2 extends CardHeader {
    private String subTitle;
    private String date;

    public CardHeader2(Context context) {
        super(context);
    }

    public CardHeader2(Context context, int innerLayout) {
        super(context, innerLayout);
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
            TextView mDateView = (TextView) view.findViewById(R.id.card_header_inner_simple_date);
            if (mDateView != null) {
                mDateView.setText(this.date);
            }
        }

    }

    public void setDate(String date) {
        this.date = date;
    }
}
