package com.herenow.fase1.Cards;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.herenow.fase1.Noticia;
import com.herenow.fase1.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.prototypes.CardWithList;
import it.gmariotti.cardslib.library.prototypes.LinearListView;
import util.AppendLog;

/**
 * Created by Milenko on 12/08/2015.
 */
public class NewsCard extends CardWithList {

    private ArrayList<Noticia> mNews;

    public NewsCard(Context context, String companyName) {
        super(context);
    }

    public NewsCard(Context context) {
        super(context);
    }

    public NewsCard(Context context, int innerLayout) {
        super(context, innerLayout);
    }

    public void setData(HashMap flightsData) {//TODO

    }


    @Override
    protected CardHeader initCardHeader() {

        //Add Header
        CardHeader header = new CardHeader(getContext(), R.layout.carddemo_googlenowweather_inner_header);

        header.setTitle("News"); //should use R.string.
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

    }

    @Override
    public void init() {

        //perform the search
        String siteUrl = "https://www.google.es/search?q=aviones&tbm=nws ";
        (new ParseURL()).execute(new String[]{siteUrl});

        mNews = null;//todo Rellenar

        super.init();
    }

    @Override
    protected List<ListObject> initChildren() {

        //Init the list
        List<ListObject> mObjects = new ArrayList<ListObject>();

        //Add an object to the list
        NewsObject w1 = new NewsObject(this);
        w1.city = "London";
        w1.time = "11:30";
//        w1.temperature = 16;
//        w1.weatherIcon = R.drawable.ic_action_cloud;
        w1.setObjectId(w1.city); //It can be important to set ad id
        mObjects.add(w1);

        NewsObject w2 = new NewsObject(this);
        w2.city = "Rome";
        w2.time = "11:33";
//        w2.temperature = 25;
//        w2.weatherIcon = R.drawable.ic_action_sun;
        w2.setObjectId(w2.city);
        w2.setSwipeable(true);

        //Example onSwipe
        /*w2.setOnItemSwipeListener(new OnItemSwipeListener() {
            @Override
            public void onItemSwipe(ListObject object,boolean dismissRight) {
                Toast.makeText(getContext(), "Swipe on " + object.getObjectId(), Toast.LENGTH_SHORT).show();
            }
        });*/
        mObjects.add(w2);

        NewsObject w3 = new NewsObject(this);
        w3.city = "Paris";
        w1.time = "11:39";
//        w3.temperature = 19;
//        w3.weatherIcon = R.drawable.ic_action_cloudy;
        w3.setObjectId(w3.city);
        mObjects.add(w3);

        return mObjects;
    }

    @Override
    public View setupChildView(int childPosition, ListObject object, View convertView, ViewGroup parent) {

        //Setup the ui elements inside the item
        TextView city = (TextView) convertView.findViewById(R.id.carddemo_weather_city);
//        ImageView icon = (ImageView) convertView.findViewById(R.id.carddemo_weather_icon);
        TextView time = (TextView) convertView.findViewById(R.id.carddemo_weather_temperature);

        //Retrieve the values from the object
        NewsObject newsObject = (NewsObject) object;
//        icon.setImageResource(flightObject.weatherIcon);
        city.setText(newsObject.city);
        time.setText(newsObject.time);

        return convertView;
    }

    @Override
    public int getChildLayoutId() {
        return R.layout.carddemo_googlenowweather_inner_main;
    }


    // -------------------------------------------------------------
    // Weather Object
    // -------------------------------------------------------------

    public class NewsObject extends DefaultListObject {

        public String title, source, content, image, link;
        public String date;
        public boolean isExact;

        public String city;
        //        public int weatherIcon;
//        public int temperature;
        public String temperatureUnit = "°C";
        public String time;

        public NewsObject(Card parentCard) {
            super(parentCard);
            init();
        }

        private void init() {
            //OnClick Listener
            setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(LinearListView parent, View view, int position, ListObject object) {
                    Toast.makeText(getContext(), "Click on " + getObjectId(), Toast.LENGTH_SHORT).show();
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

class ParseURL extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... strings) {
        StringBuffer buffer = new StringBuffer();
        try {
//            HttpClient client = new DefaultHttpClient();
//            HttpGet request = new HttpGet(strings[0]);
//            HttpResponse response = client.execute(request);
//
//            String html = "";
//            InputStream in = response.getEntity().getContent();
//            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
//            StringBuilder str = new StringBuilder();
//            String line = null;
//            while((line = reader.readLine()) != null)
//            {
//                str.append(line);
//            }
//            in.close();
//            html = str.toString();
//

//            Document doc = Jsoup.parse(html);

            Log.d("JSwa", "Connecting to [" + strings[0] + "]");
//            String url2 = "http://www.google.es/search?ie=UTF-8#q=aviones&tbm=nws";

            Document doc = Jsoup.connect(strings[0]).get(); //https y nees
//            Document doc2 = Jsoup.connect(url2).get(); //codification
//            Document doc3 = Jsoup.connect(strings[0]).get(); //
//            Document doc4 = Jsoup.connect(strings[0]).get(); //
            Log.d("JSwa", "Connected to [" + strings[0] + "]");
            // Get document (HTML page) title
            String title = doc.title();
            Log.d("JSwA", "Title [" + title + "]");
            buffer.append("Title: " + title + "\r\n");

            ///mhp
            Elements news = doc.select("li.g");
            ArrayList noticias = new ArrayList();
            for (Element oneNew : news) {
                noticias.add(ProcessHtmlNews(oneNew));
            }


            // Get meta info
            Elements metaElems = doc.select("meta");
            buffer.append("META DATA\r\n");
            for (Element metaElem : metaElems) {
                String name = metaElem.attr("name");
                String content = metaElem.attr("content");
                buffer.append("name [" + name + "] - content [" + content + "] \r\n");
            }

            Elements topicList = doc.select("h2.topic");
            buffer.append("Topic list\r\n");
            for (Element topic : topicList) {
                String data = topic.text();

                buffer.append("Data [" + data + "] \r\n");
            }

        } catch (Throwable t) {
            t.printStackTrace();
        }

        return buffer.toString();
    }

    private Noticia ProcessHtmlNews(Element oneNew) {
        Noticia noticia = null;
        try {
            noticia = new Noticia();
            noticia.title = oneNew.select("a[class=l _HId]").text();
            noticia.link = oneNew.select("a[class=l _HId]").attr("href");
            noticia.content = oneNew.select("div.st").text();
            noticia.isExact = oneNew.select("div.st").contains("<b>");

            noticia.image = oneNew.select("img[class=th _lub]").attr("src");
            noticia.source = oneNew.select("div.slp").first().child(0).text();
            noticia.date = oneNew.select("div.slp").first().child(2).text();
        } catch (Exception e) {
            AppendLog.appendLog("fallo en crear una noticia" + e.getMessage());
        }
        return noticia;
    }


    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
//        respText.setText(s);
    }
}
