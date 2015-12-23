package com.herenow.fase1.Activities;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by Milenko on 22/12/2015.
 */
public class RefreshBusNotification extends Service {
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


//        Notifications.sendNotification(we);
        this.stopSelf();
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
