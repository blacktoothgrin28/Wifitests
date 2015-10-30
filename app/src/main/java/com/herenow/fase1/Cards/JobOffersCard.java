package com.herenow.fase1.Cards;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.herenow.fase1.CardData.JobData;
import com.herenow.fase1.Cards.Components.CardHeader2;
import com.herenow.fase1.R;
import com.herenow.fase1.actions.Actions;

import java.util.ArrayList;
import java.util.List;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.prototypes.CardWithList;
import it.gmariotti.cardslib.library.prototypes.LinearListView;
import util.myLog;


/**
 * Created by Milenko on 19/09/2015.
 */


public class JobOffersCard extends CardWithList {

    private JobData mJobData;
    private int PICKFILE_RESULT_CODE = 1;

    public JobOffersCard(Context context) {
        super(context);
    }

    public JobOffersCard(Context context, int innerLayout) {
        super(context, innerLayout);
    }

    public void setData(JobData jobData) {
        mJobData = jobData;
    }

    @Override
    protected CardHeader initCardHeader() {

        //Add Header
        CardHeader2 header = new CardHeader2(getContext(), R.layout.schedule_inner_header);

        header.setTitle(mJobData.name); //should use R.string.
        header.setSubTitle("");
        header.setDate("At: " + mJobData.getDateString());
        return header;
    }

    @Override
    protected void initCard() {

        //Set the whole card as swipeable
        setSwipeable(false);

        setOnSwipeListener(new OnSwipeListener() {
            @Override
            public void onSwipe(Card card) {

                Toast.makeText(getContext(), "Swipe on " + card.getCardHeader().getTitle(), Toast.LENGTH_SHORT).show();
            }
        });

    }


    @Override
    protected List<ListObject> initChildren() {

        //Init the list
        List<ListObject> mObjects = new ArrayList<>();

        //Add an object to the list
        for (final JobData.JobOffer ji : mJobData.getItems()) {
            final jobOfferObject so = new jobOfferObject(mParentCard, ji);


            //Example onSwipe
            so.setOnItemSwipeListener(new OnItemSwipeListener() {
                @Override
                public void onItemSwipe(ListObject object, boolean dismissRight) {
                    Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
                    whatsappIntent.setType("text/plain");
                    whatsappIntent.setPackage("com.whatsapp");
                    whatsappIntent.putExtra(Intent.EXTRA_TEXT, "You could be interested in this job at " + mJobData.Company + ": " +
                            ji.title + " " + ji.getUrl());

                    try {
                        mContext.startActivity(whatsappIntent);
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(mContext, "Whatsapp have not been installed.", Toast.LENGTH_SHORT).show();
                    }
                }
            });


            mObjects.add(so);
        }


        return mObjects;
    }

    private void uploadCV(jobOfferObject so) {
        Intent fileintent = new Intent(Intent.ACTION_GET_CONTENT);
        fileintent.setType("gagt/sdf");
        try {
            ((Activity) mContext).startActivityForResult(fileintent, PICKFILE_RESULT_CODE);
        } catch (ActivityNotFoundException e) {
            myLog.add("No activity can handle picking a file. Showing alternatives.");
        }

    }


    @Override
    public View setupChildView(int childPosition, final ListObject object, View convertView, ViewGroup parent) {

        //Setup the ui elements inside the item
        TextView title = (TextView) convertView.findViewById(R.id.title);
        TextView description = (TextView) convertView.findViewById(R.id.description);
        TextView languages = (TextView) convertView.findViewById(R.id.languages);
        TextView skills = (TextView) convertView.findViewById(R.id.skills);
        ImageView upload = (ImageView) convertView.findViewById(R.id.image_upload);

        //Retrieve the values from the object
        JobData.JobOffer ji = ((jobOfferObject) object).getJobOffer();
        title.setText(ji.title);
        description.setText(ji.description);
        languages.setText(ji.getLanguages());
        skills.setText(ji.getSkills());

        upload.setClickable(true);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadCV((jobOfferObject) object);
            }
        });

        return convertView;
    }

    @Override
    public int getChildLayoutId() {
//        return R.layout.schedule_card_inner_main;
        return R.layout.job_card_inner_main_withfile;
    }


    // -------------------------------------------------------------
    // JobObject
    // -------------------------------------------------------------


    class jobOfferObject extends DefaultListObject {
        JobData.JobOffer mji;

        public jobOfferObject(Card parentCard) {
            super(parentCard);
            init();
        }

        public jobOfferObject(Card mParentCard, JobData.JobOffer ji) {
            super(mParentCard);
            mji = ji;
            init();
        }

        private void init() {
            //OnClick Listener
            setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(LinearListView parent, View view, int position, ListObject object) {
//                    Toast.makeText(getContext(), "Click on " + getObjectId(), Toast.LENGTH_SHORT).show();

//                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getObjectId()));
//                    mContext.startActivity(browserIntent);
                    OpenJobDescription(((jobOfferObject) object).getUrl());
                }


            });

            //OnItemSwipeListener
            setOnItemSwipeListener(new OnItemSwipeListener() {
                @Override
                public void onItemSwipe(ListObject object, boolean dismissRight) {
                    Toast.makeText(getContext(), "Swipe on " + object.getObjectId(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void OpenJobDescription(String url) {
            Actions.OpenWebPage(mContext, url);
        }


        public String getUrl() {
            return mji.getUrl();
        }

        public JobData.JobOffer getJobOffer() {
            return mji;
        }
    }
}