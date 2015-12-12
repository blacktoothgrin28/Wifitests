package com.herenow.fase1.Cards;

import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.herenow.fase1.R;
import com.herenow.fase1.Wifi.WifiAsker;
import com.herenow.fase1.Wifi.preguntaWifi;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.prototypes.LinearListView;
import util.GPSCoordinates;
import util.myLog;

/**
 * Created by Milenko on 03/12/2015.
 */
public class BusStopCard extends FetchingCard implements preguntaWifi {
    private WifiManager wifi;

    public BusStopCard(Context context, String url, String title, @LayoutRes int innerLayout) {
        super(context, url, title, innerLayout);
    }

    @Override
    protected ArrayList<DefaultListObject> ExtraccionObjetos(String url) {
        ArrayList<DefaultListObject> paradasUpdated = new ArrayList();
        try {
            Connection.Response response = Jsoup.connect(url)
                    .ignoreContentType(true)
                    .referrer("http://www.google.com")
                    .timeout(5000)
                    .followRedirects(true)
                    .execute();

            ArrayList<BusStop> paradas = ProcessJson(response.body());

            //update
            for (BusStop parada : paradas) {
                parada.update();
                paradasUpdated.add(parada);
            }
            myLog.add("todas las paradas updated");
        } catch (IOException e) {
            myLog.add("error en gettin data from url: " + e.getLocalizedMessage());
        } catch (JSONException e) {
            myLog.add("error2 en gettin data from url: " + e.getLocalizedMessage());
        } catch (Exception e) {
            myLog.add("error generico en extract object: " + e.getLocalizedMessage());
        }
        return paradasUpdated;
    }

    private ArrayList<BusStop> ProcessJson(String s) throws JSONException {
        String[] partes = s.split("\\}\\,\\{|\\[\\{|\\}\\]");
        ArrayList<BusStop> arr = new ArrayList();

        for (String parte : partes) {
            if (parte.length() > 3) {
                BusStop busStop = new BusStop(this);
                busStop.setData(new JSONObject("{" + parte + "}"));
                busStop.setObjectId(Integer.toString(busStop.id));
                arr.add(busStop);
                myLog.add(busStop.toString());
                if (arr.size() == 5) break;
            }
        }

        return arr;
    }

    @Override
    public View setupChildView(int childPosition, ListObject object, View convertView, ViewGroup parent) {

        try {
            TextView address = (TextView) convertView.findViewById(R.id.address);
            TextView id = (TextView) convertView.findViewById(R.id.identification);
            TextView distance = (TextView) convertView.findViewById(R.id.distance);
            TextView times = (TextView) convertView.findViewById(R.id.times);

            ImageView ivImage = (ImageView) convertView.findViewById(R.id.street_image);

            //Retrieve the values from the object
            BusStop busStop = (BusStop) object;
            address.setText(busStop.address);
            id.setText("id:" + Integer.toString(busStop.id));
            distance.setText(Double.toString(busStop.distance) + "km");
//            times.setText(busStop.TimesSummary() + "\n" + busStop.TimesSummarySorted());
            times.setText(busStop.TimesSummarySorted());

            String url = busStop.getImageUrl();

            if (url != null && !url.equals("")) {
                Picasso.with(mContext).load(url)
                        .error(R.drawable.abc_ic_ab_back_mtrl_am_alpha)
                        .placeholder(R.mipmap.ic_launcher)
                        .into(ivImage);
            }

        } catch (Exception e) {
            myLog.add("errror item: " + e.getMessage());
        }

        return convertView;
    }


    @Override
    public void OnReceiveWifis(List<ScanResult> sr) {
        myLog.add("se han detecetado las wifis");
    }

    @Override
    public void noWifiDetected() {
        myLog.add("no wifi detected");
    }

    class BusStop extends DefaultListObject {
        public String address, reference;
        int id;
        double distance;
        GPSCoordinates gps;
        private ArrayList<LineTime> lineTimes;

        @Override
        public String toString() {
            return "BusStop{" +
                    "address='" + address + '\'' +
                    ", reference='" + reference + '\'' +
                    ", id=" + id +
                    ", distance=" + distance +
                    ", gps=" + gps +
                    ", lineTimes=" + lineTimes +
                    '}';
        }

        public BusStop(Card parentCard) {
            super(parentCard);
            init();
        }

//        public String TimesSummary() {
//            StringBuilder sb = new StringBuilder();
//            String s2 = "No info ";
//            try {
//                for (LineTime lineTime : lineTimes) {
//                    sb.append(lineTime.summary() + "\n");
//                }
//                String s = sb.toString();
//                s2 = s.substring(0, s.length() - 1);
//            } catch (Exception e) {
//                myLog.add("no se pudo tener una timeline");
//            }
//            return s2;
//        }

