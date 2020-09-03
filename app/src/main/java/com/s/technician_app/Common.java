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

import com.s.technician_app.Model.TechnicianInfoModel;
import com.s.technician_app.Services.MyFirebaseMessagingService;

public class Common {
    public static final String TECHNICIAN_INFO_REFERENCE = "TechnicianInfo";
    public static final String TECHNICIAN_LOCATION_REFERENCES = "technicians_location";
    public static final String TOKEN_REFERENCE = "TOKEN";
    public static final String NOTIFICATION_TITLE = "title";
    public static final String NOTIFICATION_CONTENT = "body";

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
}
