package com.gagein.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;

import com.gagein.util.Log;
//import org.json.JSONException;

public class Company extends DataModel implements Serializable, Comparable<Company>{

	private static final long serialVersionUID = 1L;
	/** -------- constants ------- */
	// company ownership
	public static final byte kOwnerShipPublic = 0;
	public static final byte kOwnerShipPrivate = 1;
	public static final byte kOwnerShipGovernment = 2;
	public static final byte kOwnerShipSubsidary = 3;
	public static final byte kOwnerShipOther = 4;

	// company status
	public static final byte kStatusNormal = 0; // The company is normal
	public static final byte kStatusClosed = 1; // * The company has been
												// closed.
	public static final byte kStatusAquired = 2; // * The company has been
													// ACQUIRED
	public static final byte kStatusOnlyHasName = 3; // * The company only has
														// the name.
	public static final byte kStatusDelete = 4; // * The company has been
												// removed from gagein database.

	/** -------- member methods ------- */
	public boolean enabled = false;
	public boolean followed = false;
	public long orgID = 0;
	public long relevancePersonID = 0; // for sales graph display

	public String name = "";
	public String profile = "";
	public String orgName = "";
	public String website = "";
	public String logoPath = "";
	public String grade = "";
	public String type = ""; // eg. "Private Company"
	public String ownership = "";
	public String fortuneRank = "";
	public String revenueSize = "";
	public String employeeSize = "";
	public String country = "";
	public String state = "";
	public String city = "";
	public String zipcode = "";
	public String address = "";
	public String orgEmail = "";
	public String linkedInSearchUrl = "";
	public String orgLargerLogoPath = ""; // org_larger_logo_path
	public String faxNumber = "";
	public String fiscalYear = "";
	public String founded = "";
	public String googleMapUrl = "";
	public String industries = "";
	public String description = "";
	public String nickName = "";
	public String shortName = "";
	public String specialities = "";
	public String telephone = "";
	public String aliases = "";
	public String keywords = "";
	public String latestDate = "";
	public String revenuesChartUrl = "";
	public String sourceText = "";
	public Boolean select = false;
	public String inc5000_rank;
	public String global_rank;
	public String russell_rank;

	public DataPage competitors;
	public DataPage emplorees;
	public Company parent;
	public List<Company> parents = new ArrayList<Company>();
	public String lastUpdateDateStr = ""; // "latest_update_date_str":"July 2, 2013"
	public int orgStatus = kStatusNormal;
	public Company relatedCompany;

	public List<SocialProfile> socialProfiles = new ArrayList<SocialProfile>();
	public List<Ticker> tickerSymbols = new ArrayList<Ticker>();
	public List<Company> divisions = new ArrayList<Company>();
	public List<Company> subsidiaries = new ArrayList<Company>();
	public List<Company> joinVentures = new ArrayList<Company>();
	public Boolean hasFollowFiled;
	public String websiteKey;
	public List<String> nameList = new ArrayList<String>();
	public Boolean linked = false;
	
	public List<String> getNameList() {
		return nameList;
	}

	public void setNameList(List<String> nameList) {
		this.nameList = nameList;
	}

