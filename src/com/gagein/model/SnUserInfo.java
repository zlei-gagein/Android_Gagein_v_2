package com.gagein.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class SnUserInfo extends DataModel implements Serializable {

	/**
	 * Serializable
	 */
	private static final long serialVersionUID = -1594851623134189200L;
	
	public String token = null;
	public String secret = null;
	public String refreshToken = null;
	public String instanceUrl = null;
	public String firstName = null;
	public String lastName = null;
	public String email = null;
	public String accountID = null;
	public String accountName = null;
	public String profileURL = null;
	public Boolean emailExisted = null;
	public List<AutoLoginInfo> autoLoginInfos = null;
	public int snType = ModelHelper.kGGSnTypeLinkedIn;

	@Override
	public void parseData(JSONObject aJSONObject) {
		if (aJSONObject == null) return;
		super.parseData(aJSONObject);
		
		token = aJSONObject.optString("sn_token");
		secret = aJSONObject.optString("sn_secret");
		refreshToken = aJSONObject.optString("sn_refresh_token");
		instanceUrl = aJSONObject.optString("sn_instance_url");
		firstName = aJSONObject.optString("sn_first_name");
		lastName = aJSONObject.optString("sn_last_name");
		email = aJSONObject.optString("sn_email");
		accountID = aJSONObject.optString("sn_account_id");
		accountName = aJSONObject.optString("sn_account_name");
		profileURL = aJSONObject.optString("sn_profile_url");
		emailExisted = aJSONObject.optBoolean("email_existed");
		
		// auto login infos
		JSONArray autoLoginInfosData = aJSONObject.optJSONArray("auto_login_info");
		int length = (autoLoginInfosData != null) ? autoLoginInfosData.length() : -1;
		if (length > 0) {
			autoLoginInfos = new ArrayList<AutoLoginInfo>();
			for (int i = 0; i < length; i ++) {
				JSONObject autoLoginData = autoLoginInfosData.optJSONObject(i);
				AutoLoginInfo autoLoginInfo = new AutoLoginInfo();
				autoLoginInfo.parseData(autoLoginData);
				autoLoginInfos.add(autoLoginInfo);
			}
		}
	}
}

