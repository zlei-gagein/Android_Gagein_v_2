package com.gagein.model;

import java.io.Serializable;

import org.json.JSONObject;

public class FacetItemIndustry extends DataModel implements Serializable{

	private static final long serialVersionUID = 1L;
	
	public String status;
	
	public String item_name;;
	
	public String filter_param_value;;
	
	public int count;
	
	private Boolean selected = false;
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getItem_name() {
		return item_name;
	}

	public void setItem_name(String item_name) {
		this.item_name = item_name;
	}

	public String getFilter_param_value() {
		return filter_param_value;
	}

	public void setFilter_param_value(String filter_param_value) {
		this.filter_param_value = filter_param_value;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
	
	public Boolean getSelected() {
		return selected;
	}

	public void setSelected(Boolean selected) {
		this.selected = selected;
	}

	@Override
	public void parseData(JSONObject aJSONObject) {
		if (aJSONObject == null) return;
		super.parseData(aJSONObject);
		
		this.id = aJSONObject.optLong("id");
		this.count = aJSONObject.optInt("count");
		this.status = aJSONObject.optString("status");
		this.item_name = aJSONObject.optString("item_name");
		this.filter_param_value = aJSONObject.optString("filter_param_value");
		
		if (item_name.equalsIgnoreCase("All Industries")) {
			this.selected = true;
		}
	}
}
