package com.calendarevent;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.calendar.database.DBAdapter;
import com.calendar.share.Share;
import com.calendardemo.R;

public class EventView extends Activity implements OnClickListener 
{
	// declare back button layout
	private LinearLayout lrn_back;
	// declare lnr_img 
	private LinearLayout lrn_img;
	// declare lrn_share to share saved events to other social media apps
	private LinearLayout lrn_share;
	
	// declare header title text variables
	private TextView tv_title; 
	
	// declare event fields text variables
	private TextView tv_name;
	private TextView tv_time;
	private TextView tv_date;
	private TextView tv_desc;
	
	// declare edit Button variables
	private Button btn_edit;
	// declare delete Button variables
	private Button btn_delete;

	// declare default ImageView to display image
	private ImageView img_photo;
	
	// declare full screen com.calendar.TouchView.TouchImageView to display image and allow to zoom-in and zoom-out functionality 
	private ImageView img_photo_full;
	
	// declare ScrollView for display to use scroll within event list screen 
	private ScrollView sv_content;
	
	//declare Bitmap set for ImageView 
	private Bitmap imageBitmap=null;
	
	// declare Database variable 
	DBAdapter dba;
	
	//declare all variables for storing event fields
	String id = "", name = "", time = "", desc = "", img_path = "", date = "";
	
	//Declaration for Device Screen size
	public Display mDisplay;
	
