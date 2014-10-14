package com.gagein.model;

import org.json.JSONObject;

public class Member extends Person {

	private static final long serialVersionUID = 1L;
	/** ----- constants ----- */
	public static final byte kGGSignupProcessAgentsSelect = 1;
	public static final byte kGGSignupProcessAreasSelect = 2;
	public static final byte kGGSignupProcessOK = 3;
	public static final byte kGGSignupProcessOK2 = 4;

	/** ----- member variables ----- */
	public String timeZone = null;
	public String accessToken = null;
	public int signupProcessStatus = kGGSignupProcessAgentsSelect;
	public String accountEmail = null;
	public String accountPassword = null;
	public String memid = null;
	public MemberPlan plan = null;

	/** ----- methods ----- */
	 @Override
	public void parseData(JSONObject aJSONObject) {
		if (aJSONObject == null)
			return;
		super.parseData(aJSONObject);

		// id = aJSONObject.optLong("memid");
		this.accessToken = aJSONObject.optString("access_token");
		this.fullName = aJSONObject.optString("mem_full_name");
		this.timeZone = aJSONObject.optString("mem_timezone");
		this.signupProcessStatus = aJSONObject.optInt("signup_process_status");
		this.memid = aJSONObject.optString("memid");

		if (company == null)
			company = new Company();
		this.company.name = aJSONObject.optString("mem_orgname");
	}

	/**
	 * is the user sign up process completed
	 * 
	 * @return
	 */
	public Boolean isSignupOK() {
		return signupProcessStatus == kGGSignupProcessOK
				|| signupProcessStatus == kGGSignupProcessOK2;
	}

	/**
	 * create a member from login info
	 * 
	 * @param aLoginInfo
	 * @return
	 */
	public static Member memberFromLoginInfo(AutoLoginInfo aLoginInfo) {
		if (aLoginInfo == null) return null;
		
		Member member = new Member();
		member.accessToken = aLoginInfo.accessToken;
		member.memid = aLoginInfo.memberID;
		member.accountEmail = aLoginInfo.memberEmail;
		member.fullName = aLoginInfo.memberFullName;
		member.signupProcessStatus = aLoginInfo.signupProcessStatus;

		return member;
	}
}

/**

 
 */
