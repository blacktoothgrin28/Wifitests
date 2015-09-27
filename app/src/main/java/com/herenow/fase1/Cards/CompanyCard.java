/*
 * ******************************************************************************
 *   Copyright (c) 2013-2014 Gabriele Mariotti.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *  *****************************************************************************
 */

package com.herenow.fase1.Cards;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.herenow.fase1.CardData.CompanyData;
import com.herenow.fase1.R;
import com.herenow.fase1.test.CustomExpandCard;
import com.herenow.fase1.test.mMaterialLargeImageCardThumbnail;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import it.gmariotti.cardslib.library.cards.actions.BaseSupplementalAction;
import it.gmariotti.cardslib.library.cards.actions.IconSupplementalAction;
import it.gmariotti.cardslib.library.cards.actions.TextSupplementalAction;
import it.gmariotti.cardslib.library.cards.base.BaseMaterialCard;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.ViewToClickToExpand;
import util.myLog;

/**
 * @author Gabriele Mariotti (gabri.mariotti@gmail.com)
 */
public class CompanyCard extends BaseMaterialCard {

    /**
     * Resource Drawable ID
     */
    protected
    @DrawableRes
    int mDrawableIdCardThumbnail;
    protected
    @DrawableRes
    int mDrawableIconIdCardThumbnail;
    /**
     * Resource Drawable URL
     */
    protected String mUrlCardThumbnail;
    /**
     * Interface for external usage for Thumbnail
     */
    protected DrawableExternal mExternalCardThumbnail;
    /**
     * Title to use for the title over the image
     */
    protected CharSequence mTextOverImage;
    /**
     * Resource Id to use for the title over the image
     */
    protected
    @StringRes
    int mTextOverImageResId;
    /**
     * The subtitle
     */
    protected CharSequence mSubTitle;
    private boolean couldUseNativeInnerLayout = false;
    private String mDrawableIconUrl;

    public CompanyCard(Context context) {
        this(context, it.gmariotti.cardslib.library.cards.R.layout.native_material_largeimage_inner_base_main);
    }

    // -------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------

    public CompanyCard(Context context, @LayoutRes int innerLayout) {
        super(context, innerLayout);
    }

    public static SetupWizard with(Context context) {
        return new SetupWizard(context);
    }

    // -------------------------------------------------------------
    // Builder
    // -------------------------------------------------------------

    @Override
    public void build() {

        //Set CardThumbnail
        if (mCardThumbnail == null) {
            mCardThumbnail = initializeCardThumbnail();

            if (mExternalCardThumbnail != null) {
                mCardThumbnail.setExternalUsage(true);
//                ((mMaterialLargeImageCardThumbnail)mCardThumbnail).setExternalCardThumbnail(mExternalCardThumbnail);
            } else {
                if (mDrawableIdCardThumbnail != 0) {
                    mCardThumbnail.setDrawableResource(mDrawableIdCardThumbnail);
                } else if (mUrlCardThumbnail != null) {
                    mCardThumbnail.setUrlResource(mUrlCardThumbnail);
                }
            }
            addCardThumbnail(mCardThumbnail);
        }

        ((mMaterialLargeImageCardThumbnail) mCardThumbnail).setTextOverImage(mTextOverImage);
        ((mMaterialLargeImageCardThumbnail) mCardThumbnail).setTextOverImageResId(mTextOverImageResId);
//        ((mMaterialLargeImageCardThumbnail) mCardThumbnail).setIconOverImageResId(mDrawableIconIdCardThumbnail);
        myLog.add("****8urlIcono="+mDrawableIconUrl);
        ((mMaterialLargeImageCardThumbnail) mCardThumbnail).setIconOverImageUrl(mDrawableIconUrl);

    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        //Use the title in super method
        super.setupInnerViewElements(parent, view);

        //Add a simple subtitle
        if (view != null) {
            TextView mTitleView = (TextView) view.findViewById(it.gmariotti.cardslib.library.cards.R.id.card_main_inner_simple_subtitle);
            if (mTitleView != null)
                mTitleView.setText(mSubTitle);

//            //Here look for the view to click (la lupa)
//            ImageButton mib = (ImageButton) view.findViewById(R.id.ic1);
//            if (mib != null){
//                    ViewToClickToExpand viewToClickToExpand =
//                        ViewToClickToExpand.builder()
//                                .setupView(mib);
//                setViewToClickToExpand(viewToClickToExpand);
//            }


        }


        if (view != null) {
            TextView mTitleView = (TextView) view.findViewById(it.gmariotti.cardslib.library.R.id.card_main_inner_simple_title);
        }
    }

