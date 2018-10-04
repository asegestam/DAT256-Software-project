package com.example.erik.parking;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.OnCompleteListener;

import java.util.ArrayList;


public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private static final String TAG = "MapActivity";

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;

    //Statiska Positioner
    private static final LatLng LindholmenParkering = new LatLng(57.707664, 11.938690);
    private Marker mLHP;

    private ArrayList<LatLng> pPositions;

    private Boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        getLocationPermission();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        //Notify the user that the map is ready to be used
        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: Map is ready");
        mMap = googleMap;

        //Check if it is ok to get device location
        if (mLocationPermissionsGranted) {
            getDeviceLocation();
            showParkings(mMap);

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mMap.setMyLocationEnabled(true);

        }
    }

    /*Initializes the map*/
    private void initMap(){
        Log.d(TAG, "initMap: initializing the map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(MapActivity.this);
    }

    /*Gets the location from the device and sets it on the map*/
    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: Getting the current location of this device");

        //Objekt to be used to get the location of the user
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            //check the location permission
            if (mLocationPermissionsGranted) {
                //Get last known location from the device
                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: Found the location of the device");
                            Location location = (Location) task.getResult();

                            moveCamera(new LatLng(location.getLatitude(), location.getLongitude()), DEFAULT_ZOOM);

                        } else {
                            Log.d(TAG, "onComplete: Device location is unknown/null ");
                            Toast.makeText(MapActivity.this, "Unable to get current location", Toast.LENGTH_SHORT).show();

                            //Gothenburg is the default city if the device loaction is unkown
                            moveCamera(new LatLng(57.7089, 11.9746), DEFAULT_ZOOM);
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.d(TAG, "getDevicePosition: SecurityException: " + e.getMessage());
        }
    }

    /*Moves the camera view of the map to wanted location and zoom*/
    private void moveCamera(LatLng latLng, float zoom) {
        Log.d(TAG, "moveCamera: Moving the camera to lat: " + latLng.latitude + ", lng: " + latLng.longitude);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    /*Gets the necessary permissions from the user or asks for them*/
    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION};

        //Check the permissions fine_location and coarse_location from the user
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true;
                //Every permission is granted, initialize the map
                initMap();
            } else {
                //Ask for the fine_location permission because it was not already granted
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            //Ask for the coarse_location permission because it was not already granted
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: Checking the results of the permission requests");
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for(int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            mLocationPermissionsGranted = false;
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionsGranted = true;
                    //All permissions are granted so we can initialize the map
                    initMap(); 
                }
            }
        }
    }
    /** Adds a static position for a parkingplace */
    public void showParkings(GoogleMap mMap){
        mLHP = mMap.addMarker(new MarkerOptions().
                position(LindholmenParkering).
                title("Lindholmsallén Parkering").
                snippet("Här kan man visa information om parkeringen"));
        mMap.setOnMarkerClickListener(this);
    }
    /** Called when a user clicks on the marker */
    @Override
    public boolean onMarkerClick(Marker marker) {
        //Sets the color of the clicked marker to azure blue
        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        return false;
    }
}
