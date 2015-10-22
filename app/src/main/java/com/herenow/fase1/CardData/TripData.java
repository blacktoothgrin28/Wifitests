package com.herenow.fase1.CardData;

/**
 * Created by halatm on 22/10/2015.
 */
public class TripData {
    private String ranking, grade;

    public TripData(String ranking, String grade) {
        this.ranking = ranking;
        this.grade = grade;
    }

    public String getRanking() {
        return ranking;
    }

    public String getGrade() {
        return grade;
    }

    public class Comment {
        private String user, title, comment, date;
        private int grade;
    }
}
