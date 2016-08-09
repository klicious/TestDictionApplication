package com.ezlife.testdictionapplication.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Kwon on 2016-08-09.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    static final String TAG = "Database Helper";

    Context givenContext;
    String dbName;

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.givenContext = context;
        this.dbName = name;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        createTable(sqLiteDatabase);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void createTable(SQLiteDatabase db) {
        try {
            if(db != null) {
                // total Data Table
                db.execSQL("CREATE TABLE if not exists " + KEY.KEY_TABLE_DEFAULT + "("
                        + KEY.KEY_ID + " integer PRIMARY KEY autoincrement, "
                        + KEY.KEY_USERNAME + " text UNIQUE not null, "
                        + KEY.KEY_CATEGORY + " text, "
                        + KEY.KEY_PROGRAM + " text, "
                        + KEY.KEY_SEASON + " integer, "
                        + KEY.KEY_EPISODE + " integer, "
                        + KEY.KEY_LINE_NUMBER + " integer, "
                        + KEY.KEY_SCORE + " text, "
                        + KEY.KEY_TIME_STAMP + " DATETIME"
                        + ")");
            } else {
                Log.d(TAG, "Must open Database First!!");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
