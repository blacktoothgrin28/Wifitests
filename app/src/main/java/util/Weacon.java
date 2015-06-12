package util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Log;

import com.herenow.fase1.R;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

import java.io.File;
import java.util.Date;

enum TYPE {
    TOURISM, PRIVATE, COMPANY, SPORT, EDUCATIONAL, GOVERNMENT, PUBLIC_SERVICE, TRANSPORT,
    HEALTH, RETAIL, RESTAURANT, OTHER
}

/**
 * Created by Milenko on 28/05/2015.
 */
public class Weacon {

    private Date createAt;
    private Date updatedAt;
    private String imageParseUrl;
    private String idParseObject;
    private boolean validated = false;
    private Bitmap logoRounded;
    private int level; // Threshold fot detection. default=-72
    private String logoFileName;
    private Bitmap logo;
    private String SSID = "null";
    private String BSSID = "null";
    private String name, url; //Name of the weacon and main URL
    private String message; //Displayed in notification
    private File imagePath;
    private GPSCoordinates gps;
    private TYPE type = TYPE.OTHER; //Type of weacon,
    private String[] SecondaryUrls = null;
//    private String[] MultipleSSIDS= null; //TODO rules for monument detection


    public Weacon(String[] register, Activity act) {

        SSID = register[0];
        name = register[1];
        level = Integer.parseInt(register[2]);
        url = register[3];
        logoFileName = register[4];
        message = register[5];
        imagePath = new File(Environment.getExternalStorageDirectory(), "HNdata/Images/" + logoFileName);
        if (imagePath.isFile()) {
            Bitmap bm = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(imagePath.getPath()), 120, 120, true);
            logoRounded = drawableToBitmap(new RoundImage(bm));
            logo = bm;
        } else {
            //Default logo
            logo = BitmapFactory.decodeResource(act.getResources(), R.mipmap.ic_launcher);
        }

        //Others
        gps = new GPSCoordinates(0, 0);
        BSSID = "null";
        validated = false;
//        RoundLogo = new RoundImage(bm);
    }

    public Weacon(ParseObject obj) {
        this.name = obj.getString("Name");
        this.url = obj.getString("MainUrl");
        this.SSID = obj.getString("SSID");
        this.BSSID = obj.getString("BSSID");
        this.idParseObject = obj.getString("objectId");
        this.message = obj.getString("Description");
        this.type = TYPE.valueOf(obj.getString("Type"));

        try {
            this.gps = new GPSCoordinates(obj.getParseGeoPoint("GPS").getLatitude(), obj.getParseGeoPoint("GPS").getLongitude());
            this.imageParseUrl = obj.getParseFile("Logo").getUrl();//
            this.validated = obj.getBoolean("Validated");
            this.createAt = obj.getCreatedAt();
            this.updatedAt = obj.getUpdatedAt();

            //Obtaining the bitmap //TODO do not load all images, only urls to images
            ParseFile parseFile = obj.getParseFile("Logo");
            byte[] bitmapdata = parseFile.getData();
            Bitmap bm = BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length);
            this.logo = bm;
            logoRounded = drawableToBitmap(new RoundImage(bm));
//                this.createdby
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("mhp", e.toString());
        }

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

    public String getSSID() {
        return SSID;
    }

    public String getName() {
        return name;
    }

    public String getBSSID() {
        return BSSID;
    }

    public String getMessage() {

        return message;
    }

    public String getUrl() {
        return url;
    }

    public Bitmap getLogo() {
        return logo;
    }

    public Bitmap getLogoRounded() {
        return logoRounded;
    }

    public File getImagePath() {
        return imagePath;
    }

    public GPSCoordinates getGps() {
        return gps;
    }

    public int getLevel() {
        return level;
    }

    public boolean isValidated() {
        return validated;
    }

    public String getTypeString() {

        return type.toString();
    }

    public boolean isMultiUrl() {
        boolean res = false;
        if (SecondaryUrls != null & SecondaryUrls.length > 0) {
            res = true;
        }

        return res;
    }

    public Object getParseGps() {
        ParseGeoPoint point = new ParseGeoPoint(gps.getLatitude(), gps.getLongitude());
        return point;
    }

    public String getImageParseUrl() {
        return imageParseUrl;
    }
}

class GPSCoordinates {
    private double latitude = 0;
    private double longitude = 0;

    public GPSCoordinates(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}
