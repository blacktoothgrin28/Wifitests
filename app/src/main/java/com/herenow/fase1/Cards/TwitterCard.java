package com.herenow.fase1.Cards;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.herenow.fase1.Activities.cardLoadedListener;
import com.herenow.fase1.R;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.prototypes.CardWithList;
import it.gmariotti.cardslib.library.prototypes.LinearListView;
import util.OnTaskCompleted;
import util.myLog;

/**
 * Created by Milenko on 19/09/2015.
 */


public class TwitterCard extends CardWithList implements OnTaskCompleted {
    private String mCompanyTwitterUrl, mCompanyTwitterUser;
    private ArrayList<ListObject> mTweetsToShow;
    private cardLoadedListener listener;

    public TwitterCard(Context context, String companyTwitterUser) {
        super(context);
        mCompanyTwitterUser = companyTwitterUser;
        mCompanyTwitterUrl = "https://mobile.twitter.com/" + companyTwitterUser;
    }


    @Override
    protected CardHeader initCardHeader() {
        CardHeader header = new CardHeader(getContext(), R.layout.listcard_inner_header);
        header.setTitle("Tweets @" + mCompanyTwitterUser);
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
        (new ParseURLTwitter(this)).execute(new String[]{mCompanyTwitterUrl});//async
    }

    @Override
    protected List<ListObject> initChildren() {

        //Init the list
//        List<ListObject> mObjects = new ArrayList<ListObject>();
//
//        ListObject lalo = mTweetsToShow.get(1);
//
//        mObjects=(ListObject)mTweetsToShow;
        //Example onSwipe
        /*w2.setOnItemSwipeListener(new OnItemSwipeListener() {
            @Override
            public void onItemSwipe(ListObject object,boolean dismissRight) {
                Toast.makeText(getContext(), "Swipe on " + object.getObjectId(), Toast.LENGTH_SHORT).show();
            }
        });*/

        return mTweetsToShow;
    }

    @Override
    public View setupChildView(int childPosition, ListObject object, View convertView, ViewGroup parent) {

        try {
            TextView name = (TextView) convertView.findViewById(R.id.name);
            TextView alias = (TextView) convertView.findViewById(R.id.alias);
            TextView date = (TextView) convertView.findViewById(R.id.date);
            TextView content = (TextView) convertView.findViewById(R.id.content);

            //Retrieve the values from the object
            Tweet tweet = (Tweet) object;
            name.setText(tweet.name);
            alias.setText(tweet.alias);
            date.setText(tweet.date);
            content.setText(tweet.content);


        } catch (Exception e) {
            myLog.add("---Error en setup child tweets: " + e.getMessage());
        }

        return convertView;
    }

    @Override
    public int getChildLayoutId() {
        return R.layout.twitter_inner_main;
    }

    @Override
    public void OnTaskCompleted(ArrayList elements) {
        try {
            myLog.add("ontaskcompleted:" + elements.size() + " tweets");
            mTweetsToShow = new ArrayList<>();
            //Select three tweets with name of place
            for (Object ob : elements) {
                Tweet tw = (Tweet) ob;
                if (tw.alias.toLowerCase().equals("@" + mCompanyTwitterUser.toLowerCase())) {
                    mTweetsToShow.add(tw);
                }
            }
            myLog.add("Hemos seleccionado:" + mTweetsToShow.size() + " tweets");
            super.init();

            //Set a clickListener on Header Area
            this.addPartialOnClickListener(Card.CLICK_LISTENER_HEADER_VIEW, new Card.OnCardClickListener() {
                @Override
                public void onClick(Card card, View view) {
                    Toast.makeText(mContext, "Click on Header Area", Toast.LENGTH_LONG).show();
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mCompanyTwitterUrl));
                    mContext.startActivity(browserIntent);

                }
            });
            listener.OnCardReady(this, R.layout.native_cardwithlist_layout2);
        } catch (Exception e) {
            listener.OnCardErrorLoadingData(e);
        }

//        mCardViewNews.setCard(this);
    }

    @Override
    public void OnError(Exception e) {
        //TODO
    }

    public void setListener(cardLoadedListener listener) {
        this.listener = listener;
    }
//    public void setView(CardViewNative cardViewNews) {
//        mCardViewNews = cardViewNews;
//    }


    // -------------------------------------------------------------
    //  Object
    // -------------------------------------------------------------

    class Tweet extends DefaultListObject {
        String name, alias, date, content, link;

        public Tweet(Card parentCard) {
            super(parentCard);
            init();
        }

        private void init() {
            //OnClick Listener
            setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(LinearListView parent, View view, int position, ListObject object) {
                    Toast.makeText(getContext(), "Click on tweet", Toast.LENGTH_SHORT).show();

                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getObjectId()));
                    mContext.startActivity(browserIntent);
                }
            });

            //OnItemSwipeListener
            setOnItemSwipeListener(new OnItemSwipeListener() {
                @Override
                public void onItemSwipe(ListObject object, boolean dismissRight) {
                    Toast.makeText(getContext(), "Swiped", Toast.LENGTH_SHORT).show();
                }
            });
        }


    }

    class ParseURLTwitter extends AsyncTask<String, Void, ArrayList<Tweet>> {
        private OnTaskCompleted listener;

        ParseURLTwitter(OnTaskCompleted listener) {
            this.listener = listener;
        }

        @Override
        protected ArrayList<Tweet> doInBackground(String... strings) {
            ArrayList<Tweet> tweets = new ArrayList();
            try {
                Connection.Response response = Jsoup.connect(strings[0])
                        .ignoreContentType(true)
//                        .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
//                        .userAgent("Mozilla/5.0 (Windows NT 6.3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.85 Safari/537.36")
                        .referrer("http://www.google.com")
                        .timeout(12000)
                        .followRedirects(true)
                        .execute();

                Document doc = response.parse();
//                Document doc = Jsoup.connect(strings[0]).get();
                Elements tweetsRaw = doc.select("div[role=row]");//.first().select("ol").first().select("li");
//
                for (Element tweet : tweetsRaw) {
                    tweets.add(ProcessHtmlTweet(tweet));
                }

            } catch (Throwable t) {
                myLog.add("--Error en twitter card: " + t);
            }
            return tweets;
        }

        private Tweet ProcessHtmlTweet(Element element) {
            Tweet tweet = new Tweet(mParentCard);
            try {
                Element main = element.select("div [class=Tweet-text TweetText u-textBreak u-dir").first();
                tweet.content = main.text();
                tweet.link = main.child(0).attr("href");

                Element first = element.select("div [class= u-nbfc]").first().child(0);
                tweet.name = first.child(0).text();
                tweet.alias = first.child(2).text();
                tweet.date = element.select("time").first().text();
                tweet.setObjectId(tweet.link);

            } catch (Exception e) {
                myLog.add("errror capturanfo tweet " + e.getMessage());
            }

            return tweet;
        }

        @Override
        protected void onPostExecute(ArrayList<Tweet> tweets) {
            super.onPostExecute(tweets);
            listener.OnTaskCompleted(tweets);
        }
    }
}



