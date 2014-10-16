package com.gagein.model;

import android.annotation.SuppressLint;
import java.io.Serializable;
import java.util.Comparator;

import org.json.JSONObject;

public class Agent extends DataModel implements Serializable, Comparable<Agent>{
	
	private static final long serialVersionUID = 1L;
	/** ------ constants -----*/
	public static final byte kGGAgentTypePredefined = 1;
	public static final byte kGGAgentTypeCustom = 2;
	
	/** ------ member variables -----*/
	public int type = 0;
	public boolean checked = false;
	public String agentID = null;
	public String name = null;
	public String keywords = null;
	public double chartPercentage = 0;
	public boolean hasBeenAnimated = false;		// for UI usage
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/** ------ methods -----*/
	@Override
	public void parseData(JSONObject aJSONObject) {
		if (aJSONObject == null) return;
		super.parseData(aJSONObject);
		
		id = aJSONObject.optLong("agentid");
		agentID = id + "";
		type = aJSONObject.optInt("type");
		checked = (aJSONObject.optInt("checked") == 0 ? false : true);
		keywords = aJSONObject.optString("agent_keywords");
		name = aJSONObject.optString("agent_name");
		chartPercentage = aJSONObject.optDouble("chart_percentage");
	}
	
	@Override
	public int compareTo(Agent another) {
		return Comparators.NAME.compare(this, another);
	}
	
	public static class Comparators {
		public static Comparator<Agent> NAME = new Comparator<Agent>() {
			
			@Override
			public int compare(Agent lhs, Agent rhs) {
				return lhs.name.trim().toUpperCase().compareTo(rhs.name.trim().toUpperCase());
			}
		};
	}
	
	
	
	
	
	
	
}