	/** -------- methods ------- */
	@SuppressLint("DefaultLocale")
	@Override
	public void parseData(JSONObject aJSONObject) {
		if (aJSONObject == null)
			return;

		id = aJSONObject.optLong("id");
		orgID = aJSONObject.optLong("orgid");
		
		this.name = aJSONObject.optString("name").trim().equals("") ? aJSONObject.optString("org_name"): aJSONObject.optString("name");
		this.orgName = aJSONObject.optString("org_name");

		this.enabled = aJSONObject.optBoolean("enabled");
		if (aJSONObject.has("followed")) {
			this.followed = (aJSONObject.optInt("followed") == 1 ? true : false);
			String follow = aJSONObject.optString("followed").toLowerCase();
			if (null != follow) {
				if (follow.equalsIgnoreCase("true")) {
					this.followed = true;
				} else if (follow.equalsIgnoreCase("false")) {
					this.followed = false;
				}
			}
			this.hasFollowFiled = true;
		} else {
			this.hasFollowFiled = false;
		}

		this.type = aJSONObject.optString("type");
		this.website = aJSONObject.optString("org_website");
		this.logoPath = aJSONObject.optString("org_logo_path");
		this.logoPath = logoPath.replace("/s60", "/s200");
		this.employeeSize = aJSONObject.optString("employee_size");
		
		this.fortuneRank = aJSONObject.optString("fortune_rank");
		this.inc5000_rank = aJSONObject.optString("inc5000_rank");
		this.global_rank = aJSONObject.optString("global_rank");
		this.russell_rank = aJSONObject.optString("russell_rank");
		
		this.ownership = aJSONObject.optString("ownership");
		this.revenueSize = aJSONObject.optString("revenue_size");
		this.country = aJSONObject.optString("country");
		this.state = aJSONObject.optString("state");
		this.city = aJSONObject.optString("city");
		this.zipcode = aJSONObject.optString("zipcode");
		this.address = aJSONObject.optString("address");
		this.profile = aJSONObject.optString("profile");
		this.grade = aJSONObject.optString("grade");
		this.orgEmail = aJSONObject.optString("org_email");
		this.linkedInSearchUrl = aJSONObject.optString("linkedin_search_url");
		this.orgLargerLogoPath = aJSONObject.optString("org_larger_logo_path");
		this.linked = aJSONObject.optInt("linked") == 1 ? true : false;

		// competitors
		JSONObject competitorsData = aJSONObject.optJSONObject("competitors");
		if (competitorsData != null) {
			competitors = new DataPage();
			competitors.hasMore = (competitorsData.optInt("hasMore") == 1 ? true : false);
			JSONArray infoArray = competitorsData.optJSONArray("info");
			int length = (infoArray != null) ? infoArray.length() : -1;
			if (length > 0)
				competitors.items = new ArrayList<Object>();

			for (int i = 0; i < length; i++) {
				JSONObject comData = infoArray.optJSONObject(i);
				if (comData != null) {
					Company competitor = new Company();
					competitor.parseData(comData);
					competitors.items.add(competitor);
				}
			}
		}

		// employees
		JSONObject employeesData = aJSONObject.optJSONObject("contacts");
		if (employeesData != null) {
			emplorees = new DataPage();

			if (null != employeesData) {
				emplorees.hasMore = (employeesData.optInt("hasMore") == 1 ? true : false);
				JSONArray infoArray = employeesData.optJSONArray("info");
				int length = (infoArray != null) ? infoArray.length() : -1;
				if (length > 0)
					emplorees.items = new ArrayList<Object>();
				
				for (int i = 0; i < length; i++) {
					JSONObject personData = infoArray.optJSONObject(i);
					if (personData != null) {
						Person person = new Person();
						person.parseData(personData);
						emplorees.items.add(person);
					}
				}
			}
		}

		// parent company
		JSONObject parentData = aJSONObject.optJSONObject("parent");
		if (parentData != null) {
			parent = new Company();
			parent.parseData(parentData);
		}

		this.lastUpdateDateStr = aJSONObject.optString("latest_update_date_str");
		this.aliases = aJSONObject.optString("org_aliases");
		this.keywords = aJSONObject.optString("org_keywords");
		this.description = aJSONObject.optString("org_description");
		this.specialities = aJSONObject.optString("specialties");
		this.industries = aJSONObject.optString("industries");
		this.telephone = aJSONObject.optString("telephone");
		this.faxNumber = aJSONObject.optString("fax_number");
		this.profile = aJSONObject.optString("profile");
		this.fiscalYear = aJSONObject.optString("fiscal_year");
		this.founded = aJSONObject.optString("founded");
		this.googleMapUrl = aJSONObject.optString("google_map_url");
		this.latestDate = aJSONObject.optString("latest_date");
		this.revenuesChartUrl = aJSONObject.optString("revenues_chart_url");
		this.sourceText = aJSONObject.optString("source_text");
		this.orgStatus = aJSONObject.optInt("org_status");
		this.websiteKey = aJSONObject.optString("websiteKey");
		
		// related company
		JSONObject relatedComData = aJSONObject
				.optJSONObject("related_company");
		if (relatedComData != null) {
			relatedCompany = new Company();
			relatedCompany.parseData(relatedComData);
		}

		// social profiles
		JSONArray snProfilesData = aJSONObject.optJSONArray("social_profiles");
		int length = (snProfilesData != null) ? snProfilesData.length() : -1;
		if (length > 0) {
			socialProfiles = new ArrayList<SocialProfile>();
			for (int i = 0; i < length; i++) {
				JSONObject snProfileData = snProfilesData.optJSONObject(i);
				if (snProfileData != null) {
					SocialProfile snProfile = new SocialProfile().parseData(snProfileData);
					socialProfiles.add(snProfile);
				}
			}
		}

		// ticker symbols
		JSONArray symbolsData = aJSONObject.optJSONArray("ticker_symbol");
		length = (symbolsData != null) ? symbolsData.length() : -1;
		if (length > 0) {
			tickerSymbols = new ArrayList<Ticker>();
			for (int i = 0; i < length; i++) {
				JSONObject tickerData = symbolsData.optJSONObject(i);
				if (tickerData != null) {
					Ticker ticker = new Ticker();
					ticker.parseData(tickerData);
					tickerSymbols.add(ticker);
				}
			}
		}

		// divisions
		JSONArray divisionsData = aJSONObject.optJSONArray("divisions");
		length = (divisionsData != null) ? divisionsData.length() : -1;
		if (length > 0) {
			divisions.clear();
			for (int i = 0; i < length; i++) {
				JSONObject divisionData = divisionsData.optJSONObject(i);
				if (divisionData != null) {
					Company division = new Company();
					division.parseData(divisionData);
					divisions.add(division);
				}
			}
		}
		
		// joinVentures
		JSONArray joinVenturesData = aJSONObject.optJSONArray("join_ventures");
		length = (joinVenturesData != null) ? joinVenturesData.length() : -1;
		if (length > 0) {
			joinVentures.clear();
			for (int i = 0; i < length; i++) {
				JSONObject joinVentureData = joinVenturesData.optJSONObject(i);
				if (joinVentureData != null) {
					Company joinVenture = new Company();
					joinVenture.parseData(joinVentureData);
					joinVentures.add(joinVenture);
				}
			}
		}

		// subsidiaries
		JSONArray subsidiariesData = aJSONObject.optJSONArray("subsidiaries");
		length = (subsidiariesData != null) ? subsidiariesData.length() : -1;
		if (length > 0) {
			subsidiaries.clear();
			for (int i = 0; i < length; i++) {
				JSONObject subsidiaryData = subsidiariesData.optJSONObject(i);
				String name = subsidiaryData.optString("name").trim().equals("") ? subsidiaryData.optString("org_name"): subsidiaryData.optString("name");
				Log.v("silen", "name = " + name);
				if (subsidiaryData != null) {
					Company subsidiary = new Company();
					subsidiary.parseData(subsidiaryData);
					subsidiaries.add(subsidiary);
				}
			}
		}
		
		//parents
		JSONArray parentsArray = aJSONObject.optJSONArray("parents");
		length = (parentsArray != null) ? parentsArray.length() : -1;
		if (length > 0) {
			parents.clear();
			for (int i = 0; i < length; i++) {
				JSONObject parentsObject = parentsArray.optJSONObject(i);
				if (parentsObject != null) {
					Company parent = new Company();
					parent.parseData(parentsObject);
					parents.add(parent);
				}
			}
		}
		
		return;
	}

