package com.s.technician_app;

public class ActiveRequestPojo {
    private String passengerFirstName, passengerLastName, passengerPhoneNumber,
    technicianFirstName, technicianLastName, technicianNumber;


    public ActiveRequestPojo(String passengerFirstName, String passengerLastName, String passengerPhoneNumber, String technicianFirstName, String technicianLastName, String technicianNumber) {
        this.passengerFirstName = passengerFirstName;
        this.passengerLastName = passengerLastName;
        this.passengerPhoneNumber = passengerPhoneNumber;
        this.technicianFirstName = technicianFirstName;
        this.technicianLastName = technicianLastName;
        this.technicianNumber = technicianNumber;
    }

    public ActiveRequestPojo() {
    }

    public String getPassengerFirstName() {
        return passengerFirstName;
    }

    public void setPassengerFirstName(String passengerFirstName) {
        this.passengerFirstName = passengerFirstName;
    }

    public String getPassengerLastName() {
        return passengerLastName;
    }

    public void setPassengerLastName(String passengerLastName) {
        this.passengerLastName = passengerLastName;
    }

    public String getPassengerPhoneNumber() {
        return passengerPhoneNumber;
    }

    public void setPassengerPhoneNumber(String passengerPhoneNumber) {
        this.passengerPhoneNumber = passengerPhoneNumber;
    }

    public String getTechnicianFirstName() {
        return technicianFirstName;
    }

    public void setTechnicianFirstName(String technicianFirstName) {
        this.technicianFirstName = technicianFirstName;
    }

    public String getTechnicianLastName() {
        return technicianLastName;
    }

    public void setTechnicianLastName(String technicianLastName) {
        this.technicianLastName = technicianLastName;
    }

    public String getTechnicianNumber() {
        return technicianNumber;
    }

    public void setTechnicianNumber(String technicianNumber) {
        this.technicianNumber = technicianNumber;
    }
}
