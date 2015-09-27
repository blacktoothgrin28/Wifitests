package com.herenow.fase1.CardData;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.DrawableRes;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import util.myLog;

/**
 * Created by Milenko on 25/08/2015.
 */
public class CompanyData {
    private String logoUrl;
    private String mainImageUrl;
    Bitmap mainImage, logo;
    String name;
    String description;
    String phone;
    String email;
    String website;
    boolean isWebResponsive; //TODO
    String typeOfBusiness;
    String director;
    int nEmployees;
    String lemma;
    String[] founders, subsidiaries;
    String foundationYear;
    String headQuarters;
    String memberOfGroup;
    String foundationPlace;
    String linkedinUrl;
    //    String skype;
    //Product[] products;
    //Client[] clients
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

    public String getLogoUrl() {
        return logoUrl;
    }

    public String getMainImageUrl() {
        return mainImageUrl;
    }

    public CompanyData(ParseObject po) {
        name = po.getString("Name");
        description = po.getString("Description");
        phone = po.getString("Phone");
        email = po.getString("Email");
        website = po.getString("Website");
        lemma = po.getString("Lemma");
        foundationYear = po.getString("FoundationYear");
        headQuarters = po.getString("Headquarters");
        memberOfGroup = po.getString("MemberOfGroup");
        foundationPlace = po.getString("FoundationPlace");
        linkedinUrl = po.getString("LinkedinUrl");
        typeOfBusiness = po.getString("TypeOfBusiness");
        director = po.getString("Director");
//        subsidiaries=po.getString("Subsidiaries"); todo include subsidiaries

        nEmployees = (int) po.getNumber("Employees");

        //Images
        //todo use picasso
        ParseFile parseFile = po.getParseFile("Logo");
        logoUrl = parseFile.getUrl();
        myLog.add("****4urlIcono=" + logoUrl);
//            byte[] bitmapdata = parseFile.getData();
//            logo = BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length);

        parseFile = po.getParseFile("MainImage");
        mainImageUrl = parseFile.getUrl();
//            bitmapdata = parseFile.getData();
//            mainImage = BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length);

        List<Object> al = po.getList("Founders");
        founders = new String[al.size()];
        al.toArray(founders);


    }

    public static SetupWizard with(Context context) {
        return new SetupWizard(context);
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getLinkedinUrl() {
        return linkedinUrl;
    }

    public void setLinkedinUrl(String linkedinUrl) {
        this.linkedinUrl = linkedinUrl;
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

    /**
     * Remove {"S.L.","S.A",etc},  at the end of name
     */
    public String getNameClean() {
        final String[] abrev = {" SL", " S.L.", " S.A.", " SA", " INC.", " INC", " CO", " CO."};//TODO see other company names abrev
        String upper = name.toUpperCase();
        String nameClean = name;
        for (String ab : abrev) {
            if (upper.endsWith(ab)) {
                myLog.add("company name ends with:" + ab);
                nameClean = name.substring(0, name.length() - ab.length());
                break;
            }
        }
        return nameClean;
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
        if (founders.length == 1) {
            sbFoundation.append(" by " + founders[0] + ".");
        } else if (founders.length > 1) {
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

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Bitmap getLogo() {
        return logo;
    }

    public Bitmap getMainImage() {
        return mainImage;
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
