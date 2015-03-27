package com.calendarevent;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;

import com.calendar.database.DBAdapter;
import com.calendar.database.importdatabase;
import com.calendar.share.Share;
import com.calendardemo.R;

public class Splash extends Activity 
{
	// declare InputStream variable to manage database
	public static InputStream databaseInputStream1;     
	
	private Handler guiThread;
	// declare Runnable variable to update database 
	private Runnable updateTask;

	//create Database
	final DBAdapter dba = new DBAdapter(this);
	
	//Declaration of fonts Typefases
	public static Typeface BOLD,BOLD_OBLIQUE,LIGHT,LIGHT_OBLIQUE,OBLIQUE,HELVETICA;   

	@SuppressLint("SimpleDateFormat")
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		//Initialisation of Fonts
		BOLD = Typeface.createFromAsset(getAssets(),"Helvetica-Bold.ttf");
		BOLD_OBLIQUE = Typeface.createFromAsset(getAssets(),"Helvetica-BoldOblique.ttf");
		LIGHT = Typeface.createFromAsset(getAssets(),"Helvetica-Light.ttf");
		LIGHT_OBLIQUE = Typeface.createFromAsset(getAssets(),"Helvetica-LightOblique.ttf");
		OBLIQUE = Typeface.createFromAsset(getAssets(),"Helvetica-Oblique.ttf");
		HELVETICA = Typeface.createFromAsset(getAssets(),"Helvetica.ttf");
		
		
		//Assign current Date for Cale.java 
		Date d = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		String date = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
		Share.selectedDate = date;
	}

	@Override
	protected void onResume() {
		super.onResume();
		new InsertTask().execute("");
	}

	//Restore data from old database
	private class InsertTask extends AsyncTask<String, Void, Boolean> {

		@SuppressLint("SdCardPath")
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			try
			{
				//create Calendar.sql database file on default database path
				File f = new File("/data/data/com.calendardemo/databases/Calendar.sql");
				if (f.exists()) {
					
				}
				else 
				{
					try
					{
						dba.open();
						dba.close();
						databaseInputStream1 = getAssets().open("Calendar.sql");
						//copy old data
						importdatabase.copyDataBase();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
			}

		}

		@Override
		protected Boolean doInBackground(String... params) {

			Boolean success = false;
			try {
				success = true;
			} catch (Exception e) {
				if (e.getMessage() != null)
					e.printStackTrace();
			}
			return success;
		}

		@Override
		protected void onPostExecute(Boolean success) {
			super.onPostExecute(success);
			
			//Waiting to copy data after change activity
			call();
		}
	}

	public void call() {
		initThreading();
		guiThread.postDelayed(updateTask, 1000);
	}

	private void initThreading() {
		guiThread = new Handler();
		updateTask = new Runnable() {
			public void run() {
				changeImage();
			}
		};
	}

	private void changeImage() 
	{
			//redirect the Calendar Events 
			Intent intent = new Intent(Splash.this, Cale.class);
			startActivity(intent);
			finish();
			//set animation for splash screen 
			setActivityAnimation(Splash.this, R.anim.fade_in, R.anim.fade_out);
	}

	static public void setActivityAnimation(Activity activity, int in, int out) {
		try {
			Method method = Activity.class.getMethod("overridePendingTransition", new Class[] { int.class,int.class });
			method.invoke(activity, in, out);
		} catch (Exception e) {
		}
	}
}
