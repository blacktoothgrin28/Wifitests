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

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
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

import java.util.ArrayList;

import it.gmariotti.cardslib.library.cards.actions.BaseSupplementalAction;
import it.gmariotti.cardslib.library.cards.actions.IconSupplementalAction;
import it.gmariotti.cardslib.library.cards.actions.TextSupplementalAction;
import it.gmariotti.cardslib.library.cards.base.BaseMaterialCard;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.ViewToClickToExpand;
import util.myLog;

public class RestaurantCard extends BaseMaterialCard {
    private static int tableNumber = -1;
//TODO use companycard with other buttons
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

    public RestaurantCard(Context context) {
        this(context, it.gmariotti.cardslib.library.cards.R.layout.native_material_largeimage_inner_base_main);
    }

    // -------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------

    public RestaurantCard(Context context, @LayoutRes int innerLayout) {
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
     * Then calls {@link #setupInnerViewElements(ViewGroup, View)} method
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

        public RestaurantCard build() {
            return build(new RestaurantCard(mContext));
        }

        public RestaurantCard build(RestaurantCard card) {
            if (mExternalCardThumbnail != null) {
                card.setExternalCardThumbnail(mExternalCardThumbnail);
            } else {
                card.setDrawableIdCardThumbnail(mDrawableCardThumbnail);
                card.setUrlCardThumbnail(mUrlCardThumbnail);
            }
            card.setTextOverImage(mTextOverImage);
            card.setTextOverImageResId(mTextOverImageResId);

//            card.setDrawableIconIdCardThumbnail(mDrawableCardIcon);
            card.setUrlCardIcon(iconUrl);

//            card.ico(mDrawableCardIcon);

            if (mTitle != null)
                card.setTitle(mTitle.toString());
            card.setSubTitle(mSubTitle);

            //Barra de botones mhp
            mSupplementalActionLayoutId = R.layout.restaurant_button_bar;
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
            TextSupplementalAction moreAction = new TextSupplementalAction(mContext, R.id.bt_more);
            moreAction.setOnActionClickListener(new BaseSupplementalAction.OnActionClickListener() {
                @Override
                public void onClick(Card card, View view) {
                    card.doToogleExpand();
                }
            });
            actions.add(moreAction);

            ////webpage button
            IconSupplementalAction actionHttp = new IconSupplementalAction(mContext, R.id.bt_web);
            actionHttp.setOnActionClickListener(new BaseSupplementalAction.OnActionClickListener() {
                @Override
                public void onClick(Card card, View view) {
                    OpenWebPage(mCompanyData);
                }
            });
            actions.add(actionHttp);


            //Call waitress
            IconSupplementalAction actionCallWaitress = new IconSupplementalAction(mContext, R.id.bt_waitress);
            actionCallWaitress.setOnActionClickListener(new BaseSupplementalAction.OnActionClickListener() {
                @Override
                public void onClick(Card card, View view) {
                    callWaitress();
                }
            });
            actions.add(actionCallWaitress);

            //CHECK PLEASE
            IconSupplementalAction actionCheckPlease = new IconSupplementalAction(mContext, R.id.bt_check);
            actionCheckPlease.setOnActionClickListener(new BaseSupplementalAction.OnActionClickListener() {
                @Override
                public void onClick(Card card, View view) {
                    checkPlease();
                }
            });
            actions.add(actionCheckPlease);

            //PAY
            IconSupplementalAction actionPay = new IconSupplementalAction(mContext, R.id.bt_pay);
            actionPay.setOnActionClickListener(new BaseSupplementalAction.OnActionClickListener() {
                @Override
                public void onClick(Card card, View view) {
                    killBill();
                }
            });
            actions.add(actionPay);

            return actions;
        }

        private void killBill() {
            if (tableNumber != -1) {
                tableNumber = askTableNumber();
            }
            Toast.makeText(mContext, "Paying with paypal \nfor table " + tableNumber, Toast.LENGTH_SHORT).show();
        }

        private int askTableNumber() {
//            Intent i = new Intent(this, TableNumberActivity.class);
//            startActivityForResult(mContext,i,1);
//            startActivityForResult(i, 1);

            return 21;
        }

        private void checkPlease() {
            if (tableNumber != -1) {
                tableNumber = askTableNumber();
            }
            Toast.makeText(mContext, "Bringing the check \nfor table " + tableNumber, Toast.LENGTH_SHORT).show();
        }

        private void callWaitress() {
            if (tableNumber != -1) {
                tableNumber = askTableNumber();
            }
            Toast.makeText(mContext, "Calling waitress \nfor table " + tableNumber, Toast.LENGTH_SHORT).show();
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


    }
}
