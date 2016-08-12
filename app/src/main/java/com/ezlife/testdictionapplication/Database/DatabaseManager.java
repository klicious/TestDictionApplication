package com.ezlife.testdictionapplication.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.ezlife.testdictionapplication.Script;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Kwon on 2016-08-09.
 */
public class DatabaseManager {
    SQLiteDatabase db;
    DatabaseHelper dbHelper;
    Context context;
    String dbName;


    public Context getContext() { return context; }

    public void setContext(Context ct) { this.context = ct;}

    public DatabaseManager(Context ct) {
        context = ct;
        init();
    }

    private void init() {
        dbName = KEY.KEY_TABLE_DEFAULT;
        openDatabase();
    }

    public Script getLastScript() {
        Script lastActivity = new Script();
        Cursor c = null;
        try {
/*
            String query = "SELECT * FROM "
                    + KEY.KEY_TABLE_DEFAULT
                    + " WHERE " + KEY.KEY_ID
                    + "(SELECT MAX(" + KEY.KEY_ID + ") FROM "
                    + KEY.KEY_TABLE_DEFAULT
                    + ")";
*/

            String where = KEY.KEY_ID + "(SELECT MAX(" + KEY.KEY_ID + ") FROM " + KEY.KEY_TABLE_DEFAULT;
            c = db.query(KEY.KEY_TABLE_DEFAULT, null, where, null, null, null, null);
            /*
             * _id          integer
             * username     text
             * category     text
             * program      text
             * season       integer
             * episode      integer
             * line number  integer
             * score        integer
             * date         datetime(string)
             */
            lastActivity.setUsername(c.getString(1));
            lastActivity.setCategory(c.getString(2));
            lastActivity.setProgram(c.getString(3));
            lastActivity.setSeason(c.getInt(4));
            lastActivity.setEpisode(c.getInt(5));
            lastActivity.setLineNumber(c.getInt(6));
            lastActivity.setScore(c.getInt(7));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
        }

        return lastActivity;
    }

    public SQLiteDatabase openDatabase() {
        try {
             if(dbName != null) {
                 dbHelper = new DatabaseHelper(context, dbName, null, 1);
                 db = dbHelper.getWritableDatabase();
                 return db;
             }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void insertScriptToDatabase(SQLiteDatabase database, Script script) {
        /*
         * Table - Default_Table
         * contents
         *  - username
         *  - category
         *  - program
         *  - season
         *  - episode
         *  - lineNumber
         *
         */

        ContentValues values = new ContentValues();

        values.put(KEY.KEY_USERNAME, script.getUsername());
        values.put(KEY.KEY_CATEGORY, script.getCategory());
        values.put(KEY.KEY_PROGRAM, script.getProgram());
        values.put(KEY.KEY_PROGRAM, script.getProgram());
        values.put(KEY.KEY_SEASON, script.getSeason());
        values.put(KEY.KEY_EPISODE, script.getEpisode());
        values.put(KEY.KEY_LINE_NUMBER, script.getLineNumber());
        values.put(KEY.KEY_TIME_STAMP, getDateTime());

        database.insert(KEY.KEY_TABLE_DEFAULT, null, values);
    }

    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    public void insertScriptToDBAsyncTask (Script script) {
        if (db == null) {
            return;
        }
        insertScriptToDBAsync a = new insertScriptToDBAsync();
        a.execute(script);
    }

    private class insertScriptToDBAsync extends AsyncTask<Object, Void, Object> {
        @Override
        protected Object doInBackground(Object... objects) {
            Script script = (Script) objects[0];
            insertScriptToDatabase(db, script);
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            Log.d("HELLO", "Completed :: insertScriptToDatabase(db, script)");
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("HELLO", "Starting :: insertScriptToDatabase(db, script)");
        }
    }
}
