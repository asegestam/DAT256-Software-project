package com.example.erik.parking;

public class PublicTollParking extends TollParking {

    //Instance variables
    private String parkingCharge;
    private String maxParkingTimeLimitation;

    public PublicTollParking(String name, String owner, String parkingSpaces, String maxParkingTime,
                             double lat, double lng, String parkingCost, String parkingCharge, String currentParkingCost,
                             String phoneParkingCode, String maxParkingTimeLimitation) {
        super(name, owner, parkingSpaces, maxParkingTime, lat, lng, parkingCost,
                currentParkingCost, phoneParkingCode);
        this.maxParkingTimeLimitation = maxParkingTimeLimitation;
        this.parkingCharge = parkingCharge;
    }

    @Override
    public String getParkingInformation() {
        return super.getParkingInformation() +
                "MaxParkingTimeLimitation: " + getMaxParkingTimeLimitation() + "\n" +
                "ParkingCharge: " + getParkingCharge() + "\n";
    }

    public String getMaxParkingTimeLimitation() {
        return maxParkingTimeLimitation;
    }

    public void setMaxParkingTimeLimitation(String maxParkingTimeLimitation) {
        this.maxParkingTimeLimitation = maxParkingTimeLimitation;
    }

    public String getParkingCharge() {
        return parkingCharge;
    }

    public void setParkingCharge(String parkingCharge) {
        this.parkingCharge = parkingCharge;
    }
}