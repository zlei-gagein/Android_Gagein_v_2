package com.gagein.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class SearchPerson extends DataModel implements Serializable{
	private static final long serialVersionUID = 1L;
	// constants
	public static final byte kActionUnknown = 0;
	public static final byte kActionMessage = 1;
	public static final byte kActionConnect = 2;
	public static final byte kActionSearch = 3;
	public static final byte kActionInMail = 4;

	// member variables
	public String name = null;
	public String firstName = null;
	public String lastName = null;
	
	public String fullName = null;
	public String orgTitle = null;
	public String jobLevel = null;
	public String photoPath = null;
	public String linkedInID = null;
	public String actionType = null;
	public String address = null;
	public String summary = null;

	public String actionID = null; // "id" from "action_data"
	public String actionURL = null; // "url" from "action_data"

	public Company company = null;
	public List<SocialProfile> socialProfiles = null;
	public List<String> schools = null;
	public List<Company> prevCompanies = null;
	public Boolean followed = false;

	// methods
	 @Override
	public void parseData(JSONObject aJSONObject) {
		if (aJSONObject == null) return;

		id = aJSONObject.optLong("contactid");
		this.name = aJSONObject.optString("org_name");
		this.firstName = aJSONObject.optString("dop_first_name");
		this.lastName = aJSONObject.optString("dop_last_name");
		this.fullName = firstName + "" + lastName;
		this.orgTitle = aJSONObject.optString("org_title");
		this.jobLevel = aJSONObject.optString("job_level");
		this.linkedInID = aJSONObject.optString("linkedin_id");
		this.photoPath = aJSONObject.optString("photo_path");
		this.actionType = aJSONObject.optString("action_type");
		this.followed = aJSONObject.optBoolean("followed");
		this.summary = aJSONObject.optString("summary");
		this.address = aJSONObject.optString("address");

		// action data
		JSONObject actionData = (JSONObject) aJSONObject.optJSONObject("action_data");
		if (actionData != null) {
			this.actionID = actionData.optString("id");
			this.actionURL = actionData.optString("url");
		}

		// company
		this.company = new Company();
		// company.id = aJSONObject.optLong("orgid");
		this.company.name = aJSONObject.optString("org_name");

		// social profiles
		JSONArray socialProfilesData = aJSONObject
				.optJSONArray("social_profiles");
		int length = (socialProfilesData != null) ? socialProfilesData.length()
				: -1;
		if (length > 0) {
			socialProfiles = new ArrayList<SocialProfile>();
			for (int i = 0; i < length; i++) {
				JSONObject profileData = socialProfilesData.optJSONObject(i);
				SocialProfile snProfile = new SocialProfile();
				snProfile.parseData(profileData);
				socialProfiles.add(snProfile);
			}
		}

		// schools
		JSONArray schoolsData = aJSONObject.optJSONArray("schools");
		length = (schoolsData != null) ? schoolsData.length() : -1;
		if (length > 0) {
			schools = new ArrayList<String>();
			for (int i = 0; i < length; i++) {
				try {
					JSONObject jObject = schoolsData.optJSONObject(i);
					String schoolStr = jObject.optString("name");
					schools.add(schoolStr);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		}

		// previous companies
		JSONArray prevComsData = aJSONObject.optJSONArray("prev_companies");
		length = (prevComsData != null) ? prevComsData.length() : -1;
		if (length > 0) {
			prevCompanies = new ArrayList<Company>();
			for (int i = 0; i < length; i++) {
				JSONObject prevComData = prevComsData.optJSONObject(i);
				Company prevCom = new Company();
				prevCom.parseData(prevComData);
				prevCompanies.add(prevCom);
			}
		}
	}

	// person action type
	public byte personActionType() {
		if (actionType.equalsIgnoreCase("message")) {
			return kActionMessage;
		} else if (actionType.equalsIgnoreCase("connect")) {
			return kActionConnect;
		} else if (actionType.equalsIgnoreCase("search")) {
			return kActionSearch;
		} else if (actionType.equalsIgnoreCase("inmail")) {
			return kActionInMail;
		}

		return kActionUnknown;
	}

	@Override
	public String toString() {
		return "SearchPerson [name=" + name + ", fullName=" + fullName
				+ ", orgTitle=" + orgTitle + ", jobLevel=" + jobLevel
				+ ", photoPath=" + photoPath + ", linkedInID=" + linkedInID
				+ ", actionType=" + actionType + ", address=" + address
				+ ", summary=" + summary + ", actionID=" + actionID
				+ ", actionURL=" + actionURL + ", company=" + company
				+ ", socialProfiles=" + socialProfiles + ", schools=" + schools
				+ ", prevCompanies=" + prevCompanies + ", followed=" + followed
				+ "]";
	}

	
}
