package fi.jamk.harkka;

import com.google.android.gms.maps.model.LatLng;

public class LocPoint 
{
	
	private LatLng point;
	private String title;
	private String description;
	private int number;
	

public LocPoint(){}
public LocPoint( LatLng point, String title, String desc)
{
	
	this.title =title;
	this.point=point;
	this.description =desc;
}

	public void setPoint(LatLng ln){ this.point =ln;}
	public void setTitle(String t) {title =t;}
	public void setDescription(String desc) {this.description =desc;}
	public void setNumber(int i) {this.number =i;}
	public LatLng getPoint() { return point;}
	public String getDesc() {return description;}
	public String getTitle() {return title;}
	public int getNumber() {return number;}
	
	@Override
	public String toString()
	{ return title+": "+description;}
	
	
}
