package com.herenow.fase1;

import android.graphics.Bitmap;

/**
 * Created by Milenko on 12/09/2015.
 */
public class Noticia {
    public String title, source, content, imageUrl, link;
    public String date;
    public boolean isExact;
    public Bitmap image;

    public Noticia() {
        image = null;
        imageUrl = null;
    }
}
