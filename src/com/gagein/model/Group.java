package com.gagein.model;

import java.io.Serializable;

import org.json.JSONObject;

public class Group extends DataModel implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String mogid;
	
	private String status;
	
	private int count;
	
	private Boolean isSsystem;
	
	private String name;
	
	private int followLinkType;
	
	public int getFollowLinkType() {
		return followLinkType;
	}

	public void setFollowLinkType(int followLinkType) {
		this.followLinkType = followLinkType;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public Boolean getIsSsystem() {
		return isSsystem;
	}

	public void setIsSsystem(Boolean isSsystem) {
		this.isSsystem = isSsystem;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMogid() {
		return mogid;
	}

	public void setMogid(String mogid) {
		this.mogid = mogid;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	@Override
	public void parseData(JSONObject aJSONObject) {
		
		if (aJSONObject == null) return;
		
		this.mogid = aJSONObject.optString("groupid");
		this.status = aJSONObject.optString("status");
		this.isSsystem = aJSONObject.optBoolean("is_system");
		this.name = aJSONObject.optString("group_name");
		this.count = aJSONObject.optInt("count");
		this.followLinkType = aJSONObject.optInt("follow_link_type");
	}
	
}
