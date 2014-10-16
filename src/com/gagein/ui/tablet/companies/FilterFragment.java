package com.gagein.ui.tablet.companies;

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
import com.gagein.adapter.FilterCompaniesAdapter;
import com.gagein.http.APIHttp;
import com.gagein.model.FacetItemIndustry;
import com.gagein.ui.BaseFragment;
import com.gagein.util.Constant;

public class FilterFragment extends BaseFragment implements OnItemClickListener{
	
	private FilterCompaniesAdapter adapter;
	private ListView listView;
	private List<FacetItemIndustry> industryDatas;
	private OnFilterCancle onFilterCancle;
	private OnFilterDone onFilterDone;
	private OnRefreshCompaniesForFilter onRefreshCompaniesForFilter;
	
	public interface OnFilterCancle {
		public void onFilterCancle();
	}
	
	public interface OnFilterDone {
		public void onFilterDone();
	}
	public interface OnRefreshCompaniesForFilter {
		public void onRefreshCompaniesForFilter();
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
		try {
			onRefreshCompaniesForFilter = (OnRefreshCompaniesForFilter) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + "must implement onRefreshCompaniesForFilter");
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
		setLeftImageButton(R.drawable.back_arrow);
		
		listView = (ListView) view.findViewById(R.id.listView);
		
	}
	
	@Override
	protected void initData() {
		super.initData();
		

		industryDatas = Constant.industriesItem;
		
		adapter = new FilterCompaniesAdapter(mContext, industryDatas);
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
			
			onFilterCancle.onFilterCancle();
			
		}
		
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		
		Boolean checked = industryDatas.get(position).getSelected();
		if (position == 0) {
			Boolean haveChecked = false;
			for (int i = 0; i < industryDatas.size(); i ++) {
				if (i == 0) continue;
				if (industryDatas.get(i).getSelected()) {
					haveChecked = true;
					break;
				}
			}
			if (!haveChecked) {
				return;
			} else {
				industryDatas.get(position).setSelected(true);
				
				for (int i = 0; i < industryDatas.size(); i ++) {
					if (i != 0) industryDatas.get(i).setSelected(false);
				}
			}
		} else {
			
			//circle 
			industryDatas.get(position).setSelected(!checked);
			Boolean haveChecked = false;
			for (int i = 0; i < industryDatas.size(); i ++) {
				if (i == 0) {
					continue;
				} else {
					if (industryDatas.get(i).getSelected()) {
						haveChecked = true;
						industryDatas.get(0).setSelected(false);
					}
				}
			}
			if (!haveChecked) {
				industryDatas.get(0).setSelected(true);
			}
		}
		
		adapter.notifyDataSetChanged();//refresh listview state
		
		for (int i= 0; i < industryDatas.size(); i ++) {
			if (industryDatas.get(i).getSelected()) {
				Constant.INDUSTRIES = industryDatas;
			}
		}
		
		onRefreshCompaniesForFilter.onRefreshCompaniesForFilter();
		
	}

}
