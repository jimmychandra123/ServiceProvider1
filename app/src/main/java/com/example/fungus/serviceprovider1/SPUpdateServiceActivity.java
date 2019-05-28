package com.example.fungus.serviceprovider1;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fungus.serviceprovider1.model.Address;
import com.example.fungus.serviceprovider1.model.Service;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


///**
// * A simple {@link Fragment} subclass.
// * Activities that contain this fragment must implement the
// * {@link SPUpdateServiceActivity.OnFragmentInteractionListener} interface
// * to handle interaction events.
// * Use the {@link SPUpdateServiceActivity#newInstance} factory method to
// * create an instance of this fragment.
// */
public class SPUpdateServiceActivity extends AppCompatActivity  {
//    // TODO: Rename parameter arguments, choose names that match
//    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";

//    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;

//    private GoogleMap mMap;
//    private static final int LOCATION_REQUEST = 500;
    private Context context;
    private int point = 0;
//    private double markerLat,markerLong = 0;
    private EditText name;
    private String [] state,type;
    private String stateSelection,typeSelection = null;
    private TextView txtState,txtType;
    private Service service;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private String s_id;
    private ValueEventListener postListener;
    private String TAG = "SPUpdateServiceActivity";

//    private OnFragmentInteractionListener mListener;

//    public SPUpdateServiceActivity() {
//        // Required empty public constructor
//    }

//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment SPUpdateServiceActivity.
//     */
//    // TODO: Rename and change types and number of parameters
//    public static SPUpdateServiceActivity newInstance(String param1, String param2) {
//        SPUpdateServiceActivity fragment = new SPUpdateServiceActivity();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_spadd);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        s_id = getIntent().getStringExtra("s_id");

//        SupportMapFragment map = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.addSMap);
//        map.getMapAsync(this);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        txtType = findViewById(R.id.txtAddType);
        txtState = findViewById(R.id.txtAddState);

        name = findViewById(R.id.addSName);
        Button btnAddS = findViewById(R.id.btnAddS);
//        btnAddS.setText("Update");
        btnAddS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if(markerLat==0 || markerLong == 0){
//                    makeMessage("Pick a point");
//                }else
                    if(stateSelection==null){
                    makeMessage("Pick a state");
                }else if(typeSelection==null){
                    makeMessage("Pick a type");
                }
                else{
//                    makeMessage(Double.toString(markerLat+markerLong));
                    fnInsertFB();
                }
            }
        });

        Button btnAddState = findViewById(R.id.btnAddState);
        btnAddState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fnCreateDialogState();
            }
        });

        Button btnAddType = findViewById(R.id.btnAddType);
        btnAddType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fnCreateDialogType();
            }
        });

        postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                Service service = dataSnapshot.getValue(Service.class);
                name.setText(service.getS_name());
                txtState.setText(service.getS_state());
                txtType.setText(service.getS_type());
//                if(mMap!=null) {
//                    mMap.addMarker(new MarkerOptions().position(new LatLng(service.getS_latitude(), service.getS_longitude())));
//                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(service.getS_latitude(), service.getS_longitude()), 15));
//                }
//                markerLat=service.getS_latitude();
//                markerLong=service.getS_longitude();
                stateSelection = service.getS_state();
                typeSelection = service.getS_type();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };

        String sp_id = mAuth.getCurrentUser().getUid();
        mDatabase.child("Service").child(sp_id).child(s_id).addListenerForSingleValueEvent(postListener);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
    }

    @Override
    public void onStart() {
        super.onStart();
//        SupportMapFragment mapFragment = (SupportMapFragment) getFragmentManager()
//                .findFragmentById(R.id.addSMap);
//        mapFragment.getMapAsync(this);

    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_spadd, container, false);
//
//        s_id = getArguments().getString("s_id");
////        SupportMapFragment map = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.addSMap);
////        map.getMapAsync(this);
//        return view;
//        // Inflate the layout for this fragment
////        return inflater.inflate(R.layout.fragment_spadd, container, false);
//    }

