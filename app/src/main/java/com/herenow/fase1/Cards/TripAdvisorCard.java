package com.herenow.fase1.Cards;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.herenow.fase1.Activities.cardLoadedListener;
import com.herenow.fase1.CardData.TripData;
import com.herenow.fase1.Cards.Components.CardHeader2;
import com.herenow.fase1.R;
import com.herenow.fase1.actions.Actions;

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

import static util.stringUtils.TrimFirstWords;

/**
 * Created by Milenko on 19/09/2015.
 */


public class TripAdvisorCard extends CardWithList implements OnTaskCompleted {

    private String mTripUrl;
    private ArrayList<ListObject> mCommentsToShow;
    private cardLoadedListener listener;
    private TripData mTripData;
//    private CardViewNative mCardViewNews;

    public TripAdvisorCard(Context context, String tripUrl) {
        super(context);
        mTripUrl = tripUrl;
    }


    @Override
    protected CardHeader initCardHeader() {
        CardHeader2 header = new CardHeader2(getContext(), R.layout.day_menu_inner_header);

        header.setTitle("TripAdvisor");
        header.setSubTitle(mTripData.getRanking());
        header.setDate(mTripData.getGrade());
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

        setOnClickListener(new OnCardClickListener() {
            @Override
            public void onClick(Card card, View view) {
                Actions.OpenWebPage(mContext, mTripUrl);
            }
        });
    }

    @Override
    public void init() {
        (new ParseURLTrip(this)).execute(new String[]{mTripUrl});//async

    }

    @Override
    protected List<ListObject> initChildren() {

        //Init the list
//        List<ListObject> mObjects = new ArrayList<ListObject>();
//
//        ListObject lalo = mCommentsToShow.get(1);
//
//        mObjects=(ListObject)mCommentsToShow;
        //Example onSwipe
        /*w2.setOnItemSwipeListener(new OnItemSwipeListener() {
            @Override
            public void onItemSwipe(ListObject object,boolean dismissRight) {
                Toast.makeText(getContext(), "Swipe on " + object.getObjectId(), Toast.LENGTH_SHORT).show();
            }
        });*/

        return mCommentsToShow;
    }

    @Override
    public View setupChildView(int childPosition, ListObject object, View convertView, ViewGroup parent) {

        try {

            TextView title = (TextView) convertView.findViewById(R.id.title);
            TextView comment = (TextView) convertView.findViewById(R.id.comment);
            TextView user = (TextView) convertView.findViewById(R.id.user1);
            TextView date = (TextView) convertView.findViewById(R.id.date);
            ImageView stars = (ImageView) convertView.findViewById(R.id.stars);

            CommentObject commentObject = (CommentObject) object;
            title.setText(commentObject.getTitle());
            comment.setText("\"" + commentObject.getComment() + "\"");
            user.setText(commentObject.getUser());
            date.setText(commentObject.getDate());
            stars.setImageDrawable(commentObject.getStars());


        } catch (Exception e) {
            myLog.add("errror item perfiles: " + e.getMessage());
        }

        return convertView;
    }

    @Override
    public int getChildLayoutId() {
        return R.layout.trip_inner_main;
    }

    @Override
    public void OnTaskCompleted(ArrayList elements) {
        try {
            myLog.add("ontaskcompleted:" + elements.size() + " comments");
            //Select three news with exact name
            mCommentsToShow = elements;
            super.init();

            listener.OnCardReady(this, R.layout.native_cardwithlist_layout2);
        } catch (Exception e) {
            listener.OnCardErrorLoadingData(e);
        }

//        mCardViewNews.setCard(this);
    }

    public void setListener(cardLoadedListener listener) {
        this.listener = listener;
    }
//    public void setView(CardViewNative cardViewNews) {
//        mCardViewNews = cardViewNews;
//    }

    private String KeepFirstWords(String s, int i) {
        String[] res = s.split(" ");
        StringBuilder sb = new StringBuilder(res[0]);

        for (int j = 1; j < i; j++) {
            myLog.add("j=" + j + " " + res[j]);
            sb.append(" " + res[j]);
        }

        return sb.toString();
    }

