package com.herenow.fase1.Activities;

import util.GPSCoordinates;

/**
 * Created by Milenko on 25/09/2015.
 */
public interface LocationCallback {
    public void LocationReceived(GPSCoordinates gps);
}
