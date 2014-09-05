package com.gagein.http;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.Volley;
import com.gagein.model.Contact;
import com.gagein.ui.common.JsonUTF8Request;
import com.gagein.util.ApplicationUrl;
import com.gagein.util.CommonUtil;
import com.gagein.util.Constant;
import com.gagein.util.Log;

/**
 * @author silen
 * 
 */
public class APIHttp {

	private Context mContext = null;
	public static String serverURL = ApplicationUrl.DemoURL;
	private String apiRootPath = null;
	private String deviceID = null;
	private RequestQueue mRequestQueue;
	private String TAG = "APIHttp";
	
	public APIHttp(Context mContext) {
		this.mContext = mContext;
		doCustruction();
	}
	
	private void doCustruction() {
		deviceID = CommonUtil.deviceID(mContext);
		mRequestQueue = Volley.newRequestQueue(mContext);
		apiRootPath = serverURL + "/svc/";
	}
	
	/**
	 * 
	 * @param aParams
	 */
	private void addBasicParams(HashMap<String, String> aParams) {
		if (aParams != null) {
			aParams.put(APIHttpMetadata.APP_CODE_KEY, APIHttpMetadata.APP_CODE_ANDROID);
			aParams.put(APIHttpMetadata.API_VERSION_KEY, APIHttpMetadata.API_VERSION_VALUE);
			aParams.put(APIHttpMetadata.APP_DEVICE_ID, deviceID);
			aParams.put(APIHttpMetadata.APP_VERION, Constant.APP_VERSION_NAME);
			String token = "";
			if (!"".equals(APIHttpMetadata.TOKEN)) {
				token = APIHttpMetadata.TOKEN;
			} else {
				token = CommonUtil.getToken(mContext);
			}
			aParams.put(APIHttpMetadata.ACCESS_TOKEN_KEY, token);
		}
	}
	
	/**
	 * 
	 * @param aParams
	 * @return
	 */
	private String parameterString(HashMap<String, String> aParams, Boolean needEncode) {
		if (aParams != null) {
			
			String paramStr = "";
			Iterator<Entry<String, String>> ttIterator = aParams.entrySet().iterator();
			
			while (ttIterator.hasNext()) {
				
				Entry<String, String> entry = (Entry<String, String>) ttIterator.next();
				
				String value = "";
				
				if (needEncode) {
					
					value = CommonUtil.urlEncodedString(entry.getValue());
					
				} else {
					
					value = entry.getValue();
					
					if (!TextUtils.isEmpty(value)) {
						value = value.replaceAll(" ", "%20");
					}
				}
				
				paramStr = paramStr + entry.getKey() + "=" + value + "&";
			}
			paramStr = paramStr.substring(0, paramStr.lastIndexOf("&"));
			return paramStr;
		}
		return null;
	}
	
	/**
	 * @param url
	 * @param params
	 * @return
	 */
	private String packageURL(String url, HashMap<String, String> params, Boolean needEncode) {
		String paramStr = parameterString(params, needEncode);
		url = (paramStr != null && paramStr.length() > 0) ? url + "?" + paramStr : url;
		Log.v(TAG, "url = " + url);
		return url;
	}
	
	/**
	 * 
	 * @param listener
	 * @param errorListener
	 * @param url
	 * @param params
	 */
	private void connectURL(int Method, Listener<JSONObject> listener, ErrorListener errorListener, String url, HashMap<String, String> params) {
		mRequestQueue.add(new JsonUTF8Request(Method, packageURL(url, params, true), null, listener, errorListener));
	}
	
	private void connectURLNoEncode(int Method, Listener<JSONObject> listener, ErrorListener errorListener, String url, HashMap<String, String> params) {
		mRequestQueue.add(new JsonUTF8Request(Method, packageURL(url, params, false), null, listener, errorListener));
	}
	
	public void stop() {
		mRequestQueue.cancelAll(this);
		mRequestQueue.stop();
	}
	
	/**
	 * 
	 * @param aCompanyID
	 * @param aNeedSocialProfile
	 * @param aContactID
	 * @param listener
	 * @param errorListener
	 */
	public void getCompanyOverview(long aCompanyID, Boolean aNeedSocialProfile, long aContactID, Listener<JSONObject> listener, ErrorListener errorListener) {
		String url = apiRootPath + "company/" + aCompanyID + "/overview";
		HashMap<String, String> params = new HashMap<String, String>();
		addBasicParams(params);
		params.put("include_sp", (aNeedSocialProfile ? "true" : "false"));
		params.put("include_contacts", "true");
		if (aContactID > 0)
			params.put("contactid", aContactID + "");
		params.put("no_proxy", "true");
		connectURL(Method.GET,listener, errorListener, url, params);
	}

	/**
	 * 获取Company的update列表(No Filter)
	 * 
	 * @param aCompanyID
	 * @param aNewsID
	 * @param aPageFlag
	 * @param aPageTime
	 */
	public void getCompanyUpdatesNoFilter(long aCompanyID, long agentID, long aNewsID, byte aPageFlag, long aPageTime, Listener<JSONObject> listener, ErrorListener errorListener) {
		String url = apiRootPath + "company/" + aCompanyID + "/updates";
		HashMap<String, String> params = new HashMap<String, String>();
		addBasicParams(params);
		params.put("newsid", aNewsID + "");
		params.put("pageflag", aPageFlag + "");
		params.put("pagetime", aPageTime + "");
		params.put("no_proxy", "true");
		
		if (agentID > 0) {
			params.put("agentid", agentID + "");
	    }
		
		connectURL(Method.GET,listener, errorListener, url, params);
	}
	
	
	
	/**
	 * 获取公司happening
	 * 
	 * @param aCompanyID
	 * @param aEventID
	 * @param aPageFlag
	 * @param aPageTime
	 * @param aIncludeCompetitors
	 */
	public void getHappeningsForTheCompany(long aCompanyID, long aEventID,
			byte aPageFlag, long aPageTime, Boolean aIncludeCompetitors,
			Listener<JSONObject> listener, ErrorListener errorListener) {
		getHappenings(APIHttpMetadata.EGGApiHappeningTypeCompany, aCompanyID, aEventID, aPageFlag, aPageTime, aIncludeCompetitors, listener, errorListener);
	}
	
	/**
	 * 获取happening
	 * 
	 * @param aAPIHappeningType
	 * @param aTypeID
	 * @param aEventID
	 * @param aPageFlag
	 * @param aPageTime
	 * @param aIncludeCompetitors
	 * @param callback
	 */
	private void getHappenings(byte aAPIHappeningType, long aTypeID,
			long aEventID, byte aPageFlag, long aPageTime,
			Boolean aIncludeCompetitors, Listener<JSONObject> listener, ErrorListener errorListener) {
		String url = apiRootPath + "member/me/event/tracker";
		HashMap<String, String> params = new HashMap<String, String>();
		addBasicParams(params);
		params.put("eventid", aEventID + "");
		params.put("pageflag", aPageFlag + "");
		params.put("pagetime", aPageTime + "");
		params.put("msg_format", "text");
		params.put("no_proxy", "true");

		switch (aAPIHappeningType) {
		case APIHttpMetadata.EGGApiHappeningTypeCompany: {
			params.put("orgid", aTypeID + "");
			params.put("include_competitors", (aIncludeCompetitors ? "true"
					: "false"));
		}
			break;

		case APIHttpMetadata.EGGApiHappeningTypePerson: {
			params.put("contactid", aTypeID + "");
		}
			break;

		case APIHttpMetadata.EGGApiHappeningTypeFunctionalArea: {
			params.put("functional_areaid", aTypeID + "");
		}
			break;

		case APIHttpMetadata.EGGApiHappeningTypeCategory: {
			params.put("categoryid", aTypeID + "");
		}
			break;

		default:
			break;
		}

		connectURL(Method.GET,listener, errorListener, url, params);
	}
	
