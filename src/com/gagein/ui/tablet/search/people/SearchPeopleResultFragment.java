package com.gagein.ui.tablet.search.people;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.gagein.R;
import com.gagein.adapter.search.SearchPersonFilterAdapter;
import com.gagein.component.AutoNewLineLayout;
import com.gagein.component.dialog.SaveSearchDialog;
import com.gagein.component.dialog.SortBySearchDialog;
import com.gagein.component.xlistview.XListView;
import com.gagein.component.xlistview.XListView.IXListViewListener;
import com.gagein.http.APIHttp;
import com.gagein.http.APIParser;
import com.gagein.model.DataPage;
import com.gagein.model.SearchPerson;
import com.gagein.model.filter.FilterItem;
import com.gagein.model.filter.Filters;
import com.gagein.model.filter.Industry;
import com.gagein.model.filter.JobTitle;
import com.gagein.model.filter.Location;
import com.gagein.model.filter.QueryInfo;
import com.gagein.model.filter.QueryInfoItem;
import com.gagein.ui.BaseFragment;
import com.gagein.ui.company.PersonActivity;
import com.gagein.util.CommonUtil;
import com.gagein.util.Constant;
import com.gagein.util.Log;


public class SearchPeopleResultFragment extends BaseFragment implements IXListViewListener, OnItemClickListener{
	
	private TextView showDetailsTx;
	private TextView sortByText;
	private LinearLayout sortByDetailLayout;
	private AutoNewLineLayout personalInfoLayout;
	private AutoNewLineLayout employerInfoLayout;
	private XListView listView;
	private Boolean showDetails = false;
	private int PAGENUM = 1;
	private Filters mFilters;
	private List<FilterItem> sortByList;
	private List<SearchPerson> seachedPersons = new ArrayList<SearchPerson>();
	private RelativeLayout emptyLayout;
	private QueryInfo queryInfo;
	private SearchPersonFilterAdapter personAdapter;
	private String type = "con";
	private String employer = "employer";
	private String personal = "personal";
	private OnHideLeftLayout onHideLeftLayout;
	private LinearLayout wholeLayout;
	private LinearLayout personalLayout;
	private LinearLayout employerLayout;
	private TextView rankText;
	private OnUpdateFilterStatusForPeople onUpdateFilterStatusForPeople;
	
	public interface OnHideLeftLayout {
		public void onHideLeftLayout();
	}
	
