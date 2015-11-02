package com.herenow.fase1.CardData;


import java.util.ArrayList;

/**
 * Created by Milenko on 20/09/2015.
 */
public class ProductsData {
    public String name;
    private ArrayList<ProductItem> products;

    public ProductsData(String name) {
        this.name = name;
        products = new ArrayList<>();
    }

    public void addProduct(ProductItem productItem) {
        products.add(productItem);
    }


    public ArrayList<ProductItem> getProducts() {
        return products;
    }
}