    class ParseURLTrip extends AsyncTask<String, Void, ArrayList<CommentObject>> {
        private OnTaskCompleted listener;

        ParseURLTrip(OnTaskCompleted listener) {
            this.listener = listener;
        }

        @Override
        protected ArrayList<CommentObject> doInBackground(String... strings) {
            ArrayList<CommentObject> comments = new ArrayList();
            try {
                Connection.Response response = Jsoup.connect(strings[0])
                        .ignoreContentType(true)
//                        .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
                                //   .userAgent("Mozilla/5.0 (Windows NT 6.3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.85 Safari/537.36")
                        .referrer("http://www.google.com")
                        .timeout(12000)
                        .followRedirects(true)
                        .execute();

                Document doc = response.parse();

                String ranking = doc.select("div.slim_ranking").first().text();
                myLog.add("ranonkig dull" + ranking);
                String grade = doc.select("img[property=ratingValue]").first().attr("content");

//                mTripData = new TripData(KeepFirstWords(ranking, 4), grade);
                mTripData = new TripData(ranking, grade);

                Elements commentsRaw = doc.select("div.reviewSelector");

                for (Element comment : commentsRaw) {
                    comments.add(ProcessHtmlComment(comment));
                }

            } catch (Throwable t) {
                t.printStackTrace();
            }
            return comments;
        }





        private CommentObject ProcessHtmlComment(Element element) {
            CommentObject comment = new CommentObject(mParentCard);


            try {
//                comment.title = element.select("span[class=taLnk]").text();//coge solo el primer
                comment.title = element.select("div.quote").text();
                comment.comment = element.select("div.entry").text();
                comment.user = element.select("div.username").text();

                String dateLong = element.select("span.ratingDate").text();
                comment.date = TrimFirstWords(dateLong, 2);

                String porportion = element.select("span[class=rate sprite-rating_s rating_s]").first().child(0).attr("alt");
                comment.starNumber = Integer.parseInt(porportion.substring(0, 1));


            } catch (Exception e) {
                myLog.add("errror capturanfo comments" + e.getMessage());
            }

            return comment;
        }


        @Override
        protected void onPostExecute(ArrayList<CommentObject> comments) {
            super.onPostExecute(comments);
//        respText.setText(s) TODO here populate the cards
//        this.inflateCard(news);
            listener.OnTaskCompleted(comments);
        }


    }

    // -------------------------------------------------------------
    // Object
    // -------------------------------------------------------------
    class CommentObject extends DefaultListObject {
        public String comment, user, date, title;
        //        public String url;
        public int starNumber;

        public CommentObject(Card parentCard) {
            super(parentCard);
            init();
        }

        private void init() {
            //OnClick Listener
            setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(LinearListView parent, View view, int position, ListObject object) {
                    Toast.makeText(getContext(), "Click on " + getObjectId(), Toast.LENGTH_SHORT).show();

                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getObjectId()));
                    mContext.startActivity(browserIntent);
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


        public String getTitle() {
            return title;
        }

        public String getComment() {
            return comment;
        }

        public String getUser() {
            return user;
        }

        public String getDate() {
            return date;
        }

        public Drawable getStars() {
            return getDrawableStars(starNumber);
        }

        private Drawable getDrawableStars(int starNumber) {
            int sol = 0;
            Drawable myIcon;

            switch (starNumber) {
                case 0:
                    sol = R.drawable.ic_star_0;
                    break;
                case 1:
                    sol = R.drawable.ic_star_1;
                    break;
                case 2:
                    sol = R.drawable.ic_star_2;
                    break;
                case 3:
                    sol = R.drawable.ic_star_3;
                    break;
                case 4:
                    sol = R.drawable.ic_star_4;
                    break;
                case 5:
                    sol = R.drawable.ic_star_5;
                    break;
            }

            myIcon = getContext().getResources().getDrawable(sol);

            return myIcon;
        }


    }
}



