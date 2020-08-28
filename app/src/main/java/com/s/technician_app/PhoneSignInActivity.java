package com.s.technician_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;


public class PhoneSignInActivity extends AppCompatActivity {

    private Button proceed;
    private EditText phoneNumber;
    private CountryCodePicker ccp;
    private String number;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_sign_in);

        ccp = findViewById(R.id.cpp);
        phoneNumber = findViewById(R.id.editText);
        proceed = findViewById(R.id.saved_phone_number);

        auth = FirebaseAuth.getInstance();
        init();
    }

    private void init() {
        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                number = "+" + ccp.getSelectedCountryCode() + phoneNumber.getText().toString();
                //number = phoneNumber.getText().toString();
                Log.d("number", number);

                if (number.isEmpty() || number.length() < 10) {
                    phoneNumber.setError("Enter a valid mobile number");
                    phoneNumber.requestFocus();
                    return;
                }
                Intent intent = new Intent(getApplicationContext(), PhoneVerificationActivity.class);
                intent.putExtra("mobile_number", number);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}