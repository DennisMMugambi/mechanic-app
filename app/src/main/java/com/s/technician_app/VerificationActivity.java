package com.s.technician_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.s.technician_app.SignUpActivity.mUser;

public class VerificationActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Button mProceed, mResend;
    private String email,password;

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
                                    Toast.makeText(VerificationActivity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
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
            startActivity(new Intent(getApplicationContext(), TechnicianHomeActivity.class));
        } else {
            Toast.makeText(getApplicationContext(),
                    "You have not verified your email, try again or hit resend for a new link",
                    Toast.LENGTH_LONG).show();
        }
    }
}