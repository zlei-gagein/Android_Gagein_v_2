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

public class EmployeeSizeActivity extends BaseActivity implements OnItemClickListener{
	
	private ListView listView;
	private FilterAdapter adapter;
	private Filters mFilters;
	private List<FilterItem> mEmployeeSizes;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_company_filter_employee_size);
		
		doInit();
	}
	
	@Override
	protected void initView() {
		super.initView();
		setTitle(R.string.employee_size);
		setLeftImageButton(R.drawable.back_arrow);
		
		listView = (ListView) findViewById(R.id.listView);
	}
	
	@Override
	protected void initData() {
		super.initData();
		mFilters = Constant.MFILTERS;
		mEmployeeSizes = mFilters.getEmployeeSizeFromBuz();//TODO
		
		adapter = new FilterAdapter(mContext, mEmployeeSizes);
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
	public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
		Boolean checked = mEmployeeSizes.get(position).getChecked();
		
		if (position == 0) {
			Boolean haveChecked = false;
			for (int i = 0; i < mEmployeeSizes.size(); i ++) {
				if (i == 0) continue;
				if (mEmployeeSizes.get(i).getChecked()) {
					haveChecked = true;
					break;
				}
			}
			if (!haveChecked) {
				return;
			} else {
				mEmployeeSizes.get(position).setChecked(true);
				for (int i = 0; i < mEmployeeSizes.size(); i ++) {
					if (i != 0) mEmployeeSizes.get(i).setChecked(false);
				}
			}
		} else {
			mEmployeeSizes.get(position).setChecked(!checked);
			Boolean haveChecked = false;
			for (int i = 0; i < mEmployeeSizes.size(); i ++) {
				if (i == 0) {
					continue;
				} else {
					if (mEmployeeSizes.get(i).getChecked()) {
						haveChecked = true;
						mEmployeeSizes.get(0).setChecked(false);
					}
				}
			}
			if (!haveChecked) {
				mEmployeeSizes.get(0).setChecked(true);
			}
		}
		adapter.notifyDataSetChanged();
		
	}

}
