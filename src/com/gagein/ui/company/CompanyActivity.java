package com.gagein.ui.company;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.gagein.R;
import com.gagein.adapter.CompanyAboutAdapter;
import com.gagein.adapter.CompanyCompetitorsAdapter;
import com.gagein.adapter.CompanyNewsAdapter;
import com.gagein.adapter.CompanyPersonAdapter;
import com.gagein.adapter.SearchCompanyAdapter;
import com.gagein.component.dialog.CommonDialog;
import com.gagein.component.xlistview.XListView;
import com.gagein.component.xlistview.XListView.IXListViewListener;
import com.gagein.http.APIHttpMetadata;
import com.gagein.http.APIParser;
import com.gagein.model.Agent;
import com.gagein.model.Company;
import com.gagein.model.DataPage;
import com.gagein.model.Facet;
import com.gagein.model.FacetItem;
import com.gagein.model.FacetItemIndustry;
import com.gagein.model.Person;
import com.gagein.model.Resource;
import com.gagein.model.Update;
import com.gagein.ui.main.BaseActivity;
import com.gagein.ui.newsfilter.FilterNewsCompanyActivity;
import com.gagein.ui.settings.ShareActivity;
import com.gagein.util.CommonUtil;
import com.gagein.util.Constant;
import com.gagein.util.Log;

public class CompanyActivity extends BaseActivity implements OnItemClickListener, IXListViewListener{
	
	private XListView newsList;
	private XListView aboutList;
	private XListView personList;
	private XListView competitorList;
	private CompanyNewsAdapter newsAdapter;
	private CompanyAboutAdapter aboutAdapter;
	private CompanyPersonAdapter personAdapter;
	private CompanyCompetitorsAdapter competitorAdapter;
	private long mCompanyId;
	private Company mCompany;
	private ImageView companyLog;
	private ImageView followImage;
	private TextView companyName;
	private TextView websiteAndAddress;
	private List<Update> updates = new ArrayList<Update>();
	private List<Person> persons = new ArrayList<Person>();
	private List<Company> competitors = new ArrayList<Company>();
	private int employeesPageNumber = Constant.PAGE_NUMBER_START;
	private DataPage dpPersons;
	private DataPage dpCompetitors;
	private List<FacetItem> personFacetItems;
	private int requestPageNumber = Constant.PAGE_NUMBER_START;
	private TextView newsBtn;
	private TextView aboutBtn;
	private TextView peopleBtn;
	private TextView competitorsBtn;
	private TextView filterBtn;
	private Facet facet;
	private int typeChecked = 0;
	private int typeNews = 0;
	private int typeAbout = 1;
	private int typePeople = 2;
	private int typeCompetitors = 3;
	private List<FacetItemIndustry> competitorIndustries = new ArrayList<FacetItemIndustry>();
	private Facet competitorFacet;
	private List<String> industryid = new ArrayList<String>();
	private String competitorSortBy = "";
	private Boolean isNoNews = false;
	private Boolean isNoPeople = false;
	private Boolean isNoCompetitor = false;
	private LinearLayout noCompetitors;
	private LinearLayout noPeople;
	private List<Update> filings = new ArrayList<Update>();
	private RelativeLayout sortByLayout;
	private TextView sortBy;
	private List<Resource> resources = new ArrayList<Resource>();
	private ScrollView noNewsLayout;
	private LinearLayout resourceShowLayout;
	private LinearLayout resourceParentLayout;
	private LinearLayout noNewsShowLayout;
	private LinearLayout provisionedLayout;
	private RelativeLayout provisionBottomLayout;
	private TextView noNewsPt;
	private TextView provisionPt;
	private TextView shareGagein;
	private ListView parentsOrSubsidiariesList;
	private Boolean haveProcessed = false;
	private Boolean haveGotPeople = false;;
	private Boolean haveGotCompetitors = false;
	private String provisionDateStr;
	private SearchCompanyAdapter searchCompanyAdapter;
	private final List<Company> parentsAndSubsidiaries = new ArrayList<Company>();
	private RelativeLayout oneDayLayout;
	private RelativeLayout oneWeekLayout;
	private RelativeLayout oneMonthLayout;
	private TextView folRankDay;
	private TextView folRankWeek;
	private TextView folRankMonth;
	private TextView folScoreDay;
	private TextView folScoreWeek;
	private TextView folScoreMonth;
	private TextView noLongerOperating;
	private TextView relatedCompanyName;
	private LinearLayout relatedLayout;
	private LinearLayout layout;
	
	@Override
	protected List<String> observeNotifications() {
		return stringList(Constant.BROADCAST_REFRESH_COMPANY_NEWS, Constant.BROADCAST_REFRESH_COMPANY_PEOPLE,
				Constant.BROADCAST_FILTER_REFRESH_COMPETITORS, Constant.BROADCAST_FOLLOW_COMPANY, 
				Constant.BROADCAST_UNFOLLOW_COMPANY, Constant.BROADCAST_LIKED_NEWS,
				Constant.BROADCAST_UNLIKE_NEWS, Constant.BROADCAST_REMOVE_BOOKMARKS,
				Constant.BROADCAST_ADD_BOOKMARKS, Constant.BROADCAST_IRRELEVANT_TRUE, 
				Constant.BROADCAST_IRRELEVANT_FALSE, Constant.BROADCAST_HAVE_READ_STORY);
	}
	
