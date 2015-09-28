package com.herenow.fase1.Cards;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.herenow.fase1.CardData.Noticia;
import com.herenow.fase1.R;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.prototypes.CardWithList;
import it.gmariotti.cardslib.library.prototypes.LinearListView;
import it.gmariotti.cardslib.library.view.CardViewNative;
import util.OnTaskCompleted;
import util.myLog;

import static com.herenow.fase1.Cards.NewsCard.ProcessHtmlNews;

/**
 * Created by Milenko on 12/08/2015.
 */
public class NewsCard extends CardWithList implements OnTaskCompleted {

    private String mCompanyName;
    //    private ArrayList<Noticia> mNews;
    private ArrayList<Noticia> mNewsToShow;
    private String siteUrl;
    private CardViewNative mCardViewNews;
    private int maxNews;

    public NewsCard(Context context, String companyName) {
        super(context);
        mCompanyName = companyName;
    }

    public static Noticia ProcessHtmlNews(Element oneNew, HashMap<String, Bitmap> tableImages) {
        Noticia noticia = null;
        String imageId;
        try {
            noticia = new Noticia();
            noticia.title = oneNew.select("a[class=l _HId]").text();
            noticia.link = oneNew.select("a[class=l _HId]").attr("href");
            noticia.content = oneNew.select("div.st").text();
            noticia.isExact = oneNew.select("div.st").html().contains("<em>");
            noticia.date = oneNew.select("div.slp").first().child(2).text();
            noticia.source = oneNew.select("div.slp").first().child(0).text();
            //Image
            String imgPath = oneNew.select("img[class=th _lub]").attr("src");
            if (imgPath.startsWith("http")) {
                noticia.imageUrl = imgPath;
            } else if (imgPath.startsWith("data")) {
                imageId = oneNew.select("img[class=th _lub]").attr("id");
                noticia.image = tableImages.get(imageId);
            }
            return noticia;
        } catch (Exception e) {
            myLog.add("fallo en crear una noticia" + e.getMessage());
            return null;
        }
    }

    @Override
    protected CardHeader initCardHeader() {

        //Add Header
        CardHeader header = new CardHeader(getContext(), R.layout.carddemo_googlenowweather_inner_header);
        //Todo change the news header style
        header.setTitle("News"); //todo should use R.string.
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

        //perform the search

        try {

            siteUrl = "https://www.google.es/search?q=%22" + URLEncoder.encode(mCompanyName, "UTF-8") + "%22&tbm=nws ";
            myLog.add("The query for news is " + siteUrl);

            (new ParseURL(this)).execute(new String[]{siteUrl});//async
        } catch (UnsupportedEncodingException e) {
            myLog.add("Encoding error : " + e.getMessage());
        }
//        super.init();//Sync
    }

    @Override
    protected List<ListObject> initChildren() {

//        mNewsToShow = GetNewsSync();
        //Init the list
        List<ListObject> mObjects = new ArrayList<ListObject>();
        //Todo Handle if there is no or less than three new
        try {

            for (Noticia noti : mNewsToShow) {
                NewsObject newsObject = new NewsObject(this);
                newsObject.setData(noti);
                newsObject.setObjectId(newsObject.link);
                mObjects.add(newsObject);
            }

        } catch (Exception e) {
            myLog.add("--eerron in news caard: " + e.getMessage());
        }


        //Example onSwipe
        /*w2.setOnItemSwipeListener(new OnItemSwipeListener() {
            @Override
            public void onItemSwipe(ListObject object,boolean dismissRight) {
                Toast.makeText(getContext(), "Swipe on " + object.getObjectId(), Toast.LENGTH_SHORT).show();
            }
        });*/

        return mObjects;
    }

    private ArrayList<Noticia> GetNewsSync() {
        ArrayList noticias = new ArrayList();
        //Todo tomar sólo las literales
        try {
            myLog.add("Connecting to [" + siteUrl + "]");

            Document doc = Jsoup.connect(siteUrl).get();
            myLog.add("Connected to [" + siteUrl + "]");

            ///mhp
            Elements news = doc.select("li.g");
            myLog.add(" We ve got news: " + news.size());

            for (Element oneNew : news) {
//                noticias.add(ProcessHtmlNews(oneNew, tableImages));
            }
        } catch (IOException e) {
            myLog.add("--errorn en getnews symc:" + e.getMessage());
        }

        return noticias;
    }

