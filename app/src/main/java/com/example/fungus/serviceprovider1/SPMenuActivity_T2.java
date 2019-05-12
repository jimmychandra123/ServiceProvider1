package com.example.fungus.serviceprovider1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.fungus.serviceprovider1.ui.spmenuactivityt2.SpmenuActivityT2Fragment;

public class SPMenuActivity_T2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.spmenu_activity__t2_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, SpmenuActivityT2Fragment.newInstance())
                    .commitNow();
        }
    }
}
