package com.gagein.model;

import org.json.JSONObject;

public class MenuData extends DataModel {

	public static final byte kGGMenuTypeCompany = 0;
	public static final byte kGGMenuTypeAgent = 1;
	public static final byte kGGMenuTypePerson = 2;
	public static final byte kGGMenuTypeFunctionalArea = 3;
	
	public String name = null;
	public String timeInterval = null;
	public String grade = null;
	
	public boolean checked = false;
	public int type = kGGMenuTypeCompany;
	
	
	@Override
	public void parseData(JSONObject aJSONObject) {
		if (aJSONObject == null) return;
		super.parseData(aJSONObject);
		
		id = aJSONObject.optLong("menuid");
		name = aJSONObject.optString("menu_name");
		timeInterval = aJSONObject.optString("time_interval");
		grade = aJSONObject.optString("grade");
	}
	
	
}

/**

*/