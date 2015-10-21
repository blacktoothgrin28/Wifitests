package com.herenow.fase1.CardData;

import java.util.ArrayList;

/**
 * Created by Milenko on 21/10/2015.
 */
public class DayFoodMenu {
    private final String name;
    private ArrayList<MenuSection> sections;
    private Double price;

    public DayFoodMenu(String name, double price) {
        this.name = name;
        this.price = price;
        sections = new ArrayList<>();
    }

    public void addSection(MenuSection section) {
        sections.add(section);
//        sectionNames.add(section.getName());//To Keep the relative order
    }

    public String getName() {
        return name;
    }

    public Double getPrice() {
        return price;
    }

    public ArrayList<MenuSection> getSections() {
        return sections;
    }
}
