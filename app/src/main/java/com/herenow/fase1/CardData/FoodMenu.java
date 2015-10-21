package com.herenow.fase1.CardData;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by Milenko on 20/09/2015.
 */
public class FoodMenu {
    public String name;
    public String subtitle;
    HashMap<String, MenuSection> sections;
    private Calendar date;
    private ArrayList<String> sectionNames = new ArrayList<>();

    public FoodMenu(String name) {
        this.name = name;
        sections = new HashMap<>();
    }

//    public enum section {APERITIVO, PRIMERO, SEGUNDO, POSTRE, VINOS}

    public void addSection(MenuSection section) {
        sections.put(section.getName(), section);
        sectionNames.add(section.getName());//To Keep the relative order
    }

    public ArrayList<String> getSectionNames() {
        return sectionNames;
    }

    public MenuSection getSection(String sectionName) {
        return sections.get(sectionName);
    }

    public MenuSection getSection(int item) {
        MenuSection ms = null;
        if (item < sections.size()) ms = (MenuSection) sections.values().toArray()[item];
        return ms;
    }

    public ArrayList<MenuSection.Dish> getDishesFirstSection() {
        MenuSection menuSection = (MenuSection) sections.values().toArray()[0];
        return menuSection.getDishes();
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public String getDateString() {
        SimpleDateFormat format = new SimpleDateFormat("E d/MM/yy");
        return format.format(date.getTime());
    }

    public String getName() {
        return name;
    }

}


