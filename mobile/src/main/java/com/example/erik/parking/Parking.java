package com.example.erik.parking;

import com.google.android.gms.maps.model.LatLng;

public class Parking {

    private LatLng position;
    public String parkingName;
    public String time;
    public String cost;


    public Parking(String parkingName, double latitude, double longitude, String time, String cost) {
        this.parkingName = parkingName;
        this.position = new LatLng(latitude, longitude);
        this.time = time;
        this.cost = cost;
    }


    public String getCost() {
        return cost;
    }

    public String getTime() {
        return time;
    }

    public LatLng getPosition() {
        return position;
    }

    public String getParkingName() {
        return parkingName;
    }


}


