package com.herenow.fase1.Cards;

import android.content.Context;

import it.gmariotti.cardslib.library.cards.material.MaterialLargeImageCard;
import it.gmariotti.cardslib.library.cards.material.MaterialLargeImageCardThumbnail;

/**
 * Created by Milenko on 03/09/2015.
 */
public class CompanyCardTest extends MaterialLargeImageCard {

    private int mDrawableCardThumbnailIcon;

    public CompanyCardTest(Context context) {
        super(context);
    }

    public void setmDrawableCardThumbnailIcon(int mDrawableCardThumbnailIcon) {
        this.mDrawableCardThumbnailIcon = mDrawableCardThumbnailIcon;
    }
}
