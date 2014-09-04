package com.gagein.ui.search.company.filter;

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

public class RevenueSizeActivity extends BaseActivity implements OnItemClickListener{
	
	private ListView listView;
	private FilterAdapter adapter;
	private Filters mFilters;
	private List<FilterItem> mSalesVolumes;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_company_filter_employee_size);
		
		doInit();
	}
	
	@Override
	protected void initView() {
		super.initView();
		setTitle(R.string.revenue_size);
		setLeftImageButton(R.drawable.back_arrow);
		
		listView = (ListView) findViewById(R.id.listView);
	}
	
	@Override
	protected void initData() {
		super.initData();
		
		mFilters = Constant.MFILTERS;
		mSalesVolumes = mFilters.getSalesVolumeFromBuz();
		
		adapter = new FilterAdapter(mContext, mSalesVolumes);
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
		Boolean checked = mSalesVolumes.get(position).getChecked();
		if (position == 0) {
			Boolean haveChecked = false;
			for (int i = 0; i < mSalesVolumes.size(); i ++) {
				if (i == 0) continue;
				if (mSalesVolumes.get(i).getChecked()) {
					haveChecked = true;
					break;
				}
			}
			if (!haveChecked) {
				return;
			} else {
				mSalesVolumes.get(position).setChecked(true);
				for (int i = 0; i < mSalesVolumes.size(); i ++) {
					if (i != 0) mSalesVolumes.get(i).setChecked(false);
				}
			}
		} else {
			mSalesVolumes.get(position).setChecked(!checked);
			Boolean haveChecked = false;
			for (int i = 0; i < mSalesVolumes.size(); i ++) {
				if (i == 0) {
					continue;
				} else {
					if (mSalesVolumes.get(i).getChecked()) {
						haveChecked = true;
						mSalesVolumes.get(0).setChecked(false);
					}
				}
			}
			if (!haveChecked) {
				mSalesVolumes.get(0).setChecked(true);
			}
		}
		adapter.notifyDataSetChanged();
		
	}

}
