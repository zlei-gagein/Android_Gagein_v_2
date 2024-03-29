package com.gagein.ui.tablet.company;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
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
import com.gagein.component.dialog.CompanySortByDialog;
import com.gagein.component.xlistview.XListView;
import com.gagein.component.xlistview.XListView.IXListViewListener;
import com.gagein.http.APIHttp;
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
import com.gagein.ui.BaseFragment;
import com.gagein.ui.company.CompanyActivity;
import com.gagein.ui.company.PersonActivity;
import com.gagein.util.ActivityHelper;
import com.gagein.util.CommonUtil;
import com.gagein.util.Constant;
import com.gagein.util.Log;

@SuppressLint("ValidFragment")
public class CompanyFragment extends BaseFragment implements OnItemClickListener, IXListViewListener{
	
	private XListView newsList;
	private XListView aboutList;
	private XListView personList;
	private XListView competitorList;
	public CompanyNewsAdapter newsAdapter;
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
	private int employeesPageNumber = Constant.PAGE_NUMBER_START;
	private DataPage dpPersons;
	private DataPage dpCompetitors;
	private List<FacetItem> personFacetItems;
	private List<Person> persons = new ArrayList<Person>();
	private List<Company> competitors = new ArrayList<Company>();
	private int competitorsPageNumber = Constant.PAGE_NUMBER_START;
	private TextView newsBtn;
	private TextView aboutBtn;
	private TextView peopleBtn;
	private TextView competitorsBtn;
	private TextView filterBtn;
	private LinearLayout wholeLayout;
	private Facet facet;
	private int typeChecked = 0;
	private int typeNews = 0;
	private int typeAbout = 1;
	private int typePeople = 2;
	private int typeCompetitors = 3;
	private List<FacetItemIndustry> competitorIndustries = new ArrayList<FacetItemIndustry>();
	private Facet competitorFacet;
	public List<String> industryid = new ArrayList<String>();
	public String competitorSortBy = "";
	private OnNewsFilterClickListener onNewsFilterClickListener;
	private LeftBtnClick leftBtnClick;
	private OnCompetitorFilterClickListener onCompetitorFilterClickListener;
	private OnPeopleFilterClickListener onPeopleFilterClickListener;
	
	private Boolean isNoNews = false;
	private Boolean isNoPeople = false;
	private Boolean isNoCompetitor = false;
	private LinearLayout noCompetitors;
	private LinearLayout noPeople;
	private List<Update> filings = new ArrayList<Update>();
	private RelativeLayout sortByLayout;
	private TextView sortBy;
	private ScrollView noNewsLayout;
	private LinearLayout resourceShowLayout;
	private Boolean haveProcessed = false;
	private Boolean haveGotPeople = false;;
	private Boolean haveGotCompetitors = false;
	private TextView provisionPt;
	private RelativeLayout provisionBottomLayout;
	private LinearLayout provisionedLayout;
	private LinearLayout noNewsShowLayout;
	private TextView noNewsPt;
	private ListView parentsOrSubsidiariesList;
	private List<Resource> resources = new ArrayList<Resource>();
	private LinearLayout resourceParentLayout;
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
	private String provisionDateStr;
	private LinearLayout layout;
	private TextView noLongerOperating;
	private TextView relatedCompanyName;
	private LinearLayout relatedLayout;
	
	public interface OnNewsFilterClickListener {
		public void onNewsFilterClickListener();
	}
	
	public interface OnCompetitorFilterClickListener {
		public void onCompetitorFilterClickListener();
	}
	
	public interface OnPeopleFilterClickListener {
		public void onPeopleFilterClickListener();
	}
	
