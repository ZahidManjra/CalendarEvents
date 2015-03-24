package com.calendar;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.calendar.CalendarWrapper.OnDateChangedListener;
import com.calendar.share.Share;
import com.calendardemo.R;
import com.calendarevent.Cale;
import com.calendarevent.Event_View;
import com.calendarevent.Splash;

public class CalendarView extends LinearLayout {

	//declare variables for to check calendar loaded or not
	boolean first_load;
	
	Context c;
	Button btn_today;
	int date, month, year;

	//declare constant variable for view type of Calendar
	private final int CENTURY_VIEW = 5;
	private final int DECADE_VIEW = 4;
	private final int YEAR_VIEW = 3;
	private final int MONTH_VIEW = 2;
	private final int DAY_VIEW = 1;
	private final int ITEM_VIEW = 0;

	/**********************************************************************************************************/
	// declare variables for system year,month,day
	int sysYear;
	int sysMonth;
	int sysDay;
	/**********************************************************************************************************/

	// declare _calendar variables for manage the day of current month and privous or next moths of days
	private CalendarWrapper _calendar;
	// declare _days variables for maintain the day view (showing with event then display line)
	private TableLayout _days;
	
	// declare _events variables for  available event details
	private LinearLayout _events;
	
	// declare _up variables for  year
	private Button _up;
	
	// declare _prev variables for  go to the privous month
	private ImageView _prev;
	// declare _prev variables for  go to the next month
	private ImageView _next;
	
	// declare _onMonthChangedListener for  change the calendar month
	private OnMonthChangedListener _onMonthChangedListener;
	// declare _onSelectedDayChangedListener for  get current select day of calendar
	private OnSelectedDayChangedListener _onSelectedDayChangedListener;
	
	// declare _currentView for current view type(months,year..etc.)
	private int _currentView;
	private int _currentYear;
	private int _currentMonth;
	
	// declare initializing for view are created or not
	private Boolean initializing = true;
	// declare flgMonthChanged for check month change or not
	public static Boolean flgMonthChanged = true;
	
	// declare weekRows variable  for month of no week rows are created 
	private int weekRows;
	
	// declare _dayCell object array of the Daycell class
	DayCell[][] _dayCell = new DayCell[6][7];

	/**********************************************************************************************************/

	/**
	 * 	This method use to get Current day, months and year.
	 */
	public void SysDate() 
	{
		 // create c object of Calendar
		Calendar c = Calendar.getInstance();
		sysYear = c.get(Calendar.YEAR);
		sysMonth = c.get(Calendar.MONTH);
		sysDay = c.get(Calendar.DAY_OF_MONTH);

		Share.date = sysDay;
		Share.month = sysMonth+1;
		Share.year = sysYear;

	}

	/**********************************************************************************************************/

	public CalendarView(Context context) {
		super(context);
		c = context;
		init(context);
	}

	public CalendarView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public interface OnMonthChangedListener {
		public void onMonthChanged(CalendarView view);
	}

	public void setOnMonthChangedListener(OnMonthChangedListener l) {
		_onMonthChangedListener = l;
	}

	public interface OnSelectedDayChangedListener {
		public void onSelectedDayChanged(CalendarView view);
	}

	public void setOnSelectedDayChangedListener(OnSelectedDayChangedListener l) {
		_onSelectedDayChangedListener = l;
	}

	public Calendar getVisibleStartDate() {
		return _calendar.getVisibleStartDate();
	}

	public Calendar getVisibleEndDate() {
		return _calendar.getVisibleEndDate();
	}

	public Calendar getSelectedDay() {
		return _calendar.getSelectedDay();
	}

