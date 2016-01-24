package com.herenow.fase1.Activities;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.herenow.fase1.R;
import com.herenow.fase1.Wifi.LocationAsker;
import com.parse.FindCallback;
import com.parse.ParseException;

import java.util.List;

import parse.WeaconParse;
import util.GPSCoordinates;

import static parse.ParseActions.getParadas;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        getParadas(new FindCallback<WeaconParse>() {
            @Override
            public void done(List<WeaconParse> list, ParseException e) {
                for (WeaconParse we : list) {
                    mMap.addMarker(new MarkerOptions().position(we.getGPSLatLng()).title(we.getName()));

                }
            }
        });
        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        (new LocationAsker()).DoSomethingWithPosition(new LocationCallback() {

            @Override
            public void LocationReceived(GPSCoordinates gps) {
                LatLng aqui = gps.getLatLng();
                mMap.addMarker(new MarkerOptions().position(aqui).title("Yo"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(aqui));
            }
        }, this);

    }

    private void getParadasTodo() {

    }

    private void getParadasDone() {

    }

}
