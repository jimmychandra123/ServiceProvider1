package com.example.fungus.serviceprovider1;

import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import com.example.fungus.serviceprovider1.sql.DatabaseTable;

public class SearchResultActivity extends AppCompatActivity {
    DatabaseTable db = new DatabaseTable(this);
//    ListView ls;
    TextView txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        txt = findViewById(R.id.textView2);
        handleIntent(getIntent());
//        ls = findViewById(R.id.listResult);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            ListView listView = findViewById(R.id.searchList2);
            String query = intent.getStringExtra(SearchManager.QUERY);
            Log.e("query",query);
            Cursor c = db.getWordMatches(query, null);
            //process Cursor and display results

            if(c==null){
                Log.e("Cursor error","null2");
            }else{

                Log.e("Cursor error",c.getString(c.getColumnIndex("WORD")));
                TodoCursorAdapter todoAdapter = new TodoCursorAdapter(this, c);
                listView.setAdapter(todoAdapter);
            }

        }
    }
}
