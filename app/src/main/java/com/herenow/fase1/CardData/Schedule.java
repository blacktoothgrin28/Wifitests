package com.herenow.fase1.CardData;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Milenko on 20/09/2015.
 */
public class Schedule {
    public String name;
    public String subtitle;
    ArrayList<ScheduleItem> schedule;
    private Calendar date;
    private long endOfMeeting = 0;


    public Schedule(String name) {
        this.name = name;
        schedule = new ArrayList<>();
    }

    public void setEndOfMeeting(long endOfMeeting) {
        this.endOfMeeting = endOfMeeting;
    }

    public void addItem(ScheduleItem item) {
        item.setPosition(schedule.size());
        schedule.add(item);
    }

    public void addItem(String title, String speaker, int h, int min, String place, String url) {
        addItem(new ScheduleItem(title, speaker, h, min, place, url));
    }
    public void addItem(String title, String speaker, int h, int min, String place, String url, String fileUrl) {
        addItem(new ScheduleItem(title, speaker, h, min, place, url,fileUrl));
    }

    public ArrayList<ScheduleItem> getData() {
        return schedule;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public String getDateString() {
        SimpleDateFormat format = new SimpleDateFormat("E d/MM/yy");
        return format.format(date.getTime());
    }

    public class ScheduleItem {
        private final String place;
        private final String speaker;
        public String url, description;//Todo add a description to event
        String title, hour, urlImage;
        int h, min;
        private int position;
        String fileUrl;
//        private Calendar date;

        public ScheduleItem(String title, String speaker, int h, int min, String place, String url) {
            this.speaker = speaker;
            this.url = url;
            this.title = title;
            this.h = h;
            this.min = min;
            this.place = place;
        }

        public ScheduleItem(String title, String speaker, int h, int min, String place, String url, String fileUrl) {
            this( title,  speaker,  h,  min,  place,  url);
            this.fileUrl=fileUrl;
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

        public String getDescription() {
            return description;
        }

        public void setPosition(int position) {
            this.position = position;
        }


        public long getStartInMilli() {
            long startInMilli;

            if (date == null) {
                //assign today
                date = Calendar.getInstance();
                date.set(Calendar.MINUTE, 0);
                date.set(Calendar.HOUR_OF_DAY, 0);
                date.set(Calendar.SECOND, 0);
                date.set(Calendar.MILLISECOND, 0);
            }
            startInMilli = date.getTimeInMillis() + (h * 60 + min) * 60 * 1000;

            return startInMilli;
        }

        public long getEndInMilli() {

            long end;
            if (position == schedule.size() - 1) {
                if (endOfMeeting != 0) {
                    end = endOfMeeting;
                } else {
                    end = getStartInMilli() + 45 * 60 * 60 * 1000; //assume is a 45 min meeting
                }
            } else {
                //use the start of the next event
                end = schedule.get(position + 1).getStartInMilli();
            }
            return end;
        }

        public String getUrlFile() {
            return fileUrl;
        }
    }
}
