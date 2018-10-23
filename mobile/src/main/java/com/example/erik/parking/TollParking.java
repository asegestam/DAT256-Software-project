package com.example.erik.parking;

public class TollParking extends Parking {

    //Instance Variables
    private String parkingCost;
    private String currentParkingCost;
    private String phoneParkingCode;



    public TollParking(String name, String owner, String parkingSpaces, String maxParkingTime,
                       double lat, double lng, String parkingCost,
                       String currentParkingCost, String phoneParkingCode) {
        super(name, owner, parkingSpaces, maxParkingTime, lat, lng, false);
        this.parkingCost = parkingCost;
        this.currentParkingCost = currentParkingCost;
        this.phoneParkingCode = phoneParkingCode;
    }

    @Override
    public String getParkingInformation() {
        return super.getParkingInformation() +
                "Parkeringskostnad: " + getParkingCost() + "\n" +
                "Telefonkod: " + getPhoneParkingCode() + "\n";
    }

    public String getParkingCost() {
        return parkingCost;
    }

    public void setParkingCost(String parkingCost) {
        this.parkingCost = parkingCost;
    }

    public String getCurrentParkingCost() {
        return currentParkingCost;
    }

    public void setCurrentParkingCost(String currentParkingCost) {
        this.currentParkingCost = currentParkingCost;
    }

    public String getPhoneParkingCode() {
        return phoneParkingCode;
    }

    public void setPhoneParkingCode(String phoneParkingCode) {
        this.phoneParkingCode = phoneParkingCode;
    }


}