package com.gagein.model;

import org.json.JSONObject;

public class MediaFilter extends DataModel {

	public String name = null;
	public Boolean checked = false;
	
	
	@Override
	public void parseData(JSONObject aJSONObject) {
		if (aJSONObject == null) return;
		super.parseData(aJSONObject);
		
		id = aJSONObject.optLong("media_id");
		name = aJSONObject.optString("media_name");
	}
	
	
}
