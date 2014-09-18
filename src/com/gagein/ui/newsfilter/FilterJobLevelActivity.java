package com.gagein.ui.newsfilter;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.gagein.R;
import com.gagein.adapter.FilterJobLevelAdapter;
import com.gagein.model.Facet;
import com.gagein.model.FacetItem;
import com.gagein.ui.main.BaseActivity;
import com.gagein.util.Constant;

public class FilterJobLevelActivity extends BaseActivity implements OnItemClickListener{
	
	private FilterJobLevelAdapter adapter;
	private ListView listView;
	private Facet mFacet;
	private ArrayList<FacetItem> jobLevels;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_filter_news);
		
		doInit();
	}
	
	@Override
	protected void initView() {
		super.initView();
		
		setTitle(R.string.job_level);
		setLeftImageButton(R.drawable.back_arrow);
		setRightButton(R.string.done);
		
		listView = (ListView) findViewById(R.id.listView);
	}
	
	@Override
	protected void initData() {
		super.initData();
		
		mFacet = Constant.currentFacet;
		if (null == mFacet) return;
		jobLevels = mFacet.jobLevels;
		if (null == jobLevels) return;
		
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
			finish();
		} else if (v == rightBtn) {
			for (int i= 0; i < jobLevels.size(); i ++) {
				if (jobLevels.get(i).selected) {
					Constant.currentJobLevelForCompanyPeopleFilter = jobLevels;
				}
			}
			finish();
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
		
	}

}
