package com.gagein.ui.tablet.company;

import java.util.ArrayList;
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
import com.gagein.adapter.CompetitorIndustryAdapter;
import com.gagein.http.APIHttp;
import com.gagein.model.FacetItemIndustry;
import com.gagein.ui.BaseFragment;
import com.gagein.util.Constant;

public class CompetitorFilterIndustryFragment extends BaseFragment implements OnItemClickListener{
	
	private CompetitorIndustryAdapter adapter;
	private ListView listView;
	private List<Boolean> checkedList;
	private List<FacetItemIndustry> industries;
	private OnShowCompetitorsFilterFromIndustryListener onShowCompetitorsFilterFromIndustryListener; 
	private OnRefreshCompetitors onRefreshCompetitors; 
	
	public interface OnShowCompetitorsFilterFromIndustryListener {
		public void onShowCompetitorsFilterFromIndustryListener();
	}
	public interface OnRefreshCompetitors {
		public void onRefreshCompetitors();
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			onShowCompetitorsFilterFromIndustryListener = (OnShowCompetitorsFilterFromIndustryListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + "must implement OnShowCompetitorsFilterFromIndustryListener");
		}
		try {
			onRefreshCompetitors = (OnRefreshCompetitors) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + "must implement OnRefreshCompetitors");
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.activity_filter_news, container, false);
		mContext = getActivity();
		mApiHttp = new APIHttp(mContext);
		doInit();
		return view;
	}
	
	@Override
	protected void initView() {
		super.initView();
		
		setTitle(R.string.industry);
		setLeftImageButton(R.drawable.back_arrow);
		setRightButton(R.string.done);
		
		listView = (ListView) view.findViewById(R.id.listView);
	}
	
	@Override
	protected void initData() {
		super.initData();
		
		industries = Constant.currentCompetitorIndustries;
		checkedList = new ArrayList<Boolean>();
		for (int i = 0; i < industries.size(); i ++) {
			checkedList.add(false);
		}
		
		for (int i = 0; i < industries.size(); i ++) {
			if (Constant.COMPETITOR_FILTER_PARAM_VALUE.equalsIgnoreCase(industries.get(i).filter_param_value)) {
				checkedList.set(i, true);
			}
		}
		
		setData();
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
			onShowCompetitorsFilterFromIndustryListener.onShowCompetitorsFilterFromIndustryListener();
		} else if (v == rightBtn) {
			onShowCompetitorsFilterFromIndustryListener.onShowCompetitorsFilterFromIndustryListener();
		}
	}
	
	@Override
	protected void setData() {
		super.setData();
		adapter = new CompetitorIndustryAdapter(mContext, checkedList);
		listView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		adapter.notifyDataSetInvalidated();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		
		for (int i= 0; i < checkedList.size(); i ++) {
			if (i == arg2) {
				checkedList.set(i, true);
			} else {
				checkedList.set(i, false);
			}
		}
		adapter.notifyDataSetChanged();
		
		for (int i= 0; i < checkedList.size(); i ++) {
			if (checkedList.get(i) == true) {
				Constant.COMPETITOR_FILTER_PARAM_VALUE = industries.get(i).filter_param_value;
			}
		}
		onRefreshCompetitors.onRefreshCompetitors();
	}

}
