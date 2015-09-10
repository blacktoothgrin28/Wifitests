package com.herenow.fase1.CardData;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.DrawableRes;

import java.util.HashMap;

/**
 * Created by Milenko on 25/08/2015.
 */
public class CompanyData {
    Bitmap mainImage, logo;
    String name;
    String description;
    String phone;
    String email;

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    String website;
    String typeOfBusiness;
    String foundationPlace;
    String director;
    String headQuarters;
    String lemma;
    String[] founders, subsidiaries;
    String foundationYear;
    //    String skype;
    //Product[] products;
    //Client[] clients
    int nEmployees;
    //    int logoResId;
    @DrawableRes
    int imageResId, logoResId;
    HashMap<String, String> dataTable = new HashMap();

    public CompanyData(String name, int imageResId, int logoResId) {
        this.name = name;
        this.imageResId = imageResId;
        this.logoResId = logoResId;
    }

    public CompanyData(String name, Bitmap mainImage, Bitmap logo) {
        this.name = name;
        this.mainImage = mainImage;
        this.logo = logo;
    }

    public static SetupWizard with(Context context) {
        return new SetupWizard(context);
    }

    public void setEmployeesNumber(int workers) {
        this.nEmployees = workers;
    }

    public String getLemma() {
        return lemma;
    }

    public void setLemma(String lemma) {
        this.lemma = lemma;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public int getLogoResId() {
        return logoResId;
    }

    public int getImageResId() {
        return imageResId;
    }

    public String getExtraInfo() {
        StringBuilder sb = new StringBuilder();

        //Foundation (year, people)
        StringBuilder sbFoundation = new StringBuilder();
        if (foundationYear != null) sbFoundation.append("In " + foundationYear);
        if (founders.length > 0) {
            sbFoundation.append(" by ");
            for (int i = 0; i < founders.length - 1; i++) {
                sbFoundation.append(founders[i]).append(", ");
            }
            sbFoundation.deleteCharAt(sbFoundation.length() - 2);
            sbFoundation.append("and " + founders[founders.length - 1]);
        }
        AddPair(sb, "Foundation", sbFoundation.toString());
        AddPair(sb, "Employees", Integer.toString(nEmployees));
        AddPair(sb, "Director", director);

        return sb.toString();
    }

    private StringBuilder AddPair(StringBuilder sb, String type, String value) {
        if (value != null) sb.append(type + ": " + value + "\n");
        return sb;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFounders(String[] founders) {
        this.founders = founders;
    }

    public void setFoundationYear(String foundationYear) {
        this.foundationYear = foundationYear;
    }

    public void setTypeOfBusiness(String typeOfBusiness) {
        this.typeOfBusiness = typeOfBusiness;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public static final class SetupWizard {
        private final Context mContext;
        private String mName;

        private SetupWizard(Context context) {
            mContext = context;
        }

        public SetupWizard setName(String name) {
            mName = name;
            return this;
        }
    }
}
