package com.example.fungus.serviceprovider1;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.fungus.serviceprovider1.model.Service;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.Iterator;

public class MapsMainActivity_T extends AppCompatActivity implements OnMapReadyCallback,NavigationView.OnNavigationItemSelectedListener{

    private GoogleMap mMap;
    private static final int LOCATION_REQUEST = 500;
    private FirebaseAuth mAuth;
    private DatabaseReference db;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private ValueEventListener postListener;
    private String TAG = "MapsMainActivity_T";
    private ArrayList<Service> services;
    private Location currentLocation;
    private ArrayList<Double> distances = new ArrayList<>();
    private String search = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_slide_up_panel);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);

        final SlidingUpPanelLayout slidingUpPanelLayout = findViewById(R.id.sliding_layout);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout2);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,drawer , toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view2);
        navigationView.setNavigationItemSelectedListener(this);

//         Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        sharedPreferences = getApplicationContext().getSharedPreferences("authentication",0);
        editor = sharedPreferences.edit();

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance().getReference();

        final String [] searchArray = getResources().getStringArray(R.array.search_service);
        ListView listView = findViewById(R.id.contentListView);
        listView.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,searchArray));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                search = searchArray[i];
                mMap.clear();
                db.child("Search").child("Malacca").child(search).addListenerForSingleValueEvent(postListener);

                slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });
//        db.child("Search").child("Malacca").child("barber");
//        if(currentLocation!=null){
//        }
        postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();
                Iterator<DataSnapshot> iterator = snapshotIterator.iterator();
                services = new ArrayList<>();
                while (iterator.hasNext()) {
                    DataSnapshot next = (DataSnapshot) iterator.next();
                    //getting value
                    Service service = new Service(next.child("s_id").getValue().toString(), Double.valueOf(next.child("s_latitude").getValue().toString()),Double.valueOf(next.child("s_longitude").getValue().toString()),next.child("s_name").getValue().toString(),next.child("s_state").getValue().toString(),next.child("s_type").getValue().toString());
//                    services.add(next.getValue(Service.class));
                    Location serviceLocation = new Location(currentLocation);
                    serviceLocation.setLatitude(service.getS_latitude());
                    serviceLocation.setLongitude(service.getS_longitude());
                    double distance = fnCalculateDistance(currentLocation,serviceLocation);
//                    distances.add(distance);
                    service.setDistance(distance);
                    services.add(service);

//                    Collections.sort(services,Service.serviceComparator);
//                    Log.e(TAG, "Value = " + next.child("s_name").getValue());
                }

                quickSort(services,0,services.size()-1);
                //stupid sort
//                for(int j=0;j<services.size();j++){
//                    if(j+1<services.size()){
//                        if(services.get(j).getDistance()>services.get(j+1).getDistance()){
//                            Service temp = services.get(j);
//                            services.set(j,services.get(j+1));
//                            services.set(j+1,temp);
//                        }
//                    }
//                }

                for(Service service : services){
                    Log.e(TAG,String.valueOf(service.getDistance()));
                    Log.e(TAG,service.getS_name());
                }

                for(int i=0;i<services.size();i++){
                    if(i>10){
                        break;
                    }else{
                        mMap.addMarker(new MarkerOptions().position(new LatLng(services.get(i).getS_latitude(), services.get(i).getS_longitude())).title(services.get(i).getS_name()));
                    }
                }
//                for(DataSnapshot data : dataSnapshot.getChildren()){
//                    Search search = dataSnapshot.getValue(Search.class);
//                    Log.e(TAG,String.valueOf(search.getS_long()));
//                }
                // Get Post object and use the values to update the UI
//                List<Search> searches = dataSnapshot.getValue();
//                Search search = dataSnapshot.getValue(Search.class);
//                Log.e(TAG,String.valueOf(search.getS_long()));
//                int userType = user.getUserType();
                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
    }

    public void quickSort(ArrayList<Service> arr, int begin, int end) {
        if (begin < end) {
            int partitionIndex = partition(arr, begin, end);

            quickSort(arr, begin, partitionIndex-1);
            quickSort(arr, partitionIndex+1, end);
        }
    }

    private int partition(ArrayList<Service> arr, int begin, int end) {
        double pivot = arr.get(end).getDistance();
        int i = (begin-1);

        for (int j = begin; j < end; j++) {
            if (arr.get(j).getDistance() <= pivot) {
                i++;
                Service swapTemp = arr.get(i);
                arr.set(i,arr.get(j));
                arr.set(j,swapTemp);
            }
        }

        Service swapTemp = arr.get(i+1);
        arr.set(i+1,arr.get(end));
        arr.set(end,swapTemp);

        return i+1;
    }

    public double rad(double x){
        return x * Math.PI / 180;
    }

    public double fnCalculateDistance(Location p1,Location p2){
        int R = 6378137; // Earth’s mean radius in meter
        double dLat = rad(p2.getLatitude() - p1.getLatitude());
        double dLong = rad(p2.getLongitude() - p1.getLongitude());
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(rad(p1.getLatitude())) * Math.cos(rad(p2.getLatitude())) *
                        Math.sin(dLong / 2) * Math.sin(dLong / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = R * c;
        return d; // returns the distance in meter
    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST);
            return;
        }
        mMap.setMyLocationEnabled(true);
        if (mMap != null) {
//            Location arg0 = mMap.getMyLocation();
//            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(arg0.getLatitude(), arg0.getLongitude()), 14));
            mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {

                @Override
                public void onMyLocationChange(Location arg0) {
                    // TODO Auto-generated method stub
                    currentLocation = arg0;
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(arg0.getLatitude(), arg0.getLongitude()), 14));
//                    mMap.addMarker(new MarkerOptions().position(new LatLng(arg0.getLatitude(), arg0.getLongitude())).title("It's Me!"));
                }
            });
        }




        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
//        float zoomLevel = 16.0f; //This goes up to 21
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, zoomLevel));
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.nav_home){

        }else if(id == R.id.nav_out){
            editor.remove("user_id");
            editor.remove("password");
            editor.commit();
            mAuth.signOut();
            startActivity(new Intent(MapsMainActivity_T.this, LoginActivity.class)); //Go back to home page
            finish();
        }
        return true;
        }
}