    @Override
    public View setupChildView(int childPosition, ListObject object, View convertView, ViewGroup parent) {

        try {
            //Setup the ui elements inside the item

            TextView title = (TextView) convertView.findViewById(R.id.news_title);
            TextView content = (TextView) convertView.findViewById(R.id.news_content);

            TextView source = (TextView) convertView.findViewById(R.id.news_source);
            TextView date = (TextView) convertView.findViewById(R.id.news_date);

            ImageView ivImage = (ImageView) convertView.findViewById(R.id.news_image);

            //Retrieve the values from the object
            NewsObject newsObject = (NewsObject) object;
            title.setText(newsObject.title);
            content.setText(newsObject.content);
            source.setText(newsObject.source);
            date.setText(newsObject.date);

            if (newsObject.imageUrl != null) {
                Picasso.with(mContext).load(newsObject.imageUrl)//Todo si es que tiene url
                        .error(R.drawable.abc_ic_ab_back_mtrl_am_alpha)
                        .placeholder(R.mipmap.ic_launcher)
                        .into(ivImage);
            } else if (newsObject.image != null) {
                ivImage.setImageBitmap(newsObject.image);
            }
        } catch (Exception e) {
            myLog.add("errror item news : " + e.getMessage());
        }

        return convertView;
    }

    @Override
    public int getChildLayoutId() {
        return R.layout.news_inner_main;
    }

    @Override
    public void OnTaskCompleted(ArrayList news) {
        myLog.add("ontaskcompleted:" + news.size() + " neews");

        //Select maxNews news with exact name
        maxNews = 4;
        mNewsToShow = new ArrayList<>();
        for (Object ob : news) {
            Noticia not = (Noticia) ob;
            if (not.isExact) {
                mNewsToShow.add(not);
                if (mNewsToShow.size() == maxNews) break;
            }
        }
        myLog.add("Noticias exactas:"+mNewsToShow.size());

        //Complete with the other news
        for (Object ob : news) {
            if (mNewsToShow.size() == maxNews) break;
            Noticia not = (Noticia) ob;
            if (!not.isExact) {
                mNewsToShow.add(not);
            }
        }


        myLog.add("ontaskcompleted: tenemos :" + mNewsToShow.size() + " noticias para mostrar");

        super.init();


        mCardViewNews.setCard(this);
    }

    public void setView(CardViewNative cardViewNews) {
        mCardViewNews = cardViewNews;
    }

    public void updateNews(ArrayList<String> news) {


    }

    // -------------------------------------------------------------
    // News Object
    // -------------------------------------------------------------

    public class NewsObject extends DefaultListObject {

        public String title, source, content, imageUrl, link;
        public String date;
        private Bitmap image;
//        public boolean isExact;
//        public String city;
        //        public int weatherIcon;
//        public int temperature;
//        public String temperatureUnit = "°C";
//        public String time;


        public NewsObject(Card parentCard) {
            super(parentCard);
            init();
        }

        public void setData(Noticia noti) {
            title = noti.title;
            source = noti.source;
            content = noti.content;
            imageUrl = noti.imageUrl;
            link = noti.link;
            date = noti.date;
            image = noti.image;
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

    }


}

class ParseURL extends AsyncTask<String, Void, ArrayList<Noticia>> {
    private OnTaskCompleted listener;

    ParseURL(OnTaskCompleted listener) {
        myLog.add("asignando listener en parseURL");
        this.listener = listener;
    }

    @Override
    protected ArrayList<Noticia> doInBackground(String... strings) {
        ArrayList noticias = new ArrayList();
        try {

            myLog.add("Connecting to [" + strings[0] + "]");

            Document doc = Jsoup.connect(strings[0]).get();
            myLog.add("Connected to [" + strings[0] + "]");
//            String code = doc.select("script").get(9).html();//todo assure that is 9

//            String mydata = "some string with 'the data i want' inside";

            HashMap<String, Bitmap> tableImages = ObtainCodedImages(doc.select("script").get(9).html());
            myLog.add("news:la tablde de imagenes: " + tableImages.size());
            Element table = doc.select("ol[id=rso]").first();
            myLog.add("news: numero de elemnts: " + table.children().size());
            for (Element oneNew : table.children()) {
                Noticia noticia = ProcessHtmlNews(oneNew, tableImages);
                if (noticia != null) noticias.add(noticia);
            }


        } catch (Exception e) {
            myLog.addError(this.getClass(), e);
        }
        return noticias;
    }

    private HashMap<String, Bitmap> ObtainCodedImages(String script) {
        HashMap<String, Bitmap> res = new HashMap<>();
        try {
            Bitmap buffData = null;
            Pattern pattern = Pattern.compile("'(.*?)'");
            Matcher matcher = pattern.matcher(script);
            int i = 0;

            while (matcher.find()) {
                i++;
                //            System.out.println(matcher.group(1));
                String str = matcher.group(1);
                if (i % 2 == 0) {//Impares son datos, pares son los nombres. Primero datos
                    res.put(str, buffData);
                } else {
                    int commaIndex = str.indexOf(",");
                    String coded = str.substring(commaIndex + 1);//todo quitarle hasta la coma
                    byte[] decodedString = Base64.decode(coded, Base64.DEFAULT);
                    buffData = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                }
            }
        } catch (Exception e) {
            myLog.add("eerro " + e.getMessage());
        }
        return res;
    }


    @Override
    protected void onPostExecute(ArrayList<Noticia> news) {
        super.onPostExecute(news);
//        respText.setText(s) TODO here populate the cards
//        this.inflateCard(news);
        listener.OnTaskCompleted(news);
    }


}
