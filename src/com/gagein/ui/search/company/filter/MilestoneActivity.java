package com.gagein.ui.search.company.filter;

import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.gagein.R;
import com.gagein.adapter.search.FilterAdapter;
import com.gagein.model.filter.FilterItem;
import com.gagein.model.filter.Filters;
import com.gagein.ui.main.BaseActivity;
import com.gagein.util.CommonUtil;
import com.gagein.util.Constant;

public class MilestoneActivity extends BaseActivity implements OnItemClickListener{
	
	private ListView milestonesListView;
	private ListView dateRankListView;
	private LinearLayout thePastLayout;
	private Filters mFilters;
	public List<FilterItem> mMilestones;
	public List<FilterItem> mDateRanks;
	private FilterAdapter milestoneAdapter;
	private FilterAdapter dateRankAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_company_filter_milestone);
		
		doInit();
	}
	
	@Override
	protected void initView() {
		super.initView();
		setTitle(R.string.milestone);
		setLeftImageButton(R.drawable.back_arrow);
		
		milestonesListView = (ListView) findViewById(R.id.milestonesListView);
		dateRankListView = (ListView) findViewById(R.id.dataRankList);
		thePastLayout = (LinearLayout) findViewById(R.id.thePastLayout);
	}
	
	@Override
	protected void initData() {
		super.initData();
		mFilters = Constant.MFILTERS;
		mMilestones = mFilters.getMileStones();
		mDateRanks = mFilters.getMileStoneDateRange();
		
		milestoneAdapter = new FilterAdapter(mContext, mMilestones);
		milestonesListView.setAdapter(milestoneAdapter);
		CommonUtil.setListViewHeight(milestonesListView);
		milestoneAdapter.notifyDataSetChanged();
		milestoneAdapter.notifyDataSetInvalidated();
		
		dateRankAdapter = new FilterAdapter(mContext, mDateRanks);
		dateRankListView.setAdapter(dateRankAdapter);
		CommonUtil.setListViewHeight(dateRankListView); 
		dateRankAdapter.notifyDataSetChanged();
		dateRankAdapter.notifyDataSetInvalidated();
		
		setPastDateShow();//cycle for check which item is checked
	}
	
	@Override
	protected void setOnClickListener() {
		super.setOnClickListener();
		milestonesListView.setOnItemClickListener(this);
		dateRankListView.setOnItemClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		if (v == leftImageBtn) {
			finish();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parentView, View view, int position, long arg3) {
		if (parentView == milestonesListView) {
			Boolean checked = mMilestones.get(position).getChecked();
			mMilestones.get(position).setChecked(!checked);
			milestoneAdapter.notifyDataSetChanged();//refresh listview state
			setPastDateShow();//set past date show or hide
		} else if (parentView == dateRankListView) {
			Boolean checked = mDateRanks.get(position).getChecked();
			mDateRanks.get(position).setChecked(!checked);
			dateRankAdapter.notifyDataSetChanged();
		}
		
	}
	
	private void setPastDateShow() {
		Boolean haveChecked = false;
		for (int i = 0; i < mMilestones.size(); i ++) {
			if (mMilestones.get(i).getChecked()) {
				haveChecked = true;
				thePastLayout.setVisibility(View.VISIBLE);
				break;
			}
		}
		if (!haveChecked) {
			thePastLayout.setVisibility(View.GONE);//set false for date ranks when the past layout gone 
			for (int i = 0 ; i < mDateRanks.size(); i++) {
				mDateRanks.get(i).setChecked(false);
			}
			dateRankAdapter.notifyDataSetChanged();
		}
	}
}
