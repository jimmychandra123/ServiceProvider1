package com.example.fungus.serviceprovider1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.example.fungus.serviceprovider1.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    String TAG = "RegisterActivity";
    EditText editGmail,editUserName,editContact;
    Switch switchUserType;
    int userType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Register");

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        editGmail = findViewById(R.id.editGmail);
        editUserName = findViewById(R.id.editUserName);
        editContact = findViewById(R.id.editContact);
        switchUserType = findViewById(R.id.switchUserType);
//        Log.e("key",mDatabase.child("users").push().getKey());

        editGmail.setEnabled(false);

        sharedPreferences = getApplicationContext().getSharedPreferences("authentication",0);
        Log.e("Check Sign In",Boolean.toString(sharedPreferences.contains("authentication")));
        if(sharedPreferences.getString("user_id",null)!=null){
            editGmail.setText(sharedPreferences.getString("user_id",null));
        }
//        editor = sharedPreferences.edit();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public void writeNewUser(View view) {
        if(editUserName.getText().toString().equals("")){
            Toast.makeText(RegisterActivity.this,"Name cannot blank",Toast.LENGTH_SHORT).show();
        }else if(editContact.getText().toString().equals("")){
            Toast.makeText(RegisterActivity.this,"Phone number cannot blank",Toast.LENGTH_SHORT).show();
        }else {
            FirebaseUser fbUser = mAuth.getCurrentUser();
            String id = fbUser.getUid();
            userType = 0;
            Log.e("key", id);
            String email = editGmail.getText().toString();
            String name = editUserName.getText().toString();
            String contact = editContact.getText().toString();

            if (switchUserType.isChecked()) {
                userType = 1;
            } else {
                userType = 0;
            }
            User user = new User(id, email, name, userType,contact);
            mDatabase.child("users").child(id).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    // Write was successful!
                    Toast.makeText(RegisterActivity.this, "Register Success", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "register success");
                    if (userType == 0) {
                        startActivity(new Intent(RegisterActivity.this, MapsMainActivity_T.class));
                        finish();
                    } else if (userType == 1) {
                        startActivity(new Intent(RegisterActivity.this, SPNavigationDrawerActivity.class));
                        finish();
                    } else {
                        Toast.makeText(RegisterActivity.this, "Error", Toast.LENGTH_SHORT);
                    }

                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Write failed
                            Log.e(TAG, e.getMessage());
                            Toast.makeText(RegisterActivity.this, "Register Failed", Toast.LENGTH_SHORT);
                        }
                    });
            ;
        }
    }

//    private void writeNewUser(View view) {
//        String id = mDatabase.child("users").push().getKey();
//        Log.e("key",id);
//        User user = new User(email, name);
//
//        mDatabase.child("users").child(id).setValue(user);
//    }
}
