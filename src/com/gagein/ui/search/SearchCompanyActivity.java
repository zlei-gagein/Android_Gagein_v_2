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
import com.gagein.adapter.SearchCompanyAdapter;
import com.gagein.component.AutoNewLineLayout;
import com.gagein.component.dialog.SaveSearchDialog;
import com.gagein.component.xlistview.XListView;
import com.gagein.component.xlistview.XListView.IXListViewListener;
import com.gagein.http.APIParser;
import com.gagein.model.Company;
import com.gagein.model.DataPage;
import com.gagein.model.filter.FilterItem;
import com.gagein.model.filter.Filters;
import com.gagein.model.filter.Industry;
import com.gagein.model.filter.Location;
import com.gagein.model.filter.QueryInfo;
import com.gagein.model.filter.QueryInfoItem;
import com.gagein.ui.company.CompanyActivity;
import com.gagein.ui.main.BaseActivity;
import com.gagein.ui.search.company.filter.SortBySearchCompanyActivity;
import com.gagein.util.CommonUtil;
import com.gagein.util.Constant;
import com.gagein.util.Log;

public class SearchCompanyActivity extends BaseActivity implements OnItemClickListener, IXListViewListener{
	
	private TextView showDetailsTx;
	private TextView sortByText;
	private TextView rankText;
	private LinearLayout sortByDetailLayout;
	private AutoNewLineLayout companyInfoLayout;
	private XListView listView;
	private Boolean showDetails = false;
	private QueryInfo queryInfo;
	private List<Company> seachedCompanies = new ArrayList<Company>();
	private SearchCompanyAdapter adapter;
	private int PAGENUM = 1;
	private RelativeLayout emptyLayout;
	private Filters mFilters;
	private List<FilterItem> sortByList;
	private String type = "buz";
	private int requestCode = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_company_found);
		
		doInit();
	}
	
	@Override
	protected void initView() {
		super.initView();
		setLeftImageButton(R.drawable.back_arrow);
		setRightButton(R.string.u_save);
		setTitle(R.string.results);
		
		showDetailsTx = (TextView) findViewById(R.id.showDetails);
		sortByText = (TextView) findViewById(R.id.sortBy);
		rankText = (TextView) findViewById(R.id.rank);
		sortByDetailLayout = (LinearLayout) findViewById(R.id.sortByDetailLayout);
		companyInfoLayout = (AutoNewLineLayout) findViewById(R.id.companyInfoLayout);
		listView = (XListView) findViewById(R.id.listView);
		emptyLayout = (RelativeLayout) findViewById(R.id.emptyLayout);
	}
	
	@Override
	protected void initData() {
		super.initData();
		mFilters = Constant.MFILTERS;
		
		setSortBy();
		
		searchAdvancedCompanies(false);
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
		searchAdvancedCompanies(false);
	}
	
	private void setTitle(long num) {
		if (num == 0) {
			setTitle("No company found");
		} else if (num == 1) {
			setTitle("1 company found");
		} else {
			setTitle(num + " companies found");
		}
	}
	
	public void setSortBy() {
		sortByList = mFilters.getSortByFromBuz();
		for (int i = 0; i < sortByList.size(); i ++) {
			if (sortByList.get(i).getChecked()) {
				String value = sortByList.get(i).getValue();
				sortByText.setText(value);
				setRank(value);
			}
		}
	}

	private void setRank(String value) {
		if (value.equalsIgnoreCase("Employee Size") || value.equalsIgnoreCase("Revenue Size")) {
			rankText.setText(Constant.REVERSE ? " : Low-High" : " : High-Low");
		} else if (value.equalsIgnoreCase("Name")) {
			rankText.setText(Constant.REVERSE ? " : Z-A" : " : A-Z");
		} else if (value.equalsIgnoreCase("Search Relevance")) {
			Constant.REVERSE = false;
			rankText.setText("");
		}
	}
	
	private void searchAdvancedCompanies(final Boolean loadMore) {
		if (!loadMore) showLoadingDialog();
		mApiHttp.searchAdvancedCompanies(PAGENUM, CommonUtil.packageRequestDataForCompanyOrPeople(true), new Listener<JSONObject>() {

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
				showConnectionError();
			}
		});
	}
	
	private void parserIsOk(final Boolean loadMore, APIParser parser) {
		if (!loadMore) seachedCompanies.clear();
		
		//将API返回数据同步到本地的filter options
		queryInfo = parser.parserQueryInfo(true);
		
		//set query info layout
		companyInfoLayout.removeAllViews();
		setQueryInfoLayout();
		
		DataPage dataPage = parser.parseGetSimilarCompanies();
		if (dataPage == null) {
			Log.v("silen", "dataPage == null");
		}
		if (dataPage.items != null) {
			for (Object obj : dataPage.items) {
				if (obj instanceof Company) {
					seachedCompanies.add((Company)obj);
				}
			}
		}
		Boolean haveMoreNews = dataPage.hasMore;
		listView.setPullLoadEnable(haveMoreNews);
		if (!loadMore) setCompany();
		PAGENUM ++;
		
		//set title
		long total = parser.data().optLong("total");
		setTitle(total);
		
		if (seachedCompanies.size() == 0) {
			emptyLayout.setVisibility(View.VISIBLE);
		} else {
			emptyLayout.setVisibility(View.GONE);
		}
	}
	
	private void setCompany() {
		adapter = new SearchCompanyAdapter(mContext, seachedCompanies);
		listView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		adapter.notifyDataSetInvalidated();
	}
	
	private void setQueryInfoDetailButton(List<QueryInfoItem> queryInfoItemList) {
		if (null != queryInfoItemList) {
			for (int i = 0; i < queryInfoItemList.size(); i ++) {
				final View view = LayoutInflater.from(mContext).inflate(R.layout.sort_button, null);
				Button button = (Button) view.findViewById(R.id.button);
				final QueryInfoItem queryInfoItem = queryInfoItemList.get(i);
				button.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						//判断是否有选中条件 TODO
						companyInfoLayout.removeView(view);
						//TODO 数据删除
						String type = queryInfoItem.getType();
						String id = queryInfoItem.getId();
						Filters mFilters = Constant.MFILTERS;
						Log.v("silen", "type = " + type);
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
						} else if (type.equalsIgnoreCase("location_code")) {
							List<Location> locationList = mFilters.getLocations();
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
						
						Intent intent = new Intent();
						intent.setAction(Constant.BROADCAST_REFRESH_FILTER);
						mContext.sendBroadcast(intent);
					}

				});
				button.setText(queryInfoItem.getName());
				companyInfoLayout.addView(view);
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
				searchAdvancedCompanies(false);
			}
		}
	}
	
	private void deleteIndustryFilters(String id, List<Industry> industries) {
		for (int i = 0; i < industries.size(); i ++) {
			if (id.equalsIgnoreCase(industries.get(i).getId())) {
				industries.get(i).setChecked(false);
				searchAdvancedCompanies(false);
			}
		}
	}
	
	private void deleteLocationFilters(String id, List<Location> locations) {
		for (int i = 0; i < locations.size(); i ++) {
			if (id.equalsIgnoreCase(locations.get(i).getCode())) {
				locations.get(i).setChecked(false);
				searchAdvancedCompanies(false);
			}
		}
	}
	
	private void setEventSearchKeywordsButton(String value, final String type) {
		final View view = LayoutInflater.from(mContext).inflate(R.layout.sort_button, null);
		Button button = (Button) view.findViewById(R.id.button);
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				companyInfoLayout.removeView(view);
				//TODO 数据删除
				Log.v("silen", "type = " + type);
				Constant.DEFINEWORDS = false;
				Constant.ALLWORDS = "";
				Constant.EXACTWORDS = "";
				Constant.ANYWORDS = "";
				Constant.NONEWORDS = "";
				
				searchAdvancedCompanies(false);
			}
		});
		button.setText(value);
		companyInfoLayout.addView(view);
	}

	@Override
	protected void setOnClickListener() {
		super.setOnClickListener();
		showDetailsTx.setOnClickListener(this);
		sortByText.setOnClickListener(this);
		rankText.setOnClickListener(this);
		listView.setOnItemClickListener(this);
		listView.setXListViewListener(this);
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		if (v == leftImageBtn) {
			finish();
		} else if (v == rightBtn) {
			if (null == queryInfo) return;
 			SaveSearchDialog dialog = new SaveSearchDialog(mContext, type, CommonUtil.packageRequestDataForCompanyOrPeople(true), queryInfo.getQueryInfoResult());
 			Window dialogWindow = dialog.getDialog().getWindow();
 	        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
 	        lp.width = CommonUtil.getDeviceWidth(SearchCompanyActivity.this) - 100;
			dialog.showDialog();
		} else if (v == showDetailsTx) {
			showDetails = !showDetails;
			sortByDetailLayout.setVisibility(showDetails ? View.VISIBLE : View.GONE);
			showDetailsTx.setText(showDetails ? R.string.hide_details : R.string.show_details);
		} else if (v == sortByText) {
			Intent intent = new Intent();
			intent.setClass(mContext, SortBySearchCompanyActivity.class);
			startActivityForResult(intent, requestCode);
		} else if (v == rankText) {
			String value = rankText.getText().toString();
			if (!value.isEmpty()) {
				Log.v("silen", "Constant.REVERSE = " + Constant.REVERSE);
				Constant.REVERSE = !Constant.REVERSE;
				Log.v("silen", "Constant.REVERSE = " + Constant.REVERSE);
				setRank(sortByText.getText().toString());
			} else {
				return;
			}
			initSearch();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		//TODO
		if (requestCode == this.requestCode && resultCode == RESULT_OK) {
			setSortBy();
			PAGENUM = 1;
			searchAdvancedCompanies(false);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
		if (Constant.ZERO == position || position == seachedCompanies.size() + 1) return;
		long companyId = seachedCompanies.get(position - 1).orgID;
		Intent intent = new Intent();
		intent.putExtra(Constant.COMPANYID, companyId);
		intent.setClass(mContext, CompanyActivity.class);
		startActivity(intent);
	}
	
	private void setQueryInfoLayout() {
		//NewsTriggers
		List<QueryInfoItem> newsTriggersList = queryInfo.getNewsTriggers();
		setQueryInfoDetailButton(newsTriggersList);
		
		//Companies
		String companySearchKeywords = queryInfo.getCompanySearchKeywords();
		if (!companySearchKeywords.isEmpty()) {
			final View view = LayoutInflater.from(mContext).inflate(R.layout.sort_button, null);
			Button button = (Button) view.findViewById(R.id.button);
			button.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					
				}
				
			});
			button.setText(companySearchKeywords);
			companyInfoLayout.addView(view);
		}
		
		List<QueryInfoItem> companiesList = queryInfo.getCompaniesForCompany();
		setQueryInfoDetailButton(companiesList);
		
		
		//Ranks
		List<QueryInfoItem> ranksList = queryInfo.getRanks();
		setQueryInfoDetailButton(ranksList);
		
		//FiscalMonth
		List<QueryInfoItem> fiscalMonthList = queryInfo.getFiscalMonth();
		setQueryInfoDetailButton(fiscalMonthList);
		
		//MileStoneOccurrenceType
		List<QueryInfoItem> mileStoneOccurrenceTypeList = queryInfo.getMileStoneOccurrenceType();
		setQueryInfoDetailButton(mileStoneOccurrenceTypeList);
		
		//Industries
		List<QueryInfoItem> industriesList = queryInfo.getIndustries();
		setQueryInfoDetailButton(industriesList);
		
		//LocationCode
		List<QueryInfoItem> locationList = queryInfo.getLocationCode();
		setQueryInfoDetailButton(locationList);
		
		//EmployeeSize
		List<QueryInfoItem> employeeSizeList = queryInfo.getEmployeeSize();
		setQueryInfoDetailButton(employeeSizeList);
		
		//DateRange
		List<QueryInfoItem> dateRangeList = queryInfo.getDateRange();
		setQueryInfoDetailButton(dateRangeList);
		
		//MileStoneType
		List<QueryInfoItem> mileStoneTypeList = queryInfo.getMileStoneType();
		setQueryInfoDetailButton(mileStoneTypeList);
		
		//Ownership
		List<QueryInfoItem> ownershipList = queryInfo.getOwnership();
		setQueryInfoDetailButton(ownershipList);
		
		//RevenueSize
		List<QueryInfoItem> revenueSizeList = queryInfo.getRevenueSize();
		setQueryInfoDetailButton(revenueSizeList);
		
		//EventSearchKeywords
		if (null != queryInfo.getEventSearchKeywords()) {
			String eventSearchKeywords = queryInfo.getEventSearchKeywords().getName();
			if (!TextUtils.isEmpty(eventSearchKeywords)) {
				String type = queryInfo.getEventSearchKeywords().getType();
				setEventSearchKeywordsButton(eventSearchKeywords, type);
			}
		}
	}

	@Override
	public void onRefresh() {
		PAGENUM = 1;
		searchAdvancedCompanies(false);
	}

	@Override
	public void onLoadMore() {
		searchAdvancedCompanies(true);
	}

}
