package in.ac.iitm.classalerts;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/*This class is an adapter for a listview to display classes */
public class AlarmsAdapter extends CursorAdapter {

	private final LayoutInflater iInflater;
	private LocationsDatabaseHelper ldb;
	private DaysSelector act;

	public AlarmsAdapter(Context context, Cursor c,DaysSelector s) {
		super(context, c);
		iInflater = LayoutInflater.from(context);
		ldb = new LocationsDatabaseHelper(context);
		act=s;
	}

	@Override
	public void bindView(View view, Context arg1, final Cursor c) {
		TextView courseName = (TextView) view.findViewById(R.id.course_name);
		TextView time = (TextView) view.findViewById(R.id.time);
		TextView location = (TextView) view.findViewById(R.id.location);
		courseName.setText(c.getString(1));
		SimpleDateFormat df = new SimpleDateFormat("hh:mm aa");
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal1.set(Calendar.HOUR_OF_DAY,c.getInt(3));
		cal2.set(Calendar.HOUR_OF_DAY,c.getInt(5));
		cal1.set(Calendar.MINUTE,c.getInt(4));
		cal2.set(Calendar.MINUTE,c.getInt(6));
		time.setText(df.format(cal1.getTime())+" - "+df.format(cal2.getTime()));
		location.setText(c.getString(2));
		final Button editButton = (Button) view.findViewById(R.id.editButton);
		Bundle details = new Bundle();
		details.putString("course",c.getString(1));
		details.putString("startHour",c.getString(3));
		details.putString("startMinute",c.getString(4));
		details.putString("endHour",c.getString(5));
		details.putString("endMinute",c.getString(6));
		details.putString("location",c.getString(2));
		details.putString("sound",c.getString(7));
		editButton.setTag(details);
		ldb.close();
		final Button deleteButton = (Button) view.findViewById(R.id.deleteButton);
		deleteButton.setTag(c.getString(1));
		editButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Bundle extras = (Bundle) editButton.getTag();
				String course = extras.getString("course");
				act.editCourse(course,extras);
			}
		});
		
		deleteButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				act.deleteCourse((String) deleteButton.getTag());
			}
		});
				
	}

	@Override
	public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
		final View view = iInflater.inflate(R.layout.alarm_info,arg2,false);
		return view;
	}

}
