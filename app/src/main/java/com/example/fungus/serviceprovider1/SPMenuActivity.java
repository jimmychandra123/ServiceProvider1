package com.example.fungus.serviceprovider1;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.fungus.serviceprovider1.model.Service;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SPMenuActivity.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SPMenuActivity#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SPMenuActivity extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private DatabaseReference db;
    private FirebaseAuth auth;
    private String sp_id;
    private ValueEventListener postListener;
    private String TAG = "SPMenuActivity";
    private ArrayList<Service> services;
    private RecyclerView recyclerView;
    private TextView resultText;


    public SPMenuActivity() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SPMenuActivity.
     */
    // TODO: Rename and change types and number of parameters
    public static SPMenuActivity newInstance(String param1, String param2) {
        SPMenuActivity fragment = new SPMenuActivity();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_spmenu, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        resultText = view.findViewById(R.id.resultText);
        recyclerView = view.findViewById(R.id.listMenuView);
        db = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        sp_id = auth.getCurrentUser().getUid();

        postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();
                Iterator<DataSnapshot> iterator = snapshotIterator.iterator();
                services = new ArrayList<>();
                while (iterator.hasNext()) {
                    DataSnapshot next = (DataSnapshot) iterator.next();
                    //getting value
                    Service service = new Service(next.child("s_id").getValue().toString(), Double.valueOf(next.child("s_latitude").getValue().toString()),Double.valueOf(next.child("s_longitude").getValue().toString()),next.child("s_name").getValue().toString(),next.child("s_state").getValue().toString(),next.child("s_type").getValue().toString());
                    services.add(service);
                }
                if(services.size()!=0)
                    resultText.setVisibility(View.INVISIBLE);

                CustomAdapterMenuList customAdapterMenuList = new CustomAdapterMenuList(services, new CustomAdapterMenuList.OnItemClickListener() {
                    @Override
                    public void onItemClick(Service item) {
                        String i = item.getS_id();
                        Intent intent = new Intent(getContext(),SPAddServiceActivity.class);
                        intent.putExtra("s_id",i);
                        startActivity(intent);
//                        Bundle bundle = new Bundle();
//                        bundle.putString("s_id", i);
//                        SPUpdateServiceActivity spUpdateServiceActivity = new SPUpdateServiceActivity();
//                        spUpdateServiceActivity.setArguments(bundle);
//                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.screen_area,spUpdateServiceActivity).commit();
                    }
                });
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerView.setAdapter(customAdapterMenuList);
                customAdapterMenuList.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.e(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };

        db.child("Service").child(sp_id).addValueEventListener(postListener);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onStop(){
        super.onStop();
//        db.child("Service").child(sp_id).removeEventListener(postListener);
    }
}
