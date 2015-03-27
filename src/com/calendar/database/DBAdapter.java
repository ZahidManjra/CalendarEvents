package com.calendar.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.calendar.share.Share;

public class DBAdapter 
{
	// declare context variable for all activity context
	private final Context context;
	//declare DBHelper variable for creating database
	private static DatabaseHelper DBHelper;
	//declare db variable to manage sql query data
	private static SQLiteDatabase db;
	
	public DBAdapter(Context ctx) 
	{
		// get activity context
		this.context = ctx;
		// create DBHelper variable of DatabaseHelper for creating the database
		DBHelper = new DatabaseHelper(context);
	}
	public static class DatabaseHelper extends SQLiteOpenHelper
	{
		DatabaseHelper(Context context) 
		{
			super(context,Share.DATABASE_NAME, null,Share.DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// db.execSQL(DATABASE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			//If database exists then upgrade
			onCreate(db);
		}
	}

	// ---opens the database---
	
	public DBAdapter open() throws SQLException 
	{
		//Create and/or open a database that will be used for reading and writing. 
		db = DBHelper.getWritableDatabase();
		return this;
	}

	// ---closes the database---
	
	public void close() {
		//Close any open database object.
		DBHelper.close();
	}
	
	// get all event list

	public Cursor get_calender_list() throws SQLException {

		open();
		//Runs the provided SQL and returns a Cursor over the result set.
		Cursor mCursor = db.rawQuery("select * from "+Share.CALENDAR_TABLE+" ORDER BY "+ Share.CALENDAR.EVENT_TIME, null);
		//check Cursor the result set.
		if (mCursor != null) 
		{
			if (mCursor.getCount() > 0) {
				mCursor.moveToFirst();
			}
		}
		close();

		return mCursor;
	}

	
	 // Update Event Records
	
	public Boolean update_calender(String tableName, String id,String event_name, String event_time, String event_des,String image_path, String event_date) {
		open();
		//Runs the provided SQL and returns a Cursor over the result set.
		Cursor mCursor = db.rawQuery("update " + Share.CALENDAR_TABLE + " set "
				+ Share.CALENDAR.EVENT_NAME + "='" + event_name + "',"
				+ Share.CALENDAR.EVENT_TIME + "='" + event_time + "',"
				+ Share.CALENDAR.EVENT_DES + "='" + event_des + "',"
				+ Share.CALENDAR.EVENT_IMG_PATH + "='" + image_path + "',"
				+ Share.CALENDAR.EVENT_DATE + "='" + event_date 
				+ "'where "
				+ Share.CALENDAR.ID + "= '" + id + "'", null);
		//check Cursor the result set.
		if (mCursor != null) {
			mCursor.moveToFirst();
		}

		mCursor.close();
		return true;

	}
	
	
	 // delete all records
	 
	public Cursor delete_calender_list() {
		open();
		//Runs the provided SQL and returns a Cursor over the result set.
		Cursor mCursor = db.rawQuery("delete from "+Share.CALENDAR_TABLE, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		close();
		return mCursor;
	}

	// delete single record 
	
	public Cursor delete_calender(String id) {
		open();
		//Runs the provided SQL and returns a Cursor over the result set.
		Cursor mCursor = db.rawQuery("delete from "+Share.CALENDAR_TABLE+" where "+Share.CALENDAR.ID+"='" + id	+ "'", null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		close();
		return mCursor;
	}

	
	  // Add a new record
	 
	public boolean insert_calender_list(String tableName, String event_name,String event_time, String event_des, String image_path,	String event_date) {
		open();
		db = DBHelper.getWritableDatabase();

		//Creates an empty set of values using the default initial size
		ContentValues values = new ContentValues();

		values.put(Share.CALENDAR.EVENT_NAME, event_name);
		values.put(Share.CALENDAR.EVENT_TIME, event_time);
		values.put(Share.CALENDAR.EVENT_DES, event_des);
		values.put(Share.CALENDAR.EVENT_IMG_PATH, image_path);
		values.put(Share.CALENDAR.EVENT_DATE, event_date);

		//Convenience method for inserting a row into the database. 
		db.insert(tableName, null, values);
		close();
		return true;
	}

	
	 // get event list for the calendar date
	 
	public Cursor getTomorrow_Calender_EventList(String date) throws SQLException {
		open();
		//Runs the provided SQL and returns a Cursor over the result set.
		Cursor mCursor = db.rawQuery("SELECT * FROM "+Share.CALENDAR_TABLE+" WHERE "+Share.CALENDAR.EVENT_DATE+"='"+date+"'", null);
		if (mCursor != null) {
			if (mCursor.getCount() > 0) {
				mCursor.moveToFirst();
			}
		}
		close();
		return mCursor;
	}
}
