package com.calendarevent;

import java.util.ArrayList;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.calendar.CalendarDayMarker;
import com.calendar.CalendarView;
import com.calendar.CalendarView.OnMonthChangedListener;
import com.calendar.database.DBAdapter;
import com.calendardemo.R;

public class Cale extends Activity implements Runnable, OnClickListener 
{
	// variables to be used
	
	// declare array for store calendar table column values
	public static ArrayList<String> arr_event_id;
	public static ArrayList<String> arr_event_name;
	public static ArrayList<String> arr_event_time;
	public static ArrayList<String> arr_event_descripation;
	public static ArrayList<String> arr_event_img_path;
	public static ArrayList<String> arr_event_date;

	//declare for Event data rows
	public static LinearLayout lnrLayout_EventList;
	
	//declare for  Calendar view
	public static CalendarView _calendar;
	
	//Declaration for Add Event
	private ImageButton img_plus; 

	//Declaration for Device Screen size
	public static Display mDisplay;  

	@Override
	protected void onCreate(Bundle savedInstanceState)  
	{
		super.onCreate(savedInstanceState);
		// set upload layout
		setContentView(R.layout.cale);
		// set current device screen window and link to the CalendarView.java
		mDisplay = getWindowManager().getDefaultDisplay();
		
		// create Event data rows LinearLayout variable link to LinearLayout in the layout XML
		lnrLayout_EventList = (LinearLayout) findViewById(R.id.forecast_lnrLayoutTransactions);
		
		//create calendar view variable and link to CaleView in layout XML
		_calendar = (CalendarView) findViewById(R.id.forecast_calendarView);

		// start listener on CalendarView press 
		_calendar.setOnMonthChangedListener(new OnMonthChangedListener() {
			public void onMonthChanged(CalendarView view) {
				// load the day view in calendar layout XML
				markDays();
			}
		});

		
		//create calendar event table column data store in variable  and link to the CalendarView.java
		arr_event_id = new ArrayList<String>();
		arr_event_name = new ArrayList<String>();
		arr_event_time = new ArrayList<String>();
		arr_event_descripation = new ArrayList<String>();
		arr_event_img_path = new ArrayList<String>();
		arr_event_date = new ArrayList<String>();

		// create img_plus  ImageButton variable and link to ImageButton in layout XML
		img_plus = (ImageButton) findViewById(R.id.img_plus);
		// start listener on button press
		img_plus.setOnClickListener(this);
	}

	// on startup resume Add Event and View Event
	@Override
	protected void onResume() {
		super.onResume();
		
		//create Thread variable for Re-Load the event data
		Thread thread = new Thread(Cale.this);
		thread.start();
	}

	/**
	 * Create Calendar day view
	 */
	public static void markDays() {
		//set days view in calendarview Layout and link to Calendarview.java
		_calendar.setDaysWithEvents(new CalendarDayMarker[] { new CalendarDayMarker(Calendar.getInstance(), Color.BLUE) });
	}

	/**
	 * Find Current date events
	 * @param year
	 * @param month
	 * @param day
	 * @return true  : if current date is same <br/>
	 *         false : current date not match given param
	 */
	//check Events are available on given year,month and day and Link to the CalendarView.java
	public static boolean check_date_arratlist(int year, int month, int day) 
	{
		for (int i = 0; i < Cale.arr_event_date.size(); i++)
		{
			//create variable for year,months and day for matching param
			String row[] = Cale.arr_event_date.get(i).toString().split("-");
			
			if (Integer.parseInt(row[0]) == year && Integer.parseInt(row[1]) == month && Integer.parseInt(row[2]) == day) {
				return true;
			}
		}
		return false;
	}

	/**
	 *  Retrieve current date events data from database 
	 */
	@Override
	public void run() 
	{
		try 
		{
			//Remove all event column data
			arr_event_id.clear();
			arr_event_name.clear();
			arr_event_time.clear();
			arr_event_descripation.clear();
			arr_event_img_path.clear();
			arr_event_date.clear();
			
			// initialize an instance (DBAdapter)
			DBAdapter dba = new DBAdapter(Cale.this);
			
			// Uses the DBAdapter to execute a request open database  
			dba.open();
			
			// initialize a Cursor which in return query results data 
			Cursor cursor = dba.get_calender_list();
			
			// load the Event fields
			if (cursor.getCount() > 0) {
				try 
				{
					//Move to first row
					cursor.moveToFirst();
					do {
						// save database row into array
						Set_Arraylist(cursor.getString(0), cursor.getString(1),cursor.getString(2), cursor.getString(3),cursor.getString(4), cursor.getString(5));
					} while (cursor.moveToNext());

				} catch (Exception e) {
				}
			}
			// Close the cursor 
			cursor.close();
			// Uses the DBAdapter to execute a request close database
			dba.close();
			
			//update days view
			handler.sendEmptyMessage(0);
		} catch (Exception e) {
			e.printStackTrace();
			handler.sendEmptyMessage(1);
		}
	}
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {

			if (msg.what == 0) {
				try {
					markDays();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	};

	/**
	 * Save Event Fields Data into Array
	 * 
	 * @param id
	 * @param name
	 * @param time
	 * @param des
	 * @param img_path
	 * @param date
	 */
	public void Set_Arraylist(String id, String name, String time, String des,String img_path, String date) 
	{
		Calendar cal1 = Calendar.getInstance();
		
		if (!date.equalsIgnoreCase("")) 
		{
			int date1 = Integer.parseInt(date.split("-")[2]);
			int month1 = Integer.parseInt(date.split("-")[1]);
			int year1 = Integer.parseInt(date.split("-")[0]);
			cal1.set(Calendar.DAY_OF_MONTH, date1);
			cal1.set(Calendar.MONTH, month1 - 1);
			cal1.set(Calendar.YEAR, year1);
			arr_event_id.add(id);
			arr_event_name.add(name);
			arr_event_time.add(time);
			arr_event_descripation.add(des);
			arr_event_img_path.add(img_path);
			arr_event_date.add(date);
		}
	}

	@Override
	public void onClick(View v) 
	{
		if (v == img_plus)         
		{
			// create intent to start the Event activity on  button press (img_plus)
			Intent eventIntent = new Intent(Cale.this, Event.class);  
			startActivity(eventIntent);
			finish();
		}
	}
}