package com.herenow.fase1.CardData;

public class ProductItemBuilder {
    private String name;
    private String desription;
    private String keyWords;
    private String imageUrl;

    public ProductItemBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public ProductItemBuilder setDesription(String desription) {
        this.desription = desription;
        return this;
    }

    public ProductItemBuilder setKeyWords(String keyWords) {
        this.keyWords = keyWords;
        return this;
    }

    public ProductItemBuilder setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }

    public ProductItem createProductItem() {
        return new ProductItem(name, desription, keyWords, imageUrl);
    }
}