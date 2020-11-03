package com.s.technician_app.Model;

import com.firebase.geofire.GeoLocation;

public class PassengerGeoModel {
    private String key;
    private GeoLocation geoLocation;
    private PassengerInfoModel passengerInfoModel;

    public PassengerGeoModel() {
    }

    public PassengerGeoModel(String key, GeoLocation geoLocation) {
        this.key = key;
        this.geoLocation = geoLocation;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public GeoLocation getGeoLocation() {
        return geoLocation;
    }

    public void setGeoLocation(GeoLocation geoLocation) {
        this.geoLocation = geoLocation;
    }

    public PassengerInfoModel getPassengerInfoModel() {
        return passengerInfoModel;
    }

    public void setPassengerInfoModel(PassengerInfoModel passengerInfoModel) {
        this.passengerInfoModel = passengerInfoModel;
    }
}
