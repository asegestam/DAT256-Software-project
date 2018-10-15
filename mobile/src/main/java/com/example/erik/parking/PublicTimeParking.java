package com.example.erik.parking;

public class PublicTimeParking extends FreeParking {


    public PublicTimeParking(String name, String owner, String parkingSpaces, String maxParkingTime,
                             double lat, double lng, String maxParkingTimeLimitaion) {
        super(name, owner, parkingSpaces, maxParkingTime,lat, lng, maxParkingTimeLimitaion);
    }
}