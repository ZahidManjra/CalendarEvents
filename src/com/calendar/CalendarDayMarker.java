package com.calendar;

import java.util.Calendar;
import java.util.Date;

public class CalendarDayMarker {
	private int _year;
	private int _month;
	private int _day;
	private int _color;

	// simple function to set date
	public CalendarDayMarker(int year, int month, int day, int color) {
		init(year, month, day, color);
	}

	// get day and mark colour which is current date or not
	public CalendarDayMarker(Date d, int color) {
		Calendar c = Calendar.getInstance();
		c.setTime(d);

		init(c.get(Calendar.YEAR), c.get(Calendar.MONTH),
				c.get(Calendar.DAY_OF_MONTH), color);
	}

	// get day and mark colour which is current date or not
	public CalendarDayMarker(Calendar c, int color) {
		init(c.get(Calendar.YEAR), c.get(Calendar.MONTH),
				c.get(Calendar.DAY_OF_MONTH), color);
	}

	// declare year moth and day
	private void init(int year, int month, int day, int color) {
		_year = year;
		_month = month;
		_day = day;
		_color = color;
	}

	// set value year
	public void setYear(int year) {
		_year = year;
	}

	// give value of year
	public int getYear() {
		return _year;
	}

	// set value of month 
	public void setMonth(int month) {
		_month = month;
	}

	// give value of month
	public int getMonth() {
		return _month;
	}

	// set value of day 
	public void setDay(int day) {
		_day = day;
	}

	// give value of day
	public int getDay() {
		return _day;
	}

	// set value of colour .
	public void setColor(int color) {
		_color = color;
	}

	// give value of colour
	public int getColor() {
		return _color;
	}
}
