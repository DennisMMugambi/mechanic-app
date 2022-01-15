package com.s.technician_app.ui.Faults;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.s.technician_app.POJOs.FaultsPojo;
import com.s.technician_app.R;
import com.s.technician_app.ui.home.HomeViewModel;

public class FaultsFragment extends Fragment {


    private DatabaseReference mRef;
    private RecyclerView mRecyclerView;
    private FaultsViewModel FaultsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        FaultsViewModel = new ViewModelProvider(this).get(FaultsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_slideshow, container, false);
        mRef = FirebaseDatabase.getInstance().getReference().child("faults");
        mRecyclerView = (RecyclerView) root.findViewById(R.id.faults_rv);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<FaultsPojo, FaultsFragment.MyViewHolder> adapter = new FirebaseRecyclerAdapter<FaultsPojo, FaultsFragment.MyViewHolder>(
                FaultsPojo .class,
                R.layout.faults_layout,
                FaultsFragment.MyViewHolder.class,
                mRef
        ) {


            @Override
            protected void populateViewHolder(FaultsFragment.MyViewHolder myViewHolder, FaultsPojo faultyPojo, int i) {
                myViewHolder.setupViews(faultyPojo.getCompanyName(), faultyPojo.getFault());
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
        void setupViews(String Cname, String faultz){
            TextView companyName = (TextView) mView.findViewById(R.id.buildingName);
            TextView fault = (TextView) mView.findViewById(R.id.fault);


            companyName.setText(Cname);
            fault.setText(faultz);

        }
    }
}