package com.gagein.ui.tablet.company;

import java.util.ArrayList;

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
	private Facet mFacet;
	private ArrayList<FacetItem> jobLevels;
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
		
		listView = (ListView) view.findViewById(R.id.listView);
	}
	
	@Override
	protected void initData() {
		super.initData();
		
		if (Constant.currentJobLevelForCompanyPeopleFilter.size() > 0) {
			jobLevels = Constant.currentJobLevelForCompanyPeopleFilter;
		} else {
			mFacet = Constant.currentFacet;
			if (null == mFacet) return;
			jobLevels = mFacet.jobLevels;
			if (null == jobLevels) return;
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
			
		}
	}
	
	@Override
	protected void setData() {
		super.setData();
		adapter = new FilterJobLevelAdapter(mContext, jobLevels);
		listView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		adapter.notifyDataSetInvalidated();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		
		Boolean checked = jobLevels.get(position).selected;
		if (position == 0) {
			Boolean haveChecked = false;
			for (int i = 0; i < jobLevels.size(); i ++) {
				if (i == 0) continue;
				if (jobLevels.get(i).selected) {
					haveChecked = true;
					break;
				}
			}
			if (!haveChecked) {
				return;
			} else {
				jobLevels.get(position).selected = true;
				
				for (int i = 0; i < jobLevels.size(); i ++) {
					if (i != 0) jobLevels.get(i).selected = false;
				}
			}
		} else {
			
			//circle 
			jobLevels.get(position).selected = !checked;
			Boolean haveChecked = false;
			for (int i = 0; i < jobLevels.size(); i ++) {
				if (i == 0) {
					continue;
				} else {
					if (jobLevels.get(i).selected) {
						haveChecked = true;
						jobLevels.get(0).selected = false;
					}
				}
			}
			if (!haveChecked) {
				jobLevels.get(0).selected = true;
			}
		}
		
		adapter.notifyDataSetChanged();//refresh listview state
		
		for (int i= 0; i < jobLevels.size(); i ++) {
			if (jobLevels.get(i).selected) {
				Constant.currentJobLevelForCompanyPeopleFilter = jobLevels;
			}
		}
		
	}
	
}
