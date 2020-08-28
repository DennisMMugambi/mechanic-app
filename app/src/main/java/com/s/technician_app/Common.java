package com.s.technician_app;

import com.s.technician_app.Model.TechnicianInfoModel;

public class Common {
    public static final String TECHNICIAN_INFO_REFERENCE = "TechnicianInfo";
    public static final String TECHNICIAN_LOCATION_REFERENCES = "technicians_location";

    public static TechnicianInfoModel currentUser;

    public static String buildWelcomeMessage() {

        if(Common.currentUser != null)
        return new StringBuilder("Welcome ")
                .append(Common.currentUser.getFirstName())
                .append(" ")
                .append(Common.currentUser.getLastName()).toString();
        else
            return "";
    }
}