	/**
	 * getCompanyPeople
	 * 
	 * @param aCompanyID
	 * @param aPageNumber
	 * @param aOrderBy
	 * @param aFunctionalAreaID
	 * @param aJobLevelID
	 */
	public void getCompanyPeople(long aCompanyID, int aPageNumber,
			byte aOrderBy, long aFunctionalAreaID, long aJobLevelID, long aLinkedProfile,
			Listener<JSONObject> listener, ErrorListener errorListener) {
		String url = apiRootPath + "company/" + aCompanyID + "/contacts";
		HashMap<String, String> params = new HashMap<String, String>();
		addBasicParams(params);
		params.put("page", aPageNumber + "");
		params.put("profile_type", aLinkedProfile + "");
		params.put("no_proxy", "true");
		if (aFunctionalAreaID > 0)
			params.put("functional_areaid", aFunctionalAreaID + "");
		if (aJobLevelID > 0)
			params.put("job_levelid", aJobLevelID + "");

		// sort
		String orderByStr = null;
		switch (aOrderBy) {
		case APIHttpMetadata.kGGContactsOrderByJobLevel: {
			orderByStr = "joblevel";
		}
			break;

		case APIHttpMetadata.kGGContactsOrderByName: {
			orderByStr = "name";
		}
			break;

		case APIHttpMetadata.kGGContactsOrderByRecent: {
			orderByStr = "recent";
		}
			break;

		default:
			break;
		}
		if (orderByStr != null)
			params.put("order", orderByStr);
		
		connectURL(Method.GET,listener, errorListener, url, params);
	}
	
	/**
	 * getSimilarCompanies
	 * 
	 * @param aCompanyID
	 * @param aPageNumber
	 * @param callback
	 */
	public void getSimilarCompanies(long aCompanyID, String industryid, int aPageNumber, String aOrder,
			Listener<JSONObject> listener, ErrorListener errorListener) {
		String url = apiRootPath + "company/" + aCompanyID + "/competitors";
		HashMap<String, String> params = new HashMap<String, String>();
		addBasicParams(params);
		params.put("page", aPageNumber + "");
		params.put("order", aOrder);
		params.put("industryid", industryid + "");
		connectURL(Method.GET,listener, errorListener, url, params);
	}
	
	/**
	 * Follow a company
	 * 
	 * @param aCompanyID
	 * @param callback
	 */
	public void followCompany(long aCompanyID, Listener<JSONObject> listener, ErrorListener errorListener) {
		String url = apiRootPath + "member/me/company/follow";
		HashMap<String, String> params = new HashMap<String, String>();
		addBasicParams(params);
		params.put("orgid", aCompanyID + "");
		connectURL(Method.GET,listener, errorListener, url, params);
	}

	/**
	 * Unfollow a company
	 * 
	 * @param aCompanyID
	 * @param callback
	 */
	public void unfollowCompany(long aCompanyID, Listener<JSONObject> listener, ErrorListener errorListener) {
		String url = apiRootPath + "member/me/company/unfollow";
		HashMap<String, String> params = new HashMap<String, String>();
		addBasicParams(params);
		params.put("orgid", aCompanyID + "");
		connectURL(Method.GET,listener, errorListener, url, params);
	}

	/**
	 * userLogin (POST method)
	 * 
	 * @param userName
	 * @param password
	 * @param apiCallback
	 */
	public void userLogin(String userName, String password,
			Listener<JSONObject> listener, ErrorListener errorListener) {
		String url = apiRootPath + "login";
		HashMap<String, String> postParems = new HashMap<String, String>();
		addBasicParams(postParems);
		postParems.put("mem_email", userName);
		postParems.put("mem_password", password);
		connectURL(Method.POST,listener, errorListener, url, postParems);
	}
	
	/**
	 * userRegister (POST method)
	 * 
	 * @param aEmail
	 *            Email
	 * @param aPassword
	 *            
	 * @param aFristName
	 *            firstname
	 * @param aLastName
	 *            lastname
	 * @param aTimeZone
	 *            
	 * @param apiCallback
	 *            
	 */
	public void userRegister(String aEmail, String aPassword,
			String aFristName, String aLastName, String aTimeZone,
			Listener<JSONObject> listener, ErrorListener errorListener) {
		String url = apiRootPath + "register";
		HashMap<String, String> postParems = new HashMap<String, String>();
		addBasicParams(postParems);
		postParems.put("mem_email", aEmail);
		postParems.put("mem_password", aPassword);
		postParems.put("mem_first_name", aFristName);
		postParems.put("mem_last_name", aLastName);
		postParems.put("mem_timezone", aTimeZone);
		
		connectURL(Method.POST,listener, errorListener, url, postParems);
	}

	/**
	 * FindPassword (POST method)
	 * 
	 * @param aEmail
	 *            Email
	 * @param apiCallback
	 *            
	 */
	public void userFindPassword(String aEmail, Listener<JSONObject> listener, ErrorListener errorListener) {
		String url = apiRootPath + "find_password";
		HashMap<String, String> postParems = new HashMap<String, String>();
		addBasicParams(postParems);
		postParems.put("mem_email", aEmail);
		connectURL(Method.POST,listener, errorListener, url, postParems);
	}

	/**
	 * 获取与某条update相关的updates列表
	 * 
	 * @param aSimilarID
	 * @param apiCallback
	 */
	public void getSimilarUpdates(long aSimilarID, Listener<JSONObject> listener, ErrorListener errorListener) {
		String url = apiRootPath + "member/me/update/tracker";
		HashMap<String, String> params = new HashMap<String, String>();
		addBasicParams(params);
		params.put("storyline", aSimilarID + "");
		params.put("no_proxy", "true");
		connectURL(Method.GET,listener, errorListener, url, params);
	}

	/**
	 * 获取company的updates列表
	 * 
	 * @param aCompanyID
	 * @param aNewsID
	 * @param aPageFlag
	 * @param aPageTime
	 * @param aSimilarIDs
	 * @param callback
	 */
	public void getCompanyUpdates(long aCompanyID, long aNewsID, byte aPageFlag, long aPageTime, String aSimilarIDs,
			Listener<JSONObject> listener, ErrorListener errorListener) {
		String url = apiRootPath + "member/me/update/tracker";
		HashMap<String, String> params = new HashMap<String, String>();
		addBasicParams(params);
		params.put("newsid", aNewsID + "");
		params.put("pageflag", aPageFlag + "");
		params.put("pagetime", aPageTime + "");
		params.put("orgid", aCompanyID + "");
		params.put("similarids", aSimilarIDs);
		params.put("no_proxy", "true");
		connectURL(Method.GET,listener, errorListener, url, params);
	}

	/**
	 * 快速搜索公司，用于auto complete
	 * 
	 * @param aKeyword
	 * @param callback
	 */
	public void getCompanySuggestions(String aKeyword, Listener<JSONObject> listener, ErrorListener errorListener) {
		String url = apiRootPath + "search/companies/get_suggestions";
		HashMap<String, String> params = new HashMap<String, String>();
		addBasicParams(params);
		params.put("q", aKeyword);
		connectURL(Method.POST, listener, errorListener, url, params);
	}

	/**
	 * Search companies
	 * 
	 * @param aKeyword
	 *            keyword
	 * @param aPage
	 *            page number (>=1)
	 * @param callback
	 */
	public void searchCompanies(String aKeyword, int aPage,
			Listener<JSONObject> listener, ErrorListener errorListener) {
		String url = apiRootPath + "search/companies";
		HashMap<String, String> params = new HashMap<String, String>();
		addBasicParams(params);
		params.put("q", aKeyword);
		params.put("page", aPage + "");
		connectURL(Method.POST, listener, errorListener, url, params);
	}
	
