package com.herenow.fase1;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.herenow.fase1.CardData.CompanyData;
import com.herenow.fase1.Cards.AirportCard;
import com.herenow.fase1.Cards.CompanyCard;
import com.herenow.fase1.Cards.NewsCard;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

import it.gmariotti.cardslib.library.view.CardViewNative;
import util.AppendLog;

//import com.herenow.fase1.Cards.MaterialLargeImageCard;

/**
 * A placeholder fragment containing a simple view.
 */
public class CardsActivityFragment extends Fragment {
    AirportCard airportCard;
//    CompanyCard card2;

    public CardsActivityFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initCards();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.demo_fragment_cardwithlist_card, container, false);
    }

    private void initCards() {

        try {
            //News card , esto lanza la busqueda en google y se abre el navegador
//            Intent searchIntent = new Intent(Intent.ACTION_WEB_SEARCH);
//            String keyword= "\"aplicaciones en Informática avanzada\"";
//            searchIntent.putExtra(SearchManager.QUERY, keyword);
////            searchIntent.putExtra(SearchManager. keyword);
//            startActivity(searchIntent);

//            String siteUrl = "https://www.google.es/webhp?ie=UTF-8#q=%22aplicaciones+en+informatica+avanzada%22&tbm=nws";
            String siteUrl = "https://www.google.es/search?q=aviones&tbm=nws ";
            (new ParseURL()).execute(new String[]{siteUrl});
//////


            //Company Card

            CompanyData companyData = new CompanyData("Aplicaciones en Informática Avanzada", R.drawable.im_aia_fondo_claro, R.drawable.im_aia_logo);
            companyData.setEmployeesNumber(55);
            companyData.setLemma("Algoritmos para un mundo mejor");
            companyData.setDirector("Regina Llopis");
            companyData.setFoundationYear("1990");
            companyData.setDescription("El objetivo principal de Grupo AIA es producir un beneficio económico real y cuantificable para nuestros clientes a través de la actividad innovadora usando la tecnología punta como una propuesta de negocio de alto valor.");
            companyData.setFounders(new String[]{"Regina Llopis", "Toni Trias", "Xavier Fustero"});
            companyData.setTypeOfBusiness("Software");
            companyData.setEmail("secretaria@aia.es");
            companyData.setPhone("+34935044900");
            companyData.setWebsite("www.aia.es");

            CompanyCard companyCardtest = CompanyCard.with(getActivity())
                    .setData(companyData)
                    .build();

            CardViewNative cardViewCompany = (CardViewNative) getActivity().findViewById(R.id.card_view_company);
            cardViewCompany.setCard(companyCardtest);

            // News Card
            NewsCard newsCard = new NewsCard(getActivity(), companyData.getName());
            newsCard.init();
            CardViewNative cardViewNews = (CardViewNative) getActivity().findViewById(R.id.card_view_news);
//cardViewNews.setCard(cardViewNews);

            // Airport card
            airportCard = new AirportCard(getActivity());
            airportCard.init();

            CardViewNative cardViewAirport = (CardViewNative) getActivity().findViewById(R.id.card_view_airport);
            cardViewAirport.setCard(airportCard);

        } catch (Exception e) {
            AppendLog.appendLog("---error init cards: " + e);
        }

    }

    @Override
    public void onPause() {

        try {
            super.onPause();
            if (airportCard != null)
                airportCard.unregisterDataSetObserver();//TODO, VER CoMO VA ESTE ROLLO DEL REGISTRO DE OBSERVER
        } catch (Exception e) {
            AppendLog.appendLog("--error on Pause cards" + e.getMessage());
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
            noticia.isExact = oneNew.select("div.st").html().contains("<em>");

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