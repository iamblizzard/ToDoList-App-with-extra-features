package com.example.root.todolist;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import android.content.Context;
import android.content.ContentValues;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

public class MyDBHandler extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 7;
    private static final String DATABASE_NAME = "recycler_viewDB.db";
    public static final String TABLE_TASKS = "tasks";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TASKNAME = "taskname";
    public static final String COLUMN_COLOR = "color";
    public static final String COLUMN_DATE = "date";
    public static final String BOOL_NOTIFY = "notify";

    private Context context;

    public MyDBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_TASKS + " ( " +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TASKNAME + " TEXT, " +
                COLUMN_DATE + " TEXT, " +
                COLUMN_COLOR + " TEXT, " +
                BOOL_NOTIFY + " integer " +
                ");";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
        onCreate(db);
    }

    //Add a new row to the database
    public void addTask(Tasks task){
        ContentValues values = new ContentValues();
        values.put(COLUMN_TASKNAME, task.get_taskname());
        values.put(COLUMN_DATE, task.get_date());
        values.put(COLUMN_COLOR, "low_priority");
        values.put(BOOL_NOTIFY, 1);
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_TASKS, null, values);
        db.close();
    }

    //returns color by taskname from database
    public String show_color(String taskname){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT color from tasks where taskname= \""+taskname+"\";",null);
        c.moveToFirst();
        return c.getString(c.getColumnIndex("color"));
    }

    //Delete a product from the database
    public void deleteTask(String taskName){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_TASKS + " WHERE " + COLUMN_TASKNAME + "=\"" + taskName + "\";");
        closeDB();
    }

    public void updateColor(String color, String task){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " + TABLE_TASKS + " SET " + COLUMN_COLOR + "=\"" +color+ "\" WHERE " + COLUMN_TASKNAME+ "=\"" +task+ "\";");
        db.close();
    }

    public void updateNotify(int notify, String task){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " + TABLE_TASKS + " SET " + BOOL_NOTIFY + "= " +notify+ " WHERE " + COLUMN_TASKNAME+ "=\"" +task+ "\";");
        db.close();
    }


    // this is goint in record_TextView in the Main activity.
    public ArrayList<Tasks> databaseToString(){
        ArrayList<Tasks> dbString = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_TASKS + " WHERE 1";// why not leave out the WHERE  clause?

        //Cursor points to a location in your results
        Cursor recordSet = db.rawQuery(query, null);
        //Move to the first row in your results
        recordSet.moveToFirst();

        //Position after the last row means the end of the results
        Tasks task;
        while (!recordSet.isAfterLast()) {
            // null could happen if we used our empty constructor
            if (recordSet.getString(recordSet.getColumnIndex("_id")) != null) {
                task = new Tasks(recordSet.getString(recordSet.getColumnIndex("taskname")),
                        recordSet.getString(recordSet.getColumnIndex("date")), recordSet.getString(recordSet.getColumnIndex("color")),
                        recordSet.getInt(recordSet.getColumnIndex("notify")));
                dbString.add(task);

            }
            recordSet.moveToNext();
        }
        db.close();
        return dbString;
    }

    public void clearTask() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_TASKS + " WHERE 1";// why not leave out the WHERE  clause?
        db.execSQL(query);
        db.close();
    }

    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }

}

