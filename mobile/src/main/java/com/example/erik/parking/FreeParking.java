package com.example.erik.parking;

public class FreeParking extends Parking {

    //Instance variables
    private String maxParkingTimeLimitation;

    public FreeParking(String name, String owner, String parkingSpaces, String maxParkingTime,
                       double lat, double lng, String maxParkingTimeLimitation) {
        super(name, owner, parkingSpaces, maxParkingTime, lat, lng);
        this.maxParkingTimeLimitation = maxParkingTimeLimitation;
    }

    @Override
    public String getParkingInformation() {
        return super.getParkingInformation() +
                "MaxParkingTimeLimitation: " + getMaxParkingTimeLimitation() + "\n";
    }

    public String getMaxParkingTimeLimitation() {
        return maxParkingTimeLimitation;
    }

    public void setMaxParkingTimeLimitation(String maxParkingTimeLimitation) {
        this.maxParkingTimeLimitation = maxParkingTimeLimitation;
    }
}