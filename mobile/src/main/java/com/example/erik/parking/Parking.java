package com.example.erik.parking;

import com.google.android.gms.maps.model.LatLng;

public class Parking {

    private LatLng position;
    public String parkingName;
    public String time;
    public String cost;
    private boolean isAdded;

    public Parking(String parkingName, double latitude, double longitude, String time, String cost, boolean isAdded) {
        this.parkingName = parkingName;
        this.position = new LatLng(latitude, longitude);
        this.time = time;
        this.cost = cost;
        this.isAdded = isAdded;
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

    public boolean getAdded(){
        return isAdded;
    }
    public void setAdded(boolean state){
        this.isAdded = state;
    }


}