    // -------------------------------------------------------------
    // Build
    // -------------------------------------------------------------

    @Override
    public void setOnClickListener(OnCardClickListener onClickListener) {
        addPartialOnClickListener(CLICK_LISTENER_ACTIONAREA1_VIEW, onClickListener);
    }

    /**
     * Initialize the MaterialLargeImageCardThumbnail
     *
     * @return
     */
    protected mMaterialLargeImageCardThumbnail initializeCardThumbnail() {
        return new mMaterialLargeImageCardThumbnail(mContext);
    }

    /**
     * Returns
     * the title over the image
     *
     * @return
     */
    public CharSequence getTextOverImage() {
        return mTextOverImage;
    }

    /**
     * Sets the title over the image
     *
     * @param textOverImage
     */
    public void setTextOverImage(CharSequence textOverImage) {
        mTextOverImage = textOverImage;
    }

    // -------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------

    /**
     * Sets the Resource Id to use for the title over the image
     *
     * @param textOverImageResId
     */
    public void setTextOverImageResId(int textOverImageResId) {
        mTextOverImageResId = textOverImageResId;
    }

    /**
     * Returns the Resource Drawable ID
     *
     * @return
     */
    public int getDrawableIdCardThumbnail() {
        return mDrawableIdCardThumbnail;
    }

    /**
     * Sets the Resource Drawable ID
     *
     * @param drawableIdCardThumbnail
     */
    public void setDrawableIdCardThumbnail(int drawableIdCardThumbnail) {
        mDrawableIdCardThumbnail = drawableIdCardThumbnail;
    }

    public void setDrawableIconIdCardThumbnail(int drawableIconIdCardThumbnail) {
        mDrawableIconIdCardThumbnail = drawableIconIdCardThumbnail;
    }

    /**
     * Returns the Drawable URL
     *
     * @return
     */
    public String getUrlCardThumbnail() {
        return mUrlCardThumbnail;
    }

    /**
     * Sets the Drawable URL
     *
     * @param urlCardThumbnail
     */
    public void setUrlCardThumbnail(String urlCardThumbnail) {
        mUrlCardThumbnail = urlCardThumbnail;
    }

    public void setUrlCardIcon(String urlCardIcon) {
        myLog.add("****6urlIcono="+urlCardIcon);
        mDrawableIconUrl = urlCardIcon;
    }

    /**
     * Sets the interface to be called with the thumbnail
     *
     * @param externalCardThumbnail
     */
    public void setExternalCardThumbnail(DrawableExternal externalCardThumbnail) {
        mExternalCardThumbnail = externalCardThumbnail;
    }

    /**
     * Returns the subtitle
     *
     * @return
     */
    public CharSequence getSubTitle() {
        return mSubTitle;
    }

    /**
     * Sets the subtitle
     *
     * @param subTitle
     */
    public void setSubTitle(CharSequence subTitle) {
        mSubTitle = subTitle;
    }

    /**
     * Inflates the inner layout and adds to parent layout.
     * Then calls {@link #setupInnerViewElements(android.view.ViewGroup, android.view.View)} method
     * to setup all values.
     *
     * @param context context
     * @param parent  Inner Frame
     * @return
     */
    @Override
    public View getInnerView(Context context, ViewGroup parent) {

        //Check if the default inner layout could be the native layout
        if (couldUseNativeInnerLayout && isNative())
            mInnerLayout = R.layout.native_inner_base_expand;

        //Inflate the inner layout
        View view = super.getInnerView(context, parent);

        //This provides a simple implementation with a single title
        if (view != null) {

            //Add inner view to parent
            parent.addView(view);

            //Setup values
            if (mInnerLayout > -1) {
                setupInnerViewElements(parent, view);
            }
        }
        return view;
    }

