package com.herenow.fase1.CardData;

import java.util.ArrayList;

public class MenuSection {
    private final String name;
    private ArrayList<Dish> dishes;


    public MenuSection(String name) {
        this.name = name;
        dishes = new ArrayList<>();
    }

    public MenuSection addFoodItem(Dish dish) {
        dishes.add(dish);
        return this;
    }

    public MenuSection addFoodItem(String name, String description, String[] ingredients, double price) {
        return addFoodItem(new Dish(name, description, ingredients, price));
    }

    public ArrayList<Dish> getDishes() {
        return dishes;
    }

    public String getName() {
        return name;
    }

    public ArrayList getDishesDescriptions() {
        ArrayList dishesDescriptions = new ArrayList();
        for (Dish dish : dishes) {
            dishesDescriptions.add(dish.description);
        }

        return dishesDescriptions;
    }


    public class Dish {
        public String name;
        public String description;
        public String[] ingredients;
        public double price;
        private String imageUrl;

        public Dish(String name, String description, String[] ingredients, double price) {
            this.name = name;
            this.description = description;
            this.ingredients = ingredients;
            this.price = price;
            //TODO add image to dish
        }

        public String getImageUrl() {
            if (imageUrl == null || imageUrl == "") {
                imageUrl = "http://d3rsl50p8hwbdu.cloudfront.net/square_374_1541.jpg";
            }
            return imageUrl;
        }
    }
}
