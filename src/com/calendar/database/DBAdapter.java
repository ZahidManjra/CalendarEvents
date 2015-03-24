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
	//declare db variable for manage sql query data
	private static SQLiteDatabase db;
	
	public DBAdapter(Context ctx) 
	{
		// get activity context
		this.context = ctx;
		// create DBHelper variable  of DatabaseHelper for createing database
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
	/**
	 *  get Writable database Object to give a permission for write a data
	 * @return SQLiteDatabase Object
	 * @throws SQLException
	 */
	public DBAdapter open() throws SQLException 
	{
		//Create and/or open a database that will be used for reading and writing. 
		db = DBHelper.getWritableDatabase();
		return this;
	}

	// ---closes the database---
	/**
	 *   close the current database object
	 */
	public void close() {
		//Close any open database object.
		DBHelper.close();
	}

	/**
	 *  get All Event List
	 * @return Cursor
	 * @throws SQLException
	 */
	
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

	/**
	 *  Update Event Records
	 * @param tableName
	 * @param id
	 * @param event_name
	 * @param event_time
	 * @param event_des
	 * @param image_path
	 * @param event_date
	 * @return
	 */
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
	
	/**
	 *  delete all Records
	 * @return Cursor
	 */
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

	/**
	 * delete single record to pass id param
	 * @param id
	 * @return
	 */
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

	/**
	 *  Add new record
	 * @param tableName
	 * @param event_name
	 * @param event_time
	 * @param event_des
	 * @param image_path
	 * @param event_date
	 * @return
	 */
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

	/**
	 *  get list of before 24 hours events
	 * @param date
	 * @return
	 * @throws SQLException
	 */
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