        /**
         *  reformat the times as
         *  L1: 3 min, 4 min, 50 min.
         *  L9: 6min, 72 min.
         * @return
         */
        public String TimesSummarySorted() {
            String substring = "No info";
            try {
                HashMap<String, ArrayList<LineTime>> tableLines = new HashMap<>();
                ArrayList arr;
                //a
                for (LineTime lineTime : lineTimes) {
                    String lc = lineTime.lineCode;
                    if (tableLines.containsKey(lc)) {
                        arr = tableLines.get(lc);
                        arr.add(lineTime);
                        tableLines.put(lc, arr);
                    } else {
                        arr = new ArrayList();
                        arr.add(lineTime);
                        tableLines.put(lc, arr);
                    }
                }

                StringBuilder sb = new StringBuilder();
                for (String name : tableLines.keySet()) {
                    sb.append(summaryLineTimes(name, tableLines.get(name)) + "\n");
                }
                String s = sb.toString();
                substring = s.substring(0, s.length() - 2);
            } catch (Exception e) {
                myLog.add("error mostrando resumen ordenado de timeline");
            }
            return substring;
        }

        private String summaryLineTimes(String name, ArrayList<LineTime> lineTimes) {
            StringBuilder sb = new StringBuilder(name + ": ");
            for (LineTime lineTime : lineTimes) {
                sb.append(lineTime.roundedTime + ", ");
            }
            String s = sb.toString();
            return (s.substring(0, s.length() - 2) + ".");
        }

        private void init() {
            //OnClick Listener
            setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(LinearListView parent, View view, int position, ListObject object) {
                    Toast.makeText(getContext(), "Clicked on " + getObjectId(), Toast.LENGTH_SHORT).show();

                    new WifiAsker(mContext, new preguntaWifi() {
                        @Override
                        public void OnReceiveWifis(List<ScanResult> sr) {
                            Toast.makeText(mContext, "recibidos " + sr.size() + "wifis", Toast.LENGTH_SHORT).show();
                            myLog.add("recibidos los wifis forzados");
                            uploadWifis(getObjectId(),sr);
                        }

                        @Override
                        public void noWifiDetected() {
                            myLog.add("error recibiendo los sopotsde manera forzasa");
                        }
                    });

//                    SaveWifis(getObjectId());
//                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getObjectId()));
//                    mContext.startActivity(browserIntent);
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

        private void uploadWifis(String busStopId, List<ScanResult> sr) {
            
        }

        public void setData(JSONObject json) {
            try {
                gps = new GPSCoordinates(json.getDouble("CoordX"), json.getDouble("CoordY"));
                id = json.getInt("Id");
                address = json.getString("Denomination");
                distance = json.getDouble("dist");
                reference = json.getString("reference");
            } catch (JSONException e) {
                myLog.add("problema con json");
            }

        }

        public String getImageUrl() {
            String s = "https://maps.googleapis.com/maps/api/streetview?size=640x350&location=" +
                    gps.getLatitude() + "," + gps.getLongitude() +
                    "&heading=151.78&pitch=-0.76&key=AIzaSyD_H8UMnok_oZIW19KLCCmGJoaKrWbUXi8";
            return s;
        }

        public void update() throws JSONException {
            Connection.Response response = null;
            String q2 = "http://www.santqbus.santcugat.cat/consultatr.php?idparada=" + id + "&idliniasae=-1&codlinea=-1";

            try {
                response = Jsoup.connect(q2)
                        .ignoreContentType(true)
                        .referrer("http://www.google.com")
                        .timeout(5000)
                        .followRedirects(true)
                        .execute();
            } catch (IOException e) {
                e.printStackTrace();
            }

            String s = response.body();
            String[] partes = s.split("\\}\\,\\{|\\[\\{|\\}\\]");

            lineTimes = new ArrayList();

            for (String parte : partes) {
                if (parte.length() > 3) {
                    LineTime lineTime = new LineTime(new JSONObject("{" + parte + "}"));
                    lineTimes.add(lineTime);
                    myLog.add(lineTime.toString());
                }
            }
            myLog.add("updated todas las lineas de la parada:" + this.id);
        }
    }

    class LineTime {
        int arrivalTime;
        String lineCode;
        String roundedTime;
        int stopCode;

        public LineTime(JSONObject json) throws JSONException {
            arrivalTime = json.getInt("arrivalTime");
            lineCode = json.getString("lineCode");
            roundedTime = json.getString("roundedArrivalTime");
            stopCode = json.getInt("stopCode");
        }

        @Override
        public String toString() {
            return "LineTime{" +
                    "arrivalTime=" + arrivalTime +
                    ", lineCode='" + lineCode + '\'' +
                    ", roundedTime='" + roundedTime + '\'' +
                    ", stopCode=" + stopCode +
                    '}';
        }

        public String summary() {
            return lineCode + ": " + roundedTime;
        }
    }
}
