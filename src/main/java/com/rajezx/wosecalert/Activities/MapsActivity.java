package com.rajezx.wosecalert.Activities;

import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.rajezx.wosecalert.R;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private String latvalue;
    private String longvaue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_fragment);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SharedPreferences sharedPreferences = getSharedPreferences("latandlongvalue",MODE_PRIVATE);
       latvalue=  sharedPreferences.getString("latvalue","");
        longvaue=  sharedPreferences.getString("logvalue","");
        Log.v("latvalue",latvalue);
        Log.v("logvalue",longvaue);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng sydney = new LatLng(Double.valueOf(latvalue),Double.valueOf(longvaue));
        mMap.
                addMarker(new MarkerOptions().position(sydney).title("Last Known Location").snippet("Location")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_nav_gps)));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        /*MapStyleOptions style = MapStyleOptions.loadRawResourceStyle(
                this, R.raw.style_json);
        googleMap.setMapStyle(style);*/

    }
}
