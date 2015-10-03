package com.herenow.fase1.Activities;

import it.gmariotti.cardslib.library.internal.Card;

/**
 * Created by Milenko on 30/09/2015.
 */
public interface cardLoadedListener {
    void OnCardReady(Card card);
    void OnCardErrorLoadingData(Exception e);
}
