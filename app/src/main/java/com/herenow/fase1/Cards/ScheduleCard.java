package com.herenow.fase1.Cards;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.CalendarContract;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.herenow.fase1.CardData.Schedule;
import com.herenow.fase1.Cards.Components.CardHeader2;
import com.herenow.fase1.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.prototypes.CardWithList;
import it.gmariotti.cardslib.library.prototypes.LinearListView;

/**
 * Created by Milenko on 19/09/2015.
 */


public class ScheduleCard extends CardWithList {

    private Schedule mScheduleData;

    public ScheduleCard(Context context) {
        super(context);
    }

    public ScheduleCard(Context context, int innerLayout) {
        super(context, innerLayout);
    }

    public void setData(Schedule scheduleData) {
        mScheduleData = scheduleData;
    }

    @Override
    protected CardHeader initCardHeader() {

        //Add Header
        CardHeader2 header = new CardHeader2(getContext(), R.layout.schedule_inner_header);

        header.setTitle(mScheduleData.name); //should use R.string.
        header.setSubTitle(mScheduleData.subtitle);
        header.setDate(mScheduleData.getDateString());
        return header;
    }

    @Override
    protected void initCard() {

        //Set the whole card as swipeable
        setSwipeable(true);

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
        List<CardWithList.ListObject> mObjects = new ArrayList<>();

        //Add an object to the list
        for (Schedule.ScheduleItem it : mScheduleData.getData()) {
            final ScheduleObject so = new ScheduleObject(mParentCard, it);

            //Example onSwipe
            so.setOnItemSwipeListener(new OnItemSwipeListener() {
                @Override
                public void onItemSwipe(ListObject object, boolean dismissRight) {
                    downloadPresentation(so);
                }
            });


            mObjects.add(so);
        }


        return mObjects;
    }

    private void downloadPresentation(ScheduleObject so) {
        Toast.makeText(getContext(), "Downloading presentation\n\"" + so.title + "\" by "
                + so.speaker, Toast.LENGTH_SHORT).show();

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(so.fileUrl));
        // in order for this if to run, you must use the android 3.2 to compile your app
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            request.allowScanningByMediaScanner();
            request.setTitle("Scheduled presentation")
                    .setDescription(so.title)
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        }
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, so.title + ".pdf");//TODO consider other file formats

        // get download service and enqueue file
        DownloadManager manager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
    }


    @Override
    public View setupChildView(int childPosition, CardWithList.ListObject object, View convertView, ViewGroup parent) {

        //Setup the ui elements inside the item
        TextView title = (TextView) convertView.findViewById(R.id.destination);
        TextView time = (TextView) convertView.findViewById(R.id.time);
        TextView place = (TextView) convertView.findViewById(R.id.estimated);
        TextView speaker = (TextView) convertView.findViewById(R.id.company_and_flight);
        ImageView file = (ImageView) convertView.findViewById(R.id.image_ppt);


        //Retrieve the values from the object
        final ScheduleObject so = (ScheduleObject) object;
        title.setText(so.title);
        time.setText(so.getTime());
        place.setText(so.location);
        speaker.setText(so.speaker);

        if (so.fileUrl != null && !so.fileUrl.equals("")) {
            file.setClickable(true);
            file.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    downloadPresentation(so);
                }
            });
        } else {
            file.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

    @Override
    public int getChildLayoutId() {
//        return R.layout.schedule_card_inner_main;
        return R.layout.schedule_card_inner_main_withfile;
    }


    // -------------------------------------------------------------
    // event Object
    // -------------------------------------------------------------

    class ScheduleObject extends CardWithList.DefaultListObject {
        public String url;
        public String fileUrl;
        String title, hour, urlImage, location;
        int h, min;
        private String speaker, description = "";
        private long startInMilli, endInMilli;


        public ScheduleObject(Card parentCard) {
            super(parentCard);
            init();
        }

        public ScheduleObject(Card parentcard, Schedule.ScheduleItem it) {
            super(parentcard);
            title = it.getTitle();
            hour = it.getHour();
            min = it.getMin();
            h = it.getH();
            location = it.getPlace();
            speaker = it.getSpeaker();
            description = it.getDescription();
            url = it.getUrl();
            startInMilli = it.getStartInMilli();
            endInMilli = it.getEndInMilli();
            fileUrl = it.getUrlFile();
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
                    Add2Agenda((ScheduleObject) object);
                    //TODO ver si poner un botón para una url
                    //TODO ver si se puede descargar algún archivo poner botón
                    //TODO resaltar el punto de la agenda que está ocurriendo
                    //TODO, que la descripción se pueda ver,yasea expandiednog o un popup
                }

                private void Add2Agenda(ScheduleObject scItem) {
                    Calendar cal = Calendar.getInstance();
                    Intent intent = new Intent(Intent.ACTION_EDIT);
                    intent.setType("vnd.android.cursor.item/event");

                    intent.putExtra(CalendarContract.Events.TITLE, scItem.title);
                    intent.putExtra(CalendarContract.Events.ALL_DAY, false);
                    intent.putExtra(CalendarContract.Events.DESCRIPTION, "Presentation by " + scItem.speaker
                            + ".\n" + scItem.description);
                    intent.putExtra(CalendarContract.Events.EVENT_LOCATION, scItem.location); //TODO ver como poner un reaminder
//                    intent.putExtra(CalendarContract.Reminders., scItem.location);
//                    intent.putExtra(CalendarContract.Events.HAS_ALARM, 4);//Alert

                    intent.putExtra(CalendarContract.Events.DTSTART, scItem.startInMilli);//Complete
                    intent.putExtra(CalendarContract.Events.DTEND, scItem.endInMilli);//Complete

//                    intent.putExtra("beginTime", cal.getTimeInMillis() + 60 * 60 * 1000);
//                    intent.putExtra("allDay", false);
//                    intent.putExtra("rrule", "FREQ=YEARLY");
//                    intent.putExtra("endTime", cal.getTimeInMillis() + 2 * 60 * 60 * 1000);
                    mContext.startActivity(intent);

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

        public String getTime() {
            return Integer.toString(h) + ":" + String.format("%02d", min);
//            return Integer.toString(h) + ":" +  Integer.toString(min); //Todo agregar cero a la iz. lo de a
        }


    }


}