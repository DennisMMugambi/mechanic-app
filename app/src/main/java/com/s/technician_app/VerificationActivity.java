package com.s.technician_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.s.technician_app.Model.TechnicianInfoModel;

import static com.s.technician_app.SignUpActivity.mUser;

public class VerificationActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Button mProceed, mResend;
    private String email,password;
    DatabaseReference technicianInfoRef;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);
        mAuth = FirebaseAuth.getInstance();

        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        password = intent.getStringExtra("password");

        mProceed = findViewById(R.id.btn_proceed);
        mResend = findViewById(R.id.btn_resend);

        database = FirebaseDatabase.getInstance();
        technicianInfoRef = database.getReference(Common.TECHNICIAN_INFO_REFERENCE);
        
        mProceed.setOnClickListener(v -> {

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(VerificationActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            //progressBar.setVisibility(View.GONE);
                            if (!task.isSuccessful()) {
                                // there was an error
                                if (password.length() < 6) {
                                    //mPassword.setError(getString(R.string.minimum_password));
                                } else {
                                    Toast.makeText(VerificationActivity.this, getString(R.string.auth_has_failed), Toast.LENGTH_LONG).show();
                                }
                            } else {
                                verifyemail();
                            }
                        }
                    });
        });
        mResend.setOnClickListener(v -> {
            assert mUser != null;
            mUser.sendEmailVerification().addOnSuccessListener(aVoid -> Toast.makeText(getApplicationContext(),
                    "check your email address to verify your account",
                    Toast.LENGTH_LONG).show()).addOnFailureListener(e -> Log.d("Error",
                    "Email address not sent" + e.getMessage()));
        });
    }

    private void verifyemail() {
        FirebaseUser user = mAuth.getCurrentUser();
        assert user != null;
        if(user.isEmailVerified()){
            //startActivity(new Intent(getApplicationContext(), TechnicianHomeActivity.class));
            showRegisterLayout();
        } else {
            Toast.makeText(getApplicationContext(),
                    "You have not verified your email, try again or hit resend for a new link",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void showRegisterLayout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DialogTheme);
        View itemView = LayoutInflater.from(this).inflate(R.layout.layout_register_activity, null);

        TextInputEditText edt_first_name = (TextInputEditText)itemView.findViewById(R.id.edt_first_name);
        TextInputEditText edt_last_name = (TextInputEditText)itemView.findViewById(R.id.edt_last_name);
        TextInputEditText edt_phone_number = (TextInputEditText)itemView.findViewById(R.id.edt_phone_number);

        Button btn_continue = (Button)itemView.findViewById(R.id.btn_register_);

        //setData
        if(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber() != null
                && !TextUtils.isEmpty(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()))
            edt_phone_number.setText(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());

        //set view
        builder.setView(itemView);
        AlertDialog dialog = builder.create();
        dialog.show();

        btn_continue.setOnClickListener(v -> {
            if(TextUtils.isEmpty(edt_first_name.getText().toString()))
            {
                Toast.makeText(this, "Please enter first name", Toast.LENGTH_SHORT).show();
                return;
            } else if(TextUtils.isEmpty(edt_last_name.getText().toString()))
            {
                Toast.makeText(this, "Please enter last name", Toast.LENGTH_SHORT).show();
                return;
            } else if(TextUtils.isEmpty(edt_phone_number.getText().toString()))
            {
                Toast.makeText(this, "Please enter phone number", Toast.LENGTH_SHORT).show();
                return;
            } else {
                TechnicianInfoModel model = new TechnicianInfoModel();
                model.setFirstName(edt_first_name.getText().toString());
                model.setLastName(edt_last_name.getText().toString());
                model.setPhoneNumber(edt_phone_number.getText().toString());
                model.setRating(0.0);

                technicianInfoRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .setValue(model)
                        .addOnFailureListener(e -> {
                            dialog.dismiss();
                            Toast.makeText(VerificationActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }).addOnSuccessListener(aVoid -> {
                    Toast.makeText(VerificationActivity.this, "You have been successfully registered",
                            Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    goToHomeActivity(model);
                });
            }
        });
    }

    private void goToHomeActivity(TechnicianInfoModel technicianInfoModel) {
        Common.currentUser = technicianInfoModel;
        //delaySplashScreen();
        startActivity(new Intent(VerificationActivity.this, TechnicianHomeActivity.class));
        finish();
    }
}