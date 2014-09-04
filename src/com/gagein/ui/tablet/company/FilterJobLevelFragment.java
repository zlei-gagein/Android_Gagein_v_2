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
import com.gagein.adapter.FilterJobLevelAdapter;
import com.gagein.http.APIHttp;
import com.gagein.model.Facet;
import com.gagein.model.FacetItem;
import com.gagein.ui.BaseFragment;
import com.gagein.util.Constant;

public class FilterJobLevelFragment extends BaseFragment implements OnItemClickListener{

	private FilterJobLevelAdapter adapter;
	private ListView listView;
	private List<Boolean> checkedList;
	private Facet mFacet;
	private List<FacetItem> jobLevels;
	private OnClosedJoblevel onClosedJoblevel;
	
	public interface OnClosedJoblevel {
		public void onClosedJoblevel();
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			onClosedJoblevel = (OnClosedJoblevel) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + "must implement OnClosedJoblevel");
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
		
		setTitle(R.string.job_level);
		setLeftImageButton(R.drawable.back_arrow);
		setRightButton(R.string.done);
		
		listView = (ListView) view.findViewById(R.id.listView);
	}
	
	@Override
	protected void initData() {
		super.initData();
		
		mFacet = Constant.currentFacet;
		if (null == mFacet) return;
		jobLevels = mFacet.jobLevels;
		if (null == jobLevels) return;
		checkedList = new ArrayList<Boolean>();
		for (int i = 0; i < jobLevels.size(); i ++) {
			checkedList.add(false);
		}
		
		if (Constant.JOB_LEVEL_ID == 0) {
			checkedList.set(0, true);
		} else {
			for (int i = 0; i < jobLevels.size(); i ++) {
				if (Constant.JOB_LEVEL_ID == jobLevels.get(i).id) {
					checkedList.set(i, true);
				}
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
			onClosedJoblevel.onClosedJoblevel();
		} else if (v == rightBtn) {
			for (int i= 0; i < checkedList.size(); i ++) {
				if (checkedList.get(i) == true) {
					Constant.JOB_LEVEL_ID = jobLevels.get(i).id;
					Constant.JOB_LEVEL_NAME = jobLevels.get(i).name;
				}
			}
			onClosedJoblevel.onClosedJoblevel();
		}
	}
	
	@Override
	protected void setData() {
		super.setData();
		adapter = new FilterJobLevelAdapter(mContext, checkedList);
		listView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		adapter.notifyDataSetInvalidated();
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
