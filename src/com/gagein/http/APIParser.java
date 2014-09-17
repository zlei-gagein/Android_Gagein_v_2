package com.gagein.http;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.gagein.model.Agent;
import com.gagein.model.CategoryFilter;
import com.gagein.model.Company;
import com.gagein.model.DataModel;
import com.gagein.model.DataPage;
import com.gagein.model.Facet;
import com.gagein.model.FunctionalArea;
import com.gagein.model.Group;
import com.gagein.model.Happening;
import com.gagein.model.MediaFilter;
import com.gagein.model.Member;
import com.gagein.model.MenuData;
import com.gagein.model.Person;
import com.gagein.model.SavedSearch;
import com.gagein.model.SearchPerson;
import com.gagein.model.SnUserInfo;
import com.gagein.model.Update;
import com.gagein.model.UserProfile;
import com.gagein.model.filter.FilterItem;
import com.gagein.model.filter.Filters;
import com.gagein.model.filter.Industry;
import com.gagein.model.filter.Location;
import com.gagein.model.filter.QueryInfo;
import com.gagein.model.filter.QueryInfoItem;
import com.gagein.util.Constant;
import com.gagein.util.Log;
import com.gagein.util.MessageCode;

public class APIParser {
	//private static final String TAG = CommonUtil.tagForClass(APIParser.class);
	private JSONObject responseObject = null;

	public APIParser(JSONObject responseObject) {
		this.responseObject = responseObject;
	}

	/**
	 * API 状态码
	 * 
	 * @return API 状态码
	 * @throws JSONException
	 */
	public int status() {
		if (responseObject != null) {
			return responseObject.optInt("status");
		}
		return APIHttpMetadata.kGGApiStatusNull;
	}

	/**
	 * API message
	 * 
	 * @return API message
	 * @throws JSONException
	 */
	public String message() {
		if (responseObject != null) {
			return responseObject.optString("msg");
		}
		return null;
	}

	/**
	 * API 是否调用成功
	 * 
	 * @return API 是否调用成功
	 * @throws JSONException
	 */
	public Boolean isOK() {
		return status() == APIHttpMetadata.kGGApiStatusSuccess;
	}

	public int messageCode() {
		if (responseObject != null) {
			return responseObject.optInt("msg_code");
		}
		return MessageCode.ErrorNull;
	}

	public void setMessageCode(long aMessageCode) {
		if (responseObject != null) {
			try {
				responseObject.put("msg_code", aMessageCode);
			} catch (JSONException e) {
				// Do nothing
			}
		}
	}

	public String messageExtraInfo() {
		if (responseObject != null) {
			return responseObject.optString("msg_extra_info");
		}
		return null;
	}

	public JSONObject data() {
		if (responseObject != null) {
			return responseObject.optJSONObject("data");
		}
		return null;
	}

	public JSONArray dataArray() {
		if (responseObject != null) {
			return responseObject.optJSONArray("data");
		}
		return null;
	}

	/** --- simple data access methods ---- */
	public boolean dataHasMore() {
		JSONObject dataObj = data();
		if (dataObj != null) {
			return (dataObj.optInt("hasMore") == 1 ? true : false);
		}
		return false;
	}

	public long dataTimestamp() {
		JSONObject dataObj = data();
		if (dataObj != null) {
			return dataObj.optLong("timestamp");
		}
		return 0;
	}

	public JSONArray dataInfos() {
		JSONObject dataObj = data();
		if (dataObj != null) {
			return dataObj.optJSONArray("info");
		}
		return null;
	}

	public JSONObject dataFacet() {
		JSONObject dataObj = data();
		if (dataObj != null) {
			return dataObj.optJSONObject("facet");
		}
		return null;
	}

	public Boolean dataCharEnabled() {
		JSONObject dataObj = data();
		if (dataObj != null) {
			return dataObj.optInt("chart_enabled") == 1 ? true : false;
		}
		return false;
	}

	private final String CLASS_COMPANY = "CLASS_COMPANY";
	private final String CLASS_UPDATE = "CLASS_UPDATE";
	private final String CLASS_HAPPENING = "CLASS_HAPPENING";
	private final String CLASS_PERSON = "CLASS_PERSON";
	private final String CLASS_MENU_DATA = "CLASS_MENU_DATA";
	private final String CLASS_MEDIA_FILTER = "CLASS_MEDIA_FILTER";
	private final String CLASS_CATEGORY_FILTER = "CLASS_CATEGORY_FILTER";
	private final String CLASS_AGENT = "CLASS_AGENT";
	private final String CLASS_FUNCTIONAL_AREA = "CLASS_FUNCTIONAL_AREA";
	private final String CLASS_SAVED_SEARCH = "CLASS_SAVED_SEARCH";
	private final String CLASS_SEARCH_PERSON = "CLASS_SEARCH_PERSON";
	private final String CLASS_GROUP = "CLASS_GROUP";

	private Object createObject(String aClassName) {
		if (aClassName != null && aClassName.length() > 0) {
			if (aClassName.equalsIgnoreCase(CLASS_COMPANY)) {
				return new Company();
			} else if (aClassName.equalsIgnoreCase(CLASS_UPDATE)) {
				return new Update();
			} else if (aClassName.equalsIgnoreCase(CLASS_HAPPENING)) {
				return new Happening();
			} else if (aClassName.equalsIgnoreCase(CLASS_PERSON)) {
				return new Person();
			} else if (aClassName.equalsIgnoreCase(CLASS_MENU_DATA)) {
				return new MenuData();
			} else if (aClassName.equalsIgnoreCase(CLASS_MEDIA_FILTER)) {
				return new MediaFilter();
			} else if (aClassName.equalsIgnoreCase(CLASS_CATEGORY_FILTER)) {
				return new CategoryFilter();
			} else if (aClassName.equalsIgnoreCase(CLASS_AGENT)) {
				return new Agent();
			} else if (aClassName.equalsIgnoreCase(CLASS_FUNCTIONAL_AREA)) {
				return new FunctionalArea();
			} else if (aClassName.equalsIgnoreCase(CLASS_SAVED_SEARCH)) {
				return new SavedSearch();
			} else if (aClassName.equalsIgnoreCase(CLASS_SEARCH_PERSON)) {
				return new SearchPerson();
			} else if (aClassName.equalsIgnoreCase(CLASS_GROUP)) {
				return new Group();
			}

		}

		return null;
	}

