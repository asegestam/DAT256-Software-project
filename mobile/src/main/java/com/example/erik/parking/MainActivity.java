package com.example.erik.parking;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import static com.android.volley.toolbox.Volley.newRequestQueue;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private DrawerLayout mDrawerLayout;
    private static final int ERROR_DIALOG_REQUEST = 9001;


    private Button btnSendRequest;

    private RequestQueue mRequestQueue;

    private StringRequest stringRequest;

    private String url = "http://data.goteborg.se/ParkingService/v2.1/PublicTollParkings/{00e0719c-23ce-4f32-badf-333a0e83fc9e}?latitude={57.707664}&longitude={11.938690}&radius={500}&format={JSON}";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //set contentview to activity_main
        setContentView(R.layout.activity_main);

        mDrawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();

                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here

                        return true;
                    }
                });

        //Check if google services is ok before init()
        if (isServicesOK()) {
            init();
        }
    }


    private void init() {

        //OnClickListener for the btnMap button
        Button btnMap = (Button) findViewById(R.id.btnMap);
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MapActivity.class);
                startActivity(intent);

            }
        });

        Button parkingList = (Button) findViewById(R.id.xmlButton);
        parkingList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, Parking.class);
                startActivity(intent);
            }
        });

        btnSendRequest = (Button) findViewById(R.id.btnSendRequest);
        btnSendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SendRequestAndPrintResponse();
            }
        });

        //OnclickListener for the btnParkingList button
        Button btnParkingList = (Button) findViewById(R.id.btnParkingList);
        btnParkingList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ParkingListActivity.class);
                startActivity(intent);
            }
        });
    }

    public boolean isServicesOK(){
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        //Check if the google play services is available on device
        if (available == ConnectionResult.SUCCESS) {
            //Everything is good and clear to go with the user
            Log.d(TAG, "isServicesOK: Google play services is working");
            return true;
        }
        else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //ERROR occured but it's okej, can get fixed by the user
            Log.d(TAG, "isServicesOK: error occured but can get fixed");

            //Creating a error dialog
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
            //Showing the dialog to the user
            dialog.show();
        }
        else {
            Log.d(TAG, "isServicesOK: Map Request failed");
            Toast.makeText(this, "Map request failed", Toast.LENGTH_SHORT).show();
        }

        return false;
    }

    private void SendRequestAndPrintResponse() {

        mRequestQueue = Volley.newRequestQueue(this);

        stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.i(TAG, "Response:" + response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.i(TAG, error.toString());
            }
        });

        mRequestQueue.add(stringRequest);

        }
    }