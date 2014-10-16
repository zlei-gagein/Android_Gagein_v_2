package com.gagein.ui.tablet.search.people;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.gagein.R;
import com.gagein.adapter.search.FilterAdapter;
import com.gagein.adapter.search.PeopleFilterCompaniesAdapter;
import com.gagein.http.APIHttp;
import com.gagein.http.APIParser;
import com.gagein.model.DataPage;
import com.gagein.model.SavedSearch;
import com.gagein.model.filter.FilterItem;
import com.gagein.model.filter.Filters;
import com.gagein.ui.BaseFragment;
import com.gagein.util.CommonUtil;
import com.gagein.util.Constant;
import com.gagein.util.Log;

public class CompaniesFragment extends BaseFragment implements OnItemClickListener{
	
	private ListView listView;
	private ListView savedListView;
	private EditText edit;
	private FilterAdapter adapter;
	private Filters mFilters;
	private List<FilterItem> companyTypes = new ArrayList<FilterItem>();
	private int PAGENUM = 1;
	private List<SavedSearch> mSavedSearchs = new ArrayList<SavedSearch>();
	private PeopleFilterCompaniesAdapter companiesAdapter;
	private LinearLayout savedSearchLayout;
	private LinearLayout specificLayout;
	private OnCompaniesFinish onCompaniesFinish;
	private OnSearchFromCompanies onSearchFromCompanies;
	private OnCanleTask onCanleTask;
	
	public void refreshAdapter() {
		if (null != adapter) adapter.notifyDataSetChanged();
 	}
	
