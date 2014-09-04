package com.gagein.model;

import org.json.JSONObject;
/**
 * 时区
 * @author silen
 *
 */
public class TimeZone {

	/**
	 *id 
	 */
	private String id = "";
	
	/**
	 * 世界时
	 */
	private String gmtnum = "";
	
	/**
	 * 地点名
	 */
	private String name = "";

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getGmtnum() {
		return gmtnum;
	}

	public void setGmtnum(String gmtnum) {
		this.gmtnum = gmtnum;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "TimeZone [id=" + id + ", gmtnum=" + gmtnum + ", name=" + name
				+ "]";
	}
	
	/**
	 * 创建时区对象
	 * @param jsonObject
	 * @return
	 */
	public TimeZone createFromJson(JSONObject jsonObject) {
		this.id = jsonObject.optString("id", "");
		this.gmtnum = jsonObject.optString("gmtnum", "");
		this.name = jsonObject.optString("name", "");
		return this;
	}

}
