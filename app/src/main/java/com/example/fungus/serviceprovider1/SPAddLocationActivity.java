package com.example.fungus.serviceprovider1;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.fungus.serviceprovider1.model.Service;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SPAddLocationActivity extends AppCompatActivity implements OnMapReadyCallback {
    private Location currentLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private GoogleMap mMap;
    private static final int LOCATION_REQUEST = 500;
    private int point = 0;
    private double markerLat,markerLong = 0;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private String s_id,stateSelection,typeSelection;
    private Intent getIntent;
    private String SP_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_spmap);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Insert Location");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        getIntent = getIntent();
        s_id = getIntent.getStringExtra("s_id");
        stateSelection = getIntent.getStringExtra("stateSelection");
        typeSelection = getIntent.getStringExtra("typeSelection");

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        final SupportMapFragment map = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.addSMap);
        map.getMapAsync(this);

        Button btnAddS = findViewById(R.id.btnAddS);
        btnAddS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(markerLat==0 || markerLong == 0){
                    makeMessage("Pick a point");
                }else{
//                    makeMessage(Double.toString(markerLat+markerLong));
                    fnInsertFB();
                }
            }
        });

        SP_id = mAuth.getCurrentUser().getUid();

    }

    public void fnInsertFB(){

        databaseReference.child("Service").child(SP_id).child(s_id).child("s_latitude").setValue(markerLat).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                databaseReference.child("Service").child(SP_id).child(s_id).child("s_longitude").setValue(markerLong).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        databaseReference.child("Search").child(stateSelection).child(s_id).child("s_latitude").setValue(markerLat).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                databaseReference.child("Search").child(stateSelection).child(s_id).child("s_longitude").setValue(markerLong).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        makeMessage("Service Added");
                                        setResult(1);
                                        finish();
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });
    }

    public void makeMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST);
            return;
        }
//        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST);
//            return;
//        }
        mMap.setMyLocationEnabled(true);

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if(point==1){
                    mMap.clear();
                }
                mMap.addMarker(new MarkerOptions().position(latLng));
                point=1;
                markerLong = latLng.longitude;
                markerLat = latLng.latitude;
            }
        });

        databaseReference.child("Service").child(SP_id).child(s_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Service service = dataSnapshot.getValue(Service.class);
                if(service!=null){
                    if(service.getS_latitude() != 0 || service.getS_longitude() != 0) {
                        markerLat = service.getS_latitude();
                        markerLong = service.getS_longitude();
                        if(markerLat==0&&markerLong==0)
                            fetchLastLocation();
                        LatLng marker = new LatLng(service.getS_latitude(),service.getS_longitude());
                        if(mMap!=null){
                            mMap.addMarker(new MarkerOptions().position(marker));
                            mMap.animateCamera(CameraUpdateFactory.newLatLng(marker));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker,15));
                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void fetchLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;
                    Toast.makeText(SPAddLocationActivity.this,currentLocation.getLatitude()+" "+currentLocation.getLongitude(),Toast.LENGTH_SHORT).show();
                    LatLng latLng = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                            new LatLng(currentLocation.getLatitude(),
                                    currentLocation.getLongitude()), 14));
//                    SupportMapFragment supportMapFragment= (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
//                    supportMapFragment.getMapAsync(MapsMainActivity_T.this);
                }else{
                    Toast.makeText(SPAddLocationActivity.this,"No Location recorded",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
