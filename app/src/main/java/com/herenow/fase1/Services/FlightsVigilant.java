package com.herenow.fase1.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.herenow.fase1.CardData.GoogleFlight;
import com.herenow.fase1.Cards.AirportCard;
import com.herenow.fase1.R;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import util.myLog;
import util.parameters;

import static com.herenow.fase1.R.mipmap.ic_launcher;

/**
 * Created by Milenko on 02/10/2015.
 */
public class FlightsVigilant extends IntentService {

    private GoogleFlight mCurrentGoogle;
    private GoogleFlight mOldGoogle;
    private String mCode, mRemoteCity;
    private Timer t;
    private String tag = "vig";
    int iCounter = 0;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public FlightsVigilant(String name) {
        super(name);
    }

    public FlightsVigilant() {
        super("vigilant");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        myLog.add("Startingthe service", tag);
        try {
            mRemoteCity = intent.getStringExtra("remoteCity");
            mCode = intent.getStringExtra("code");


            t = new Timer();
            t.schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        GetInfoFlightFromGoogleSync(mCode); //repeated
                    } catch (Exception e) {
                        myLog.add("eeror en run:" + e.getLocalizedMessage(), tag);
                    }
                }
            }, 10000, parameters.timeBetweenFlightQueries);
        } catch (Exception e) {
            myLog.add("errr en handler: " + e.getLocalizedMessage(), tag);
        }

    }

    @Override
    public void onDestroy() {
        myLog.add("saliendo, destriccion: ", tag);

        super.onDestroy();
    }

    private void GetInfoFlightFromGoogleSync(String code) {

        try {
            myLog.add("getting in google sync (timer)", tag);
            Connection.Response response = Jsoup.connect("https://www.google.es/search?q=" + code)
                    .ignoreContentType(true)
                    .referrer("http://www.google.com")
                    .timeout(5000)
                    .followRedirects(true)
                    .execute();

            Document doc = response.parse();
            Element googleFlightCard = doc.select("div[class=card-section vk_c]").first();
            Elements datos = googleFlightCard.select("tr[class=_FJg").first().children();
            mOldGoogle = mCurrentGoogle;
            mCurrentGoogle = new GoogleFlight(datos);
            mCurrentGoogle.setCode(mCode);
            mCurrentGoogle.setRemoteCity(mRemoteCity);

            mCurrentGoogle.code = code;

            myLog.add(iCounter + " ...info de timer: " + mCurrentGoogle.toString(), tag);
            iCounter++;
            if (iCounter == 4) {
                myLog.add("apagamos esto", tag);
                this.stopSelf();
            }

            String notiTitle;
            String content;
            if (mCurrentGoogle.hasChanged(mOldGoogle)) {
                myLog.add("has changed: " + mCurrentGoogle.changesText, tag);
                if (mCurrentGoogle.hasDepartedOrLanded()) {//todo implement
                    myLog.add("stopping the service", tag);
                    t.cancel();
                    this.stopSelf();
                } else {
                    notiTitle = mCurrentGoogle.changeSummarized;
                    content = mCurrentGoogle.getSummary(AirportCard.TypeOfCard.departure);
                    myLog.add("******NOTIFICATION: " + notiTitle);
                    myLog.add("***NOTIFICATION: " + content);

                    //Notification
                    sendNotificationFlight(notiTitle, content);
                }
            } else {
                myLog.add("Hasn't changed ");
            }


        } catch (IOException e) {
            myLog.add("---error en getinfo form google:" + e.getMessage(), tag);
        }

    }

    private void sendNotificationFlight(String notiTitle, String content) {
        final NotificationManager mNotificationManager;
        NotificationCompat.Builder notif;
        Context mContext = getApplicationContext();//TODO puede que no funcione con esto...

        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        Bitmap bmImage = BitmapFactory.decodeResource(mContext.getResources(), ic_launcher);//Todo get the image from other source
        notif = new NotificationCompat.Builder(mContext)
                .setSmallIcon(R.drawable.ic_stat_name_hn)
                .setLargeIcon(bmImage)
                .setContentTitle(notiTitle)
                .setContentText(content)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND | Notification.FLAG_SHOW_LIGHTS)
                .setLights(0xE6D820, 300, 100)
                .setTicker("Change in Flight");
//                .setDeleteIntent(pendingDeleteIntent)
//                .addAction(myAction);
        //todo improve the notif
        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.setBigContentTitle("PP" + notiTitle);
        bigTextStyle.bigText("PP" + content);

        notif.setStyle(bigTextStyle);
//        notif.setContentIntent(resultPendingIntent);

        mNotificationManager.notify(1, notif.build());

    }

}
