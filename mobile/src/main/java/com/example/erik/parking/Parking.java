package com.example.erik.parking;

import com.google.android.gms.maps.model.LatLng;

public class Parking {

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
    }

    public LatLng getPosition() {
        return position;
    }

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
