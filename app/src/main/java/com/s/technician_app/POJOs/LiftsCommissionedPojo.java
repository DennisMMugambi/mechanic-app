package com.s.technician_app.POJOs;

public class LiftsCommissionedPojo {
  private String liftCommissioned;

    public LiftsCommissionedPojo(String liftCommissioned) {
        this.liftCommissioned = liftCommissioned;
    }

    public LiftsCommissionedPojo() {
    }

    public String getLiftCommissioned() {
        return liftCommissioned;
    }

    public void setLiftCommissioned(String liftCommissioned) {
        this.liftCommissioned = liftCommissioned;
    }
}
