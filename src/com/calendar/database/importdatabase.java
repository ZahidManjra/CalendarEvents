package com.calendar.database;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import android.annotation.SuppressLint;
import com.calendarevent.Splash;

public class importdatabase 
{
	@SuppressLint("SdCardPath")
	@SuppressWarnings("unused")
	public static void copyDataBase() {
		try 
		{
			// create databaseOutputStream variable to open the default database 
			OutputStream databaseOutputStream = new FileOutputStream("/data/data/com.calendardemo/databases/Calendar.sql");
			//declare databaseInputStream variable use to copy data
			InputStream databaseInputStream;
			//default size of the buffer
			byte[] buffer = new byte[1024];
			//declare length variable for databaseInputStream length
			int length;
			//assign the object of splash activity shared by importdatabase activity
			databaseInputStream = Splash.databaseInputStream1;

			//start copy of data
			while ((length = databaseInputStream.read(buffer)) > 0) 
			{
				databaseOutputStream.write(buffer);
			}
			databaseInputStream.close();
			databaseOutputStream.flush();
			databaseOutputStream.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
