package parse;

import android.location.Location;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import util.AppendLog;
import util.parameters;

/**
 * Created by Milenko on 27/07/2015.
 */
@ParseClassName("SAPO_hits_users")
public class HitSapo extends ParseObject {
    String bssid, ssid;
    ParseGeoPoint location;

    public void setBssid(String bssid) {
        put("bssid", bssid);
        put("user", ParseUser.getCurrentUser());
    }

    public void setSsid(String ssid) {
        put("ssid", ssid);
    }

    public void setnhits(int nhits) {
        put("nhits", nhits);
    }

    @Override
    public String toString() {
        String st;
        try {
            st = "HitSapo{" +
                    " objId=" + getObjectId() +
                    ", b='" + getBSSID() + '\'' +
                    ", s='" + getSSID() + '\'' +
                    ", l=" + getLocation() +
                    '}';
        } catch (Exception e) {
            AppendLog.appendLog("---ERROR. could get hit.toString. Probably is empty object:" + e.getMessage(),
                    "SAPO2");
            st = "Empty hit with objId=" + this.getObjectId();
        }

        return st;
    }

    //        put("displayName", value);
//    }
    public String getObId() {
        return super.getObjectId();
    }

    public void pinMe() {

        this.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                AppendLog.appendLog("guardado el hit " + this, "SAPOA");
            }
        });//TOdo hacer pin
    }

    public void incrementMe() {
        AppendLog.appendLog("incrementado el hit " + this, "SAPOA"); //Not possible since is empty;
        this.increment("nhits");
        this.pinInBackground(parameters.pinSapo, new SaveCallback() {
            @Override
            public void done(ParseException e) {
                AppendLog.appendLog("pin con el incremento ", "SAPOA");
            }
        });
//        this.saveInBackground(new SaveCallback() {
//            @Override
//            public void done(ParseException e) {
//                AppendLog.appendLog("guardado con el incremento", "SAPO2");
//            }
//        });

    }

    public String getBSSID() {
        return getString("bssid");
    }

    public String getSSID() {
        return getString("ssid");
    }

    public ParseGeoPoint getLocation() {
        return getParseGeoPoint("GPS");
    }

    public void setLocation(Location loc) {
        put("GPS", new ParseGeoPoint(loc.getLatitude(), loc.getLongitude()));
        put("GPS_error", loc.getAccuracy());
    }
}
