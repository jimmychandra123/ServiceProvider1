package com.example.fungus.serviceprovider1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.fungus.serviceprovider1.model.Booking;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

public class SPManageBookingActivity extends AppCompatActivity {

    private DatabaseReference db;
    private FirebaseAuth auth;
    private String u_id;
    private ValueEventListener postListener;
    private String TAG = "SPMenuActivity";
    private ArrayList<Booking> bookings;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_manage_booking);

        recyclerView = findViewById(R.id.listManageBookingView);
        db = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        u_id = auth.getCurrentUser().getUid();

        postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();
                Iterator<DataSnapshot> iterator = snapshotIterator.iterator();
                bookings = new ArrayList<>();
                while (iterator.hasNext()) {
                    DataSnapshot next = (DataSnapshot) iterator.next();
                    //getting value
                    Booking booking = new Booking(next.child("b_id").getValue().toString(), next.child("date").getValue().toString(),next.child("time").getValue().toString(),next.child("s_id").getValue().toString(),next.child("sp_id").getValue().toString(),next.child("u_id").getValue().toString());
                    Log.e(TAG,booking.getB_id());
                    bookings.add(booking);
                }
                CustomAdapterBookingList customAdapterBookingList = new CustomAdapterBookingList(bookings, new CustomAdapterBookingList.OnItemClickListener() {
                    @Override
                    public void onItemClick(Booking item) {
                        String sp_id = item.getSp_id();
                        String b_id = item.getB_id();
                        Intent intent = new Intent(getApplicationContext(),UserMessageActivity.class);
                        intent.putExtra("id",sp_id);
                        intent.putExtra("b_id",b_id);
                        startActivity(intent);
//                        Bundle bundle = new Bundle();
//                        bundle.putString("s_id", i);
//                        SPUpdateServiceActivity spUpdateServiceActivity = new SPUpdateServiceActivity();
//                        spUpdateServiceActivity.setArguments(bundle);
//                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.screen_area,spUpdateServiceActivity).commit();
                    }
                },1);
                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                recyclerView.setAdapter(customAdapterBookingList);
                customAdapterBookingList.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        Log.e("e",u_id);
        db.child("S_Book").child(u_id).addValueEventListener(postListener);
    }
}
