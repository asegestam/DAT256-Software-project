package com.example.erik.parking;

public class HandicapParking extends FreeParking {

    private String type = "Handikappsparkering";

    public HandicapParking(String name, String owner, String parkingSpaces, String maxParkingTime,
                           double lat, double lng, String maxParkingTimeLimitation) {
        super(name, owner, parkingSpaces, maxParkingTime, lat, lng, maxParkingTimeLimitation);
    }

    public String getType(){
        return type;
    }

}