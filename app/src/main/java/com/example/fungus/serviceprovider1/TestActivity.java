package com.example.fungus.serviceprovider1;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class TestActivity extends AppCompatActivity {
    String strURL = "http://172.16.92.48:8888/webServiceJSON/index.php";

    String token;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_test);

        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task)
            {        token = task.getResult().getToken();
                    Log.e("test",token);
            }});
//        fnNotifcation();

        Button btnSend = findViewById(R.id.btn_send_noti);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fnSendNotifcation();
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void fnNotifcation(){
//        //1. Define the NotificationChannel
//        NotificationChannel ncEmpty = new NotificationChannel("DailyExpenses", "Daily Expenses", NotificationManager.IMPORTANCE_DEFAULT);
////2. Set which activity that will opened when user click on the notification.
//        Intent notiIntent = new Intent(getApplicationContext(),TestActivity.class);
////3. Add the intent into TaskStackBuilder
//        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);stackBuilder.addNextIntent(notiIntent);
////4. Define PendingIntent
//           PendingIntent pendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
////5. Define the NotificationCompat and the messages content
//            NotificationCompat.Builder builder = new NotificationCompat.Builder(this,"DailyExpenses");
//        builder.setContentIntent(pendingIntent).setContentTitle("Stupid").setContentText("Just Want to notify you!").setSmallIcon(R.mipmap.ic_launcher).setAutoCancel(true);
////6. Define and call NotificationManager that the app is going to perform push notification
//        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
//            manager.createNotificationChannel(ncEmpty);
//        }
//        manager.notify(1,builder.build());

    }

    public void fnSendNotifcation(){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, strURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Log.e("a",response);
                } catch (Exception ee) {

                    Log.e("onReponse", ee.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("ErrorListner",error.getMessage());
            }
        })
        {
            @Override
            protected Map<String,String> getParams()throws AuthFailureError {
                Map<String,String> params = new HashMap<String,String>();
                params.put("title","Test Title");
                params.put("message","Test Message");
                params.put("push_type","individual");
                params.put("regId","eqsjDyx-RS0:APA91bGq3ZqX6CgKmCNJ9Hec8RJ1EZ1wGCmFID8Go-uPydZJpJUgOLTiZnHlrkyWv1ZoFUd84VHzDtlXwYiJTlpaUXZ9Nsb5q5w5DxxZanvWe9cTOpvbt0Q2hAQZyy4vc4k_N47MB6Mz");
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }




}