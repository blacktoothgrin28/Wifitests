package com.herenow.fase1.actions;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.ContactsContract;
import android.widget.Toast;

import com.herenow.fase1.CardData.CompanyData;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import util.myLog;

/**
 * Created by Milenko on 28/10/2015.
 */
public class Actions {
    public static void OpenWebPage(Context mContext, String url) {
        try {
            if (!url.startsWith("https://") && !url.startsWith("http://")) {
                url = "http://" + url;
            }
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            mContext.startActivity(browserIntent);
        } catch (Exception e) {
            myLog.add("-err openweb: " + e.getMessage());
        }
    }

    public static void StartCall(Context mContext, String phone) {
        try {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + phone));
            mContext.startActivity(intent);
        } catch (Exception e) {
            myLog.add("----errror in call phone: " + e.getMessage());
        }
    }

    public static void SendEmail(Context mContext, String email) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("plain/text");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
        intent.putExtra(Intent.EXTRA_SUBJECT, "");
        intent.putExtra(Intent.EXTRA_TEXT, "");

        mContext.startActivity(Intent.createChooser(intent, ""));
    }

    public static void AddContact(CompanyData mCompanyData, Context mContext) {
        try {
            String DisplayName = mCompanyData.getName();
//            String MobileNumber = "123456";
//            String HomeNumber = "1111";
            String WorkNumber = mCompanyData.getPhone();
            String emailID = mCompanyData.getEmail();
//            String company = "bad";
//            String jobTitle = "abcd";

            mCompanyData.getLogo();


//                Bitmap bmImage = BitmapFactory.decodeResource(mContext.getResources(), mCompanyData.getLogoResId());
            Bitmap bmImage = mCompanyData.getLogo();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();//Todo protect in the case there is no image
            bmImage.compress(Bitmap.CompressFormat.JPEG, 80, baos);
            byte[] b = baos.toByteArray();

            ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

            ops.add(ContentProviderOperation.newInsert(
                    ContactsContract.RawContacts.CONTENT_URI)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                    .build());

            //------------------------------------------------------ Names
            if (DisplayName != null) {
                ops.add(ContentProviderOperation.newInsert(
                        ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE,
                                ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                        .withValue(
                                ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                                DisplayName).build());
            }

            //------------------------------------------------------ Mobile Number
//            if (MobileNumber != null) {
//                ops.add(ContentProviderOperation.
//                        newInsert(ContactsContract.Data.CONTENT_URI)
//                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
//                        .withValue(ContactsContract.Data.MIMETYPE,
//                                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
//                        .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, MobileNumber)
//                        .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
//                                ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
//                        .build());
//            }
//
//            //------------------------------------------------------ Home Numbers
//            if (HomeNumber != null) {
//                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
//                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
//                        .withValue(ContactsContract.Data.MIMETYPE,
//                                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
//                        .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, HomeNumber)
//                        .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
//                                ContactsContract.CommonDataKinds.Phone.TYPE_HOME)
//                        .build());
//            }

            //------------------------------------------------------ Work Numbers
            if (WorkNumber != null) {
                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE,
                                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, WorkNumber)
                        .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                                ContactsContract.CommonDataKinds.Phone.TYPE_WORK)
                        .build());
            }

            //------------------------------------------------------ Email
            if (emailID != null) {
                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE,
                                ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Email.DATA, emailID)
                        .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                        .build());
            }

            //------------------------------------------------------ Picture
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Photo.DATA15, b)
                    .build());

            //------------------------------------------------------ Organization
//            if (!company.equals("") && !jobTitle.equals("")) {
//                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
//                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
//                        .withValue(ContactsContract.Data.MIMETYPE,
//                                ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)
//                        .withValue(ContactsContract.CommonDataKinds.Organization.COMPANY, company)
//                        .withValue(ContactsContract.CommonDataKinds.Organization.TYPE, ContactsContract.CommonDataKinds.Organization.TYPE_WORK)
//                        .withValue(ContactsContract.CommonDataKinds.Organization.TITLE, jobTitle)
//                        .withValue(ContactsContract.CommonDataKinds.Organization.TYPE, ContactsContract.CommonDataKinds.Organization.TYPE_WORK)
//                        .build());
//            }

            // Asking the Contact provider to create a new contact
            try {
                mContext.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
                Toast.makeText(mContext, "Added to Contacts", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(mContext, "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            myLog.add("---eror adding contact: " + e.getMessage());
        }

    }
}
