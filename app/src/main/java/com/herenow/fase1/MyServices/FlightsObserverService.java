package com.herenow.fase1.MyServices;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.herenow.fase1.CardData.GoogleFlight;
import com.herenow.fase1.Cards.AirportCard;
import com.herenow.fase1.GetFlightInfo;
import com.herenow.fase1.R;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import util.OnTaskCompleted;
import util.myLog;
import util.parameters;

import static com.herenow.fase1.R.mipmap.ic_launcher;

/**
 * Created by Milenko on 04/10/2015.
 */
public class FlightsObserverService extends Service {
    public static final long NOTIFY_INTERVAL = 10 * 1000; // 10 seconds
    int iCounter = 0;
    private Handler mHandler = new Handler();
    private String tag = "vig";
    private GoogleFlight mOldGoogle;
    private String mCode, mRemoteCity;
    private Timer mTimer = null;
    private GoogleFlight mCurrentGoogle;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        // cancel if already existed
        myLog.add("on create the service", tag);
        try {
            if (mTimer != null) {
                mTimer.cancel();
            } else {
                // recreate new
                mTimer = new Timer();
            }
            // schedule task.
            mTimer.schedule(new TimeDisplayTimerTask(), 2000, parameters.timeBetweenFlightQueries);
        } catch (Exception e) {
            myLog.add("on create" + e.getLocalizedMessage(), tag);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        myLog.add("Starting the service", tag);
        try {
            mRemoteCity = intent.getStringExtra("remoteCity");
            mCode = intent.getStringExtra("code");
            boolean bMainTh = (Looper.getMainLooper().getThread() == Thread.currentThread());
            myLog.add("[startcomm] Runnin in UI thread=" + bMainTh,tag);
        } catch (Exception e) {
            myLog.add("errr en handler: " + e.getLocalizedMessage(), tag);
        }

        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mTimer.cancel();
        myLog.add("destruyendo the service", tag);
    }

    private void stopTheService() {
        myLog.add("stoping ht service in a methjod", tag);
        this.stopSelf();
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

    class TimeDisplayTimerTask extends TimerTask implements OnTaskCompleted {
        final OnTaskCompleted th = (OnTaskCompleted) this;

        @Override
        public void run() {
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    GetFlightInfo geto = new GetFlightInfo(mCode,mRemoteCity , th);
//                    (new readGoogleFlight(th)).execute(new String[]{mCode});
                }
            });
        }

        @Override
        public void OnTaskCompleted(ArrayList elements) {
            String notiTitle;
            String content;

            mOldGoogle = mCurrentGoogle;
            mCurrentGoogle = (GoogleFlight) elements.get(0);

            myLog.add(iCounter + " ...info de timer: " + mCurrentGoogle.toString(), tag);

            iCounter++;

            if (iCounter == 60) {
                myLog.add("apagamos service porque no cambia, por seguridad", tag);
                FlightsObserverService.this.stopTheService();
            }

            if (mCurrentGoogle.hasChanged(mOldGoogle)) {
                myLog.add("***has changed: " + mCurrentGoogle.changesText, tag);
                if (mCurrentGoogle.hasDepartedOrLanded()) {//todo implement
                    myLog.add("stopping the service", tag);
                    FlightsObserverService.this.stopTheService();

                } else {
                    notiTitle = mCurrentGoogle.changeSummarized;
                    content = mCurrentGoogle.getSummary(AirportCard.TypeOfCard.departure);
                    myLog.add("******NOTIFICATION: " + notiTitle, tag);
                    myLog.add("***NOTIFICATION: " + content, tag);

                    //Notification
                    sendNotificationFlight(notiTitle, content);
                    //todo remove
                    FlightsObserverService.this.stopTheService();
                }
            } else {
//                myLog.add("Hasn't changed ", tag);
            }
        }
    }

    class readGoogleFlight extends AsyncTask<String, Void, GoogleFlight> {
        private OnTaskCompleted listener;

        readGoogleFlight(OnTaskCompleted listener) {
            myLog.add("asignando listener en parseGoogleflight", tag);
            this.listener = listener;
        }

        @Override
        protected GoogleFlight doInBackground(String... strings) {
            GoogleFlight googleFlight = null;

            try {
                Connection.Response response = Jsoup.connect("https://www.google.es/search?q=" + strings[0])
                        .ignoreContentType(true)
//                        .userAgent("Mozilla/5.0 (Windows NT 6.3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.85 Safari/537.36")
                        .referrer("http://www.google.com")
                        .timeout(12000)
                        .followRedirects(true)
                        .execute();

                Document doc = response.parse();
                Element googleFlightCard = doc.select("div[class=card-section vk_c]").first();
                Elements datos = googleFlightCard.select("tr[class=_FJg").first().children();
                googleFlight = new GoogleFlight(datos);
                googleFlight.setRemoteCity(mRemoteCity);
                googleFlight.setCode(mCode);

                googleFlight.code = strings[0];
//                Toast.makeText(mContext, googleFlight.toString(), Toast.LENGTH_SHORT).show();
            } catch (Throwable t) {
                myLog.add("-eeeoo query a google flifht" + t.getMessage(), tag);
            }
            return googleFlight;
        }

        @Override
        protected void onPostExecute(GoogleFlight googleFlight) {

            try {
                super.onPostExecute(googleFlight);
                ArrayList buff = new ArrayList<>();
                buff.add(googleFlight);
                listener.OnTaskCompleted(buff);
            } catch (Exception e) {
                myLog.add("fallo en post execute del google wuery: " + e.getMessage(), tag);
            }

        }

    }
}
