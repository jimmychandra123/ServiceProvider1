package com.example.fungus.serviceprovider1.sql;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.text.TextUtils;
import android.util.Log;

import com.example.fungus.serviceprovider1.R;
import com.example.fungus.serviceprovider1.model.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class DatabaseTable {

    private static final String TAG = "DictionaryDatabase";

    //The columns we'll include in the dictionary table
    public static final String COL_ID = "_id";
    public static final String COL_WORD = "WORD";
    public static final String COL_DEFINITION = "DEFINITION";

    private static final String DATABASE_NAME = "DICTIONARY";
    private static final String FTS_VIRTUAL_TABLE = "FTS";
    private static final int DATABASE_VERSION = 5;

    //User table columns
    public static final String COL_USER_ID = "USER_ID";
    public static final String COL_USER_PASSWORD = "USER_PASSWORD";
    private static final String USER_TABLE = "USER";

    private final DatabaseOpenHelper databaseOpenHelper;

    public DatabaseTable(Context context) {
        databaseOpenHelper = new DatabaseOpenHelper(context);
    }

    private static class DatabaseOpenHelper extends SQLiteOpenHelper {

        private final Context helperContext;
        private SQLiteDatabase mDatabase;
        public static final String FTS_TABLE_CREATE = "CREATE TABLE " + FTS_VIRTUAL_TABLE
                + " (" +COL_ID+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_WORD + " TEXT, "
                + COL_DEFINITION + " TEXT)";

        public static final String USER_TABLE_CREATE = "CREATE TABLE " + USER_TABLE
                + " (" +COL_ID+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_USER_ID + " TEXT, "
                + COL_USER_PASSWORD + " TEXT)";

//        private static final String FTS_TABLE_CREATE =
//                "CREATE TABLE " + FTS_VIRTUAL_TABLE +
//                        " fts3 (" +
//                        COL_WORD + ", " +
//                        COL_DEFINITION + ")";

        DatabaseOpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            helperContext = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            mDatabase = db;
            mDatabase.execSQL(FTS_TABLE_CREATE);
            mDatabase.execSQL(USER_TABLE_CREATE);
            loadDictionary();

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + FTS_VIRTUAL_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE);
            onCreate(db);
        }
        private void loadDictionary() {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        loadWords();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }).start();
        }

        private void loadWords() throws IOException {
//            final Resources resources = helperContext.getResources();
//            String[] countries = resources.getStringArray(R.array.countries);
//            for (String line : countries) {
//                long id = addWord(line.trim(),line.trim());
//                    if (id < 0) {
//                        Log.e(TAG, "unable to add word: " + line.trim());
//                    }
//            }
            final Resources resources = helperContext.getResources();
            InputStream inputStream = resources.openRawResource(R.raw.definitions);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] strings = TextUtils.split(line, "-");
                    if (strings.length < 2) continue;
                    long id = addWord(strings[0].trim(), strings[1].trim());
                    if (id < 0) {
                        Log.e(TAG, "unable to add word: " + strings[0].trim());
                    }
                }
            } finally {
                reader.close();
            }
        }

        public long addWord(String word, String definition) {
            ContentValues initialValues = new ContentValues();
            initialValues.put(COL_WORD, word);
            initialValues.put(COL_DEFINITION, definition);

            return mDatabase.insert(FTS_VIRTUAL_TABLE, null, initialValues);
        }

    }

    public Cursor getWordMatches(String query, String[] columns) {
        String selection = COL_WORD + " LIKE ?";
        String[] selectionArgs = new String[] {query+"*"};

        return query(query, selectionArgs, columns);

//        return query(selection, selectionArgs, columns);
    }

    private Cursor query(String selection, String[] selectionArgs, String[] columns) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(FTS_VIRTUAL_TABLE);
        Log.e("Cursor run","null");
//        Cursor cursor = builder.query(databaseOpenHelper.getReadableDatabase(),
//                columns, selection, selectionArgs, null, null, null);
        String query = "SELECT * FROM "+FTS_VIRTUAL_TABLE+" WHERE "+COL_WORD+" LIKE '%"+selection+"%'";
        Cursor cursor = databaseOpenHelper.getReadableDatabase().rawQuery(query,null);
        if (cursor == null) {
            Log.e("Cursor error","null");
            return null;
        } else if (!cursor.moveToFirst()) {
            Log.e("Cursor error","weird");
            return null;
        }
        return cursor;
    }

    public float fnInsertUser(User meUser) {
        float retResult = 0;
        SQLiteDatabase db = databaseOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USER_ID, meUser.getUser_ID());
        values.put(COL_USER_PASSWORD, meUser.getUser_Password());
        retResult = db.insert(USER_TABLE, null, values);
        return retResult;
    }

    public User fnGetUser(int intUserId) {
        User modelUser = new User();
        String strSelQry = "Select * from " + USER_TABLE + " where " + COL_ID + " = " + intUserId;
        Cursor cursor = databaseOpenHelper.getReadableDatabase().rawQuery(strSelQry, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        modelUser.setId(cursor.getString(cursor.getColumnIndex(COL_ID)));
        modelUser.setUser_ID(cursor.getString(cursor.getColumnIndex(COL_USER_ID)));
        modelUser.setUser_Password(cursor.getString(cursor.getColumnIndex(COL_USER_PASSWORD)));
        return modelUser;
    }

    public List<User> fnGetAllUser()

    {
        List<User> listUser = new ArrayList<User>();
        String strSelAll = "Select * from " + USER_TABLE;
        Cursor cursor = databaseOpenHelper.getReadableDatabase().rawQuery(strSelAll, null);
        if (cursor.moveToFirst()) {
            do {
                User modelUser = new User();
                modelUser.setId(cursor.getString(cursor.getColumnIndex(COL_ID)));
                modelUser.setUser_ID(cursor.getString(cursor.getColumnIndex(COL_USER_ID)));
                modelUser.setUser_Password(cursor.getString(cursor.getColumnIndex(COL_USER_PASSWORD)));
                listUser.add(modelUser);
            } while (cursor.moveToNext());
        }
        return listUser;
    }

    public int fnUpdateUser(User meUser) {
        int retResult = 0;
        ContentValues values = new ContentValues();
        values.put(COL_USER_ID, meUser.getUser_ID());
        values.put(COL_USER_PASSWORD, meUser.getUser_Password());
        String[] argg = {String.valueOf(meUser.getId())};
        retResult = databaseOpenHelper.getWritableDatabase().update(USER_TABLE, values, COL_ID + " = ?", argg);
        return retResult;
    }

}
