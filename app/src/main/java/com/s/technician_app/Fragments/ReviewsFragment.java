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
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.s.technician_app.Common;
import com.s.technician_app.POJOs.LiftsCommissionedPojo;
import com.s.technician_app.POJOs.ReviewsPojo;
import com.s.technician_app.R;


public class ReviewsFragment extends Fragment {
    private DatabaseReference mRef, technicianRef;
    private RecyclerView mRecyclerView;
    private String technician_key;
    private FirebaseDatabase database;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_reviews, container, false);
        mRef = Common.FIREBASE_TECHNICIAN_REVIEW_REFERENCE;
        Toast.makeText(root.getContext(), Common.FIREBASE_TECHNICIAN_REFERENCE.toString(), Toast.LENGTH_LONG).show();
        mRecyclerView = (RecyclerView) root.findViewById(R.id.reviews_rv);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<ReviewsPojo, ReviewsFragment.MyViewHolder> adapter = new FirebaseRecyclerAdapter<ReviewsPojo, ReviewsFragment.MyViewHolder>(
                ReviewsPojo .class,
                R.layout.reviews_layout,
                ReviewsFragment.MyViewHolder.class,
                mRef
        ) {


            @Override
            protected void populateViewHolder(ReviewsFragment.MyViewHolder myViewHolder, ReviewsPojo reviewsPojo, int i) {
                myViewHolder.setupViews(reviewsPojo.getReview());
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
        void setupViews(String reviews){
            TextView review = (TextView) mView.findViewById(R.id.review_text_view);


            review.setText(reviews);

        }
    }
}