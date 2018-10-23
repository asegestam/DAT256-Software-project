package com.example.erik.parking;

public class PrivateTollParking extends TollParking {

    private String type = "Privat betalparkering";

    public PrivateTollParking(String name, String owner, String parkingSpaces, String maxParkingTime,
                              double lat, double lng, String parkingCost, String currentParkingCost,
                              String phoneParkingCode) {
        super(name, owner, parkingSpaces, maxParkingTime, lat, lng, parkingCost, currentParkingCost, phoneParkingCode);
    }

    @Override
    public String getParkingInformation() {
        return super.getParkingInformation();
    }

    public String getType(){
        return type;
    }
}