package com.s.technician_app.Services;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.s.technician_app.Common;
import com.s.technician_app.EventBus.TechnicianRequestReceived;
import com.s.technician_app.Utils.UserUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;
import java.util.Random;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            UserUtils.updateToken(this, s);
        }
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Map<String, String> dataReceived = remoteMessage.getData();
        if(dataReceived != null){
                Common.showNotification(this, new Random().nextInt(),
                        dataReceived.get(Common.NOTIFICATION_TITLE),
                        dataReceived.get(Common.NOTIFICATION_CONTENT),
                        null);
            if(dataReceived.get(Common.NOTIFICATION_TITLE).equals(Common.REQUEST_TECHNICIAN_TITLE)){

                EventBus.getDefault().postSticky(new TechnicianRequestReceived(
                        dataReceived.get(Common.RIDER_KEY),
                dataReceived.get(Common.PASSENGER_PICKUP_LOCATION)
                        ));
            } else {
                Common.showNotification(this, new Random().nextInt(),
                        dataReceived.get(Common.NOTIFICATION_TITLE),
                        dataReceived.get(Common.NOTIFICATION_CONTENT),
                        null);
            }
        }
    }
}
