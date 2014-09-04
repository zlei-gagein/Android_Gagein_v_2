package com.gagein.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class UserProfile extends DataModel implements Serializable {
	
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 8861698093994996412L;
	
	public String firstName = null;
	public String lastName = null;
	public String email = null;
	
	public long orgID = 0;
	public String orgTitle = null;
	public String orgName = null;
	public String orgWebsite = null;
	public String orgLogoPath = null;
	
	public String planName = null;
	public long planID = 0;
	
	public String timezone = null;
	public String timezoneGMT = null;
	public String timezoneName = null;
	
	List<String> schools = null;
	List<Company> prevCompanies = null;
	
	
	
	@Override
	public void parseData(JSONObject aJSONObject) {
		if (aJSONObject == null) return;
		super.parseData(aJSONObject);
		
		firstName = aJSONObject.optString("mem_first_name");
		lastName = aJSONObject.optString("mem_last_name");
		email = aJSONObject.optString("mem_email");
		timezone = aJSONObject.optString("mem_add_timezone");
		orgTitle = aJSONObject.optString("mem_org_title");
		orgID = aJSONObject.optLong("orgid");
		orgName = aJSONObject.optString("org_name");
		orgWebsite = aJSONObject.optString("org_website");
		orgLogoPath = aJSONObject.optString("org_logo_path");
		planID = aJSONObject.optLong("plan_id");
		planName = aJSONObject.optString("plan_name");
		timezoneGMT = aJSONObject.optString("timezone_gmtnum");
		timezoneName = aJSONObject.optString("timezone_name");
		
		// schools
		JSONArray schoolsData = aJSONObject.optJSONArray("schools");
		int length = (schoolsData != null) ? schoolsData.length() : -1;
		if (length > 0) {
			schools = new ArrayList<String>();
			for (int i = 0; i < length; i++) {
				JSONObject schoolObj = schoolsData.optJSONObject(i);
				String schoolName = null;
				if (schoolObj != null && (schoolName = schoolObj.optString("name")) != null) {
					schools.add(schoolName);
				}
			}
		}
		
		// previous companies
		JSONArray prevComData = aJSONObject.optJSONArray("prev_companies");
		length = (prevComData != null) ? prevComData.length() : -1;
		if (length > 0) {
			prevCompanies = new ArrayList<Company>();
			for (int i = 0; i < length; i++) {
				JSONObject comObj = prevComData.optJSONObject(i);
				Company company = new Company();
				company.parseData(comObj);
				prevCompanies.add(company);
			}
		}
	}
	
	
	public String fullName() {
		if (firstName != null && lastName != null) {
			return firstName + " " + lastName;
		} else if (firstName != null) {
			return firstName;
		} else if (lastName != null) {
			return lastName;
		}
		
		return "";
	}
	
	/**set company data to profile*/
	public void setCompany(Company aCompany) {
		if (aCompany != null) {
			orgID = aCompany.validID();
			orgName = aCompany.name;
			orgWebsite = aCompany.website;
			orgLogoPath = aCompany.logoPath;
		}
	}
}

