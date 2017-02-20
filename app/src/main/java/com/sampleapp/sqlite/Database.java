package com.sampleapp.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.sampleapp.model.WorldPojo;

import java.util.ArrayList;


public class Database extends SQLiteOpenHelper {

    public static String Db_name = "TestingApp";
    public static String Table_name = "";

    public String createTable = "create table myData " +
            "(id integer primary key, rank text,population text,country text, image text)";


    public Database(Context context) {
        super(context, Db_name, null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS myData");
        onCreate(db);
    }

    public int numberOfRows() {
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, "myData");
        return numRows;
    }

    public void deleteData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + "myData");
        db.close();
    }


    public boolean addData(String rank, String population, String country, String image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("rank", rank);
        contentValues.put("population", population);
        contentValues.put("country", country);
        contentValues.put("image", image);
        db.insert("myData", null, contentValues);
        return true;
    }

    public ArrayList<WorldPojo> getallData() {
        ArrayList<WorldPojo> array_list = new ArrayList<WorldPojo>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res = db.rawQuery("select * from myData", null);
        res.moveToFirst();

        while (res.isAfterLast() == false) {
            WorldPojo dataObject = new WorldPojo();
            dataObject.setRank(res.getString(res.getColumnIndex("rank")));
            dataObject.setCountry(res.getString(res.getColumnIndex("country")));
            dataObject.setPopulation(res.getString(res.getColumnIndex("population")));
            dataObject.setFlag_image(res.getString(res.getColumnIndex("image")));
            array_list.add(dataObject);
            res.moveToNext();
        }

        return array_list;

    }

}