	// declare screenWidth variable
	int screenWidth;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.eventview_activity);
		
		dba = new DBAdapter(EventView.this);
		
		
		init();
		
	}

	@SuppressWarnings("deprecation")
	void init() {
		// create Display variable to get current device screen width
		mDisplay = getWindowManager().getDefaultDisplay();
		screenWidth = mDisplay.getWidth();

		// create  BitmapDrawable variable to get current header image 
		BitmapDrawable bd = (BitmapDrawable) getResources().getDrawable(R.drawable.bg_header);
		//create height variable to get current header image height
		int height = bd.getBitmap().getHeight();

		// link sv_content variables to hide and show content with ScrollView in the layout XML
		sv_content = (ScrollView) findViewById(R.id.sv_content);
		// link lnr_img variables to correspond to LinearLayout in the layout XML
		lrn_img = (LinearLayout) findViewById(R.id.lrn_ev_img);
		// link lnr_back variables to correspond to LinearLayout in the layout XML
		lrn_back = (LinearLayout) findViewById(R.id.lrn_ev_back);
		// link lrn_share variables to correspond to LinearLayout in the layout XML
		lrn_share = (LinearLayout) findViewById(R.id.lrn_share);
		// link tv_title variables to correspond to TextView in the layout XML
		tv_title = (TextView) findViewById(R.id.tv_ev_title);
		
		// link TextView variables to correspond to TextView in the layout XML
		tv_name = (TextView) findViewById(R.id.tv_ev_name);
		tv_time = (TextView) findViewById(R.id.tv_ev_time);
		tv_desc = (TextView) findViewById(R.id.tv_ev_desc);
		tv_date = (TextView) findViewById(R.id.tv_ev_date);
		
		
		
		// link img_photo variables to correspond to ImageView in the layout XML
		img_photo = (ImageView) findViewById(R.id.img_photo);
		// link img_photo_full variables to correspond to ImageView in the layout XML
		img_photo_full = (ImageView) findViewById(R.id.img_photo_full);
		
		// create btn_edit variable and link to button in layout XML
		btn_edit = (Button) findViewById(R.id.btn_ev_edit);
		// create btn_delete variable and link to button in layout XML
		btn_delete = (Button) findViewById(R.id.btn_ev_delete);

		//update lnr_back height (increase clickable area)
		lrn_back.getLayoutParams().height = height;
		//update lnr_img height
		lrn_img.getLayoutParams().height = (int) (screenWidth * 0.5);
		//update lrn_share height (increase clickable area)
		lrn_share.getLayoutParams().height = height;
		
		// start listener
		lrn_share.setOnClickListener(this);
		lrn_back.setOnClickListener(this);
		img_photo.setOnClickListener(this);
		img_photo_full.setOnClickListener(this);
		btn_edit.setOnClickListener(this);
		btn_delete.setOnClickListener(this);

		// get current event data from calendarView.java file
		Bundle extras = getIntent().getExtras(); 
		if (extras != null) {
			// assign event record value
			id = extras.getString(Share.CALENDAR.ID);
			name = extras.getString(Share.CALENDAR.EVENT_NAME);
			time = extras.getString(Share.CALENDAR.EVENT_TIME);
			desc = extras.getString(Share.CALENDAR.EVENT_DES);
			img_path = extras.getString(Share.CALENDAR.EVENT_IMG_PATH);
			date = extras.getString(Share.CALENDAR.EVENT_DATE);

			//set event name
			tv_name.setText(name);
			//set event time
			tv_time.setText(time);
			//set event description
			tv_desc.setText(desc);
			//set event date
			tv_date.setText(date.substring(8, 10) + "-" + date.substring(5, 7)+ "-" + date.substring(0, 4));

			//load sd card
			if (Share.ensureSDCardAccess()) 
			{
				File image = new File(img_path);
				//check file is available or not
				if (image.exists()) 
				{
					// setImageView Size
					img_photo.getLayoutParams().width = (int) (screenWidth * 0.5);
					img_photo.getLayoutParams().height = (int) (screenWidth * 0.5);
					
					// if file is available than set Image file in default ImageView and full ImageView
					imageBitmap = BitmapFactory.decodeFile(image.getAbsolutePath());
					img_photo.setImageBitmap(imageBitmap);
					img_photo_full.setImageBitmap(imageBitmap);
				} else {
					// if file not found then set default image
					img_photo.setImageResource(R.drawable.ic_camera);
				}
			}
		}
		// initialise text font
		setTypeface();
	}
	
	 // Use for Set Font
	 
	void setTypeface()
	{
		tv_title.setTypeface(Splash.BOLD);
		tv_name.setTag(Splash.HELVETICA);
		tv_time.setTag(Splash.HELVETICA);
		tv_desc.setTag(Splash.HELVETICA);
		btn_edit.setTypeface(Splash.BOLD);
		btn_delete.setTypeface(Splash.BOLD);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onClick(View v) 
	{
		if (v == btn_edit)    
		{
			// create intent to start with edit mode of the Event activity on button press (btn_edit)
			Intent eventIntent = new Intent(EventView.this, Event.class);
			//put the all event fields data and pass on to the event activity
			eventIntent.putExtra(Share.CALENDAR.ID, id);
			eventIntent.putExtra(Share.CALENDAR.EVENT_NAME, name);
			eventIntent.putExtra(Share.CALENDAR.EVENT_TIME, time);
			eventIntent.putExtra(Share.CALENDAR.EVENT_DES, desc);
			eventIntent.putExtra(Share.CALENDAR.EVENT_IMG_PATH, img_path);
			eventIntent.putExtra(Share.CALENDAR.EVENT_DATE, date);
			startActivity(eventIntent);
			//close Event_view activity
			finish();
		}
		else if (v == btn_delete)  
		{
			//create a dialog on the press of btn_delete view
			final AlertDialog deleteDialog = new AlertDialog.Builder(EventView.this).create();
			
			//set error message on dialog
			deleteDialog.setMessage("Are You Sure You Want To Delete This Event");
			//if press the Yes button then delete the event data from database
			deleteDialog.setButton("Yes",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// Uses the DBAdapter to execute a request open database
							dba.open();
							//pass current id as parameter to delete the record
							dba.delete_calender(id);   
							// Uses the DBAdapter to execute a request close database
							dba.close();
							
							//remove photo file from given image path
//							File file = new File(img_path); 
//							if(file.exists())
//								file.delete();
							onBackPressed();
						}
					});
			//if press the Cancel button hide deleteDialog
			deleteDialog.setButton2("Cancel",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							deleteDialog.cancel();
						}
					});
			deleteDialog.show();
		}
		else if (v == lrn_back)
		{
			//back to the Cale activity
			onBackPressed();
		}
		else if( v == lrn_share)
		{
			//Share event data into other social media apps
			initShareIntent();
		}
		else if(v == img_photo)
		{
			//full screen with ImageView
			if(imageBitmap != null) 
			{
				sv_content.setVisibility(View.GONE);
				img_photo_full.setVisibility(View.VISIBLE);				
			}
		}
		else if (v == img_photo_full)  
		{
			//default screen with ImageView
			sv_content.setVisibility(View.VISIBLE);
			img_photo_full.setVisibility(View.GONE);
		}
	}
	
	@Override
	public void onBackPressed()
	{
		// create intent to start the Cale activity on button press (lnr_back)
		Intent caleIntent = new Intent(EventView.this, Cale.class);  
		startActivity(caleIntent); 
		//close Event_view activity
		finish();
	}
	
	
	//  Use for Sharing the Calendar Event
	
	private void initShareIntent(){
		try
		{
			// create uri variable for image path
			Uri uri = Uri.fromFile(new File(img_path));
			// create shareIntent to start the other app activity on LinearLayout press (lrn_share)
			Intent shareIntent = new Intent();
			shareIntent.setAction(Intent.ACTION_SEND);
			shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Calendar Events");
			shareIntent.putExtra(Intent.EXTRA_TEXT, name + "\n" + date + " & "+ time + "\n" + desc);
			// use this when you want to send an image
			shareIntent.putExtra(Intent.EXTRA_STREAM, uri); 
			shareIntent.setType("image/*");
			shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
			startActivity(Intent.createChooser(shareIntent, "send"));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