//    @Override
//    public void onViewCreated(View view, @Nullable final Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        SupportMapFragment map = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.addSMap);
//        map.getMapAsync(this);
//
//        mDatabase = FirebaseDatabase.getInstance().getReference();
//        mAuth = FirebaseAuth.getInstance();
//
//        txtType = view.findViewById(R.id.txtAddType);
//        txtState = view.findViewById(R.id.txtAddState);
//
//        name = view.findViewById(R.id.addSName);
//        Button btnAddS = view.findViewById(R.id.btnAddS);
//        btnAddS.setText("Update");
//        btnAddS.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(markerLat==0 || markerLong == 0){
//                    makeMessage("Pick a point");
//                }else if(stateSelection==null){
//                    makeMessage("Pick a state");
//                }else if(typeSelection==null){
//                    makeMessage("Pick a type");
//                }
//                else{
////                    makeMessage(Double.toString(markerLat+markerLong));
//                    fnInsertFB();
//                }
//            }
//        });
//
//        Button btnAddState = view.findViewById(R.id.btnAddState);
//        btnAddState.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                fnCreateDialogState();
//            }
//        });
//
//        Button btnAddType = view.findViewById(R.id.btnAddType);
//        btnAddType.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                fnCreateDialogType();
//            }
//        });
//
//        postListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                // Get Post object and use the values to update the UI
//                Service service = dataSnapshot.getValue(Service.class);
//                name.setText(service.getS_name());
//                txtState.setText(service.getS_state());
//                txtType.setText(service.getS_type());
//                mMap.addMarker(new MarkerOptions().position(new LatLng(service.getS_latitude(),service.getS_longitude())));
//                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(service.getS_latitude(),service.getS_longitude()),15));
//                markerLat=service.getS_latitude();
//                markerLong=service.getS_longitude();
//                stateSelection = service.getS_state();
//                typeSelection = service.getS_type();
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                // Getting Post failed, log a message
//                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
//                // ...
//            }
//        };
//
//        String sp_id = mAuth.getCurrentUser().getUid();
//        mDatabase.child("Service").child(sp_id).child(s_id).addListenerForSingleValueEvent(postListener);
//    }

//    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }
//
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        this.context = context;
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }

//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        mMap = googleMap;
//        mMap.getUiSettings().setZoomControlsEnabled(true);
////        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
////            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST);
////            return;
////        }
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST);
//            return;
//        }
//        mMap.setMyLocationEnabled(true);
//
//        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
//            @Override
//            public void onMapClick(LatLng latLng) {
//                if(point==1){
//                    mMap.clear();
//                }
//                mMap.addMarker(new MarkerOptions().position(latLng));
//                point=1;
//                markerLong = latLng.longitude;
//                markerLat = latLng.latitude;
//            }
//        });
//    }

    public void makeMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

    public void fnInsertFB(){
        String SP_id = mAuth.getCurrentUser().getUid();
//        final String id = mDatabase.child("Service").push().getKey();
        String SP_name = name.getText().toString();
        Address address = new Address();
        service = new Service(s_id,SP_name,stateSelection,typeSelection,SP_id);
//        Search search = new Search(id,"barber",stateSelection,markerLat,markerLong);
        mDatabase.child("Service").child(SP_id).child(s_id).setValue(service).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mDatabase.child("Search").child(stateSelection).child(typeSelection).child(s_id).setValue(service).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        makeMessage("Service Updated");
                        Intent intent = new Intent(SPUpdateServiceActivity.this,SPAddLocationActivity.class);
                        intent.putExtra("s_id",s_id);
                        intent.putExtra("stateSelection",stateSelection);
                        intent.putExtra("typeSelection",typeSelection);
//                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.screen_area,new SPMenuActivity()).commit();
                        startActivity(intent);
//                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.screen_area,new SPMenuActivity()).commit();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        makeMessage("Failed");
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                makeMessage("Failed");
            }
        })
        ;
    }

    public void fnCreateDialogState(){
        state = getResources().getStringArray(R.array.malaysia_state);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick a State");
        builder.setSingleChoiceItems(state, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                stateSelection = state[i];
            }
        });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                txtState.setText(stateSelection);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builder.show();
    }

    public void fnCreateDialogType(){
        type = getResources().getStringArray(R.array.search_service);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick a Service Type");
        builder.setSingleChoiceItems(type, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                typeSelection = type[i];
            }
        });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                txtType.setText(typeSelection);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builder.show();
    }
//    /**
//     * This interface must be implemented by activities that contain this
//     * fragment to allow an interaction in this fragment to be communicated
//     * to the activity and potentially other fragments contained in that
//     * activity.
//     * <p>
//     * See the Android Training lesson <a href=
//     * "http://developer.android.com/training/basics/fragments/communicating.html"
//     * >Communicating with Other Fragments</a> for more information.
//     */
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);
//    }
}