	private DataPage parsePageForClass(String aClassName) {
		if (aClassName == null || aClassName.length() <= 0)
			return null;
		
		JSONObject data = data();
		if (data == null)
			return null;

		DataPage dataPage = new DataPage();
		dataPage.hasMore = dataHasMore();
		dataPage.timestamp = dataTimestamp();
		dataPage.chartEnabled = dataCharEnabled();

		// data infos
		JSONArray dataInfos = dataInfos();
		int length = (dataInfos != null) ? dataInfos.length() : -1;
		if (length > 0) {
			dataPage.items = new ArrayList<Object>();
			for (int i = 0; i < length; i++) {
				JSONObject infoObj = dataInfos.optJSONObject(i);
				Object dataObj = createObject(aClassName);
				
				if (DataModel.class.isInstance(dataObj)) {
					((DataModel) dataObj).parseData(infoObj);
				}
				
				dataPage.items.add(dataObj);
			}
		}

		// data facet
		JSONObject facetData = dataFacet();
		if (facetData != null) {
			Facet facet = new Facet();
			facet.parseData(facetData);
			dataPage.facet = facet;
		}

		return dataPage;
	}
	
	private DataPage parseImportPageForClass(String aClassName) {
		
		if (aClassName == null || aClassName.length() <= 0)
			return null;
		
		JSONObject data = data();
		if (data == null)
			return null;
		
		DataPage dataPage = new DataPage();
		dataPage.hasMore = dataHasMore();
		dataPage.timestamp = dataTimestamp();
		dataPage.chartEnabled = dataCharEnabled();
		
		
		// data infos
		JSONArray dataInfos = dataInfos();
		int length = (dataInfos != null) ? dataInfos.length() : -1;
		if (length > 0) {
			dataPage.items = new ArrayList<Object>();
			for (int i = 0; i < length; i++) {
				JSONObject infoObj = dataInfos.optJSONObject(i);
				
				Iterator it = infoObj.keys();
				while (it.hasNext()) {
					String key = it.next().toString();
					Log.v("silen", "key = " + key);
					JSONObject companyObject = infoObj.optJSONObject(key);
					try {
						companyObject.put("websiteKey", key);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					
					Object dataObj = createObject(aClassName);
					
					if (DataModel.class.isInstance(dataObj)) {
						Log.v("silen", "companyObject = " + companyObject.toString());
						((DataModel) dataObj).parseData(companyObject);
					}
					
					dataPage.items.add(dataObj);
				}
				
				
			}
		}
		
		// data facet
		JSONObject facetData = dataFacet();
		if (facetData != null) {
			Facet facet = new Facet();
			facet.parseData(facetData);
			dataPage.facet = facet;
		}
		
		return dataPage;
	}

	/** --- parse data Page methods --- */
	public DataPage parseGetCompanyUpdates() {
		return parsePageForClass(CLASS_UPDATE);
	}

	public DataPage parseGetSavedUpdates() {
		return parsePageForClass(CLASS_UPDATE);
	}

	public DataPage parseGetCompanyHappenings() {
		return parsePageForClass(CLASS_HAPPENING);
	}

	public DataPage parseGetCompanyPeople() {
		return parsePageForClass(CLASS_PERSON);
	}
	
	public DataPage parseGetSearchPeople() {
		return parsePageForClass(CLASS_SEARCH_PERSON);
	}

	public DataPage parseGetSimilarCompanies() {
		return parsePageForClass(CLASS_COMPANY);
	}

	public DataPage parseSearchCompany() {
		return parsePageForClass(CLASS_COMPANY);
	}
	
	public DataPage parseGetAllCompanyGroups() {
		return parsePageForClass(CLASS_GROUP);
	}

	public DataPage parseFollowedCompanies() {
		DataPage dataPage = parsePageForClass(CLASS_COMPANY);
		if (dataPage == null || dataPage.items == null || dataPage.items.size() <= 0)
			return dataPage;

		for (Object company : dataPage.items) {
			((Company) company).followed = true;
		}

		return dataPage;
	}

	public DataPage parseImportCompanies() {
		return parseImportPageForClass(CLASS_COMPANY);
	}

	public DataPage parseGetRecommendedCompanies() {
		return parsePageForClass(CLASS_COMPANY);
	}

	public DataPage parseGetMediaFiltersList() {
		return parsePageForClass(CLASS_MEDIA_FILTER);
	}

	public DataPage parseGetAgentFiltersList() {
		return parsePageForClass(CLASS_AGENT);
	}

	public DataPage parseGetCategoryFiltersList() {
		return parsePageForClass(CLASS_CATEGORY_FILTER);
	}

	public DataPage parseGetAgents() {
		return parsePageForClass(CLASS_AGENT);
	}

	public DataPage parseGetFunctionalAreas() {
		return parsePageForClass(CLASS_FUNCTIONAL_AREA);
	}

	public DataPage parseSearchForPeople() {
		return parsePageForClass(CLASS_PERSON);
	}

	public DataPage parseGetFollowedPeople() {
		DataPage dataPage = parsePageForClass(CLASS_PERSON);
		if (dataPage == null || dataPage.items == null || dataPage.items.size() <= 0)
			return dataPage;

		for (Object company : dataPage.items) {
			((Person) company).followed = true;
		}

		return dataPage;
	}
	
	public DataPage parseGetSeggestedPeople() {
		return parsePageForClass(CLASS_PERSON);
	}

	public DataPage parseGetRecommendedPeople() {
		return parsePageForClass(CLASS_PERSON);
	}
	
	public DataPage parseGetSavedSearch() {
		return parsePageForClass(CLASS_SAVED_SEARCH);
	}

	/** --- parse data model methods --- */
	public Member parseLogin() {
		JSONObject data = data();
		if (data == null)
			return null;

		Member result = new Member();
		result.parseData(data());
		return result;
	}

	public Company parseGetCompanyOverview() {
		JSONObject data = data();
		if (data == null)
			return null;

		Company result = new Company();
		result.parseData(data);
		return result;
	}

	public Person parseGetPersonOverview() {
		JSONObject data = data();
		if (data == null)
			return null;

		Person result = new Person();
		result.parseData(data);
		return result;
	}

	public UserProfile parseGetMyOverview() {
		JSONObject data = data();
		if (data == null)
			return null;

		UserProfile result = new UserProfile();
		result.parseData(data);
		return result;
	}

	public Update parseGetCompanyUpdateDetail() {
		JSONObject data = data();
		if (data == null)
			return null;

		Update result = new Update();
		result.parseData(data);
		return result;
	}

	public Happening parseCompanyEventDetail() {
		JSONObject data = data();
		if (data == null)
			return null;

		Happening result = new Happening();
		result.parseData(data);
		return result;
	}

	private DataPage parseMenuData(JSONObject aMenuData) {
		if (aMenuData == null) return null;

		DataPage dataPage = new DataPage();
		dataPage.hasMore = (aMenuData.optInt("hasMore") == 1 ? true : false);
		dataPage.timestamp = aMenuData.optLong("timestamp");
		dataPage.chartEnabled = aMenuData.optInt("chart_enabled") == 1 ? true : false;

		// data infos
		JSONArray dataInfos = aMenuData.optJSONArray("info");
		int length = (dataInfos != null) ? dataInfos.length() : -1;
		if (length > 0) {
			dataPage.items = new ArrayList<Object>();
			for (int i = 0; i < length; i++) {
				JSONObject infoObj = dataInfos.optJSONObject(i);
				MenuData dataObj = new MenuData();
				dataObj.parseData(infoObj);
				
				dataPage.items.add(dataObj);
			}
		}
		
		return dataPage;
	}
	
	public List<DataPage> parseGetMenu(Boolean aIsCompanyMenu) {
		JSONArray dataArray = dataArray();
		if (dataArray == null || dataArray.length() <= 0)
			return null;

		List<DataPage> resultList = new ArrayList<DataPage>();
		int type = aIsCompanyMenu ? MenuData.kGGMenuTypeCompany
				: MenuData.kGGMenuTypePerson;
		for (int i = 0; i < dataArray.length(); i++) {
			JSONObject menuData = dataArray.optJSONObject(i);
			if (menuData != null) {
				DataPage page = parseMenuData(menuData);//parsePageForClass(CLASS_MENU_DATA);
				if (page != null) {
					if (page.items != null) {
						for (Object obj : page.items) {
							((MenuData) obj).type = type;
						}
					}

					resultList.add(page);
				}
			}

			type++;
		}

		return resultList;
	}

	public List<String> parseSnGetList() {
		JSONObject data = data();
		if (data == null)
			return null;

		JSONArray typesArray = data.optJSONArray("types");
		if (typesArray != null) {
			List<String> result = new ArrayList<String>();
			for (int i = 0; i < typesArray.length(); i++) {
				result.add((String) typesArray.optString(i));
			}

			return result;
		}

		return null;
	}

	public SnUserInfo parseSnGetUserInfo() {
		JSONObject data = data();
		if (data == null)
			return null;

		SnUserInfo result = new SnUserInfo();
		result.parseData(data);
		return result;
	}
	
	public Filters parserFilters() {
		JSONObject data = data();
		if (data == null) return null;
		JSONObject options = data.optJSONObject("options");
		if (options == null) return null;
		
		Filters filters = new Filters();
		
		JSONArray Ownership = options.optJSONArray("list_ownership");
		filters.setOwnerships(setFilterItem(Ownership, true));
		JSONArray PeopleTypesFromPeople = options.optJSONArray("people_search_people_types");
		filters.setPeopleTypesFromPeople(setFilterItem(PeopleTypesFromPeople, true));
		
		JSONArray SortByFromBuz = options.optJSONArray("list_buz_sortby");//TODO
//		filters.setSortByFromBuz(setFilterItem(SortByFromBuz, false));
		List<FilterItem> sortbyBuzs = new ArrayList<FilterItem>();
		for (int i = 0; i < 4; i ++) {
			FilterItem filterItem = new FilterItem();
			if (i == 0) {
				filterItem.setKey("noe");
				filterItem.setValue("Employee size");
				filterItem.setChecked(true);
			} else if (i == 1) {
				filterItem.setKey("revenue");
				filterItem.setValue("Revenue size");
			} else if (i == 2) {
				filterItem.setKey("buzname");
				filterItem.setValue("Name");
			} else if (i == 3) {
				filterItem.setKey("relevance");
				filterItem.setValue("Search Relevance");
			}
			sortbyBuzs.add(filterItem);
		}
		filters.setSortByFromBuz(sortbyBuzs);
		
		JSONArray SortByFromContact = options.optJSONArray("list_contact_sortby");//TODO
//		filters.setSortByFromContact(setFilterItem(SortByFromContact, false));
		List<FilterItem> sortbyContact = new ArrayList<FilterItem>();
		for (int i = 0; i < 4; i ++) {
			FilterItem filterItem = new FilterItem();
			if (i == 0) {
				filterItem.setKey("joblevel");
				filterItem.setValue("Job Level");
				filterItem.setChecked(true);
			} else if (i == 1) {
				filterItem.setKey("name");
				filterItem.setValue("Name");
			} else if (i == 2) {
				filterItem.setKey("buzname");
				filterItem.setValue("Company Name");
			} else if (i == 3) {
				filterItem.setKey("relevance");
				filterItem.setValue("Search Relevance");
			}
			sortbyContact.add(filterItem);
		}
		filters.setSortByFromContact(sortbyContact);
		
		
		JSONArray CompanyTypesFromPeople = options.optJSONArray("people_search_company_types");
		filters.setCompanyTypesFromPeople(setFilterItem(CompanyTypesFromPeople, false));
		JSONArray CompanyTypesFromCompany = options.optJSONArray("company_search_company_types");
		filters.setCompanyTypesFromCompany(setFilterItem(CompanyTypesFromCompany, false));
		JSONArray EmployeeSizeFromBuz = options.optJSONArray("list_buz_employee_size");
		filters.setEmployeeSizeFromBuz(setFilterItem(EmployeeSizeFromBuz, true));
		JSONArray Occurreds = options.optJSONArray("occurreds");
		filters.setOccurreds(setFilterItem(Occurreds, false));
		JSONArray JobLevel = options.optJSONArray("list_job_level");
		filters.setJobLevel(setFilterItem(JobLevel, true));
		JSONArray FunctionalRoles = options.optJSONArray("functional_roles");
		filters.setFunctionalRoles(setFilterItem(FunctionalRoles, true));
		JSONArray SalesVolumeFromBuz = options.optJSONArray("list_buz_sales_volume");
		filters.setSalesVolumeFromBuz(setFilterItem(SalesVolumeFromBuz, true));
		
		JSONArray DateRanges = options.optJSONArray("list_build_list_date_range");
		List<FilterItem> mDateRanges = setFilterItem(DateRanges, false);
		for (int i = 0; i < mDateRanges.size(); i ++) {
			if (mDateRanges.get(i).getValue().trim().equals("1")) {
				mDateRanges.get(i).setValue("Day");
			} else if (mDateRanges.get(i).getValue().trim().equals("7")) {
				mDateRanges.get(i).setValue("Week");
			} else if (mDateRanges.get(i).getValue().trim().equals("14")) {
				mDateRanges.get(i).setValue("2 Weeks");
			} else if (mDateRanges.get(i).getValue().trim().equals("30")) {
				mDateRanges.get(i).setValue("Month");
			}  else if (mDateRanges.get(i).getValue().trim().equals("90")) {
				mDateRanges.get(i).setValue("3 Months");
			}
		}
		filters.setDateRanges(mDateRanges);
		
		JSONArray mileStoneDateRange = options.optJSONArray("occurreds");
		List<FilterItem> mileStoneDateRanges = setFilterItem(mileStoneDateRange, false);
		filters.setMileStoneDateRange(mileStoneDateRanges);
		
		JSONArray Milestones = options.optJSONArray("milestones");
		filters.setMileStones(setFilterItem(Milestones, false));
		
		JSONArray FiscalYearEndMonths = options.optJSONArray("fiscal_year_end_months");
		filters.setFiscalYearEndMonths(setFilterItem(FiscalYearEndMonths, true));
		
		//TODO
//		JSONArray NewsTriggers = options.optJSONArray("system_agents");
//		filters.setNewsTriggers(setFilterItem(NewsTriggers, true));
		filters.setNewsTriggers(Constant.locationSelecedNewsTriggers);
		
		JSONArray Ranks = options.optJSONArray("list_rank");
		filters.setRanks(setFilterItem(Ranks, true));
		
		JSONArray IndustriesObject = options.optJSONArray("industries");
		filters.setIndustries(setIndustry(IndustriesObject));
		
		return filters;
	}

	private List<FilterItem> setFilterItem(JSONArray jsonArray, Boolean addAll) {
		List<FilterItem> fiItems = new ArrayList<FilterItem>();
		if (addAll) {
			for (int i = 0 ; i < jsonArray.length() + 1; i ++) {
				if (i == 0) {
					FilterItem fItem = new FilterItem();
					fItem.setKey("");
					fItem.setValue("All");
					fiItems.add(fItem);
				} else {
					JSONObject jsonObject = jsonArray.optJSONObject(i - 1);
					String key = jsonObject.optString("key");
					String value = jsonObject.optString("value");
					FilterItem fItem = new FilterItem();
					fItem.setKey(key);
					fItem.setValue(value);
					fiItems.add(fItem);
				}
			}
		} else {
			for (int i = 0 ; i < jsonArray.length(); i ++) {
				JSONObject jsonObject = jsonArray.optJSONObject(i);
				String key = jsonObject.optString("key");
				String value = jsonObject.optString("value");
				FilterItem fItem = new FilterItem();
				fItem.setKey(key);
				fItem.setValue(value);
				fiItems.add(fItem);
			}
		}
		return fiItems;
	}
	
	@SuppressWarnings("unused")
	private List<Industry> setIndustry(JSONArray jsonArray) {
		List<Industry> industries = new ArrayList<Industry>();
		if (industries == null) return industries;
		
		for (int i = 0 ; i < jsonArray.length() + 1; i ++) {
			if (i == 0) {//TODO
				Industry industry = new Industry();
				industry.setId("");
				industry.setName("All");
				industries.add(industry);
			} else {
				JSONObject jsonObject = jsonArray.optJSONObject(i - 1);
				String id = jsonObject.optString("id");
				String name = jsonObject.optString("name");
				JSONArray jArray = jsonObject.optJSONArray("children");
				List<Industry> industriesList = new ArrayList<Industry>();
				if (null != jsonArray) {
					for (int j = 0; j < jArray.length() + 1; j ++) {
						if (j == 0) {
							Industry industry = new Industry();
							industry.setId("");
							industry.setName("All");
							industriesList.add(industry);
						} else {
							JSONObject jObject = jArray.optJSONObject(j - 1);
							Industry industry = new Industry();
							industry.setId(jObject.optString("id"));
							industry.setName(jObject.optString("name"));
							industriesList.add(industry);
						}
					}
				}
				Industry industry = new Industry();
				industry.setId(id);
				industry.setName(name);
				industry.setChildrens(industriesList);
				industries.add(industry);
			}
		}
		return industries;
	}
	
	public List<Location> parserLocation() {
		JSONArray dataInfos = dataInfos();
		List<Location> locations = new ArrayList<Location>();
		if (null == dataInfos) return locations;
		for (int i = 0; i < dataInfos.length(); i ++) {
			JSONObject jsonObject = dataInfos.optJSONObject(i);
			Location location = new Location();
			location.setStatus(jsonObject.optString("status"));
			location.setCode(jsonObject.optString("org_location_code"));
			location.setLocation(jsonObject.optString("org_location"));
			locations.add(location);
		}
		return locations;
	}
	
	public List<Agent> parserAgent() {
		JSONArray dataInfos = dataInfos();
		List<Agent> agents = new ArrayList<Agent>();
		if (null == dataInfos) return agents;
		for (int i = 0; i < dataInfos.length(); i ++) {
			JSONObject jsonObject = dataInfos.optJSONObject(i);
			Agent agent = new Agent();
			agent.setName(jsonObject.optString("agent"));
			agents.add(agent);
		}
		return agents;
	}
	
	private String companyQueryInfoStr;
	
	public QueryInfo parserQueryInfo(Boolean fromCompany) {
		Filters mFilters = Constant.MFILTERS;
		QueryInfo queryInfo = new QueryInfo();
		JSONObject dataObj = data();
		JSONObject friendlyInfo = dataObj.optJSONObject("search_data_user_frendly_info");
		Log.v("silen", "friendlyInfo = " + friendlyInfo.toString());
		
		companyQueryInfoStr = "";//组装save中的string
		
		//Job Title
		String jobTitleStr = friendlyInfo.optString("dop_title");
		if (!jobTitleStr.isEmpty()) {
			if (jobTitleStr.isEmpty()) {
				companyQueryInfoStr = jobTitleStr;
			} else {
				companyQueryInfoStr = companyQueryInfoStr + "," + jobTitleStr;
			}
			queryInfo.setJobTitle(jobTitleStr);
		}
		
		//Job Level
		JSONArray jobLevelArray = friendlyInfo.optJSONArray("dop_job_level");
		if (null != jobLevelArray) {
			queryInfo.setJobLevels(setQueryInfo(jobLevelArray, "dop_level"));
			List<FilterItem> jobLevelList = mFilters.getJobLevel();
			setFilterOptionsCheck(jobLevelArray, jobLevelList);
			
			//根据返回的queryInfo 设置MileStoneOccurrenceType
			for (int i = 0; i < jobLevelArray.length(); i ++) {
				jobLevelList.get(0).setChecked(false);
				String id = jobLevelArray.optJSONObject(i).optString("id");
				for (int j = 0 ; j < jobLevelList.size(); j ++) {
					String key = jobLevelList.get(j).getKey();
					if (id.equalsIgnoreCase(key)) {
						jobLevelList.get(j).setChecked(true);
					}
				}
			}
		}
		
		//people_location_code
		JSONArray peopleLocationCodeArray = friendlyInfo.optJSONArray("people_location_code");
		
		if (null != peopleLocationCodeArray) {
			
			queryInfo.setLocations(setQueryInfoForPeopleLocationCode(peopleLocationCodeArray));
			
			List<Location> peopleLocationCodes = mFilters.getLocations();
			setFilterOptionsCheckForPeopleLocationCode(peopleLocationCodeArray, peopleLocationCodes);
			
			//根据返回的queryInfo 设置people location code
			for (int i = 0; i < peopleLocationCodeArray.length(); i ++) {
				
				String id = peopleLocationCodeArray.optJSONObject(i).optString("id");
				for (int j = 0 ; j < peopleLocationCodes.size(); j ++) {
					String mId = peopleLocationCodes.get(j).getCode();
					if (id.equalsIgnoreCase(mId)) {
						peopleLocationCodes.get(j).setChecked(true);
					}
				}
			}
		}
		
		//Functional Role 
		JSONArray functionalRoleArray = friendlyInfo.optJSONArray("dop_functional_role");
		if (null != functionalRoleArray) {
			queryInfo.setFunctionalRoles(setQueryInfo(functionalRoleArray, "dop_functional_role"));
			List<FilterItem> functionalRoleList = mFilters.getFunctionalRoles();
			setFilterOptionsCheck(functionalRoleArray, functionalRoleList);
			
			//根据返回的queryInfo 设置MileStoneOccurrenceType
			for (int i = 0; i < functionalRoleArray.length(); i ++) {
				functionalRoleList.get(0).setChecked(false);
				String id = functionalRoleArray.optJSONObject(i).optString("id");
				for (int j = 0 ; j < functionalRoleList.size(); j ++) {
					String key = functionalRoleList.get(j).getKey();
					if (id.equalsIgnoreCase(key)) {
						functionalRoleList.get(j).setChecked(true);
					}
				}
			}
		}
		
		//News Triggers
		JSONArray newsTriggersArray = friendlyInfo.optJSONArray("mer_for_id");
		if (null != newsTriggersArray) {
			Log.v("silen", "newsTriggersArray = " + newsTriggersArray.toString());
			queryInfo.setNewsTriggers(setQueryInfo(newsTriggersArray, "mer_for_id"));
			
			List<FilterItem> newsTriggerList = mFilters.getNewsTriggers();
			setFilterOptionsCheck(newsTriggersArray, newsTriggerList);
			
			//根据返回的queryInfo 设置News Triggers
			for (int i = 0; i < newsTriggersArray.length(); i ++) {
				newsTriggerList.get(0).setChecked(false);
				String id = newsTriggersArray.optJSONObject(i).optString("id");
				for (int j = 0 ; j < newsTriggerList.size(); j ++) {
					String key = newsTriggerList.get(j).getKey();
					if (id.equalsIgnoreCase(key)) {
						newsTriggerList.get(j).setChecked(true);
					}
				}
			}
		}

		//DateRange
		JSONArray dateRangeArray = friendlyInfo.optJSONArray("search_date_range");
		if (null != dateRangeArray) {
			queryInfo.setDateRange(setQueryInfo(dateRangeArray, "search_date_range"));
			
			List<FilterItem> dateRangeList = mFilters.getDateRanges();
			setFilterOptionsCheck(dateRangeArray, dateRangeList);
			
			//根据返回的queryInfo 设置MileStoneOccurrenceType
			for (int i = 0; i < dateRangeArray.length(); i ++) {
				dateRangeList.get(3).setChecked(false);
				String id = dateRangeArray.optJSONObject(i).optString("id");
				for (int j = 0 ; j < dateRangeList.size(); j ++) {
					String key = dateRangeList.get(j).getKey();
					if (id.equalsIgnoreCase(key)) {
						dateRangeList.get(j).setChecked(true);
					}
				}
			}
		}
		
		//Company Companies
		JSONArray companiesArray = friendlyInfo.optJSONArray("search_company_for_type");
		if (null != companiesArray) {
			if (fromCompany) {
				queryInfo.setCompaniesForCompany(setQueryInfo(companiesArray, "search_company_for_type"));
			} else {
				queryInfo.setCompaniesForPeople(setQueryInfo(companiesArray, "search_company_for_type"));
			}
			
			if (fromCompany) {
				List<FilterItem> companiesForCompanyList = mFilters.getCompanyTypesFromCompany();
				setFilterOptionsCheck(companiesArray, companiesForCompanyList);
				
				//根据返回的queryInfo 设置Companies
				for (int i = 0; i < companiesArray.length(); i ++) {
					companiesForCompanyList.get(0).setChecked(false);
					String id = companiesArray.optJSONObject(i).optString("id");
					for (int j = 0 ; j < companiesForCompanyList.size(); j ++) {
						String key = companiesForCompanyList.get(j).getKey();
						if (id.equalsIgnoreCase(key)) {
							companiesForCompanyList.get(j).setChecked(true);
						}
					}
				}
			} else {
				List<FilterItem> companiesForPeopleList = mFilters.getCompanyTypesFromPeople();
				setFilterOptionsCheck(companiesArray, companiesForPeopleList);
				
				//根据返回的queryInfo 设置Companies
				for (int i = 0; i < companiesArray.length(); i ++) {
					companiesForPeopleList.get(0).setChecked(false);
					String id = companiesArray.optJSONObject(i).optString("id");
					for (int j = 0 ; j < companiesForPeopleList.size(); j ++) {
						String key = companiesForPeopleList.get(j).getKey();
						Log.v("silen", "APIParser-key = " + key);
						if (id.equalsIgnoreCase(key)) {
							companiesForPeopleList.get(j).setChecked(true);
						}
					}
				}
			}
		}
		
		//EventSearchKeywords
		String eventSearchKeywords = friendlyInfo.optString("event_search_keywords");
		if (!TextUtils.isEmpty(eventSearchKeywords)) {
			QueryInfoItem queryInfoItem = new QueryInfoItem();
			queryInfoItem.setName(eventSearchKeywords);
			queryInfoItem.setType("event_search_keywords");
			queryInfo.setEventSearchKeywords(queryInfoItem);
			
			//TODO
//			mFilters.get
			Log.v("silen", "eventSearchKeywords = " + eventSearchKeywords);
			if (TextUtils.isEmpty(companyQueryInfoStr)) {
				companyQueryInfoStr = eventSearchKeywords;
			} else {
				companyQueryInfoStr = companyQueryInfoStr + "," + eventSearchKeywords;
			}
		}
		
		//filter_saved_company_search
		JSONArray savedCompanyArray = friendlyInfo.optJSONArray("filter_saved_company_search");
		
		if (null != savedCompanyArray) {
			
			queryInfo.setSavedCompany(setQueryInfo(savedCompanyArray, "filter_saved_company_search"));
			
			List<FilterItem> savedCompaniesList = mFilters.getSavedCompanies();
			setFilterOptionsCheck(savedCompanyArray, savedCompaniesList);
			
			//根据返回的queryInfo 设置savedCompany
			for (int i = 0; i < savedCompanyArray.length(); i ++) {
				savedCompaniesList.get(0).setChecked(false);
				String id = savedCompanyArray.optJSONObject(i).optString("id");
				for (int j = 0 ; j < savedCompaniesList.size(); j ++) {
					String key = savedCompaniesList.get(j).getKey();
					if (id.equalsIgnoreCase(key)) {
						savedCompaniesList.get(j).setChecked(true);
					}
				}
			}
		}

		//companyQueryInfoStr
		String companySearchKeywords = friendlyInfo.optString("company_search_keywords");
		if (!companySearchKeywords.isEmpty()) {
			if (companyQueryInfoStr.isEmpty()) {
				companyQueryInfoStr = companySearchKeywords;
			} else {
				companyQueryInfoStr = companyQueryInfoStr + "," + companySearchKeywords;
			}
			queryInfo.setCompanySearchKeywords(companySearchKeywords);
		}
		
		//LocationCode
		JSONArray locationCodeArray = friendlyInfo.optJSONArray("location_code");
		
		if (null != locationCodeArray) {
			
			queryInfo.setLocationCode(setQueryInfo(locationCodeArray, "location_code"));
			
			List<Location> headquarters = mFilters.getHeadquarters();
			setLocationFilterOptionsCheck(locationCodeArray, headquarters);
			
			//根据返回的queryInfo 设置LocationCode
//			List<Location> headquarters = mFilters.getHeadquarters();
//			headquarters.clear();
			for (int i = 0; i < locationCodeArray.length(); i ++) {
				Location location = new Location();
				location.setChecked(true);
				location.setCode(locationCodeArray.optJSONObject(i).optString("id"));
				location.setLocation(locationCodeArray.optJSONObject(i).optString("name"));
				
				headquarters.add(location);
			}
		}

		//Industries
		JSONArray industryArray = friendlyInfo.optJSONArray("org_industries");
		if (null != industryArray) {
			
			queryInfo.setIndustries(setQueryInfo(industryArray, "org_industries"));
			
			List<Industry> industryList = mFilters.getIndustries();
			setIndustryFilterOptionsCheck(industryArray, industryList);
			
			//TODO
			//根据返回的queryInfo 设置MileStoneOccurrenceType
			for (int i = 0; i < industryArray.length(); i ++) {
				industryList.get(0).setChecked(false);
				String id = industryArray.optJSONObject(i).optString("id");
				for (int j = 0 ; j < industryList.size(); j ++) {
					String key = industryList.get(j).getKey();
					if (id.equalsIgnoreCase(key)) {
						industryList.get(j).setChecked(true);
					}
				}
			}
		}
		
		//EmployeeSize
		JSONArray employeeSizeArray = friendlyInfo.optJSONArray("org_employee_size");
		
		if (null != employeeSizeArray) {
			
			queryInfo.setEmployeeSize(setQueryInfo(employeeSizeArray, "org_employee_size"));
			
			List<FilterItem> employeeSizeList = mFilters.getEmployeeSizeFromBuz();
			setFilterOptionsCheck(employeeSizeArray, employeeSizeList);
			
			//根据返回的queryInfo 设置employeesize
			for (int i = 0; i < employeeSizeArray.length(); i ++) {
				employeeSizeList.get(0).setChecked(false);
				String id = employeeSizeArray.optJSONObject(i).optString("id");
				for (int j = 0 ; j < employeeSizeList.size(); j ++) {
					String key = employeeSizeList.get(j).getKey();
					if (id.equalsIgnoreCase(key)) {
						employeeSizeList.get(j).setChecked(true);
					}
				}
			}
		}
		

		//RevenueSize
		JSONArray revenueSizeArray = friendlyInfo.optJSONArray("org_revenue_size");
		if (null != revenueSizeArray) {
			queryInfo.setRevenueSize(setQueryInfo(revenueSizeArray, "org_revenue_size"));
			
			List<FilterItem> revenueSizeList = mFilters.getSalesVolumeFromBuz();
			setFilterOptionsCheck(revenueSizeArray, revenueSizeList);
			
			//根据返回的queryInfo 设置revenueSize
			for (int i = 0; i < revenueSizeArray.length(); i ++) {
				revenueSizeList.get(0).setChecked(false);
				String id = revenueSizeArray.optJSONObject(i).optString("id");
				for (int j = 0 ; j < revenueSizeList.size(); j ++) {
					String key = revenueSizeList.get(j).getKey();
					if (id.equalsIgnoreCase(key)) {
						revenueSizeList.get(j).setChecked(true);
					}
				}
			}
		}

		//Ownership
		JSONArray ownershipArray = friendlyInfo.optJSONArray("org_ownership");
		if (null != ownershipArray) {
			queryInfo.setOwnership(setQueryInfo(ownershipArray, "org_ownership"));
			
			List<FilterItem> ownershipList = mFilters.getOwnerships();
			setFilterOptionsCheck(ownershipArray, ownershipList);
			
			//根据返回的queryInfo 设置ownership
			for (int i = 0; i < ownershipArray.length(); i ++) {
				ownershipList.get(0).setChecked(false);
				String id = ownershipArray.optJSONObject(i).optString("id");
				for (int j = 0 ; j < ownershipList.size(); j ++) {
					String key = ownershipList.get(j).getKey();
					if (id.equalsIgnoreCase(key)) {
						ownershipList.get(j).setChecked(true);
					}
				}
			}
		}
		

		//MileStoneType
		JSONArray mileStoneTypeArray = friendlyInfo.optJSONArray("milestone_type");
		if (null != mileStoneTypeArray) {
			queryInfo.setMileStoneType(setQueryInfo(mileStoneTypeArray, "milestone_type"));
			
			List<FilterItem> mileStoneTypeList = mFilters.getMileStones();
			setFilterOptionsCheck(mileStoneTypeArray, mileStoneTypeList);
			
			//根据返回的queryInfo 设置MileStoneOccurrenceType
			for (int i = 0; i < mileStoneTypeArray.length(); i ++) {
				String id = mileStoneTypeArray.optJSONObject(i).optString("id");
				for (int j = 0 ; j < mileStoneTypeList.size(); j ++) {
					String key = mileStoneTypeList.get(j).getKey();
					if (id.equalsIgnoreCase(key)) {
						mileStoneTypeList.get(j).setChecked(true);
					}
				}
			}
		}
		

		//MileStoneOccurrenceType
		JSONArray mileStoneOccurrenceTypeArray = friendlyInfo.optJSONArray("milestone_occurrence_type");
		if (null != mileStoneOccurrenceTypeArray) {
			queryInfo.setMileStoneOccurrenceType(setQueryInfo(mileStoneOccurrenceTypeArray, "milestone_occurrence_type"));
			
			List<FilterItem> occurredList = mFilters.getOccurreds();
			setFilterOptionsCheck(mileStoneOccurrenceTypeArray, occurredList);
			
			//根据返回的queryInfo 设置MileStoneOccurrenceType
			for (int i = 0; i < mileStoneOccurrenceTypeArray.length(); i ++) {
				occurredList.get(0).setChecked(false);
				String id = mileStoneOccurrenceTypeArray.optJSONObject(i).optString("id");
				for (int j = 0 ; j < occurredList.size(); j ++) {
					String key = occurredList.get(j).getKey();
					if (id.equalsIgnoreCase(key)) {
						occurredList.get(j).setChecked(true);
					}
				}
			}
		}
		
		
		//Ranks
		JSONArray ranksArray = friendlyInfo.optJSONArray("rank");
		if (null != ranksArray) {
			queryInfo.setRanks(setQueryInfo(ranksArray, "rank"));
			
			List<FilterItem> rankList = mFilters.getRanks();
			setFilterOptionsCheck(ranksArray, rankList);
			
			//根据返回的queryInfo 设置rank
			for (int i = 0; i < ranksArray.length(); i ++) {
				rankList.get(0).setChecked(false);
				String id = ranksArray.optJSONObject(i).optString("id");
				for (int j = 0 ; j < rankList.size(); j ++) {
					String key = rankList.get(j).getKey();
					if (id.equalsIgnoreCase(key)) {
						rankList.get(j).setChecked(true);
					}
				}
			}
		}
		
		//FiscalMonth
		JSONArray fiscalMonthArray = friendlyInfo.optJSONArray("org_fiscal_month");
		if (null != fiscalMonthArray) {
			queryInfo.setFiscalMonth(setQueryInfo(fiscalMonthArray, "org_fiscal_month"));
			
			List<FilterItem> fiscalMothList = mFilters.getFiscalYearEndMonths();
			setFilterOptionsCheck(fiscalMonthArray, fiscalMothList);
			
			//根据返回的queryInfo 设置rank
			for (int i = 0; i < fiscalMonthArray.length(); i ++) {
				fiscalMothList.get(0).setChecked(false);
				String id = fiscalMonthArray.optJSONObject(i).optString("id");
				for (int j = 0 ; j < fiscalMothList.size(); j ++) {
					String key = fiscalMothList.get(j).getKey();
					if (id.equalsIgnoreCase(key)) {
						fiscalMothList.get(j).setChecked(true);
					}
				}
			}
		}
		
		queryInfo.setQueryInfoResult(companyQueryInfoStr);
		Log.v("silen", "companyQueryInfoStr = " + companyQueryInfoStr);
		
		return queryInfo;
	}

	private void setFilterOptionsCheck(JSONArray jsonArray, List<FilterItem> filterItems) {
		for (int i = 0; i < filterItems.size(); i ++) {
			String key = filterItems.get(i).getKey();
			for (int j = 0; j < jsonArray.length(); j ++) {
				JSONObject jObject = jsonArray.optJSONObject(j);
				String id = jObject.optString("id");
				if (id.equalsIgnoreCase(key)) {
					filterItems.get(i).setChecked(true);
					if (TextUtils.isEmpty(companyQueryInfoStr)) {
						companyQueryInfoStr = filterItems.get(i).getValue();
					} else {
						companyQueryInfoStr = companyQueryInfoStr + "," + filterItems.get(i).getValue();
					}
				}
			}
		}
	}
	
	private void setFilterOptionsCheckForPeopleLocationCode(JSONArray jsonArray, List<Location> peopleLocationCodes) {
		for (int i = 0; i < peopleLocationCodes.size(); i ++) {
			String mId = peopleLocationCodes.get(i).getCode();
			for (int j = 0; j < jsonArray.length(); j ++) {
				JSONObject jObject = jsonArray.optJSONObject(j);
				String id = jObject.optString("id");
				if (id.equalsIgnoreCase(mId)) {
					peopleLocationCodes.get(i).setChecked(true);
					if (TextUtils.isEmpty(companyQueryInfoStr)) {
						companyQueryInfoStr = peopleLocationCodes.get(i).getLocation();
					} else {
						companyQueryInfoStr = companyQueryInfoStr + "," + peopleLocationCodes.get(i).getLocation();
					}
				}
			}
		}
	}
	
	private void setIndustryFilterOptionsCheck(JSONArray jsonArray, List<Industry> industries) {
		for (int i = 0; i < industries.size(); i ++) {
			String industryId = industries.get(i).getId();
			for (int j = 0; j < jsonArray.length(); j ++) {
				JSONObject jObject = jsonArray.optJSONObject(j);
				if (null != jObject) {
					String id = jObject.optString("id");
					if (id.equalsIgnoreCase(industryId)) {
						industries.get(i).setChecked(true);
						if (TextUtils.isEmpty(companyQueryInfoStr)) {
							companyQueryInfoStr = industries.get(i).getName();
						} else {
							companyQueryInfoStr = companyQueryInfoStr + "," + industries.get(i).getName();
						}
					}
				}
			}
		}
	}
	
	private void setLocationFilterOptionsCheck(JSONArray jsonArray, List<Location> locations) {
		for (int i = 0; i < locations.size(); i ++) {
			String code = locations.get(i).getCode();
			for (int j = 0; j < jsonArray.length(); j ++) {
				JSONObject jObject = jsonArray.optJSONObject(j);
				String id = jObject.optString("id");
				if (id.equalsIgnoreCase(code)) {
					locations.get(i).setChecked(true);
					if (TextUtils.isEmpty(companyQueryInfoStr)) {
						companyQueryInfoStr = locations.get(i).getLocation();
					} else {
						companyQueryInfoStr = companyQueryInfoStr + "," + locations.get(i).getLocation();
					}
				}
			}
		}
	}
	
	private List<QueryInfoItem> setQueryInfo(JSONArray jsonArray, String type) {
		
		List<QueryInfoItem> queryInfoItems = new ArrayList<QueryInfoItem>();
		for (int i = 0; i < jsonArray.length(); i ++) {
			JSONObject jObject = jsonArray.optJSONObject(i);
			if (jObject != null) {
				QueryInfoItem queryInfoItem = new QueryInfoItem();
				queryInfoItem.setId(jObject.optString("id"));
				queryInfoItem.setName(jObject.optString("name"));
				queryInfoItem.setType(type);
				queryInfoItems.add(queryInfoItem);
			}
			
			//同步本地的filter options
		}
		return queryInfoItems;
		
	}
	
	private List<Location> setQueryInfoForPeopleLocationCode(JSONArray jsonArray) {
		
		List<Location> locationCodes = new ArrayList<Location>();
		for (int i = 0; i < jsonArray.length(); i ++) {
			JSONObject jObject = jsonArray.optJSONObject(i);
			if (jObject != null) {
				Location locationCode = new Location();
				
				locationCode.setCode(jObject.optString("id"));
				locationCode.setLocation(jObject.optString("name"));
				locationCodes.add(locationCode);
			}
			
			//同步本地的filter options
		}
		return locationCodes;
		
	}
	
}

