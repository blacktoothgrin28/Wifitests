package com.herenow.fase1.Activities;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.herenow.fase1.R;
import com.herenow.fase1.Wifi.LocationAsker;
import com.herenow.fase1.Wifi.WifiAsker;
import com.herenow.fase1.Wifi.preguntaWifi;
import com.parse.FindCallback;
import com.parse.ParseException;

import java.util.HashMap;
import java.util.List;

import parse.ParseActions;
import parse.WeaconParse;
import util.GPSCoordinates;
import util.myLog;

import static parse.ParseActions.getParadasFree;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    HashMap<String, String> hashMarkers;
    private GoogleMap mMap;
    private GPSCoordinates mGps;
    private String paradaId;
    private Context mContext;
    private Marker selectedMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hashMarkers = new HashMap<>();
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mContext = this;

        //click on marker event
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                selectedMarker = marker;
                paradaId = hashMarkers.get(marker.getId());

                return false;
            }
        });

        //Own position
        (new LocationAsker()).DoSomethingWithPosition(new LocationCallback() {

            @Override
            public void LocationReceived(GPSCoordinates gps) {
                mGps = gps;
                LatLng aqui;
                aqui = gps.getLatLng();
                mMap.addMarker(new MarkerOptions().position(aqui).title("Yo")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(aqui, 15));

                getParadasFree(new FindCallback<WeaconParse>() {
                    @Override
                    public void done(List<WeaconParse> list, ParseException e) {
                        myLog.add("puntos a pintar: " + list.size());
                        for (WeaconParse we : list) {
                            Marker marker = mMap.addMarker(new MarkerOptions().position(we.getGPSLatLng()).title(we.getName()));
                            hashMarkers.put(marker.getId(), we.getParadaId());
                            myLog.add("hemos puesto el marker(" + we.getName() + ") idmarker=" +
                                    marker.getId() + "| parada=" + we.getParadaId());
                        }
                    }
                }, gps.getGeoPoint());

            }
        }, this);


    }


    public void onClickSendWifis(View view) {
        new WifiAsker(mContext, new preguntaWifi() {
            @Override
            public void OnReceiveWifis(List<ScanResult> sr) {
                Toast.makeText(mContext, "Recibidos " + sr.size() + "wifis", Toast.LENGTH_SHORT).show();
                myLog.add("Recibidos los wifis forzados para parada");

                try {
                    ParseActions.assignSpotsToWeacon(paradaId, sr, mGps);
                    selectedMarker.setVisible(false);
                } catch (Exception e) {
                    myLog.add("error in assign spot to weacon:" + e.getLocalizedMessage());
                }
            }

            @Override
            public void noWifiDetected() {
                myLog.add("error recibiendo los sopotsde manera forzasa");
            }
        });
    }
}