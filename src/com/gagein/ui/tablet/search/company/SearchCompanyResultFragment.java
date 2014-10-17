package com.gagein.ui.tablet.search.company;

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
import com.gagein.adapter.SearchCompanyAdapter;
import com.gagein.component.AutoNewLineLayout;
import com.gagein.component.dialog.SaveSearchDialog;
import com.gagein.component.dialog.SortBySearchDialog;
import com.gagein.component.xlistview.XListView;
import com.gagein.component.xlistview.XListView.IXListViewListener;
import com.gagein.http.APIHttp;
import com.gagein.http.APIParser;
import com.gagein.model.Company;
import com.gagein.model.DataPage;
import com.gagein.model.filter.FilterItem;
import com.gagein.model.filter.Filters;
import com.gagein.model.filter.Industry;
import com.gagein.model.filter.Location;
import com.gagein.model.filter.QueryInfo;
import com.gagein.model.filter.QueryInfoItem;
import com.gagein.ui.BaseFragment;
import com.gagein.ui.tablet.company.CompanyTabletActivity;
import com.gagein.util.CommonUtil;
import com.gagein.util.Constant;
import com.gagein.util.Log;

public class SearchCompanyResultFragment extends BaseFragment implements OnItemClickListener, IXListViewListener{
	
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
	private LinearLayout wholeLayout;
	private OnHideLeftLayout onHideLeftLayout;
	private OnUpdateFilterStatusForCompany onUpdateFilterStatusForCompany;
	
	public interface OnHideLeftLayout {
		public void onHideLeftLayout();
	}
	
	public interface OnUpdateFilterStatusForCompany {
		public void onUpdateFilterStatusForCompany();
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		try {
			onHideLeftLayout = (OnHideLeftLayout) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + "must implement onUpdateFilterStatusForCompany");
		}
		
