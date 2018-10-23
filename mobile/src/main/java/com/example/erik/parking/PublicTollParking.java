package com.example.erik.parking;

public class PublicTollParking extends TollParking {

    //Instance variables
    private String parkingCharge;
    private String maxParkingTimeLimitation;
    private String type = "Allmän betalparkering";

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
                "Information: " + getMaxParkingTimeLimitation() + "\n" +
                "Parkeringskostnad: " + getParkingCharge() + "\n";
    }

    public String getType(){
        return type;
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