	public interface OnCompaniesFinish {
		public void onCompaniesFinish();
	}
	public interface OnSearchFromCompanies {
		public void onSearchFromCompanies();
	}
	public interface OnCanleTask {
		public void onCanleTask();
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			onCompaniesFinish = (OnCompaniesFinish) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + "must implement OnCompaniesFinish");
		}
		try {
			onSearchFromCompanies = (OnSearchFromCompanies) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + "must implement OnSearchFromCompanies");
		}
		try {
			onCanleTask = (OnCanleTask) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + "must implement OnCanleTask");
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.activity_search_people_filter_company, container, false);
		mContext = getActivity();
		mApiHttp = new APIHttp(mContext);
		
		doInit();
		return view;
	}
	
	@Override
	protected void initView() {
		super.initView();
		setTitle(R.string.u_companies);
		setLeftImageButton(R.drawable.back_arrow);
		
		edit = (EditText) view.findViewById(R.id.edit);
		listView = (ListView) view.findViewById(R.id.listView);
		savedListView = (ListView) view.findViewById(R.id.savedListView);
		savedSearchLayout = (LinearLayout) view.findViewById(R.id.savedSearchLayout);
		specificLayout = (LinearLayout) view.findViewById(R.id.specificLayout);
		
		edit.setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
				
				if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {// COMPANY_SEARCH_KEYWORDS
					
					String text = textView.getText().toString();
					CommonUtil.hideSoftKeyBoard(mContext, getActivity());
					
					if (TextUtils.isEmpty(text)) {
						
						return false;
						
					} else {
						
						if (text.trim().length() < 2) {
							
							showShortToast("Keywords must be at least two characters long.");
							return false;
							
						} else {
							
							Constant.COMPANY_SEARCH_KEYWORDS = text;
							onSearchFromCompanies.onSearchFromCompanies();
							
						}
					}
					
				}
				return false;
			}
		});
	}
	
	@Override
	protected void initData() {
		super.initData();
		
		setInitialData();
		
	}
	
	public void setInitialData() {
		
		mFilters = Constant.MFILTERS;
		companyTypes = mFilters.getCompanyTypesFromPeople();
		
		adapter = new FilterAdapter(mContext, companyTypes);
		listView.setAdapter(adapter);
		CommonUtil.setListViewHeight(listView);
		adapter.notifyDataSetChanged();
		adapter.notifyDataSetInvalidated();
		
		for (int i = 0; i < companyTypes.size(); i ++) {
			if (companyTypes.get(i).getValue().equalsIgnoreCase("Specific Companies")) {
				if (companyTypes.get(i).getChecked()) {
					specificLayout.setVisibility(View.VISIBLE);
					edit.setText(Constant.COMPANY_SEARCH_KEYWORDS);
				} else {
					specificLayout.setVisibility(View.GONE);
					edit.setText("");
				}
			}
		}
		
//		if (mFilters.getSavedCompanies().size() == 0) {
//			
//		}
		
		getSavedCompany(false);
		
	}
	
	private void setSavedCompany() {
		companiesAdapter = new PeopleFilterCompaniesAdapter(mContext, mSavedSearchs);
		savedListView.setAdapter(companiesAdapter);
		CommonUtil.setListViewHeight(savedListView);
		companiesAdapter.notifyDataSetChanged();
		companiesAdapter.notifyDataSetInvalidated();
	}
	
	private void getSavedCompany(final Boolean loadMore) {
		mApiHttp.getSavedCompanySearchesWithPage(PAGENUM, new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject jsonObject) {
				APIParser parser = new APIParser(jsonObject);
				if (parser.isOK()) {
					if (!loadMore) mSavedSearchs.clear();
					DataPage dataPage = parser.parseGetSavedSearch();
					List<Object> items = dataPage.items;
					if (items != null) {
						for (Object obj : items) {
							mSavedSearchs.add((SavedSearch) obj);
						}
						
						List<FilterItem> savedCompanies = new ArrayList<FilterItem>();
						
						for (int i = 0; i < mSavedSearchs.size(); i ++) {
							
							SavedSearch savedSearch = mSavedSearchs.get(i);
							savedSearch.setChecked((i == 0) ? true : false);
							
							FilterItem filterItem = new FilterItem();
							filterItem.setChecked(savedSearch.getChecked());
							filterItem.setKey(savedSearch.getId());
							filterItem.setValue(savedSearch.getName());
							savedCompanies.add(filterItem);
							
							mFilters.setSavedCompanies(savedCompanies);
						}
						if (!loadMore) setSavedCompany();
						
					}
					
					PAGENUM ++;
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
	
	@Override
	protected void setOnClickListener() {
		super.setOnClickListener();
		listView.setOnItemClickListener(this);
		savedListView.setOnItemClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		if (v == leftImageBtn) {
			onCompaniesFinish.onCompaniesFinish();
		}
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View arg1, int position, long arg3) {
		
		CommonUtil.hideSoftKeyBoard(mContext, getActivity());
		
		if (parent == listView) {
			// when clicked saved company search...
			Boolean checked = companyTypes.get(position).getChecked();
			if (checked) {
				
				return;
				
			} else {// this item is not checked
				
				edit.setText("");
				Constant.COMPANY_SEARCH_KEYWORDS = "";
				
				if (companyTypes.get(position).getValue().equalsIgnoreCase("Specific Companies")) {
					
					savedSearchLayout.setVisibility(View.GONE);
					specificLayout.setVisibility(View.VISIBLE);
					setCompanyTypesSelected(position);
					
					return;
					
				} else {
					
					if (companyTypes.get(position).getValue().equalsIgnoreCase("Saved Company Search")) {
						
						if (mSavedSearchs.size() == 0) {
							showShortToast("You have no saved searches");
							getSavedCompany(false);
							return;
						}
						
						if (position == 4) {
							
							List<String> requestDataList = CommonUtil.packageRequestDataForCompanyOrPeople(true, true);
							String requestStr = requestDataList.get(0);
							String haveSelectCondition = requestDataList.get(1);
							Log.v("silen", "requestStr = " + requestStr);
							Log.v("silen", "haveSelectCondition = " + requestDataList.get(1));
							
							if (haveSelectCondition.equalsIgnoreCase("false")) {
								showShortToast("You have to enter in search criteria! Try again.");
								return;
							}
							
						}
						
						for (int i = 0; i < mSavedSearchs.size(); i ++) {
							mSavedSearchs.get(i).setChecked(false);
						}
						mSavedSearchs.get(0).setChecked(true);
						companiesAdapter.notifyDataSetChanged();
						
						if (mSavedSearchs.size() > 0) {
							savedSearchLayout.setVisibility(View.VISIBLE);
						} else {
							showShortToast(R.string.no_saved_company_searches);
							return;
						}
						
						setCompanyTypesSelected(position);
						
						savedSearchLayout.setVisibility(View.VISIBLE);
						specificLayout.setVisibility(View.GONE);
						
					} else {
						
						setCompanyTypesSelected(position);
						
						savedSearchLayout.setVisibility(View.GONE);
						specificLayout.setVisibility(View.GONE);
						
					}
				}
				
				if (position == 4) {
					
					List<String> requestDataList = CommonUtil.packageRequestDataForCompanyOrPeople(true, true);
					String requestStr = requestDataList.get(0);
					String haveSelectCondition = requestDataList.get(1);
					Log.v("silen", "requestStr = " + requestStr);
					Log.v("silen", "haveSelectCondition = " + requestDataList.get(1));
					
					if (haveSelectCondition.equalsIgnoreCase("false")) {
						showShortToast("You have to enter in search criteria! Try again.");
					} else {
						onSearchFromCompanies.onSearchFromCompanies();
					}
					
				} else {
					onSearchFromCompanies.onSearchFromCompanies();
				}
			}
			
		} else if (parent == savedListView) {
			
			Boolean checked = mSavedSearchs.get(position).getChecked();
			if (checked) {
				return;
			} else {// this item is not checked
				
				for (int i = 0; i < mSavedSearchs.size(); i ++) {
					mSavedSearchs.get(i).setChecked(false);
				}
				
				mSavedSearchs.get(position).setChecked(true);//set checked item
				
				List<FilterItem> savedCompanies = mFilters.getSavedCompanies();
				for (int i = 0; i < savedCompanies.size(); i ++) {
					savedCompanies.get(i).setChecked(false);
				}
				savedCompanies.get(position).setChecked(true);
				
			}
			
			companiesAdapter.notifyDataSetChanged();
			onSearchFromCompanies.onSearchFromCompanies();
			
		}
		
	}

	private void setCompanyTypesSelected(int position) {
		for (int i = 0; i < companyTypes.size(); i ++) {
			companyTypes.get(i).setChecked(false);
		}
		companyTypes.get(position).setChecked(true);
		adapter.notifyDataSetChanged();
	}

}
