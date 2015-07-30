package com.herenow.fase1;

import com.parse.ParseObject;

import util.GPSCoordinates;

/**
 * Created by Milenko on 17/07/2015.
 */
public class SPOT { //TODO replace for a class that extends from  ParseObject
    private final String ssid;
    private final String bssid;
    private final GPSCoordinates gps;
    private final int level;
    private final ParseObject owner;
    private final boolean validated;
    private final String placeId;

    public SPOT(ParseObject obj) {
        ssid = obj.getString("ssid");
        bssid = obj.getString("bssid");
        gps = new GPSCoordinates(obj.getParseGeoPoint("GPS").getLatitude(), obj.getParseGeoPoint("GPS").getLongitude());
        level = obj.getInt("Level");
        owner = obj.getParseObject("owner");
        validated = obj.getBoolean("validated");
        placeId = (obj.getParseObject("associated_place")).getObjectId();

    }

    public String getPlaceId() {
        return placeId;
    }

    public String getSsid() {
        return ssid;
    }

    public String getBssid() {
        return bssid;
    }

    public GPSCoordinates getGps() {
        return gps;

    }

    /**
     * A shorter description of coordinates
     *
     * @return
     */
    public String gpsString() {
        return "(" + gps.getLatitude() + "," + gps.getLongitude() + ")";
    }

    public int getLevel() {
        return level;
    }

    public ParseObject getOwner() {
        return owner;
    }

    public boolean isValidated() {
        return validated;
    }


}
