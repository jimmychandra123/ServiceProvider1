package com.example.fungus.serviceprovider1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.example.fungus.serviceprovider1.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    String TAG = "RegisterActivity";
    EditText editGmail,editUserName;
    Switch switchUserType;
    int userType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        editGmail = findViewById(R.id.editGmail);
        editUserName = findViewById(R.id.editUserName);
        switchUserType = findViewById(R.id.switchUserType);
//        Log.e("key",mDatabase.child("users").push().getKey());

        editGmail.setEnabled(false);

        sharedPreferences = getApplicationContext().getSharedPreferences("authentication",0);
        Log.e("Check Sign In",Boolean.toString(sharedPreferences.contains("authentication")));
        if(sharedPreferences.getString("user_id",null)!=null){
            editGmail.setText(sharedPreferences.getString("user_id",null));
        }
//        editor = sharedPreferences.edit();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.e(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    Toast.makeText(getApplicationContext(),"Successfully signed in with: " + user.getEmail(),Toast.LENGTH_SHORT);
                } else {
                    // User is signed out
                    Log.e(TAG, "onAuthStateChanged:signed_out");
                    Toast.makeText(getApplicationContext(),"Successfully signed out.",Toast.LENGTH_SHORT);
                }
                // ...
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    public void writeNewUser(View view) {
        FirebaseUser fbUser = mAuth.getCurrentUser();
        String id = fbUser.getUid();
        userType = 0;
        Log.e("key",id);
        String email = editGmail.getText().toString();
        String name = editUserName.getText().toString();

        if(switchUserType.isChecked()){
            userType = 1;
        }else{
            userType = 0;
        }
        User user = new User(id,email, name,userType);
        Task a = mDatabase.child("users").child(id).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // Write was successful!
                Toast.makeText(RegisterActivity.this,"Register Success",Toast.LENGTH_SHORT);
                Log.e(TAG,"register success");
                if(userType==0){
                    startActivity(new Intent(RegisterActivity.this,MapsMainActivity_T.class));
                    finish();
                }else if(userType==1){
                    startActivity(new Intent(RegisterActivity.this,SPNavigationDrawerActivity.class));
                    finish();
                }else{
                    Toast.makeText(RegisterActivity.this,"Error",Toast.LENGTH_SHORT);
                }

            }
        })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Write failed
                    Log.e(TAG,e.getMessage());
                    Toast.makeText(RegisterActivity.this,"Register Failed",Toast.LENGTH_SHORT);
                }
            });;
    }

//    private void writeNewUser(View view) {
//        String id = mDatabase.child("users").push().getKey();
//        Log.e("key",id);
//        User user = new User(email, name);
//
//        mDatabase.child("users").child(id).setValue(user);
//    }
}
