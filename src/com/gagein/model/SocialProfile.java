package com.gagein.model;

import java.io.Serializable;

import org.json.JSONObject;

import com.gagein.util.CommonUtil;

public class SocialProfile implements Serializable{

	private static final long serialVersionUID = 1L;

	/** ----- member variables ----- */
	public String type = null; // salesforce, linkedIn ...
	
	public String url = null; // social profile url
	
	public boolean hasProfileUrl = false;
	

	@Override
	public String toString() {
		return "SocialProfile [type=" + type + ", url=" + url
				+ ", hasProfileUrl=" + hasProfileUrl + "]";
	}

	public SocialProfile parseData(JSONObject aJSONObject) {
		if (aJSONObject == null)
			return null;

		this.type = aJSONObject.optString("type");
		this.url = aJSONObject.optString("url");
		this.hasProfileUrl = aJSONObject.optBoolean("hasProfileUrl");
		return this;
	}

	
	private final String LINKED_IN = "LinkedIn";
	
	public String capitalizedType() {
		String capStr = CommonUtil.capitalizedType(type);
		if (capStr != null && capStr.equalsIgnoreCase(LINKED_IN)) {
			capStr = LINKED_IN;
		}
		
		return capStr;
	}
}

/**

*/