    public static interface DrawableExternal {
        void setupInnerViewElements(ViewGroup parent, View viewImage);
    }

    public static final class SetupWizard {
        private final Context mContext;
        @DrawableRes
        int mDrawableCardIcon;
        private CompanyData mCompanyData;
        private
        @DrawableRes
        int mDrawableCardThumbnail;
        private String mUrlCardThumbnail;
        private DrawableExternal mExternalCardThumbnail;
        private CharSequence mTextOverImage;
        private
        @StringRes
        int mTextOverImageResId;
        private CharSequence mTitle;
        private CharSequence mSubTitle;
        private ArrayList<BaseSupplementalAction> mActions;
        private int mSupplementalActionLayoutId;
        private Bitmap mLogo;
        private Bitmap mMainImage;
        private String iconUrl;


        private SetupWizard(Context context) {
            mContext = context;
        }

        public SetupWizard setData(CompanyData companyData) {
            mCompanyData = companyData;
            mTextOverImage = mCompanyData.getName();

            iconUrl = companyData.getLogoUrl();
            myLog.add("****5urlIcono="+iconUrl);
//            mDrawableCardIcon = mCompanyData.getLogoResId();
//            mDrawableCardThumbnail = mCompanyData.getImageResId();
//            mExternalCardThumbnail=new Fondo(mCompanyData.getMainImage());
//            mLogo = mCompanyData.getLogo(   );
//            mMainImage = mCompanyData.getMainImage();


            return this;
        }

        public SetupWizard useDrawableId(@DrawableRes int drawableIdCardThumbnail) {
            mDrawableCardThumbnail = drawableIdCardThumbnail;
            return this;
        }

        public SetupWizard useIconDrawableId(@DrawableRes int drawableIdCardIcon) {
            mDrawableCardIcon = drawableIdCardIcon;
            return this;
        }


        public SetupWizard useDrawableUrl(String drawableUrlCardThumbnail) {
            mUrlCardThumbnail = drawableUrlCardThumbnail;
            return this;
        }

        public SetupWizard useDrawableExternal(DrawableExternal externalCardThumbnail) {
            mExternalCardThumbnail = externalCardThumbnail;
            return this;
        }

        public SetupWizard setTextOverImage(CharSequence textOverImage) {
            mTextOverImage = textOverImage;
            return this;
        }

        public SetupWizard setTextOverImage(int textOverImageResId) {
            mTextOverImageResId = textOverImageResId;
            return this;
        }

        public SetupWizard setTitle(CharSequence title) {
            mTitle = title;
            return this;
        }

        public SetupWizard setSubTitle(CharSequence subTitle) {
            mSubTitle = subTitle;
            return this;
        }

//        public SetupWizard setupSupplementalActions(@LayoutRes int layoutId, ArrayList<BaseSupplementalAction> actions) {
//            mSupplementalActionLayoutId = layoutId;
//            mActions = actions;
//            return this;
//        }

        public CompanyCard build() {
            return build(new CompanyCard(mContext));
        }

        public CompanyCard build(CompanyCard card) {
            if (mExternalCardThumbnail != null) {
                card.setExternalCardThumbnail(mExternalCardThumbnail);
            } else {
                card.setDrawableIdCardThumbnail(mDrawableCardThumbnail);
                card.setUrlCardThumbnail(mUrlCardThumbnail);
            }
            card.setTextOverImage(mTextOverImage);
            card.setTextOverImageResId(mTextOverImageResId);

//            card.setDrawableIconIdCardThumbnail(mDrawableCardIcon);
            myLog.add("****7urlIcono="+iconUrl);
            card.setUrlCardIcon(iconUrl);

//            card.ico(mDrawableCardIcon);

            if (mTitle != null)
                card.setTitle(mTitle.toString());
            card.setSubTitle(mSubTitle);

            //Barra de botones mhp
            mSupplementalActionLayoutId = R.layout.company_button_bar;
            mActions = setActions();

            if (mActions != null) {
                for (BaseSupplementalAction ac : mActions)
                    card.addSupplementalAction(ac);
            }
            card.setLayout_supplemental_actions_id(mSupplementalActionLayoutId);
            card.build();

            ViewToClickToExpand viewToClickToExpand2 = ViewToClickToExpand.builder().enableForExpandAction();
            card.setViewToClickToExpand(viewToClickToExpand2);

            CustomExpandCard expand = new CustomExpandCard(mContext);
            expand.setLemma(mCompanyData.getLemma());
            expand.setBullets(mCompanyData.getExtraInfo());
            expand.setDescription(mCompanyData.getDescription());
            card.addCardExpand(expand);

            return card;
        }

