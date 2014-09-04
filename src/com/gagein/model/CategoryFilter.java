package com.gagein.model;

import org.json.JSONObject;

public class CategoryFilter extends DataModel {

	public String name = null;
	public Boolean checked = false;
	
	
	@Override
	public void parseData(JSONObject aJSONObject) {
		if (aJSONObject == null) return;
		super.parseData(aJSONObject);
		
		id = aJSONObject.optLong("categoryid");
		name = aJSONObject.optString("category_name");
		checked = aJSONObject.optBoolean("checked");
	}
	
	
}

/**

 */