	public void setDaysWithEvents(CalendarDayMarker[] markers) {

		int dayItemsInGrid = weekRows * 7;
		int abb = dayItemsInGrid;

		Boolean flag = true;
		Calendar new_tempCal = _calendar.getVisibleStartDate();

		int new_col = 0;
		int new_row = 1;

		for (int j = 0; j < abb; j++) {

			TableRow tr = (TableRow) _days.getChildAt(new_row);
			RelativeLayout rltvLayout = (RelativeLayout) tr.getChildAt(new_col);

			BitmapDrawable bd = (BitmapDrawable) this.getResources()
					.getDrawable(R.drawable.current_date_bg);
			int height = bd.getBitmap().getHeight();
			int width = bd.getBitmap().getWidth();

			rltvLayout.setMinimumWidth(width);
			rltvLayout.setMinimumHeight(height);

			LinearLayout lnrLayoutPayStatus = (LinearLayout) rltvLayout
					.getChildAt(3);
			ImageView ivGreen = (ImageView) lnrLayoutPayStatus.getChildAt(0);

			int[] tag = (int[]) rltvLayout.getTag();
			int day = tag[1];

			TextView tvDay = (TextView) rltvLayout.getChildAt(2);
			tvDay.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
			if (tvDay.getTextColors() == ColorStateList.valueOf(getResources().getColor(R.color.black))) {

				rltvLayout.setBackgroundColor(getResources().getColor(R.color.cal_select));

				// rltvLayout.setBackgroundColor(R.drawable.cal_light);

			} else {

				rltvLayout.setBackgroundColor(getResources().getColor(R.color.cal_month));
			
				//TODO : Hide Day not in  Months
				tvDay.setTextColor(Color.WHITE);
				// rltvLayout.setBackgroundColor(R.drawable.cal_dark);

			}

			if (new_tempCal.get(Calendar.YEAR) == sysYear && new_tempCal.get(Calendar.MONTH) == sysMonth && day == sysDay)
			{
				tvDay.setTypeface(Splash.BOLD);
			}
			else {
				tvDay.setTypeface(Splash.HELVETICA);
			}
			if (flgMonthChanged) {

				boolean _found = false;

				for (int l = 0; l < markers.length; l++) {
					CalendarDayMarker n = markers[l];

					if (Cale.check_date_arratlist(
							new_tempCal.get(Calendar.YEAR),
							new_tempCal.get(Calendar.MONTH) + 1,
							new_tempCal.get(Calendar.DAY_OF_MONTH))) {

						ivGreen.setBackgroundResource(R.drawable.ic_black_line);
						ivGreen.setVisibility(View.VISIBLE);
					} else {
						ivGreen.setVisibility(View.GONE);
					}

					if (day == 1) {
						if (flag) {
							flag = false;
							rltvLayout.setEnabled(true);

						} else {
							flag = true;
							rltvLayout.setEnabled(false);

						}
					} else {
						if (flag) {
							rltvLayout.setEnabled(false);

						} else {
							rltvLayout.setEnabled(true);

						}
					}
					if (new_tempCal.get(Calendar.YEAR) == sysYear && new_tempCal.get(Calendar.MONTH) == sysMonth && day == sysDay) {

						rltvLayout.setBackgroundResource(R.drawable.current_date_bg);
						tvDay.setTextColor(Color.BLACK);
						ivGreen.setBackgroundResource(R.drawable.ic_blue_line);
						if (!initializing) {

						}
					}
					else if (new_tempCal.get(Calendar.YEAR) == n.getYear() && new_tempCal.get(Calendar.MONTH) == n.getMonth()) 
					{
						// Log.e("", "2");
						if (day == n.getDay()) {
							ivGreen.setBackgroundResource(R.drawable.ic_black_line);
							if (tvDay.getTextColors() == ColorStateList.valueOf(getResources().getColor(R.color.black))) {
								rltvLayout
										.setBackgroundResource(R.drawable.selected_date_bg);
								ivGreen.setBackgroundResource(R.drawable.ic_black_line);

							}
						}
					}
				}

			} else {
				for (int k = 0; k < markers.length; k++) {
					CalendarDayMarker m = markers[k];

					if (Cale.check_date_arratlist(
							new_tempCal.get(Calendar.YEAR),
							new_tempCal.get(Calendar.MONTH) + 1,
							new_tempCal.get(Calendar.DAY_OF_MONTH))) {

						ivGreen.setBackgroundResource(R.drawable.ic_black_line);
						ivGreen.setVisibility(View.VISIBLE);
					} else {
						ivGreen.setVisibility(View.GONE);
					}

					if (new_tempCal.get(Calendar.YEAR) == sysYear	&& new_tempCal.get(Calendar.MONTH) == sysMonth	&& day == sysDay) {

						// Log.e("", "3");
						rltvLayout.setBackgroundResource(R.drawable.current_date_bg);
						ivGreen.setBackgroundResource(R.drawable.ic_blue_line);
						new_setTransactionRow(m.getYear(), m.getMonth() + 1,m.getDay());

					} else if (new_tempCal.get(Calendar.YEAR) == m.getYear()
							&& new_tempCal.get(Calendar.MONTH) == m.getMonth()) {

						if (day == m.getDay()) {

							if (tvDay.getTextColors() == ColorStateList
									.valueOf(getResources().getColor(
											R.color.black))) {

								rltvLayout
										.setBackgroundResource(R.drawable.selected_date_bg);

								ivGreen.setBackgroundResource(R.drawable.ic_black_line);

								new_setTransactionRow(m.getYear(),
										m.getMonth() + 1, m.getDay());

							}

						}
					}
				}
			}

			new_col++;

			if (new_col == 7) {
				new_col = 0;
				new_row++;
			}

			new_tempCal.add(Calendar.DAY_OF_MONTH, 1);
		}

		if (first_load) {
			first_load = false;

			new_setTransactionRow(sysYear, sysMonth + 1, sysDay);
			// new_setTransactionRow_g(sysYear, sysMonth + 1, sysDay);
		}
		flgMonthChanged = false;
	}

