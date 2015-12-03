package com.herenow.fase1.Cards;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.herenow.fase1.Activities.cardLoadedListener;
import com.herenow.fase1.CardData.ProductItem;
import com.herenow.fase1.CardData.ProductsData;
import com.herenow.fase1.Cards.Components.CardHeader2;
import com.herenow.fase1.Parada;
import com.herenow.fase1.R;
import com.herenow.fase1.actions.Actions;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.prototypes.CardWithList;
import it.gmariotti.cardslib.library.prototypes.LinearListView;
import util.GPSCoordinates;
import util.OnTaskCompleted;
import util.myLog;

/**
 * Created by Milenko on 19/09/2015.
 */


public class ParadaCard extends CardWithList implements OnTaskCompleted {

    private GPSCoordinates mGps;
    private cardLoadedListener listener;

    public ParadaCard(Context context, GPSCoordinates gps) {
        super(context);
        mGps = gps;
    }


    @Override
    protected CardHeader initCardHeader() {
        CardHeader header = new CardHeader(getContext(), R.layout.listcard_inner_header);
        header.setTitle("Paradas cercanas");
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
        (new readParada()).execute(new GPSCoordinates[]{mGps});//async

    }
    @Override
    protected List<ListObject> initChildren() {
        return mParadasToShow;
    }
    //////////////
    public ParadaCard(Context context, int innerLayout) {
        super(context, innerLayout);
    }

    public void setData(ProductsData productsData) {
        mProductData = productsData;
    }





    private List<ListObject> prepareChildren(ArrayList<ProductItem> products) {

        List<ListObject> mObjects = new ArrayList<>();

        //Add an object to the list
        for (ProductItem item : products) {
            final ProductObject so = new ProductObject(mParentCard, item);

            //Example onSwipe
            so.setOnItemSwipeListener(new OnItemSwipeListener() {
                @Override
                public void onItemSwipe(ListObject object, boolean dismissRight) {
//                    Toast.makeText(getContext(), "Downloading presentation\n\"" + so.title + "\" by "
//                            + so.speaker, Toast.LENGTH_SHORT).show();

                }
            });
            mObjects.add(so);
        }
        return mObjects;
    }


    @Override
    public View setupChildView(int childPosition, ListObject object, View convertView, ViewGroup parent) {

        //Setup the ui elements inside the item
        TextView name = (TextView) convertView.findViewById(R.id.name);
        TextView description = (TextView) convertView.findViewById(R.id.description);
        ImageView image = (ImageView) convertView.findViewById(R.id.product_image);
        TextView keyWords = (TextView) convertView.findViewById(R.id.key_words);

        //Retrieve the values from the object
        ProductItem pi = ((ProductObject) object).getProductItem();
        name.setText(pi.getName());
        description.setText(pi.getDescription());
        keyWords.setText(pi.getKeyWords());

        if (image != null) {
            Picasso.with(mContext).load(pi.getImageUrl())
                    .error(R.drawable.abc_ic_ab_back_mtrl_am_alpha)
                    .placeholder(R.mipmap.ic_launcher).fit().centerCrop()
                    .into(image);
        }

        //Todo put icons an filter by type of food (vegan, gluten, etc)

        return convertView;
    }

    @Override
    public int getChildLayoutId() {
        return R.layout.products_card_inner_main;
    }

    @Override
    public void OnTaskCompleted(ArrayList elements) {
        try {
            myLog.add("ontaskcompleted:" + elements.size() + " paradas");
            //Select three news with exact name
            mParadasToShow = elements;
            super.init();

            listener.OnCardReady(this, R.layout.native_cardwithlist_layout2);
        } catch (Exception e) {
            listener.OnCardErrorLoadingData(e);
        }

    }

    class readParada extends AsyncTask<GPSCoordinates, Void, ArrayList<Parada>> {

        @Override
        protected ArrayList<Parada> doInBackground(GPSCoordinates... gpsS) {
            ArrayList<Parada> paradasUpdated = null;
            GPSCoordinates gps = gpsS[0];

            String q = "http://www.santqbus.santcugat.cat/consultasae.php?x=" +
                    gps.getLatitude() + "&y=" + gps.getLongitude();
            try {
                Connection.Response response = Jsoup.connect(q)
                        .ignoreContentType(true)
                        .referrer("http://www.google.com")
                        .timeout(5000)
                        .followRedirects(true)
                        .execute();

                ArrayList<Parada> ParadasCercanas = GetParadas(response.body());

                //Pedimos info de esta parada
//                InfoParada(ParadasCercanas.get(0));
//                InfoParada(ParadasCercanas.get(1));
//                InfoParada(ParadasCercanas.get(2));

                paradasUpdated = UpdateParadas(ParadasCercanas);

            } catch (IOException e) {
                myLog.add("error en gettin data from url: " + e.getLocalizedMessage());
            } catch (JSONException e) {
                myLog.add("error2 en gettin data from url: " + e.getLocalizedMessage());
            }

            return paradasUpdated;
        }

        @Override
        protected void onPostExecute(ArrayList<Parada> paradas) {
            super.onPostExecute(paradas);
            //TODO abrir la ventana con las paradas
        }

        private ArrayList<Parada> UpdateParadas(ArrayList<Parada> paradas) throws IOException, JSONException {
            ArrayList<Parada> arr = new ArrayList<>();

            for (Parada parada : paradas) {
                arr.add(UpdateParada(parada));
            }
            return arr;
        }

        private Parada UpdateParada(Parada parada) throws IOException, JSONException {
            Connection.Response response;
            int id = parada.getId();
            String q2 = "http://www.santqbus.santcugat.cat/consultatr.php?idparada=" + id + "&idliniasae=-1&codlinea=-1";

            response = Jsoup.connect(q2)
                    .ignoreContentType(true)
                    .referrer("http://www.google.com")
                    .timeout(5000)
                    .followRedirects(true)
                    .execute();

            return parada.updateTimes(response.body());
        }


        private ArrayList<Parada> GetParadas(String s) throws JSONException {
            String[] partes = s.split("\\}\\,\\{|\\[\\{|\\}\\]");
            ArrayList<Parada> arr = new ArrayList();

            for (String parte : partes) {
                if (parte.length() > 3) {
                    Parada parada = new Parada(new JSONObject("{" + parte + "}"));
                    arr.add(parada);
                    myLog.add(parada.toString());
                    if (arr.size() == 3) break;
                }
            }

            return arr;
        }
    }
    class ProductObject extends DefaultListObject {


        public ProductItem getProductItem() {
            return productItem;
        }

        private ProductItem productItem;

        public ProductObject(Card parentCard) {
            super(parentCard);
            init();
        }

        public ProductObject(Card parentcard, ProductItem productItem) {
            super(parentcard);
            this.productItem = productItem;

            init();
        }

        private void init() {
            //OnClick Listener
            setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(LinearListView parent, View view, int position, ListObject object) {
//                    Toast.makeText(getContext(), "Click on " + getObjectId(), Toast.LENGTH_SHORT).show();
                    Actions.OpenWebPage(mContext, ((ProductObject) object).getImageUrl());

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


        public String getImageUrl() {
            return productItem.getImageUrl();
        }
    }
}