	@Override
	public void handleNotifications(Context aContext, Intent intent) {
		super.handleNotifications(aContext, intent);
		
		String actionName = intent.getAction();
		if (actionName.equals(Constant.BROADCAST_REFRESH_COMPANY_NEWS)) {
			
			getNews(false, 0, APIHttpMetadata.kGGPageFlagFirstPage, 0, true);
			
		} else if (actionName.equals(Constant.BROADCAST_REFRESH_COMPANY_PEOPLE)) {
			
			getPersons(false);
			
		} else if (actionName.equals(Constant.BROADCAST_FILTER_REFRESH_COMPETITORS)) {
			
			competitorSortBy = Constant.COMPETITOR_SORT_BY;
			industryid = Constant.COMPETITOR_FILTER_PARAM_VALUE;
			getCompetitors(false);
			
		}else if (actionName.equals(Constant.BROADCAST_FOLLOW_COMPANY)) {
			
			refreshParentsAndSubsidiariesStatus(intent, true);
			refreshCompany(intent, true);
			
		} else if (actionName.equals(Constant.BROADCAST_UNFOLLOW_COMPANY)) {
			
			refreshParentsAndSubsidiariesStatus(intent, false);
			refreshCompany(intent, false);
			
		} else if (actionName.equals(Constant.BROADCAST_LIKED_NEWS)) {
			
			setUpdateLikeStatus(intent, true);
			
		} else if (actionName.equals(Constant.BROADCAST_UNLIKE_NEWS)) {	
			
			setUpdateLikeStatus(intent, false);
			
		} else if (actionName.equals(Constant.BROADCAST_REMOVE_BOOKMARKS)) {
			
			setUpdateBookmarkStatus(intent, false);
			
		} else if (actionName.equals(Constant.BROADCAST_ADD_BOOKMARKS)) {	
			
			setUpdateBookmarkStatus(intent, true);
			
		} else if (actionName.equals(Constant.BROADCAST_IRRELEVANT_TRUE)) {
			
			setUpdateIrrelevantStatus(intent, true);
			
		} else if (actionName.equals(Constant.BROADCAST_IRRELEVANT_FALSE)) {
			
			setUpdateIrrelevantStatus(intent, false);
			
		} else if (actionName.equals(Constant.BROADCAST_HAVE_READ_STORY)) {
			
			haveReadStory(intent);
			newsAdapter.notifyDataSetChanged();
			
		}
	}
	
	private void haveReadStory(Intent intent) {
		
		long newsId = intent.getLongExtra(Constant.NEWSID, 0);
		
		for (int i = 0; i < updates.size(); i ++) {
			if (updates.get(i).newsId == newsId) {
				updates.get(i).hasBeenRead = true;
				
			}
		}
		
	}

	private void setUpdateLikeStatus(Intent intent, Boolean like) {
		
		long updatesId = intent.getLongExtra(Constant.UPDATEID, 0);
		for (int i = 0 ; i < updates.size(); i ++) {
			if (updatesId == updates.get(i).newsId) {
				updates.get(i).liked = like;
				Log.v("silen", "like = " + like);
			}
		}
		if (null != newsAdapter) newsAdapter.notifyDataSetChanged();
		
	}
	
	private void setUpdateBookmarkStatus(Intent intent, Boolean save) {
		
		long updatesId = intent.getLongExtra(Constant.UPDATEID, 0);
		for (int i = 0 ; i < updates.size(); i ++) {
			if (updatesId == updates.get(i).newsId) {
				updates.get(i).saved = save;
			}
		}
		
		if (null != newsAdapter) newsAdapter.notifyDataSetChanged();
		
	}
	
	private void setUpdateIrrelevantStatus(Intent intent, Boolean irrelevant) {
		
		long updatesId = intent.getLongExtra(Constant.UPDATEID, 0);
		for (int i = 0 ; i < updates.size(); i ++) {
			if (updatesId == updates.get(i).newsId) {
				updates.get(i).irrelevant = irrelevant;
			}
		}
		if (null != newsAdapter) newsAdapter.notifyDataSetChanged();
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		
		setContentView(R.layout.activity_company);
		
		doInit();
		
	}
	
	@Override
	protected void initView() {
		super.initView();
		
		setLeftImageButton(R.drawable.back_arrow);
		newsList = (XListView) findViewById(R.id.newsList);
		aboutList = (XListView) findViewById(R.id.aboutList);
		personList = (XListView) findViewById(R.id.personList);
		competitorList = (XListView) findViewById(R.id.competitorList);
		companyLog = (ImageView) findViewById(R.id.companyLog);
		followImage = (ImageView) findViewById(R.id.followImage);
		companyName = (TextView) findViewById(R.id.companyName);
		websiteAndAddress = (TextView) findViewById(R.id.websiteAndAddress);
		noCompetitors = (LinearLayout) findViewById(R.id.noCompetitors);
		noPeople = (LinearLayout) findViewById(R.id.noPeople);
		sortByLayout = (RelativeLayout) findViewById(R.id.sortByLayout);
		noNewsLayout = (ScrollView) findViewById(R.id.noNewsLayout);
		resourceShowLayout = (LinearLayout) findViewById(R.id.resourceShowLayout);
		resourceParentLayout = (LinearLayout) findViewById(R.id.resourceParentLayout);
		provisionedLayout = (LinearLayout) findViewById(R.id.provisionedLayout);
		noNewsShowLayout = (LinearLayout) findViewById(R.id.noNewsShowLayout);
		provisionBottomLayout = (RelativeLayout) findViewById(R.id.provisionBottomLayout);
		noNewsPt = (TextView) findViewById(R.id.noNewsPt);
		provisionPt = (TextView) findViewById(R.id.provisionPt);
		shareGagein = (TextView) findViewById(R.id.shareGagein);
		parentsOrSubsidiariesList = (ListView) findViewById(R.id.parentsOrSubsidiariesList);
		
		newsBtn = (TextView) findViewById(R.id.news);
		aboutBtn = (TextView) findViewById(R.id.about);
		peopleBtn = (TextView) findViewById(R.id.people);
		competitorsBtn = (TextView) findViewById(R.id.competitors);
		filterBtn = (TextView) findViewById(R.id.filter);
		sortBy = (TextView) findViewById(R.id.sortBy);
		
		oneDayLayout = (RelativeLayout) findViewById(R.id.oneDayLayout);
		oneWeekLayout = (RelativeLayout) findViewById(R.id.oneWeekLayout);
		oneMonthLayout = (RelativeLayout) findViewById(R.id.oneMonthLayout);
		folRankDay = (TextView) findViewById(R.id.folRankDay);
		folRankWeek = (TextView) findViewById(R.id.folRankWeek);
		folRankMonth = (TextView) findViewById(R.id.folRankMonth);
		folScoreDay = (TextView) findViewById(R.id.folScoreDay);
		folScoreWeek = (TextView) findViewById(R.id.folScoreWeek);
		folScoreMonth = (TextView) findViewById(R.id.folScoreMonth);
		noLongerOperating = (TextView) findViewById(R.id.noLongerOperating);
		relatedCompanyName = (TextView) findViewById(R.id.relatedCompanyName);
		layout = (LinearLayout) findViewById(R.id.layout);
		relatedLayout = (LinearLayout) findViewById(R.id.relatedLayout);
		
		newsList.setPullLoadEnable(false);
		aboutList.setPullLoadEnable(false);
		personList.setPullLoadEnable(false);
		competitorList.setPullLoadEnable(false);
	}
	
