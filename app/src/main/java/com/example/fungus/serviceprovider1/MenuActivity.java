package com.example.fungus.serviceprovider1;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class MenuActivity extends Fragment{

    Intent intent;
    EditText editQuery;
    Button btnSearch;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_menu,null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        intent = new Intent(view.getContext(),SearchableActivity.class);

        editQuery = view.findViewById(R.id.editQuery);
//        btnSearch = view.findViewById(R.id.searchButton);
        view.findViewById(R.id.searchButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fnSendQuery(view);
            }
        });
    }


    //    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_menu);
//
//        intent = new Intent(this,SearchableActivity.class);
//        editQuery = findViewById(R.id.editQuery);
//
//    }


    public void fnSendQuery(View view) {
        String query = editQuery.getText().toString();
        Log.e("queryMenu",query);
        intent.putExtra("query",query);
        startActivity(intent);
    }

}
