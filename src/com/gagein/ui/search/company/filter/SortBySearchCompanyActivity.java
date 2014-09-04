package com.gagein.ui.search.company.filter;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.gagein.R;
import com.gagein.adapter.search.FilterAdapter;
import com.gagein.model.filter.FilterItem;
import com.gagein.model.filter.Filters;
import com.gagein.ui.main.BaseActivity;
import com.gagein.ui.search.SearchCompanyActivity;
import com.gagein.util.Constant;

public class SortBySearchCompanyActivity extends BaseActivity implements OnItemClickListener{
	
	private ListView listView;
	private Filters mFilters;
	private List<FilterItem> sortBy;
	private FilterAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_company_sortby);
		
		doInit();
	}
	
	@Override
	protected void initView() {
		super.initView();
		setTitle(R.string.sort_by);
		setRightButton(R.string.done);
		
		listView = (ListView) findViewById(R.id.listView);
	}
	
	@Override
	protected void initData() {
		super.initData();
		mFilters = Constant.MFILTERS;
		sortBy = mFilters.getSortByFromBuz();
		adapter = new FilterAdapter(mContext, sortBy);
		listView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		adapter.notifyDataSetInvalidated();
	}
	
	@Override
	protected void setOnClickListener() {
		super.setOnClickListener();
		rightBtn.setOnClickListener(this);
		listView.setOnItemClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		if (v == rightBtn) {
			setResultToActivity();
		}
	}

	private void setResultToActivity() {
		Intent intent = new Intent();
		intent.setClass(SortBySearchCompanyActivity.this, SearchCompanyActivity.class);
		setResult(RESULT_OK, intent);
		finish();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		for (int i = 0; i < sortBy.size(); i ++) {
			sortBy.get(i).setChecked(false);
		}
		sortBy.get(position).setChecked(true);
		adapter.notifyDataSetChanged();
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		 
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            //do something...
        	setResultToActivity();
         }
         return super.onKeyDown(keyCode, event);
	}

}
