package util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;

import com.herenow.fase1.R;

import java.io.File;

/**
 * Created by Milenko on 28/05/2015.
 */
public class Weacon {

    private int level;
    private String logoFileName;
    private Bitmap logo;
    private String name, url;
    private String SSID;
    private String message;
    private File path;


    public Weacon(String[] register, Activity act) {


        SSID = register[0];
        name = register[1];
        level = Integer.parseInt(register[2]);
        url = register[3];
        logoFileName = register[4];
        message = register[5];
        path = new File(Environment.getExternalStorageDirectory(), "HNdata/Images/" + logoFileName);
        if (path.isFile()) {
            Bitmap bm = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(path.getPath()), 120, 120, false);
            logo = drawableToBitmap(new RoundImage(bm));
        } else {
            //Default logo
            logo = BitmapFactory.decodeResource(act.getResources(), R.mipmap.ic_launcher);
        }
//                getBitmap();
//        logo = new RoundImage(bm);
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

    public String getMessage() {

        return message;
    }

    public String getUrl() {
        return url;
    }

    public Bitmap getLogo() {
        return logo;
    }

    public File getPath() {
        return path;
    }
    public int getLevel() {
        return level;
    }
}
