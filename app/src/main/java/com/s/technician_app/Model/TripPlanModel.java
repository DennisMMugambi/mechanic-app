package com.s.technician_app.Model;

public class TripPlanModel {
    private String rider, technician;
    private TechnicianInfoModel technicianInfoModel;
    private RiderModel riderModel;
    private String origin, destination;
    private String distance, duration;
    private double currentLat, currentLong;
    private boolean isDone, isCancel;

    public TripPlanModel() {
    }

    public String getRider() {
        return rider;
    }

    public void setRider(String rider) {
        this.rider = rider;
    }

    public String getTechnician() {
        return technician;
    }

    public void setTechnician(String technician) {
        this.technician = technician;
    }

    public TechnicianInfoModel getTechnicianInfoModel() {
        return technicianInfoModel;
    }

    public void setTechnicianInfoModel(TechnicianInfoModel technicianInfoModel) {
        this.technicianInfoModel = technicianInfoModel;
    }

    public RiderModel getRiderModel() {
        return riderModel;
    }

    public void setRiderModel(RiderModel riderModel) {
        this.riderModel = riderModel;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public double getCurrentLat() {
        return currentLat;
    }

    public void setCurrentLat(double currentLat) {
        this.currentLat = currentLat;
    }

    public double getCurrentLong() {
        return currentLong;
    }

    public void setCurrentLong(double currentLong) {
        this.currentLong = currentLong;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    public boolean isCancel() {
        return isCancel;
    }

    public void setCancel(boolean cancel) {
        isCancel = cancel;
    }
}
