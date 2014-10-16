package com.gagein.ui.companies;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.gagein.R;
import com.gagein.adapter.FilterCompaniesAdapter;
import com.gagein.model.FacetItemIndustry;
import com.gagein.ui.main.BaseActivity;
import com.gagein.util.Constant;

public class CompaniesFilterActivity extends BaseActivity implements OnItemClickListener{
	
	private FilterCompaniesAdapter adapter;
	private ListView listView;
	private List<FacetItemIndustry> industryDatas;
	
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
		
		industryDatas = Constant.industriesItem;
		
		adapter = new FilterCompaniesAdapter(mContext, industryDatas);
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
			
			for (int i= 0; i < industryDatas.size(); i ++) {
				if (industryDatas.get(i).getSelected()) {
					Constant.INDUSTRIES = industryDatas;
				}
			}
			
			Intent intent = new Intent(CompaniesFilterActivity.this, CompaniesActivity.class);
			setResult(RESULT_OK, intent);
			finish();
			
		} else if (v == leftBtn) {
			
			finish();
			
		}
		
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		
		Boolean checked = industryDatas.get(position).getSelected();
		if (position == 0) {
			Boolean haveChecked = false;
			for (int i = 0; i < industryDatas.size(); i ++) {
				if (i == 0) continue;
				if (industryDatas.get(i).getSelected()) {
					haveChecked = true;
					break;
				}
			}
			if (!haveChecked) {
				return;
			} else {
				industryDatas.get(position).setSelected(true);
				
				for (int i = 0; i < industryDatas.size(); i ++) {
					if (i != 0) industryDatas.get(i).setSelected(false);
				}
			}
		} else {
			
			//circle 
			industryDatas.get(position).setSelected(!checked);
			Boolean haveChecked = false;
			for (int i = 0; i < industryDatas.size(); i ++) {
				if (i == 0) {
					continue;
				} else {
					if (industryDatas.get(i).getSelected()) {
						haveChecked = true;
						industryDatas.get(0).setSelected(false);
					}
				}
			}
			if (!haveChecked) {
				industryDatas.get(0).setSelected(true);
			}
		}
		
		adapter.notifyDataSetChanged();//refresh listview state
		
	}
	
}
