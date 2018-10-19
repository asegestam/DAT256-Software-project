package com.example.erik.parking;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.XmlResourceParser;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener, PopupMenu.OnMenuItemClickListener {
    /** Static declarations */
    private static final String TAG = "MapActivity";

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;

    private Marker lastClicked;

    private Boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    private ArrayList<Parking> parkings = new ArrayList<>();
    private ArrayList<Marker> markers = new ArrayList<>();
    private ArrayList<Marker> favorites = new ArrayList<>();

    protected void init() {
        ImageButton btn;
        btn = findViewById(R.id.btnMarker);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick_QueryServer();
            }
        });
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        getLocationPermission();
        init();
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
            mMap.setOnMarkerClickListener(this);
            mMap.setOnMapClickListener(this);

        }
    }

    /*Initializes the map*/
    private void initMap() {
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
                            if (location != null) {
                                moveCamera(new LatLng(location.getLatitude(), location.getLongitude()), DEFAULT_ZOOM);
                            }

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
                    for (int i = 0; i < grantResults.length; i++) {
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

    /**Called when a user clicks on the map */
    @Override
    public void onMapClick(LatLng latlng) {
        //if there is a last clicked marker, set it to default color red
        if (lastClicked != null) {
            lastClicked.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        }
        //if the favorite button is visible, set it to gone
        if ((findViewById(R.id.favorite_btn)).getVisibility() == View.VISIBLE) {
            (findViewById(R.id.favorite_btn)).setVisibility(View.GONE);
        }
    }

    public String getDistance(LatLng marker, LatLng myPos) {
        float b3 = (float) myPos.latitude;
        float b2 = (float) marker.latitude;
        float c3 = (float) myPos.longitude;
        float c2 = (float) marker.longitude;

        float[] result = new float[1];
        Location.distanceBetween(b3, c3, b2, c2, result);

        return String.valueOf(Math.round(result[0]));

    }

    /** Called when a user clicks on the marker */
    @Override
    public boolean onMarkerClick(Marker marker) {
        //if there was a lastClicked marker, set it to default color red




        marker.setSnippet("fågelavstånd: " + getDistance(marker.getPosition(), getMyLocation()) + "m");

        if (lastClicked != null) {
            lastClicked.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

        }
        //change the clicked marker to blue
        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        lastClicked = marker;
        (findViewById(R.id.favorite_btn)).setVisibility(View.VISIBLE);
        if (!favorites.contains(lastClicked)) {
            ((ImageButton) findViewById(R.id.favorite_btn)).setImageResource(R.drawable.ic_star_border_black_24dp);
        } else {
            ((ImageButton) findViewById(R.id.favorite_btn)).setImageResource(R.drawable.ic_star_black_24dp);
        }

        return false;
    }

    private LatLng getMyLocation() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();
        LatLng myPos = new LatLng(latitude,longitude);
        return  myPos;
    }

    /**Shows a popup menu when called with map type switching functionality*/
    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);

        // This activity implements OnMenuItemClickListener
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.maptype_menu);
        popup.show();
    }

    /**Changes the maptype depending on what the user clicked on*/
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.maptype_normal:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case R.id.maptype_satellite:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case R.id.maptype_hybrid:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
            case R.id.maptype_terrain:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
        }
        return true;
    }

    /** Creates a options menu on creation */
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.filter_menu, menu);
        return true;
    }

    /**Changes the visibility of markers depending on what filter was chosen */
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case R.id.ltp:
                if (item.isChecked()) {
                    item.setChecked(false);
                    //TODO gå igenom listan för visibility här för varje meny val
                } else {
                    item.setChecked(true);
                }
                break;
            case R.id.ktp:
                if (item.isChecked()) {
                    item.setChecked(false);
                    for (Marker marker : markers) {
                        marker.setVisible(false);
                    }
                } else {
                    item.setChecked(true);
                    for (Marker marker : markers) {
                        marker.setVisible(true);
                    }
                }
                break;
        }

        return  true;
    }
    /** Adds the last clicked marker (i.e currently selected marker) to favorites */
    public void addMarkerToFavorite(View v){
        if(!favorites.contains(lastClicked)){
            favorites.add(lastClicked);
            ((ImageButton)findViewById(R.id.favorite_btn)).setImageResource(R.drawable.ic_star_black_24dp);
        }
        else{
            favorites.remove(lastClicked);
            ((ImageButton)findViewById(R.id.favorite_btn)).setImageResource(R.drawable.ic_star_border_black_24dp);
        }
        Log.d(TAG, "items in favorites: " + favorites.size());
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    //ParkingList starts here



    private static final String LATITUDE = "57.707664";
    private static final String LONGITUDE = "11.938690";
    private static final String RADIUS = "500";



    private static final String APP_ID = "00e0719c-23ce-4f32-badf-333a0e83fc9e";
    private static final String SERVER_URL = "http://data.goteborg.se/ParkingService/v2.1/PrivateTollParkings/";
    private static final String QUERY_OPTIONS = "{" + APP_ID + "}?latitude={" + LATITUDE + "}&longitude={" + LONGITUDE + "}&radius={" + RADIUS + "}";
    private static final String QUERY_URL = SERVER_URL + QUERY_OPTIONS;



    public void onClick_QueryServer(){
        Log.d(TAG, "onClick_QueryServer: onClick_QueryServer() called");
        AsyncDownloader downloader = new AsyncDownloader();
        downloader.execute();
    }

    /** Adds a parking object to a ArrayList
     * If the parking spot is added to the map -
     * create a marker and add it to the map
     * add the marker to a ArrayList for control of markers
     * */
    private void addMarkerToMap(Parking park) {
        parkings.add(park);
        for(Parking parking:parkings){
            if(parking.getAdded() == false){
                Marker marker = mMap.addMarker(new MarkerOptions().
                        position(parking.getPosition()).title(parking.getName()));
                parking.setAdded(true);
                markers.add(marker);
            }
        }
    }

    //Inner class for doing background download
    private class AsyncDownloader extends AsyncTask<Object, String, Integer> {

            @Override
            protected Integer doInBackground(Object... objects) {
                XmlPullParser receivedData = tryDownloadingXmlData();
                int recordsFound = tryParsingXmlData(receivedData);
                return recordsFound;
            }

            private XmlPullParser tryDownloadingXmlData() {
                try {
                    Log.i(TAG, "tryDownloadingXmlData: Trying to download");
                    URL xmlUrl = new URL(QUERY_URL);
                    XmlPullParser receivedData = XmlPullParserFactory.newInstance().newPullParser();
                    receivedData.setInput(xmlUrl.openStream(), null);
                    return receivedData;

                } catch (XmlPullParserException e) {
                    Log.e(TAG, "tryDownloadingXmlData: XmlPullParserException: ", e);
                } catch (MalformedURLException e) {
                    Log.e(TAG, "tryDownloadingXmlData: MalformedURLException: ", e);
                } catch (IOException e) {
                    Log.e(TAG, "tryDownloadingXmlData: IOException: ", e);
                }

                return null;

            }

            private int tryParsingXmlData(XmlPullParser receivedData) {
                Log.d(TAG, "tryParsingXmlData: Trying to parse the xml");
                if (receivedData != null) {
                    try {
                        return processReceived(receivedData);
                    } catch (XmlPullParserException e) {
                        Log.e(TAG, "tryParsingXmlData: Pull Parser failure", e);
                    } catch (IOException e) {
                        Log.e(TAG, "tryParsingXmlData: IO Exception parsing XML", e);
                    }
                }

                return -1;
            }

            private int processReceived(XmlPullParser receivedData) throws IOException, XmlPullParserException {

                int recordsFound = 0;

                //Values in the XML records
                String name = "";
                String lat = "";
                String lng = "";
                String cost = "";

                Log.d(TAG, "processReceived: Starting to process the received data");

                int eventType = -1;
                while(eventType != XmlPullParser.END_DOCUMENT) {
                    String tagName = receivedData.getName();

                    switch (eventType) {
                        case XmlPullParser.START_TAG:
                            //Start of a record, so pull values encoded as attributes
                            if (tagName.equals("Name")) {
                                receivedData.next();
                                name = receivedData.getText();
                            }else if (tagName.equals("Lat")) {
                                receivedData.next();
                                lat = receivedData.getText();
                            }else if(tagName.equals("Long")) {
                                receivedData.next();
                                lng = receivedData.getText();
                            }else if(tagName.equals("CurrentParkingCost")) {
                                receivedData.next();
                                cost = receivedData.getText();
                            }
                            break;

                        case XmlResourceParser.TEXT:
                            // name += receivedData.getText() + "\n";
                            break;

                        case XmlPullParser.END_TAG:
                            if (tagName.equals("PrivateParking")) {
                                recordsFound++;
                                publishProgress(name, lat, lng, cost);
                            }
                            break;
                    }
                    eventType = receivedData.next();

                    //Temp, remove so all data goes handled
                    //if (recordsFound > 50)
                    //  break;
                }

                if (recordsFound == 0) {
                    publishProgress();
                }
                Log.d(TAG, "processReceived: Finnished proccesing data, processed: " + recordsFound + " records.");
                return recordsFound;
            }

            @Override
            protected void onProgressUpdate(String... values){
                if (values.length == 0)
                    Log.i(TAG, "onProgressUpdate: No data Downloaded");
                if (values.length == 4) {
                    //addContentToTextView(values[0] + " " + values[1]  + " " + values[2]  + " " + values[3]);
                    addMarkerToMap(new Parking(values[0], Double.parseDouble(values[1]), Double.parseDouble(values[2]), Double.parseDouble(values[3]), false));
                }

                super.onProgressUpdate(values);
            }
        }
}