	public void setListViewItems(View[] views) {

		_events.removeAllViews();

		for (int i = 0; i < views.length; i++) {
			_events.addView(views[i]);
		}
	}

	private void init(Context context) {
		View v = LayoutInflater.from(context).inflate(R.layout.calendar, this,true);
		_calendar = new CalendarWrapper();
		_days = (TableLayout) v.findViewById(R.id.days);
		_up = (Button) v.findViewById(R.id.up);
		_prev = (ImageView) v.findViewById(R.id.previous);
		_next = (ImageView) v.findViewById(R.id.next);

		_events = (LinearLayout) v.findViewById(R.id.events);

		Share.date = date;
		Share.month = sysMonth;
		Share.year = sysYear;
		
		btn_today = (Button) v.findViewById(R.id.btn_today);
		first_load = true;

		/*********************************************************************************************************/
		SysDate();
		refreshCurrentDate();
		/**********************************************************************************************************/
		// Days Table
		for (int i = 0; i < 1; i++) 
		{ 	// Rows
			TableRow tr = (TableRow) _days.getChildAt(i);

			for (int j = 0; j < 7; j++) 
			{ 	// Columns
				Boolean header = i == 0; // First row is weekday headers
				RelativeLayout rltvLayout = (RelativeLayout) tr.getChildAt(j);
				if (!header) {
					rltvLayout.setClickable(true);
					rltvLayout.setOnClickListener(_dayClicked);     // define day onClick Listener
				}
			}
		}

		refreshDayCells();

		// Listeners
		_calendar.setOnDateChangedListener(_dateChanged);
		_prev.setOnClickListener(_incrementClicked);
		_next.setOnClickListener(_incrementClicked);

		btn_today.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				refreshView();
			}
		});
		
		setView(MONTH_VIEW);
	}

	@SuppressLint("SimpleDateFormat")
	public void refreshView()
	{
		final Calendar c = Calendar.getInstance();
		//c.set(sysYear, sysMonth, sysDay);

		date = sysDay;
		month = sysMonth;
		year = sysYear;

		Share.date = date;
		Share.month = sysMonth+1;
		Share.year = sysYear;

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Share.selectedDate = sdf.format(c.getTime());
		final CalendarView _calendarView = (CalendarView) findViewById(R.id.forecast_calendarView);
		Cale.lnrLayout_EventList.removeAllViews();
		_calendar.setMonth(sysMonth);
		_calendar.setDay(sysDay);
		_calendar.setYear(sysYear);
		
		if ((sysMonth + 1) < 10) {

			if (_calendar.toString("MM").equalsIgnoreCase(
					"0" + String.valueOf(sysMonth + 1))) {

				if (_calendar.toString("yyyy").equalsIgnoreCase(
						String.valueOf(sysYear))) {
					new_setTransactionRow(sysYear, sysMonth + 1, sysDay);
				}

			}
		} else {

			if (_calendar.toString("MM").equalsIgnoreCase(
					String.valueOf(sysMonth + 1))) {

				if (_calendar.toString("yyyy").equalsIgnoreCase(
						String.valueOf(sysYear))) {
					new_setTransactionRow(sysYear, sysMonth + 1, sysDay);
				}

			}
		}
		_calendarView.setDaysWithEvents(new CalendarDayMarker[] { new CalendarDayMarker(c, Color.BLACK) });
//		Toast.makeText(getContext(), "Today date", Toast.LENGTH_SHORT).show();
	}
	private OnDateChangedListener _dateChanged = new OnDateChangedListener() {
		public void onDateChanged(CalendarWrapper sc) {

			Boolean monthChanged = _currentYear != sc.getYear()
					|| _currentMonth != sc.getMonth();

			// Log.e("", "msg=====" + sc.getMonth());
			if (monthChanged) {
				refreshDayCells();
				invokeMonthChangedListener();
				// setOnMonthChangedListener();
			}

			refreshCurrentDate();
			refreshUpText();
		}
	};

	private OnClickListener _incrementClicked = new OnClickListener() {
		public void onClick(View v) {

			int inc = (v == _next ? 1 : -1);

			if (_currentView == MONTH_VIEW) {

				flgMonthChanged = true;

				Cale.lnrLayout_EventList.removeAllViews();
				_calendar.addMonth(inc);

				if ((sysMonth + 1) < 10) {

					if (_calendar.toString("MM").equalsIgnoreCase(
							"0" + String.valueOf(sysMonth + 1))) {

						if (_calendar.toString("yyyy").equalsIgnoreCase(
								String.valueOf(sysYear))) {
							new_setTransactionRow(sysYear, sysMonth + 1, sysDay);
						}

					}
				} else {

					if (_calendar.toString("MM").equalsIgnoreCase(
							String.valueOf(sysMonth + 1))) {

						if (_calendar.toString("yyyy").equalsIgnoreCase(
								String.valueOf(sysYear))) {
							new_setTransactionRow(sysYear, sysMonth + 1, sysDay);
						}

					}
				}
			} else if (_currentView == DAY_VIEW) {
				_calendar.addDay(inc);
				invokeSelectedDayChangedListener();
			} else if (_currentView == YEAR_VIEW) {
				_currentYear += inc;
				refreshUpText();
			}
		}
	};

	// For change the date select date
	private OnClickListener _dayClicked = new OnClickListener() {
		public void onClick(View v) {

			int[] tag = (int[]) v.getTag();
			_calendar.addMonthSetDay(tag[0], tag[1]);

			if (initializing)
				initializing = false;

			final Calendar c = Calendar.getInstance();
			c.set(_currentYear, _currentMonth, tag[1]);

			date = tag[1];
			month = _currentMonth;
			year = _currentYear;

			Share.date = c.get(Calendar.DAY_OF_MONTH);
			Share.month =c.get(Calendar.MONTH)+1;
			Share.year = c.get(Calendar.YEAR);
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

			Share.selectedDate = sdf.format(c.getTime());
			Log.e("", "calenderView Click====" + Share.selectedDate);
			// Log.e("", "msg cal====" + tag[1]);
			final CalendarView _calendarView = (CalendarView) findViewById(R.id.forecast_calendarView);

			// saves the clicked date when month is changed..
			_calendarView
					.setOnMonthChangedListener(new OnMonthChangedListener() {
						public void onMonthChanged(CalendarView view) {

							_calendarView
									.setDaysWithEvents(new CalendarDayMarker[] { new CalendarDayMarker(
											c, Color.BLACK) });
						}
					});

			_calendarView
					.setDaysWithEvents(new CalendarDayMarker[] { new CalendarDayMarker(
							c, Color.BLACK) });
			refreshUpText();
		}
	};

	private void refreshDayCells() {

		int[] dayGrid = _calendar.get7x6DayArray();
		int monthAdd = -1;
		int row = 1; // Skip weekday header row
		int col = 0;
		Boolean flgDeleteRow = false;

		for (int i = 0; i < dayGrid.length; i++) {
			int day = dayGrid[i];

			if (day == 1)
				monthAdd++;

			TableRow tr = (TableRow) _days.getChildAt(row);
			RelativeLayout rltvLayout = (RelativeLayout) tr.getChildAt(col);
			rltvLayout.setMinimumHeight(rltvLayout.getWidth());

			ImageView ivRight = (ImageView) rltvLayout.getChildAt(1);
			BitmapDrawable bd = (BitmapDrawable) this.getResources().getDrawable(R.drawable.current_date_bg);
			int height = bd.getBitmap().getHeight();
			int width = bd.getBitmap().getWidth();

			ivRight.setMinimumHeight((Share.width / 7));

			if (!flgDeleteRow) {

				if (col == 0 && monthAdd > 0) {

					weekRows = 5;
					flgDeleteRow = true;

					for (int x = 0; x < rltvLayout.getChildCount(); x++)

						rltvLayout.setVisibility(View.GONE);
					col++;

				} 
				else 
				{
					// tvDay.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
					weekRows = 6;
					rltvLayout.setVisibility(View.VISIBLE);

					/****************************** Date Text ************************************/
					TextView tv = (TextView) rltvLayout.getChildAt(2);
					tv.setText(String.valueOf(day));
					// tv.setTypeface(Splash.font);
					tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, 1);

					for (int k = 0; k < 6; k++)
						for (int j = 0; j < 7; j++) {

							_dayCell[k][j] = new DayCell();
							_dayCell[k][j].day = day;
						}

					if (monthAdd != 0) {

						rltvLayout.setBackgroundColor(getResources().getColor(R.color.cal_month));

						tv.setTextColor(getResources().getColor(R.color.gray));

						rltvLayout.setClickable(false);

					} else {

						rltvLayout.setBackgroundColor(getResources().getColor(R.color.cal_select));
						tv.setTextColor(getResources().getColor(R.color.black));

						rltvLayout.setClickable(true);
						rltvLayout.setOnClickListener(_dayClicked);
					}

					rltvLayout.setTag(new int[] { monthAdd, dayGrid[i] });

					col++;

					if (col == 7) {
						col = 0;
						row++;
					}
				}
			} else {
				rltvLayout.setVisibility(View.GONE);
				col++;
			}
		}
	}

	private void setView(int view) {

		if (_currentView != view) {
			_currentView = view;
			_events.setVisibility(_currentView == DAY_VIEW ? View.VISIBLE
					: View.GONE);
			_days.setVisibility(_currentView == MONTH_VIEW ? View.VISIBLE
					: View.GONE);
			_up.setEnabled(_currentView != YEAR_VIEW);

			if (_up.isEnabled() == false)
				_up.setTextColor(Color.BLACK);

			refreshUpText();
		}
	}

	private void refreshUpText() {

		switch (_currentView) {
		case MONTH_VIEW:
			_up.setText(_calendar.toString("MMMM yyyy"));
			break;
		case YEAR_VIEW:
			_up.setText(_currentYear + "");
			break;
		case CENTURY_VIEW:
			_up.setText("CENTURY_VIEW");
			break;
		case DECADE_VIEW:
			_up.setText("DECADE_VIEW");
			break;
		case DAY_VIEW:
			_up.setText(_calendar.toString("EEEE, MMMM dd, yyyy"));
			break;
		case ITEM_VIEW:
			_up.setText("ITEM_VIEW");
			break;
		default:
			break;
		}
	}

	private void refreshCurrentDate() {

		_currentYear = _calendar.getYear();
		_currentMonth = _calendar.getMonth();
		_calendar.getDay();
	}

	private void invokeMonthChangedListener() {

		if (_onMonthChangedListener != null)
			_onMonthChangedListener.onMonthChanged(this);

	}

	private void invokeSelectedDayChangedListener() {

		if (_onSelectedDayChangedListener != null)
			_onSelectedDayChangedListener.onSelectedDayChanged(this);
	}

	/**
	 *  User for Create Event Row on Calendarn Event List in Cale.java
	 * @param year
	 * @param month
	 * @param day
	 */
	@SuppressLint("SimpleDateFormat")
	public void new_setTransactionRow(int year, int month, int day) {

		  // create linf variable for use with change layout of event rows
		LayoutInflater linf = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		linf = LayoutInflater.from(getContext());
		
		 // remove event item in lnrLayout_EventList(contain event row view)
		Cale.lnrLayout_EventList.removeAllViews();
		
		//Load Event_rows in lnrLayout_EventList layout XML
		for (int i = 0; i < Cale.arr_event_date.size(); i++) {
			//get row for getting the year,month and day from date
			String row[] = Cale.arr_event_date.get(i).toString().split("-");
			
			//check the current date with event date if match then create event row view
			if (Integer.parseInt(row[0]) == year && Integer.parseInt(row[1]) == month && Integer.parseInt(row[2]) == day) {
				 // set upload layout for Event item row
				final View event_row = linf.inflate(R.layout.row_main, null);
				try {

					 // link row_img  to imageviews in the layout in row_main.XML for set event image
					ImageView row_img = (ImageView)event_row.findViewById(R.id.row_img);
					
					// link tv_name  to TextView in the layout in row_main.XML for set event name
					TextView tv_name = (TextView) event_row.findViewById(R.id.row_name);
					
					// link tv_datetime  to TextView in the layout in row_main.XML for set event name
					TextView tv_datetime = (TextView) event_row.findViewById(R.id.row_date_time);
					
					// link tv_desc  to TextView in the layout in row_main.XML for set event name
					TextView tv_desc = (TextView) event_row.findViewById(R.id.row_desc);
					
					// create width variable for set the event image view width and mDisplay link to the Cale.java
					final int width  = Cale.mDisplay.getWidth();
					row_img.getLayoutParams().width = (int) (width * 0.2);
					row_img.getLayoutParams().height = (int) (width * 0.2);
					
					//update event name
					tv_name.setText(Cale.arr_event_name.get(i));
					//update event time
					tv_datetime.setText(Cale.arr_event_time.get(i));
					//update.event descriptions
					tv_desc.setText(Cale.arr_event_descripation.get(i));
					
					//set font
					tv_name.setTypeface(Splash.HELVETICA);
					tv_datetime.setTypeface(Splash.HELVETICA);
					tv_desc.setTypeface(Splash.HELVETICA);
					
					//check image file
					File imgFile = new  File(Cale.arr_event_img_path.get(i));
					if(imgFile.exists()){
						
						Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
						//update event image
						row_img.setImageBitmap(myBitmap);
					}
				} catch (Exception e) {
				}

				final int j = i;
				
				//If select any event item then swipe the Event_view activity with pass the event item param
				event_row.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) 
					{
						// create intent to start the Event_View activity on select event row
						Intent viewEventIntent = new Intent(getContext(),Event_View.class);
						//pass the value of event data
						
						viewEventIntent.putExtra(Share.CALENDAR.ID, Cale.arr_event_id.get(j));
						viewEventIntent.putExtra(Share.CALENDAR.EVENT_NAME,Cale.arr_event_name.get(j));
						viewEventIntent.putExtra(Share.CALENDAR.EVENT_TIME,Cale.arr_event_time.get(j));
						viewEventIntent.putExtra(Share.CALENDAR.EVENT_DES,Cale.arr_event_descripation.get(j));
						viewEventIntent.putExtra(Share.CALENDAR.EVENT_IMG_PATH,Cale.arr_event_img_path.get(j));
						viewEventIntent.putExtra(Share.CALENDAR.EVENT_DATE,Cale.arr_event_date.get(j));
						
						getContext().startActivity(viewEventIntent);
						((Activity) getContext()).finish();
					}
				});
				
				//add event row item in lnrLayout_EventList layout link to the Cale.java
				Cale.lnrLayout_EventList.addView(event_row);
			}
		}
	}
}

class DayCell {

	int day;
	int saving;
	Boolean flgGreen;
	Boolean flgRed;

	DayCell() {
		day = 0;
		saving = 0;
		flgGreen = false;
		flgRed = false;
	}

	@SuppressWarnings("null")
	DayCell getDayCell() {

		DayCell temp = null;
		temp.day = this.day;
		temp.saving = this.saving;
		temp.flgGreen = this.flgGreen;
		temp.flgRed = this.flgRed;

		return temp;
	}

	void setDayCell(DayCell temp) {

		day = temp.day;
		saving = temp.saving;
		flgGreen = temp.flgGreen;
		flgRed = temp.flgRed;
	}

}
