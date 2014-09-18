package com.gagein.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gagein.model.Agent;
import com.gagein.model.Company;
import com.gagein.model.Contact;
import com.gagein.model.Facet;
import com.gagein.model.FacetItem;
import com.gagein.model.FacetItemIndustry;
import com.gagein.model.Group;
import com.gagein.model.Happening;
import com.gagein.model.SavedSearch;
import com.gagein.model.Update;
import com.gagein.model.filter.FilterItem;
import com.gagein.model.filter.Filters;

/**
 * 存放应用常量
 * 
 * @author silen
 * 
 */
public abstract class Constant {

	/**
	 * 状态码:成功 1
	 */
	public static final String SUCCESS = "1";
	/**
	 * 状态码:参数错误 2
	 */
	public static final String PARAMETER_WRONG = "2";
	/**
	 * 状态码:验证错误 3
	 */
	public static final String VERIFY_FAIL = "3";
	/**
	 * 状态码:系统内部错误 4
	 */
	public static final String SYSTEM_INTERNAL_ERROR = "4";
	/**
	 * 状态码:用户操作错误 5
	 */
	public static final String USER_OPERATION_MISTAKE = "5";
	
	public static final String TIME_ZONE = "US/Pacific";
	
	public static final String MENU_BROADCAST = "menu";
	
	public static final String EXPLORE = "Explore";
	
	public static final String MENUID = "MenuId";
	
	public static String SUGGEST = "suggest";
	
	public static String SEARCH_COMPANY = "buz";
	
	public static String SEARCH_PEOPLE = "cnt";
	
	private static List<Update> updates;
	
	public static List<Agent> locationNewsTriggers = new ArrayList<Agent>();
	
	public static List<Group> selectedGroupFilter = new ArrayList<Group>();
	
	public static List<Agent> locationNewsTriggersForCompany = new ArrayList<Agent>();
	
	public static List<FilterItem> locationSelecedNewsTriggers = new ArrayList<FilterItem>();
	
	public static List<SavedSearch> locationSavedSearchs = new ArrayList<SavedSearch>();
	
	public static String APP_VERSION_NAME;
	
	public static Facet currentFacet;
	
	public static List<FacetItemIndustry> industriesItem = new ArrayList<FacetItemIndustry>();
	
	public static List<Contact> contacts;
	
	public static Boolean edit = false;
	
	public static String Category = "Category";
	
	public static String CategoryType = "CategoryType";
	
	public static List<Update> getUpdates() {
		if (updates == null) updates = new ArrayList<Update>();
		return updates;
	}

	public static void setUpdates(List<Update> anUpdates) {
		if (updates == null) updates = new ArrayList<Update>();
		if (anUpdates == null) return;
		updates.clear();
		updates.addAll(anUpdates);
	}

	public static List<Happening> happenings = new ArrayList<Happening>();
	
	/**
	 * PAGE_NUMBER_START = 1
	 */
	public static int PAGE_NUMBER_START = 1;
	
	//public static String REFRESH = "Refresh";
	
	public static String GAGEIN_SLOGAN = "Actionable News for Sales.";
	
	public static String COMPANY = "Company";
	
	public static String COMPANYNAME = "CompanyName";
	
	public static String EVENT = "Event";
	
	public static String EVENTS = "EVENTS";
	
	public static String COMPANYID = "CompanyId";
	
	public static String COMPANIESID = "CompaniesId";
	
	public static String PERSONID = "PersonId";
	
	public static String PERSON = "person";
	
	public static String POSITION = "position";
	
	public static String FROM = "from";
	
	public static String UPDATE = "update";
	
	public static String UPDATES = "UPDATES";
	
	public static String PEOPLE_UPDATES = "People Updates";
	
	public static String NEWS = "NEWS";
	
	public static String IMAGE_TYPE = "ImageType";
	
	public static String IMAGE_TYPE_MAP = "map";
	
	public static String IMAGE_TYPE_REVENUE = "revenue";
	
	public static String SETTING = "setting";
	
	public static String SETTING_TYPE = "settingType";
	
	public static String SUBJECT = "subject";
	
	public static String FACET = "facet";
	
	public static String NEWSID = "newsId";
	
	public static String GROUP = "group";
	
	public static String GROUPID = "groupId";
	
	public static String GROUPNAME = "groupName";
	
	public static String RELEVANCE = "relevance";
	
	public static String ACTIVITYNAME = "activity_name";
	
	public static String BROADCAST_REFRESH_NEWS = "broadcast refresh news";
	
	public static String BROADCAST_REFRESH_COMPANIES = "broadcast refresh companies";
	
	public static String BROADCAST_REFRESH_GROUPSACTIVITY = "broadcast refresh groupsActivity";
	
	public static String BROADCAST_GET_SAVED_SEARCH = "broadcast get saved search";
	
	public static String BROADCAST_ADD_COMPANIES = "broadcast add companies";
	
	public static String BROADCAST_ADD_NEW_COMPANIES = "broadcast add new companies";
	
	public static String BROADCAST_ADD_COMPANIES_FROM_FOLLOW_COMPANIES = "broadcast add companies from follow companies";
	
