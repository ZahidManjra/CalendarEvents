package com.calendar.share;

import java.io.File;

import android.os.Environment;

public class Share {
	
	//default imageview width  and link to CalendarView.java
	public static int width = 0;
	
	//Create mScreenshotPath 
	public static final String IMAGE_PATH = Environment.getExternalStorageDirectory() + "/DCIM/Camera/";

	//Manage current event date link to splash.java,Event.java and CalendarView.java
	public static String selectedDate;
	
	//Manage Current date,month and year link to Event.java and CalendarView.java
	public static int date,year,month;
	
	//create DATABASE_NAME variable to link to DBAdapter.java
	public static final String DATABASE_NAME = "Calendar.sql";
	//create DATABASE_VERSION variable to link to DBAdapter.java
	public static final int DATABASE_VERSION = 1;
	
	//create CALENDAR_TABLE variable to link to DBAdapter.java and Event.java
	public static final String CALENDAR_TABLE="calendar";
	
	//create CALENDAR class for shared key name for event fields and link to CalendarView.java , DBAdapter.java, Event_view.java and Event.java
	public class CALENDAR
	{
		public static final String ID="id";
		public static final String EVENT_NAME="event_name";
		public static final String EVENT_TIME="event_time";
		public static final String EVENT_DES="event_des";
		public static final String EVENT_IMG_PATH="image_path";
		public static final String EVENT_DATE="event_date";
	}
	/**
	 * Check Image File
	 * @return true : Image File is available
	 * <br> false : Image File is not available
	 */
	//creating method for check sdcard is exits or not and link to the Event.java and Event_view.java
	public static boolean ensureSDCardAccess() {
		File file = new File(Share.IMAGE_PATH);
		if (file.exists()) {
			return true;
		} else if (file.mkdirs()) {
			return true;
		}
		return false;
	}
}
