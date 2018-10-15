package com.example.erik.parking;

import com.google.android.gms.maps.model.LatLng;

public class Parking {

    //Instance Variabels
    private String name;
    private String owner;
    private String parkingSpaces;
    private String maxParkingTime;
    private LatLng position;

    public Parking(String name, String owner, String parkingSpaces, String maxParkingTime, double lat, double lng){
        this.name = name;
        this.owner = owner;
        this.parkingSpaces = parkingSpaces;
        this.maxParkingTime = maxParkingTime;
        position = new LatLng(lat, lng);

    }

    public String getParkingInformation() {
        return  "Type of parking: " + this.getClass().getName() + "\n" +
                "Owner: " + getOwner() + "\n" +
                "ParkingSpaces: " + getParkingSpaces() + "\n" +
                "MaxParkingTime: " + getMaxParkingTime() + "\n";
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


}