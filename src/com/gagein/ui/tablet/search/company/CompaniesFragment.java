package com.gagein.ui.tablet.search.company;

import java.util.ArrayList;
import java.util.List;

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

import com.gagein.R;
import com.gagein.adapter.search.FilterAdapter;
import com.gagein.http.APIHttp;
import com.gagein.model.filter.FilterItem;
import com.gagein.model.filter.Filters;
import com.gagein.ui.BaseFragment;
import com.gagein.util.CommonUtil;
import com.gagein.util.Constant;
import com.gagein.util.Log;

public class CompaniesFragment extends BaseFragment implements OnItemClickListener{
	
	private ListView listView;
	private EditText edit;
	private FilterAdapter adapter;
	private Filters mFilters;
	private List<FilterItem> companyTypes = new ArrayList<FilterItem>();
	private LinearLayout savedSearchLayout;
	private OnCompaniesFinish onCompaniesFinish;
	private OnSearchFromCompanies onSearchFromCompanies;
	private OnCanleTask onCanleTask;
	
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
		view = inflater.inflate(R.layout.activity_search_company_filter_company, container, false);
		mContext = getActivity();
		mApiHttp = new APIHttp(mContext);
		
		doInit();
		return view;
	}
	
	public void refreshAdapter() {
		if (null != adapter) adapter.notifyDataSetChanged();
	}
	
	@Override
	protected void initView() {
		super.initView();
		setTitle(R.string.u_companies);
		setLeftImageButton(R.drawable.back_arrow);
		
		listView = (ListView) view.findViewById(R.id.listView);
		edit = (EditText) view.findViewById(R.id.edit);
		
		edit.setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
				
				if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {// COMPANY_SEARCH_KEYWORDS
					
					String text = textView.getText().toString();
					CommonUtil.hideSoftKeyBoard(mContext, getActivity());
					
					if (TextUtils.isEmpty(text)) {
						
						onSearchFromCompanies.onSearchFromCompanies();
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
		savedSearchLayout = (LinearLayout) view.findViewById(R.id.savedSearchLayout);
	}
	
	@Override
	protected void initData() {
		super.initData();
		
		setInitialData();
		
	}
	
	public void setInitialData() {
		
		mFilters = Constant.MFILTERS;
		companyTypes = mFilters.getCompanyTypesFromCompany();
		
		adapter = new FilterAdapter(mContext, companyTypes);
		listView.setAdapter(adapter);
		CommonUtil.setListViewHeight(listView);
		adapter.notifyDataSetChanged();
		adapter.notifyDataSetInvalidated();
		
		for (int i = 0; i < companyTypes.size(); i ++) {
			
			if (companyTypes.get(i).getValue().equalsIgnoreCase("Specific Companies")) {
				if (companyTypes.get(i).getChecked()) {
					savedSearchLayout.setVisibility(View.VISIBLE);
					edit.setText(Constant.COMPANY_SEARCH_KEYWORDS);
				} else {
					savedSearchLayout.setVisibility(View.GONE);
					edit.setText("");
				}
			}
		}
		
	}
	
	@Override
	protected void setOnClickListener() {
		super.setOnClickListener();
		listView.setOnItemClickListener(this);
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
		
		// when clicked saved company search...
		Boolean checked = companyTypes.get(position).getChecked();
		
		if (checked) {
			return;
		} else {// this item is not checked
			
			if (companyTypes.get(position).getValue().equalsIgnoreCase("Specific Companies")) {
				
				savedSearchLayout.setVisibility(View.VISIBLE);
				onCanleTask.onCanleTask();
				
			} else {
				
				savedSearchLayout.setVisibility(View.GONE);
				edit.setText("");
				edit.clearFocus();
				CommonUtil.hideSoftKeyBoard(mContext, getActivity());
				
			}
			
			if (position == 2) {
				
				List<String> requestDataList = CommonUtil.packageRequestDataForCompanyOrPeople(true, true);
				String requestStr = requestDataList.get(0);
				String haveSelectCondition = requestDataList.get(1);
				Log.v("silen", "requestStr = " + requestStr);
				Log.v("silen", "haveSelectCondition = " + requestDataList.get(1));
				
				if (haveSelectCondition.equalsIgnoreCase("false")) {
					
					showShortToast("You have to enter in search criteria! Try again.");
					return;
					
				} else {
					
					onSearchFromCompanies.onSearchFromCompanies();
					
				}
				
			}
			
			for (int i = 0; i < companyTypes.size(); i ++) {
				companyTypes.get(i).setChecked(false);
			}
			companyTypes.get(position).setChecked(true);//set checked item
			
		}
		adapter.notifyDataSetChanged();
		
		if (position == 0) {
			
			onSearchFromCompanies.onSearchFromCompanies();
			
		}
	}

}
