package in.ac.iitm.classalerts;

import java.io.FileInputStream;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/*This activity is called when the alarm is displayed ie 10mins befire the start of the class */
public class AlarmActivity extends Activity {


    Button okButton;
    TextView courseView,locationView;
    TextView clock;
	boolean play=true; //play sound or not ?
	MediaPlayer mp;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        Window window = getWindow();
        Vibrator vib = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        okButton = (Button) findViewById(R.id._okButton);
        clock = (TextView) findViewById(R.id.analogClock1);
        courseView = (TextView) findViewById(R.id._course);
        locationView = (TextView) findViewById(R.id._location);
        
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        Bundle extras = getIntent().getExtras();
        locationView.setText(extras.getString("location"));
		Uri soundUri = Uri.parse(extras.getString("sound"));
		courseView.setText(extras.getString("course"));
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY,Integer.parseInt(extras.getString("hour")));
		cal.set(Calendar.MINUTE,Integer.parseInt(extras.getString("minute")));
		SimpleDateFormat df = new SimpleDateFormat("hh:mm aa");
		clock.setText(df.format(cal.getTime()));
		LocationsDatabaseHelper ldh = new LocationsDatabaseHelper(this);
		Cursor c = ldh.getReadableDatabase().rawQuery("SELECT * FROM locations;",null);
		c.moveToFirst();
		do
		{
			if(c.getString(1).equalsIgnoreCase(extras.getString("location")))
				break;
		}while(c.moveToNext());
		double latitude = Double.parseDouble(c.getString(2));
		double longitude = Double.parseDouble(c.getString(3));
		LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Location l = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER); //approximation here since getting current location will take time and may not be accurate
		Location l1 = new Location("reverseGeocoded");
		l1.setLatitude(latitude);
		l1.setLongitude(longitude);
		ldh.close();
		if(l.distanceTo(l1)<100) //if we are less than 100m away from the classroom
		{
			play=false;
		}
		try {
			FileInputStream fis = openFileInput("classalerts.lock"); 
			if(fis.read()==0) //if already one class is going on
				play=false;
			fis.close();	
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			mp = MediaPlayer.create(this,soundUri);
			mp.setVolume(1,1); //set maximum volume
			mp.setLooping(true); //play sound in loop
			vib.vibrate(1000); //vibrate for one second
			if(play)
					mp.start();
			okButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					mp.stop();
					finish();
				}
			});
		}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_alarm, menu);
        return true;
    }
    
    public void onPause() //if user presses back button instead of ok
    {
    	super.onPause();
    	mp.stop();
    }
}
