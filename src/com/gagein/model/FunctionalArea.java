package com.gagein.model;

import org.json.JSONObject;

public class FunctionalArea extends DataModel {

	public Boolean checked = false;
	public String name = null;
	
	
	
	@Override
	public void parseData(JSONObject aJSONObject) {
		if (aJSONObject == null) return;
		super.parseData(aJSONObject);
		
		id = aJSONObject.optLong("functional_areaid");
		checked = aJSONObject.optBoolean("checked");
		name = aJSONObject.optString("functional_area_name");
	}
	
	
}

/**

*/