package com.herenow.fase1.Wifi;

import android.net.wifi.ScanResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by halatm on 08/12/2015.
 */
public interface preguntaWifi {
    void OnReceiveWifis(List<ScanResult> sr);

    void noWifiDetected();
}