	public void searchAllPersons(String aKeyword, int aPage, Listener<JSONObject> listener, ErrorListener errorListener) {
		String url = apiRootPath + "search/contacts";
		HashMap<String, String> params = new HashMap<String, String>();
		addBasicParams(params);
		params.put("q", aKeyword);
		params.put("page", aPage + "");
		params.put("sort_by", "joblevel");
		
//		params.put("no_proxy", true + "");//TODO
		connectURL(Method.POST, listener, errorListener, url, params);
	}
	
	public void searchPartPersons(String aKeyword, int aPage, Listener<JSONObject> listener, ErrorListener errorListener) {
		String url = apiRootPath + "search/contacts/get_suggestions";
		HashMap<String, String> params = new HashMap<String, String>();
		addBasicParams(params);
		params.put("q", aKeyword);
		params.put("page", aPage + "");
//		params.put("no_proxy", "true");//TODO
		connectURL(Method.POST, listener, errorListener, url, params);
	}

	/**
	 * 获取followed companies
	 * 
	 * @param aPage
	 *            page number (>=1)
	 * @param callback
	 */
	public void getFollowedCompanies(int aPage, String industryid, int type, Boolean onlyBasicInfo, Listener<JSONObject> listener, ErrorListener errorListener) {
		String url = apiRootPath + "member/me/company/get_followed";
		HashMap<String, String> params = new HashMap<String, String>();
		addBasicParams(params);
		params.put("only_basic_info", onlyBasicInfo + "");
		if (industryid.isEmpty()) industryid = "0";
		long industryId = Long.parseLong(industryid);
		if (industryId <= 0) {
			industryId = -1;
		} else if (industryId > 0) {
			params.put("industryid", industryId + "");
		}
		
		params.put("type", type + "");//GET ALL 3; GET PENDING 2; GET ALL EXCEPT 1
		
		if (aPage > 0) params.put("page", aPage + "");
		connectURL(Method.GET, listener, errorListener, url, params);
	}

	/**
	 * 获取company update详情
	 * 
	 * @param aNewsID
	 *            update的ID
	 * @param aIncludeCompetitors
	 *            是否包含competitors
	 * @param callback
	 */
	public void getCompanyUpdateDetail(long aNewsID,
			Boolean aIncludeCompetitors, Listener<JSONObject> listener, ErrorListener errorListener) {
		String url = apiRootPath + "update/" + aNewsID + "/detail";
		HashMap<String, String> params = new HashMap<String, String>();
		addBasicParams(params);
		params.put("format", "text");
		params.put("include_competitors", (aIncludeCompetitors ? "true" : "false"));
		connectURL(Method.GET, listener, errorListener, url, params);
	}

	/**
	 * 获取推荐公司列表
	 * 
	 * @param aPageNumber
	 * @param callback
	 */
	public void getRecommendedCompanies(long aPageNumber, Boolean onlyBasic, 
			Listener<JSONObject> listener, ErrorListener errorListener) {
		String url = apiRootPath + "member/me/company/get_recommended";
		HashMap<String, String> params = new HashMap<String, String>();
		addBasicParams(params);
		if (aPageNumber > 0)
			params.put("page", aPageNumber + "");
		params.put("only_basic_info", onlyBasic + "");
		connectURL(Method.GET, listener, errorListener, url, params);
	}

	/**
	 * 新增公司
	 * 
	 * @param aCompanyName
	 * @param callback
	 */
	public void addNewCompany(String aCompanyName, Listener<JSONObject> listener, ErrorListener errorListener) {
		String url = apiRootPath + "member/me/tempcompany/add";
		HashMap<String, String> params = new HashMap<String, String>();
		addBasicParams(params);
		params.put("org_name", aCompanyName);
		params.put("follow", "true");
		connectURL(Method.POST, listener, errorListener, url, params);
	}

	/**
	 * 获取菜单
	 * 
	 * @param aType
	 *            类型，可传入的值为"companies","people"
	 * @param aPageNumber
	 * @param callback
	 */
	public void getMenu(String aType, int aPageNumber, Listener<JSONObject> listener, ErrorListener errorListener) {
		String url = apiRootPath + "member/me/tracker/menu";
		HashMap<String, String> params = new HashMap<String, String>();
		addBasicParams(params);
		params.put("type", aType);
		if (aPageNumber > 0)
			params.put("page", aPageNumber + "");
		connectURL(Method.GET, listener, errorListener, url, params);
	}

	/**
	 * 获取联系人Happening
	 * 
	 * @param aPersonID
	 * @param aEventID
	 * @param aPageFlag
	 * @param aPageTime
	 * @param aIncludeCompetitors
	 * @param callback
	 */
	public void getHappeningsForThePerson(long aPersonID, long aEventID,
			byte aPageFlag, long aPageTime, Boolean aIncludeCompetitors,
			Listener<JSONObject> listener, ErrorListener errorListener) {
		getHappenings(APIHttpMetadata.EGGApiHappeningTypePerson, aPersonID,
				aEventID, aPageFlag, aPageTime, aIncludeCompetitors, listener, errorListener);
	}

	/**
	 * 获取function area happening
	 * 
	 * @param aAreaID
	 * @param aEventID
	 * @param aPageFlag
	 * @param aPageTime
	 * @param aIncludeCompetitors
	 * @param callback
	 */
	public void getHappeningsForTheFunctionArea(long aAreaID, long aEventID,
			byte aPageFlag, long aPageTime, Boolean aIncludeCompetitors,
			Listener<JSONObject> listener, ErrorListener errorListener) {
		getHappenings(APIHttpMetadata.EGGApiHappeningTypeFunctionalArea,
				aAreaID, aEventID, aPageFlag, aPageTime, aIncludeCompetitors, listener, errorListener);
	}

	/**
	 * 获取category happening
	 * 
	 * @param aCategoryID
	 * @param aEventID
	 * @param aPageFlag
	 * @param aPageTime
	 * @param aIncludeCompetitors
	 * @param callback
	 */
	public void getHappeningsForTheCategory(long aCategoryID, long aEventID,
			byte aPageFlag, long aPageTime, Boolean aIncludeCompetitors,
			Listener<JSONObject> listener, ErrorListener errorListener) {
		getHappenings(APIHttpMetadata.EGGApiHappeningTypeCategory, aCategoryID,
				aEventID, aPageFlag, aPageTime, aIncludeCompetitors, listener, errorListener);
	}

	/**
	 * 获取公司事件详情
	 * 
	 * @param aEventID
	 * @param aIncludeCompetitors
	 * @param callback
	 */
	public void getCompanyEventDetail(long aEventID,
			Boolean aIncludeCompetitors, Listener<JSONObject> listener, ErrorListener errorListener) {
		String url = apiRootPath + "company/event/" + aEventID + "/detail";
		HashMap<String, String> params = new HashMap<String, String>();
		addBasicParams(params);
		params.put("msg_format", "text");
		params.put("include_competitors", (aIncludeCompetitors ? "true" : "false"));
		params.put("no_proxy", "true");
		connectURL(Method.GET, listener, errorListener, url, params);
	}

	/**
	 * 获取联系人事件详情
	 * 
	 * @param aEventID
	 * @param aIncludeCompetitors
	 * @param callback
	 */
	public void getPersonEventDetail(long aEventID,
			Boolean aIncludeCompetitors, Listener<JSONObject> listener, ErrorListener errorListener) {
		String url = apiRootPath + "contact/event/" + aEventID + "/detail";
		HashMap<String, String> params = new HashMap<String, String>();
		addBasicParams(params);
		params.put("msg_format", "text");
		params.put("include_competitors", (aIncludeCompetitors ? "true" : "false"));
		params.put("no_proxy", "true");
		connectURL(Method.GET, listener, errorListener, url, params);
	}

	/**
	 * 保存update
	 * 
	 * @param anUpdateID
	 * @param callback
	 */
	public void saveUpdate(long anUpdateID, Listener<JSONObject> listener, ErrorListener errorListener) {
		String url = apiRootPath + "member/me/update/save";
		HashMap<String, String> params = new HashMap<String, String>();
		addBasicParams(params);
		params.put("newsid", anUpdateID + "");
		connectURL(Method.POST, listener, errorListener, url, params);
	}

