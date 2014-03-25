package in.ac.iitm.classalerts;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
/*This is called when class starts */
public class ClassStartReciever extends BroadcastReceiver {

	@Override
	public void onReceive(Context arg0, Intent arg1) {
		try {
			FileOutputStream fout = arg0.openFileOutput("classalerts.lock",Context.MODE_PRIVATE);
			fout.write(0);//Note class has started
			fout.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
