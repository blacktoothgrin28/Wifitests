package com.herenow.fase1.Activities;

import android.support.annotation.LayoutRes;

import it.gmariotti.cardslib.library.internal.Card;

/**
 * Created by Milenko on 30/09/2015.
 */
public interface cardLoadedListener {
    void OnCardReady(Card card,@LayoutRes int CardLayout);
    void OnCardErrorLoadingData(Exception e);
}
