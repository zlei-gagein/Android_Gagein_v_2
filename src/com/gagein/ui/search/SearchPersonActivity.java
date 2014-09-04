package com.gagein.ui.search;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
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
import com.gagein.component.xlistview.XListView;
import com.gagein.component.xlistview.XListView.IXListViewListener;
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
import com.gagein.ui.company.PersonActivity;
import com.gagein.ui.main.BaseActivity;
import com.gagein.ui.search.people.filter.SortBySearchPersonActivity;
import com.gagein.util.CommonUtil;
import com.gagein.util.Constant;
import com.gagein.util.Log;

public class SearchPersonActivity extends BaseActivity implements IXListViewListener, OnItemClickListener{
	
	private TextView showDetailsTx;
	private TextView sortByText;
	private LinearLayout sortByDetailLayout;
	private AutoNewLineLayout pesonalInfoLayout;
	private AutoNewLineLayout employerInfoLayout;
	private XListView listView;
	private Boolean showDetails = false;
	private int requestCode = 1;
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
	private TextView rankText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_people_found);
		
		doInit();
	}
	
	@Override
	protected void initView() {
		super.initView();
		setTitle(R.string.results);
		setLeftImageButton(R.drawable.back_arrow);
		setRightButton(R.string.u_save);
		
		showDetailsTx = (TextView) findViewById(R.id.showDetails);
		sortByText = (TextView) findViewById(R.id.sortBy);
		sortByDetailLayout = (LinearLayout) findViewById(R.id.sortByDetailLayout);
		pesonalInfoLayout = (AutoNewLineLayout) findViewById(R.id.pesonalInfoLayout);
		employerInfoLayout = (AutoNewLineLayout) findViewById(R.id.employerInfoLayout);
		listView = (XListView) findViewById(R.id.listView);
		emptyLayout = (RelativeLayout) findViewById(R.id.emptyLayout);
		rankText = (TextView) findViewById(R.id.rank);
	}
	
	@Override
	protected void initData() {
		super.initData();
		
		mFilters = Constant.MFILTERS;
		
		setSortBy();
		
		searchAdvancedPersons(false);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		//TODO
		if (requestCode == this.requestCode && resultCode == RESULT_OK) {
			setSortBy();
			PAGENUM = 1;
			searchAdvancedPersons(false);
		}
	}
	
	public void searchSavedResult(String savedId) {
		showLoadingDialog();
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
				showConnectionError();
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
			setTitle(num + " people found");
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
			rankText.setText(Constant.REVERSE ? " : Low-High" : " : High-Low");
		} else if (value.equalsIgnoreCase("Name")) {
			rankText.setText(Constant.REVERSE ? " : Z-A" : " : A-Z");
		} else if (value.equalsIgnoreCase("Search Relevance")) {
			Constant.REVERSE = false;
			rankText.setText("");
		}
	}
	
	private void searchAdvancedPersons(final Boolean loadMore) {
		if (!loadMore) showLoadingDialog();
		mApiHttp.searchAdvancedPersons(PAGENUM, CommonUtil.packageRequestDataForCompanyOrPeople(false), new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject jsonObject) {
				
				APIParser parser = new APIParser(jsonObject);
				Log.v("silen", "jsonObject = " + jsonObject.toString());
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
				showConnectionError();
			}
		});
	}
	
	private void parserIsOk(final Boolean loadMore, APIParser parser) {
		if (!loadMore) seachedPersons.clear();
		
		//将API返回数据同步到本地的filter options
//		Constant.MFILTERS.getHeadquarters().clear();
//		Constant.MFILTERS.getJobTitles().clear();
//		Constant.MFILTERS.getLocations().clear();
//		Constant.REVERSE = false;
//		CommonUtil.resetFilters();
//		CommonUtil.initConSortBy();
		
		
		queryInfo = parser.parserQueryInfo(false);
		
		//set query info layout
		pesonalInfoLayout.removeAllViews();
		employerInfoLayout.removeAllViews();
		setEmployerInfoLayout();
		setPersonalInfoLayout();
		
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
		PAGENUM ++;
		
		//set title
		long total = parser.data().optLong("total");
		setTitle(total);
		
		if (seachedPersons.size() == 0) {
			emptyLayout.setVisibility(View.VISIBLE);
		} else {
			emptyLayout.setVisibility(View.GONE);
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
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		if (v == leftImageBtn) {
			finish();
		} else if (v == rightBtn) {
			SaveSearchDialog dialog = new SaveSearchDialog(mContext, type, CommonUtil.packageRequestDataForCompanyOrPeople(false), queryInfo.getQueryInfoResult());
			Window dialogWindow = dialog.getDialog().getWindow();
 	        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
 	        lp.width = CommonUtil.getDeviceWidth(SearchPersonActivity.this) - 100;
			dialog.showDialog();
		} else if (v == showDetailsTx) {
			showDetails = !showDetails;
			sortByDetailLayout.setVisibility(showDetails ? View.VISIBLE : View.GONE);
			showDetailsTx.setText(showDetails ? R.string.hide_details : R.string.show_details);
		} else if (v == sortByText) {
			Intent intent = new Intent();
			intent.setClass(mContext, SortBySearchPersonActivity.class);
			startActivityForResult(intent, requestCode);
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
	
	private void setInfoDetailButton(List<QueryInfoItem> queryInfoItemList, final String type) {
		if (null != queryInfoItemList) {
			for (int i = 0; i < queryInfoItemList.size(); i ++) {
				final View view = LayoutInflater.from(mContext).inflate(R.layout.sort_button, null);
				Button button = (Button) view.findViewById(R.id.button);
				final QueryInfoItem queryInfoItem = queryInfoItemList.get(i);
				button.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						if (type.equalsIgnoreCase(employer)) {
							employerInfoLayout.removeView(view);
						} else if (type.equalsIgnoreCase(personal)) {
							pesonalInfoLayout.removeView(view);
						}
						//TODO 数据删除
						String type = queryInfoItem.getType();
						Log.v("silen", "type = " + type);
						String id = queryInfoItem.getId();
						Filters mFilters = Constant.MFILTERS;
						if (type.equalsIgnoreCase("mer_for_id")) {
							List<FilterItem> newsTriggerList = mFilters.getNewsTriggers();
							deleteFilters(id, newsTriggerList);
						} else if (type.equalsIgnoreCase("search_company_for_type")) {
							List<FilterItem> companiesList = mFilters.getCompanyTypesFromCompany();
							deleteFilters(id, companiesList);
						} else if (type.equalsIgnoreCase("rank")) {
							List<FilterItem> rankList = mFilters.getRanks();
							deleteFilters(id, rankList);
						} else if (type.equalsIgnoreCase("org_fiscal_month")) {
							List<FilterItem> fiscalMonthList = mFilters.getFiscalYearEndMonths();
							deleteFilters(id, fiscalMonthList);
						} else if (type.equalsIgnoreCase("milestone_occurrence_type")) {
							List<FilterItem> mileStoneDateRangeList = mFilters.getMileStoneDateRange();
							deleteFilters(id, mileStoneDateRangeList);
						} else if (type.equalsIgnoreCase("org_industries")) {
							List<Industry> industryList = mFilters.getIndustries();
							deleteIndustryFilters(id, industryList);
						} else if (type.equalsIgnoreCase("location_code")) {//TODO
							List<Location> locationList = mFilters.getHeadquarters();
							deleteLocationFilters(id, locationList);
						} else if (type.equalsIgnoreCase("org_employee_size")) {
							List<FilterItem> employeeSizeList = mFilters.getEmployeeSizeFromBuz();
							deleteFilters(id, employeeSizeList);
						} else if (type.equalsIgnoreCase("search_date_range")) {
							List<FilterItem> ranks = mFilters.getDateRanges();
							deleteFilters(id, ranks);
						} else if (type.equalsIgnoreCase("milestone_type")) {
							List<FilterItem> mileStoneList = mFilters.getMileStones();
							deleteFilters(id, mileStoneList);
						} else if (type.equalsIgnoreCase("org_ownership")) {
							List<FilterItem> ownershipList = mFilters.getOwnerships();
							deleteFilters(id, ownershipList);
						} else if (type.equalsIgnoreCase("org_revenue_size")) {
							List<FilterItem> revenueSizeList = mFilters.getSalesVolumeFromBuz();
							deleteFilters(id, revenueSizeList);
						}
						//TODO
						else if (type.equalsIgnoreCase("dop_title")) {
							List<JobTitle> titleList = mFilters.getJobTitles();
							deleteJobTitleFilters(id, titleList);
						} else if (type.equalsIgnoreCase("dop_job_level")) {
							List<FilterItem> levelList = mFilters.getJobLevel();
							deleteFilters(id, levelList);
						} else if (type.equalsIgnoreCase("dop_address")) {
							List<Location> locationList = mFilters.getLocations();
							deleteLocationFilters(id, locationList);
						} else if (type.equalsIgnoreCase("dop_functional_role")) {
							List<FilterItem> functionalRoleList = mFilters.getFunctionalRoles();
							deleteFilters(id, functionalRoleList);
						}
						
						Intent intent = new Intent();
						intent.setAction(Constant.BROADCAST_REFRESH_FILTER);
						mContext.sendBroadcast(intent);
					}

				});
				button.setText(queryInfoItem.getName());
				if (type.equalsIgnoreCase(employer)) {
					employerInfoLayout.addView(view);
				} else if (type.equalsIgnoreCase(personal)) {
					pesonalInfoLayout.addView(view);
				}
			}
		}
	}
	
	private void setPersonalInfoLayout() {//TODO
		//Job Title
		String jobTitleStr = queryInfo.getJobTitle();
		if (null != jobTitleStr && !jobTitleStr.isEmpty()) {
			final View view = LayoutInflater.from(mContext).inflate(R.layout.sort_button, null);
			Button button = (Button) view.findViewById(R.id.button);
			button.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					
				}
				
			});
			button.setText(jobTitleStr);
			pesonalInfoLayout.addView(view);
		}	
		
		//Job Level
		List<QueryInfoItem> jobLevelList = queryInfo.getJobLevels();
		setInfoDetailButton(jobLevelList, personal);
		
		//Location
		String locationStr = queryInfo.getLocation();
		if (null != locationStr && !locationStr.isEmpty()) {
			final View view = LayoutInflater.from(mContext).inflate(R.layout.sort_button, null);
			Button button = (Button) view.findViewById(R.id.button);
			button.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					
				}
				
			});
			button.setText(locationStr);
			pesonalInfoLayout.addView(view);
		}
		
		//Functional Role
		List<QueryInfoItem> functionalRoleList = queryInfo.getFunctionalRoles();
		setInfoDetailButton(functionalRoleList, personal);
	}
	
	
	private void setEmployerInfoLayout() {
		//NewsTriggers
		List<QueryInfoItem> newsTriggersList = queryInfo.getNewsTriggers();
		setInfoDetailButton(newsTriggersList, employer);
		
		//Companies
		//TODO
		List<QueryInfoItem> companiesList = queryInfo.getCompaniesForPeople();
		setInfoDetailButton(companiesList, employer);
		
		//Ranks
		List<QueryInfoItem> ranksList = queryInfo.getRanks();
		setInfoDetailButton(ranksList, employer);
		
		//FiscalMonth
		List<QueryInfoItem> fiscalMonthList = queryInfo.getFiscalMonth();
		setInfoDetailButton(fiscalMonthList, employer);
		
		//MileStoneOccurrenceType
		List<QueryInfoItem> mileStoneOccurrenceTypeList = queryInfo.getMileStoneOccurrenceType();
		setInfoDetailButton(mileStoneOccurrenceTypeList, employer);
		
		//Industries
		List<QueryInfoItem> industriesList = queryInfo.getIndustries();
		setInfoDetailButton(industriesList, employer);
		
		//LocationCode
		List<QueryInfoItem> locationList = queryInfo.getLocationCode();
		setInfoDetailButton(locationList, employer);
		
		//EmployeeSize
		List<QueryInfoItem> employeeSizeList = queryInfo.getEmployeeSize();
		setInfoDetailButton(employeeSizeList, employer);
		
		//DateRange
		List<QueryInfoItem> dateRangeList = queryInfo.getDateRange();
		setInfoDetailButton(dateRangeList, employer);
		
		//MileStoneType
		List<QueryInfoItem> mileStoneTypeList = queryInfo.getMileStoneType();
		setInfoDetailButton(mileStoneTypeList, employer);
		
		//Ownership
		List<QueryInfoItem> ownershipList = queryInfo.getOwnership();
		setInfoDetailButton(ownershipList, employer);
		
		//RevenueSize
		List<QueryInfoItem> revenueSizeList = queryInfo.getRevenueSize();
		setInfoDetailButton(revenueSizeList, employer);
		
		//EventSearchKeywords
		if (null != queryInfo.getEventSearchKeywords()) {
			String eventSearchKeywords = queryInfo.getEventSearchKeywords().getName();
			if (!TextUtils.isEmpty(eventSearchKeywords)) {
				String type = queryInfo.getEventSearchKeywords().getType();
				setEventSearchKeywordsButton(eventSearchKeywords, type);
			}
		}
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
		Button button = (Button) view.findViewById(R.id.button);
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				employerInfoLayout.removeView(view);
				//TODO 数据删除
				Log.v("silen", "type = " + type);
				Constant.DEFINEWORDS = false;
				Constant.ALLWORDS = "";
				Constant.EXACTWORDS = "";
				Constant.ANYWORDS = "";
				Constant.NONEWORDS = "";
				
				PAGENUM = 1;
				searchAdvancedPersons(false);
			}
		});
		button.setText(value);
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
