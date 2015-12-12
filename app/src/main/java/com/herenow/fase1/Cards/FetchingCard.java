package com.herenow.fase1.Cards;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.herenow.fase1.Activities.cardLoadedListener;
import com.herenow.fase1.R;

import java.util.ArrayList;
import java.util.List;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.prototypes.CardWithList;
import util.OnTaskCompleted;
import util.myLog;

/**
 * Created by Milenko on 19/09/2015.
 */

public abstract class FetchingCard extends CardWithList implements OnTaskCompleted {

    private String mUrl, mTitle;
    private ArrayList<ListObject> mElementsToShow;
    private cardLoadedListener cardLoadedListener;
    private int myInnerLayout;

    public FetchingCard(Context context, String url, String title, int innerLayout) {
        super(context);
        mUrl = url;
        mTitle = title;
        myInnerLayout = innerLayout;
    }

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
    public void init() {
        (new FetchUrl(this)).execute(new String[]{mUrl});
    }

    @Override
    protected List<ListObject> initChildren() {
        return mElementsToShow;
    }

    @Override
    public int getChildLayoutId() {
        return myInnerLayout;
    }

    @Override
    public void OnTaskCompleted(ArrayList elements) {
        try {
            myLog.add("ontaskcompleted:" + elements.size() + " Elements");
            mElementsToShow = elements;
            super.init();

            cardLoadedListener.OnCardReady(this, R.layout.native_cardwithlist_layout2);
        } catch (Exception e) {
            myLog.add("---errorr en ontraskcompleted fetching " + e.getLocalizedMessage());
            cardLoadedListener.OnCardErrorLoadingData(e);
        }
    }

    public void setListener(cardLoadedListener listener) {
        myLog.add("setting listener on fechtinc card (card loaded)");
        this.cardLoadedListener = listener;
    }

    protected ArrayList ExtraccionObjetos(String url) {
        //to be overwritten
        myLog.add("usando la verison antigua de Extraction");
        return null;
    }

    class FetchUrl extends AsyncTask<String, Void, ArrayList<DefaultListObject>> {
        private OnTaskCompleted onTaskCompletedListener;

        FetchUrl(OnTaskCompleted listener) {
            this.onTaskCompletedListener = listener;
        }

        @Override
        protected ArrayList<DefaultListObject> doInBackground(String... strings) {
            return ExtraccionObjetos(strings[0]);
        }

        @Override
        protected void onPostExecute(ArrayList<DefaultListObject> employees) {
            super.onPostExecute(employees);
            onTaskCompletedListener.OnTaskCompleted(employees);
        }
    }
}