	public static String BROADCAST_FOLLOW_COMPANY = "broadcast follow company";
	
	public static String BROADCAST_UNFOLLOW_COMPANY = "broadcast unfollow company";
	
	public static String BROADCAST_REMOVE_COMPANIES = "broadcast remove companies";
	
	public static String BROADCAST_REMOVE_PENDING_COMPANIES = "broadcast remove pending companies";
	
	public static String BROADCAST_ADD_COMPANYGROUP = "broadcast add company group";
	
	public static String BROADCAST_REFRESH_SEARCH = "broadcast refresh search";
	
	public static String BROADCAST_REFRESH_COMPANY_NEWS = "broadcast refresh company news";
	
	public static String BROADCAST_REFRESH_COMPANY_PEOPLE = "broadcast refresh company people";
	
	public static String BROADCAST_FILTER_REFRESH_COMPETITORS = "broadcast filter refresh competitors";
	
	public static String BROADCAST_ADDED_PENDING_COMPANY = "broadcast added pending company";
	
	public static String BROADCAST_REFRESH_FILTER = "broadcast refresh filter";
	
	public static String BROADCAST_REMOVE_BOOKMARKS = "broadcast delete bookmarks";
	
	public static String BROADCAST_ADD_BOOKMARKS = "broadcast add bookmarks";
	
	public static String BROADCAST_HAVE_READ_STORY = "broadcast have read story";
	
	public static String LINKEDIN = "linkedin";
	public static String FACEBOOK = "facebook";
	public static String TWITTER = "twitter";
	public static String YOUTUBE = "youtube";
	public static String HOOVERS = "hoovers";
	public static String YAHOO = "yahoo";
	public static String YAMMER = "yammer";
	public static String CRUNCHBASE = "crunchbase";
	public static String SLIDESHARE = "slideshare";
	public static String SALESFORCE = "salesforce";
	
	public static String SEARCH_TIP_TEXT = "search tip text";
	
	public static int NEWS_TYPE = 1;
	public static int ABOUT_TYPE = 2;
	public static int PERSONS_TYPE = 3;
	public static int COMPETITORS_TYPE = 4;
	
	public static String BROADCAST_IRRELEVANT_TRUE = "broadcast irrelevant true";
	
	public static String BROADCAST_IRRELEVANT_FALSE = "broadcast irrelevant false";
	
	public static String BROADCAST_LIKED_NEWS = "broadcast set news liked";
	
	public static String BROADCAST_UNLIKE_NEWS = "broadcast set news UNlike";
	
	public static String UPDATEID = "updateid";
	
	/**
	 * SHOWSHARETIME = 200
	 */
	public static int SHOWSHARETIME = 200;
	
	/**
	 * URL = "url"
	 */
	public static String URL = "url";
	
	public static String WEBVIEW_FONT_SIZE = "WEBVIEW_FONT_SIZE";
	
	public static String NOTHING = "";
	
	public static byte PEOPLE_SORT_BY = 0;
	
	public static ArrayList<FacetItem> currentJobLevelForCompanyPeopleFilter = new ArrayList<FacetItem>();
	public static ArrayList<FacetItem> currentFunctionRoleForCompanyPeopleFilter = new ArrayList<FacetItem>();
	public static ArrayList<FacetItem> currentLinkedProfileForCompanyPeopleFilter = new ArrayList<FacetItem>();
	
	
	public static long INDUSTRY = 0;
	public static String COMPETITOR_SORT_BY = "noe";
	
	public static String FILTER_INDUSTRY_NAME = "";
	
	public static String INDUSTRYID = "0";
	
	public static String SAVEDID = "";
	
	public static String FILTERS = "filters";
	
	public static String MORENEWS = "morenews";
	
	
	/**
	 *  0
	 */
	public static int ZERO = 0;
	
	public static int COMPANY_PERSON = 0;
	
	public static int COMPANY_COMPETITOR = 1;
	
	public static Map<Long, List<Company>> updateMap = new HashMap<Long, List<Company>>();
	
	public static Map<Long, Happening> mHappeningMap = new HashMap<Long, Happening>();
	
	public static List<FacetItemIndustry> currentCompetitorIndustries = new ArrayList<FacetItemIndustry>();
	
	public static Group currentGroup;
	
	public static List<String> COMPETITOR_FILTER_PARAM_VALUE = new ArrayList<String>();
	
	public static Filters MFILTERS;
	
	public static List<Company> CURRENT_PENDING_COMPANY = new ArrayList<Company>();
	
	public static Boolean REVERSE = false;
	public static String COMPANY_SEARCH_KEYWORDS = "";
	public static String ALLWORDS_FOR_TRIGGERS = "";
//	public static String EXACTWORDS = "";
//	public static String ANYWORDS = "";
//	public static String NONEWORDS = "";
	
	public static int CURRENT_CATEGORY_TYPE_IN_SETTINGS = 0;
	
	public static List<Company> employmentHistory = new ArrayList<Company>();
	
	public static List<Update> SECFILINGS = new ArrayList<Update>();
	
	public static Boolean SIGNUP = false;
	
	public static String RATE_URL = "https://play.google.com/store/apps/details?id=com.gagein";
}
