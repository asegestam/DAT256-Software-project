package com.example.erik.parking;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.OnCompleteListener;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMapClickListener,
        PopupMenu.OnMenuItemClickListener,
        NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MapActivity";

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;


    private DrawerLayout mDrawerLayout;
    private Marker lastClicked;
    private Boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;

    //Different types of parkings
    private static final String PRIVATE_TOLL_PARKINGS = "PrivateTollParkings";
    private static final String HANDICAP_PARKINGS = "HandicapParkings";
    private static final String PUBLIC_TOLL_PARKINGS = "PublicTollParkings";
    private static final String PUBLIC_TIME_PARKINGS = "PublicTimeParkings";

    private ArrayList<Parking> parkings = new ArrayList<>();
    private ArrayList<Marker> markers = new ArrayList<>();
    private ArrayList<Marker> favorites = new ArrayList<>();

    private SubMenu subMenu;
    private Menu filterMenu;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
        }
        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        getLocationPermission();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem){
        int id = menuItem.getItemId();
        if (id == R.id.lessThanThirtyMin)
            lessThanThirtyMin();
        else if (id == R.id.lessThanOneHour)
            lessThanOneHour();
        else if (id == R.id.lessThanTwoHour)
            lessThanTwoHour();
        else if (id == R.id.greaterThanFourHour)
            greaterThanFourHour();
        else if (id == R.id.allParkings)
            activateAllChoosenMarkers();
        else if (id == R.id.standardTheme ||
                id == R.id.retroTheme ||
                id == R.id.darkTheme){
            setMapTheme(id);
        }else{
            for(Marker marker: favorites){
                if(marker.getTitle().equals(menuItem.getTitle())){
                    moveCamera(marker.getPosition(), DEFAULT_ZOOM);
                    marker.showInfoWindow();
                    marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                    mDrawerLayout.closeDrawers();
                    break;
                }
            }
        }


            return false;
    }

    private void activateAllChoosenMarkers(){
        MenuItem item;

        item = filterMenu.findItem(R.id.hkp);
        if (item.isChecked()){
            for (Marker marker: markers){
                if (marker.getTag() instanceof HandicapParking)
                    marker.setVisible(true);
            }
        }

        item = filterMenu.findItem(R.id.free);
        if (item.isChecked()){
            for (Marker marker : markers) {
                if (marker.getTag() instanceof FreeParking && !(marker.getTag() instanceof HandicapParking)) {
                    marker.setVisible(true);
                }
            }
        }

        item = filterMenu.findItem(R.id.betal);;
        if (item.isChecked()){
            for (Marker marker : markers) {
                if (marker.getTag() instanceof TollParking) {
                    marker.setVisible(true);
                }
            }
        }

    }

    public void lessThanThirtyMin(){
        Toast.makeText(this, "Filtrerar bort parkeringar under 30min", Toast.LENGTH_SHORT).show();
        activateAllChoosenMarkers();
        for (Marker marker: markers) {
            if (marker.isVisible()){
                Parking parking = (Parking) marker.getTag();
                if (parking.getMaxParkingTime().equals("10 min"))
                    marker.setVisible(false);
            }
        }
    }

    public void lessThanOneHour(){
        Toast.makeText(this, "Filtrerar bort parkeringar under 1 timme", Toast.LENGTH_SHORT).show();
        activateAllChoosenMarkers();
        for (Marker marker: markers) {
            if (marker.isVisible()){
                Parking parking = (Parking) marker.getTag();
                if (parking.getMaxParkingTime().equals("10 min") || parking.getMaxParkingTime().equals("30 min"))
                    marker.setVisible(false);
            }
        }
    }

    public void lessThanTwoHour(){
        Toast.makeText(this, "Filtrerar bort parkeringar under 2 timmar", Toast.LENGTH_SHORT).show();
        activateAllChoosenMarkers();
        for (Marker marker: markers) {
            if (marker.isVisible()){
                Parking parking = (Parking) marker.getTag();
                if (parking.getMaxParkingTime().equals("10 min") || parking.getMaxParkingTime().equals("30 min") || parking.getMaxParkingTime().equals("1 tim"))
                    marker.setVisible(false);
            }
        }
    }

    public void greaterThanFourHour(){
        Toast.makeText(this, "Filtrerar bort parkeringar under 4 timmar", Toast.LENGTH_SHORT).show();
        activateAllChoosenMarkers();
        for (Marker marker: markers) {
            if (marker.isVisible()){
                Parking parking = (Parking) marker.getTag();
                if (parking.getMaxParkingTime().equals("10 min") || parking.getMaxParkingTime().equals("30 min") || parking.getMaxParkingTime().equals("1 tim") || parking.getMaxParkingTime().equals("2 tim"))
                    marker.setVisible(false);
            }

        }
    }

    public void setMapTheme(int id){
        switch (id){
            case R.id.standardTheme:
                mMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                                this, R.raw.standard_theme));
                break;

            case R.id.retroTheme:
                mMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                                this, R.raw.retro_theme));
                break;

            case R.id.darkTheme:
                mMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                                this, R.raw.dark_theme));
                break;

        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        //Notify the user that the map is ready to be used
        Toast.makeText(this, "Redo att köras", Toast.LENGTH_SHORT).show();
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
            try {
                // Customise the styling of the base map using a JSON object defined
                // in a raw resource file.
                boolean success = googleMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                                this, R.raw.style_aubergine));

                if (!success) {
                    Log.e(TAG, "Style parsing failed.");
                }
            } catch (Resources.NotFoundException e) {
                Log.e(TAG, "Can't find style. Error: ", e);
            }
            mMap.setMyLocationEnabled(true);
            mMap.setMyLocationEnabled(true);
            mMap.setOnMarkerClickListener(this);
            mMap.setOnMapClickListener(this);
            onClick_QueryServer();

        }
    }

    /*Initializes the map*/
    private void initMap() {
        Log.d(TAG, "initMap: initializing the map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        assert mapFragment != null;
        mapFragment.getMapAsync(MapActivity.this);
    }

    /*Gets the location from the device and sets it on the map*/
    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: Getting the current location of this device");

        //Objekt to be used to get the location of the user
        FusedLocationProviderClient mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

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
        //Ask for the coarse_location permission because it was not already granted
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true;
                //Every permission is granted, initialize the map
                initMap();
            } else {
                //Ask for the fine_location permission because it was not already granted
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: Checking the results of the permission requests");
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int grantResult : grantResults) {
                        if (grantResult != PackageManager.PERMISSION_GRANTED) {
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

    /**
     * Called when a user clicks on the map
     */
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

    /**
     * Called when a user clicks on the marker
     */
    @Override
    public boolean onMarkerClick(Marker marker) {
        //if there was a lastClicked marker, set it to default color red
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

    /**
     * Shows a popup menu when called with map type switching functionality
     */
    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);

        // This activity implements OnMenuItemClickListener
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.maptype_menu);
        popup.show();
    }

    /**
     * Changes the maptype depending on what the user clicked on
     */
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

    /**
     * Creates a options menu on creation
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        filterMenu = menu;
        inflater.inflate(R.menu.filter_menu, menu);
        return true;
    }

    /** Handles the selection of options menu */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                //TODO implementera filter skit här
            case R.id.hkp:
                if (item.isChecked()) {
                    item.setChecked(false);
                    for (Marker marker : markers) {
                        if (marker.getTag() instanceof HandicapParking) {
                            marker.setVisible(false);
                        }
                    }
                } else {
                    item.setChecked(true);
                    for (Marker marker : markers) {
                        if (marker.getTag() instanceof HandicapParking) {
                            marker.setVisible(true);
                        }
                    }
                }
                break;
            case R.id.free:
                if (item.isChecked()) {
                    item.setChecked(false);
                    for (Marker marker : markers) {
                        if (marker.getTag() instanceof FreeParking && !(marker.getTag() instanceof HandicapParking)) {
                                marker.setVisible(false);
                        }
                    }
                } else {
                    item.setChecked(true);
                    for (Marker marker : markers) {
                        if (marker.getTag() instanceof FreeParking && !(marker.getTag() instanceof HandicapParking)) {
                            marker.setVisible(true);
                        }
                    }
                }
                break;
            case R.id.betal:
                if (item.isChecked()) {
                    item.setChecked(false);
                    for (Marker marker : markers) {
                        if (marker.getTag() instanceof TollParking) {
                            marker.setVisible(false);
                        }
                    }
                } else {
                    for (Marker marker : markers) {
                        item.setChecked(true);
                        if (marker.getTag() instanceof TollParking) {
                            marker.setVisible(true);
                        }
                    }
                }
                break;
        }
                return super.onOptionsItemSelected(item);
        }

    /**
     * Adds the last clicked marker (i.e currently selected marker) to favorites
     */
    public void addMarkerToFavorite(View v) {
        if (!favorites.contains(lastClicked)) {
            favorites.add(lastClicked);
            Toast.makeText(this, "Tillagd i favoriter", Toast.LENGTH_SHORT).show();
            ((ImageButton) findViewById(R.id.favorite_btn)).setImageResource(R.drawable.ic_star_black_24dp);
            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            Menu menu = navigationView.getMenu();
            if (subMenu == null)
                subMenu = menu.addSubMenu("Favorites");
            subMenu.add(Menu.NONE, lastClicked.hashCode(), Menu.NONE, lastClicked.getTitle());

        } else {
            subMenu.removeItem(lastClicked.hashCode());
            favorites.remove(lastClicked);
            Toast.makeText(this, "Borttagen ur favoriter", Toast.LENGTH_SHORT).show();
            ((ImageButton) findViewById(R.id.favorite_btn)).setImageResource(R.drawable.ic_star_border_black_24dp);
        }
        Log.d(TAG, "items in favorites: " + favorites.size());
    }

    /**
     * Adds a parking object to a ArrayList
     * If the parking spot is added to the map -
     * create a marker and add it to the map
     * add the marker to a ArrayList for control of markers
     */
    private void addMarkerToMap(Parking parking) {
        parkings.add(parking);
        for (Parking park : parkings) {
            if (!park.getAdded()) {
                Marker marker = mMap.addMarker(new MarkerOptions().
                        position(parking.getPosition()).
                        title(parking.getName()).
                        snippet(parking.getParkingInformation()));
                marker.setTag(parking);
                marker.setVisible(false);
                mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                    @Override
                    public View getInfoWindow(Marker arg0) {
                        return null;
                    }

                    @Override
                    public View getInfoContents(Marker marker) {
                        LinearLayout info = new LinearLayout(MapActivity.this);
                        info.setOrientation(LinearLayout.VERTICAL);
                        TextView title = new TextView(MapActivity.this);
                        title.setTextColor(Color.BLACK);
                        title.setGravity(Gravity.CENTER);
                        title.setTypeface(null, Typeface.BOLD);
                        title.setText(marker.getTitle());
                        TextView snippet = new TextView(MapActivity.this);
                        snippet.setTextColor(Color.GRAY);
                        snippet.setText(marker.getSnippet());
                        info.addView(title);
                        info.addView(snippet);
                        return info;
                    }
                });
                parking.setAdded(true);
                markers.add(marker);
            }
        }
    }
    public void onClick_QueryServer() {
        Log.d(TAG, "onClick_QueryServer: onClick_QueryServer() called");

        //Create new background processes and get the different parkings
        new AsyncDownloader().execute(HANDICAP_PARKINGS);
        new AsyncDownloader().execute(PRIVATE_TOLL_PARKINGS);
        new AsyncDownloader().execute(PUBLIC_TIME_PARKINGS);
        new AsyncDownloader().execute(PUBLIC_TOLL_PARKINGS);
    }

    //Inner class for doing background download
    private class AsyncDownloader extends AsyncTask<Object, Parking, Integer> {

        private static final String APP_ID = "00e0719c-23ce-4f32-badf-333a0e83fc9e";
        private static final String SERVER_URL = "http://data.goteborg.se/ParkingService/v2.1/";
        private static final String QUERY_OPTIONS = "/{" + APP_ID + "}?";


        @Override
        protected Integer doInBackground(Object... objects) {
            if (objects[0] instanceof String) {
                String typeOfParking = (String) objects[0];
                XmlPullParser receivedData = tryDownloadingXmlData(typeOfParking);
                return tryParsingXmlData(receivedData, typeOfParking);
            }
            return null;
        }

        private XmlPullParser tryDownloadingXmlData(String typeOfParking) {
            try {
                Log.i(TAG, "tryDownloadingXmlData: Trying to download");
                String QUERY_URL = SERVER_URL + typeOfParking + QUERY_OPTIONS;
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

        private int tryParsingXmlData(XmlPullParser receivedData, String typeOfParking) {
            Log.d(TAG, "tryParsingXmlData: Trying to parse the xml");
            if (receivedData != null) {
                try {
                    return processReceived(receivedData, typeOfParking);
                } catch (XmlPullParserException e) {
                    Log.e(TAG, "tryParsingXmlData: Pull Parser failure", e);
                } catch (IOException e) {
                    Log.e(TAG, "tryParsingXmlData: IO Exception parsing XML", e);
                }
            }

            return -1;
        }

        private int processReceived(XmlPullParser receivedData, String typeOfParking) throws IOException, XmlPullParserException {

            int recordsFound = 0;

            Log.d(TAG, "processReceived: Starting to process the received data. Type of parking: " + typeOfParking);

            switch (typeOfParking) {
                case HANDICAP_PARKINGS:
                    recordsFound = processHandicapParkings(receivedData);
                    break;

                case PRIVATE_TOLL_PARKINGS:
                    recordsFound = processPrivateTollParkings(receivedData);
                    break;

                case PUBLIC_TIME_PARKINGS:
                    recordsFound = processPublicTimeParkings(receivedData);
                    break;

                case PUBLIC_TOLL_PARKINGS:
                    recordsFound = processPublicTollParkings(receivedData);


                default:
                    publishProgress();
            }

            if (recordsFound == 0) {
                publishProgress();
            }
            Log.d(TAG, "processReceived: Finnished proccesing data, processed: " + recordsFound + " records, type: " + typeOfParking);
            return recordsFound;
        }

        @Override
        protected void onProgressUpdate(Parking... parkings) {
            if (parkings.length == 0)
                Log.i(TAG, "onProgressUpdate: No data Downloaded.");
            if (parkings.length == 1)
                addMarkerToMap(parkings[0]);

            super.onProgressUpdate(parkings);
        }


        private int processHandicapParkings(XmlPullParser receivedData) throws IOException, XmlPullParserException {
            int recordsFound = 0;
            String endTag = "HandicapParking";

            //Wanted values in the XML records for handicap parkings
            String name = "";
            String owner = "";
            String parkingSpaces = "";
            String maxParkingTime = "";
            String lat = "";
            String lng = "";
            String maxParkingTimeLimitation = "";

            int eventType = -1;
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = receivedData.getName();

                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        //Start of a record, so pull values encoded as attributes
                        switch (tagName) {
                            case "Name":
                                name = receivedData.nextText();
                                break;

                            case "Owner":
                                owner = receivedData.nextText();
                                break;

                            case "ParkingSpaces":
                                parkingSpaces = receivedData.nextText();
                                break;

                            case "MaxParkingTime":
                                maxParkingTime = receivedData.nextText();
                                break;

                            case "Lat":
                                lat = receivedData.nextText();
                                break;

                            case "Long":
                                lng = receivedData.nextText();
                                break;

                            case "MaxParkingTimeLimitation":
                                maxParkingTimeLimitation = receivedData.nextText();
                        }

                    case XmlPullParser.END_TAG:
                        if (tagName.equals(endTag)) {
                            recordsFound++;

                            if (parkingSpaces.equals("")) {
                                parkingSpaces = "No data";
                            }
                            if (maxParkingTimeLimitation.equals("")) {
                                maxParkingTimeLimitation = "No data";
                            }
                            if (maxParkingTime.equals("")) {
                                maxParkingTime = "No data";
                            }
                            if (owner.equals("")) {
                                owner = "No data";
                            }
                            if (lat.equals("")) {
                                lat = "0";
                            }
                            if (lng.equals("")) {
                                lng = "0";
                            }
                            publishProgress(new HandicapParking(name,
                                    owner,
                                    parkingSpaces,
                                    maxParkingTime,
                                    Double.parseDouble(lat),
                                    Double.parseDouble(lng),
                                    maxParkingTimeLimitation
                            ));
                        }
                        break;
                }
                eventType = receivedData.next();
            }

            return recordsFound;

        }

        private int processPrivateTollParkings(XmlPullParser receivedData) throws IOException, XmlPullParserException {
            int recordsFound = 0;
            String endTag = "PrivateParking";

            //Wanted values in the XML records for handicap parkings
            String name = "";
            String owner = "";
            String parkingSpaces = "";
            String maxParkingTime = "";
            String lat = "";
            String lng = "";
            String parkingCost = "";
            String currentParkingCost = "";
            String phoneParkingCode = "";


            int eventType = -1;
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = receivedData.getName();

                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        //Start of a record, so pull values encoded as attributes
                        switch (tagName) {
                            case "Name":
                                name = receivedData.nextText();
                                break;

                            case "Owner":
                                owner = receivedData.nextText();
                                break;

                            case "ParkingSpaces":
                                parkingSpaces = receivedData.nextText();
                                break;

                            case "MaxParkingTime":
                                maxParkingTime = receivedData.nextText();
                                break;

                            case "Lat":
                                lat = receivedData.nextText();
                                break;

                            case "Long":
                                lng = receivedData.nextText();
                                break;

                            case "ParkingCost":
                                parkingCost = receivedData.nextText();
                                break;

                            case "CurrentParkingCost":
                                currentParkingCost = receivedData.nextText();
                                break;

                            case "PhoneParkingCode":
                                phoneParkingCode = receivedData.nextText();
                                break;

                        }

                    case XmlPullParser.END_TAG:
                        if (tagName.equals(endTag)) {
                            recordsFound++;

                            if (name.equals("")) name = "NoData";
                            if (owner.equals("")) owner = "No Data";
                            if (parkingSpaces.equals("")) parkingSpaces = "No Data";
                            if (maxParkingTime.equals("")) maxParkingTime = "No Data";
                            if (parkingCost.equals("")) parkingCost = "No Data";
                            if (currentParkingCost.equals("")) currentParkingCost = "No Data";
                            if (phoneParkingCode.equals("")) phoneParkingCode = "No Data";
                            if (lat.equals("")) lat = "0";
                            if (lng.equals("")) lng = "0";

                            publishProgress(new PrivateTollParking(name,
                                    owner,
                                    parkingSpaces,
                                    maxParkingTime,
                                    Double.parseDouble(lat),
                                    Double.parseDouble(lng),
                                    parkingCost,
                                    currentParkingCost,
                                    phoneParkingCode
                            ));
                        }
                        break;
                }
                eventType = receivedData.next();
            }

            return recordsFound;

        }

        private int processPublicTimeParkings(XmlPullParser receivedData) throws IOException, XmlPullParserException {
            int recordsFound = 0;
            String endTag = "PublicTimeParking";

            //Wanted values in the XML records for handicap parkings
            String name = "";
            String owner = "";
            String parkingSpaces = "";
            String maxParkingTime = "";
            String lat = "";
            String lng = "";
            String parkingCost = "";
            String parkingCharge = "";
            String currentParkingCost = "";
            String phoneParkingCode = "";
            String maxParkingTimeLimitation = "";


            int eventType = -1;
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = receivedData.getName();

                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        //Start of a record, so pull values encoded as attributes
                        switch (tagName) {
                            case "Name":
                                name = receivedData.nextText();
                                break;

                            case "Owner":
                                owner = receivedData.nextText();
                                break;

                            case "ParkingSpaces":
                                parkingSpaces = receivedData.nextText();
                                break;

                            case "MaxParkingTime":
                                maxParkingTime = receivedData.nextText();
                                break;

                            case "Lat":
                                lat = receivedData.nextText();
                                break;

                            case "Long":
                                lng = receivedData.nextText();
                                break;

                            case "ParkingCost":
                                parkingCost = receivedData.nextText();
                                break;

                            case "ParkingCharge":
                                parkingCharge = receivedData.nextText();
                                break;

                            case "CurrentParkingCost":
                                currentParkingCost = receivedData.nextText();
                                break;

                            case "PhoneParkingCode":
                                phoneParkingCode = receivedData.nextText();
                                break;

                            case "MaxParkingTimeLimitation":
                                maxParkingTimeLimitation = receivedData.nextText();
                                break;

                        }

                    case XmlPullParser.END_TAG:
                        if (tagName.equals(endTag)) {
                            recordsFound++;


                            if (name.equals("")) name = "NoData";
                            if (owner.equals("")) owner = "No Data";
                            if (parkingSpaces.equals("")) parkingSpaces = "No Data";
                            if (maxParkingTime.equals("")) maxParkingTime = "No Data";
                            if (parkingCost.equals("")) parkingCost = "No Data";
                            if (currentParkingCost.equals("")) currentParkingCost = "No Data";
                            if (phoneParkingCode.equals("")) phoneParkingCode = "No Data";
                            if (lat.equals("")) lat = "0";
                            if (lng.equals("")) lng = "0";
                            if (maxParkingTimeLimitation.equals(""))
                                maxParkingTimeLimitation = "No data";

                            publishProgress(new PublicTimeParking(name,
                                    owner,
                                    parkingSpaces,
                                    maxParkingTime,
                                    Double.parseDouble(lat),
                                    Double.parseDouble(lng),
                                    maxParkingTimeLimitation
                            ));
                        }
                        break;
                }
                eventType = receivedData.next();
            }

            return recordsFound;

        }

        private int processPublicTollParkings(XmlPullParser receivedData) throws IOException, XmlPullParserException {
            int recordsFound = 0;
            String endTag = "PublicTollParking";

            //Wanted values in the XML records for handicap parkings
            String name = "";
            String owner = "";
            String parkingSpaces = "";
            String maxParkingTime = "";
            String lat = "";
            String lng = "";
            String parkingCost = "";
            String parkingCharge = "";
            String currentParkingCost = "";
            String phoneParkingCode = "";
            String maxParkingTimeLimitation = "";


            int eventType = -1;
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = receivedData.getName();

                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        //Start of a record, so pull values encoded as attributes
                        switch (tagName) {
                            case "Name":
                                name = receivedData.nextText();
                                break;

                            case "Owner":
                                owner = receivedData.nextText();
                                break;

                            case "ParkingSpaces":
                                parkingSpaces = receivedData.nextText();
                                break;

                            case "MaxParkingTime":
                                maxParkingTime = receivedData.nextText();
                                break;

                            case "Lat":
                                lat = receivedData.nextText();
                                break;

                            case "Long":
                                lng = receivedData.nextText();
                                break;

                            case "ParkingCost":
                                parkingCost = receivedData.nextText();
                                break;

                            case "ParkingCharge":
                                parkingCharge = receivedData.nextText();
                                break;

                            case "CurrentParkingCost":
                                currentParkingCost = receivedData.nextText();
                                break;

                            case "PhoneParkingCode":
                                phoneParkingCode = receivedData.nextText();
                                break;

                            case "MaxParkingTimeLimitation":
                                maxParkingTimeLimitation = receivedData.nextText();
                                break;

                        }

                    case XmlPullParser.END_TAG:
                        if (tagName.equals(endTag)) {
                            recordsFound++;

                            if (name.equals("")) name = "NoData";
                            if (owner.equals("")) owner = "No Data";
                            if (parkingSpaces.equals("")) parkingSpaces = "No Data";
                            if (maxParkingTime.equals("")) maxParkingTime = "No Data";
                            if (parkingCost.equals("")) parkingCost = "No Data";
                            if (currentParkingCost.equals("")) currentParkingCost = "No Data";
                            if (phoneParkingCode.equals("")) phoneParkingCode = "No Data";
                            if (lat.equals("")) lat = "0";
                            if (lng.equals("")) lng = "0";
                            if (maxParkingTimeLimitation.equals(""))
                                maxParkingTimeLimitation = "No data";

                            publishProgress(new PublicTollParking(name,
                                    owner,
                                    parkingSpaces,
                                    maxParkingTime,
                                    Double.parseDouble(lat),
                                    Double.parseDouble(lng),
                                    parkingCost,
                                    parkingCharge,
                                    currentParkingCost,
                                    phoneParkingCode,
                                    maxParkingTimeLimitation
                            ));
                        }
                        break;
                }
                eventType = receivedData.next();
            }

            return recordsFound;

        }

    }
}
