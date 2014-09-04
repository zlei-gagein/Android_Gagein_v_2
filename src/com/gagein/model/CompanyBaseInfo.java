package com.gagein.model;

import java.io.Serializable;

import org.json.JSONObject;

public class CompanyBaseInfo extends DataModel implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String name = null;
	public long id;
	public String source = null;
	
	
	@Override
	public void parseData(JSONObject aJSONObject) {
		if (aJSONObject == null) return;
		super.parseData(aJSONObject);
		
		name = aJSONObject.optString("org_name");
		id = aJSONObject.optLong("orgid");
		source = aJSONObject.optString("source_text");
	}
	
	
}
