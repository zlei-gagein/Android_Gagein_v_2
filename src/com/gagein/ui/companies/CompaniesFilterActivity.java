package com.gagein.ui.companies;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.gagein.R;
import com.gagein.adapter.FilterCompaniesAdapter;
import com.gagein.model.FacetItemIndustry;
import com.gagein.ui.main.BaseActivity;
import com.gagein.util.Constant;
import com.gagein.util.Log;

public class CompaniesFilterActivity extends BaseActivity implements OnItemClickListener{
	
	private FilterCompaniesAdapter adapter;
	private ListView listView;
	private List<FacetItemIndustry> industryData;
	private List<Boolean> checkedList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_filter_companies);
		
		doInit();
		
	}
	
	@Override
	protected void initView() {
		super.initView();
		
		setTitle(R.string.companies_filters);
		setLeftButton(R.string.cancel);
		setRightButton(R.string.done);
		
		listView = (ListView) findViewById(R.id.listView);
		
	}
	
	@Override
	protected void initData() {
		super.initData();
		
		industryData = Constant.industriesItem;
		
		checkedList = new ArrayList<Boolean>();
		for (int i = 0; i < industryData.size(); i ++) {
			checkedList.add(false);
		}
		
		if (TextUtils.isEmpty(Constant.FILTER_INDUSTRY_NAME)) {
			checkedList.set(0, true);
		} else {
			for (int i = 0; i < industryData.size(); i ++) {
				if (Constant.FILTER_INDUSTRY_NAME.equalsIgnoreCase(industryData.get(i).item_name)) {
					checkedList.set(i, true);
				}
			}
		}
		
		adapter = new FilterCompaniesAdapter(mContext, checkedList);
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
		
		if (v == rightBtn) {
			
			for (int i= 0; i < checkedList.size(); i ++) {
				if (checkedList.get(i) == true) {
					Constant.FILTER_INDUSTRY_NAME = industryData.get(i).item_name;
					String industryId = industryData.get(i).filter_param_value;
					if (null == industryId || industryId.isEmpty()) industryId = "0";
					Constant.INDUSTRYID = industryId;
				}
			}
			
			//TODO
			Intent intent = new Intent(CompaniesFilterActivity.this, CompaniesActivity.class);
			setResult(RESULT_OK, intent);
			finish();
			Log.v("silen", "Constant.INDUSTRYID = " + Constant.INDUSTRYID);
			
		} else if (v == leftBtn) {
			
			finish();
			
		}
		
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
