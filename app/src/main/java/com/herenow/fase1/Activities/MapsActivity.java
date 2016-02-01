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
import java.util.HashSet;
import java.util.List;

import parse.ParseActions;
import parse.WeaconParse;
import util.GPSCoordinates;
import util.myLog;
import util.stringUtils;

import static parse.ParseActions.getParadasFree;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    HashMap<String, String> hashMarkers;
    private GoogleMap mMap;
    private GPSCoordinates mGps;
    private String paradaId;
    private Context mContext;
    private Marker selectedMarker;

    private HashSet<Marker> hashMarkersPlaces = new HashSet();

    private boolean placesVisible;
    private Marker yo;

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

    @Override
    protected void onResume() {
        super.onResume();
        //Own position
        myLog.add("onresume", "aut");
        LocationCallback call = new LocationCallback() {

            @Override
            public void LocationReceived(GPSCoordinates gps) {
                UpdateMyPosition(gps);
            }

            @Override
            public void LocationReceived(GPSCoordinates gps, double accuracy) {
                myLog.add("recibida con procision, pero no lo requerimamos" + accuracy, "aut");
            }
        };
        new LocationAsker(mContext, call);

    }

    public void UpdateMyPosition(GPSCoordinates gps) {
        mGps = gps;
        LatLng aqui = gps.getLatLng();

        yo.setPosition(aqui);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(aqui, 15));
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
        LocationCallback call = new LocationCallback() {

            @Override
            public void LocationReceived(GPSCoordinates gps) {
                mGps = gps;
                LatLng aqui = gps.getLatLng();
                LoadPlaces();
                yo = mMap.addMarker(new MarkerOptions().position(aqui).title("Yo")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(aqui, 15));

                getParadasFree(new FindCallback<WeaconParse>() {
                    @Override
                    public void done(List<WeaconParse> list, ParseException e) {
                        myLog.add("puntos a pintar: " + list.size());
                        for (WeaconParse we : list) {
                            Marker marker = mMap.addMarker(new MarkerOptions().position(we.getGPSLatLng()).title(we.getName()));
                            hashMarkers.put(marker.getId(), we.getParadaId());
//                            myLog.add("hemos puesto el marker(" + we.getName() + ") idmarker=" +
//                                    marker.getId() + "| parada=" + we.getParadaId());
                        }
                    }
                }, gps.getGeoPoint());

            }

            @Override
            public void LocationReceived(GPSCoordinates gps, double accuracy) {
                myLog.add("recibida con procision, pero no lo requerimamos" + accuracy, "aut");
            }
        };
        new LocationAsker(mContext, call);
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

    public void onClickShowWeacons(View view) {
        for (Marker ma : hashMarkersPlaces) {
            ma.setVisible(!placesVisible);
        }
        placesVisible = !placesVisible;
    }

    private void LoadPlaces() {
        ParseActions.getPlacesAround(mGps, 2, new FindCallback<WeaconParse>() {
            @Override
            public void done(List<WeaconParse> list, ParseException e) {
                if (list != null) {

                    if (list.size() == 0) {
                        String s = "no hemos cargadowacons en el mmap";
                        myLog.add(s, "aut");
                        Toast.makeText(mContext, s, Toast.LENGTH_SHORT).show();
                    }
                    for (WeaconParse we : list) {
                        placesVisible = false;
                        Marker marker = mMap.addMarker(new MarkerOptions().position(we.getGPSLatLng()).title(we.getName())
                                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_map_weacon)));
                        marker.setVisible(placesVisible);
                        hashMarkersPlaces.add(marker);
//                        myLog.add("hemos puesto el marker(" + we.getName() + ") idmarker=" +
//                                marker.getId() + "| parada=" + we.getParadaId());
                    }

                } else {
                    String s = "La listade weacios es null";
                    myLog.add(s, "aut");
                    Toast.makeText(mContext, s, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * the user declares he is near a bus stop. THis verifieies if its true and then capture the wifis
     *
     * @param view
     */
    public void onClickImIn(View view) {
        final FindCallback<WeaconParse> oneParadaCallback = new FindCallback<WeaconParse>() {
            @Override
            public void done(List<WeaconParse> list, ParseException e) {
                if (e == null) {
                    if (list.size() > 0) {
                        String s = "hemos detectado " + list.size() + " paradas en este radio  " +
                                stringUtils.Listar(list);
                        myLog.add(s, "aut");
                        Toast.makeText(mContext, s, Toast.LENGTH_SHORT).show();
                        //nos quedamos con el primero que suponemso que es el mas cercvano
                        paradaId = list.get(0).getParadaId();
                        onClickSendWifis(null);
                        ParseActions.getSpots(false, 4, mGps, mContext);
                    } else {
                        String s = "no hemos detectado ninguna parada";
                        myLog.add(s, "aut");
                        Toast.makeText(mContext, s, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    myLog.add("error en listener oneparadacallbadk" + e.getLocalizedMessage(), "aut");
                }

            }
        };

        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void LocationReceived(GPSCoordinates gps) {
                myLog.add("tenemos localización   pero sin accuracy" + gps, "aut");

            }

            @Override
            public void LocationReceived(GPSCoordinates gps, double accuracy) {
                myLog.add("en maps, ya tenemos la loclizacion con precicion:" + accuracy, "aut");
                UpdateMyPosition(gps);
                ParseActions.getParadaHere(gps, accuracy, oneParadaCallback);
            }
        };
        new LocationAsker(mContext, locationCallback, 10);
    }

}
