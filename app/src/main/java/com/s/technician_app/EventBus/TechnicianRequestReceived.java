package com.s.technician_app.EventBus;

public class TechnicianRequestReceived {
    private String key, pickupLocation;

    public TechnicianRequestReceived(String key, String pickupLocation) {
        this.key = key;
        this.pickupLocation = pickupLocation;
    }

    public TechnicianRequestReceived() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(String pickupLocation) {
        this.pickupLocation = pickupLocation;
    }
}