        @NonNull
        private ArrayList<BaseSupplementalAction> setActions() {

            // Set supplemental actions as icon
            //TODO remove buttons if email or phone not provided. take into account combinations
            ArrayList<BaseSupplementalAction> actions = new ArrayList<BaseSupplementalAction>();

            ////MORE button
            TextSupplementalAction moreAction = new TextSupplementalAction(mContext, R.id.text1);
            moreAction.setOnActionClickListener(new BaseSupplementalAction.OnActionClickListener() {
                @Override
                public void onClick(Card card, View view) {
                    card.doToogleExpand();
                }
            });
            actions.add(moreAction);
            ////webpage button
            IconSupplementalAction actionHttp = new IconSupplementalAction(mContext, R.id.ic01);
            actionHttp.setOnActionClickListener(new BaseSupplementalAction.OnActionClickListener() {
                @Override
                public void onClick(Card card, View view) {
                    OpenWebPage(mCompanyData);
                }
            });
            actions.add(actionHttp);
            ////AddContact button
            IconSupplementalAction actionAddContact = new IconSupplementalAction(mContext, R.id.ic0);
            actionAddContact.setOnActionClickListener(new BaseSupplementalAction.OnActionClickListener() {
                @Override
                public void onClick(Card card, View view) {
                    AddContact(mCompanyData);
                }
            });
            actions.add(actionAddContact);
            ////Call button
            IconSupplementalAction actionPhone = new IconSupplementalAction(mContext, R.id.ic1);
            actionPhone.setOnActionClickListener(new BaseSupplementalAction.OnActionClickListener() {
                @Override
                public void onClick(Card card, View view) {
                    StartCall();
                }
            });
            actions.add(actionPhone);
            ////Send email button
            IconSupplementalAction mailAction = new IconSupplementalAction(mContext, R.id.ic2);
            mailAction.setOnActionClickListener(new BaseSupplementalAction.OnActionClickListener() {
                @Override
                public void onClick(Card card, View view) {
                    SendEmail();
                }
            });
            actions.add(mailAction);

            return actions;
        }

        private void OpenWebPage(CompanyData mCompanyData) {
            try {
                String url = mCompanyData.getWebsite();
                if (!url.startsWith("https://") && !url.startsWith("http://")) {
                    url = "http://" + url;
                }
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                mContext.startActivity(browserIntent);
            } catch (Exception e) {
                myLog.add("-err openweb: " + e.getMessage());
            }
        }

        private void StartCall() {
            try {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + mCompanyData.getPhone()));
                mContext.startActivity(intent);
            } catch (Exception e) {
                myLog.add("----errror in call phone: " + e.getMessage());
            }
        }

        private void SendEmail() {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("plain/text");
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{mCompanyData.getEmail()});
            intent.putExtra(Intent.EXTRA_SUBJECT, "");
            intent.putExtra(Intent.EXTRA_TEXT, "");

            mContext.startActivity(Intent.createChooser(intent, ""));
        }

        private void AddContact(CompanyData mCompanyData) {
            try {
                String DisplayName = mCompanyData.getName();
//            String MobileNumber = "123456";
//            String HomeNumber = "1111";
                String WorkNumber = mCompanyData.getPhone();
                String emailID = mCompanyData.getEmail();
//            String company = "bad";
//            String jobTitle = "abcd";

                Bitmap bmImage = BitmapFactory.decodeResource(mContext.getResources(), mCompanyData.getLogoResId());//Todo get the image from other source
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
}
