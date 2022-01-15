package com.s.technician_app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.s.technician_app.Model.TechnicianInfoModel;
import com.s.technician_app.Utils.UserUtils;

import java.util.Arrays;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;

public class LoginActivity extends AppCompatActivity {

    public static FirebaseAuth mAuth;
    private Button mLogin;
    private TextView mReset, mSign_up;
    private EditText mEmail, mPassword;
    private SignInButton mGoogleSignIn;
    private Button mPhoneLogin;
    private static final int RC_SIGN_IN = 1;
    private static final int LOGIN_REQUEST_CODE = 2700;
    String name, email;
    String idToken;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener listener;
    private GoogleSignInClient googleSignInClient;
    FirebaseDatabase database;
    DatabaseReference technicianInfoRef;
    ConstraintLayout constraintLayout;
    TechnicianInfoModel technicianInfoModelLogin;



    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(listener);;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //fetch auth instance
        mAuth = FirebaseAuth.getInstance();
        //mAuth = com.google.firebase.auth.FirebaseAuth.getInstance();


        //get reference to database
        database = FirebaseDatabase.getInstance();
        technicianInfoRef = database.getReference(Common.TECHNICIAN_INFO_REFERENCE);

        setContentView(R.layout.activity_login);
        mLogin = findViewById(R.id.btn_login);
        mReset = findViewById(R.id.forgot_password);
        mSign_up = findViewById(R.id.sign_up);
        mEmail = findViewById(R.id.username);
        mPassword = findViewById(R.id.password);
        mPhoneLogin = findViewById(R.id.btn_phone);
        constraintLayout = findViewById(R.id.login_constraint);
        showLoginLayout();

        if(mAuth.getCurrentUser() != null) {
            // startActivity(new Intent(getApplicationContext(), MapsActivity.class));
            //finish();
            //update token
            FirebaseInstanceId.getInstance()
                    .getInstanceId()
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                @Override
                public void onSuccess(InstanceIdResult instanceIdResult) {
                    Log.d("TOKEN", instanceIdResult.getToken());
                    UserUtils.updateToken(LoginActivity.this, instanceIdResult.getToken());
                }
            });
            checkUserFromFirebase();
        }
        init();
    }

    private void checkUserFromFirebase() {

        Snackbar.make(constraintLayout, "Checking your login status, please be patient", Snackbar.LENGTH_LONG).show();
        technicianInfoRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            //add a visual cue to let user know you're processing
                            //Toast.makeText(LoginActivity.this, "user already exists", Toast.LENGTH_SHORT).show();
                            Snackbar.make(constraintLayout, "Automatically logging you in", Snackbar.LENGTH_LONG).show();
                            TechnicianInfoModel technicianInfoModel = snapshot.getValue(TechnicianInfoModel.class);
                            goToHomeActivity(technicianInfoModel);
                        } else {
                            showRegisterLayout();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(LoginActivity.this, ""+ error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
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
                            Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }).addOnSuccessListener(aVoid -> {
                    Toast.makeText(LoginActivity.this, "You have been successfully registered",
                            Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    goToHomeActivity(model);
                });
            }
        });
    }
    private void init() {
        listener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                //Get currently signed in user
                FirebaseUser user = firebaseAuth.getCurrentUser();

                //if user is signed in, we call a helper method to save the user details to firebase

                if (user != null) {
                    //User is signed in
                    Log.d("error", "onAuthStateChanged:signed_in:" + user.getUid());

                } else {
                    //user is signed out
                    Log.d("error", "onAuthStateChanged:signed_out");
                }
            }
        };

        showLoginLayout();

    }

    private void goToHomeActivity(TechnicianInfoModel technicianInfoModel) {
        Common.currentUser = technicianInfoModel;
        //delaySplashScreen();
        startActivity(new Intent(LoginActivity.this, TechnicianHomeActivity.class));
        finish();
    }

    private void showLoginLayout() {
        mReset.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), ForgotPassActivity.class)));
        mSign_up.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), SignUpActivity.class)));
        mPhoneLogin.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), PhoneSignInActivity.class)));
//        mGoogleSignIn = findViewById(R.id.btn_google);
/*        mGoogleSignIn.setOnClickListener(v -> {

            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.web_client_id))
                    .requestEmail()
                    .build();

            googleSignInClient = GoogleSignIn.getClient(LoginActivity.this, gso);

            Intent intent = googleSignInClient.getSignInIntent();
            startActivityForResult(intent, RC_SIGN_IN);
        });*/
        mLogin.setOnClickListener(v -> {
            String email = mEmail.getText().toString();
            final String password = mPassword.getText().toString();

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(password)) {
                Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            //progressBar.setVisibility(View.GONE);
                            if (!task.isSuccessful()) {
                                // there was an error
                                if (password.length() < 6) {
                                    mPassword.setError(getString(R.string.minimum_password));
                                } else {
                                    Toast.makeText(LoginActivity.this, getString(R.string.auth_has_failed), Toast.LENGTH_LONG).show();
                                }
                            } else {

                               // TechnicianInfoModel technicianInfoModel = snapshot.getValue(TechnicianInfoModel.class);
                              //
                                technicianInfoRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if(snapshot.exists()){
                                                    //add a visual cue to let user know you're processing
                                                    //Toast.makeText(LoginActivity.this, "user already exists", Toast.LENGTH_SHORT).show();
                                                    Snackbar.make(constraintLayout, "Automatically logging you in", Snackbar.LENGTH_LONG).show();
                                                    TechnicianInfoModel technicianInfoModel = snapshot.getValue(TechnicianInfoModel.class);
                                                    goToHomeActivity(technicianInfoModel);
                                                } else {
                                                    //showRegisterLayout();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                Toast.makeText(LoginActivity.this, ""+ error.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                               // Intent intent = new Intent(LoginActivity.this, TechnicianHomeActivity.class);
                               // startActivity(intent);
                               // finish();
                            }
                        }
                    });
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
        if(requestCode == LOGIN_REQUEST_CODE)
        {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if(resultCode == RESULT_OK)
            {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            }
            else
            {
                Toast.makeText(this, "[ERROR]: " + response.getError().getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onStop() {
        if(firebaseAuth != null && listener != null)
            firebaseAuth.removeAuthStateListener(listener);
        super.onStop();
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            GoogleSignInAccount account = result.getSignInAccount();
            idToken = account.getIdToken();
            name = account.getDisplayName();
            email = account.getEmail();

            AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
            //firebaseAuthWithGoogle(credential);
        } else {
            //Google Sign In failed
            Log.e("m", "Login Unsuccessful." + result);
            Toast.makeText(this, "Login Unsuccessful" +  result.getStatus(), Toast.LENGTH_SHORT).show();

        }
    }

   /* private void firebaseAuthWithGoogle(AuthCredential credential) {

        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("m", "signInWithCredential:onComplete:" + task.isSuccessful());
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Login successful", Toast.LENGTH_SHORT).show();
                            gotoMain();
                        } else {
                            Log.w("err", "signInWithCredential" + task.getException().getMessage());
                            task.getException().printStackTrace();
                            Toast.makeText(getApplicationContext(), "Authentication failed", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }*/

    private void gotoMain(){
        Intent intent = new Intent(getApplicationContext(), TechnicianHomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }


}