	/**
	 * unsave an update
	 * 
	 * @param anUpdateID
	 * @param callback
	 */
	public void unsaveUpdate(long anUpdateID, Listener<JSONObject> listener, ErrorListener errorListener) {
		String url = apiRootPath + "member/me/update/unsave";
		HashMap<String, String> params = new HashMap<String, String>();
		addBasicParams(params);
		params.put("newsid", anUpdateID + "");
		connectURL(Method.POST, listener, errorListener, url, params);
	}

	/**
	 * 获得保存的update列表
	 * 
	 * @param aPageNumber
	 * @param aIsUnread
	 * @param callback
	 */
	public void getSavedUpdates(int aPageNumber, Boolean aIsUnread,
			Listener<JSONObject> listener, ErrorListener errorListener) {
		String url = apiRootPath + "member/me/update/get_saved";
		HashMap<String, String> params = new HashMap<String, String>();
		addBasicParams(params);
		params.put("page", aPageNumber + "");
		params.put("type", (aIsUnread ? "unread" : "all"));
		params.put("no_proxy", "true");
		connectURL(Method.POST, listener, errorListener, url, params);
	}

	/**
	 * like an update
	 * 
	 * @param anUpdateID
	 * @param callback
	 */
	public void likeUpdate(long anUpdateID, Listener<JSONObject> listener, ErrorListener errorListener) {
		String url = apiRootPath + "member/me/update/like";
		HashMap<String, String> params = new HashMap<String, String>();
		addBasicParams(params);
		params.put("newsid", anUpdateID + "");
		connectURL(Method.POST, listener, errorListener, url, params);
	}

	/**
	 * unlike an update
	 * 
	 * @param anUpdateID
	 * @param callback
	 */
	public void unlikeUpdate(long anUpdateID, Listener<JSONObject> listener, ErrorListener errorListener) {
		String url = apiRootPath + "member/me/update/unlike";
		HashMap<String, String> params = new HashMap<String, String>();
		addBasicParams(params);
		params.put("newsid", anUpdateID + "");
		connectURL(Method.POST, listener, errorListener, url, params);
	}

	/**
	 * 向linkedIn 联系人发送消息
	 * 
	 * @param aLinkedInID
	 *            联系人linkedIn的ID
	 * @param aSubject
	 *            消息主题
	 * @param aMessage
	 *            消息内容
	 * @param callback
	 */
	public void linkedInSendMessage(String aLinkedInID, String aSubject,
			String aMessage, Listener<JSONObject> listener, ErrorListener errorListener) {
		String url = apiRootPath + "member/me/contact/send_message";
		HashMap<String, String> params = new HashMap<String, String>();
		addBasicParams(params);
		params.put("contact_linkedinid", aLinkedInID);
		params.put("subject", aSubject);
		params.put("message", aMessage);
		connectURL(Method.POST, listener, errorListener, url, params);
	}

	/**
	 * 向linkedIn联系人发送connect请求
	 * 
	 * @param aLinkedInID
	 *            联系人linkedIn的ID
	 * @param aSubject
	 *            消息主题
	 * @param aMessage
	 *            消息内容
	 * @param callback
	 */
	public void linkedInSendInvitation(String aLinkedInID, String aSubject,
			String aMessage, Listener<JSONObject> listener, ErrorListener errorListener) {
		String url = apiRootPath + "member/me/contact/send_connect";
		HashMap<String, String> params = new HashMap<String, String>();
		addBasicParams(params);
		params.put("contact_linkedinid", aLinkedInID);
		if (!TextUtils.isEmpty(aSubject)) params.put("subject", aSubject);
		params.put("message", aMessage);
		connectURL(Method.POST, listener, errorListener, url, params);
	}

	/**
	 * 关注一个联系人
	 * 
	 * @param aPersonID
	 * @param callback
	 */
	public void followPerson(long aPersonID, Listener<JSONObject> listener, ErrorListener errorListener) {
		String url = apiRootPath + "member/me/contact/follow";
		HashMap<String, String> params = new HashMap<String, String>();
		addBasicParams(params);
		params.put("contactid", aPersonID + "");
		connectURL(Method.GET, listener, errorListener, url, params);
	}

	/**
	 * 取消关注一个联系人
	 * 
	 * @param aPersonID
	 * @param callback
	 */
	public void unfollowPerson(long aPersonID, Listener<JSONObject> listener, ErrorListener errorListener) {
		String url = apiRootPath + "member/me/contact/unfollow";
		HashMap<String, String> params = new HashMap<String, String>();
		addBasicParams(params);
		params.put("contactid", aPersonID + "");
		connectURL(Method.GET, listener, errorListener, url, params);
	}

	/**
	 * 获取followed联系人列表
	 * 
	 * @param aPageNumber
	 * @param callback
	 */
	public void getFollowedPeople(int aPageNumber, Listener<JSONObject> listener, ErrorListener errorListener) {
		String url = apiRootPath + "member/me/contact/get_followed";
		HashMap<String, String> params = new HashMap<String, String>();
		addBasicParams(params);
		if (aPageNumber > 0)
			params.put("page", aPageNumber + "");
		params.put("only_basic_info", "true");
		params.put("no_proxy", "true");
		connectURL(Method.GET, listener, errorListener, url, params);
	}

	/**
	 * 获取联系人概要信息
	 * 
	 * @param aPersonID
	 * @param aMergeCompanies
	 *            控制是把有效的公司排前面，如果=true,就会去掉重复
	 * @param callback
	 */
	public void getPersonOverview(long aPersonID, Boolean aMergeCompanies,
			Listener<JSONObject> listener, ErrorListener errorListener) {
		String url = apiRootPath + "contact/" + aPersonID + "/overview";
		HashMap<String, String> params = new HashMap<String, String>();
		addBasicParams(params);
		params.put("no_proxy", "true");
		params.put("exclude_current_company", "false"); // 控制是否包含当前公司
		params.put("order_valid_company_first", (aMergeCompanies ? "true" : "false")); // 控制是把有效的公司排前面，如果=true,就会去掉重复
		connectURL(Method.GET, listener, errorListener, url, params);
	}

	/**
	 * 获取我的资料
	 * 
	 * @param callback
	 */
	public void getMyOverview(Listener<JSONObject> listener, ErrorListener errorListener) {
		String url = apiRootPath + "member/me/overview";
		HashMap<String, String> params = new HashMap<String, String>();
		addBasicParams(params);
		params.put("memid", "me");
		connectURL(Method.GET, listener, errorListener, url, params);
	}

	/**
	 * 发送会员升级邮件
	 * 
	 * @param callback
	 */
	public void sendUpgradeLink(Listener<JSONObject> listener, ErrorListener errorListener) {
		String url = apiRootPath + "config/plan/send_link";
		HashMap<String, String> params = new HashMap<String, String>();
		addBasicParams(params);
		params.put("memid", "me");
		connectURL(Method.GET, listener, errorListener, url, params);
	}

	/**
	 * 获取agent列表
	 * 
	 * @param callback
	 */
	public void getAgents(Listener<JSONObject> listener, ErrorListener errorListener) {
		String url = apiRootPath + "config/plan/send_link";
		HashMap<String, String> params = new HashMap<String, String>();
		addBasicParams(params);
		connectURL(Method.GET, listener, errorListener, url, params);
	}

	/**
	 * 拼装数组数据
	 * 
	 * @param aValues
	 * @param aKey
	 * @return
	 */
	private String stringForMultipleValuesToOneKey(List<String> aValues,
			String aKey) {

		if (aValues == null || aValues.isEmpty() || aKey == null || aKey.length() <= 0)
			return null;

		String result = "";
		int i = 0;
		for (String value : aValues) {
			if (i != 0)
				result = result + "&" + aKey + "=";
			result += value;
			i++;
		}
		return result;
	}

