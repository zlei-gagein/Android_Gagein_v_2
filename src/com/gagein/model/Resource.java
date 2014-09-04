package com.gagein.model;

import org.json.JSONObject;

public class Resource extends DataModel {
	
	public String url;
	
	public int publicationFlag;
	
	public int validStatus;
	
	@Override
	public void parseData(JSONObject aJSONObject) {
		if (aJSONObject == null) return;
		super.parseData(aJSONObject);
		
		url = aJSONObject.optString("url");
		publicationFlag = aJSONObject.optInt("url_publication_flag");
		publicationFlag = aJSONObject.optInt("url_valid_status");
		
	}
}
