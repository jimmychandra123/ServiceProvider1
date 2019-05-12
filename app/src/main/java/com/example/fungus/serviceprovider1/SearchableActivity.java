package com.example.fungus.serviceprovider1;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.fungus.serviceprovider1.sql.DatabaseTable;

public class SearchableActivity extends AppCompatActivity {
    DatabaseTable db = new DatabaseTable(this);
    TextView txt1;
    ListView listView;
    Intent intentR;
    String query;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable);
        listView = (ListView) findViewById(R.id.searchList);
        intentR = getIntent();
        query = intentR.getStringExtra("query");
        txt1 = findViewById(R.id.textView3);
        Log.e("queryMenu",query);

        //        handleIntent(getIntent());
    }

    @Override
    protected void onStart() {
        super.onStart();
        handleIntent();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
//        SearchView searchView = (SearchView)MenuItemCompat.getActionView(searchItem);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        searchView.setQuery(query,true);

//        listView = findViewById(R.id.searchList);

        intentR = new Intent(this,SearchResultActivity.class);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Cursor c = db.getWordMatches(s, null);
                if(c==null){
                    Log.e("Cursor error","null2");
                    listView.setAdapter(null);
                }else{
                    Log.e("Cursor error",c.getString(c.getColumnIndex("WORD")));
                    TodoCursorAdapter todoAdapter = new TodoCursorAdapter(SearchableActivity.this, c);
                    listView.setAdapter(todoAdapter);
                }
//                Log.e("string",s);
//                intentR.putExtra(SearchManager.QUERY,s);
//                startActivity(intentR);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {

                return false;
            }
        });
        return true;
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.search:
//                onSearchRequested();
//                return true;
//            default:
//                return false;
//        }
//    }

    private void handleIntent() {
        Cursor c = db.getWordMatches(query, null);
        if (c == null) {
            Log.e("Cursor error", "null2");
        } else {

            Log.e("Cursor error", c.getString(c.getColumnIndex("WORD")));
            TodoCursorAdapter todoAdapter = new TodoCursorAdapter(this, c);
            listView.setAdapter(todoAdapter);
        }


        //process Cursor and display results
//            ListView lvItems = (ListView) findViewById(R.id.searchList);
//            TodoCursorAdapter todoAdapter = new TodoCursorAdapter(this, c);
//            lvItems.setAdapter(todoAdapter);
    }
}