	/**
	 * select agent(one or more)
	 * 
	 * @param aAgentIDs
	 * @param callback
	 */
	public void saveAgents(List<String> aAgentIDs, Listener<JSONObject> listener, ErrorListener errorListener) {
		String url = apiRootPath + "config/sales_trigger/save";
		HashMap<String, String> params = new HashMap<String, String>();
		addBasicParams(params);

		Log.v("silen", "aAgentIDs.size() = " + aAgentIDs.size());
		String key = "agentid";
		if (aAgentIDs.size() != 0) {
			String value = stringForMultipleValuesToOneKey(aAgentIDs, key);
			if (value != null) {
				params.put(key, value);
			}
		} else {
			params.put(key, "-1");
		}
		connectURLNoEncode(Method.POST, listener, errorListener, url, params);
	}

	/**
	 * 获取functional area列表
	 * 
	 * @param callback
	 */
	public void getFunctionAreas(Listener<JSONObject> listener, ErrorListener errorListener) {
		String url = apiRootPath + "config/functional_area/list";
		HashMap<String, String> params = new HashMap<String, String>();
		addBasicParams(params);
		connectURL(Method.GET, listener, errorListener, url, params);
	}

	/**
	 * select functional area (one or more)
	 * 
	 * @param aAreaIDs
	 * @param callback
	 */
	public void saveFunctionalAreas(List<String> aAreaIDs,
			Listener<JSONObject> listener, ErrorListener errorListener) {
		String url = apiRootPath + "config/functional_area/save";
		HashMap<String, String> params = new HashMap<String, String>();
		addBasicParams(params);

		String key = "functional_areaid";
		String value = stringForMultipleValuesToOneKey(aAreaIDs, key);
		if (value != null)
			params.put(key, value);
		connectURLNoEncode(Method.POST, listener, errorListener, url, params);
	}

	/**
	 * getfilter agent list
	 * 
	 * @param callback
	 */
	public void getAgentFilters(Listener<JSONObject> listener, ErrorListener errorListener) {
		String url = apiRootPath + "config/filters/agent/list";
		HashMap<String, String> params = new HashMap<String, String>();
		addBasicParams(params);
		connectURL(Method.GET, listener, errorListener, url, params);
	}

	/**
	 * get filter relevance value
	 * 
	 * @param callback
	 */
	public void getFilterRelevance(Listener<JSONObject> listener, ErrorListener errorListener) {
		String url = apiRootPath + "config/filters/relevance";
		HashMap<String, String> params = new HashMap<String, String>();
		addBasicParams(params);
		connectURL(Method.GET, listener, errorListener, url, params);
	}

	/**
	 * modify filter relevance value
	 * 
	 * @param aRelevance
	 * @param callback
	 */
	public void setFilterRelevance(int aRelevance, Listener<JSONObject> listener, ErrorListener errorListener) {
		String url = apiRootPath + "config/filters/relevance/change";
		HashMap<String, String> params = new HashMap<String, String>();
		addBasicParams(params);
		params.put("relevance", aRelevance + "");
		connectURL(Method.POST, listener, errorListener, url, params);
	}

	/**
	 * 个人资料修改 -- 姓名
	 * 
	 * @param aFirstName
	 * @param aLastName
	 * @param callback
	 */
	public void changeProfileName(String aFirstName, String aLastName,
			Listener<JSONObject> listener, ErrorListener errorListener) {
		String url = apiRootPath + "member/me/info/update";
		HashMap<String, String> params = new HashMap<String, String>();
		addBasicParams(params);
		params.put("mem_first_name", aFirstName);
		params.put("mem_last_name", aLastName);
		connectURL(Method.POST, listener, errorListener, url, params);
	}

	public void changeProfileEmail(String aEmail, Listener<JSONObject> listener, ErrorListener errorListener) {
		String url = apiRootPath + "member/me/info/update";
		HashMap<String, String> params = new HashMap<String, String>();
		addBasicParams(params);
		params.put("mem_email", aEmail);
		connectURL(Method.POST, listener, errorListener, url, params);
	}

	public void changeProfileJobTitle(String aJobTitle, Listener<JSONObject> listener, ErrorListener errorListener) {
		String url = apiRootPath + "member/me/info/update";
		HashMap<String, String> params = new HashMap<String, String>();
		addBasicParams(params);
		params.put("mem_org_title", aJobTitle);
		connectURL(Method.POST, listener, errorListener, url, params);
	}

	public void changeProfileTimezone(String aTimezone, Listener<JSONObject> listener, ErrorListener errorListener) {
		String url = apiRootPath + "member/me/info/update";
		HashMap<String, String> params = new HashMap<String, String>();
		addBasicParams(params);
		params.put("mem_add_timezone", aTimezone);
		connectURL(Method.POST, listener, errorListener, url, params);
	}

	public void changeProfileCompany(long aCompanyID, Listener<JSONObject> listener, ErrorListener errorListener) {
		String url = apiRootPath + "member/me/info/update";
		HashMap<String, String> params = new HashMap<String, String>();
		addBasicParams(params);
		params.put("orgid", aCompanyID + "");
		connectURL(Method.POST, listener, errorListener, url, params);
	}

	public void changeProfileCompany(String aCompanyName,
			Listener<JSONObject> listener, ErrorListener errorListener) {
		String url = apiRootPath + "member/me/info/update";
		HashMap<String, String> params = new HashMap<String, String>();
		addBasicParams(params);
		params.put("org_name", aCompanyName);
		connectURL(Method.POST, listener, errorListener, url, params);
	}
	
	public void changeProfile(String name, String aEmail, String aCompanyName, String aJobTitle,
			Listener<JSONObject> listener, ErrorListener errorListener) {
		String url = apiRootPath + "member/me/info/update";
		HashMap<String, String> params = new HashMap<String, String>();
		addBasicParams(params);
		params.put("mem_first_name", name);
		params.put("mem_last_name", "");
		params.put("mem_email", aEmail);
		params.put("org_name", aCompanyName);
		params.put("mem_org_title", aJobTitle);
		connectURL(Method.POST, listener, errorListener, url, params);
	}
	
	public void changeProfile(String firstName, String lastName, String aEmail, String aCompanyName, String aJobTitle,
			Listener<JSONObject> listener, ErrorListener errorListener) {
		String url = apiRootPath + "member/me/info/update";
		HashMap<String, String> params = new HashMap<String, String>();
		addBasicParams(params);
		params.put("mem_first_name", firstName);
		params.put("mem_last_name", lastName);
		params.put("mem_email", aEmail);
		params.put("org_name", aCompanyName);
		params.put("mem_org_title", aJobTitle);
		connectURL(Method.POST, listener, errorListener, url, params);
	}

	/**
	 * 使用社交网络账号注册Gagein账号
	 * 
	 * @param aEmail
	 * @param aPassword
	 * @param aFirstName
	 * @param aLastName
	 * @param aSnType
	 * @param aSnToken
	 * @param aSnSecret
	 * @param aSnAccountID
	 * @param aSnFirstName
	 * @param aSnLastName
	 * @param aSnEmail
	 * @param aSnAccountName
	 * @param aSnProfileURL
	 * @param aSfRefreshToken
	 * @param aSfInstanceURL
	 * @param aTimezone
	 * @param callback
	 */
	public void userRegister(String aEmail, String aPassword,
			String aFirstName, String aLastName, byte aSnType, String aSnToken,
			String aSnSecret, String aSnAccountID, String aSnFirstName,
			String aSnLastName, String aSnEmail, String aSnAccountName,
			String aSnProfileURL, String aSfRefreshToken,
			String aSfInstanceURL, String aTimezone, Listener<JSONObject> listener, ErrorListener errorListener) {
		String url = apiRootPath + "register";
		HashMap<String, String> params = new HashMap<String, String>();
		addBasicParams(params);
		params.put("mem_email", aEmail);
		params.put("mem_password", aPassword);
		params.put("mem_first_name", aFirstName);
		params.put("mem_last_name", aLastName);
		params.put("sn_type", aSnType + "");
		params.put("sn_token", aSnToken);
		params.put("sn_secret", aSnSecret);
		params.put("sn_account_id", aSnAccountID);
		params.put("sn_first_name", aSnFirstName);
		params.put("sn_last_name", aSnLastName);
		params.put("sn_email", aSnEmail);
		params.put("sn_account_name", aSnAccountName);
		params.put("sn_profile_url", aSnProfileURL);
		params.put("mem_timezone", aTimezone);

		if (aSnType == APIHttpMetadata.kGGSnTypeSalesforce) {
			params.put("sn_refresh_token", aSfRefreshToken);
			params.put("sn_instance_url", aSfInstanceURL);
		}

		connectURL(Method.POST, listener, errorListener, url, params);
	}

