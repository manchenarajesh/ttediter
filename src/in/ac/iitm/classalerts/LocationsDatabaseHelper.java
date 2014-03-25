package in.ac.iitm.classalerts;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LocationsDatabaseHelper extends SQLiteOpenHelper{

	public LocationsDatabaseHelper(Context context) {
		super(context,"LocationsDB",null,1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE locations (_id INTEGER PRIMARY KEY AUTOINCREMENT,_location TEXT,_latitude TEXT,_longitude TEXT);");		
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		
	}

}
