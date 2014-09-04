package com.gagein.ui.tablet.companies;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.gagein.R;
import com.gagein.adapter.FilterCompaniesAdapter;
import com.gagein.http.APIHttp;
import com.gagein.model.FacetItemIndustry;
import com.gagein.ui.BaseFragment;
import com.gagein.util.Constant;

public class FilterFragment extends BaseFragment implements OnItemClickListener{
	
	private FilterCompaniesAdapter adapter;
	private ListView listView;
	private List<FacetItemIndustry> industryData;
	private List<Boolean> checkedList;
	private OnFilterCancle onFilterCancle;
	private OnFilterDone onFilterDone;
	
	public interface OnFilterCancle {
		public void onFilterCancle();
	}
	
	public interface OnFilterDone {
		public void onFilterDone();
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		try {
			onFilterCancle = (OnFilterCancle) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + "must implement OnFilterCancle");
		}
		try {
			onFilterDone = (OnFilterDone) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + "must implement OnFilterDone");
		}
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		view = inflater.inflate(R.layout.activity_filter_companies, container, false);
		mContext = getActivity();
		mApiHttp = new APIHttp(mContext);
		
		doInit();
		
		return view;
	}
	
	@Override
	protected void initView() {
		super.initView();
		
		setTitle(R.string.companies_filters);
		setLeftButton(R.string.cancel);
		setRightButton(R.string.done);
		
		listView = (ListView) view.findViewById(R.id.listView);
		
	}
	
	@Override
	protected void initData() {
		super.initData();
		
		industryData = Constant.industriesItem;
		
		checkedList = new ArrayList<Boolean>();
		for (int i = 0; i < industryData.size(); i ++) {
			checkedList.add(i == 0 ? true : false);
		}
		
		if (TextUtils.isEmpty(Constant.FILTER_INDUSTRY_NAME)) {
			checkedList.set(0, true);
		} else {
			for (int i = 0; i < industryData.size(); i ++) {
				if (Constant.FILTER_INDUSTRY_NAME.equalsIgnoreCase(industryData.get(i).item_name)) {
					checkedList.set(i + 1, true);
				}
			}
		}
		
		adapter = new FilterCompaniesAdapter(mContext, checkedList);
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
		
		if (v == leftBtn) {
			
			onFilterCancle.onFilterCancle();
			
		} else if (v == rightBtn) {
			
			for (int i= 0; i < checkedList.size(); i ++) {
				if (checkedList.get(i) == true) {
					Constant.FILTER_INDUSTRY_NAME = industryData.get(i).item_name;
					Constant.INDUSTRYID = industryData.get(i).filter_param_value;
				}
			}
			onFilterDone.onFilterDone();
			
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		
		for (int i= 0; i < checkedList.size(); i ++) {
			if (i == position) {
				checkedList.set(i, true);
			} else {
				checkedList.set(i, false);
			}
		}
		adapter.notifyDataSetChanged();
		
	}

}
