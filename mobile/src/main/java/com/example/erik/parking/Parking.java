package com.example.erik.parking;

import com.google.android.gms.maps.model.LatLng;

public class Parking {

    //Instance Variabels
    private String name;
    private String owner;
    private String parkingSpaces;
    private String maxParkingTime;
    private LatLng position;
    private boolean isAdded;
    private String type ="";

    public Parking(String name, String owner, String parkingSpaces, String maxParkingTime, double lat, double lng, boolean isAdded){
        this.name = name;
        this.owner = owner;
        this.parkingSpaces = parkingSpaces;
        this.maxParkingTime = maxParkingTime;
        this.isAdded = isAdded;
        position = new LatLng(lat, lng);
    }

    public String getParkingInformation() {
        return  "Typ av parkering: " + this.getType() + "\n" +
                "Ägare: " + getOwner() + "\n" +
                "Antal platser: " + getParkingSpaces() + "\n" +
                "Maximal parkeringstid: " + getMaxParkingTime() + "\n";
    }

    public String getType(){
        return type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getParkingSpaces() {
        return parkingSpaces;
    }

    public void setParkingSpaces(String parkingSpaces) {
        this.parkingSpaces = parkingSpaces;
    }

    public String getMaxParkingTime() {
        return maxParkingTime;
    }

    public void setMaxParkingTime(String maxParkingTime) {
        this.maxParkingTime = maxParkingTime;
    }

    public LatLng getPosition() {
        return position;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }

    public boolean getAdded(){
        return isAdded;
    }

    public void setAdded(boolean add){
        this.isAdded = add;
    }


}