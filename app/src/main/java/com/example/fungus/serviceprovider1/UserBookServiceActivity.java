package com.example.fungus.serviceprovider1;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.fungus.serviceprovider1.model.Booking;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;

public class UserBookServiceActivity extends AppCompatActivity {
    private TextView b_serviceDate,b_serviceTime,b_serviceName;
    private Button btnBookDate,btnBookTime,btnBook;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private TimePickerDialog.OnTimeSetListener onTimeSetListener;
    private DatabaseReference databaseReference;
    private Intent intent2;

    String s_name,sp_id,s_id,time,date;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_book_service);

        intent2 = new Intent(this,UserManageBookingActivity.class);
        Intent intent = getIntent();
        s_name = intent.getStringExtra("s_name");
        sp_id = intent.getStringExtra("sp_id");
        s_id = intent.getStringExtra("s_id");

        databaseReference = FirebaseDatabase.getInstance().getReference();
        b_serviceName = findViewById(R.id.b_serviceName);
        b_serviceName.setText(s_name);

        b_serviceDate = findViewById(R.id.b_serviceDate);
        btnBookDate = findViewById(R.id.btnBDate);
        btnBookDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fnCreateDatePickerDialog();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                i1++;
                date = i2+"/"+i1+"/"+i;
                b_serviceDate.setText(date);
            }
        };

        b_serviceTime = findViewById(R.id.b_serviceTime);
        btnBookTime = findViewById(R.id.btnBTime);
        btnBookTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fnCreateTimePickerDialog();
            }
        });

        onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                time = i+":"+i1;
                b_serviceTime.setText(time);
            }
        };

        btnBook = findViewById(R.id.btn_book);
        btnBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(date==null){
                    makeMessage("Pick a date");
                }else if(time == null){
                    makeMessage("Pick a time");
                }else if(s_id!=null&&sp_id!=null){
                    fnInsertDB();
                }
            }
        });
    }

    public void fnInsertDB(){
//        service = new Service(id,markerLat,markerLong,S_name,stateSelection,typeSelection,SP_id);
//        Search search = new Search(id,"barber",stateSelection,markerLat,markerLong);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        final String u_id = auth.getCurrentUser().getUid();
        final String id = databaseReference.child("Booking").push().getKey();
        final Booking booking = new Booking(id,date,time,s_id,sp_id,u_id);
        final HashMap<String,String> a = new HashMap<>();
        a.put("b_id",id);

        databaseReference.child("Booking").child(id).setValue(booking).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                databaseReference.child("U_Book").child(u_id).child(id).setValue(booking).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        databaseReference.child("S_Book").child(sp_id).child(id).setValue(booking).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                makeMessage("Booking Added");
                                startActivity(intent2);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                makeMessage("S_Book Failed");
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        makeMessage("U_Book Failed");
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

    public void fnCreateDatePickerDialog(){
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(UserBookServiceActivity.this,R.style.Theme_AppCompat_DayNight_Dialog_MinWidth,mDateSetListener,year,month,day);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        dialog.show();
    }

    public void fnCreateTimePickerDialog(){
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);

        TimePickerDialog dialog = new TimePickerDialog(UserBookServiceActivity.this,onTimeSetListener,hour,minute,true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        dialog.show();
    }

    public void makeMessage(String str){
        Toast.makeText(getApplicationContext(),str,Toast.LENGTH_SHORT).show();
    }
}
