package com.s.technician_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TechnicianListActivity extends AppCompatActivity {

    private DatabaseReference mRef;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_technician_list);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mRef = FirebaseDatabase.getInstance().getReference().child("TechnicianInfo");
        //mRecyclerView = (RecyclerView) findViewById(R.id.technicians_rv);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<FirebasePojo, MyViewHolder> adapter = new FirebaseRecyclerAdapter<FirebasePojo, MyViewHolder>(
                FirebasePojo .class,
                R.layout.fb_recycler_view,
                MyViewHolder .class,
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
    static class MyViewHolder extends RecyclerView.ViewHolder{
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