	@Override
	protected void initData() {
		super.initData();
		
		mCompanyId = getIntent().getLongExtra(Constant.COMPANYID, 0);
		getCompanyDetail();
		
		setSelectedButton(newsBtn);
		
		newsList.setVisibility(View.VISIBLE);
	}
	
	@Override
	protected void setData() {
		super.setData();
		
		setTitle(mCompany.orgName);
		setFollowButton();
		setHeadView();
		setScore();
		
	}
	
	private void setScore() {
		CommonUtil.setScoreLayout(oneDayLayout, mCompany.fol_rank1, mCompany.fol_score1, folRankDay, folScoreDay, mContext);
		CommonUtil.setScoreLayout(oneWeekLayout, mCompany.fol_rank7, mCompany.fol_score7, folRankWeek, folScoreWeek, mContext);
		CommonUtil.setScoreLayout(oneMonthLayout, mCompany.fol_rank30, mCompany.fol_score30, folRankMonth, folScoreMonth, mContext);
	}

	private void setHeadView() {
		
		loadImage(mCompany.orgLargerLogoPath, companyLog);
		int companyLogWith = companyLog.getWidth();
		int followImageWith = followImage.getWidth();
		int deviceWidth = CommonUtil.getDeviceWidth((Activity)mContext);
		companyName.setMaxWidth(deviceWidth - companyLogWith - followImageWith - 80);
		companyName.setText(mCompany.orgName);

		websiteAndAddress.setText(CommonUtil.removeWebsite3W(mCompany.website) + "  " + mCompany.address());
		
		setFollowImage();
		
	}
	
	private void refreshCompany(Intent intent, Boolean follow) {
		
		long companyId = intent.getLongExtra(Constant.COMPANYID, 0);
		
		for (int i = 0; i < mCompany.subsidiaries.size(); i ++) {
			Company subsidiary = mCompany.subsidiaries.get(i);
			if (subsidiary.orgID == companyId) {
				subsidiary.followed = follow;
			}
		}
		
		for (int i = 0; i < mCompany.parents.size(); i ++) {
			Company parent = mCompany.parents.get(i);
			if (parent.orgID == companyId) {
				parent.followed = follow;
			}
		}
		
		for (int i = 0; i < mCompany.joinVentures.size(); i ++) {
			Company joinVenture = mCompany.joinVentures.get(i);
			if (joinVenture.orgID == companyId) {
				joinVenture.followed = follow;
			}
		}
		
		for (int i = 0; i < mCompany.divisions.size(); i ++) {
			Company division = mCompany.divisions.get(i);
			if (division.orgID == companyId) {
				division.followed = follow;
			}
		}
		
		for (int i = 0; i < competitors.size(); i ++) {
			Company competitor = competitors.get(i);
			if (competitor.orgID == companyId) {
				competitor.followed = follow;
				if (null != competitorAdapter) competitorAdapter.notifyDataSetChanged();
			}
		}
		
		if (companyId == mCompany.orgID) {	
			mCompany.followed = follow;
			setFollowImage();
			setFollowButton();
		}
		
	}

	private void setFollowImage() {
		
		followImage.setVisibility(mCompany.followed ? View.VISIBLE : View.GONE);
		
		int companyLogWith = companyLog.getWidth();
		int followImageWith = followImage.getWidth();
		int deviceWidth = CommonUtil.getDeviceWidth((Activity)mContext);
		if (mCompany.followed) {
			companyName.setMaxWidth(deviceWidth - companyLogWith - followImageWith - 80);
		} else {
			companyName.setMaxWidth(deviceWidth - companyLogWith - 80);
		}
		
	}
	
	public void setNews() {
		newsAdapter = new CompanyNewsAdapter(null, mContext, updates);
		newsList.setAdapter(newsAdapter);
		newsAdapter.notifyDataSetChanged();
		newsAdapter.notifyDataSetInvalidated();
	}
	
	public void setPersons() {
		personAdapter = new CompanyPersonAdapter(mContext, persons);
		personList.setAdapter(personAdapter);
		personAdapter.notifyDataSetChanged();
		personAdapter.notifyDataSetInvalidated();
	}
	
	public void setCompetitors() {
		competitorAdapter = new CompanyCompetitorsAdapter(mContext, competitors);
		competitorList.setAdapter(competitorAdapter);
		competitorAdapter.notifyDataSetChanged();
		competitorAdapter.notifyDataSetInvalidated();
	}

	private void unfollowCompany() {
		
		showLoadingDialog();
		mApiHttp.unfollowCompany(mCompanyId, new Listener<JSONObject>() {
			
			@Override
			public void onResponse(JSONObject jsonObject) {
				
				APIParser parser = new APIParser(jsonObject);
				if (parser.isOK()) {
					
					mCompany.followed = false;
					
					Intent intent = new Intent();
					intent.putExtra(Constant.COMPANYID, mCompanyId);
					intent.setAction(Constant.BROADCAST_UNFOLLOW_COMPANY);
					mContext.sendBroadcast(intent);
					
					setFollowButton();
					setFollowImage();
				} else {
					alertMessageForParser(parser);
				}
				dismissLoadingDialog();
			}
		}, new Response.ErrorListener() {
			
			@Override
			public void onErrorResponse(VolleyError error) {
				showConnectionError();
			}
		});
	}
	
	private void followCompany() {
		
		showLoadingDialog();
		mApiHttp.followCompany(mCompanyId, new Listener<JSONObject>() {
			
			@Override
			public void onResponse(JSONObject jsonObject) {
				
				APIParser parser = new APIParser(jsonObject);
				if (parser.isOK()) {
					
					mCompany.followed = true;
					
					Intent intent = new Intent();
					intent.putExtra(Constant.COMPANYID, mCompanyId);
					intent.setAction(Constant.BROADCAST_FOLLOW_COMPANY);
					mContext.sendBroadcast(intent);
					
					setFollowButton();
					setFollowImage();
					
				} else {
					alertMessageForParser(parser);
				}
				dismissLoadingDialog();
			}
		}, new Response.ErrorListener() {
			
			@Override
			public void onErrorResponse(VolleyError error) {
				showConnectionError();
			}
		});
	}
	
