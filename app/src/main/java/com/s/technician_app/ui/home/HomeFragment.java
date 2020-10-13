package com.s.technician_app.ui.home;

import android.Manifest;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.Chip;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.s.technician_app.Common;
import com.s.technician_app.EventBus.TechnicianRequestReceived;
import com.s.technician_app.LoginActivity;
import com.s.technician_app.R;
import com.s.technician_app.Remote.IGoogleApi;
import com.s.technician_app.Remote.RetrofitClient;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class HomeFragment extends Fragment implements OnMapReadyCallback {

    @BindView(R.id.chip_decline)
    Chip chip_decline;
    @BindView(R.id.layout_accept)
    CardView layout_accept;
    @BindView(R.id.circularProgressBar)
    CircularProgressBar circularProgressBar;
    @BindView(R.id.txt_estimate_time)
    TextView txt_estimate_time;
    @BindView(R.id.txt_estimate_distance)
    TextView txt_estimate_distance;
    @BindView(R.id.root_layout)
    FrameLayout rootLayout;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private IGoogleApi iGoogleApi;
    private Polyline blackPolyLine, greyPolyLine;
    private PolylineOptions polylineOptions, blackPolyLineOptions;
    private List<LatLng> polyLineList;
    private GoogleMap mMap;

    private HomeViewModel homeViewModel;

    private SettingsClient mSettingsClient;
    private LocationSettingsRequest mLocationSettingsRequest;

    //location variales
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    SupportMapFragment mapFragment;
    boolean showsnackBar;

    //online system
    DatabaseReference onlineRef, currentUserRef, techniciansLocationRef;
    GeoFire geoFire;
    ValueEventListener onlineValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            if (snapshot.exists() && currentUserRef != null) {
                currentUserRef.onDisconnect().removeValue();
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Snackbar.make(mapFragment.getView(), error.getMessage(), Snackbar.LENGTH_LONG)
                    .show();
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        //if (EventBus.getDefault().hasSubscriberForEvent(TechnicianRequestReceived.class))
         ///   EventBus.getDefault().removeStickyEvent(TechnicianRequestReceived.class);
        //EventBus.getDefault().unregister(this);

        //compositeDisposable.clear();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (EventBus.getDefault().hasSubscriberForEvent(TechnicianRequestReceived.class))
            EventBus.getDefault().removeStickyEvent(TechnicianRequestReceived.class);
        EventBus.getDefault().unregister(this);

        compositeDisposable.clear();
    }

    @Override
    public void onDestroy() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        geoFire.removeLocation(FirebaseAuth.getInstance().getCurrentUser().getUid());
        onlineRef.removeEventListener(onlineValueEventListener);

        if (EventBus.getDefault().hasSubscriberForEvent(TechnicianRequestReceived.class))
            EventBus.getDefault().removeStickyEvent(TechnicianRequestReceived.class);
        EventBus.getDefault().unregister(this);

        compositeDisposable.clear();

        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        showsnackBar = true;
        registerOnlineSystem();

       // EventBus.getDefault().register(this);
    }

    @Override
    public void onStart() {
        super.onStart();
       // if(!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        //}
    }

    private void registerOnlineSystem() {
        onlineRef.addValueEventListener(onlineValueEventListener);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
        Toast.makeText(root.getContext(), "This app uses gps to get your location, kindly ensure location setting is on", Toast.LENGTH_LONG).show();

        initViews(root);
        init();

        showsnackBar = true;

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        return root;
    }

    private void initViews(View root) {
        ButterKnife.bind(this, root);
    }

    private void init() {
        iGoogleApi = RetrofitClient.getInstance().create(IGoogleApi.class);

        onlineRef = FirebaseDatabase.getInstance().getReference().child(".info/connected");
        techniciansLocationRef = FirebaseDatabase.getInstance().getReference(Common.TECHNICIAN_LOCATION_REFERENCES);
        geoFire = new GeoFire(techniciansLocationRef);


        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
             Snackbar.make(rootLayout, getString(R.string.permission_require), Snackbar.LENGTH_SHORT).show();
            return;
        }

        buildLocationRequest();

       buildLocationCallBack();

       updateLocation();

    }

    private void updateLocation() {
        if(fusedLocationProviderClient == null){
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                Snackbar.make(getView(), getString(R.string.permission_require), Snackbar.LENGTH_SHORT).show();
                return;
            }
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());        }
}
    private void buildLocationCallBack() {
            if(locationCallback == null){
                locationCallback = new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LatLng newPosition = new LatLng(locationResult.getLastLocation().getLatitude(),
                                locationResult.getLastLocation().getLongitude());

                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newPosition, 15f));

                        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                        List<Address> addressList;
                        try {
                            addressList = geocoder.getFromLocation(locationResult.getLastLocation().getLatitude(),
                                    locationResult.getLastLocation().getLongitude(), 1);
                            String cityName = addressList.get(0).getCountryName();
                            Log.d("location", cityName);


                            if (cityName != null) {
                                techniciansLocationRef = FirebaseDatabase.getInstance().getReference(Common.TECHNICIAN_LOCATION_REFERENCES)
                                        .child(cityName);
                                currentUserRef = techniciansLocationRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                geoFire = new GeoFire(techniciansLocationRef);
                            } else {
                                techniciansLocationRef = FirebaseDatabase.getInstance().getReference(Common.TECHNICIAN_LOCATION_REFERENCES);
                                currentUserRef = FirebaseDatabase.getInstance().getReference(Common.TECHNICIAN_LOCATION_REFERENCES).child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                geoFire = new GeoFire(techniciansLocationRef);
                            }

                            geoFire.setLocation(FirebaseAuth.getInstance().getCurrentUser().getUid(),
                                    new GeoLocation(locationResult.getLastLocation().getLatitude()
                                            , locationResult.getLastLocation().getLongitude()),
                                    (key, error) -> {
                                        if (error != null)
                                            Snackbar.make(mapFragment.getView(), error.getMessage(), Snackbar.LENGTH_LONG)
                                                    .show();
                                    });


                            registerOnlineSystem();

                        } catch (IOException e) {
                            Snackbar.make(getView(), e.getMessage(), Snackbar.LENGTH_SHORT).show();
                        }
                    }
                };
}}
    private void buildLocationRequest() {
        if(locationRequest == null){
            locationRequest = new LocationRequest();
            locationRequest.setSmallestDisplacement(50f);
            locationRequest.setInterval(15000);
            locationRequest.setFastestInterval(10000);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            //**************************
            }}
            @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // mMap.getUiSettings().setZoomControlsEnabled(true);
        Dexter.withContext(getContext())
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            Snackbar.make(getView(), getString(R.string.permission_require), Snackbar.LENGTH_SHORT).show();
                            return;
                        }
                        mMap.setMyLocationEnabled(true);
                        mMap.getUiSettings().setMyLocationButtonEnabled(true);
                        mMap.setOnMyLocationButtonClickListener(() -> {
                            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                //    ActivityCompat#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for ActivityCompat#requestPermissions for more details.
                                return false;
                            }
                            fusedLocationProviderClient.getLastLocation()
                                    .addOnFailureListener(e -> Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show()).addOnSuccessListener(location -> {
                                LatLng userLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 18f));
                            });
                            return true;
                        });

                        View locationButton = ((View) mapFragment.getView().findViewById(Integer.parseInt("1"))
                                .getParent()).findViewById(Integer.parseInt("2"));

                        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
                        //align to right bottom of screen

                        params.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
                        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
                        params.setMargins(0, 0, 0, 50);
                        buildLocationRequest();

                        buildLocationCallBack();

                        updateLocation();                   }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        Toast.makeText(getContext(), "Permission " + permissionDeniedResponse.getPermissionName()
                                + "" + " was denied", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {

                    }
                }).check();

        try {
            boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.uber_maps_style));
            if (!success)
                Log.e("error", "style parsing error");
        } catch (Resources.NotFoundException e) {
            Log.e("error", Objects.requireNonNull(e.getMessage()));
        }
        Snackbar.make(mapFragment.getView(), "you're online", Snackbar.LENGTH_LONG)
                .show();

    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onTechnicianRequestReceived(TechnicianRequestReceived event) {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Snackbar.make(requireView(), getString(R.string.permission_require), Snackbar.LENGTH_LONG).show();
            return;
        }
        fusedLocationProviderClient.getLastLocation()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(requireView(), e.getMessage(), Snackbar.LENGTH_LONG).show();
                    }
                }).addOnSuccessListener(location -> {

                    compositeDisposable.add(iGoogleApi.getDirection("driving",
                            "less_driving",
                            new StringBuilder()
                    .append(location.getLatitude())
                    .append(",")
                    .append(location.getLongitude())
                    .toString(),
                            event.getPickupLocation(),
                            getString(R.string.directions_api_key))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(returnResult -> {

                        try{
                            //PARSE JSON
                            JSONObject jsonObject = new JSONObject(returnResult);
                            JSONArray jsonArray = jsonObject.getJSONArray("routes");
                            for(int i = 0; i < jsonArray.length(); i++)
                            {
                                JSONObject route = jsonArray.getJSONObject(i);
                                JSONObject poly = route.getJSONObject("overview_polyline");
                                String polyline = poly.getString("points");
                                polyLineList = Common.decodePoly(polyline);

                            }

                            polylineOptions = new PolylineOptions()
                                    .color(Color.GRAY)
                                    .width(12)
                                    .startCap(new SquareCap())
                                    .jointType(JointType.ROUND)
                                    .addAll(polyLineList);

                            greyPolyLine = mMap.addPolyline(polylineOptions);

                            blackPolyLineOptions = new PolylineOptions()
                                    .color(Color.BLACK)
                                    .width(12)
                                    .startCap(new SquareCap())
                                    .jointType(JointType.ROUND)
                                    .addAll(polyLineList);

                            blackPolyLine = mMap.addPolyline(blackPolyLineOptions);

                            //Animator
                            ValueAnimator valueAnimator = ValueAnimator.ofInt(0,1);
                            valueAnimator.setDuration(1100);
                            valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
                            valueAnimator.setInterpolator(new LinearInterpolator());
                            valueAnimator.addUpdateListener(value -> {
                                List<LatLng> points = greyPolyLine.getPoints();
                                int percentValue = (int)value.getAnimatedValue();
                                int size = points.size();
                                int newPoints = (int) (size * (percentValue/100.0f));
                                List<LatLng> p = points.subList(0, newPoints);
                                blackPolyLine.setPoints(p);
                            });

                            valueAnimator.start();

                            LatLng origin = new LatLng(location.getLatitude(), location.getLongitude());
                            LatLng destination = new LatLng(Double.parseDouble(event.getPickupLocation().split(",")[0]),
                                    Double.parseDouble(event.getPickupLocation().split(",")[1]));

                            LatLngBounds latLngBounds = new LatLngBounds.Builder()
                                    .include(origin)
                                    .include(destination)
                                    .build();

                            //add icon for origin
                            JSONObject object = jsonArray.getJSONObject(0);
                            JSONArray legs = object.getJSONArray("legs");
                            JSONObject legObjects = legs.getJSONObject(0);

                            JSONObject time = legObjects.getJSONObject("duration");
                            String duration = time.getString("text");

                            JSONObject distanceEstimate = legObjects.getJSONObject("distance");
                            String distance = distanceEstimate.getString("text");

                            txt_estimate_time.setText(duration);
                            txt_estimate_distance.setText(distance);

                           mMap.addMarker(new MarkerOptions()
                           .position(destination)
                           .icon(BitmapDescriptorFactory.defaultMarker())
                           .title("Pickup Location"));

                            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 160));
                            mMap.moveCamera(CameraUpdateFactory.zoomTo(mMap.getCameraPosition().zoom - 1));

                            Log.d("view", "setting visibility");
                            chip_decline.setVisibility(View.VISIBLE);
                            layout_accept.setVisibility(View.VISIBLE);
                            Log.d("done_v", "visibility set");

                            Observable.interval(100, TimeUnit.MILLISECONDS)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .doOnNext( x -> {
                                        circularProgressBar.setProgress(circularProgressBar.getProgress()+1f);
                                    })
                                    .takeUntil(aLong -> aLong == 300)
                                    .doOnComplete(() -> {
                                        Toast.makeText(getContext(), "Fake accept action", Toast.LENGTH_SHORT).show();
                                    }).subscribe();

                        } catch (Exception e)
                        {
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }));
        });
    }
}