	public interface OnUpdateFilterStatusForPeople {
		public void onUpdateFilterStatusForPeople();
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			onHideLeftLayout = (OnHideLeftLayout) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + "must implement OnHideLeftLayout");
		}
		try {
			onUpdateFilterStatusForPeople = (OnUpdateFilterStatusForPeople) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + "must implement OnUpdateFilterStatusForPeople");
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.activity_people_found, container, false);
		mContext = getActivity();
		mApiHttp = new APIHttp(mContext);
		
		doInit();
		return view;
	}
	
	@Override
	protected void initView() {
		super.initView();
		setTitle(R.string.results);
		setLeftButton(R.string.filters);
		setRightButton(R.string.u_save);
		
		showDetailsTx = (TextView) view.findViewById(R.id.showDetails);
		sortByText = (TextView) view.findViewById(R.id.sortBy);
		sortByDetailLayout = (LinearLayout) view.findViewById(R.id.sortByDetailLayout);
		personalInfoLayout = (AutoNewLineLayout) view.findViewById(R.id.pesonalInfoLayout);
		employerInfoLayout = (AutoNewLineLayout) view.findViewById(R.id.employerInfoLayout);
		listView = (XListView) view.findViewById(R.id.listView);
		emptyLayout = (RelativeLayout) view.findViewById(R.id.emptyLayout);
		wholeLayout = (LinearLayout) view.findViewById(R.id.wholeLayout);
		personalLayout = (LinearLayout) view.findViewById(R.id.personalLayout);
		employerLayout = (LinearLayout) view.findViewById(R.id.employerLayout);
		rankText = (TextView) view.findViewById(R.id.rank);
		
		CommonUtil.setLayoutWith(wholeLayout, getActivity());
	}
	
	@Override
	protected void initData() {
		super.initData();
		
		mFilters = Constant.MFILTERS;
		
		setSortBy();
	}
	
	public void searchSavedResult(String savedId) {
		showLoadingDialog(mContext);
		PAGENUM = 1;
		mApiHttp.getSavedSearch(savedId, PAGENUM, new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject jsonObject) {
				APIParser parser = new APIParser(jsonObject);
				if (parser.isOK()) {
					parserIsOk(false, parser);
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
	
	public void initSearch() {
		PAGENUM = 1;
		searchAdvancedPersons(false);
	}
	
	private void setTitle(long num) {
		if (num == 0) {
			setTitle("No people found");
		} else {
			setTitle(CommonUtil.splitNumberByComma(num) + " people found");
		}
	}
	
	public void setSortBy() {
		
		sortByList = mFilters.getSortByFromContact();
		for (int i = 0; i < sortByList.size(); i ++) {
			if (sortByList.get(i).getChecked()) {
				String value = sortByList.get(i).getValue();
				sortByText.setText(value);
				setRank(value);
			}
		}
	}
	
	private void setRank(String value) {
		if (value.equalsIgnoreCase("Job Level") || value.equalsIgnoreCase("Company Name")) {
			rankText.setText(!Constant.REVERSE ?  " : High-Low" : " : Low-High");
		} else if (value.equalsIgnoreCase("Name")) {
			rankText.setText(!Constant.REVERSE ? " : Z-A" : " : A-Z");
		} else if (value.equalsIgnoreCase("Search Relevance")) {
			Constant.REVERSE = false;
			rankText.setText("");
		}
	}
	
	private void searchAdvancedPersons(final Boolean loadMore) {
		
		if (!loadMore) showLoadingDialog(mContext);
		mApiHttp.searchAdvancedPersons(PAGENUM, CommonUtil.packageRequestDataForCompanyOrPeople(false, true).get(0), new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject jsonObject) {
				APIParser parser = new APIParser(jsonObject);
				if (parser.isOK()) {
					parserIsOk(loadMore, parser);
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
	
	private void setSavedStatus(JSONObject jsonObject) {
		
		String savedId = jsonObject.optString("mss_search_name");
		rightBtn.setText(TextUtils.isEmpty(savedId) ? R.string.u_save : R.string.Saved);
		
	}
	
	public void setSaved() {
		rightBtn.setText(R.string.Saved);
	}
	
	private void parserIsOk(final Boolean loadMore, APIParser parser) {
		
		if (!loadMore) seachedPersons.clear();
		
		//根据mssid来判断是否saved
		setSavedStatus(parser.data());
		
		//将API返回数据同步到本地的filter options
		queryInfo = parser.parserQueryInfo(false);
		
		//set query info layout
		personalInfoLayout.removeAllViews();
		employerInfoLayout.removeAllViews();
		setPersonalInfoLayout();
		setEmployerInfoLayout();
		
		onUpdateFilterStatusForPeople.onUpdateFilterStatusForPeople();
		
		DataPage dataPage = parser.parseGetSearchPeople();
		if (dataPage.items != null) {
			for (Object obj : dataPage.items) {
				if (obj instanceof SearchPerson) {
					seachedPersons.add((SearchPerson)obj);
				}
			}
		}
		Boolean haveMoreNews = dataPage.hasMore;
		listView.setPullLoadEnable(haveMoreNews);
		if (!loadMore) setPersons();
		CommonUtil.setViewHeight(listView, listView.getAdapter().getCount() * CommonUtil.dp2px(mContext, 71));
		PAGENUM ++;
		
		//set title
		long total = parser.data().optLong("total");
		setTitle(total);
		
		if (seachedPersons.size() == 0) {
			emptyLayout.setVisibility(View.VISIBLE);
			listView.setVisibility(View.GONE);
		} else {
			emptyLayout.setVisibility(View.GONE);
			listView.setVisibility(View.VISIBLE);
		}
	}
	
	private void setPersons() {
		personAdapter = new SearchPersonFilterAdapter(mContext, seachedPersons);
		listView.setAdapter(personAdapter);
		personAdapter.notifyDataSetChanged();
		personAdapter.notifyDataSetInvalidated();
	}
	
	@Override
	protected void setOnClickListener() {
		super.setOnClickListener();
		
		showDetailsTx.setOnClickListener(this);
		sortByText.setOnClickListener(this);
		rankText.setOnClickListener(this);
		listView.setXListViewListener(this);
		listView.setOnItemClickListener(this);
		listView.setPullRefreshEnable(false);
		
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		
		if (v == leftBtn) {
			
			onHideLeftLayout.onHideLeftLayout();
			
		} else if (v == rightBtn) {
			
			if (rightBtn.getText().toString().equalsIgnoreCase("Saved")) return;
			
			SaveSearchDialog dialog = new SaveSearchDialog(mContext, type, CommonUtil.packageRequestDataForCompanyOrPeople(false, false).get(0), queryInfo.getQueryInfoResult());
			dialog.showDialog(Constant.SEARCH_PEOPLE);
			
		} else if (v == showDetailsTx) {
			
			showDetails = !showDetails;
			sortByDetailLayout.setVisibility(showDetails ? View.VISIBLE : View.GONE);
			showDetailsTx.setText(showDetails ? R.string.hide_details : R.string.show_details);
			
		} else if (v == sortByText) {
			
			SortBySearchDialog dialog = new SortBySearchDialog(mContext, this, false);
			dialog.showDialog();
			
		} else if (v == rankText) {
			
			String value = rankText.getText().toString();
			if (!value.isEmpty()) {
				Constant.REVERSE = !Constant.REVERSE;
				setRank(sortByText.getText().toString());
			} else {
				return;
			}
			initSearch();
			
		}
	}
	
	private void setInfoDetailButton(List<QueryInfoItem> queryInfoItemList, final String type, List<QueryInfoItem> queryInfoItemList2) {
		
		if (null != queryInfoItemList) {
			
			for (int i = 0; i < queryInfoItemList.size(); i ++) {
				
				final View view = LayoutInflater.from(mContext).inflate(R.layout.sort_button, null);
				LinearLayout buttonLayout = (LinearLayout) view.findViewById(R.id.buttonLayout);
				TextView textView = (TextView) view.findViewById(R.id.text);
				final QueryInfoItem queryInfoItem = queryInfoItemList.get(i);
				final String queryType = queryInfoItem.getType();
				
				buttonLayout.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						
						//判断是否有选中条件 
						Log.v("silen", "queryType00 = " + queryType);
						List<QueryInfoItem> conditions = queryInfo.allConditions(false);
						if (conditions.size() <= 1) {
							if (!(personalInfoLayout.getChildCount() >= 1 && employerInfoLayout.getChildCount() >=1)) {
								showShortToast("You have to enter in search criteria! Try again.");
								return;
							}
						} else if (conditions.size() == 2) {
							QueryInfoItem condition1 = conditions.get(0);
							QueryInfoItem condition2 = conditions.get(1);
							if (condition1.getType().equalsIgnoreCase("search_date_range")
									|| condition2.getType().equalsIgnoreCase("search_date_range")) {
								showShortToast("You have to enter in search criteria! Try again.");
								return;
							}
						}
						
						if (type.equalsIgnoreCase(employer)) {
							employerInfoLayout.removeView(view);
						} else if (type.equalsIgnoreCase(personal)) {
							personalInfoLayout.removeView(view);
						}
						//TODO 数据删除
						String id = queryInfoItem.getId();
						Filters mFilters = Constant.MFILTERS;
						
						if (queryType.equalsIgnoreCase("mer_for_id")) {
							
							List<FilterItem> newsTriggerList = mFilters.getNewsTriggers();
							deleteFilters(id, newsTriggerList);
							
						} else if (queryType.equalsIgnoreCase("search_company_for_type")) {
							
//							if (id.trim().equalsIgnoreCase("4")) return;
							List<FilterItem> companiesList = mFilters.getCompanyTypesFromPeople();
							deleteFilters(id, companiesList);
							
						} else if (queryType.equalsIgnoreCase("rank")) {
							
							List<FilterItem> rankList = mFilters.getRanks();
							deleteFilters(id, rankList);
							
						} else if (queryType.equalsIgnoreCase("org_fiscal_month")) {
							
							List<FilterItem> fiscalMonthList = mFilters.getFiscalYearEndMonths();
							deleteFilters(id, fiscalMonthList);
							
						} else if (queryType.equalsIgnoreCase("milestone_occurrence_type")) {
							
							List<FilterItem> mileStoneDateRangeList = mFilters.getMileStoneDateRange();
							deleteFilters(id, mileStoneDateRangeList);
							
						} else if (queryType.equalsIgnoreCase("org_industries")) {
							List<Industry> industryList = mFilters.getIndustries();
							for (int j = 0; j < industryList.size(); j++) {
								Industry industry = industryList.get(j);
								if (industry.getChecked()) {
									List<Industry> chiIndustries = industry.getChildrens();
									deleteIndustryFilters(id, industryList);
									deleteIndustryFilters(id, chiIndustries);
								}
							}
							
						} else if (queryType.equalsIgnoreCase("location_code")) {
							
							List<Location> locationList = mFilters.getHeadquarters();
							deleteLocationFilters(id, locationList);
							
						} else if (queryType.equalsIgnoreCase("org_employee_size")) {
							
							List<FilterItem> employeeSizeList = mFilters.getEmployeeSizeFromBuz();
							deleteFilters(id, employeeSizeList);
							
						} else if (queryType.equalsIgnoreCase("search_date_range")) {
							
							List<FilterItem> ranks = mFilters.getDateRanges();
							deleteFilters(id, ranks);
							
						} else if (queryType.equalsIgnoreCase("milestone_type")) {
							
							List<FilterItem> mileStoneList = mFilters.getMileStones();
							deleteFilters(id, mileStoneList);
							
						} else if (queryType.equalsIgnoreCase("org_ownership")) {
							
							List<FilterItem> ownershipList = mFilters.getOwnerships();
							deleteFilters(id, ownershipList);
							
						} else if (queryType.equalsIgnoreCase("org_revenue_size")) {
							
							List<FilterItem> revenueSizeList = mFilters.getSalesVolumeFromBuz();
							deleteFilters(id, revenueSizeList);
							
						}
						//TODO
						else if (queryType.equalsIgnoreCase("dop_title")) {
							List<JobTitle> titleList = mFilters.getJobTitles();
							deleteJobTitleFilters(id, titleList);
						} else if (queryType.equalsIgnoreCase("dop_level")) {
							List<FilterItem> levelList = mFilters.getJobLevel();
							deleteFilters(id, levelList);
						} else if (queryType.equalsIgnoreCase("dop_address")) {
							List<Location> locationList = mFilters.getLocations();
							deleteLocationFilters(id, locationList);
						} else if (queryType.equalsIgnoreCase("dop_functional_role")) {
							List<FilterItem> functionalRoleList = mFilters.getFunctionalRoles();
							deleteFilters(id, functionalRoleList);
						} else if (queryType.equalsIgnoreCase("filter_saved_company_search")) {//TODO
							List<FilterItem> companyTypes = mFilters.getCompanyTypesFromPeople();
							for (int j = 0; j < companyTypes.size(); j++) {
								companyTypes.get(j).setChecked((j == companyTypes.size() -1) ? true : false);
							}
							PAGENUM = 1;
							searchAdvancedPersons(false);
						}
						
						Intent intent = new Intent();
						intent.setAction(Constant.BROADCAST_REFRESH_FILTER);
						mContext.sendBroadcast(intent);
					}

				});

				if (null != queryInfoItemList2) {
					queryInfoItem.setDisplayName(queryInfoItemList2);
				}
				
				textView.setText(queryInfoItem.getDisplayName());
				
				CommonUtil.setFilterMaxWith(textView);
				
				if (type.equalsIgnoreCase(employer)) {
					employerInfoLayout.addView(view);
				} else if (type.equalsIgnoreCase(personal)) {
					personalInfoLayout.addView(view);
				}
			}
		}
	}
	
	private void setPersonalInfoLayout() {
		
		//Job Title
		final String jobTitleStr = queryInfo.getJobTitle();
		
		if (null != jobTitleStr && !jobTitleStr.isEmpty()) {
			
			final View view = LayoutInflater.from(mContext).inflate(R.layout.sort_button, null);
			LinearLayout buttonLayout = (LinearLayout) view.findViewById(R.id.buttonLayout);
			TextView textView = (TextView) view.findViewById(R.id.text);
			
			buttonLayout.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					
					for (int i = 0; i < mFilters.getJobTitles().size(); i ++) {
						
						if (mFilters.getJobTitles().get(i).getChecked()) {
							
							if (mFilters.getJobTitles().get(i).getName().equalsIgnoreCase(jobTitleStr)) {
								mFilters.getJobTitles().get(i).setChecked(false);
								PAGENUM = 1;
								searchAdvancedPersons(false);
							}
						}
					}
				}
				
			});
			
			textView.setText(jobTitleStr);
			
			CommonUtil.setFilterMaxWith(textView);
			
			personalInfoLayout.addView(view);
		}	
		
		//Job Level
		List<QueryInfoItem> jobLevelList = queryInfo.getJobLevels();
		setInfoDetailButton(jobLevelList, personal, null);
		
		//People Location
		final List<Location> peopleLocationCodes = queryInfo.getLocations();
		
		if (null != peopleLocationCodes) {
			
			for (int i = 0; i < peopleLocationCodes.size(); i ++) {
				
				final View view = LayoutInflater.from(mContext).inflate(R.layout.sort_button, null);
				LinearLayout buttonLayout = (LinearLayout) view.findViewById(R.id.buttonLayout);
				TextView textView = (TextView) view.findViewById(R.id.text);
				final Location peopleLocationCode = peopleLocationCodes.get(i);
				
				buttonLayout.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						String id = peopleLocationCode.getCode();
						
						for (int j = 0; j < mFilters.getLocations().size(); j++) {
							
							if (id.equalsIgnoreCase(mFilters.getLocations().get(j).getCode())) {
								mFilters.getLocations().get(j).setChecked(false);
								PAGENUM = 1;
								searchAdvancedPersons(false);
								return;
							}
							
						}
					}
				});
				
				textView.setText(peopleLocationCode.getLocation());
				
				CommonUtil.setFilterMaxWith(textView);
				
				personalInfoLayout.addView(view);
			} 
		}
		
		//Functional Role
		List<QueryInfoItem> functionalRoleList = queryInfo.getFunctionalRoles();
		setInfoDetailButton(functionalRoleList, personal, null);
		
		int childCount = personalInfoLayout.getChildCount();
		personalLayout.setVisibility((childCount == 0) ? View.GONE : View.VISIBLE);
	}
	
	
	private void setEmployerInfoLayout() {
		
		//NewsTriggers
		List<QueryInfoItem> newsTriggersList = queryInfo.getNewsTriggers();
		setInfoDetailButton(newsTriggersList, employer, null);
		
		//EventSearchKeywords
		if (null != queryInfo.getEventSearchKeywords()) {
			String eventSearchKeywords = queryInfo.getEventSearchKeywords().getName();
			if (!TextUtils.isEmpty(eventSearchKeywords)) {
				String type = queryInfo.getEventSearchKeywords().getType();
				setEventSearchKeywordsButton(eventSearchKeywords, type);
			}
		}
		
		//DateRange
		List<QueryInfoItem> dateRangeList = queryInfo.getDateRange();
		setInfoDetailButton(dateRangeList, employer, null);
		
//		//Companies
//		List<QueryInfoItem> companiesList = queryInfo.getCompaniesForPeople();
//		setInfoDetailButton(companiesList, employer, null);
		
		//Companies
		String companySearchKeywords = queryInfo.getCompanySearchKeywords();
		if (!companySearchKeywords.isEmpty()) {
			
			final View view = LayoutInflater.from(mContext).inflate(R.layout.sort_button, null);
			LinearLayout buttonLayout = (LinearLayout) view.findViewById(R.id.buttonLayout);
			TextView textView = (TextView) view.findViewById(R.id.text);
			
			buttonLayout.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					
					int filterButtonCount = employerInfoLayout.getChildCount() + personalInfoLayout.getChildCount();
					if (filterButtonCount == 1) {
						showShortToast("You have to enter in search criteria! Try again.");
						return;
					}
					employerInfoLayout.removeView(view);
					Constant.COMPANY_SEARCH_KEYWORDS = "";
					
					List<FilterItem> companyTypes = mFilters.getCompanyTypesFromCompany();
					for (int i = 0; i < companyTypes.size(); i ++) {
						companyTypes.get(i).setChecked(i == 2 ? true : false);
					}
					
					PAGENUM = 1;
					searchAdvancedPersons(false);
					
				}
				
			});
			
			textView.setText(companySearchKeywords);
			
			CommonUtil.setFilterMaxWith(textView);
			
			employerInfoLayout.addView(view);
		}
		
		
		List<QueryInfoItem> companiesList = queryInfo.getCompaniesForPeople();
		setInfoDetailButton(companiesList, employer, null);
		
		//SavedCompanies
		List<QueryInfoItem> savedCompanies = queryInfo.getSavedCompany();
		setInfoDetailButton(savedCompanies, employer, null);
		
		
		//LocationCode
		List<QueryInfoItem> locationList = queryInfo.getLocationCode();
		setInfoDetailButton(locationList, employer, null);
		
		//Industries
		List<QueryInfoItem> industriesList = queryInfo.getIndustries();
		setInfoDetailButton(industriesList, employer, null);
		
		//EmployeeSize
		List<QueryInfoItem> employeeSizeList = queryInfo.getEmployeeSize();
		setInfoDetailButton(employeeSizeList, employer, null);
		
		//RevenueSize
		List<QueryInfoItem> revenueSizeList = queryInfo.getRevenueSize();
		setInfoDetailButton(revenueSizeList, employer, null);
		
		
		//Ownership
		List<QueryInfoItem> ownershipList = queryInfo.getOwnership();
		setInfoDetailButton(ownershipList, employer, null);
		
		//MileStoneOccurrenceType
		List<QueryInfoItem> mileStoneOccurrenceTypeList = queryInfo.getMileStoneOccurrenceType();
		
		//MileStoneType
		int mileStoneOccurrenceTypeListSize = mileStoneOccurrenceTypeList == null ? 0 : mileStoneOccurrenceTypeList.size();
		
		if (mileStoneOccurrenceTypeListSize > 0) {
			
			List<QueryInfoItem> mileStoneTypeList = queryInfo.getMileStoneType();
			setInfoDetailButton(mileStoneTypeList, employer, mileStoneOccurrenceTypeList);
		}
		
		//Ranks
		List<QueryInfoItem> ranksList = queryInfo.getRanks();
		setInfoDetailButton(ranksList, employer, null);
		
		//FiscalMonth
		List<QueryInfoItem> fiscalMonthList = queryInfo.getFiscalMonth();
		setInfoDetailButton(fiscalMonthList, employer, null);
		
		int childCount = employerInfoLayout.getChildCount();
		employerLayout.setVisibility((childCount == 0) ? View.GONE : View.VISIBLE);
	}
	
	/**
	 * delete filter options
	 * @param id
	 * @param filterItems
	 */
	private void deleteFilters(String id, List<FilterItem> filterItems) {
		for (int i = 0; i < filterItems.size(); i ++) {
			if (id.equalsIgnoreCase(filterItems.get(i).getKey())) {
				filterItems.get(i).setChecked(false);
				PAGENUM = 1;
				searchAdvancedPersons(false);
			}
		}
	}
	
	private void deleteIndustryFilters(String id, List<Industry> industries) {
		for (int i = 0; i < industries.size(); i ++) {
			if (id.equalsIgnoreCase(industries.get(i).getId())) {
				industries.get(i).setChecked(false);
				PAGENUM = 1;
				searchAdvancedPersons(false);
			}
		}
	}
	
	private void deleteLocationFilters(String id, List<Location> locations) {
		for (int i = 0; i < locations.size(); i ++) {
			if (id.equalsIgnoreCase(locations.get(i).getCode())) {
				locations.get(i).setChecked(false);
				PAGENUM = 1;
				searchAdvancedPersons(false);
			}
		}
	}
	
	private void deleteJobTitleFilters(String id, List<JobTitle> jobTitles) {
		for (int i = 0; i < jobTitles.size(); i ++) {
			if (id.equalsIgnoreCase(jobTitles.get(i).getId())) {
				jobTitles.get(i).setChecked(false);
				PAGENUM = 1;
				searchAdvancedPersons(false);
			}
		}
	}
	
	private void setEventSearchKeywordsButton(String value, final String type) {
		
		
		final View view = LayoutInflater.from(mContext).inflate(R.layout.sort_button, null);
		LinearLayout buttonLayout = (LinearLayout) view.findViewById(R.id.buttonLayout);
		TextView textView = (TextView) view.findViewById(R.id.text);
		
		buttonLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				employerInfoLayout.removeView(view);
				//TODO 数据删除
				Log.v("silen", "type00 = " + type);
				Constant.ALLWORDS_FOR_TRIGGERS = "";
//				Constant.EXACTWORDS = "";
//				Constant.ANYWORDS = "";
//				Constant.NONEWORDS = "";
				
				Constant.COMPANY_SEARCH_KEYWORDS = "";
				
				List<FilterItem> peopleTypes = mFilters.getCompanyTypesFromPeople();
				for (int i = 0; i < peopleTypes.size(); i ++) {
					peopleTypes.get(i).setChecked(i == 2 ? true : false);
				}
				
				PAGENUM = 1;
				searchAdvancedPersons(false);
			}
		});
		
		Log.v("silen", "value = " + value);
		
		textView.setText(value);
		
		CommonUtil.setFilterMaxWith(textView);
		
		employerInfoLayout.addView(view);
		
	}


	@Override
	public void onRefresh() {
		PAGENUM = 1;
		searchAdvancedPersons(false);
	}

	@Override
	public void onLoadMore() {
		searchAdvancedPersons(true);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		if (Constant.ZERO == position || position == seachedPersons.size() + 1) return;
		Intent intent = new Intent();
		intent.putExtra(Constant.PERSONID, seachedPersons.get(position - 1).id);
		intent.setClass(mContext, PersonActivity.class);
		startActivity(intent);
	}
}
