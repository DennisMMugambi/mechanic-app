package com.s.technician_app;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.s.technician_app.Model.TechnicianInfoModel;
import com.s.technician_app.Services.MyFirebaseMessagingService;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Common {
    public static final String TECHNICIAN_INFO_REFERENCE = "TechnicianInfo";
    public static final String TECHNICIAN_LOCATION_REFERENCES = "technicians_location";
    public static final String TOKEN_REFERENCE = "TOKEN";
    public static final String NOTIFICATION_TITLE = "title";
    public static final String NOTIFICATION_CONTENT = "body";
    public static final String PASSENGER_PICKUP_LOCATION = "pickupLocation";
    public static int numRequests;
    public static final String RIDER_KEY = "Rider key";
    public static final String REQUEST_TECHNICIAN_TITLE = "You have a request!";
    public static final String TECHNICIAN_KEY = "TechnicianKey";
    public static final String REQUEST_TECHNICIAN_DECLINE = "Decline";
    public static final String ACTIVE_REQUESTS_REF = "Active requests";
    public static final String RIDER_INFO_REFERENCE = "riders";
    public static final String CONFIRM_TECHNICIAN_ACCEPT = "Accept";
    public static final String TRIP_KEY = "TripKey";
    public static final String TECHNICIAN_COMPLETE_REPAIR = "Repair complete";
    public static DatabaseReference FIREBASE_TECHNICIAN_REFERENCE = null;
    public static DatabaseReference FIREBASE_TECHNICIAN_REVIEW_REFERENCE = null;

    public static TechnicianInfoModel currentUser;
    public static String Trip = "Trips";

    public static String buildWelcomeMessage() {

        if(Common.currentUser != null)
        return new StringBuilder("Welcome ")
                .append(Common.currentUser.getFirstName())
                .append(" ")
                .append(Common.currentUser.getLastName()).toString();
        else
            return "";
    }

    public static List<LatLng> decodePoly(String encoded) {
        List poly = new ArrayList();
        int index=0,len=encoded.length();
        int lat=0,lng=0;
        while(index < len)
        {
            int b,shift=0,result=0;
            do{
                b=encoded.charAt(index++)-63;
                result |= (b & 0x1f) << shift;
                shift+=5;

            }while(b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1):(result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do{
                b = encoded.charAt(index++)-63;
                result |= (b & 0x1f) << shift;
                shift +=5;
            }while(b >= 0x20);
            int dlng = ((result & 1)!=0 ? ~(result >> 1): (result >> 1));
            lng +=dlng;

            LatLng p = new LatLng((((double)lat / 1E5)),
                    (((double)lng/1E5)));
            poly.add(p);
        }
        return poly;
    }

    public static void showNotification(Context context, int id, String title, String body, Intent intent) {
        PendingIntent pendingIntent = null;
        if(intent != null)
            pendingIntent = PendingIntent.getActivity(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        String NOTIFICATION_CHANNEL_ID = "passenger_requests";
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    "passenger_requests", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription("requests");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);

            notificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder builder= new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID);
        builder.setContentText(title)
                .setAutoCancel(false)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setSmallIcon(R.drawable.ic_person_pin)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_person_pin));
        if(pendingIntent != null)
        {
            builder.setContentIntent(pendingIntent);
        }
        Notification notification = builder.build();
        notificationManager.notify(id, notification);
    }

    public static String createUniqueTripIdNumber(long timeOffset) {
        Random random = new Random();
        Long current = System.currentTimeMillis() + timeOffset;
        Long unique = current + random.nextLong();
        if(unique < 0) unique*=(-1);
        return String.valueOf(unique);
    }
}
