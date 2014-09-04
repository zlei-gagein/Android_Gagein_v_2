package com.gagein.ui.tablet.company;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.util.Log;
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

public class PeopleFilterJobLevelFragment extends BaseFragment implements OnItemClickListener{
	
	private FilterJobLevelAdapter adapter;
	private ListView listView;
	private List<Boolean> checkedList;
	private Facet mFacet;
	private List<FacetItem> jobLevels;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.activity_filter_news, container, false);
		mContext = getActivity();
		mApiHttp = new APIHttp(mContext);
		
		doInit();
		initData();
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
		Log.v("silen", "jobLevels.size = " + jobLevels.size());
		checkedList = new ArrayList<Boolean>();
		for (int i = 0; i < jobLevels.size() + 1; i ++) {
			checkedList.add(false);
		}
		
		if (Constant.JOB_LEVEL_ID == 0) {
			checkedList.set(0, true);
		} else {
			for (int i = 0; i < jobLevels.size(); i ++) {
				if (Constant.JOB_LEVEL_ID == jobLevels.get(i).id) {
					checkedList.set(i + 1, true);
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
		if (v == leftImageBtn) {//TODO
//			finish();
		} else if (v == rightBtn) {
			for (int i= 0; i < checkedList.size(); i ++) {
				if (checkedList.get(i) == true) {
					if (i == 0) {
						Constant.JOB_LEVEL_ID = 0;
					} else {
						Constant.JOB_LEVEL_ID = jobLevels.get(i - 1).id;
						Constant.JOB_LEVEL_NAME = jobLevels.get(i - 1).name;
					}
				}
			}
			//TODO
//			finish();
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
