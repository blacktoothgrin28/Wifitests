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
    String mName, description, phone, email, website, typeOfBusiness,
            foundationPlace, director, headQuarters, lemma;
    String[] founders, subsidiaries;
    String foundationYear;
    //Product[] products;
    //Client[] clients
    int nEmployees;
    //    int logoResId;
    @DrawableRes
    int imageResId, logoResId;
    HashMap<String, String> dataTable = new HashMap();

    public CompanyData(String mName, int imageResId, int logoResId) {
        this.mName = mName;
        this.imageResId = imageResId;
        this.logoResId = logoResId;
    }

    public CompanyData(String mName, Bitmap mainImage, Bitmap logo) {
        this.mName = mName;
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

    public String getDescription() {
        return description;
    }

    public String getName() {
        return mName;
    }

    public int getLogoResId() {
        return logoResId;
    }

    public int getImageResId() {
        return imageResId;
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
//            sbFoundation.deleteCharAt(sb.length()-1);
            sbFoundation.append("and " + founders[founders.length - 1]);
        }
        AddPair(sb, "<b>Foundation</b>", sbFoundation.toString());

        AddPair(sb, "*Employees*", Integer.toString(nEmployees));
        AddPair(sb, "Director", director);
//        AddPair(sb, "Description", description);

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

    public void setLemma(String lemma) {
        this.lemma = lemma;
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

    public void setDescription(String description) {
        this.description = description;
    }
}