	/**
	 * 获取社交网络用户信息(用于注册)
	 * 
	 * @param aSnType
	 * @param aSnToken
	 * @param aSnSecret
	 * @param aSfAccountID
	 * @param aSfRefreshToken
	 * @param aSfInstanceUrl
	 * @param callback
	 */
	public void snGetUserInfo(int aSnType, String aSnToken, String aSnSecret,
			String aSfAccountID, String aSfRefreshToken, String aSfInstanceUrl,
			Listener<JSONObject> listener, ErrorListener errorListener) {
		String url = apiRootPath + "socialnetwork/get_userinfo";
		HashMap<String, String> params = new HashMap<String, String>();
		addBasicParams(params);

		params.put("sn_type", aSnType + "");
		params.put("sn_token", aSnToken);
		params.put("include_login_info", "true");
		if(aSnSecret != null) params.put("sn_secret", aSnSecret);

		if (aSnType == APIHttpMetadata.kGGSnTypeSalesforce) {
			if(aSfAccountID != null) params.put("sn_account_id", aSfAccountID);
			if(aSfRefreshToken != null) params.put("sn_refresh_token", aSfRefreshToken);
			if(aSfInstanceUrl != null) params.put("sn_instance_url", aSfInstanceUrl);
		}

		connectURL(Method.GET, listener, errorListener, url, params);
	}
	
	public void snGetUserInfoSalesforce(String aSnToken
			, String aSfAccountID, String aSfRefreshToken, String aSfInstanceUrl, Listener<JSONObject> listener, ErrorListener errorListener) {
		snGetUserInfo(APIHttpMetadata.kGGSnTypeSalesforce, aSnToken, null, aSfAccountID, aSfRefreshToken, aSfInstanceUrl, listener, errorListener);
	}
	
	public void snGetUserInfoLinkedIn(String aSnToken, String aSnSecret, Listener<JSONObject> listener, ErrorListener errorListener) {
		snGetUserInfo(APIHttpMetadata.kGGSnTypeLinkedIn, aSnToken, aSnSecret, null, null, null, listener, errorListener);
	}
	
	public void snGetUserInfoTwitter(String aSnToken, String aSnSecret, Listener<JSONObject> listener, ErrorListener errorListener) {
		snGetUserInfo(APIHttpMetadata.kGGSnTypeTwitter, aSnToken, aSnSecret, null, null, null, listener, errorListener);
	}
	
	public void snGetUserInfoFacebook(String aSnToken, Listener<JSONObject> listener, ErrorListener errorListener) {
		snGetUserInfo(APIHttpMetadata.kGGSnTypeFacebook, aSnToken, null, null, null, null, listener, errorListener);
	}

	/**
	 * 保存社交网络登录信息
	 * 
	 * @param aSnType
	 * @param aSnToken
	 * @param aSnSecret
	 * @param aSfAccountID
	 * @param aSfRefreshToken
	 * @param aSfInstanceUrl
	 * @param callback
	 */
	public void snSaveInfo(int aSnType, String aSnToken, String aSnSecret,
			String aSfAccountID, String aSfRefreshToken, String aSfInstanceUrl,
			Listener<JSONObject> listener, ErrorListener errorListener) {
		String url = apiRootPath + "socialnetwork/save_sn_info";
		HashMap<String, String> params = new HashMap<String, String>();
		addBasicParams(params);

		params.put("sn_type", aSnType + "");
		if (aSnToken != null) params.put("sn_token", aSnToken);
		if (aSnSecret != null) params.put("sn_secret", aSnSecret);

		if (aSnType == APIHttpMetadata.kGGSnTypeSalesforce) {
			params.put("sn_account_id", aSfAccountID);
			params.put("sn_refresh_token", aSfRefreshToken);
			params.put("sn_instance_url", aSfInstanceUrl);
		}
		connectURL(Method.POST, listener, errorListener, url, params);
	}
	
	/** save salesforce oauth info*/
	public void snSaveSalesforce(String aSnToken, String aSfAccountID, String aSfRefreshToken, String aSfInstanceUrl, Listener<JSONObject> listener, ErrorListener errorListener) {
		snSaveInfo(APIHttpMetadata.kGGSnTypeSalesforce, aSnToken, null, aSfAccountID, aSfRefreshToken, aSfInstanceUrl, listener, errorListener);
	}
	
	/** save linedin oauth info*/
	public void snSaveLinkedIn(String aSnToken, String aSnSecret, Listener<JSONObject> listener, ErrorListener errorListener) {
		snSaveInfo(APIHttpMetadata.kGGSnTypeLinkedIn, aSnToken, aSnSecret, null, null, null, listener, errorListener);
	}
	
	/** save twitter oauth info*/
	public void snSaveTwitter(String aSnToken, String aSnSecret, Listener<JSONObject> listener, ErrorListener errorListener) {
		snSaveInfo(APIHttpMetadata.kGGSnTypeTwitter, aSnToken, aSnSecret, null, null, null, listener, errorListener);
	}
	
	/** save facebook oauth info*/
	public void snSaveFacebook(String aSnToken, Listener<JSONObject> listener, ErrorListener errorListener) {
		snSaveInfo(APIHttpMetadata.kGGSnTypeFacebook, aSnToken, null, null, null, null, listener, errorListener);
	}

	/**
	 * 获得已连接的社交网络列表
	 * 
	 * @param callback
	 */
	public void snGetList(Listener<JSONObject> listener, ErrorListener errorListener) {
		String url = apiRootPath + "socialnetwork/linked/list";
		HashMap<String, String> params = new HashMap<String, String>();
		addBasicParams(params);
		connectURL(Method.GET, listener, errorListener, url, params);
	}

