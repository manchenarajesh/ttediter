package in.ac.iitm.classalerts;

import java.util.Calendar;

import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class TimeTableEditor extends Activity {

	public static int soundSelectReqCode = 2; 
	
	
	Spinner dayChooser;
	Spinner locChooser;
	Button okButton;
	Button cancelButton;
	Button soundSelectButton;
	EditText courseSelector;
	TimePicker startTimeChooser;
	TimePicker endTimeChooser;
	String oldCourse;
	Uri soundUri;
	boolean edit;
	String[] from = {"_location"};
	int[] to = {android.R.id.text1};
	SimpleCursorAdapter dataAdapter;
	Cursor c;
	LocationsDatabaseHelper h;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_table_editor);
        locChooser = (Spinner) findViewById(R.id.locationSpinner);
        okButton = (Button) findViewById(R.id.okButton);
        cancelButton = (Button) findViewById(R.id.cancelButton);
        soundSelectButton = (Button) findViewById(R.id.soundSelectButton);
        courseSelector = (EditText) findViewById(R.id.courseName);
        startTimeChooser = (TimePicker) findViewById(R.id.startTimePicker);
        endTimeChooser = (TimePicker) findViewById(R.id.endTimePicker);
        soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if(soundUri == null){
            soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            if(soundUri == null){ 
                soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);               
            }
        }
        
        
        Bundle extras = getIntent().getExtras();
        edit = extras.getBoolean("edit");
        h = new LocationsDatabaseHelper(this);
        c = h.getReadableDatabase().rawQuery("SELECT * FROM locations;",null);
        dataAdapter = new SimpleCursorAdapter(this,android.R.layout.simple_spinner_dropdown_item,c,from,to);
        locChooser.setAdapter(dataAdapter);
        if(edit)
        {
        	oldCourse = extras.getString("course");
        	okButton.setText("Save");
        	startTimeChooser.setCurrentHour(Integer.parseInt(extras.getString("startHour")));
        	startTimeChooser.setCurrentMinute(Integer.parseInt(extras.getString("startMinute")));
        	endTimeChooser.setCurrentHour(Integer.parseInt(extras.getString("endHour")));
        	endTimeChooser.setCurrentMinute(Integer.parseInt(extras.getString("endMinute")));
        	courseSelector.setText(extras.getString("course"));
        	for(int i=0;i<locChooser.getCount();i++)
        	{
        		locChooser.setSelection(i);
        		if(((Cursor)locChooser.getSelectedItem()).getString(1).equals(extras.getString("location")))
        			break;
        	}
        	soundUri = Uri.parse(extras.getString("sound"));
        	
        }

        soundSelectButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {	
				Intent intent = new Intent();
				intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);
				intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT,true);
				intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE,RingtoneManager.TYPE_ALL);
				intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE,"Select alarm tone..");
				intent.setAction(RingtoneManager.ACTION_RINGTONE_PICKER);
				startActivityForResult(intent,TimeTableEditor.soundSelectReqCode);
			}
		});
        
        cancelButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent data = new Intent();
				setResult(RESULT_CANCELED,data);
				h.close();
				finish();
			}
		});
        
        okButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent data = new Intent();
				Bundle b = new Bundle();
				String s = courseSelector.getText().toString().trim();
				Calendar start = Calendar.getInstance();
				start.set(Calendar.HOUR_OF_DAY,startTimeChooser.getCurrentHour());
				start.set(Calendar.MINUTE,startTimeChooser.getCurrentMinute());
				start.set(Calendar.SECOND,0);
				
				Calendar end = Calendar.getInstance();
				end.set(Calendar.HOUR_OF_DAY,endTimeChooser.getCurrentHour());
				end.set(Calendar.MINUTE,endTimeChooser.getCurrentMinute());
				end.set(Calendar.SECOND,0);
				if(s.length()<=0)
				{
					Toast.makeText(v.getContext(),"Please enter a course",Toast.LENGTH_SHORT).show();
				}
				else if(((Cursor)locChooser.getSelectedItem())==null)
				{
					Toast.makeText(v.getContext(),"Please select a location",Toast.LENGTH_SHORT).show();
				}
				else if(start.getTimeInMillis()>end.getTimeInMillis())
				{
					Toast.makeText(v.getContext(),"End time cannot be before starting time",Toast.LENGTH_SHORT).show();
				}
				else
				{
					if(edit)
						b.putString("oldCourse",oldCourse);
					b.putString("course",s);
					TextView tv = ((TextView)locChooser.getSelectedView().findViewById(android.R.id.text1));
					b.putString("location",tv.getText().toString());
					b.putString("startHour",startTimeChooser.getCurrentHour().toString());
					b.putString("startMinute",startTimeChooser.getCurrentMinute().toString());
					b.putString("endHour",endTimeChooser.getCurrentHour().toString());
					b.putString("endMinute",endTimeChooser.getCurrentMinute().toString());
					b.putString("sound",soundUri.toString());
					data.putExtras(b);
					h.close();
					setResult(RESULT_OK,data);
					finish();
				}
			}
		});
    }

    public void onActivityResult(int reqCode,int respCode,Intent data)
    {
    	if(reqCode==TimeTableEditor.soundSelectReqCode)
    	{
    		if(respCode == RESULT_OK)
    		{
    			soundUri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
    			if(soundUri==null)
    			{
    		        soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
    		        if(soundUri == null){
    		            soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    		            if(soundUri == null){ 
    		                soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);               
    		            }
    		        }
    			}
    		}
    	}
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_time_table_editor, menu);
        return true;
    }
}
