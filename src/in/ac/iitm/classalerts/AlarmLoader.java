package in.ac.iitm.classalerts;

import java.util.Calendar;
import java.util.Date;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

/*This reciever is called when the user presses the done button after setting the timetable and thereafter on every midnight.
 * It's function is to load alarms for the day*/
public class AlarmLoader extends BroadcastReceiver {

	AlarmDatabaseHelper db;
	@Override
	public void onReceive(Context arg0, Intent arg1) {
		db = new AlarmDatabaseHelper(arg0);
		Calendar cal = Calendar.getInstance();
		Cursor c = null;
		switch(cal.get(Calendar.DAY_OF_WEEK))
		{
		case Calendar.MONDAY:
			c = db.getReadableDatabase().rawQuery("SELECT * FROM _monday_schedule;",null);
			break;
		case Calendar.TUESDAY:
			c = db.getReadableDatabase().rawQuery("SELECT * FROM _tuesday_schedule;",null);
			break;
		case Calendar.WEDNESDAY:
			c = db.getReadableDatabase().rawQuery("SELECT * FROM _wednesday_schedule;",null);
			break;
		case Calendar.THURSDAY:
			c = db.getReadableDatabase().rawQuery("SELECT * FROM _thursday_schedule;",null);
			break;
		case Calendar.FRIDAY:
			c = db.getReadableDatabase().rawQuery("SELECT * FROM _friday_schedule;",null);
			break;
		case Calendar.SATURDAY:
			c = db.getReadableDatabase().rawQuery("SELECT * FROM _saturday_schedule;",null);
			break;
		case Calendar.SUNDAY:
			c = db.getReadableDatabase().rawQuery("SELECT * FROM _sunday_schedule;",null);
			break;
		}
		final LocationManager lm = (LocationManager) arg0.getSystemService(Context.LOCATION_SERVICE); 
		final LocationListener onLocationChange = new LocationListener() {
			@Override
			public void onLocationChanged(Location arg0) {
			}
			@Override
			public void onProviderDisabled(String arg0) {}
			@Override
			public void onProviderEnabled(String arg0) {}
			@Override
			public void onStatusChanged(String arg0, int arg1, Bundle arg2) {}
		};
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 48000,100, onLocationChange); //get location updates every 8 minutes
		Intent myIntent;
		PendingIntent pi;
		AlarmManager am = (AlarmManager) arg0.getSystemService(Context.ALARM_SERVICE);
		if(c.moveToFirst()){
			do
			{
				myIntent = new Intent(arg0,AlarmActivity.class);
				myIntent.putExtra("course",c.getString(1));
				myIntent.putExtra("location",c.getString(2));
				myIntent.putExtra("hour", c.getString(3));
				myIntent.putExtra("minute",c.getString(4));
				myIntent.putExtra("sound",c.getString(7));
				Date d = new Date();
				cal.setTime(d);	
				cal.set(Calendar.HOUR_OF_DAY,c.getInt(3));
				cal.set(Calendar.MINUTE,c.getInt(4));
				cal.set(Calendar.SECOND,0);
				if(cal.getTimeInMillis()<Calendar.getInstance().getTimeInMillis()) //if this class is already complete (in case pressing done button)
					continue;
				cal.add(Calendar.MINUTE,-10);//give alarm 10mins before the class
				pi = PendingIntent.getActivity(arg0,(int)cal.getTimeInMillis(), myIntent,PendingIntent.FLAG_CANCEL_CURRENT);
				am.set(AlarmManager.RTC_WAKEUP,cal.getTimeInMillis(),pi);
				myIntent = new Intent("in.ac.iitm.classalerts.classstart");
				cal.add(Calendar.MINUTE,10); //save when the class starts. User is in class.Alarms should not be played. Only reminders
				pi = PendingIntent.getBroadcast(arg0,(int)cal.getTimeInMillis()*10,myIntent, PendingIntent.FLAG_CANCEL_CURRENT);
				am.set(AlarmManager.RTC_WAKEUP,cal.getTimeInMillis(),pi);
				myIntent = new Intent("in.ac.iitm.classalerts.classend"); 
				cal.set(Calendar.HOUR_OF_DAY,c.getInt(5));
				cal.set(Calendar.MINUTE,c.getInt(6));//when the class ends, alarms can be played
				pi = PendingIntent.getBroadcast(arg0,(int)cal.getTimeInMillis()*100,myIntent,PendingIntent.FLAG_CANCEL_CURRENT);
				am.set(AlarmManager.RTC_WAKEUP,cal.getTimeInMillis(),pi);
			}while(c.moveToNext());
		}
		db.close();
		cal.add(Calendar.DATE,1);
		cal.set(Calendar.HOUR_OF_DAY,0);
		cal.set(Calendar.MINUTE,0);
		cal.set(Calendar.SECOND,0);
		myIntent = new Intent("in.ac.iitm.classalerts.updatealarm"); //set AlarmLoader to start itself at mid night
		pi = PendingIntent.getBroadcast(arg0, (int)cal.getTimeInMillis(),myIntent,PendingIntent.FLAG_CANCEL_CURRENT);
		am.set(AlarmManager.RTC_WAKEUP,cal.getTimeInMillis(),pi);	
	}
}
