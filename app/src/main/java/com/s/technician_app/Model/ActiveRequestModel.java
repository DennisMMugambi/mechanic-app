package com.s.technician_app.Model;

public class ActiveRequestModel {
    private String technicianFirstName, technicianLastName, technicianNumber,
            passengerFirstName, passengerLastName, passengerPhoneNumber;

    public ActiveRequestModel() {
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
}
