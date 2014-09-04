package com.gagein.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class Happening extends DataModel{

	/** ---- constant strings ----- */
	public static final String CHANGE_TYPE_JOIN = "JOIN";
	public static final String CHANGE_TYPE_LEFT = "LEAVE";

	/***************** company html template *********************/
	// d) Employees Join Another Company (<Contact Name> <Old title> at <Old
	// Company> has joined <Company> as <Title>)
	public static final String EVENT_MSG_COM_PERSON_JONIED = "%@ %@ at %@ has joined %@ as %@";
	// <Contact Name> has joined <Company> as <Title>
	public static final String EVENT_MSG_COM_PERSON_JONIED_1ST_JOB = "%@ has joined %@ as %@";
	// <contact name> has joined <company name> as <job title>
	public static final String EVENT_MSG_COM_PERSON_JONIED_OLD = "%@ has joined %@ as %@";
	// e) Employees Left Company (<Contact Name> <Old title>, has left <Old
	// Company> and is now <Title> at <Company>)
	public static final String EVENT_MSG_COM_PERSON_LEFT = "%@ %@, has left %@ and is now %@ at %@";
	// <contact name>, <job title>, has left <company name>
	public static final String EVENT_MSG_COM_PERSON_LEFT_OLD = "%@, as %@, has left %@";
	// <company name>'s [annual / quarterly] revenue has increased xx.xx%
	public static final String EVENT_MSG_COM_REVENUE_INCREASED_OLD = "%@'s %@ revenue has increased %.2f%%";
	// <company name>'s [annual / quarterly] revenue has decreased xx.xx%
	public static final String EVENT_MSG_COM_REVENUE_DECREASED_OLD = "%@'s %@ revenue has decreased %.2f%%";
	// <company name> closed <funding amount> in <round> funding
	public static final String EVENT_MSG_COM_FUNDING_CLOSED_OLD = "%@ closed %@ in %@ funding";
	// <company name> has a new address: <address>
	public static final String EVENT_MSG_COM_ADDRESS_CHANGED_OLD = "%@ has a new address: %@";
	// <contact name>, <old job title>, is now <new job title> at <company name>
	public static final String EVENT_MSG_COM_PERSON_TITLE_CHANGED_OLD = "%@, %@, is now %@ at %@";
	// The employee size at <company name> has increased to <number>
	public static final String EMPLOYEE_SIZE_INCREASED_OLD = "The employee size at %@ has increased to %@ ";
	// The employee size at <company name> has decreased to <number>
	public static final String EMPLOYEE_SIZE_DECREASED_OLD = "The employee size at %@ has decreased to %@ ";
	// ////////////////// OLD: contact html template /////////////////////////
	// <contact name> has an updated profile picture on Linkedin
	public static final String EVENT_MSG_CON_HAS_UPDATED_PROFILE_PICTURE_OLD = "%@ has an updated profile picture on LinkedIn";
	// <contact name>,has joined another company:<company name>
	public static final String EVENT_MSG_CON_JOIN_ANOTHER_COMPANY_OLD = "%@ has joined another company: %@";
	// <contact name> has a new location:<location name>
	public static final String EVENT_MSG_CON_NEW_LOCATION_OLD = "%@ has a new location: %@";
	// <contact name> has a new job title:<job title>
	public static final String EVENT_MSG_CON_NEW_JOB_TITLE_OLD = "%@ has a new job title: %@";

	/***************** NEW: contact html template *********************/
	// <Contact Name>, <Title> at <Company Name>, has an updated profile picture
	// on <Source>
	public static final String EVENT_MSG_CON_HAS_UPDATED_PROFILE_PICTURE = "%@, %@ at %@ has an updated profile picture on %@";
	// (<Contact Name>, <Old Title> at <Old Company Name>, has joined <Company
	// Name> as <Title>)
	public static final String EVENT_MSG_CON_JOIN_ANOTHER_COMPANY = "%@, %@ at %@, has joined %@ as %@";
	// Location Changes (<Contact Name>, <Title> at <Company Name>, has moved
	// from <Old Address> to <Address>.)
	public static final String EVENT_MSG_CON_NEW_LOCATION = "%@, %@ at %@, has moved from %@ to %@";
	// d) Job Title Changes (<Contact Name>, <Old Title>, is now <Title> at
	// <Company Name>.)
	public static final String EVENT_MSG_CON_NEW_JOB_TITLE = "%@, %@, is now %@ at %@";

	/** ---- constants ----- */
	// company happening type
	public static final int kTypeCompanyPersonJoin = 2001;
	public static final int kTypeCompanyPersonJoinDetail = 2002;
	public static final int kTypeCompanyRevenueChange = 2008;
	public static final int kTypeCompanyNewFunding = 2004;
	public static final int kTypeCompanyNewLocation = 2005;
	public static final int kTypeCompanyEmloyeeSizeIncrease = 2006;
	public static final int kTypeCompanyEmloyeeSizeDecrease = 2007;
	// person happening type
	public static final int kTypePersonUpdateProfilePic = 1001; // person change
																// picture
	public static final int kTypePersonJoinOtherCompany = 1002; // person change
																// job
	public static final int kTypePersonNewLocation = 1003; // person change
															// location
	public static final int kTypePersonNewJobTitle = 1004; // person change job
															// title

	// happening source
	public static final int kSourceLindedIn = 2002;
	public static final int kSourceCrunchBase = 3001;
	public static final int kSourceYahoo = 3002;
	public static final int kSourceHoovers = 2006;
	public static final int kSourceFacebook = 10000;
	public static final int kSourceTwitter = 10001;
	public static final int kSourceYoutube = 10002;
	public static final int kSourceSlideShare = 10003;
	public static final int kSourceUnKnown = 99999;

	/** ---- member variables ----- */
	public long timestamp = 0;
	public long newTimestamp = 0;
	public long oldTimestamp = 0;
	public long fundingTimestamp = 0;
	public long protocol = 0;
	public long contactID = 0;

	public String dateStr = null;
	public String name = null;
	public String title = null;
	public String jobTitle = null;
	public String oldJobTitle = null;
	public String newJobTitle = null; // newJobTile, but cocoa used 'new-x'
										// prefix
	public String oldRevenue = null;
	public String newRevenue = null;
	public String percentage = null;
	public String period = null;
	public String revenueChart = null;

	public String funding = null;
	public String round = null;
	public String oldEmployNum = null;
	public String employNum = null;
	public String direction = null;
	public String address = null;
	public String addressCompany = null;
	public String addressCompanyOld = null;
	public String addressPerson = null;

	public String addressPersonOld = null;
	public String addressMap = null;
	public String oldAddressMap = null;
	public String photoPath = null;
	public String profilePic = null;
	public String oldProfilePic = null;
	public String change = null; // e.g. LEAVE
	public String sourceName = null;
	public String messageStr = null; // only if param 'msg_format=text or html',
										// this data is provided
	public String orgID = null;
	public String orgName = null;
	public String orgLogoPath = null;
	public String msgSubject = null;

	public Company company = null;
	public Company oldCompany = null;
	public Company contactCurrentCompany = null;

	public int type = kTypeCompanyPersonJoin;
	public int source = kSourceUnKnown;
	public Boolean hasBeenRead = false;
	public long eventId; 
	
	public List<RevenuePlot> revenues = null;

	// -------- class RevenuePlot begin ----------
	public class RevenuePlot extends DataModel {
		public long period = 0;
		public double revenue = 0;

		@Override
		public void parseData(JSONObject aJSONObject) {
			if (aJSONObject == null)
				return;
			super.parseData(aJSONObject);

			period = ((JSONObject) (aJSONObject.optJSONObject("period")))
					.optLong("timestamp");
			revenue = aJSONObject.optDouble("revenue");
		}
	}

	// -------- class RevenuePlot end ----------

	public HappeningPerson person = null;

	// ------ class HappeningPerson begin-----------------------
	public class HappeningPerson extends Person {

		private static final long serialVersionUID = 1L;
		public String profile = null;
		public String contactName = null;
		public String orgName = null;
		public String oldPhotoPath = null; // from oldProfilePic

		public long contactID = 0;
		public long orgID = 0;

		@Override
		public void parseData(JSONObject aJSONObject) {
			if (aJSONObject == null)
				return;
			super.parseData(aJSONObject);

			id = aJSONObject.optLong("id");
			this.profile = aJSONObject.optString("profile");
			this.contactID = aJSONObject.optLong("contactid");
			this.orgID = aJSONObject.optLong("orgid");
			this.orgName = aJSONObject.optString("org_name");
			this.contactName = aJSONObject.optString("contact_name");
		}
	}

	// ------ class HappeningPerson end -----------------------

	/** ----- methods ----- */
	@Override
	public void parseData(JSONObject aJSONObject) {
		if (aJSONObject == null)
			return;
		id = aJSONObject.optLong("id");
		this.protocol = aJSONObject.optLong("protocol"); // IMPORTANT: parse this
													// first

		this.type = aJSONObject.optInt("type");
		this.change = aJSONObject.optString("change");
		this.timestamp = ModelHelper.optLongFromJSONObject(aJSONObject, "timestamp",
				"timestamp");
		this.dateStr = aJSONObject.optString("date_str");
		this.contactID = aJSONObject.optLong("contactid");
		this.name = aJSONObject.optString("name");
		this.photoPath = aJSONObject.optString("photo_path");
		this.eventId = aJSONObject.optLong("eventid");
		this.source = aJSONObject.optInt("source");
		this.sourceName = aJSONObject.optString("source_name");
		this.messageStr = aJSONObject.optString("msg_str");

		this.orgID = aJSONObject.optString("orgid");
		this.orgName = aJSONObject.optString("org_name");
		this.orgLogoPath = aJSONObject.optString("org_logo_path");

		// person
		person = new HappeningPerson();
		person.parseData(aJSONObject.optJSONObject("person"));

		// company
		company = new Company();
		company.parseData(aJSONObject.optJSONObject("company"));

		// contact company
		contactCurrentCompany = new Company();
		contactCurrentCompany.parseData(aJSONObject
				.optJSONObject("contact_current_company"));

		this.title = ModelHelper.optStringFromJSONObject(aJSONObject, "title",
				"title");
		this.newJobTitle = ModelHelper.optStringFromJSONObject(aJSONObject,
				"newjobtitle", "title");
		this.address = ModelHelper.optStringFromJSONObject(aJSONObject, "address",
				"address");

		this.addressMap = aJSONObject.optString("address_map");
		this.oldAddressMap = aJSONObject.optString("old_address_map");

		this.percentage = aJSONObject.optString("percentage");
		this.period = aJSONObject.optString("period");
		this.revenueChart = aJSONObject.optString("revenue_chart");

		this.funding = aJSONObject.optString("funding");
		this.round = aJSONObject.optString("round");
		this.msgSubject = aJSONObject.optString("msg_subject");
		this.direction = aJSONObject.optString("direction");

		if (isOldProtocol()) {
			parseOldProtocol(aJSONObject);
		} else {
			parseNewProtocol(aJSONObject);
		}

	}

	private Boolean isOldProtocol() {
		return protocol != 2;
	}

	public Boolean isPersonEvent() {
		return isPersonEvent(type);
	}

	/**
	 * 判断是否为person event
	 * 
	 * @param aEventType
	 * @return
	 */
	public static Boolean isPersonEvent(int aEventType) {
		return aEventType >= kTypePersonUpdateProfilePic
				&& aEventType <= kTypePersonNewJobTitle;
	}

	private void parseOldProtocol(JSONObject aJSONObject) {
		oldCompany = new Company();
		oldCompany.parseData(aJSONObject.optJSONObject("oldCompany"));

		jobTitle = ModelHelper.optStringFromJSONObject(aJSONObject, "jobtitle",
				"title");
		oldJobTitle = ModelHelper.optStringFromJSONObject(aJSONObject,
				"oldjobtitle", "title");

		newTimestamp = ModelHelper.optLongFromJSONObject(aJSONObject,
				"newTimestamp", "timestamp");
		oldTimestamp = ModelHelper.optLongFromJSONObject(aJSONObject,
				"oldTimestamp", "timestamp");
		fundingTimestamp = ModelHelper.optLongFromJSONObject(aJSONObject,
				"fundingTimestamp", "timestamp");

		oldRevenue = aJSONObject.optString("oldRevenue");
		newRevenue = aJSONObject.optString("newRevenue");
		oldEmployNum = aJSONObject.optString("oldEmployNum");
		employNum = aJSONObject.optString("employNum");

		profilePic = aJSONObject.optString("profilepic");
		oldProfilePic = aJSONObject.optString("oldProfilepic");
		if (person != null)
			person.oldPhotoPath = oldProfilePic;
	}

	private void parseNewProtocol(JSONObject aJSONObject) {
		oldCompany = new Company();
		oldCompany.parseData(aJSONObject.optJSONObject("old_company"));

		jobTitle = ModelHelper.optStringFromJSONObject(aJSONObject,
				"job_title", "title");
		oldJobTitle = ModelHelper.optStringFromJSONObject(aJSONObject,
				"old_job_title", "title");

		newTimestamp = ModelHelper.optLongFromJSONObject(aJSONObject,
				"new_timestamp", "timestamp");
		oldTimestamp = ModelHelper.optLongFromJSONObject(aJSONObject,
				"old_timestamp", "timestamp");
		fundingTimestamp = ModelHelper.optLongFromJSONObject(aJSONObject,
				"funding_timestamp", "timestamp");

		addressCompany = ModelHelper.optStringFromJSONObject(aJSONObject,
				"company_address", "address");
		addressCompanyOld = ModelHelper.optStringFromJSONObject(aJSONObject,
				"old_company_address", "address");
		addressPerson = ModelHelper.optStringFromJSONObject(aJSONObject,
				"person_address", "address");
		addressPersonOld = ModelHelper.optStringFromJSONObject(aJSONObject,
				"old_person_address", "address");

		oldRevenue = aJSONObject.optString("old_revenue");
		newRevenue = aJSONObject.optString("new_revenue");
		oldEmployNum = aJSONObject.optString("old_employ_num");
		employNum = aJSONObject.optString("employ_num");

		profilePic = aJSONObject.optString("profile_pic");
		oldProfilePic = aJSONObject.optString("old_profile_pic");
		if (person != null)
			person.oldPhotoPath = oldProfilePic;

		// revenues
		JSONArray revenuesData = aJSONObject.optJSONArray("revenues");
		int length = (revenuesData != null) ? revenuesData.length() : -1;
		if (length > 0) {
			revenues = new ArrayList<Happening.RevenuePlot>();
			for (int i = 0; i < length; i++) {
				RevenuePlot revenuePlot = new RevenuePlot();
				revenuePlot.parseData(revenuesData.optJSONObject(i));
				revenues.add(revenuePlot);
			}
		}
	}
}

/**


*/