	/**
	 * full address
	 * 
	 * @return
	 */
	public String fullAddress() {
		String fullAddr = "";

		if (address != null && address.length() > 0) {
			fullAddr += address;
		}

		if (city != null && city.length() > 0) {
			if (fullAddr.length() > 0)
				fullAddr += ", ";
			fullAddr += city;
		}

		if (state != null && state.length() > 0) {
			if (fullAddr.length() > 0)
				fullAddr += ", ";
			fullAddr += state;
		}

		if (country != null && country.length() > 0) {
			if (fullAddr.length() > 0)
				fullAddr += ", ";
			fullAddr += country;
		}

		return fullAddr;
	}
	
	/**
	 * address
	 * 
	 * @return
	 */
	public String address() {
		String fullAddr = "";

		if (city != null && city.length() > 0) {
			if (fullAddr.length() > 0)
				fullAddr += ", ";
			fullAddr += city;
		}

		if (state != null && state.length() > 0) {
			if (fullAddr.length() > 0)
				fullAddr += ", ";
			fullAddr += state;
		}

		if (country != null && country.length() > 0) {
			if (fullAddr.length() > 0)
				fullAddr += ", ";
			fullAddr += country;
		}

		return fullAddr;
	}

	/**
	 * ownership type
	 * 
	 * @return
	 */
	public byte ownershipType() {
		if (ownership.equalsIgnoreCase("public")) {
			return kOwnerShipPublic;
		} else if (ownership.equalsIgnoreCase("private")) {
			return kOwnerShipPrivate;
		} else if (ownership.equalsIgnoreCase("subsidiary")) {
			return kOwnerShipSubsidary;
		} else if (ownership.equalsIgnoreCase("government-owned corporation")) {
			return kOwnerShipGovernment;
		}

		return kOwnerShipOther;
	}
	
	public long validID() {
		if (id != 0) {
			return id;
		}
		
		return orgID;
	}
	
	public boolean theSameAs(Company aCompany) {
		if (aCompany == null) return false;
		
		return aCompany.validID() != 0 && aCompany.validID() == validID();
	}
	
	
	public Data getData() {
		Data data = new Data();
		data.id = id;
		data.orgID = orgID;
		data.name = name;
		data.website = website;
		data.followed = followed;
		return data;
	}
	
	public class Data implements Serializable {
		
		private static final long serialVersionUID = 1L;
		
		public long id;
		public long orgID;
		public String name;
		public String website;
		public boolean followed;
		
		public Company company() {
			Company company = new Company();
			company.id = id;
			company.orgID = orgID;
			company.name = name;
			company.website = website;
			company.followed = followed;
			return company;
		}
	}

	@Override
	public int compareTo(Company another) {
		return Comparators.NAME.compare(this, another);
	}
	
	public static class Comparators {
		public static Comparator<Company> NAME = new Comparator<Company>() {
			
			@Override
			public int compare(Company lhs, Company rhs) {
				return lhs.name.trim().toUpperCase().compareTo(rhs.name.trim().toUpperCase());
			}
		};
	}
}

