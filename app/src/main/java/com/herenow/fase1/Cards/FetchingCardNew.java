package com.herenow.fase1.Cards;

import android.content.Context;
import android.widget.Toast;

import com.herenow.fase1.Activities.CardLoadedInterface;
import com.herenow.fase1.R;
import com.herenow.fase1.fetchers.notificationFetcher;

import java.util.ArrayList;
import java.util.List;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.prototypes.CardWithList;
import parse.WeaconParse;
import util.MultiTaskCompleted;
import util.myLog;

/**
 * Created by Milenko on 19/09/2015.
 */

public abstract class FetchingCardNew extends CardWithList {

    protected notificationFetcher fetcher;
    protected WeaconParse mWe = new WeaconParse();
    protected CardLoadedInterface cardLoadedListener;
    protected ArrayList<ListObject> mElementsToShow;

    private String mTitle;
    private int myInnerLayout;

    public FetchingCardNew(Context context, WeaconParse we, int innerLayout, CardLoadedInterface listener) {
        super(context);
        cardLoadedListener = listener;
        mWe = we;
        mTitle = we.getName();
        myInnerLayout = innerLayout;
    }

    protected void setFetcher(notificationFetcher mfetcher) {
        myLog.add("***entrando a schedule fetch", "test");
        fetcher = mfetcher.setListener(new MultiTaskCompleted() {
            @Override
            public void OneTaskCompleted() {
                AllReady();
            }

            @Override
            public void OnError(Exception e) {
                //TODO
            }
        }, mWe); //mWe is an empty weacon that is used to store the results of fetching

        fetcher.execute();
    }

    private void AllReady() {
        myLog.add("ontaskcompleted:" + mWe.getFetchedElements().size() + " Elements");
        mElementsToShow = convertToListObjects(mWe.getFetchedElements());
        super.init();
        cardLoadedListener.OnCardReady(this, R.layout.native_cardwithlist_layout2);
    }

    protected abstract ArrayList<ListObject> convertToListObjects(ArrayList fetchedElements);

//    public void setListener(CardLoadedListener listener) {
//        myLog.add("setting listener on fechtinc card (card loaded)","test");
//
//        this.cardLoadedListener = listener;
//    }

    @Override
    protected CardHeader initCardHeader() {
        CardHeader header = new CardHeader(getContext(), R.layout.listcard_inner_header);
        header.setTitle(mTitle);
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

        setEmptyViewViewStubLayoutId(R.layout.carddemo_extras_base_withlist_empty);
        setUseProgressBar(true);
    }

    @Override
    protected List<ListObject> initChildren() {
        return mElementsToShow;
    }

    @Override
    public int getChildLayoutId() {
        return myInnerLayout;
    }


}



