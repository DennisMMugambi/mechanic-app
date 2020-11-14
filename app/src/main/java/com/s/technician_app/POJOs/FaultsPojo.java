package com.s.technician_app.POJOs;

public class FaultsPojo {
    private String companyName, fault;

    public FaultsPojo(String companyName, String fault) {
        this.companyName = companyName;
        this.fault = fault;
    }


    public FaultsPojo() {
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getFault() {
        return fault;
    }

    public void setFaulty(String fault) {
        this.fault = fault;
    }
}
