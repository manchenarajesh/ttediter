package in.ac.iitm.classalerts;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/*This is called when class ends */
public class ClassEndReciever extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			FileOutputStream f =context.openFileOutput("classalerts.lock",Context.MODE_PRIVATE);
			f.write(-1);//note class has ended
			f.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
