package in.ac.iitm.classalerts;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ListView;
import android.widget.Toast;

public class Locations_manager extends Activity {

	String locName="";
	ListView locationsList;
	Button addButton;
	Button doneButton;
	SimpleCursorAdapter listViewAdapter;
	String[] from = {"_location"};
	int[] to = {R.id.location};
	LocationsDatabaseHelper helper;
	AlarmDatabaseHelper alh;
	Double latitude,longitude;
	int respCode;
	Cursor c;
	String str,str2;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locations_manager);
        helper = new LocationsDatabaseHelper(this);
        c = helper.getReadableDatabase().rawQuery("SELECT * FROM locations;",null);
        listViewAdapter = new SimpleCursorAdapter(this,R.layout.locations_list_activity,c,from,to);
        locationsList = (ListView) findViewById(R.id.locationsView);
        locationsList.setAdapter(listViewAdapter);
        addButton = (Button) findViewById(R.id.addButton);
        doneButton = (Button) findViewById(R.id.doneButton);
        locationsList.setItemsCanFocus(true);
        locationsList.setClickable(true);
        locationsList.setSelected(true);
		final LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
		final LocationListener onLocationChange = new LocationListener() {
			@Override
			public void onLocationChanged(Location arg0) {
				latitude = arg0.getLatitude();
				longitude = arg0.getLongitude();
			}
			@Override
			public void onProviderDisabled(String arg0) {}
			@Override
			public void onProviderEnabled(String arg0) {}
			@Override
			public void onStatusChanged(String arg0, int arg1, Bundle arg2) {}
		};
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000,100, onLocationChange);
        locationsList.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				delete(((TextView)arg1.findViewById(R.id.location)).getText().toString().trim());
			}
        	
        });
        doneButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				lm.removeUpdates(onLocationChange);
				helper.close();
				finish();
				
			}
		});
                
        addButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent data = new Intent("in.ac.iitm.classalerts.addLocation");
				startActivityForResult(data,1);
			}
		});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_locations_manager, menu);
        return true;
    }
    
    public void onActivityResult(int requestCode,int resultCode,Intent intent)
    {
    	respCode = resultCode;
    	if(requestCode==1)
    	{
    		if(resultCode==RESULT_OK)
    		{
    			locName=intent.getStringExtra("name");
				ContentValues cv = new ContentValues();
				cv.put("_location",locName);
				if(latitude == null || longitude==null)
				{
					Toast.makeText(this,"Getting location.. Please wait for some time and try again",Toast.LENGTH_SHORT).show();
				}
				else
				{
					cv.put("_latitude",latitude);
					cv.put("_longitude",longitude);
					helper.getWritableDatabase().insert("locations","_location",cv);
					c.requery();
					listViewAdapter.swapCursor(c);
				}
    		}
    	}
    }
    
    private void delete(String location)
    {
    	alh = new AlarmDatabaseHelper(this);
    	str = "_schedule WHERE _location=\""+location+"\";";
    	str2 = "DELETE FROM locations WHERE _location=\""+location+"\";";
    	showDialog(0);
    }
    
    public Dialog onCreateDialog(int id)
    {
    	return new AlertDialog.Builder(this)
    	.setTitle("Delete ?")
    	.setMessage("Are u sure u want to delete this location ? All alarms associated with this location will also be deleted")
    	.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
		    	
		    	alh.getWritableDatabase().execSQL("DELETE FROM _monday"+str);
		    	alh.getWritableDatabase().execSQL("DELETE FROM _tuesday"+str);
		    	alh.getWritableDatabase().execSQL("DELETE FROM _wednesday"+str);
		    	alh.getWritableDatabase().execSQL("DELETE FROM _thursday"+str);
		    	alh.getWritableDatabase().execSQL("DELETE FROM _friday"+str);
		    	alh.getWritableDatabase().execSQL("DELETE FROM _saturday"+str);
		    	alh.getWritableDatabase().execSQL("DELETE FROM _sunday"+str);
		    	alh.close();
		    	helper.getWritableDatabase().execSQL(str2);
				c.requery();
				listViewAdapter.swapCursor(c);
				
			}
		})
		
		.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
			}
		})
		.create();

    }
}
