package com.s.technician_app.ui.Profile;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.FirebaseDatabase;
import com.s.technician_app.Common;
import com.s.technician_app.Fragments.LiftsCommissionedFragment;
import com.s.technician_app.Fragments.ReviewsFragment;
import com.s.technician_app.R;
import com.s.technician_app.ui.Faults.FaultsViewModel;

public class ProfileFragment extends Fragment {


    private ProfileViewModel ProfileViewModel;
    ProfilePagerAdapter profilePagerAdapter;
    ViewPager viewPager;
    TextView name;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ProfileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        View root = inflater.inflate(R.layout.fragment_profile, container, false);
        FragmentManager fragmentManager = getFragmentManager();
        profilePagerAdapter = new ProfilePagerAdapter(fragmentManager);
        viewPager = root.findViewById(R.id.profile_pager);
        viewPager.setAdapter(profilePagerAdapter);
        name = root.findViewById(R.id.profile_name);
        String tname = Common.currentUser.getFirstName() + " " + Common.currentUser.getLastName();
        name.setText(tname);
        TabLayout tabLayout = root.findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        return root;
    }

    public class ProfilePagerAdapter extends FragmentStatePagerAdapter {

        public ProfilePagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int i) {
            switch (i) {
                case 0:
                    Fragment fragment = new ReviewsFragment();

                    return fragment;
                case 1:
                    Fragment fragment2 = new LiftsCommissionedFragment();

                    return fragment2;
                default:
                    return null;
            }
        }

        @Override
        public int getCount(){
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Reviews";
                // break;
                case 1:
                    return "Repair History";
                // break;
                default :
                    return null;
            }
        }
    }
}