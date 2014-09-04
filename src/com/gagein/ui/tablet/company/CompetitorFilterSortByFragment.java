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
import com.gagein.adapter.FilterSortByAdapter;
import com.gagein.http.APIHttp;
import com.gagein.http.APIHttpMetadata;
import com.gagein.ui.BaseFragment;
import com.gagein.util.Constant;

public class CompetitorFilterSortByFragment extends BaseFragment implements OnItemClickListener{
	
	private FilterSortByAdapter adapter;
	private ListView listView;
	private List<Boolean> checkedList;
	private OnShowCompetitorsFilterFromSortByListener onShowCompetitorsFilterFromSortByListener; 
	
	
	public interface OnShowCompetitorsFilterFromSortByListener {
		public void onShowCompetitorsFilterFromSortByListener();
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			onShowCompetitorsFilterFromSortByListener = (OnShowCompetitorsFilterFromSortByListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + "must implement OnShowCompetitorsFilterFromSortByListener");
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
		
		setTitle(R.string.sort_by);
		setLeftImageButton(R.drawable.back_arrow);
		setRightButton(R.string.done);
		
		listView = (ListView) view.findViewById(R.id.listView);
	}
	
	@Override
	protected void initData() {
		super.initData();
		
		checkedList = new ArrayList<Boolean>();
		for (int i = 0; i < 3; i ++) {
			checkedList.add(false);
		}
		if (Constant.COMPETITOR_SORT_BY == APIHttpMetadata.kCompetitorOrderByRevenueSize) {
			checkedList.set(0, true);
		} else if (Constant.COMPETITOR_SORT_BY == APIHttpMetadata.kCompetitorOrderByEmployeeSize) {
			checkedList.set(1, true);
		} else if (Constant.COMPETITOR_SORT_BY == APIHttpMetadata.kCompetitorOrderByName) {
			checkedList.set(2, true);
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
			onShowCompetitorsFilterFromSortByListener.onShowCompetitorsFilterFromSortByListener();
		} else if (v == rightBtn) {
			onShowCompetitorsFilterFromSortByListener.onShowCompetitorsFilterFromSortByListener();
			for (int i= 0; i < checkedList.size(); i ++) {
				if (checkedList.get(i) == true) {
					if (i == 0) {
						Constant.COMPETITOR_SORT_BY = APIHttpMetadata.kCompetitorOrderByRevenueSize;
					} else if (i == 1) {
						Constant.COMPETITOR_SORT_BY = APIHttpMetadata.kCompetitorOrderByEmployeeSize;
					} else if (i == 2) {
						Constant.COMPETITOR_SORT_BY = APIHttpMetadata.kCompetitorOrderByName;
					}
				}
			}
		}
	}
	
	@Override
	protected void setData() {
		super.setData();
		adapter = new FilterSortByAdapter(mContext, checkedList, Constant.COMPANY_COMPETITOR);
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
	}

}
