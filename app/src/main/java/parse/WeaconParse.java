package parse;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.google.android.gms.maps.model.LatLng;
import com.herenow.fase1.LineTime;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import util.RoundImage;
import util.formatter;
import util.myLog;
import util.parameters;

/**
 * Created by Milenko on 30/07/2015.
 */
@ParseClassName("Weacon")
public class WeaconParse extends ParseObject {

    private String[] cards;
    private ArrayList fetchedElements;
    private String fetchingUrl;

    public WeaconParse() {
    }

    public WeaconParse(String name, String mainUrl, String phone, String url2, String url3, String description,
                       String type, double lat, double lon, int rating, boolean automatic, ParseUser user, Bitmap logo) {
        setName(name);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        logo.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        ParseFile fileLogo = new ParseFile(this.getName().replace(" ", "_") + ".png", byteArray);
        setLogo(fileLogo);

        setMainUrl(mainUrl);
        setPhone(phone);
        setUrl2(url2);
        setUrl3(url3);
        setDescription(description);
        setType(type);
        setGPS(new ParseGeoPoint(lat, lon));
        setRating(rating);
        setAutomatic(automatic);
        setOwner(user);
    }

    private static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    @Override
    public int hashCode() {
        return getObjectId().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (!(o instanceof WeaconParse)) return false;

        WeaconParse other = (WeaconParse) o;
        return this.getObjectId() == other.getObjectId();
    }


    //GETTERS

    public String getName() {
        String name = getString("Name");
        return name;
    }

    //SETTERS
    public void setName(String name) {
        put("Name", name);
    }

    public String getCompanyDataObjectId() {

        ParseObject po = getParseObject("CardCompany");
        if (po == null) {
            return null;
        } else {
            return po.getObjectId();
        }
    }

    public void setCompanyDataObjectId(String value) {
        put("CardCompany", value);
    }

    public String getUrl() {
        return getString("MainUrl");
    }

    /**
     * If true, it shows a separated notification if has been fetched
     *
     * @return
     */
    public boolean getOwnNotif() {
        return (getBoolean("OwnNotif") && fetchedElements.size() > 0);
    }

    public String[] getCards() {

        try {
            List<Object> al = getList("cards");
            cards = new String[al.size()];
            al.toArray(cards);
        } catch (Exception e) {
            myLog.add("--error: ther is no definition of cards in parse: " + e.getLocalizedMessage());
        }
        return cards;
    }

    public Bitmap getLogoRounded() {
        Bitmap bm = getLogo();
        Bitmap logoRounded = drawableToBitmap(new RoundImage(bm));

        return logoRounded;
    }

    public String getParadaId() {
        return getString("paradaId");
    }

    public String getMessage() {
        String message = getString("Description");
        return message;
    }

    public Bitmap getLogo() {
        Bitmap bm = null;
        try {
            ParseFile parseFile = getParseFile("Logo");
            byte[] bitmapdata = new byte[0];
            bitmapdata = parseFile.getData();
            bm = BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return bm;
    }

    public void setLogo(ParseFile fileLogo) {
        put("Logo", fileLogo);
    }

    public LatLng getGPSLatLng() {
        return new LatLng(getGPS().getLatitude(), getGPS().getLongitude());
    }

    public String getImageParseUrl() {
        return getParseFile("Logo").getUrl();
    }

    public ParseGeoPoint getGPS() {
        return getParseGeoPoint("GPS");
    }

    public void setGPS(ParseGeoPoint GPS) {
        put("GPS", GPS);
    }

    public String getType() {
        return getString("Type");
    }

    public void setType(String type) {
        put("Type", type);
    }

    public void setMainUrl(String mainUrl) {
        put("MainUrl", mainUrl);
    }

    public void setPhone(String phone) {
        put("Phone", phone);
    }

    public void setUrl2(String url2) {
        put("Url2", url2);

    }

    public void setUrl3(String url3) {
        put("Url3", url3);
    }

    public void setDescription(String description) {
        put("Description", description);
    }

    public void setAirportCode(String airportCode) {
        put("AirportCode", airportCode);
    }

    public void setRating(int rating) {
        put("Rating", rating);
    }

    public void setAutomatic(boolean automatic) {
        put("Automatic", automatic);

    }

    public void setOwner(ParseUser owner) {
        put("Owner", owner);

    }

    // OTHER
    public boolean isAirport() {
        return getType().equals("AIRPORT");
    }

    private boolean isSpotFree(String bssid) throws ParseException {
        boolean res;
        ParseQuery query = ParseQuery.getQuery("SSIDS");
        query.whereEqualTo("bssid", bssid);
        query.whereDoesNotExist("associated_place");

        List elements = query.find();
        if (elements.size() == 0) {
            res = true;
            myLog.add("this SSID is free");
        } else {
            res = false;
            myLog.add("this SSID isn't free");
        }
        return res;
    }


    public boolean isBrowser() {
        boolean b = false;
        try {
            String first = getCards()[0];
            b = first.equals("Browser");
            myLog.add("iss bwoser= " + b + " fris card is " + first);
        } catch (Exception e) {
            myLog.add("no tinene card definida en parse");
        }
        return b;
    }

    public String getOneLineSummary() {
        StringBuilder sb = new StringBuilder(getName());
        if (this.notificationRequiresFetching()) {
            formatter form = new formatter(fetchedElements);
            sb.append(": " + form.summarizeAllLines(true));
        }
        return sb.toString();
    }

    //Fetching for notification
    public boolean notificationRequiresFetching() {
        if (getType().equals("bus_stop") || getName().equals("ESADECREAPOLIS")) {
            return true;
        } else {
            return false;
        }
    }

    public ArrayList getFetchedElements() {
        return fetchedElements;
    }

    public void setFetchingResults(ArrayList elements) {
        this.fetchedElements = elements;
    }

    public void resetFetchingResults() {
        for (Object o : fetchedElements) {
            LineTime lineTime = (LineTime) o;
            lineTime.setRoundedTime("-");
        }
    }

    public int getRepeatedOffRemoveFromNotification() {
        int res;
        if (getType().equals("bus_stop")) {
            res = 1;
        } else {
            res = parameters.repeatedOffRemoveFromNotification;
        }
        return res;
    }

    public String getFetchingUrl() {
        fetchingUrl = getString("FetchingUrl") + getParadaId();
//        if (getName().startsWith("ESADE F")) {
//            fetchingUrl = "http://intranet.esade.edu/web1/pkg_pantalles.info_layer?ample=500&alt=901&segons=0&edifici=2";
//        } else if (getType().equals("bus_stop")) {
//            fetchingUrl = "http://www.santqbus.santcugat.cat/consultatr.php?idparada=" + getParadaId() + "&idliniasae=-1&codlinea=-1";
//        }
        return fetchingUrl;
    }


//    public void setFetchingUrl(String fetchingUrl) {
//        this.fetchingUrl = fetchingUrl;
//    }
}
