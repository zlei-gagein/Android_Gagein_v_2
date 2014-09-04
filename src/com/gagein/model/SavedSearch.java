package com.gagein.model;

import org.json.JSONObject;

public class SavedSearch extends DataModel {
	
	private String id;
	
	private String text;
	
	private String date;
	
	private String name;
	
	private String type;
	
	private Boolean checked = false;
	
	public Boolean getChecked() {
		return checked;
	}

	public void setChecked(Boolean checked) {
		this.checked = checked;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public void parseData(JSONObject aJSONObject) {
		if (aJSONObject == null) return;
		
		id = aJSONObject.optString("saved_searchid");
		text = aJSONObject.optString("tip_text");
		date = aJSONObject.optString("saved_date");
		name = aJSONObject.optString("search_name");
		type = aJSONObject.optString("search_type");
	}
}
