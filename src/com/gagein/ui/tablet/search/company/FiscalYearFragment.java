package com.gagein.ui.tablet.search.company;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.gagein.R;
import com.gagein.adapter.search.FilterAdapter;
import com.gagein.http.APIHttp;
import com.gagein.model.filter.FilterItem;
import com.gagein.model.filter.Filters;
import com.gagein.ui.BaseFragment;
import com.gagein.util.Constant;

public class FiscalYearFragment extends BaseFragment implements OnItemClickListener{
	
	private ListView listView;
	private FilterAdapter adapter;
	private Filters mFilters;
	private List<FilterItem> mFiscalYearEndMonths;
	private OnFiscalYearFinish onFiscalYearFinish;
	private OnSearchFromFiscalYear onSearchFromFiscalYear;
	
	public void refreshAdapter() {
		if (null != adapter) adapter.notifyDataSetChanged();
	}
	
	public interface OnFiscalYearFinish {
		public void onFiscalYearFinish();
	}
	public interface OnSearchFromFiscalYear {
		public void onSearchFromFiscalYear();
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			onFiscalYearFinish = (OnFiscalYearFinish) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + "must implement OnFiscalYearFinish");
		}
		try {
			onSearchFromFiscalYear = (OnSearchFromFiscalYear) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + "must implement OnSearchFromFiscalYear");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.activity_search_company_filter_employee_size, container, false);
		mContext = getActivity();
		mApiHttp = new APIHttp(mContext);
		
		doInit();
		return view;
	}
	
	@Override
	protected void initView() {
		super.initView();
		setTitle(R.string.fiscal_year_end);
		setLeftImageButton(R.drawable.back_arrow);
		
		listView = (ListView) view.findViewById(R.id.listView);
	}
	
	@Override
	protected void initData() {
		super.initData();
		
		setInitialData();
	}
	
	public void setInitialData() {
		
		mFilters = Constant.MFILTERS;
		mFiscalYearEndMonths = mFilters.getFiscalYearEndMonths();
		
		adapter = new FilterAdapter(mContext, mFiscalYearEndMonths);
		listView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		adapter.notifyDataSetInvalidated();
		
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
			onFiscalYearFinish.onFiscalYearFinish();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		Boolean checked = mFiscalYearEndMonths.get(position).getChecked();
		if (position == 0) {
			Boolean haveChecked = false;
			for (int i = 0; i < mFiscalYearEndMonths.size(); i ++) {
				if (i == 0) continue;
				if (mFiscalYearEndMonths.get(i).getChecked()) {
					haveChecked = true;
					break;
				}
			}
			if (!haveChecked) {
				return;
			} else {
				mFiscalYearEndMonths.get(position).setChecked(true);
				for (int i = 0; i < mFiscalYearEndMonths.size(); i ++) {
					if (i != 0) mFiscalYearEndMonths.get(i).setChecked(false);
				}
			}
		} else {
			mFiscalYearEndMonths.get(position).setChecked(!checked);
			Boolean haveChecked = false;
			for (int i = 0; i < mFiscalYearEndMonths.size(); i ++) {
				if (i == 0) {
					continue;
				} else {
					if (mFiscalYearEndMonths.get(i).getChecked()) {
						haveChecked = true;
						mFiscalYearEndMonths.get(0).setChecked(false);
					}
				}
			}
			if (!haveChecked) {
				mFiscalYearEndMonths.get(0).setChecked(true);
			}
		}
		adapter.notifyDataSetChanged();
		
		onSearchFromFiscalYear.onSearchFromFiscalYear();
	}
}