	public void setFollowButton() {
		setRightButton(mCompany.followed ? R.string.unfollow : R.string.follow);
	}
	
	public void getCompetitorsFilter() {
		mApiHttp.getSimilarCompanies(mCompanyId, industryid, requestPageNumber, competitorSortBy,  new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject jsonObject) {
						
				APIParser parser = new APIParser(jsonObject);
				if (parser.isOK()) {
					
					dpCompetitors = parser.parseGetSimilarCompanies();
					competitorFacet = dpCompetitors.facet;
					if (competitorFacet != null) {
						competitorIndustries = competitorFacet.industries;
					}
					
					Constant.currentCompetitorIndustries = competitorIndustries;
				}
			}
						
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
			}
		});
	}
	
	
	public void getCompetitors(final Boolean loadMore) {
		
		if (!loadMore) showLoadingDialog();
		mApiHttp.getSimilarCompanies(mCompanyId, industryid, requestPageNumber, competitorSortBy,  new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject jsonObject) {
						
				if (!loadMore) competitors.clear();
						
						APIParser parser = new APIParser(jsonObject);
						if (parser.isOK()) {
							
							haveGotCompetitors = true;
							
							dpCompetitors = parser.parseGetSimilarCompanies();
							competitorFacet = dpCompetitors.facet;
							if (competitorFacet != null) {
								competitorIndustries = competitorFacet.industries;
							}
							
							if (Constant.currentCompetitorIndustries.size() <= 0) {
								Constant.currentCompetitorIndustries = competitorIndustries;
							}
							
							List<Object> items = dpCompetitors.items;
							if (items != null) {
								for (Object obj : items) {
									competitors.add((Company)obj);
								}
							}
							
							if (competitors.size() == 0) {
								isNoCompetitor = true;
								noCompetitors.setVisibility(View.VISIBLE);
								dismissLoadingDialog();
								return;
							} else {
								isNoCompetitor = false;
								noCompetitors.setVisibility(View.GONE);
							}
							
							setFilterAndSortByVisible(isNoCompetitor);
							
							competitorList.setPullLoadEnable(dpCompetitors.hasMore);
							
							if (!loadMore) setCompetitors();
							
						} else {
							
							isNoCompetitor = true;
							setFilterAndSortByVisible(isNoCompetitor);
							alertMessageForParser(parser);
						}
						dismissLoadingDialog();
					}

		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				showConnectionError();
			}
		});
	}
	
	private ArrayList<String> getFilterIds(ArrayList<FacetItem> currentFilters) {
		
		ArrayList<String> filterIds = new ArrayList<String>();
		
		ArrayList<FacetItem> filters = currentFilters;
		for (int i = 0; i < filters.size(); i++) {
			if (filters.get(i).selected && filters.get(i).id != 0) {
				filterIds.add(filters.get(i).id + "");
			}
		}
		
		return filterIds;
		
	}
	
	public void getPersons(final Boolean loadMore) {
		
		if (!loadMore) showLoadingDialog();
		mApiHttp.getCompanyPeople(mCompanyId, employeesPageNumber, Constant.PEOPLE_SORT_BY,
				getFilterIds(Constant.currentFunctionRoleForCompanyPeopleFilter), 
				getFilterIds(Constant.currentJobLevelForCompanyPeopleFilter), 
				getFilterIds(Constant.currentLinkedProfileForCompanyPeopleFilter), new Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject jsonObject) {
						
						APIParser parser = new APIParser(jsonObject);
						if (parser.isOK()) {
							
							haveGotPeople = true;
							
							personFacetItems = new ArrayList<FacetItem>();
							
							dpPersons = parser.parseGetCompanyPeople();
							personFacetItems.clear();
							facet = dpPersons.facet;
							Constant.currentFacet = facet;
							if (facet != null) {
								// job levels
								List<FacetItem> jobLevels = facet.jobLevels;
								
								FacetItem item = new FacetItem();
								item.name = mContext.getResources().getString(R.string.all_job_levels);
								item.type = FacetItem.TYPE_JOB_LEVEL;
								personFacetItems.add(item);
								
//								boolean hasSelecteID = false;
								if (jobLevels != null) {
									for (int i = 0; i < jobLevels.size(); i++) {
										item = jobLevels.get(i);
										item.type = FacetItem.TYPE_JOB_LEVEL;
										personFacetItems.add(item);
//										if (item.id == selectedJobLevelIDs) {
////											hasSelecteID = true;
//										}
									}

								}
//								if (!hasSelecteID) selectedJobLevelID = 0;
//								functionalPosition = (jobLevels != null) ? jobLevels.size() + 1 : 0;
								
								// functional roles
								List<FacetItem> functionalRoles = facet.functionalRoles;
								item = new FacetItem();
								item.name = mContext.getResources().getString(R.string.all_functional_roles);
								item.type = FacetItem.TYPE_FUNCTIONAL_ROLE;
								personFacetItems.add(item);
								
//								hasSelecteID = false;
								if (functionalRoles != null) {
									for (int i = 0; i < functionalRoles.size(); i++) {
										item = functionalRoles.get(i);
										item.type = FacetItem.TYPE_FUNCTIONAL_ROLE;
										personFacetItems.add(item);
										
//										if (item.id == selectedFunctionRoleIDs) {
////											hasSelecteID = true;
//										}
									}
								}
//								if (!hasSelecteID) selectedFunctionRoleID = 0;
							}
							
							if (!loadMore) persons.clear();
							
							List<Object> items = dpPersons.items;
							if (items != null) {
								for (Object obj : items) {
									persons.add((Person)obj);
								}
								
								if (!loadMore) setPersons();
							}
							
							if (persons.size() == 0) {
								isNoPeople = true;
								noPeople.setVisibility(View.VISIBLE);
								personList.setPullLoadEnable(false);
								dismissLoadingDialog();
								return;
							} else {
								isNoPeople = false;
								noPeople.setVisibility(View.GONE);
							}
							
							setFilterAndSortByVisible(isNoPeople);
							
						} else {
							
							isNoPeople = true;
							setFilterAndSortByVisible(isNoPeople);
							
							alertMessageForParser(parser);
							
						}
						
						personList.setPullLoadEnable(dpPersons.hasMore);
						dismissLoadingDialog();
					}

		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				showConnectionError();
			}
		});
	}
	
	private void setFilterAndSortByVisible(Boolean noData) {
		if (noData) {
			setFilterVisible(View.GONE);
			setSortByVisible(View.GONE);
		} else {
			setFilterVisible(View.VISIBLE);
			setSortByVisible(View.VISIBLE);
		}
	}
	
	public void getSecFilings() {
		if (mCompanyId == 0) return; 
		mApiHttp.getSecFilings(mCompanyId, new Listener<JSONObject>(){
			
			@Override
			public void onResponse(JSONObject jsonObject) {
				APIParser parser = new APIParser(jsonObject);
				if (null != jsonObject) {
					if (parser.isOK()) {
						filings.clear();
						DataPage dataPage = parser.parseGetCompanyUpdates();
						if (dataPage != null && dataPage.items != null) {
							for (Object obj : dataPage.items) {
								filings.add((Update)obj);
							}
						}
					}
				}
				aboutAdapter = new CompanyAboutAdapter(mContext, mCompany, filings);
				aboutList.setAdapter(aboutAdapter);
				aboutAdapter.notifyDataSetChanged();
			}
			
		}, new Response.ErrorListener() {
			
			@Override
			public void onErrorResponse(VolleyError error) {
				showConnectionError();
			}
		});
	}
	
	private void getProvisionDateWithCompanyID() {
		if (mCompanyId == 0) return; 
		mApiHttp.getProvisionDateWithCompanyID(mCompanyId, new Listener<JSONObject>(){
			
			@Override
			public void onResponse(JSONObject jsonObject) {
				
				APIParser parser = new APIParser(jsonObject);
				
				if (null != jsonObject) {
					if (parser.isOK()) {
						
						JSONObject dataObject = parser.data();
						
						haveProcessed = dataObject.has("is_processed");
//						haveProvisionDate = dataObject.has("provision_date");
//						
//						isProcessed = dataObject.optBoolean("is_processed");
//						provisionDate = dataObject.optLong("provision_date");
						provisionDateStr = dataObject.optString("provision_date_str");
						
					}
				}
				
				getNews(false, 0, APIHttpMetadata.kGGPageFlagFirstPage, 0, false);
			}
			
		}, new Response.ErrorListener() {
			
			@Override
			public void onErrorResponse(VolleyError error) {
				showConnectionError();
			}
		});
	}
	
	public void getCompanyDetail() {
		
		if (mCompanyId == 0) return; 
		showLoadingDialog();
		mApiHttp.getCompanyOverview(mCompanyId, true, 0, new Listener<JSONObject>(){

			@Override
			public void onResponse(JSONObject jsonObject) {
				
				getProvisionDateWithCompanyID();
				
				
				APIParser parser = new APIParser(jsonObject);
				if (null != jsonObject) {
					if (parser.isOK()) {
						mCompany = parser.parseGetCompanyOverview();
						
						getSecFilings();
						setData();
					} else {
						alertMessageForParser(parser);
					}
				}
			}
			
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				showConnectionError();
			}
		});
	}
	
	private ArrayList<String> getAgentsId() {
		
		ArrayList<String> agentsId = new ArrayList<String>();
		for (int i = 0; i < Constant.locationNewsTriggersForCompany.size() ; i++) {
			Agent agent = Constant.locationNewsTriggersForCompany.get(i);
			if (agent.checked && !agent.agentID.equalsIgnoreCase("0")) {
				agentsId.add(agent.agentID);
			}
		}
		
		return agentsId;
	}
	
	private void getNews(final boolean loadMore, final long aNewsID, byte aPageFlag, final long aPageTime, final boolean showDialog) {
		
		if (showDialog) showLoadingDialog();
		if (0 == mCompanyId) return;
			mApiHttp.getCompanyUpdatesNoFilter(getAgentsId(), mCompanyId, 0, 0, APIHttpMetadata.kGGPageFlagFirstPage, 0,
					new Listener<JSONObject>() {

						@Override
						public void onResponse(JSONObject response) {
							
							dismissLoadingDialog();
							
							if (!loadMore) updates.clear();
							newsList.setPullLoadEnable(false);
							
							APIParser parser = new APIParser(response);
							
							if (null != response) {
								
								if (parser.isOK()) {
									
									DataPage dataPage = parser.parseGetCompanyUpdates();
									Boolean haveMoreNews = dataPage.hasMore;
									newsList.setPullLoadEnable(haveMoreNews);
									
									if (dataPage != null && dataPage.items != null) {
										for (Object obj : dataPage.items) {
											updates.add((Update)obj);
										}
									}
									
									if (!loadMore) setNews();
									
									
									if (updates.size() == 0) {
										isNoNews = true;
										noNewsLayout.setVisibility(View.VISIBLE);
									} else {
										isNoNews = false;
										noNewsLayout.setVisibility(View.GONE);
									}
									
								} else {
									
									isNoNews = true;
									noNewsLayout.setVisibility(View.VISIBLE);
									
									/// data preparation
									JSONObject extraInfoObject = response.optJSONObject("msg_extra_info");
									String grade = mCompany.grade;//(null == extraInfoObject) ? "" : extraInfoObject.optString("grade");
									Boolean haveResource = false;
									JSONArray resourceArray = (null == extraInfoObject) ? null : extraInfoObject.optJSONArray("resources");
									if (null != resourceArray && resourceArray.length() > 0) haveResource = true;
									
									final List<Company> parents = mCompany.parents;
									final List<Company> subsidiaries = mCompany.subsidiaries;
									final List<Company> divisions = mCompany.divisions;
									final List<Company> joinVentures = mCompany.joinVentures;
									
									int parentsSize = 0;
									int subsidiariesSize = 0;
									parentsAndSubsidiaries.clear();
									for (int i = 0; i < parents.size(); i ++) {
										if (!parents.get(i).followed) {
											parentsAndSubsidiaries.add(parents.get(i));
											parentsSize ++;
										}
									}
									for (int i = 0; i < subsidiaries.size(); i ++) {
										if (!subsidiaries.get(i).followed) {
											parentsAndSubsidiaries.add(subsidiaries.get(i));
											subsidiariesSize ++;
										}
									}
									for (int i = 0; i < joinVentures.size(); i ++) {
										if (!joinVentures.get(i).followed) {
											parentsAndSubsidiaries.add(joinVentures.get(i));
											subsidiariesSize ++;
										}
									}
									for (int i = 0; i < divisions.size(); i ++) {
										if (!divisions.get(i).followed) {
											parentsAndSubsidiaries.add(divisions.get(i));
											subsidiariesSize ++;
										}
									}
									
									/// real logic goes here
									//if (_associatedCompanies.count > 0 && [_company isFuckingGradeAorB]) {
									
									
									if ((parentsSize > 0 || subsidiariesSize > 0) && isFuckingAorB(grade)) {
										
										noNewsShowLayout.setVisibility(View.VISIBLE);
										
										String noNewsPromot = "There are no news stories in the last 180 days. Follow this company's %s to receive more news.";
										
										if (parentsSize > 0 && subsidiariesSize == 0) {
											noNewsPromot = String.format(noNewsPromot, (parentsSize > 1) ? "parents" : "parent");
										} else if (subsidiariesSize > 0 && parentsSize == 0) {
											noNewsPromot = String.format(noNewsPromot, (parentsSize > 1) ? "subsidiaries" : "subsidiary");
										} else {
											if (parentsSize == 1 && subsidiariesSize == 1) {
												noNewsPromot = String.format(noNewsPromot, "parent and/or subsidiary");
											} else if (parentsSize > 1 && subsidiariesSize == 1) {
												noNewsPromot = String.format(noNewsPromot, "parents and/or subsidiary");
											} else if (parentsSize == 1 && subsidiariesSize > 1) {
												noNewsPromot = String.format(noNewsPromot, "parent and/or subsidiaries");
											} else {
												noNewsPromot = String.format(noNewsPromot, "parents and/or subsidiaries");
											}
											
										}
										
										noNewsPt.setText(noNewsPromot);
										
										searchCompanyAdapter = new SearchCompanyAdapter(mContext, parentsAndSubsidiaries);
										parentsOrSubsidiariesList.setAdapter(searchCompanyAdapter);
										CommonUtil.setViewHeight(parentsOrSubsidiariesList, parentsOrSubsidiariesList.getAdapter().getCount() * CommonUtil.dp2px(mContext, 71));
										searchCompanyAdapter.notifyDataSetChanged();
										searchCompanyAdapter.notifyDataSetInvalidated();
										parentsOrSubsidiariesList.setOnItemClickListener(new OnItemClickListener() {
											
											@Override
											public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
												Intent intent = new Intent();
												intent.putExtra(Constant.COMPANYID, parentsAndSubsidiaries.get(position).orgID);
												intent.setClass(mContext, CompanyActivity.class);
												startActivity(intent);
											}
										});
									}  // end of parent/sub section
									else if (haveResource) {
										
										showResource(resourceArray);
										
									} else if (grade.equalsIgnoreCase("a") 
											|| grade.equalsIgnoreCase("a2")
											|| grade.equalsIgnoreCase("b")) {
										noNewsShowLayout.setVisibility(View.VISIBLE);
										String noNewsPromot = "There are no news stories in the last 180 days.";
										noNewsPt.setText(noNewsPromot);
									} else if (!haveProcessed) {
										Log.v("silen", "grade = " + grade);
										if (grade.equalsIgnoreCase("a1")) {
											noNewsShowLayout.setVisibility(View.VISIBLE);
											String noNewsPromot = "There are no news stories in the last 180 days.";
											noNewsPt.setText(noNewsPromot);
										} else {
											if (grade.equalsIgnoreCase("oob")) {
												noLongerOperating.setText("This company is no longer\noperating.");
												noLongerOperating.setVisibility(View.VISIBLE);
												return;
											} else {
												if (mCompany.followed) {
													String provisionPtStr = "This company is waiting to be provisioned.";
													provisionPt.setText(provisionPtStr);
													provisionBottomLayout.setVisibility(View.GONE);
													provisionedLayout.setVisibility(View.VISIBLE);
													layout.setVisibility(View.VISIBLE);
	//												noLongerOperating.setText("This company is no longer\noperating.");
	//												noLongerOperating.setVisibility(View.VISIBLE);
													return;
												} else {
													String provisionPtStr = "This company is waiting to be provisioned. Follow to speed up processing.";
													provisionPt.setText(provisionPtStr);
													provisionBottomLayout.setVisibility(View.GONE);
													provisionedLayout.setVisibility(View.VISIBLE);
												}
											}
										}
									} else if (null != mCompany.relatedCompany) {
										relatedLayout.setVisibility(View.VISIBLE);
										relatedCompanyName.setText(mCompany.relatedCompany.name);
										layout.setVisibility(View.GONE);
										return;
									} else {
										String provisionPtStr = "This company is being provisioned and will be done by %s.";
										provisionPt.setText(String.format(provisionPtStr,  (!TextUtils.isEmpty(provisionDateStr) ? provisionDateStr : "Jun 8 by 7:12am")));
										provisionedLayout.setVisibility(View.VISIBLE);
									}
								}
								
								layout.setVisibility(View.VISIBLE);
							}
							
						}
					}, new Response.ErrorListener() {

						@Override
						public void onErrorResponse(VolleyError error) {
							showConnectionError();
						}
					});
	};
	
	private boolean isFuckingAorB(String grade) {
		return grade.equalsIgnoreCase("a") 
				|| grade.equalsIgnoreCase("a1")
				|| grade.equalsIgnoreCase("a2")
				|| grade.equalsIgnoreCase("b");
	}
	
	private void refreshParentsAndSubsidiariesStatus(Intent intent, Boolean isFollow) {
		
		long companyId = intent.getLongExtra(Constant.COMPANYID, 0);
		if (parentsAndSubsidiaries.size() == 0 || null == searchCompanyAdapter) return;
		for (int i = 0; i < parentsAndSubsidiaries.size(); i ++) {
			Company company = parentsAndSubsidiaries.get(i);
			if (company.orgID == companyId) {
				company.followed = isFollow;
			}
		}
		searchCompanyAdapter.notifyDataSetChanged();
		
	}
		
	private void showResource(JSONArray resourceArray) {
		
		resourceShowLayout.setVisibility(View.VISIBLE);
		
		int resourcesLength = resourceArray.length();
		resources.clear();
		resourceParentLayout.removeAllViews();
		
		for (int i = 0; i < resourcesLength; i ++) {
			Resource resource = new Resource();
			try {
				resource.parseData(resourceArray.getJSONObject(i));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			resources.add(resource);
		}
		
		Log.v("silen", "resources.size() = " + resources.size());
		
		for (int i = 0; i < resources.size(); i ++) {
			View view = getLayoutInflater().inflate(R.layout.item_resource, null);
			TextView resourceUrl = (TextView) view.findViewById(R.id.resourceUrl);
			final String url = resources.get(i).url;
			resourceUrl.setText(url);
			resourceUrl.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					startWebActivity(url);
				}
			});
			resourceParentLayout.addView(view);
		}
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		if (v == leftImageBtn) {
			finish();
		} else if (v == rightBtn) {
			
			if (!mCompany.followed) {
				followCompany();
			} else {
				String text = mCompany.followed ? mContext.getResources().getString(R.string.unfollow) : mContext.getResources().getString(R.string.follow);
				final CommonDialog dialog = new CommonDialog(mContext);
				dialog.setCancelable(false);
				Window window = dialog.getWindow();
				window.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				window.setGravity(Gravity.BOTTOM);
				window.setWindowAnimations(R.style.dialog_animation);
				dialog.showDialog(text, new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						unfollowCompany();
						dialog.dismissDialog(); 
					}
				}, new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						dialog.dismissDialog();
					}
				});
				
			}
			
		} else if (v == filterBtn) {
			
			if (typeChecked == typeNews) {
				Intent intent = new Intent();
				intent.setClass(mContext, FilterNewsCompanyActivity.class);
				intent.putExtra(Constant.ACTIVITYNAME, "CompanyActivity");
				startActivityForResult(intent, 0);
			} else if (typeChecked == typePeople) {
				Intent intent = new Intent();
				intent.setClass(mContext, PeopleFilterActivity.class);
				startActivityForResult(intent, 1);
			} else if (typeChecked == typeCompetitors) {
				Intent intent = new Intent();
				intent.setClass(mContext, CompetitorFilterActivity.class);
				startActivityForResult(intent, 2);
			}
			
		} else if (v == sortBy) {
			
			if (typeChecked == typePeople) {
				Intent intent = new Intent();
				intent.setClass(mContext, PeopleFilterSortByActivity.class);
				startActivityForResult(intent, 3);
			} else if (typeChecked == typeCompetitors) {
				Intent intent = new Intent();
				intent.setClass(mContext, CompetitorFilterSortByActivity.class);
				startActivityForResult(intent, 4);
			}
			
		} else if (v == newsBtn) {
			
			setAllListViewGone();
			setListViewVisible(newsList);
			setFilterVisible(View.VISIBLE);
			
			typeChecked = typeNews;
			setSortByVisible(View.GONE);
			setCategoryButtonDefault();
			setSelectedButton(newsBtn);
			setNoDatasGone();
			if (isNoNews) noNewsLayout.setVisibility(View.VISIBLE);
			
		} else if (v == aboutBtn) {
			
			setAllListViewGone();
			setListViewVisible(aboutList);
			setFilterVisible(View.GONE);
			
			typeChecked = typeAbout;
			setSortByVisible(View.GONE);
			setCategoryButtonDefault();
			setSelectedButton(aboutBtn);
			setNoDatasGone();
			
		} else if (v == peopleBtn) {
			
			setAllListViewGone();
			setListViewVisible(personList);
			setFilterVisible(View.GONE);
			setSortByVisible(View.GONE);
			
			typeChecked = typePeople;
			setCategoryButtonDefault();
			setSelectedButton(peopleBtn);
			
			if (persons.size() == 0 && !isNoPeople) getPersons(false);
			setNoDatasGone();
			
			if (isNoPeople) noPeople.setVisibility(View.VISIBLE);
			
			if (haveGotPeople) setFilterAndSortByVisible(isNoPeople);
			
		} else if (v == competitorsBtn) {
			
			setAllListViewGone();
			setListViewVisible(competitorList);
			setFilterVisible(View.GONE);
			setSortByVisible(View.GONE);
			
			typeChecked = typeCompetitors;
			setCategoryButtonDefault();
			setSelectedButton(competitorsBtn);
			
			if (competitors.size() == 0 && !isNoCompetitor) getCompetitors(false);
			setNoDatasGone();
			if (isNoCompetitor) noCompetitors.setVisibility(View.VISIBLE);
			
			if (haveGotCompetitors) setFilterAndSortByVisible(isNoCompetitor);
			
		} else if (v == shareGagein) {
			
			startActivitySimple(ShareActivity.class);
			
		} else if (v == relatedCompanyName) {
			Intent intent = new Intent();
			intent.setClass(mContext, CompanyActivity.class);
			intent.putExtra(Constant.COMPANYID, mCompany.relatedCompany.orgID);
			startActivity(intent);
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		if (typeChecked == typeCompetitors) {
			if (Constant.currentCompetitorIndustries.size() == 0) {
				getCompetitorsFilter();
			}
		}
		
	}

	private void setSortByVisible(int visible) {
		sortByLayout.setVisibility(visible);
		setSortByButton();
	}

	private void setSortByButton() {
		if (typeChecked == typePeople) {
			if (Constant.PEOPLE_SORT_BY == 0) {
				setSortByBtnText(R.string.job_level);
			} else if (Constant.PEOPLE_SORT_BY == 1) {
				setSortByBtnText(R.string.name);
			} else if (Constant.PEOPLE_SORT_BY == 2) {
				setSortByBtnText(R.string.recent);
			}
		} else if (typeChecked == typeCompetitors) {
			if (Constant.COMPETITOR_SORT_BY == APIHttpMetadata.kCompetitorOrderByEmployeeSize) {
				setSortByBtnText(R.string.employee_size);
			} else if (Constant.COMPETITOR_SORT_BY == APIHttpMetadata.kCompetitorOrderByRevenueSize) {
				setSortByBtnText(R.string.revenue_size);
			} else if (Constant.COMPETITOR_SORT_BY == APIHttpMetadata.kCompetitorOrderByName) {
				setSortByBtnText(R.string.name);
			} else if (Constant.COMPETITOR_SORT_BY == APIHttpMetadata.KCOMPETITORORDERBYRELEVANCE) {
				setSortByBtnText(R.string.u_relevance);
			}
		}
	}
	
	private void setSortByBtnText(int stringId) {
		sortBy.setText(mContext.getResources().getString(stringId));
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		
		if (resultCode == RESULT_OK) {
			
			if (requestCode == 0) {//news filter
				
				newsList.setPullLoadEnable(false);
				getNews(false, 0, APIHttpMetadata.kGGPageFlagFirstPage, 0, true);
				
			} else if (requestCode == 1) {//people filter
				
				employeesPageNumber = Constant.PAGE_NUMBER_START;
				
				personList.setPullLoadEnable(false);
				getPersons(false);
				
			} else if (requestCode == 2) {//competitor filter
				
				requestPageNumber = Constant.PAGE_NUMBER_START;
				
				competitorList.setPullLoadEnable(false);
				getCompetitors(false);
				
			} else if (requestCode == 3) {//people sortBy
				
				setSortByButton();
				employeesPageNumber = Constant.PAGE_NUMBER_START;
				
				personList.setPullLoadEnable(false);
				getPersons(false);
				
			} else if (requestCode == 4) {//competitor sortBy
				
				setSortByButton();
				competitorSortBy = Constant.COMPETITOR_SORT_BY;
				industryid = Constant.COMPETITOR_FILTER_PARAM_VALUE;
				requestPageNumber = Constant.PAGE_NUMBER_START;
				
				competitorList.setPullLoadEnable(false);
				getCompetitors(false);
				
			}
		}
	}
	
	private void setNoDatasGone() {
		noNewsLayout.setVisibility(View.GONE);
		noCompetitors.setVisibility(View.GONE);
		noPeople.setVisibility(View.GONE);
	}
	
	private void setFilterVisible(int visible) {
		filterBtn.setVisibility(visible);
	}
	
	private void setAllListViewGone(){
		newsList.setVisibility(View.GONE);
		aboutList.setVisibility(View.GONE);
		personList.setVisibility(View.GONE);
		competitorList.setVisibility(View.GONE);
	}
	
	private void setListViewVisible(XListView listView) {
		listView.setVisibility(View.VISIBLE);
	}
	
	private void setCategoryButtonDefault() {
		setUnSelectedButton(newsBtn);
		setUnSelectedButton(aboutBtn);
		setUnSelectedButton(peopleBtn);
		setUnSelectedButton(competitorsBtn);
	}
	
	private void setSelectedButton(TextView button) {
		button.setTextColor(mContext.getResources().getColor(R.color.yellow));
		button.setBackgroundResource(R.drawable.linear_frame_yellow);
	}
	
	@SuppressLint("NewApi")
	private void setUnSelectedButton(TextView button) {
		button.setTextColor(mContext.getResources().getColor(R.color.text_weak));
		button.setBackground(null);
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
		if (typeChecked == typeNews) {
			if (position == 1 || position == updates.size() + 1) return;
		} else if (typeChecked == typePeople) {
			Intent intent = new Intent();
			intent.putExtra(Constant.PERSONID, persons.get(position - 1).id);
			intent.setClass(mContext, PersonActivity.class);
			startActivity(intent);
		} else if (typeChecked == typeCompetitors) {
			Intent intent = new Intent();
			intent.putExtra(Constant.COMPANYID, competitors.get(position - 1).validID());
			intent.setClass(mContext, CompanyActivity.class);
			startActivity(intent);
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		Constant.PEOPLE_SORT_BY = 0;
		Constant.currentJobLevelForCompanyPeopleFilter.clear();
		Constant.currentFunctionRoleForCompanyPeopleFilter.clear();
		Constant.currentLinkedProfileForCompanyPeopleFilter.clear();
		
		Constant.locationNewsTriggersForCompany.clear();
		
		Constant.COMPETITOR_SORT_BY = "noe";
		Constant.COMPETITOR_FILTER_PARAM_VALUE.clear();
		Constant.currentCompetitorIndustries.clear();
	}

	@Override
	public void onRefresh() {//no this function
	}

	@Override
	public void onLoadMore() {
		if (typeChecked == typeNews) {
			Update lastUpdate = updates.get(updates.size() - 1);
			long aNewsID = lastUpdate.newsId;
			long aPageTime = lastUpdate.date;
			getNews(true, aNewsID, APIHttpMetadata.kGGPageFlagMoveDown, aPageTime, false);
		} else if (typeChecked == typePeople) {
			employeesPageNumber++;
			getPersons(true);
		} else if (typeChecked == typeCompetitors) {
			requestPageNumber ++;
			getCompetitors(true);
		}
	}
	
	@SuppressLint("NewApi")
	@Override
	protected void setOnClickListener() {
		super.setOnClickListener();
		newsList.setPullRefreshEnable(false);
		aboutList.setPullRefreshEnable(false);
		aboutList.setPullLoadEnable(false);
		personList.setPullRefreshEnable(false);
		competitorList.setPullRefreshEnable(false);
		
		newsList.setOnItemClickListener(this);
		aboutList.setOnItemClickListener(this);
		personList.setOnItemClickListener(this);
		competitorList.setOnItemClickListener(this);
		
		newsList.setXListViewListener(this);
		aboutList.setXListViewListener(this);
		personList.setXListViewListener(this);
		competitorList.setXListViewListener(this);
		
		newsBtn.setOnClickListener(this);
		aboutBtn.setOnClickListener(this);
		peopleBtn.setOnClickListener(this);
		competitorsBtn.setOnClickListener(this);
		filterBtn.setOnClickListener(this);
		sortBy.setOnClickListener(this);
		relatedCompanyName.setOnClickListener(this);
		
		shareGagein.setOnClickListener(this);
		
	}

}
