package com.gagein.model;

import java.io.Serializable;

import org.json.JSONObject;

public class FacetItemIndustry extends DataModel implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public String status = null;
	public String item_name = null;
	public String filter_param_value = null;
	public int count;

	
	@Override
	public void parseData(JSONObject aJSONObject) {
		if (aJSONObject == null) return;
		super.parseData(aJSONObject);
		
		id = aJSONObject.optLong("id");
		count = aJSONObject.optInt("count");
		status = aJSONObject.optString("status");
		item_name = aJSONObject.optString("item_name");
		filter_param_value = aJSONObject.optString("filter_param_value");
	}
}
