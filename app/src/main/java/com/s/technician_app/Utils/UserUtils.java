package com.s.technician_app.Utils;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.s.technician_app.Common;
import com.s.technician_app.Model.TokenModel;
import com.s.technician_app.Services.MyFirebaseMessagingService;

import java.util.Map;

public class UserUtils {
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
}
