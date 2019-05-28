package com.example.fungus.serviceprovider1;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.fungus.serviceprovider1.model.Booking;
import com.example.fungus.serviceprovider1.model.Message;
import com.example.fungus.serviceprovider1.model.Service;
import com.example.fungus.serviceprovider1.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class UserMessageActivity extends AppCompatActivity {

    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    String receive_id,send_id,receive_name,booking_id,user_type;
    Intent intent;
    ImageButton btnSendMessage;
    EditText editSendMessage;
    ArrayList<Message> messages;
    RecyclerView recyclerView;
    TextView receiverName;
    TextView txtName, txtDate, txtTime, txtStatus;
    WebView webView;
    String token,title,message,serverURL,serverNO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_message);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //notification
        webView = findViewById(R.id.vwWeb);
        serverURL = "172.20.10.4";
        serverNO = ":8888";
//        token = "eqsjDyx-RS0:APA91bGq3ZqX6CgKmCNJ9Hec8RJ1EZ1wGCmFID8Go-uPydZJpJUgOLTiZnHlrkyWv1ZoFUd84VHzDtlXwYiJTlpaUXZ9Nsb5q5w5DxxZanvWe9cTOpvbt0Q2hAQZyy4vc4k_N47MB6Mz";
//        String title = "title";
//        String message = "message";

        receiverName = findViewById(R.id.receiverName);
        btnSendMessage = findViewById(R.id.btnSendMessage);
        editSendMessage = findViewById(R.id.editSendMessage);
        recyclerView = findViewById(R.id.messageList);

        txtName = findViewById(R.id.sBookingName);
        txtDate = findViewById(R.id.sBookingDate);
        txtTime = findViewById(R.id.sBookingTime);
        txtStatus = findViewById(R.id.sBookingStatus);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        send_id = firebaseAuth.getCurrentUser().getUid();

        intent = getIntent();
        receive_id = intent.getStringExtra("id");
        booking_id = intent.getStringExtra("b_id");
        user_type = intent.getStringExtra("user_type");

        final Button btnConfirm = findViewById(R.id.btnConfirm);
        final Button btnCancel = findViewById(R.id.btnCancel);
        btnCancel.setVisibility(View.INVISIBLE);
//        btnCancel.setClickable(false);
        btnConfirm.setVisibility(View.INVISIBLE);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseReference.child("Booking").child(booking_id).child("status").setValue("Confirmed");
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseReference.child("Booking").child(booking_id).child("status").setValue("Canceled");
            }
        });

        databaseReference.child("Booking").child(booking_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Booking booking = dataSnapshot.getValue(Booking.class);
                txtDate.setText(String.valueOf(booking.getDate()));
                txtTime.setText(booking.getTime());
                txtStatus.setText(booking.getStatus());
                String status = booking.getStatus();
                if(status.equals("Pending")&&user_type.equals("1")){
                    btnCancel.setVisibility(View.VISIBLE);
                    btnConfirm.setVisibility(View.VISIBLE);
                }
                databaseReference.child("Service").child(booking.getSp_id()).child(booking.getS_id()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Service service = dataSnapshot.getValue(Service.class);
                        txtName.setText(service.getS_name());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReference.child("users").child(receive_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                receive_name = user.getName();
                receiverName.setText(receive_name);
                token = user.getRegID();

                readMessage(send_id,receive_id);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReference.child("users").child(send_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                String sender_name = user.getName();
                title = sender_name;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message1 = editSendMessage.getText().toString();
                if(!message1.equals("")){
                    sendMessage(send_id,receive_id,message1);
                }
                editSendMessage.setText("");
            }
        });
    }

    private void sendMessage(String sender,String receiver,String message){
        HashMap<String,Object> hashMap = new HashMap<>();

        hashMap.put("sender",sender);
        hashMap.put("receiver",receiver);
        hashMap.put("message",message);

        databaseReference.child("Message").child(booking_id).push().setValue(hashMap);
        String url = "http://"+serverURL+serverNO+"/webServiceJSON/?regId="+token+"&title="+title+"&message="+message+"&push_type=individual";
        webView.loadUrl(url);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        });
        webView.setWebChromeClient(new WebChromeClient());
    }

    private void readMessage(final String myid, final String userid){
        messages = new ArrayList<>();
        databaseReference.child("Message").child(booking_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messages.clear();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Message message = snapshot.getValue(Message.class);
                    if(message.getReceiver().equals(myid)&&message.getSender().equals(userid)||message.getReceiver().equals(userid)&&message.getSender().equals(myid)){
                        messages.add(message);
                    }
                }
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
//                linearLayoutManager.setReverseLayout(true);
                MessageAdapter messageAdapter = new MessageAdapter(messages);
                recyclerView.setLayoutManager(linearLayoutManager);
                recyclerView.setAdapter(messageAdapter);
                recyclerView.scrollToPosition(messages.size() - 1);
                messageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
