package com.s.technician_app.Utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.s.technician_app.Common;
import com.s.technician_app.Model.FCMSendData;
import com.s.technician_app.Model.TokenModel;
import com.s.technician_app.R;
import com.s.technician_app.Remote.IFCMService;
import com.s.technician_app.Remote.RetrofitFCMClient;
import com.s.technician_app.Services.MyFirebaseMessagingService;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class UserUtils {
    private static final String ACTIVE_REQUEST_NOTIFICATION_ID = "77717";
    private static final String ACTIVE_REQUEST_NOTIFICATION_CHANNEL_ID = "20640";
    private static final int ACTIVE_REQUEST_PENDING_INTENT_ID = 3417;

    public static void updateUser(View view, Map<String, Object> updateData) {
        FirebaseDatabase.getInstance()
                .getReference(Common.TECHNICIAN_INFO_REFERENCE)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .updateChildren(updateData)
                .addOnFailureListener(e -> Snackbar.make(view, e.getMessage(), Snackbar.LENGTH_SHORT).show()).addOnSuccessListener(aVoid -> Snackbar.make(view, "Your information has been successfully updated", Snackbar.LENGTH_SHORT).show());
    }

    public static void updateToken(Context context, String token) {
        TokenModel tokenModel = new TokenModel(token);

        FirebaseDatabase.getInstance()
                .getReference(Common.TOKEN_REFERENCE)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
            .setValue(tokenModel)
                .addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }).addOnSuccessListener(new OnSuccessListener<Void>() {
        @Override
        public void onSuccess(Void aVoid) {

        }
    });
}

    public static void sendDeclineRequest(View view, Context context, String key) {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        IFCMService ifcmService = RetrofitFCMClient.getInstance().create(IFCMService.class);

        FirebaseDatabase
                .getInstance()
                .getReference(Common.TOKEN_REFERENCE)
                .child(key)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){

                            TokenModel tokenModel = snapshot.getValue(TokenModel.class);

                            Map<String, String> notificationData = new HashMap<>();
                            notificationData.put(Common.NOTIFICATION_TITLE, Common.REQUEST_TECHNICIAN_DECLINE);
                            notificationData.put(Common.NOTIFICATION_CONTENT, "This message represents technician decline action");
                            notificationData.put(Common.TECHNICIAN_KEY, FirebaseAuth.getInstance().getCurrentUser().getUid());

                            if(tokenModel.getToken() != null) {
                                FCMSendData fcmSendData = new FCMSendData(tokenModel.getToken(), notificationData);
                                Log.d("token", tokenModel.getToken());

                                compositeDisposable.add(ifcmService.sendNotification(fcmSendData)
                                        .subscribeOn(Schedulers.newThread())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(fcmResponse -> {
                                            if(fcmResponse.getSuccess() == 0)
                                            {
                                                compositeDisposable.clear();
                                                Snackbar.make(view , context.getString(R.string.Decline_failed), Snackbar.LENGTH_LONG).show();
                                            }
                                            else {
                                                Snackbar.make(view , context.getString(R.string.Decline_success), Snackbar.LENGTH_LONG).show();
                                            }

                                        }, throwable -> {

                                            compositeDisposable.clear();
                                            Snackbar.make(view, throwable.getMessage(), Snackbar.LENGTH_LONG).show();
                                        }));}
                            else {
                                Log.d("Token", "no token provided");
                            }

                        } else {
                            compositeDisposable.clear();
                            Snackbar.make(view, context.getString(R.string.token_not_found), Snackbar.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        compositeDisposable.clear();
                        Snackbar.make(view, error.getMessage(), Snackbar.LENGTH_LONG).show();
                    }
                });
    }

    public static void sendNotificationToCallCenter(Context context) {
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        // COMPLETED (9) Create a notification channel for Android O devices
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    ACTIVE_REQUEST_NOTIFICATION_ID,
                    context.getString(R.string.active_request_channel_name),
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(mChannel);
        }
        // COMPLETED (10) In the remindUserBecauseCharging method use NotificationCompat.Builder to create a notification
        // that:
        // - has a color of R.color.colorPrimary - use ContextCompat.getColor to get a compatible color
        // - has ic_drink_notification as the small icon
        // - uses icon returned by the largeIcon helper method as the large icon
        // - sets the title to the charging_reminder_notification_title String resource
        // - sets the text to the charging_reminder_notification_body String resource
        // - sets the style to NotificationCompat.BigTextStyle().bigText(text)
        // - sets the notification defaults to vibrate
        // - uses the content intent returned by the contentIntent helper method for the contentIntent
        // - automatically cancels the notification when the notification is clicked
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context,ACTIVE_REQUEST_NOTIFICATION_CHANNEL_ID)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setSmallIcon(R.drawable.ic_person_pin)
                .setLargeIcon(largeIcon(context))
                .setContentTitle(context.getString(R.string.new_active_request))
                .setContentText(context.getString(R.string.click_to_view_request))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(
                        context.getString(R.string.click_to_view_request)))
                .setDefaults(Notification.DEFAULT_VIBRATE)
