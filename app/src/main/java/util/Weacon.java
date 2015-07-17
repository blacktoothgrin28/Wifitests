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
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Date;

/**
 * Created by Milenko on 28/05/2015.
 */
public class Weacon {

    private String phone;
    private boolean automatic;
    private String typeGoogle;
    private String url3 = "None";
    private String url2 = "None";
    private Date createAt;
    private Date updatedAt;
    private String imageParseUrl;
    private String ObjectId;
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

    public Weacon(String SSID, String BSSID, String name, String url, String message, GPSCoordinates gps, boolean validated, TYPE type, int level, Bitmap logo) {
        this.SSID = SSID;
        this.BSSID = BSSID;
        this.name = name;
        this.url = url;
        this.message = message;
        this.gps = gps;
        this.validated = validated;
        this.type = type;
        this.level = level;
        this.logo = logo;
    }
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
        this.url2 = obj.getString("Url2");
        this.url3 = obj.getString("Url3");
        this.ObjectId = obj.getObjectId();
        this.message = obj.getString("Description");
//        this.type = TYPE.valueOf(obj.getString("Type"));
        this.typeGoogle = obj.getString("type");//TODO homegineze types, probably use google's
        this.automatic = obj.getBoolean("Automatic");
//        this.level = obj.getInt("Level");
        this.phone = obj.getString("Phone");

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

    public String getUrl3() {
        return url3;
    }

    public String getUrl2() {
        return url2;
    }

    /**
     * upload the SSID and the weacon, and then relate them
     */
    public void upload() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        this.getLogo().compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        ParseFile fileLogo = new ParseFile(this.getName().replace(" ", "_") + ".png", byteArray);

        // Experiment :  upload in one call
        try {
            ParseObject parseWeacon = new ParseObject("Weacon");
            parseWeacon.put("Name", this.getName());
            parseWeacon.put("GPS", this.getParseGps());
            parseWeacon.put("Logo", fileLogo); //TODO check that file to upload is small
            parseWeacon.put("MainUrl", this.getUrl());
            parseWeacon.put("Url2", this.getUrl2());
            parseWeacon.put("Url3", this.getUrl3());
            parseWeacon.put("Automatic", false);
            parseWeacon.put("Description", this.getMessage());
            parseWeacon.put("Type", this.getTypeString());
            parseWeacon.put("Rating", -1);
            parseWeacon.put("Owner", ParseUser.getCurrentUser()); //Todo check if this works

            ParseObject parseSSID = new ParseObject("SSIDS");
            parseSSID.put("ssid", this.getSSID());
            parseSSID.put("bssid", this.getBSSID());
            parseSSID.put("Automatic", false);
            parseSSID.put("GPS", this.getParseGps());
            parseSSID.put("Level", this.getLevel());
            parseSSID.put("owner", ParseUser.getCurrentUser());
            parseSSID.put("validated", this.isValidated());
            parseSSID.put("associated_place", parseWeacon);

            parseSSID.saveInBackground(new SaveCallback() {
                public void done(ParseException e) {
                    if (e == null) {
                        // Saved successfully.
                        Log.e("mhp", "Increidble, it worked!");
                        //                    String id = po.getObjectId();
                        //                    Log.d(TAG, "The object id is: " + id);
                    } else {
                        // The save failed.
                        Log.e("mhp", "Sorry, error: " + e);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("mhp", "Sorry, pff, error: " + e);

        }



 /*       // 1. weacon, then the SSID
        ParseObject parseWeacon = null;
        try {
            parseWeacon = new ParseObject("Weacon");
            parseWeacon.put("Name", this.getName());
            parseWeacon.put("GPS", this.getParseGps());
            parseWeacon.put("Logo", fileLogo); //TODO check that file to upload is small
            parseWeacon.put("MainUrl", this.getUrl());
            parseWeacon.put("Url2", this.getUrl2());
            parseWeacon.put("Url3", this.getUrl3());
            parseWeacon.put("Automatic", false);
            parseWeacon.put("Description", this.getMessage());
            parseWeacon.put("Type", this.getTypeString());
            parseWeacon.put("Rating", -1);
            parseWeacon.put("Owner", ParseUser.getCurrentUser()); //Todo check if this works
            parseWeacon.saveInBackground(new SaveCallback() {
                public void done(ParseException e) {
                    if (e == null) {
                        // Saved successfully.
                        Log.d("mhp" "User update saved!");
                        String id = po.getObjectId();
                        Log.d(TAG, "The object id is: " + id);
                    } else {
                        // The save failed.
                        Log.d(TAG, "User update error: " + e);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("mhp", "Weacon |upload place . error="+e.getMessage());

        }

        // 2. upload the SSID
        //Todo check if already used by other, Validation cycle

        try {
            ParseObject parseSSID = new ParseObject("SSIDS");
            parseSSID.put("ssid", this.getSSID());
            parseSSID.put("bssid", this.getBSSID());
            parseSSID.put("Automatic", false);
            parseSSID.put("GPS", this.getParseGps());
            parseSSID.put("Level", this.getLevel());
            parseSSID.put("owner", ParseUser.getCurrentUser());
            parseSSID.put("validated", this.isValidated());
            parseSSID.put("associated_place", placeId);
            parseWeacon.saveInBackground();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("mhp", "Weacon |upload ssid. error=" + e.getMessage());
        }

*/
    }

    public String getObjectId() {
        return ObjectId;
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

