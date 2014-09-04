package com.gagein.model;

import java.io.Serializable;

import org.json.JSONObject;

public class Ticker extends DataModel implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String name = null;
	public String url = null;
	
	
	@Override
	public void parseData(JSONObject aJSONObject) {
		if (aJSONObject == null) return;
		super.parseData(aJSONObject);
		
		name = aJSONObject.optString("ticker_name");
		url = aJSONObject.optString("ticker_url");
	}
	
	
}

/**

 */
