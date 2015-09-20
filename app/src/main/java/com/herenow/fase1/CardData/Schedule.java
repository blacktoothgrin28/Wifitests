package com.herenow.fase1.CardData;


import java.util.ArrayList;

/**
 * Created by Milenko on 20/09/2015.
 */
public class Schedule {
    ArrayList<ScheduleItem> schedule;
    String name;

    public Schedule(String name) {
        this.name = name;
        schedule = new ArrayList<>();
    }

    public void addItem(ScheduleItem item) {
        schedule.add(item);
    }

    public void addItem(String title, String speaker, int h, int min, String place, String url) {
        addItem(new ScheduleItem(title, speaker, h, min, place, url));
    }

    public ArrayList<ScheduleItem> getData() {
        return schedule;
    }

    public class ScheduleItem {
        private final String place;
        private final String speaker;
        public String url;
        String title, hour, urlImage;
        int h, min;

        public ScheduleItem(String title, String speaker, int h, int min, String place, String url) {
            this.speaker = speaker;
            this.url = url;
            this.title = title;
            this.h = h;
            this.min = min;
            this.place = place;
        }

        public String getPlace() {
            return place;
        }

        public String getSpeaker() {
            return speaker;
        }

        public String getUrl() {
            return url;
        }

        public String getTitle() {
            return title;
        }

        public String getHour() {
            return hour;
        }

        public String getUrlImage() {
            return urlImage;
        }

        public int getH() {
            return h;
        }

        public int getMin() {
            return min;
        }
    }
}
