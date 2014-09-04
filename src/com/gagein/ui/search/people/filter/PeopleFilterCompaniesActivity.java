package com.gagein.ui.search.people.filter;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
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
import com.gagein.http.APIParser;
import com.gagein.model.DataPage;
import com.gagein.model.SavedSearch;
import com.gagein.model.filter.FilterItem;
import com.gagein.model.filter.Filters;
import com.gagein.ui.main.BaseActivity;
import com.gagein.util.CommonUtil;
import com.gagein.util.Constant;

public class PeopleFilterCompaniesActivity extends BaseActivity implements OnItemClickListener{
	
	private ListView listView;
	private ListView savedListView;
	private EditText edit;
	private FilterAdapter adapter;
	private Filters mFilters;
	private List<FilterItem> companyTypes;
	private int PAGENUM = 1;
	private List<SavedSearch> mSavedSearchs = new ArrayList<SavedSearch>();
	private PeopleFilterCompaniesAdapter companiesAdapter;
	private LinearLayout savedSearchLayout;
	private LinearLayout specificLayout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_people_filter_company);
		
		doInit();
	}
	
	@Override
	protected void initView() {
		super.initView();
		setTitle(R.string.u_companies);
		setLeftImageButton(R.drawable.back_arrow);
		
//		edit = (EditText) findViewById(R.id.edit);
		listView = (ListView) findViewById(R.id.listView);
		savedListView = (ListView) findViewById(R.id.savedListView);
		savedSearchLayout = (LinearLayout) findViewById(R.id.savedSearchLayout);
		specificLayout = (LinearLayout) findViewById(R.id.specificLayout);
		
//		edit.setOnEditorActionListener(new OnEditorActionListener() {//TODO
//			
//			@Override
//			public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
//				if (actionId == EditorInfo.IME_ACTION_DONE) {//TODO COMPANY_SEARCH_KEYWORDS
//					String text = textView.getText().toString();
//					CommonUtil.hideSoftKeyBoard(mContext, PeopleFilterCompaniesActivity.this);
//					if (TextUtils.isEmpty(text)) {
//						return false;
//					} else {
//						Constant.COMPANY_SEARCH_KEYWORDS = text;
//					}
//				}
//				return false;
//			}
//		});
	}
	
	@Override
	protected void initData() {
		super.initData();
		
		mFilters = Constant.MFILTERS;
		companyTypes = mFilters.getCompanyTypesFromPeople();
		
		adapter = new FilterAdapter(mContext, companyTypes);
		listView.setAdapter(adapter);
		CommonUtil.setListViewHeight(listView);
		adapter.notifyDataSetChanged();
		adapter.notifyDataSetInvalidated();
		
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
						
						for (int i = 0; i < mSavedSearchs.size(); i ++) {
							mSavedSearchs.get(i).setChecked((i == 0) ? true : false);
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
				showConnectionError();
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
			finish();
		}
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View arg1, int position, long arg3) {
		if (parent == listView) {
			//TODO when clicked saved company search...
			Boolean checked = companyTypes.get(position).getChecked();
			if (checked) {
				return;
			} else {// this item is not checked
				if (companyTypes.get(position).getValue().equalsIgnoreCase("Specific Companies")) {
					savedSearchLayout.setVisibility(View.VISIBLE);
					
					savedSearchLayout.setVisibility(View.GONE);
				} else if (companyTypes.get(position).getValue().equalsIgnoreCase("All Companies")) {//TODO
					savedSearchLayout.setVisibility(View.GONE);
					return;
				} else {
					if (companyTypes.get(position).getValue().equalsIgnoreCase("Saved Company Search")) {
						if (companyTypes.get(position).getChecked()) {
							savedSearchLayout.setVisibility(View.GONE);
						} else {
							if (mSavedSearchs.size() > 0) {
								savedSearchLayout.setVisibility(View.VISIBLE);
							} else {
								showShortToast(R.string.no_saved_company_searches);
								return;
							}
						}
					} else {
						savedSearchLayout.setVisibility(View.GONE);
					}
				}
			}
			
			adapter.notifyDataSetChanged();
		} else if (parent == savedListView) {//TODO
			Boolean checked = mSavedSearchs.get(position).getChecked();
			if (checked) {
				return;
			} else {// this item is not checked
				for (int i = 0; i < mSavedSearchs.size(); i ++) {
					mSavedSearchs.get(i).setChecked(false);
				}
				mSavedSearchs.get(position).setChecked(true);//set checked item
			}
			companiesAdapter.notifyDataSetChanged();
		}
		
	}

}