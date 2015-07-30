package com.herenow.fase1;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

import parse.Hit;

/**
 * Created by Milenko on 17/07/2015.
 */
public class Weacons extends Application {
    /**
     * Called when the application is starting, before any activity, service,
     * or receiver objects (excluding content providers) have been created.
     * Implementations should be as quick as possible (for example using
     * lazy initialization of state) since the time spent in this function
     * directly impacts the performance of starting the first activity,
     * service, or receiver in a process.
     * If you override this method, be sure to call super.onCreate().
     */
    @Override
    public void onCreate() {
        super.onCreate();
        Parse.enableLocalDatastore(this);
        ParseObject.registerSubclass(Hit.class);
        Parse.initialize(this, "CADa4nX2Lx29QEJlC3LUY1snbjq9zySlF5S3YSVG", "hC9VWCmGEBxb9fSGQPiOjSInaAPnYMZ0t8k3V0UO");
    }
}
