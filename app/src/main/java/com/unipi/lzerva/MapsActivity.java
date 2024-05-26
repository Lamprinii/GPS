package com.unipi.lzerva;

import androidx.fragment.app.FragmentActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.unipi.lzerva.databinding.ActivityMapsBinding;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private float latitudes[];
    private float longtitudes[];
    private float accelaration[];
    SQLiteDatabase database;

    private ActivityMapsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database = openOrCreateDatabase("myDB.db",MODE_PRIVATE,null);
        select();


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public void select(){
        Cursor cursor = database.rawQuery("Select Latitude, Longitude, Accelaration from EmergancyBreak",null);
        int counter=0;
        int size = cursor.getCount();
        latitudes = new float[size];
        longtitudes = new float[size];
        accelaration = new float[size];

        while (cursor.moveToNext()){
            latitudes[counter] = cursor.getFloat(0);
            longtitudes[counter] = cursor.getFloat(1);
            accelaration[counter] = cursor.getFloat(2);
            counter++;
        }
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

        LatLng tempLocation = null;
        // Add a marker in Sydney and move the camera
        for (int i=0; i<latitudes.length; i++) {
            tempLocation = new LatLng(latitudes[i], longtitudes[i]);
            mMap.addMarker(new MarkerOptions().position(tempLocation).title(String.valueOf(accelaration[i])));

        }
        mMap.moveCamera(CameraUpdateFactory.newLatLng(tempLocation));
    }
}