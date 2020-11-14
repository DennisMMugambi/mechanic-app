package com.s.technician_app.ui.SignOut;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.s.technician_app.LoginActivity;
import com.s.technician_app.R;


public class SignOutFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_sign_out, container, false);

                    AlertDialog.Builder builder = new AlertDialog.Builder(root.getContext());
                    builder.setTitle("Sign out")
                            .setMessage("Confirm you wish to sign out")
                            .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).setPositiveButton("Sign out", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FirebaseAuth.getInstance().signOut();
                            Intent intent = new Intent(root.getContext(), LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                           // root.getContext().finish();
                        }
                    })
                            .setCancelable(false);
                    AlertDialog dialog = builder.create();
                    dialog.setOnShowListener(dialogInterface -> {
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                                .setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                                .setTextColor(getResources().getColor(R.color.colorAccent));
                    });
                    dialog.show();

             //   if(item.getItemId() == R.id.nav_faults){
               //     startActivity(new Intent(TechnicianHomeActivity.this, FaultsFragment.class));
                //}
                //if(item.getItemId() == R.id.nav_profile){
                  //  startActivity(new Intent(TechnicianHomeActivity.this, ProfileFragment.class));
               // }
        return root;
    }
}