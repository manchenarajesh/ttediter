package in.ac.iitm.classalerts;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/*This class manages the class time table */
public class AlarmDatabaseHelper extends SQLiteOpenHelper {

	private static String DATABASE_NAME = "ClassAlertsDB";
	public AlarmDatabaseHelper(Context context) {
		super(context,DATABASE_NAME,null,1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE _monday_schedule (_id INTEGER PRIMARY KEY AUTOINCREMENT,_courseName TEXT,_location TEXT,_startHours INTEGER,_startMinutes INTEGER, _endHours INTEGER, _endMinutes INTEGER,_soundUri TEXT);");
		db.execSQL("CREATE TABLE _tuesday_schedule (_id INTEGER PRIMARY KEY AUTOINCREMENT,_courseName TEXT,_location TEXT,_startHours INTEGER,_startMinutes INTEGER, _endHours INTEGER, _endMinutes INTEGER,_soundUri TEXT);");
		db.execSQL("CREATE TABLE _wednesday_schedule (_id INTEGER PRIMARY KEY AUTOINCREMENT,_courseName TEXT,_location TEXT,_startHours INTEGER,_startMinutes INTEGER, _endHours INTEGER, _endMinutes INTEGER,_soundUri TEXT);");
		db.execSQL("CREATE TABLE _thursday_schedule (_id INTEGER PRIMARY KEY AUTOINCREMENT,_courseName TEXT,_location TEXT,_startHours INTEGER,_startMinutes INTEGER, _endHours INTEGER, _endMinutes INTEGER,_soundUri TEXT);");
		db.execSQL("CREATE TABLE _friday_schedule (_id INTEGER PRIMARY KEY AUTOINCREMENT,_courseName TEXT,_location TEXT,_startHours INTEGER,_startMinutes INTEGER, _endHours INTEGER, _endMinutes INTEGER,_soundUri TEXT);");
		db.execSQL("CREATE TABLE _saturday_schedule (_id INTEGER PRIMARY KEY AUTOINCREMENT,_courseName TEXT,_location TEXT,_startHours INTEGER,_startMinutes INTEGER, _endHours INTEGER, _endMinutes INTEGER,_soundUri TEXT);");
		db.execSQL("CREATE TABLE _sunday_schedule (_id INTEGER PRIMARY KEY AUTOINCREMENT,_courseName TEXT,_location TEXT,_startHours INTEGER,_startMinutes INTEGER, _endHours INTEGER, _endMinutes INTEGER,_soundUri TEXT);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
	}

}
