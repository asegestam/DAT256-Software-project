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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private DrawerLayout mDrawerLayout;
    private static final int ERROR_DIALOG_REQUEST = 9001;


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

}
