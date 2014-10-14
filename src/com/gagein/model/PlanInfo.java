package com.gagein.model;

import java.io.Serializable;

import org.json.JSONObject;

public class PlanInfo extends DataModel implements Serializable{
	
	private static final long serialVersionUID = -4120355188480064068L;

	private String teamOrgid;
	
	private String pid;
	
	private String teamName;
	
	private String accessToken;
	
	private String accountStatus;
	
	private String memid;

	public String getTeamOrgid() {
		return teamOrgid;
	}

	public void setTeamOrgid(String teamOrgid) {
		this.teamOrgid = teamOrgid;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getTeamName() {
		return teamName;
	}

	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getAccountStatus() {
		return accountStatus;
	}

	public void setAccountStatus(String accountStatus) {
		this.accountStatus = accountStatus;
	}

	public String getMemid() {
		return memid;
	}

	public void setMemid(String memid) {
		this.memid = memid;
	}
	
	 @Override
	 public void parseData(JSONObject aJSONObject) {
		 if (aJSONObject == null)
			 return;
		 super.parseData(aJSONObject);
		 
		 this.teamOrgid = aJSONObject.optString("mem_team_orgid");
		 this.pid = aJSONObject.optString("mem_mpid");
		 this.teamName = aJSONObject.optString("mem_team_name");
		 this.accessToken = aJSONObject.optString("access_token");
		 this.accountStatus = aJSONObject.optString("mem_account_status");
		 this.memid = aJSONObject.optString("memid");
		 
	 }
	
}








