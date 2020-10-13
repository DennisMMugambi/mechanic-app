package com.s.technician_app.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.s.technician_app.FirebasePojo;
import com.s.technician_app.R;
import com.s.technician_app.TechnicianListActivity;


public class TechniciansFragment extends Fragment {
    private DatabaseReference mRef;
    private RecyclerView mRecyclerView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_technicians2, container, false);
        mRef = FirebaseDatabase.getInstance().getReference().child("TechnicianInfo");
        mRecyclerView = (RecyclerView) root.findViewById(R.id.technicians_rv);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<FirebasePojo, MyViewHolder> adapter = new FirebaseRecyclerAdapter<FirebasePojo, MyViewHolder>(
                FirebasePojo .class,
                R.layout.fb_recycler_view,
                MyViewHolder.class,
                mRef
        ) {


            @Override
            protected void populateViewHolder(MyViewHolder myViewHolder, FirebasePojo firebasePojo, int i) {
                myViewHolder.setupViews(firebasePojo.getFirstName(), firebasePojo.getLastName(), firebasePojo.getPhoneNumber(),
                        firebasePojo.getRating());
            }
        };
        mRecyclerView.setAdapter(adapter);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        private View mView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }
        void setupViews(String firstName,String lastName, String phoneNumber, double rating){
            TextView fName = (TextView) mView.findViewById(R.id.firstName);
            TextView lName = (TextView) mView.findViewById(R.id.lastName);
            TextView phNumber = (TextView) mView.findViewById(R.id.phoneNumber);
            TextView rting = (TextView) mView.findViewById(R.id.rating);

            fName.setText(firstName);
            lName.setText(lastName);
            phNumber.setText(phoneNumber);
            rting.setText(String.valueOf(rating));
        }
    }
}