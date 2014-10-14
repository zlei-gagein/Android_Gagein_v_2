package com.gagein.model;

import org.json.JSONObject;

public class AutoLoginInfo extends DataModel {

	
	public String accessToken = "";
	public String memberID = "";
	public String memberEmail = "";
	public String memberFullName = "";
	public String memberTimeZone = "";
	public int signupProcessStatus = 0;
	
	
	
	@Override
	public void parseData(JSONObject aJSONObject) {
		if (aJSONObject == null) return;
		super.parseData(aJSONObject);
		
		memberID = aJSONObject.optString("memid");
		accessToken = aJSONObject.optString("access_token");
		memberEmail = aJSONObject.optString("mem_email");
		memberFullName = aJSONObject.optString("mem_full_name");
		memberTimeZone = aJSONObject.optString("mem_timezone");
		signupProcessStatus = aJSONObject.optInt("signup_process_status");
	}
	
	
	public static AutoLoginInfo loginInfoFromMember(Member aMember) {
		if (aMember == null) return null;
		
		AutoLoginInfo info = new AutoLoginInfo();
		info.accessToken = aMember.accessToken;
		info.memberID = aMember.memid;
		info.memberEmail = aMember.accountEmail;
		info.memberFullName = aMember.fullName;
		info.signupProcessStatus = aMember.signupProcessStatus;

		return info;
	}
	
}

/**

 */
