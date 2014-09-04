package com.gagein.ui.search.people.filter;

import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.gagein.R;
import com.gagein.adapter.search.FilterAdapter;
import com.gagein.model.filter.FilterItem;
import com.gagein.model.filter.Filters;
import com.gagein.ui.main.BaseActivity;
import com.gagein.util.Constant;

public class JobLevelActivity extends BaseActivity implements OnItemClickListener{
	
	private ListView listView;
	private FilterAdapter adapter;
	private Filters mFilters;
	private List<FilterItem> mJobLevels;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_company_filter_employee_size);
		
		doInit();
	}
	
	@Override
	protected void initView() {
		super.initView();
		setTitle(R.string.job_level);
		setLeftImageButton(R.drawable.back_arrow);
		
		listView = (ListView) findViewById(R.id.listView);
	}
	
	@Override
	protected void initData() {
		super.initData();
		
		mFilters = Constant.MFILTERS;
		mJobLevels = mFilters.getJobLevel();
		
		adapter = new FilterAdapter(mContext, mJobLevels);
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
			finish();
		}
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		Boolean checked = mJobLevels.get(position).getChecked();
		if (position == 0) {
			Boolean haveChecked = false;
			for (int i = 0; i < mJobLevels.size(); i ++) {
				if (i == 0) continue;
				if (mJobLevels.get(i).getChecked()) {
					haveChecked = true;
					break;
				}
			}
			if (!haveChecked) {
				return;
			} else {
				mJobLevels.get(position).setChecked(true);
				for (int i = 0; i < mJobLevels.size(); i ++) {
					if (i != 0) mJobLevels.get(i).setChecked(false);
				}
			}
		} else {
			mJobLevels.get(position).setChecked(!checked);
			Boolean haveChecked = false;
			for (int i = 0; i < mJobLevels.size(); i ++) {
				if (i == 0) {
					continue;
				} else {
					if (mJobLevels.get(i).getChecked()) {
						haveChecked = true;
						mJobLevels.get(0).setChecked(false);
					}
				}
			}
			if (!haveChecked) {
				mJobLevels.get(0).setChecked(true);
			}
		}
		adapter.notifyDataSetChanged();
		
	}

}