		try {
			onUpdateFilterStatusForCompany = (OnUpdateFilterStatusForCompany) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + "must implement OnUpdateFilterStatus");
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.activity_company_found, container, false);
		mContext = getActivity();
		mApiHttp = new APIHttp(mContext);
		
		doInit();
		return view;
	}
	
	@Override
	protected void initView() {
		super.initView();
		setLeftButton(R.string.filters);
		setRightButton(R.string.u_save);
		setTitle(R.string.results);
		
		showDetailsTx = (TextView) view.findViewById(R.id.showDetails);
		sortByText = (TextView) view.findViewById(R.id.sortBy);
		rankText = (TextView) view.findViewById(R.id.rank);
		sortByDetailLayout = (LinearLayout) view.findViewById(R.id.sortByDetailLayout);
		companyInfoLayout = (AutoNewLineLayout) view.findViewById(R.id.companyInfoLayout);
		listView = (XListView) view.findViewById(R.id.listView);
		emptyLayout = (RelativeLayout) view.findViewById(R.id.emptyLayout);
		wholeLayout = (LinearLayout) view.findViewById(R.id.wholeLayout);
		
		CommonUtil.setLayoutWith(wholeLayout, getActivity());
	}
	
	@Override
	protected void initData() {
		super.initData();
		mFilters = Constant.MFILTERS;
		
		setSortBy();
	}
	
	public void setFollowOrUnFollow(Intent intent, Boolean isFollow) {
		long companyId = intent.getLongExtra(Constant.COMPANYID, 0);
		
		for (int i = 0; i < seachedCompanies.size() ; i++) {
			Company company = seachedCompanies.get(i);
			if (company.orgID == companyId) {
				company.followed = isFollow;
			}
		}
		adapter.notifyDataSetChanged();
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
		searchAdvancedCompanies(false);
	}
	
	private void setTitle(long num) {
		if (num == 0) {
			setTitle("No companies found");
		} else if (num == 1) {
			setTitle("1 company found");
		} else {
			setTitle(CommonUtil.splitNumberByComma(num) + " companies found");
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
			rankText.setText(!Constant.REVERSE ?  " : High-Low" : " : Low-High");
		} else if (value.equalsIgnoreCase("Name")) {
			rankText.setText(!Constant.REVERSE ? " : Z-A" : " : A-Z");
		} else if (value.equalsIgnoreCase("Search Relevance")) {
			Constant.REVERSE = false;
			rankText.setText("");
		}
	}
	
	private void searchAdvancedCompanies(final Boolean loadMore) {
		
		if (!loadMore) showLoadingDialog(mContext);
		mApiHttp.searchAdvancedCompanies(PAGENUM, CommonUtil.packageRequestDataForCompanyOrPeople(true, true).get(0), new Listener<JSONObject>() {

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
		
		if (!loadMore) seachedCompanies.clear();
		
		//根据mssid来判断是否saved
		setSavedStatus(parser.data());
		
		//将API返回数据同步到本地的filter options
		queryInfo = parser.parserQueryInfo(true);
		
		//set query info layout
		companyInfoLayout.removeAllViews();
		
		setQueryInfoLayout();
		
		onUpdateFilterStatusForCompany.onUpdateFilterStatusForCompany();
		
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
		CommonUtil.setViewHeight(listView, listView.getAdapter().getCount() * CommonUtil.dp2px(mContext, 71));
		PAGENUM ++;
		
		//set title
		long total = parser.data().optLong("total");
		setTitle(total);
		
		if (seachedCompanies.size() == 0) {
			emptyLayout.setVisibility(View.VISIBLE);
			listView.setVisibility(View.GONE);
		} else {
			emptyLayout.setVisibility(View.GONE);
			listView.setVisibility(View.VISIBLE);
		}
		
	}
	
	private void setCompany() {
		
		adapter = new SearchCompanyAdapter(mContext, seachedCompanies);
		listView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		adapter.notifyDataSetInvalidated();
		
	}
	
	private void setQueryInfoDetailButton(List<QueryInfoItem> queryInfoItemList, List<QueryInfoItem> queryInfoItemList2) {
		
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
						List<QueryInfoItem> conditions = queryInfo.allConditions(true);
						if (conditions.size() <= 1) {
							showShortToast("You have to enter in search criteria! Try again.");
							return;
						} else if (conditions.size() == 2) {
							QueryInfoItem condition1 = conditions.get(0);
							QueryInfoItem condition2 = conditions.get(1);
							if (condition1.getType().equalsIgnoreCase("search_date_range")
									|| condition2.getType().equalsIgnoreCase("search_date_range")) {
								showShortToast("You have to enter in search criteria! Try again.");
								return;
							}
						}
						
						companyInfoLayout.removeView(view);
						
						String id = queryInfoItem.getId();
						Filters mFilters = Constant.MFILTERS;
						
						Log.v("silen", "delete-queryType = " + queryType);
						
						if (queryType.equalsIgnoreCase("mer_for_id")) {
							
							List<FilterItem> newsTriggerList = mFilters.getNewsTriggers();
							deleteFilters(id, newsTriggerList);
							
						} else if (queryType.equalsIgnoreCase("search_date_range")) {
							
							List<FilterItem> ranks = mFilters.getDateRanges();
							deleteFilters(id, ranks);
							
						} else if (queryType.equalsIgnoreCase("search_company_for_type")) {
							
//							if (id.trim().equalsIgnoreCase("4")) return;//TODO
							List<FilterItem> companiesList = mFilters.getCompanyTypesFromCompany();
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
							deleteIndustryFilters(id, industryList);
							
						} else if (queryType.equalsIgnoreCase("location_code")) {
							
							List<Location> locationList = mFilters.getHeadquarters();
							deleteLocationFilters(id, locationList);
							
						} else if (queryType.equalsIgnoreCase("org_employee_size")) {
							
							List<FilterItem> employeeSizeList = mFilters.getEmployeeSizeFromBuz();
							deleteFilters(id, employeeSizeList);
							
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
					}

				});
				
				if (null != queryInfoItemList2) {
					queryInfoItem.setDisplayName(queryInfoItemList2);
				}
				
				textView.setText(queryInfoItem.getDisplayName());
				
				CommonUtil.setFilterMaxWith(textView);
				
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
				PAGENUM = 1;
				searchAdvancedCompanies(false);
			}
		}
		
	}
	
	private void deleteIndustryFilters(String id, List<Industry> industries) {
		
		for (int i = 0; i < industries.size(); i ++) {
			if (id.equalsIgnoreCase(industries.get(i).getId())) {
				industries.get(i).setChecked(false);
				PAGENUM = 1;
				searchAdvancedCompanies(false);
			}
		}
		
	}
	
	private void deleteLocationFilters(String id, List<Location> locations) {
		
		for (int i = 0; i < locations.size(); i ++) {
			if (id.equalsIgnoreCase(locations.get(i).getCode())) {
				locations.get(i).setChecked(false);
				PAGENUM = 1;
				searchAdvancedCompanies(false);
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
				
				int filterButtonCount = companyInfoLayout.getChildCount();
				if (filterButtonCount == 1) {
					showShortToast("You have to enter in search criteria! Try again.");
					return;
				}
				
				companyInfoLayout.removeView(view);
				//TODO 数据删除
				Log.v("silen", "type = " + type);
				Constant.ALLWORDS_FOR_TRIGGERS = "";
//				Constant.EXACTWORDS = "";
//				Constant.ANYWORDS = "";
//				Constant.NONEWORDS = "";
				
				PAGENUM = 1;
				searchAdvancedCompanies(false);
			}
		});
		
		textView.setText(value);
		
		CommonUtil.setFilterMaxWith(textView);
		
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
		listView.setPullRefreshEnable(false);
		
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		
		if (v == leftBtn) {
			
			onHideLeftLayout.onHideLeftLayout();
			
		} else if (v == rightBtn) {
			
			if (null == queryInfo) return;
			if (rightBtn.getText().toString().equalsIgnoreCase("Saved")) return;
			
 			SaveSearchDialog dialog = new SaveSearchDialog(mContext, type, CommonUtil.packageRequestDataForCompanyOrPeople(true, false).get(0), queryInfo.getQueryInfoResult());
			dialog.showDialog(Constant.SEARCH_COMPANY);
			
		} else if (v == showDetailsTx) {
			
			showDetails = !showDetails;
			sortByDetailLayout.setVisibility(showDetails ? View.VISIBLE : View.GONE);
			showDetailsTx.setText(showDetails ? R.string.hide_details : R.string.show_details);
			
		} else if (v == sortByText) {
			
			SortBySearchDialog dialog = new SortBySearchDialog(mContext, this, true);
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

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
		
		if (Constant.ZERO == position || position == seachedCompanies.size() + 1) return;
		long companyId = seachedCompanies.get(position - 1).orgID;
		Intent intent = new Intent();
		intent.putExtra(Constant.COMPANYID, companyId);
		intent.setClass(mContext, CompanyTabletActivity.class);
		startActivity(intent);
		
	}
	
	private void setQueryInfoLayout() {
		
		//NewsTriggers
		List<QueryInfoItem> newsTriggersList = queryInfo.getNewsTriggers();
		setQueryInfoDetailButton(newsTriggersList, null);
		
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
		setQueryInfoDetailButton(dateRangeList, null);
		
		//Companies
		String companySearchKeywords = queryInfo.getCompanySearchKeywords();
		if (!companySearchKeywords.isEmpty()) {
			
			final View view = LayoutInflater.from(mContext).inflate(R.layout.sort_button, null);
			LinearLayout buttonLayout = (LinearLayout) view.findViewById(R.id.buttonLayout);
			TextView textView = (TextView) view.findViewById(R.id.text);
			
			buttonLayout.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					
					companyInfoLayout.removeView(view);
					Constant.COMPANY_SEARCH_KEYWORDS = "";
					
					List<FilterItem> companyTypes = mFilters.getCompanyTypesFromCompany();
					for (int i = 0; i < companyTypes.size(); i ++) {
						companyTypes.get(i).setChecked(i == 2 ? true : false);
					}
					
					PAGENUM = 1;
					searchAdvancedCompanies(false);
					
				}
				
			});
			
			textView.setText(companySearchKeywords);
			
			CommonUtil.setFilterMaxWith(textView);
			
			companyInfoLayout.addView(view);
		}
		
		
		List<QueryInfoItem> companiesList = queryInfo.getCompaniesForCompany();
		setQueryInfoDetailButton(companiesList, null);
		
		//LocationCode
		List<QueryInfoItem> locationList = queryInfo.getLocationCode();
		setQueryInfoDetailButton(locationList, null);
		
		//Industries
		List<QueryInfoItem> industriesList = queryInfo.getIndustries();
		setQueryInfoDetailButton(industriesList, null);
		
		//EmployeeSize
		List<QueryInfoItem> employeeSizeList = queryInfo.getEmployeeSize();
		setQueryInfoDetailButton(employeeSizeList, null);
		
		//RevenueSize
		List<QueryInfoItem> revenueSizeList = queryInfo.getRevenueSize();
		setQueryInfoDetailButton(revenueSizeList, null);
		
		//Ownership
		List<QueryInfoItem> ownershipList = queryInfo.getOwnership();
		setQueryInfoDetailButton(ownershipList, null);
		
		//MileStoneOccurrenceType
		List<QueryInfoItem> mileStoneOccurrenceTypeList = queryInfo.getMileStoneOccurrenceType();
		
		//MileStoneType
		int mileStoneOccurrenceTypeListSize = mileStoneOccurrenceTypeList == null ? 0 : mileStoneOccurrenceTypeList.size();
		
		if (mileStoneOccurrenceTypeListSize > 0) {
			
			List<QueryInfoItem> mileStoneTypeList = queryInfo.getMileStoneType();
			setQueryInfoDetailButton(mileStoneTypeList, mileStoneOccurrenceTypeList);
		}
		
		//Ranks
		List<QueryInfoItem> ranksList = queryInfo.getRanks();
		setQueryInfoDetailButton(ranksList, null);
		
		//FiscalMonth
		List<QueryInfoItem> fiscalMonthList = queryInfo.getFiscalMonth();
		setQueryInfoDetailButton(fiscalMonthList, null);
		
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
