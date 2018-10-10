package com.example.erik.parking;

import com.google.android.gms.maps.model.LatLng;

public class Parking {

<<<<<<< HEAD
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
=======
    //Variabels
    private LatLng position;
    private String name;
    private double cost;
    private boolean isAdded;

    public Parking(String name, double lat, double lng, double cost, boolean isAdded){
        this.name = name;
        position = new LatLng(lat, lng);
        this.cost = cost;
        this.isAdded = isAdded;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
>>>>>>> testing
    }

    public LatLng getPosition() {
        return position;
    }

<<<<<<< HEAD
    public String getParkingName() {
        return parkingName;
    }


}

=======
    public void setPosition(LatLng position){
        this.position = position;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public boolean getAdded(){
        return isAdded;
    }
    public void setAdded(boolean state){
        this.isAdded = state;
    }

}
>>>>>>> testing
