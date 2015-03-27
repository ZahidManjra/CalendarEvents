package com.calendarevent;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.calendar.database.DBAdapter;
import com.calendar.share.Share;
import com.calendardemo.R;

public class Event extends Activity implements OnClickListener {
	// declare header title text variables
	private TextView tv_title;

	// declare back button layout
	private LinearLayout lnr_back;

	// declare lnr_img 
	private LinearLayout lnr_img;

	// declare Event Fields variables
	private EditText et_name;
	private EditText et_time;
	private EditText et_desc;
	private EditText et_date;

	// declare ImageView to display image
	private ImageView img_event;

	// declare save Button variables
	private Button btn_save;

	// declare Bitmap set for ImageView
	private Bitmap imageBitmap;

	// declare variable for image path
	private String path = "";

	// declare Database variable
	private DBAdapter dba;

	// declare isEdit variable to check action of edit or add button
	private Boolean isEdit = false;

	// declare screenWidth variable 
	private int screenWidth;

	// declare variable to get current event id if in edit mode
	private String id = "";

	// declare variable to set date
	private int pYear;
	private int pMonth;
	private int pDay;

	// declare hour and minute to manage current time
	private static int hour, minute;

	// declare variable for event time
	private String time = "00:00";

	// declare Calendar variable to manage date and time
	private Calendar c;

	// declare request code to start gallery activity.
	private static int RESULT_LOAD_IMG = 1;
	// declare request code to start camera activity.
	private static int TAKE_PICTURE = 2;

	Uri imageUri;
	public static Bitmap setphoto = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// set upload layout
		setContentView(R.layout.event_activity);

		// initialise an instance (DBAdapter)
		dba = new DBAdapter(getApplicationContext());

