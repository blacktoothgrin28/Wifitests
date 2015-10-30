package com.herenow.fase1.Activities;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.herenow.fase1.actions.Actions;

import util.myLog;

/**
 * Created by Milenko on 30/10/2015.
 */
public class ConnectToWifi extends Service{
    @Override
    public  void onCreate() {
        super.onCreate();
        myLog.add("estamos en activitiy connect to wifi");
        Actions.ConnectToWifi("piripiri", "spideyhg3711", this);
        this.stopSelf();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        myLog.add("Apagando serviccio de connecion de wiji");
    }
}
