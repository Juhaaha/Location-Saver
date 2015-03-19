package fi.jamk.harkka;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;



public class MainActivity extends Activity implements SavingLocPointDialog.SavingDialogListener, 
 EditItemDialog.editDialogListener, SelectActionDialog.ActionDialogListener, LocationsDialog.LocationsDialogListener
{

    private GoogleMap myMap;
    private LatLng curLoc;
  	private LatLng clicked;
  	private LatLng selected;
    private EditText et;
    private String strSearch;
    private Geocoder gc;
    private SQLiteDatabase db;
    private DatabaseHelper dbh;
    InputMethodManager im;
    DialogFragment df;
    LocPoint loc;
    Spinner spinner;
    private ArrayList<Marker> markerlist;
    private  ArrayList<Integer> spinnersList;
    public static ArrayList<String> loclist;
    private ArrayList<LocPoint> locpointlist;
    private String[] all={DatabaseHelper.LOCATION_ID, DatabaseHelper.LOCATION_NUMBER, DatabaseHelper.LOCATION_TITLE, DatabaseHelper.LOCATION_DESC, DatabaseHelper.LOCATION_LAT,   		
			DatabaseHelper.LOCATION_LONG};
    
    public static String locTitle;
    public static String locDesc;
    public static LatLng locPos;
    public static int locNum;
    public static String strAddress;
    
  
  
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
	
		markerlist =new ArrayList<Marker>();
		locpointlist=new ArrayList<LocPoint>();
		spinnersList=new ArrayList<Integer>();
		loclist =new ArrayList<String>();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		et =(EditText)findViewById(R.id.edit);
		et.clearFocus();
		gc =new Geocoder(this);
		im =(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		dbh=new DatabaseHelper(this);
		db =dbh.getWritableDatabase();
		spinner =(Spinner) findViewById(R.id.spinner);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) 
			{
				myMap.clear();
				if(loclist.size()> 0 || locpointlist.size() > 0 || markerlist.size()> 0)
				{
					loclist.clear();
					locpointlist.clear();
					markerlist.clear();
				}
				//position 0 = shows all saved locations in database
				if(position!=0)
				{
					String selectedRow =parent.getSelectedItem().toString();
					String query ="select * from "+DatabaseHelper.TABLE_NAME +" where "+DatabaseHelper.LOCATION_NUMBER+" = "+selectedRow+" order by "+DatabaseHelper.LOCATION_TITLE;
					Cursor cursor =	db.rawQuery(query, null);
					
					if(cursor.getCount()> 0)
					{						
						
						while(cursor.moveToNext())
						{							
							LocPoint locpoint =new LocPoint();
							locpoint.setNumber(cursor.getInt(1));
							locpoint.setTitle(cursor.getString(2));
							locpoint.setDescription(cursor.getString(3));
							LatLng t =new  LatLng(cursor.getFloat(4), cursor.getFloat(5));
							locpoint.setPoint(t);
							locpointlist.add(locpoint);
							loclist.add(locpoint.toString());							
							Marker m= myMap.addMarker(new MarkerOptions().position(t).snippet(locpoint.getDesc()).title(locpoint.getTitle()));
							markerlist.add(m);
							myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(t, 16));
						}						
					}
				}
				if(position == 0)
				{	
					String query ="select * from "+DatabaseHelper.TABLE_NAME+" order by "+DatabaseHelper.LOCATION_TITLE;
					Cursor cursor =	db.rawQuery(query, null);
					if(cursor.getCount()> 0)
					{						
						
						while(cursor.moveToNext())
						{							
							LocPoint locpoint =new LocPoint();
							locpoint.setNumber(cursor.getInt(1));
							locpoint.setTitle(cursor.getString(2));
							locpoint.setDescription(cursor.getString(3));
							LatLng t =new  LatLng(cursor.getFloat(4), cursor.getFloat(5));
							locpoint.setPoint(t);
							locpointlist.add(locpoint);
							loclist.add(locpoint.toString());							
							Marker m= myMap.addMarker(new MarkerOptions().position(t).snippet(locpoint.getDesc()).title(locpoint.getTitle()));
							m.showInfoWindow();
							markerlist.add(m);
						}						
					}
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

				
			}
			
		});
	
	et.setOnClickListener(new OnClickListener(){

		@Override
		public void onClick(View v) 
		{
			im.showSoftInput(v, 0);
		}
		});
		
		
		// Getting Google Play availability status
		if (serviceAvailable()) 
		{
			initMap();

		}
	}
	
	//fills spinner with distinct numbers
	public void fillSpinner()
	{
		spinnersList.clear();
		try
		{
			spinnersList.add(0);
		String query ="select distinct "+DatabaseHelper.LOCATION_NUMBER+" from "+DatabaseHelper.TABLE_NAME;
		Cursor c =db.rawQuery(query, null);
		if(c.getCount()>0)
			{
				for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext())
				{
					spinnersList.add(c.getInt(0));
				}
			ArrayAdapter<Integer> adapter =new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item, spinnersList);
			adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
			spinner.setAdapter(adapter);
			}
		}
		catch(Exception e)
		{
			Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
		}
	}
	
	// is Google Play Services running
	private boolean serviceAvailable() {
		// Getting Google Play availability status
		int errorCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (errorCode != ConnectionResult.SUCCESS) {
			GooglePlayServicesUtil.getErrorDialog(errorCode, this, 0).show();
			return false;
		} else {
			return true;
		}
	}
	
	//initialazes the map & and adds onclicklisteners for map & markers
	private void initMap() 
	{
		
		myMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();		
        myMap.setMyLocationEnabled(true);
		myMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);			
		
		myMap.setOnMarkerClickListener(new OnMarkerClickListener() {
	        @Override
	        public boolean onMarkerClick( Marker marker) 
	        {	        	
				try 
				{
	
					
					marker.showInfoWindow();
					clicked =new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
					 locTitle =marker.getTitle();
				    locDesc =marker.getSnippet();
				    String query ="select * from "+DatabaseHelper.TABLE_NAME+" where "+
				    DatabaseHelper.LOCATION_TITLE+" = '"+locTitle+"' and "+DatabaseHelper.LOCATION_DESC+" = '"+locDesc+"'";
				    Cursor cursor =db.rawQuery(query, null);
				    cursor.moveToFirst();
				    locNum=cursor.getInt(1);
				   
				    
				  
				    loc =new LocPoint(clicked, locTitle, locDesc);
				    loc.setNumber(locNum);				    				    
				     locPos =selected;
				     df =new SelectActionDialog();
				     df.show(getFragmentManager(), "sad");
					return true;
				}
				 catch (Exception e) 
				 {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
					return false;

	        	}
	        	
	        }
		});	
		
		myMap.setOnMapClickListener( new OnMapClickListener()
		{
			
			@Override
			public void onMapClick(LatLng point) {
				
				im.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
				List<Address> lista1;
				try {
					
					 
						curLoc =new LatLng(point.latitude, point.longitude);
						lista1 =gc.getFromLocation(point.latitude, point.longitude, 1);
						strAddress=lista1.get(0).getAddressLine(0)+", "+lista1.get(0).getAddressLine(1);
						df =new SavingLocPointDialog();
				    	df.show(getFragmentManager(), "dialog1");

				} catch (Exception e) {
					Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
					e.printStackTrace();
				}	
			}			
		});		
		
		
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public void onDestroy ()
    {
    	db.close();
    	super.onDestroy();   	      	
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) 
    {    	
        int id = item.getItemId();
      
        if(id ==R.id.action_erase)
        {
        	myMap.clear();
        	Toast.makeText(getApplicationContext(), "Markers erased from map", Toast.LENGTH_LONG).show();
        	return true;
        }
      if(id ==R.id.action_exit)
    	  this.finish();
      return super.onOptionsItemSelected(item);
        
    }
    
    public void updateMap()
    {
    	myMap.clear();
    	String query ="select * from "+DatabaseHelper.TABLE_NAME+" order by "+DatabaseHelper.LOCATION_TITLE;
		Cursor c =	db.rawQuery(query, null);
    	Toast.makeText(getApplicationContext(), "There is  "+c.getCount()+" items on map", Toast.LENGTH_LONG).show();
    	if(loclist.size()> 0)
			loclist.clear();
    	if(c.getCount()>0)
    	{	
    		while(c.moveToNext())
    		{   		
    			LocPoint tempPoint =new LocPoint();
    			tempPoint.setNumber(c.getInt(1));
    			tempPoint.setTitle(c.getString(2));
    			tempPoint.setDescription(c.getString(3));
    			LatLng t =new  LatLng(c.getFloat(4), c.getFloat(5));
    			tempPoint.setPoint(t);
    			loclist.add(tempPoint.toString());
    			myMap.addMarker(new MarkerOptions().position(t).title(tempPoint.getTitle()).snippet((tempPoint.getDesc()))).showInfoWindow();
    		}
    	}  
    }
    public void showLocations(View v)
    {
    	try
    	{		
    		updateMap();
    		fillSpinner(); 
    	}
    	catch(Exception e)
    	{
    		Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
    	}	    	
    }
    public void saveMyLocation(View v)
    {
    	try
    	{    		  		
    		 curLoc =new LatLng(myMap.getMyLocation().getLatitude(), myMap.getMyLocation().getLongitude());
    		 List<Address> lista2 =gc.getFromLocation(curLoc.latitude, curLoc.longitude, 1);
 			strAddress=lista2.get(0).getAddressLine(0).toString()+", "+lista2.get(0).getAddressLine(1).toString();
 			df =new SavingLocPointDialog();
 			df.show(getFragmentManager(), "dialog1");
    	}
    	catch(Exception ex)
    	{
    		Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_LONG).show();
    	}
    }
	public void search(View v)
	{
		
		strSearch =et.getText().toString();
		im.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
		et.clearFocus();
		
		if(strSearch.length() !=0)
		{
			try
			{
				List<Address> lista;
				Address adrs;
				lista =gc.getFromLocationName(strSearch, 1);
				adrs =lista.get(0);
				
				String locality =lista.get(0).getLocality();
				LatLng lat =new LatLng(adrs.getLatitude(), adrs.getLongitude());
				myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lat, 16));
						
				et.getText().clear();

					Toast.makeText(getApplicationContext(), "Location found! "+adrs.getAddressLine(0)+", "+adrs.getAddressLine(1), Toast.LENGTH_LONG).show();
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_LONG).show();
			}
		}
	}
	public void showList(View v)
	{
		DialogFragment df =new LocationsDialog();
		df.show(getFragmentManager(), "locationsdialog");
	}
	

	//SavingLocPointDialogs button clicks
	@Override
	public void onSavingDialogPositiveClick(String desc, String title, int nmb) 
	{
		try
		{
		 ContentValues values =new ContentValues();
		values.put(DatabaseHelper.LOCATION_NUMBER, nmb);
		values.put(DatabaseHelper.LOCATION_TITLE, title);
		values.put(DatabaseHelper.LOCATION_DESC, desc);
		values.put(DatabaseHelper.LOCATION_LAT, (float)curLoc.latitude);
		values.put(DatabaseHelper.LOCATION_LONG, (float)curLoc.longitude);	
		db.insert(DatabaseHelper.TABLE_NAME, null, values);
		updateMap();
		fillSpinner();
		Toast.makeText(getApplicationContext(), "Added item to database ", Toast.LENGTH_LONG).show();		
		}
		catch(Exception e)
		{
			Toast.makeText(getApplicationContext(), "something went wrong : "+e.toString(), Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onSavingDialogNegativeClick() 
	{
		Toast.makeText(getApplicationContext(), "Cancel", Toast.LENGTH_LONG).show();
		
	}

	//SelectActionDialogs button clicks
	@Override
	public void onActionDialogPositiveClick(int actPos) {
		
		switch( actPos)
		{
		case 0:
				db.delete(DatabaseHelper.TABLE_NAME, DatabaseHelper.LOCATION_DESC+" = '"+loc.getDesc() +"' and "+DatabaseHelper.LOCATION_TITLE+ " = '"+loc.getTitle()+"' and "+DatabaseHelper.LOCATION_NUMBER+" ="+loc.getNumber()+";", null);
				updateMap();
				fillSpinner();
				Toast.makeText(getApplicationContext(), "Itemi poistettu tietokannasta", Toast.LENGTH_LONG).show();		
				break;
		case 1: 
				df=new EditItemDialog(); 
				df.show(getFragmentManager(), "dialog3");	
				break;
	
			default: Toast.makeText(getApplicationContext(), "Tapahtui virhe", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onActionDialogNegativeClick() 
	{
		Toast.makeText(getApplicationContext(), "Cancel", Toast.LENGTH_LONG).show();		
	}

	
	//EditItemDialogs button clicks
	@Override
	public void onEditDialogPositiveClick(String descrp, String title, int nmb) 
	{				
		ContentValues values =new ContentValues();
		values.put(DatabaseHelper.LOCATION_NUMBER, nmb);
		values.put(DatabaseHelper.LOCATION_TITLE, title);
		values.put(DatabaseHelper.LOCATION_DESC, descrp);
		db.update(DatabaseHelper.TABLE_NAME, values, DatabaseHelper.LOCATION_DESC+" = '"+loc.getDesc()+"' and "
		+DatabaseHelper.LOCATION_TITLE+" = '"+loc.getTitle()+"' and "+DatabaseHelper.LOCATION_NUMBER+" = '"+loc.getNumber()+"' ;" , null);
		Toast.makeText(getApplicationContext(), "Tietokanta päivitetty!", Toast.LENGTH_LONG).show();
		fillSpinner();
		updateMap();
		
	}

	@Override
	public void onEditDialogNegativeClick() 
	{
		Toast.makeText(getApplicationContext(), "Cancel", Toast.LENGTH_LONG).show();		
	}

	//LocationsDialog button clicks
	@Override
	public void onLocationsDialogPositiveClick(int pos) 
	{
		final LatLng focusPoint =locpointlist.get(pos).getPoint();
		myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(focusPoint, 16));
		markerlist.get(pos).showInfoWindow();		
	}

	@Override
	public void onLocationsDialogNegativeClick() 
	{	
		Toast.makeText(getApplicationContext(), "Cancel", Toast.LENGTH_LONG).show();	
	}	
}
