package com.s.technician_app.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.s.technician_app.Common;
import com.s.technician_app.POJOs.FaultsPojo;
import com.s.technician_app.POJOs.LiftsCommissionedPojo;
import com.s.technician_app.POJOs.ReviewsPojo;
import com.s.technician_app.R;
import com.s.technician_app.ui.Faults.FaultsFragment;
import com.s.technician_app.ui.Faults.FaultsViewModel;


public class LiftsCommissionedFragment extends Fragment {

    private DatabaseReference mRef, technicianRef;
    private RecyclerView mRecyclerView;
    private String technician_key;
    private FirebaseDatabase database;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_lifts_commissioned, container, false);
        mRef = Common.FIREBASE_TECHNICIAN_REFERENCE;
        Toast.makeText(root.getContext(), Common.FIREBASE_TECHNICIAN_REFERENCE.toString(), Toast.LENGTH_LONG).show();
        mRecyclerView = (RecyclerView) root.findViewById(R.id.lifts_commissioned_rv);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<LiftsCommissionedPojo, LiftsCommissionedFragment.MyViewHolder> adapter = new FirebaseRecyclerAdapter<LiftsCommissionedPojo, LiftsCommissionedFragment.MyViewHolder>(
                LiftsCommissionedPojo .class,
                R.layout.reviews_layout,
                LiftsCommissionedFragment.MyViewHolder.class,
                mRef
        ) {


            @Override
            protected void populateViewHolder(LiftsCommissionedFragment.MyViewHolder myViewHolder, LiftsCommissionedPojo liftsCommissionedPojo, int i) {
                myViewHolder.setupViews(liftsCommissionedPojo.getLiftCommissioned());
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
        void setupViews(String lifts_commissioned){
            TextView liftsCommisioned = (TextView) mView.findViewById(R.id.review_text_view);


            liftsCommisioned.setText(lifts_commissioned);

        }
    }
}