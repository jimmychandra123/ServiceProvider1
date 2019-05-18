package com.example.fungus.serviceprovider1;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.fungus.serviceprovider1.model.Message;
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
    String receive_id,send_id,receive_name,booking_id;
    Intent intent;
    ImageButton btnSendMessage;
    EditText editSendMessage;
    ArrayList<Message> messages;
    RecyclerView recyclerView;
    TextView receiverName;
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

        receiverName = findViewById(R.id.receiverName);
        btnSendMessage = findViewById(R.id.btnSendMessage);
        editSendMessage = findViewById(R.id.editSendMessage);
        recyclerView = findViewById(R.id.messageList);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        send_id = firebaseAuth.getCurrentUser().getUid();

        intent = getIntent();
        receive_id = intent.getStringExtra("id");
        booking_id = intent.getStringExtra("b_id");

        databaseReference.child("users").child(receive_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                receive_name = user.getName();
                receiverName.setText(receive_name);

                readMessage(send_id,receive_id);
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
                MessageAdapter messageAdapter = new MessageAdapter(messages);
                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                recyclerView.setAdapter(messageAdapter);
                messageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
