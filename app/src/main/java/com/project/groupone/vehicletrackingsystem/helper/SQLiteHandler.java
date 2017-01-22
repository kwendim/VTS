package com.project.groupone.vehicletrackingsystem.helper;

/**
 * Created by kidus wendimagegn on 1/2/17.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

public class SQLiteHandler extends SQLiteOpenHelper {
    private static final String TAG = SQLiteHandler.class.getSimpleName();

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "VTS";

    // Login table name
    private static final String TABLE_USER = "user";

    // Login Table Columns names
    private static final String KEY_PID = "PID";
    private static final String KEY_UID = "UID";
    private static final String KEY_FNAME = "FName";
    private static final String KEY_MNAME = "MName";
    private static final String KEY_LNAME = "LName";
    private static final String KEY_EMAIL = "Email";
    private static final String KEY_SEX = "Sex";
    private static final String KEY_BIRTHDAY = "BirthDay";
    private static final String KEY_TEL="Tel";
    private static final String KEY_ADDRESS = "Address";
    private static final String KEY_REG_DATE = "RegDate";
    private static final String KEY_UPDATED_DATE = "UpdateDate";
    private static final String KEY_CREATED_DATE = "CreatedDate";
    private static final String KEY_PHOTO = "Photo";

    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + KEY_UID + " INTEGER PRIMARY KEY," + KEY_PID + " INTEGER UNIQUE," + KEY_FNAME + " TEXT,"
                + KEY_MNAME + " TEXT," + KEY_LNAME + " TEXT,"
                + KEY_EMAIL + " TEXT UNIQUE," + KEY_SEX + " TEXT,"
                + KEY_BIRTHDAY + " TEXT," + KEY_TEL + " TEXT," + KEY_ADDRESS + " TEXT," + KEY_PHOTO + " TEXT,"
                + KEY_REG_DATE + " TEXT," + KEY_UPDATED_DATE + " TEXT,"
                + KEY_CREATED_DATE + " TEXT" + ")";

        db.execSQL(CREATE_LOGIN_TABLE);

        String GPSData = "CREATE TABLE GPSData(GID TEXT PRIMARY KEY,Lat REAL,LON REAL,Bearing TEXT,Time TEXT)";
        db.execSQL(GPSData);

        String Vehicles = "CREATE TABLE Vehicles(VID TEXT PRIMARY KEY,UID INTEGER,GID TEXT," +
                "DID INTEGER,BrandName TEXT,ModelNumber TEXT,EngineCC INTEGER,Color TEXT,Image TEXT,Name TEXT,Status INTEGER)";
        db.execSQL(Vehicles);

        String Drivers = "CREATE TABLE Drivers(DID INTEGER PRIMARY KEY,"
                + KEY_PID + " INTEGER UNIQUE," + KEY_FNAME + " TEXT,"
                + KEY_MNAME + " TEXT," + KEY_LNAME + " TEXT,"
                + KEY_SEX + " TEXT," + KEY_BIRTHDAY + " TEXT," + KEY_TEL + " TEXT,"
                + KEY_ADDRESS + " TEXT," + KEY_PHOTO + " TEXT,"
                + KEY_REG_DATE + " TEXT,Agent TEXT,IsAssigned INTEGER)";

        db.execSQL(Drivers);

        Log.d("Drivers", Drivers);


        Log.d(TAG, db.toString());
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + "GPSData");
        db.execSQL("DROP TABLE IF EXISTS " + "Vehicles");
        db.execSQL("DROP TABLE IF EXISTS " + "Drivers");
        // Create tables again
        onCreate(db);
    }

    /**
     * Storing user details in database
     * */
    public void addUser(String uid, String pid, String fname, String mname, String lname, String email, String sex, String bday, String tel, String address,String regdate,String updateddate,String createddate, String photo) {


        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_UID, uid);
        values.put(KEY_PID, pid);
        values.put(KEY_FNAME, fname);
        values.put(KEY_MNAME, mname);
        values.put(KEY_LNAME, lname);
        values.put(KEY_EMAIL, email);
        values.put(KEY_SEX, sex);
        values.put(KEY_BIRTHDAY, bday);
        values.put(KEY_TEL, tel);
        values.put(KEY_ADDRESS, address);
        values.put(KEY_REG_DATE, regdate);
        values.put(KEY_UPDATED_DATE,updateddate);
        values.put(KEY_CREATED_DATE,createddate);
        values.put(KEY_PHOTO, photo);

        // Inserting Row
        long id = db.insert(TABLE_USER, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New user inserted into sqlite: " + id);
    }

    /**
     * Getting user data from database
     * */

    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_USER;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user.put("UID", cursor.getString(0));
            user.put("PID", cursor.getString(1));
            user.put("FName", cursor.getString(2));
            user.put("MName", cursor.getString(3));
            user.put("LName", cursor.getString(4));
            user.put("Email", cursor.getString(5));
            user.put("Sex", cursor.getString(6));
            user.put("BirthDay", cursor.getString(7));
            user.put("Tel", cursor.getString(8));
            user.put("Address", cursor.getString(9));
            user.put("Photo", cursor.getString(10));
            user.put("RegDate",cursor.getString(11));
            user.put("UpdatedDate",cursor.getString(12));
            user.put("CreatedDate", cursor.getString(13));
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching user from Sqlite: " + user.toString());

        return user;
    }

    /**
     * Re crate database Delete all tables and create them again
     * */
    public void deleteUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_USER, null, null);
        db.delete("Vehicles",null,null);
        db.delete("GPSData",null,null);
        db.delete("Drivers",null,null);
        db.close();

        Log.d(TAG, "Deleted all user info from sqlite");

        //TODO: Delete user profile while we are at it

    }

    public void insertPhoto(String photo_link, String uid){
        String query = "UPDATE " + TABLE_USER + " SET " + KEY_PHOTO +" = '" + photo_link + "' WHERE " + KEY_UID + " = " + uid;
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(query);
        db.close();

    }
    public void insertVehiclePhoto(String photo_link, String vid){
        String query = "UPDATE Vehicles SET Image = '" + photo_link + "' WHERE VID = '" + vid + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(query);
        db.close();
    }


    public void addVehicle(String vid, String uid, String gid,String did, String brandname , String modelno, String enginecc, String color, String image,String name, String status) {
        //String Vehicles = "CREATE TABLE Vehicles(VID TEXT PRIMARY KEY,UID INTEGER PRIMARY KEY,GID TEXT PRIMARY KEY," +
          //      "BrandName TEXT,ModelNumber TEXT,EngineCC INTEGER,Color TEXT,Image TEXT,Name TEXT,Status INTEGER)";

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("VID", vid);
        values.put("UID", Integer.valueOf(uid));
        values.put("GID", Integer.valueOf(gid));
        values.put("DID",did);
        values.put("BrandName", brandname);
        values.put("ModelNumber", modelno);
        values.put("EngineCC", Integer.valueOf(enginecc));
        values.put("Color", color);
        values.put("Image", image);
        values.put("Name", name);
        values.put("Status", status);


        // Inserting Row
        long id = db.insert("Vehicles", null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New Vehicle inserted into sqlite: " + vid);
    }


    public List<HashMap<String,String>> getVehicleDetails() {

        List<HashMap<String,String>> vehicleDetails = new ArrayList<>();
        HashMap<String, String> vehicles;

        String selectQuery = "SELECT  * FROM Vehicles";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);


        if(cursor.getCount() > 0) {
            while (cursor.moveToNext()) {

                vehicles = new HashMap<String, String>();
                vehicles.put("VID", cursor.getString(0));
                vehicles.put("UID", cursor.getString(1));
                vehicles.put("GID", cursor.getString(2));
                vehicles.put("DID", cursor.getString(3));
                vehicles.put("BrandName", cursor.getString(4));
                vehicles.put("ModelNumber", cursor.getString(5));
                vehicles.put("EngineCC", cursor.getString(6));
                vehicles.put("Color", cursor.getString(7));
                vehicles.put("Image", cursor.getString(8));
                vehicles.put("Name", cursor.getString(9));
                vehicles.put("Status", cursor.getString(10));
                vehicleDetails.add(vehicles);

            }

        }
        cursor.close();
        db.close();
        Log.d(TAG, "Fetching vehicle from Sqlite: " + vehicleDetails.toString());

        return vehicleDetails;
    }

    public void removeVehicle(String vid) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("Vehicles", "VID = " + vid ,null);
        db.close();
        Log.d(TAG, "Deleted Vehicle " + vid + " info from sqlite");

    }

    public List<HashMap<String, String>> getDriversDetails() {

        List<HashMap<String,String>> DriverDetails = new ArrayList<>();
        HashMap<String, String> driver;

        String selectQuery = "SELECT  * FROM Drivers";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {

                driver = new HashMap<String, String>();

                driver.put("DID", cursor.getString(0));
                driver.put("PID", cursor.getString(1));
                driver.put("FName", cursor.getString(2));
                driver.put("MName", cursor.getString(3));
                driver.put("LName", cursor.getString(4));
                driver.put("Sex", cursor.getString(5));
                driver.put("BirthDay", cursor.getString(6));
                driver.put("Tel", cursor.getString(7));
                driver.put("Address", cursor.getString(8));
                driver.put("Photo", cursor.getString(9));
                driver.put("RegDate", cursor.getString(10));
                driver.put("Agent", cursor.getString(11));
                driver.put("IsAssigned", cursor.getString(12));
                DriverDetails.add(driver);
            }
        }
        cursor.close();
        db.close();
        Log.d(TAG, "Fetching Driver from Sqlite: " + DriverDetails.toString());

        return DriverDetails;
    }


    public void insertDriverPhoto(String photo_link, String did) {
        String query = "UPDATE Drivers SET "+ KEY_PHOTO + " = '" + photo_link + "' WHERE DID = '" + did + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(query);
        db.close();
    }

    public void addDriver(String did, String pid, String fname, String mname, String lname, String sex, String bday, String tel, String address, String photo, String regdate, String agent, String isassigned) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("DID", did);
        values.put(KEY_PID, pid);
        values.put(KEY_FNAME, fname);
        values.put(KEY_MNAME, mname);
        values.put(KEY_LNAME, lname);
        values.put(KEY_SEX, sex);
        values.put(KEY_BIRTHDAY, bday);
        values.put(KEY_TEL, tel);
        values.put(KEY_ADDRESS, address);
        values.put(KEY_PHOTO, photo);
        values.put(KEY_REG_DATE, regdate);
        values.put("Agent",agent);
        values.put("IsAssigned",isassigned);

        // Inserting Row
        long id = db.insert("Drivers", null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New Driver inserted into sqlite: " + did);


    }

    public void removeDriver(String did) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("Drivers", "DID = " + did ,null);
        db.close();
        Log.d(TAG, "Deleted Driver " + did + " info from sqlite");
    }
}
