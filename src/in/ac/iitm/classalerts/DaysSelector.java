package in.ac.iitm.classalerts;


import java.util.Calendar;

import android.os.Bundle;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

/*This activity displays the timetable for each day of the week and provides an interface to edit the timetable */
public class DaysSelector extends Activity implements AdapterView.OnItemSelectedListener{
	
	String columns[]={"_courseName"};
	int to[] = {R.id.course_name};
	AlarmsAdapter dataAdapter;
	ListView dayView;
	Button addButton,doneButton;
	Spinner dayChooser;
	AlarmDatabaseHelper db;
	String day;
	String add;
	Intent i;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_days_selector);
        dayChooser = (Spinner) findViewById(R.id.spinner1);
        dayView = (ListView) findViewById(R.id.listView1);
        addButton = (Button) findViewById(R.id.button1);
        doneButton = (Button) findViewById(R.id.button2);
        dayChooser.setOnItemSelectedListener(this);
        db = new AlarmDatabaseHelper(this);
        dataAdapter = new AlarmsAdapter(this,getData(Calendar.MONDAY),this);
        dayView.setAdapter(dataAdapter);
        addButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				i = new Intent("in.ac.iitm.classalerts.ADDALARM");
				i.putExtra("edit",false);
				startActivityForResult(i,1);
			}
		});
        
        doneButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent("in.ac.iitm.classalerts.updatealarm");
				db.close();
				sendBroadcast(i);
				finish();
			}
		});
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_days_selector, menu);
        return true;
    }
	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		dataAdapter.swapCursor(getData(arg2));
	}
	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onActivityResult(int requestCode,int resultCode,Intent data)
	{
		if(requestCode == 1)
		{
			if(resultCode == RESULT_OK)
			{
				Bundle b = data.getExtras();
				ContentValues cv = new ContentValues();
				cv.put("_courseName",b.getString("course"));
				cv.put("_location",b.getString("location"));
				cv.put("_startHours",b.getString("startHour"));
				cv.put("_startMinutes",b.getString("startMinute"));
				cv.put("_endHours",b.getString("endHour"));
				cv.put("_endMinutes",b.getString("endMinute"));
				cv.put("_soundUri",b.getString("sound"));
				db.getWritableDatabase().insert("_"+dayChooser.getSelectedItem().toString()+"_schedule","_courseName",cv);
				dataAdapter.swapCursor(getData(dayChooser.getSelectedItemPosition()));
			}
		}
		if(requestCode == 2 )
		{
			if(resultCode == RESULT_OK)
			{
				Bundle b = data.getExtras();
				String day = (String) dayChooser.getSelectedItem();
				String add = "DELETE FROM _"+day+"_schedule WHERE _courseName=\""+b.getString("oldCourse")+"\";";
				db.getWritableDatabase().execSQL(add);
				ContentValues cv = new ContentValues();
				cv.put("_courseName",b.getString("course"));
				cv.put("_location",b.getString("location"));
				cv.put("_startHours",b.getString("startHour"));
				cv.put("_startMinutes",b.getString("startMinute"));
				cv.put("_endHours",b.getString("endHour"));
				cv.put("_endMinutes",b.getString("endMinute"));
				cv.put("_soundUri",b.getString("sound"));
				db.getWritableDatabase().insert("_"+dayChooser.getSelectedItem().toString()+"_schedule","_courseName",cv);
				dataAdapter.swapCursor(getData(dayChooser.getSelectedItemPosition()));	
			}
		}
	}

	private Cursor getData(int day)
	{
		Cursor c = null;
		String add = "SELECT * FROM _";
		switch(day)
		{
		case 0:
			c = db.getReadableDatabase().rawQuery(add+"monday_schedule",null);
			break;
		case 1:
			c = db.getReadableDatabase().rawQuery(add+"tuesday_schedule",null);
			break;
		case 2:
			c = db.getReadableDatabase().rawQuery(add+"wednesday_schedule",null);
			break;
		case 3:
			c = db.getReadableDatabase().rawQuery(add+"thursday_schedule",null);
			break;
		case 4:
			c = db.getReadableDatabase().rawQuery(add+"friday_schedule",null);
			break;
		case 5:
			c = db.getReadableDatabase().rawQuery(add+"saturday_schedule",null);
			break;
		case 6:
			c = db.getReadableDatabase().rawQuery(add+"sunday_schedule",null);
			break;
		};
		return c;
	}
	
	public void deleteCourse(String courseName)
	{
		day = (String) dayChooser.getSelectedItem();
		add = "DELETE FROM _"+day+"_schedule WHERE _courseName=\""+courseName+"\";";
		showDialog(1);
	}
	
	public void editCourse(String courseName,Bundle details)
	{
		i = new Intent("in.ac.iitm.classalerts.ADDALARM");
		details.putBoolean("edit",true);
		i.putExtras(details);
		showDialog(2);
		//startActivityForResult(i,2);
	}
	
	public Dialog onCreateDialog(int id)
	{
		switch(id)
		{
		case 1:
			return new AlertDialog.Builder(this)
			.setTitle("delete ?")
			.setMessage("Are u sure u want to delete this alarm ? Alarms scheduled for today will not be cancelled..")
			.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					db.getWritableDatabase().execSQL(add);
					dataAdapter.swapCursor(getData(dayChooser.getSelectedItemPosition()));					
				}
			})
			.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
				}
			})
			.create();
		case 2:
			return new AlertDialog.Builder(this)
			.setTitle("Edit ?")
			.setMessage("Please note alarms scheduled for today will not be changed..")
			.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					startActivityForResult(i,2);
				}
			})
			.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
				}
			})
			.create();
		}
		return null;
	}
	
	public void onStop()
	{
		super.onStop();
		db.close();
	}
}

