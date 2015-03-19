package fi.jamk.harkka;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

	public static final String DATABASE_NAME ="locations.db";
	public static final String TABLE_NAME ="location";
	public static final String LOCATION_ID="L_Id";
	public static final String LOCATION_NUMBER ="number";
	public static final String LOCATION_DESC ="description";
	public static final String LOCATION_TITLE ="title";
	public static final String LOCATION_LAT ="latitude";
	public static final String LOCATION_LONG ="longitude";
	private static final String DATABASE_CREATE = 
			"create table " + TABLE_NAME + 
			"(" + LOCATION_ID + " integer primary key autoincrement not null, "
				+LOCATION_NUMBER+" integer not null, "
				+LOCATION_TITLE+ " text ,"+
					LOCATION_DESC + " text ,"
				+ LOCATION_LAT+" float not null, " 
				+ LOCATION_LONG+" float not null "
			+" );";

	
	public DatabaseHelper(Context context) 
	{
		super(context, DATABASE_NAME, null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) 
	{
		db.execSQL(DATABASE_CREATE);			
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(db);
	}
}
