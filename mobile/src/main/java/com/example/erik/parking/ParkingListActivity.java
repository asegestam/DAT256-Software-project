package com.example.erik.parking;

import android.content.res.XmlResourceParser;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class ParkingListActivity extends AppCompatActivity {

    private static final String TAG = "ParkingListActivity";

    private static final String LATITUDE = "57.707664";
    private static final String LONGITUDE = "11.938690";
    private static final String RADIUS = "500";


    private static final String APP_ID = "00e0719c-23ce-4f32-badf-333a0e83fc9e";
    private static final String SERVER_URL = "http://data.goteborg.se/ParkingService/v2.1/PrivateTollParkings/";
    private static final String QUERY_OPTIONS = "{" + APP_ID + "}?latitude={" + LATITUDE + "}&longitude={" + LONGITUDE + "}&radius={" + RADIUS + "}";
    private static final String QUERY_URL = SERVER_URL + QUERY_OPTIONS;



    @Override
    protected void onCreate(@NonNull Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_parking_list);

        init();
    }

    private void init(){

        TextView textView = (TextView) findViewById(R.id.parking_list);
        textView.setMovementMethod(new ScrollingMovementMethod());

        Button btn;

        btn = (Button) findViewById(R.id.displayData);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick_QueryServer();
            }
        });

        btn = (Button) findViewById(R.id.clearDisplay);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick_ClearDisplay();
            }
        });
    }

    public void onClick_ClearDisplay(){
        TextView textView = (TextView) findViewById(R.id.parking_list);
        textView.setText("");
    }

    public void onClick_QueryServer(){
        Log.d(TAG, "onClick_QueryServer: onClick_QueryServer() called");
        AsyncDownloader downloader = new AsyncDownloader();
        downloader.execute();
    }

    private void addContentToTextView(String theString){
        TextView textView = (TextView) findViewById(R.id.parking_list);
        textView.setText(textView.getText().toString() + theString + "\n");
    }

    //Inner class for doing background download
    private class AsyncDownloader extends AsyncTask<Object, String, Integer>{

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

            Log.d(TAG, "processReceived: Starting to process the received data");

            int eventType = -1;
            while(eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = receivedData.getName();

                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        //Start of a record, so pull values encoded as attributes
                        if (tagName.equals("Name")) {
                            //name += receivedData.getText();
                        }
                        break;

                    case XmlResourceParser.TEXT:
                        name += receivedData.getText() + "\n";
                        break;

                    case XmlPullParser.END_TAG:
                        if (tagName.equals("Name")) {
                            recordsFound++;
                            publishProgress(name);
                        }
                        break;
                }
                eventType = receivedData.next();

                //Temp, remove so all data goes handled
                if (recordsFound > 50)
                    break;
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
            if (values.length == 1) {
                addContentToTextView(values[0]);
            }
            super.onProgressUpdate(values);
        }


    }
}