	public interface LeftBtnClick {
		public void onLeftBtnClick();
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			onNewsFilterClickListener = (OnNewsFilterClickListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + "must implement OnNewsFilterClickListener");
		}
		try {
			leftBtnClick = (LeftBtnClick) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + "must implement LeftBtnClick");
		}
		try {
			onCompetitorFilterClickListener = (OnCompetitorFilterClickListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + "must implement OnCompetitorFilterClickListener");
		}
		try {
			onPeopleFilterClickListener = (OnPeopleFilterClickListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + "must implement OnPeopleFilterClickListener");
		}
	}
	
	public CompanyFragment(long mCompanyId) {
		super();
		this.mCompanyId = mCompanyId;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.activity_company, container, false);
		mContext = getActivity();
		mApiHttp = new APIHttp(mContext);
		
		doInit();
		return view;
	}
	
	@Override
	protected void initView() {
		super.initView();
		setLeftImageButton(R.drawable.back_arrow);
		newsList = (XListView) view.findViewById(R.id.newsList);
		aboutList = (XListView) view.findViewById(R.id.aboutList);
		personList = (XListView) view.findViewById(R.id.personList);
		competitorList = (XListView) view.findViewById(R.id.competitorList);
		wholeLayout = (LinearLayout) view.findViewById(R.id.wholeLayout);
		companyLog = (ImageView) view.findViewById(R.id.companyLog);
		followImage = (ImageView) view.findViewById(R.id.followImage);
		companyName = (TextView) view.findViewById(R.id.companyName);
		websiteAndAddress = (TextView) view.findViewById(R.id.websiteAndAddress);
		noCompetitors = (LinearLayout) view.findViewById(R.id.noCompetitors);
		noPeople = (LinearLayout) view.findViewById(R.id.noPeople);
		sortByLayout = (RelativeLayout) view.findViewById(R.id.sortByLayout);
		noNewsLayout = (ScrollView) view.findViewById(R.id.noNewsLayout);
		resourceShowLayout = (LinearLayout) view.findViewById(R.id.resourceShowLayout);
		provisionPt = (TextView) view.findViewById(R.id.provisionPt);
		provisionBottomLayout = (RelativeLayout) view.findViewById(R.id.provisionBottomLayout);
		provisionedLayout = (LinearLayout) view.findViewById(R.id.provisionedLayout);
		noNewsShowLayout = (LinearLayout) view.findViewById(R.id.noNewsShowLayout);
		noNewsPt = (TextView) view.findViewById(R.id.noNewsPt);
		parentsOrSubsidiariesList = (ListView) view.findViewById(R.id.parentsOrSubsidiariesList);
		resourceParentLayout = (LinearLayout) view.findViewById(R.id.resourceParentLayout);
		
		newsBtn = (TextView) view.findViewById(R.id.news);
		aboutBtn = (TextView) view.findViewById(R.id.about);
		peopleBtn = (TextView) view.findViewById(R.id.people);
		competitorsBtn = (TextView) view.findViewById(R.id.competitors);
		filterBtn = (TextView) view.findViewById(R.id.filter);
		sortBy = (TextView) view.findViewById(R.id.sortBy);
		
		oneDayLayout = (RelativeLayout) view.findViewById(R.id.oneDayLayout);
		oneWeekLayout = (RelativeLayout) view.findViewById(R.id.oneWeekLayout);
		oneMonthLayout = (RelativeLayout) view.findViewById(R.id.oneMonthLayout);
		folRankDay = (TextView) view.findViewById(R.id.folRankDay);
		folRankWeek = (TextView) view.findViewById(R.id.folRankWeek);
		folRankMonth = (TextView) view.findViewById(R.id.folRankMonth);
		folScoreDay = (TextView) view.findViewById(R.id.folScoreDay);
		folScoreWeek = (TextView) view.findViewById(R.id.folScoreWeek);
		folScoreMonth = (TextView) view.findViewById(R.id.folScoreMonth);
		noLongerOperating = (TextView) view.findViewById(R.id.noLongerOperating);
		layout = (LinearLayout) view.findViewById(R.id.layout);
		relatedLayout = (LinearLayout) view.findViewById(R.id.relatedLayout);
		relatedCompanyName = (TextView) view.findViewById(R.id.relatedCompanyName);
		
		CommonUtil.setLayoutWith(wholeLayout, getActivity());
		newsList.setPullLoadEnable(false);
		aboutList.setPullLoadEnable(false);
		personList.setPullLoadEnable(false);
		competitorList.setPullLoadEnable(false);
	}
	
	@Override
	protected void initData() {
		super.initData();
		
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
	
	public void haveReadStory(Intent intent) {
		
		long newsId = intent.getLongExtra(Constant.NEWSID, 0);
		
		for (int i = 0; i < updates.size(); i ++) {
			if (updates.get(i).newsId == newsId) {
				updates.get(i).hasBeenRead = true;
				
			}
		}
		
	}
	
	public void refreshCompany(Intent intent, Boolean follow) {
		
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
	
	public void refreshNewsForFilter() {
		Log.v("silen", "refreshNewsForFilter");
		getNews(false, 0, APIHttpMetadata.kGGPageFlagFirstPage, 0, true);
	}
	
	public void setNews() {
		newsAdapter = new CompanyNewsAdapter(this, mContext, updates);
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
		
		showLoadingDialog(mContext);
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
				showConnectionError(mContext);
			}
		});
	}
	
	private void followCompany() {
		
		showLoadingDialog(mContext);
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
				showConnectionError(mContext);
			}
		});
	}
	
	public void setFollowButton() {
		setRightButton(mCompany.followed ? R.string.unfollow : R.string.follow);
	}
	
	public void resume() {
		if (typeChecked == typeCompetitors) {
			if (Constant.currentCompetitorIndustries.size() == 0) {
				getCompetitorsFilter();
			}
		}
	}
	
	public void getCompetitorsFilter() {
		mApiHttp.getSimilarCompanies(mCompanyId, industryid, competitorsPageNumber, competitorSortBy,  new Listener<JSONObject>() {

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
	
	public void getCompetitors(final Boolean loadMore, Boolean showDialog) {
		if (showDialog) showLoadingDialog(mContext);
		mApiHttp.getSimilarCompanies(mCompanyId, industryid, competitorsPageNumber, competitorSortBy,  new Listener<JSONObject>() {

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
							
							//TODO
//							if (Constant.currentCompetitorIndustries.size() <= 0) {
//							}
							Constant.currentCompetitorIndustries = competitorIndustries;
							
							List<Object> items = dpCompetitors.items;
							if (items != null) {
								for (Object obj : items) {
									competitors.add((Company)obj);
								}
							}
							
							if (competitors.size() == 0) {
								isNoCompetitor = true;
								noCompetitors.setVisibility(View.VISIBLE);
								competitorList.setPullLoadEnable(false);
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
				showConnectionError(mContext);
			}
		});
	}
	
	public void refreshPeopleForFilter() {
		employeesPageNumber = Constant.PAGE_NUMBER_START;
		getPersons(false);
	}
	
	public void refreshCompetitorsForFilter() {
		competitorsPageNumber = Constant.PAGE_NUMBER_START;
		industryid = Constant.COMPETITOR_FILTER_PARAM_VALUE;
		getCompetitors(false, true);
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
		if (!loadMore) showLoadingDialog(mContext);
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
//										if (item.id == selectedJobLevelID) {
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
										
//										if (item.id == selectedFunctionRoleID) {
////											hasSelecteID = true;
//										}
									}
								}
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
				showConnectionError(mContext);
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
			}
			
		}, new Response.ErrorListener() {
			
			@Override
			public void onErrorResponse(VolleyError error) {
				CommonUtil.dissmissLoadingDialog();
				CommonUtil.showLongToast(mContext.getResources().getString(R.string.connection_error));
			}
		});
	}
	
	public void getCompanyDetail() {
		
		if (mCompanyId == 0) return; 
		showLoadingDialog(mContext);
		mApiHttp.getCompanyOverview(mCompanyId, true, 0, new Listener<JSONObject>(){

			@Override
			public void onResponse(JSONObject jsonObject) {
				APIParser parser = new APIParser(jsonObject);
				if (null != jsonObject) {
					if (parser.isOK()) {
						
						getProvisionDateWithCompanyID();
						getNews(false, 0, APIHttpMetadata.kGGPageFlagFirstPage, 0, false);
						
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
				showConnectionError(mContext);
			}
		});
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
				showConnectionError(mContext);
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
	
	public void getNews(final boolean loadMore, final long aNewsID, byte aPageFlag, final long aPageTime, final boolean showDialog) {
		
		if (showDialog) showLoadingDialog(mContext);
		if (0 == mCompanyId) return;
			mApiHttp.getCompanyUpdatesNoFilter(getAgentsId(), mCompanyId, 0, aNewsID, aPageFlag, aPageTime,
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
							showConnectionError(mContext);
						}
					});
	};
	
	private boolean isFuckingAorB(String grade) {
		return grade.equalsIgnoreCase("a") 
				|| grade.equalsIgnoreCase("a1")
				|| grade.equalsIgnoreCase("a2")
				|| grade.equalsIgnoreCase("b");
	}
	
	public void refreshParentsAndSubsidiariesStatus(Intent intent, Boolean isFollow) {
		
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
			View view = LayoutInflater.from(mContext).inflate(R.layout.item_resource, null);
			TextView resourceUrl = (TextView) view.findViewById(R.id.resourceUrl);
			final String url = resources.get(i).url;
			resourceUrl.setText(url);
			resourceUrl.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					ActivityHelper.startWebActivity(url, mContext);
				}
			});
			resourceParentLayout.addView(view);
		}
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		
		if (v == leftImageBtn) {
			leftBtnClick.onLeftBtnClick();
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
			
		} else if (v == sortBy) {
			
			if (typeChecked == typePeople) {
				CompanySortByDialog dialog = new CompanySortByDialog(mContext, this, true);
				dialog.showDialog();
			} else if (typeChecked == typeCompetitors) {
				CompanySortByDialog dialog = new CompanySortByDialog(mContext, this, false);
				dialog.showDialog();
			}
			
		} else if (v == filterBtn) {
			
			if (typeChecked == typeNews) {
				onNewsFilterClickListener.onNewsFilterClickListener();
			} else if (typeChecked == typePeople) {
				onPeopleFilterClickListener.onPeopleFilterClickListener();
			} else if (typeChecked == typeCompetitors) {
				onCompetitorFilterClickListener.onCompetitorFilterClickListener();
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
			
			if (competitors.size() == 0 && !isNoCompetitor) getCompetitors(false, true);
			setNoDatasGone();
			
			if (isNoCompetitor) noCompetitors.setVisibility(View.VISIBLE);
			if (haveGotCompetitors) setFilterAndSortByVisible(isNoCompetitor);
			
		} else if (v == relatedCompanyName) {
			Intent intent = new Intent();
			intent.setClass(mContext, CommonUtil.isTablet(mContext) ? CompanyTabletActivity.class : CompanyActivity.class);
			intent.putExtra(Constant.COMPANYID, mCompany.relatedCompany.orgID);
			startActivity(intent);
		}
	}
	
	public void getPeopleBySortBy() {
		setSortByButton();
		getPersons(false);
	}
	
	public void getCompetitorsBySortBy() {
		setSortByButton();
		competitorSortBy = Constant.COMPETITOR_SORT_BY;
		industryid = Constant.COMPETITOR_FILTER_PARAM_VALUE;
		getCompetitors(false, true);
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
			intent.setClass(mContext, CompanyTabletActivity.class);
			startActivity(intent);
		}
	}

	@Override
	public void onRefresh() {//no this function
	}
	
	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
		}

	};

	@Override
	public void onLoadMore() {
		if (typeChecked == typeNews) {
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					
				}
			}, 10);
			Update lastUpdate = updates.get(updates.size() - 1);
			long aNewsID = lastUpdate.newsId;
			long aPageTime = lastUpdate.date;
			getNews(true, aNewsID, APIHttpMetadata.kGGPageFlagMoveDown, aPageTime, false);
		} else if (typeChecked == typePeople) {
			employeesPageNumber++;
			getPersons(true);
		} else if (typeChecked == typeCompetitors) {
			competitorsPageNumber ++;
			getCompetitors(true, false);
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
	}

}
