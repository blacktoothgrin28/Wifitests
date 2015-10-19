package com.herenow.fase1.CardData;


import android.graphics.Bitmap;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Milenko on 20/09/2015.
 */
public class FoodMenu {
    public String name;
    public String subtitle;
    ArrayList<FoodItem> foodItems;
    private Calendar date;


    public FoodMenu(String name) {
        this.name = name;
        foodItems = new ArrayList<>();
    }


    public void addItem(FoodItem item) {
        item.setPosition(foodItems.size());
        foodItems.add(item);
    }

    public void addItem(String title, String speaker, int h, int min, String place, String url) {
        addItem(new FoodItem(title, speaker, h, min, place, url));
    }

    public void addItem(String title, String speaker, int h, int min, String place, String url, String fileUrl) {
        addItem(new FoodItem(title, speaker, h, min, place, url, fileUrl));
    }

    public ArrayList<FoodItem> getData() {
        return foodItems;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public String getDateString() {
        SimpleDateFormat format = new SimpleDateFormat("E d/MM/yy");
        return format.format(date.getTime());
    }

    public class FoodItem {
        private String name, description, urlImage;
        private String[] ingredients;
        private long price;
        private Bitmap picture;
        private int position;

        public FoodItem(String title, String speaker, int h, int min, String place, String url) {
        }

        public FoodItem(String title, String speaker, int h, int min, String place, String url, String fileUrl) {
            this(title, speaker, h, min, place, url);
        }


        public String getUrlImage() {
            return urlImage;
        }

        public String getDescription() {
            return description;
        }

        public void setPosition(int position) {
            this.position = position;
        }
    }
}
