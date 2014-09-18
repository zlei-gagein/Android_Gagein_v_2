package com.gagein.model;

import java.io.Serializable;

import org.json.JSONObject;

public class FacetItem extends DataModel implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int TYPE_FUNCTIONAL_ROLE = 0;
	public static final int TYPE_JOB_LEVEL = 1;
	
	public String name = null;
	public int count = 0;
	
	public int type;
	public Boolean selected = false;

	
	@Override
	public void parseData(JSONObject aJSONObject) {
		if (aJSONObject == null) return;
		super.parseData(aJSONObject);
		
		id = aJSONObject.optLong("id");
		name = aJSONObject.optString("name");
		count = aJSONObject.optInt("count");
	}
}
