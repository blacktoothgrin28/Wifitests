package com.herenow.fase1.CardData;

/**
 * Created by Milenko on 21/10/2015.
 */
public class ChefData {
    private String imageUrl, name;
    private String description;
    private Double price;

    public ChefData(String name, String imageUrl,Double price) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.price=price;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getName() {
        return name;
    }

    public Double getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }
}
