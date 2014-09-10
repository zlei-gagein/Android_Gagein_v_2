package com.gagein.model.filter;

import java.util.List;

import com.gagein.util.Log;

public class QueryInfoItem {
	
	private String id;
	
	private String name;
	
	private String type;
	
	private String displayName;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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
	
	public String setDisplayName(List<QueryInfoItem> queryInfoItems) {
		
		String name = "";
		for (int i = 0; i < queryInfoItems.size(); i ++) {
			
			if (i != queryInfoItems.size() - 1) {
				name = name + queryInfoItems.get(i).getName() + ", ";
			} else {
				name = name + queryInfoItems.get(i).getName();
			}
		}
		
		this.displayName = name;
		return displayName;
	}
	
	public String getDisplayName() {
		
		Log.v("silen", "type = " + type);
		
		if (type.equalsIgnoreCase("search_date_range")) {
			
			if (id.equalsIgnoreCase("1")) {
				return "Day";
			} else if (id.equalsIgnoreCase("7")) {
				return "Week";
			} else if (id.equalsIgnoreCase("14")) {
				return "2 Weeks";
			} else if (id.equalsIgnoreCase("30")) {
				return "Month";
			} else if (id.equalsIgnoreCase("90")) {
				return "3 Months";
			}
			
		} else if (type.equalsIgnoreCase("milestone_type")) {
			
			return name + ": " + displayName;
			
		} else if (type.equalsIgnoreCase("org_employee_size")) {
			
			return name + " employees";
			
		} 
		
		
		
		return name;
	}
	

}
