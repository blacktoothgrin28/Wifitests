package com.herenow.fase1.CardData;

/**
 * Created by Milenko on 02/11/2015.
 */
public class ProductItem {
    String name, description, keyWords, imageUrl;

    public ProductItem(String name, String description, String keyWords, String imageUrl) {
        this.name = name;
        this.description = description;
        this.keyWords = keyWords;
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getKeyWords() {
        return keyWords;
    }
}