	/**
	 * 分享update到社交网络
	 * 
	 * @param aUpdateID
	 * @param aSnType
	 * @param aMessage
	 * @param aHeadLine
	 * @param aSummary
	 * @param aPictureUrl
	 * @param callback
	 */
	public void snShareUpdate(long aUpdateID, int aSnType, String aMessage,
			String aHeadLine, String aSummary, String aPictureUrl,
			Listener<JSONObject> listener, ErrorListener errorListener) {
		String url = apiRootPath + "member/me/update/" + aUpdateID + "/share";
		HashMap<String, String> params = new HashMap<String, String>();
		addBasicParams(params);
		params.put("sn_type", aSnType + "");
		params.put("message", aMessage);
		aHeadLine = "Didn’t limit Google-hire";//TODO
		try {
			aHeadLine = URLEncoder.encode(aHeadLine, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		params.put("title", aHeadLine);
		params.put("summary", aSummary);
		params.put("picture", aPictureUrl);
		connectURL(Method.POST, listener, errorListener, url, params);
	}

	/**
	 * 分享公司event到社交网络
	 * 
	 * @param aSnType
	 * @param aEventID
	 * @param aMessage
	 * @param callback
	 */
	public void snShareCompanyEvent(int aSnType, long aEventID,
			String aMessage, Listener<JSONObject> listener, ErrorListener errorListener) {
		String url = apiRootPath + "member/me/company/event/" + aEventID + "/share";
		HashMap<String, String> params = new HashMap<String, String>();
		addBasicParams(params);
		params.put("sn_type", aSnType + "");
		params.put("message", aMessage);
		connectURL(Method.POST, listener, errorListener, url, params);
	}

	/**
	 * 分享联系人event到社交网络
	 * 
	 * @param aSnType
	 * @param aEventID
	 * @param aMessage
	 * @param callback
	 */
	public void snSharePersonEvent(int aSnType, long aEventID,
			String aMessage, Listener<JSONObject> listener, ErrorListener errorListener) {
		String url = apiRootPath + "member/me/contact/event/" + aEventID + "/share";
		HashMap<String, String> params = new HashMap<String, String>();
		addBasicParams(params);
		params.put("sn_type", aSnType + "");
		params.put("message", aMessage);
		connectURL(Method.POST, listener, errorListener, url, params);
	}

	/**
	 * 分享消息
	 * 
	 * @param aSnType
	 * @param aMessage
	 * @param callback
	 */
	public void snShare(int aSnType, String aMessage, Listener<JSONObject> listener, ErrorListener errorListener) {
		String url = apiRootPath + "member/me/message/share";
		HashMap<String, String> params = new HashMap<String, String>();
		addBasicParams(params);
		params.put("sn_type", aSnType + "");
		params.put("message", aMessage);
		connectURL(Method.POST, listener, errorListener, url, params);
	}

	/**
	 * 从社交网络导入公司
	 * 
	 * @param aSnType
	 * @param callback
	 */
	public void importCompanies(int aSnType, Listener<JSONObject> listener, ErrorListener errorListener) {
		String url = apiRootPath + "socialnetwork/import_companies";
		HashMap<String, String> params = new HashMap<String, String>();
		addBasicParams(params);
		params.put("sn_type", aSnType + "");
		connectURL(Method.GET, listener, errorListener, url, params);
	}
	
	public void selectAgentFilterWithID(long aFilterID, boolean aSelected,  Listener<JSONObject> listener, ErrorListener errorListener) {
		String url = apiRootPath + "config/filters/agent/" + aFilterID + "/" + (aSelected ? "true" : "false");
		HashMap<String, String> params = new HashMap<String, String>();
		addBasicParams(params);
		connectURL(Method.POST, listener, errorListener, url, params);
	}
	
	public void addCompanyWebsite(long aCompanyId,String name, String aWebsite, Boolean forceAdd, Listener<JSONObject> listener, ErrorListener errorListener) {
		String url = apiRootPath + "member/me/company/" + aCompanyId + "/add_website";
		HashMap<String, String> params = new HashMap<String, String>();
		addBasicParams(params);
		params.put("org_name", name);
		params.put("org_website", aWebsite);
		params.put("force_add", forceAdd + "");
		connectURL(Method.POST, listener, errorListener, url, params);
	}
	
	// Get Filter Options API
	public void getFilter(Listener<JSONObject> listener, ErrorListener errorListener) {
		String url = apiRootPath + "search/options";
		HashMap<String, String> params = new HashMap<String, String>();
		addBasicParams(params);
		connectURL(Method.GET, listener, errorListener, url, params);
	}
	
	public void searchAgent(String keywords, Listener<JSONObject> listener, ErrorListener errorListener) {
		String url = apiRootPath + "search/agent_keywords_auto_complete";
		HashMap<String, String> params = new HashMap<String, String>();
		addBasicParams(params);
		params.put("keywords", keywords);
		connectURL(Method.GET, listener, errorListener, url, params);
	}
	
	/**
	 * 
	 * @param keywords
	 * @param locationType  0 表示 company location， 1 表示contact location
	 * @param listener
	 * @param errorListener
	 */
	public void searchLocation(String keywords, String locationType, Listener<JSONObject> listener, ErrorListener errorListener) {
		String url = apiRootPath + "search/location_auto_complete";
		HashMap<String, String> params = new HashMap<String, String>();
		addBasicParams(params);
		params.put("keywords", keywords);
		params.put("location_type", locationType);
		connectURL(Method.GET, listener, errorListener, url, params);
	}
	
	public void getSavedCompanySearchesWithPage(int pageNum, Listener<JSONObject> listener, ErrorListener errorListener) {
		String url = apiRootPath + "search/companies/get_saved_searches";
		HashMap<String, String> params = new HashMap<String, String>();
		addBasicParams(params);
		params.put("page", pageNum + "");
		connectURL(Method.GET, listener, errorListener, url, params);
	}
	
	public void getSavedPeopleSearchesWithPage(int pageNum, Listener<JSONObject> listener, ErrorListener errorListener) {
		String url = apiRootPath + "search/contacts/get_saved_searches";
		HashMap<String, String> params = new HashMap<String, String>();
		addBasicParams(params);
		params.put("page", pageNum + "");
		connectURL(Method.GET, listener, errorListener, url, params);
	}
	
	public void getSavedSearchesWithPage(int pageNum, Listener<JSONObject> listener, ErrorListener errorListener) {
		String url = apiRootPath + "search/get_saved_searches";
		HashMap<String, String> params = new HashMap<String, String>();
		addBasicParams(params);
		params.put("page", pageNum + "");
		connectURL(Method.GET, listener, errorListener, url, params);
	}
	
	public void searchAdvancedCompanies(int pageNum, String filterJsonStr, Listener<JSONObject> listener, ErrorListener errorListener) {//TODO
		String url = apiRootPath + "search/advanced/companies";
		HashMap<String, String> params = new HashMap<String, String>();
		addBasicParams(params);
		params.put("query_info", filterJsonStr);
		Log.v("silen", "RequestData = " + filterJsonStr);
		params.put("need_check_more", "true");
		params.put("page", pageNum + "");
		connectURL(Method.POST, listener, errorListener, url, params);
	}
	
	public void searchAdvancedPersons(int pageNum, String filterJsonStr, Listener<JSONObject> listener, ErrorListener errorListener) {//TODO
		String url = apiRootPath + "search/advanced/contacts";
		HashMap<String, String> params = new HashMap<String, String>();
		addBasicParams(params);
		params.put("query_info", filterJsonStr);
		params.put("need_check_more", "true");
		params.put("page", pageNum + "");
		connectURL(Method.POST, listener, errorListener, url, params);
	}
	
	public void saveSearchCompanies(String searchName, String queryInfo, Listener<JSONObject> listener, ErrorListener errorListener) {//TODO
		String url = apiRootPath + "search/advanced/companies/save_search";
		HashMap<String, String> params = new HashMap<String, String>();
		addBasicParams(params);
		params.put("search_name", searchName);
		params.put("query_info", queryInfo);
		connectURL(Method.POST, listener, errorListener, url, params);
	}
	
	public void saveSearchPersons(String searchName, String queryInfo, Listener<JSONObject> listener, ErrorListener errorListener) {//TODO
		String url = apiRootPath + "search/advanced/contacts/save_search";
		HashMap<String, String> params = new HashMap<String, String>();
		addBasicParams(params);
		params.put("search_name", searchName);
		params.put("query_info", queryInfo);
		connectURL(Method.POST, listener, errorListener, url, params);
	}
	
	public void deleteSavedSearch(String searchId, Listener<JSONObject> listener, ErrorListener errorListener) {//TODO
		String url = apiRootPath + "search/delete_saved_search";
		HashMap<String, String> params = new HashMap<String, String>();
		addBasicParams(params);
		params.put("saved_searchid", searchId);
		connectURL(Method.POST, listener, errorListener, url, params);
	}
	
	public void getSavedSearch(String searchId, int pageNum, Listener<JSONObject> listener, ErrorListener errorListener) {
		String url = apiRootPath + "search/search_by_saved_search";
		HashMap<String, String> params = new HashMap<String, String>();
		addBasicParams(params);
		params.put("saved_searchid", searchId);
		params.put("page", pageNum + "");
		connectURL(Method.GET, listener, errorListener, url, params);
	}
	
	public void getSecFilings(long companyId, Listener<JSONObject> listener, ErrorListener errorListener) {
		String url = apiRootPath + "company/" + companyId + "/filing_updates";
		HashMap<String, String> params = new HashMap<String, String>();
		addBasicParams(params);
		params.put("companyId", companyId + "");
		params.put("no_proxy", true + "");
		connectURL(Method.GET, listener, errorListener, url, params);
	}
	
	public void sendFeedback(int category, String importance, String message, String summary, Listener<JSONObject> listener, ErrorListener errorListener) {
		String url = apiRootPath + "member/me/send_feedback";
		HashMap<String, String> params = new HashMap<String, String>();
		addBasicParams(params);
		params.put("category", category + "");
		params.put("importance", importance);
		params.put("message", message);
		params.put("summary", summary);
		params.put("os", "Android");
		
		connectURL(Method.POST, listener, errorListener, url, params);
	}
	
	public void getAllCompanyGroups(Listener<JSONObject> listener, ErrorListener errorListener) {
		String url = apiRootPath + "company/group/all";
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("type", "1");
		addBasicParams(params);
		connectURL(Method.GET, listener, errorListener, url, params);
	}
	
	public void newCompanyGroup(String groupName, Listener<JSONObject> listener, ErrorListener errorListener) {
		String url = apiRootPath + "company/group/add";
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("group_name", groupName);
		addBasicParams(params);
		connectURL(Method.POST, listener, errorListener, url, params);
	}
	
	public void deleteCompanyGroup(String groupId, Listener<JSONObject> listener, ErrorListener errorListener) {
		String url = apiRootPath + "company/group/" + groupId + "/delete";
		HashMap<String, String> params = new HashMap<String, String>();
		addBasicParams(params);
		connectURL(Method.POST, listener, errorListener, url, params);
	}
	
	public void renameCompanyGroup(String groupId, String newName, Listener<JSONObject> listener, ErrorListener errorListener) {
		String url = apiRootPath + "company/group/" + groupId + "/rename";
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("new_group_name", newName);
		addBasicParams(params);
		connectURL(Method.POST, listener, errorListener, url, params);
	}
	
	public void resetAndAddCompanies(String groupId, ArrayList<String> companiesId, Listener<JSONObject> listener, ErrorListener errorListener) {
		String url = apiRootPath + "company/group/" + groupId + "/link_orgs";
		HashMap<String, String> params = new HashMap<String, String>();
		String key = "orgid";
		String value = stringForMultipleValuesToOneKey(companiesId, key);
		params.put(key, value);
		addBasicParams(params);
		connectURLNoEncode(Method.POST, listener, errorListener, url, params);
	}
	
	public void addCompanies(String groupId, ArrayList<String> companiesId, Listener<JSONObject> listener, ErrorListener errorListener) {
		String url = apiRootPath + "company/group/" + groupId + "/add_orgs";
		HashMap<String, String> params = new HashMap<String, String>();
		String key = "orgid";
		String value = stringForMultipleValuesToOneKey(companiesId, key);
		params.put(key, value);
		addBasicParams(params);
		connectURLNoEncode(Method.POST, listener, errorListener, url, params);
	}
	
	public void removeCompanies(String groupId, ArrayList<String> companiesId, Listener<JSONObject> listener, ErrorListener errorListener) {
		String url = apiRootPath + "company/group/" + groupId + "/delete_orgs";
		HashMap<String, String> params = new HashMap<String, String>();
		String key = "orgid";
		String value = stringForMultipleValuesToOneKey(companiesId, key);
		params.put(key, value);
		addBasicParams(params);
		connectURLNoEncode(Method.POST, listener, errorListener, url, params);
	}
	
	public void getCompaniesOfGroup(String nextPage, String groupId, String industryId, int type, Listener<JSONObject> listener, ErrorListener errorListener) {
		
		String url = apiRootPath + "company/group/orgs_by_group";
		
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("type", type + "");//GET ALL 3; GET PENDING 2; GET ALL EXCEPT 1
		if (!groupId.equalsIgnoreCase("0")) params.put("groupid", groupId + "");
		if (Long.parseLong(industryId) > 0) params.put("industryid", industryId + "");
		
		params.put("page", nextPage);
		params.put("limit", 20 + "");
		addBasicParams(params);
		connectURL(Method.GET, listener, errorListener, url, params);
	}
	
	public void getCompaniesOfGroupNew(int followLinkType, String nextPage, String groupId, String industryId, int followedCompanyType, Listener<JSONObject> listener, ErrorListener errorListener) {
		
		String url = apiRootPath + "member/me/company/followed";
		
		HashMap<String, String> params = new HashMap<String, String>();
		if (!groupId.equalsIgnoreCase("0")) params.put("groupid", groupId + "");
		if (Long.parseLong(industryId) > 0 || Long.parseLong(industryId) == -1) params.put("industryid", industryId + "");
		
		params.put("followed_company_type", followedCompanyType + "");
		params.put("follow_link_type", followLinkType + "");
		params.put("page", nextPage);
		addBasicParams(params);
		connectURL(Method.GET, listener, errorListener, url, params);
	}
	
	public void importCompaniesWithContactsDomain(List<Contact> contacts, Listener<JSONObject> listener, ErrorListener errorListener) {
		String url = apiRootPath + "company/match_companies_by_email_domain";
		HashMap<String, String> params = new HashMap<String, String>();
		String key = "email_domain";
		ArrayList<String> emails = new ArrayList<String>();
		for (int i = 0; i < contacts.size(); i ++) {
			
			for (int j = 0; j < contacts.get(i).getEmails().size(); j ++) {
				String email = contacts.get(i).getEmails().get(j);
				Log.v("silen", "email = " + email);
				emails.add(email);
			}
		}
		String value = stringForMultipleValuesToOneKey(emails, key);
		
		if (null != value && TextUtils.isEmpty(value)) {
			params.put(key, value);
		}
		addBasicParams(params);
		connectURLNoEncode(Method.POST, listener, errorListener, url, params);
	}
	
	public void removePendingCompany(ArrayList<String> micids, Listener<JSONObject> listener, ErrorListener errorListener) {
		String url = apiRootPath + "company/delete_temp_companies";
		HashMap<String, String> params = new HashMap<String, String>();
		String key = "micid";
		String value = stringForMultipleValuesToOneKey(micids, key);
		params.put(key, value);
		addBasicParams(params);
		connectURLNoEncode(Method.POST, listener, errorListener, url, params);
	}
	
	public void addNewCompanyWithName(String name, String website, Boolean forceAdd, Listener<JSONObject> listener, ErrorListener errorListener) {
		String url = apiRootPath + "member/me/company/add";
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("org_website", website);
		params.put("org_name", name);
		params.put("force_add", forceAdd + "");
		addBasicParams(params);
		connectURL(Method.POST, listener, errorListener, url, params);
	}
	
	public void getProvisionDateWithCompanyID(long companyId, Listener<JSONObject> listener, ErrorListener errorListener) {
		String url = apiRootPath + "company/get_provision_date";
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("orgid", companyId + "");
		addBasicParams(params);
		connectURL(Method.GET, listener, errorListener, url, params);
	}
	
	public void setNewsToIrrelevant(long newsid, Boolean flag, Listener<JSONObject> listener, ErrorListener errorListener) {
		String url = apiRootPath + "me/updates/" + newsid + "/irrelevant";
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("flag", flag ? "1" : "0");
		addBasicParams(params);
		connectURL(Method.GET, listener, errorListener, url, params);
	}
	
	public void billingGetInfo( Listener<JSONObject> listener, ErrorListener errorListener) {
		String url = apiRootPath + "member/me/subscription";
		HashMap<String, String> params = new HashMap<String, String>();
		addBasicParams(params);
		connectURL(Method.GET, listener, errorListener, url, params);
	}
}