//                .setContentIntent(contentIntent(context))
                .setAutoCancel(true);

        // COMPLETED (11) If the build version is greater than or equal to JELLY_BEAN and less than OREO,
        // set the notification's priority to PRIORITY_HIGH.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }

        // COMPLETED (12) Trigger the notification by calling notify on the NotificationManager.
        // Pass in a unique ID of your choosing for the notification and notificationBuilder.build()
        notificationManager.notify(Integer.parseInt(ACTIVE_REQUEST_NOTIFICATION_ID), notificationBuilder.build());
    }

    private static Bitmap largeIcon(Context context) {
        // COMPLETED (5) Get a Resources object from the context.
        Resources res = context.getResources();
        // COMPLETED (6) Create and return a bitmap using BitmapFactory.decodeResource, passing in the
        // resources object and R.drawable.ic_local_drink_black_24px
        Bitmap largeIcon = BitmapFactory.decodeResource(res, R.drawable.ic_person_pin);
        return largeIcon;
    }

    public static void sendAcceptConfirmationToRider(View view, Context context, String key, String tripNumberId) {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        IFCMService ifcmService = RetrofitFCMClient.getInstance().create(IFCMService.class);

        FirebaseDatabase
                .getInstance()
                .getReference(Common.TOKEN_REFERENCE)
                .child(key)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){

                            TokenModel tokenModel = snapshot.getValue(TokenModel.class);

                            Map<String, String> notificationData = new HashMap<>();
                            notificationData.put(Common.NOTIFICATION_TITLE, Common.CONFIRM_TECHNICIAN_ACCEPT);
                            notificationData.put(Common.NOTIFICATION_CONTENT, "This message represents technician accept action");
                            notificationData.put(Common.TECHNICIAN_KEY, FirebaseAuth.getInstance().getCurrentUser().getUid());
                            notificationData.put(Common.TRIP_KEY, tripNumberId);

                            if(tokenModel.getToken() != null) {
                                FCMSendData fcmSendData = new FCMSendData(tokenModel.getToken(), notificationData);
                                Log.d("token", tokenModel.getToken());

                                compositeDisposable.add(ifcmService.sendNotification(fcmSendData)
                                        .subscribeOn(Schedulers.newThread())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(fcmResponse -> {
                                            if(fcmResponse.getSuccess() == 0)
                                            {
                                                compositeDisposable.clear();
                                                Snackbar.make(view , context.getString(R.string.Accept_failed), Snackbar.LENGTH_LONG).show();
                                            }

                                        }, throwable -> {

                                            compositeDisposable.clear();
                                            Snackbar.make(view, throwable.getMessage(), Snackbar.LENGTH_LONG).show();
                                        }));}
                            else {
                                Log.d("Token", "no token provided");
                            }

                        } else {
                            compositeDisposable.clear();
                            Snackbar.make(view, context.getString(R.string.token_not_found), Snackbar.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        compositeDisposable.clear();
                        Snackbar.make(view, error.getMessage(), Snackbar.LENGTH_LONG).show();
                    }
                });
    }

    public static void sendCompleteTripToRider(View view, Context context, String key, String tripNumberId) {

        CompositeDisposable compositeDisposable = new CompositeDisposable();
        IFCMService ifcmService = RetrofitFCMClient.getInstance().create(IFCMService.class);

        FirebaseDatabase
                .getInstance()
                .getReference(Common.TOKEN_REFERENCE)
                .child(key)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){

                            TokenModel tokenModel = snapshot.getValue(TokenModel.class);

                            Map<String, String> notificationData = new HashMap<>();
                            notificationData.put(Common.NOTIFICATION_TITLE, Common.TECHNICIAN_COMPLETE_REPAIR);
                            notificationData.put(Common.NOTIFICATION_CONTENT, "This message represents technician complete action");
                            notificationData.put(Common.TECHNICIAN_KEY, FirebaseAuth.getInstance().getCurrentUser().getUid());

                            if(tokenModel.getToken() != null) {
                                FCMSendData fcmSendData = new FCMSendData(tokenModel.getToken(), notificationData);
                                Log.d("token", tokenModel.getToken());

                                compositeDisposable.add(ifcmService.sendNotification(fcmSendData)
                                        .subscribeOn(Schedulers.newThread())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(fcmResponse -> {
                                            if(fcmResponse.getSuccess() == 0)
                                            {
                                                compositeDisposable.clear();
                                                Snackbar.make(view , context.getString(R.string.Complete_failed), Snackbar.LENGTH_LONG).show();
                                            }
                                            else {
                                                Snackbar.make(view , context.getString(R.string.Complete_success), Snackbar.LENGTH_LONG).show();
                                            }

                                        }, throwable -> {

                                            compositeDisposable.clear();
                                            Snackbar.make(view, throwable.getMessage(), Snackbar.LENGTH_LONG).show();
                                        }));}
                            else {
                                Log.d("Token", "no token provided");
                            }

                        } else {
                            compositeDisposable.clear();
                            Snackbar.make(view, context.getString(R.string.token_not_found), Snackbar.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        compositeDisposable.clear();
                        Snackbar.make(view, error.getMessage(), Snackbar.LENGTH_LONG).show();
                    }
                });
    }
}
