package com.example.erik.parking;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class ParkingListActivity extends AppCompatActivity {

    private static final String TAG = "ParkingListActivity";

    @Override
    protected void onCreate(@NonNull Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_parking_list);

        setTextViewContent(generateXmlString());
    }

    private void setTextViewContent(String theString){
        TextView textView = (TextView) findViewById(R.id.parking_list);
        textView.setText(theString);
    }

    private String generateXmlString(){
        return "";
    }


}
