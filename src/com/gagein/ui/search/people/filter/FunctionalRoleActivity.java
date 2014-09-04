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

public class FunctionalRoleActivity extends BaseActivity implements OnItemClickListener{
	
	private ListView listView;
	private FilterAdapter adapter;
	private Filters mFilters;
	private List<FilterItem> mFunctionalRoles;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_company_filter_employee_size);
		
		doInit();
	}
	
	@Override
	protected void initView() {
		super.initView();
		setTitle(R.string.functional_role);
		setLeftImageButton(R.drawable.back_arrow);
		
		listView = (ListView) findViewById(R.id.listView);
	}
	
	@Override
	protected void initData() {
		super.initData();
		mFilters = Constant.MFILTERS;
		mFunctionalRoles = mFilters.getFunctionalRoles();
		
		adapter = new FilterAdapter(mContext, mFunctionalRoles);
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
		Boolean checked = mFunctionalRoles.get(position).getChecked();
		if (position == 0) {
			if (checked) {
				return;
			} else {
				mFunctionalRoles.get(position).setChecked(true);
				for (int i = 0; i < mFunctionalRoles.size(); i ++) {
					if (i != 0) mFunctionalRoles.get(i).setChecked(false);
				}
			}
			
		} else {
			mFunctionalRoles.get(position).setChecked(!checked);
			Boolean haveChecked = false;
			for (int i = 0; i < mFunctionalRoles.size(); i ++) {
				if (i != 0) {
					if (mFunctionalRoles.get(i).getChecked()) {
						haveChecked = true;
						mFunctionalRoles.get(0).setChecked(false);
					}
				}
			}
			if (!haveChecked) {
				mFunctionalRoles.get(0).setChecked(true);
			}
		}
		adapter.notifyDataSetChanged();
		
	}

}
