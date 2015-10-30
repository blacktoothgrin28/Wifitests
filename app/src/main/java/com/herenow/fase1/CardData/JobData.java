package com.herenow.fase1.CardData;

import java.util.ArrayList;

import util.stringUtils;

/**
 * Created by Milenko on 29/10/2015.
 */
public class JobData {
    public String name;
    private String dateString;
    private ArrayList<JobOffer> mJobOffers;
    public String Company;

    public JobData(String name, String dateString, String company) {
        this.dateString = dateString;
        mJobOffers = new ArrayList<>();
        this.name = name;
        this.Company = company;
    }

    public String getDateString() {
        return dateString;
    }

    public ArrayList<JobOffer> getItems() {

        return mJobOffers;
    }

    public void add(JobOffer j1) {
        mJobOffers.add(j1);
    }

    public static class JobOffer {

        private String url;
        public String title, description;
        String[] languages, skills;

        public JobOffer(String url, String title, String description, String[] languages, String[] skills) {

            this.url = url;
            this.title = title;
            this.description = description;
            this.languages = languages;
            this.skills = skills;
        }

        public String getLanguages() {
            return stringUtils.ConcatenateComma(languages);
        }

        public String getSkills() {
            return stringUtils.ConcatenateComma(skills);
        }

        public String getUrl() {
            return url;
        }
    }

    public static class JobOfferBuilder {
        private String url;
        private String title;
        private String description;
        private String[] languages;
        private String[] skills;

        public JobOfferBuilder setUrl(String url) {
            this.url = url;
            return this;
        }

        public JobOfferBuilder setTitle(String title) {
            this.title = title;
            return this;
        }

        public JobOfferBuilder setDescription(String description) {
            this.description = description;
            return this;
        }

        public JobOfferBuilder setLanguages(String[] languages) {
            this.languages = languages;
            return this;
        }

        public JobOfferBuilder setSkills(String[] skills) {
            this.skills = skills;
            return this;
        }

        public JobOffer createJobOffer() {
            return new JobOffer(url, title, description, languages, skills);
        }
    }
}