		// initialise all View
		init();
	}

	@SuppressLint("SimpleDateFormat")
	@SuppressWarnings("deprecation")
	void init() {
		// create Display variable to get current device screen width
		Display display = getWindowManager().getDefaultDisplay();
		screenWidth = display.getWidth();

		// create BitmapDrawable variable to get current header image
		BitmapDrawable bd = (BitmapDrawable) getResources().getDrawable(
				R.drawable.bg_header);
		// create height variable to get current header image height
		int height = bd.getBitmap().getHeight();

		// create Calendar variable to get current date and time
		c = Calendar.getInstance();
		// create hour variable to get current hour
		hour = c.get(Calendar.HOUR_OF_DAY);
		// create minute variable to get current minute
		minute = c.get(Calendar.MINUTE);

		// link tv_title variables to correspond to TextViews in the layout XML
		tv_title = (TextView) findViewById(R.id.tv_event_title);
		// link lnr_back variables to correspond to LinearLayout in the layout
		// XML
		lnr_back = (LinearLayout) findViewById(R.id.lrn_eve_back);
		// link lnr_img variables to correspond to LinearLayout in the layout
		// XML
		lnr_img = (LinearLayout) findViewById(R.id.lnr_img);
		// link img_event variables to correspond to ImageView in the layout XML
		img_event = (ImageView) findViewById(R.id.img_eve_photo);

		// link EditText variables to correspond to EditText in the layout XML
		et_name = (EditText) findViewById(R.id.et_eve_name);
		et_date = (EditText) findViewById(R.id.et_eve_date);
		et_time = (EditText) findViewById(R.id.et_eve_time);
		et_desc = (EditText) findViewById(R.id.et_eve_decs);

		// link btn_save variables to correspond to the Button in the layout XML
		btn_save = (Button) findViewById(R.id.btn_eve_save);

		// update lnr_back height (increase Clickable area)
		lnr_back.getLayoutParams().height = height;
		// update lnr_img height
		lnr_img.getLayoutParams().height = (int) (screenWidth * 0.5);

		// set filter to define character length 
		et_name.setFilters(new InputFilter[] { new InputFilter.LengthFilter(200) });
		et_desc.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
				1500) });

		// set selected date to calendar
		et_date.setText("" + pad(Share.date) + "-" + pad(Share.month) + "-"
				+ Share.year);
		// set current time
		et_time.setText(showTime(hour, minute));
		// update header text
		tv_title.setText("Add Event");

		// start listener
		lnr_img.setOnClickListener(this);
		lnr_back.setOnClickListener(this);
		btn_save.setOnClickListener(this);
		et_date.setKeyListener(null);
		et_date.setOnClickListener(this);
		et_time.setOnClickListener(this);
		et_time.setKeyListener(null);

		Date d = new Date();

		// create Bundle variable for edit mode to get event data
		Bundle extras = getIntent().getExtras();
		// Load extras
		if (extras != null) {
			isEdit = true;
			// update Header text
			tv_title.setText("Edit Event");

			// create id variable for passing parameter to update the row
			id = extras.getString(Share.CALENDAR.ID);
			time = extras.getString(Share.CALENDAR.EVENT_TIME);
			path = extras.getString(Share.CALENDAR.EVENT_IMG_PATH);

			// set event name
			et_name.setText(extras.getString(Share.CALENDAR.EVENT_NAME));
			// set event time
			et_time.setText(time);
			// set event description
			et_desc.setText(extras.getString(Share.CALENDAR.EVENT_DES));

			// create SimpleDateFormat for date format
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			try {
				d = sdf.parse(Share.selectedDate);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			// edit event mode then update date of calendar variable with date and time
			c.setTime(d);
			pYear = c.get(Calendar.YEAR);
			pMonth = c.get(Calendar.MONTH + 1);
			pDay = c.get(Calendar.DAY_OF_MONTH);
			hour = Integer.valueOf(time.substring(0, 2));
			minute = Integer.valueOf(time.substring(5, 7));

			// check time with PM then add 12 hours as the calendar variable set is with a 24 hours format
			if (time.substring(8, 10).equals("PM"))
				hour = hour + 12;

			// load sd card
			if (Share.ensureSDCardAccess()) {
				File image = new File(path);
				// check file is available or not
				if (image.exists()) {
					// if file is found then set fix ImageView size using current
					// device screen width
					img_event.getLayoutParams().width = (int) (screenWidth * 0.5);
					img_event.getLayoutParams().height = (int) (screenWidth * 0.5);
					// set image
					img_event.setImageBitmap(BitmapFactory.decodeFile(image
							.getAbsolutePath()));
				}
			}
		} else {
			// if in add event screen then set current date
			c.setTime(d);
			pYear = c.get(Calendar.YEAR);
			pMonth = c.get(Calendar.MONTH) + 1;
			pDay = c.get(Calendar.DAY_OF_MONTH);
		}

		// initialise text font
		setTypeface();
	}

	@SuppressLint("NewApi")
	@Override
	public void onClick(View v) {

		if (v == lnr_back) {
			// create intent to start the Cale activity on button press
			Intent calIntent = new Intent(Event.this, Cale.class);
			startActivity(calIntent);
			// close Event activity
			finish();
		} else if (v == et_time) {
			// create a dialog when click on et_time view
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			final TimePicker picker = new TimePicker(this);

			// set hours and minutes
			picker.setCurrentHour(hour);
			picker.setCurrentMinute(minute);

			// set Title for time picker dialog
			builder.setTitle("Select Time");
			// set time picker view in alert dialog
			builder.setView(picker);

			// upon press of Set button then update the current time
			builder.setPositiveButton("Set",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// update et_time view
							hour = picker.getCurrentHour();
							minute = picker.getCurrentMinute();
							time = showTime(hour, minute);
							et_time.setText(showTime(hour, minute));
						}
					});
			// upon press of the Cancel button can't update time
			builder.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// set default time to et_time view
							et_time.setText(showTime(hour, minute));
						}
					});
			builder.create();

			if (!builder.show().isShowing())
				builder.show();
		} else if (v == et_date) {
			// create a dialog when click on et_date view
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			final DatePicker picker = new DatePicker(this);

			// set year,month and day in picker
			picker.updateDate(Share.year, Share.month - 1, Share.date);
			// show date only
			picker.setCalendarViewShown(false);
			// set Title text of alert dialog
			builder.setTitle("Select Date");
			// set datepicker view in alert dialog
			builder.setView(picker);
			// upon press of Set button then update the current date
			builder.setPositiveButton("Set",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							pYear = picker.getYear();
							pMonth = picker.getMonth() + 1;
							pDay = picker.getDayOfMonth();

							Share.selectedDate = pYear + "-" + pad(pMonth)
									+ "-" + pad(pDay);
							Share.date = pDay;
							Share.month = pMonth;
							Share.year = pYear;

							// update et_date view
							et_date.setText("" + pad(pDay) + "-" + pad(pMonth)
									+ "-" + pYear);
						}
					});
			// upon press of the Cancel button can't update date
			builder.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							// set default date to et_date view
							et_date.setText("" + pad(Share.date) + "-"
									+ pad(Share.month) + "-" + Share.year);
							dialog.dismiss();
						}
					});
			builder.create();
			if (!builder.show().isShowing())
				builder.show();

		} else if (v == lnr_img) {

			// create dialog for select image gallery or photo
			final Dialog alertDialog = new Dialog(Event.this);
			// hide default title
			alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			// update dialog view layout
			alertDialog.setContentView(R.layout.dlg_camera_gallary);

			// create lnr_gallery variable and link to gallery button in dlg_camera_gallary layout XML
			LinearLayout lnr_gallery = (LinearLayout) alertDialog
					.findViewById(R.id.lnr_gallery);
			// create lnr_photo variable and link to gallery button in dlg_camera_gallary layout XML
			LinearLayout lnr_photo = (LinearLayout) alertDialog
					.findViewById(R.id.lnr_photo);

			// start listener on gallery press
			lnr_gallery.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// create intent to start the default gallery activity on button press (gallery)
					Intent galleryIntent = new Intent(
							Intent.ACTION_PICK,
							android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
					alertDialog.cancel();
				}
			});
			// start listener on photo press
			lnr_photo.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (hasCamera()) {
						
						// create intent to start the default camera activity on button press (photo)
						// Intent intent = new Intent(
						// MediaStore.ACTION_IMAGE_CAPTURE);
						// startActivityForResult(intent, TAKE_PICTURE);
						Intent intent = new Intent(
								android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
						startActivityForResult(intent, TAKE_PICTURE);

					} else {
						// if camera is not available then show the following message
						Toast.makeText(Event.this, "Camera Not Found On The Device",
								Toast.LENGTH_SHORT).show();
					}
					alertDialog.cancel();
				}
			});

			alertDialog.show();
		} else if (v == btn_save) {
			// check event name is not empty
			if (validation()) {
				// Uses the DBAdapter to execute a request to open database
				dba.open();
				// check action mode either edit or add
				if (isEdit) {
					// This will update current event data into database
					if (dba.update_calender(Share.CALENDAR_TABLE, id,
							et_name.getText().toString().replace("'", "''")
									.replace("\"", "\"\""), et_time.getText()
									.toString(), et_desc.getText().toString()
									.replace("'", "''").replace("\"", "\"\""),
							path, Share.selectedDate)) {
						Toast.makeText(Event.this, "Successfully Updated",
								Toast.LENGTH_SHORT).show();

						// create intent to start the Cale activity
						Intent eventIntent = new Intent(Event.this, Cale.class);
						startActivity(eventIntent);

						// close Event activity
						finish();
					}

				} else {
					// store new event data in database
					if (dba.insert_calender_list(Share.CALENDAR_TABLE,
							et_name.getText().toString().replace("'", "''")
									.replace("\"", "\"\""), et_time.getText()
									.toString(), et_desc.getText().toString()
									.replace("'", "''").replace("\"", "\"\""),
							path, Share.selectedDate)) {
						Toast.makeText(Event.this, "Successfully Saved",
								Toast.LENGTH_SHORT).show();

						// create intent to start the Cale activity
						Intent eventIntent = new Intent(Event.this, Cale.class);
						startActivity(eventIntent);
						// close Event activity
						finish();
					}
				}
				// Uses the DBAdapter to execute a request to close the database
				dba.close();
			}
		}
	}

	@Override
	public void onBackPressed() {
		// create intent to start the Cale activity on button press (go back)
		Intent eventIntent = new Intent(Event.this, Cale.class);
		startActivity(eventIntent);
		finish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		try {
			// check intent action of either gallery or camera
			if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
					&& null != intent) {
				// if gallery action then get a selected image
				Uri selectedImage = intent.getData();
				String[] filePathColumn = { MediaStore.Images.Media.DATA };
				// get image data
				Cursor cursor = getContentResolver().query(selectedImage,
						filePathColumn, null, null, null);
				cursor.moveToFirst();
				// get Index of image path
				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				// get full path image using columnIndex
				path = cursor.getString(columnIndex);
				cursor.close();

				// create Bitmap image using path variable
				imageBitmap = BitmapFactory.decodeFile(path);
				if (imageBitmap != null) {
					// update ImageView width and height using current device screen width
			
					img_event.getLayoutParams().width = (int) (screenWidth * 0.5);
					img_event.getLayoutParams().height = (int) (screenWidth * 0.5);
				}
				// update ImageView
				img_event.setImageBitmap(BitmapFactory.decodeFile(path));
			} else if (requestCode == TAKE_PICTURE && resultCode == RESULT_OK
					&& intent != null) {

				this.imageFromCamera(resultCode, intent);

			} else {
				// Cancel the dialog without selecting of image 
				Toast.makeText(this, "No Image Chosen",
						Toast.LENGTH_LONG).show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void imageFromCamera(int resultCode, Intent data) {
		updateImageView((Bitmap) data.getExtras().get("data"), resultCode);

		img_event.setImageBitmap(setphoto);
		// set height and width of the ImageView
		img_event.getLayoutParams().width = (int) (screenWidth * 0.5);
		img_event.getLayoutParams().height = (int) (screenWidth * 0.5);

	}

	private void updateImageView(Bitmap newImage, int resultCode) {

		setphoto = newImage.copy(Bitmap.Config.ARGB_8888, true);

		if (Share.ensureSDCardAccess()) {

			ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			this.setphoto.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

			File f = new File(Share.IMAGE_PATH + "/"
					+ System.currentTimeMillis() + ".PNG");

			path = f.getAbsolutePath();

			Log.e("", "FILEPATH::::" + path);

			try {
				f.createNewFile();
				FileOutputStream fo = new FileOutputStream(f);
				fo.write(bytes.toByteArray());
				sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
						Uri.parse("file://"
								+ Environment.getExternalStorageDirectory())));
			} catch (IOException e) {

				e.printStackTrace();
			}

		}

	}

	
	// Use for Set Font
	 
	void setTypeface() {
		tv_title.setTypeface(Splash.BOLD);
		et_name.setTypeface(Splash.HELVETICA);
		et_date.setTypeface(Splash.HELVETICA);
		et_time.setTypeface(Splash.HELVETICA);
		et_desc.setTypeface(Splash.HELVETICA);
		btn_save.setTypeface(Splash.BOLD);
	}

	// Use for event name validation
	 
	@SuppressWarnings("deprecation")
	public Boolean validation() {
		Boolean validation = true;

		if (et_name.getText().length() == 0) {
			// create dialog for error message
			final AlertDialog eventDialog = new AlertDialog.Builder(Event.this)
					.create();
			// set the error text
			eventDialog.setMessage("Please Enter An Event Name");

			// create close button to hide dialog
			eventDialog.setButton("Close",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							eventDialog.cancel();
						}
					});
			eventDialog.show();
			// update variable if it does not allow to save event data
			validation = false;
		}

		return validation;
	}

	// check if the camera is aialble or not
	
	private boolean hasCamera() {
		return getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_CAMERA);
	}

	// Image save in sd card
	
	private void saveimg() {
		OutputStream fOut = null;
		if (Share.ensureSDCardAccess()) {
			// create image in sdcard
			File file = new File(Share.IMAGE_PATH, String.valueOf(System
					.currentTimeMillis()) + ".png");
			path = file.getAbsolutePath();
			try {
				fOut = new FileOutputStream(file);
				imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
				fOut.flush();
				fOut.close();
				MediaStore.Images.Media.insertImage(getContentResolver(),
						file.getAbsolutePath(), file.getName(), file.getName());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();

			}
		}
	}

	// This method is used for double digit format and link with Event.java
	public static String pad(int c) {
		if (c >= 10)
			return String.valueOf(c);
		else
			return "0" + String.valueOf(c);
	}

	// show time in 12 hours format and link with Event.java
	public static String showTime(int hour, int min) {
		String format;
		if (hour == 0) {
			hour += 12;
			format = "AM";
		} else if (hour == 12) {
			format = "PM";
		} else if (hour > 12) {
			hour -= 12;
			format = "PM";
		} else {
			format = "AM";
		}
		return (new StringBuilder().append(pad(hour)).append(" : ")
				.append(pad(min)).append(" ").append(format)).toString();
	}
}
