package com.gagein.ui.company;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.gagein.R;
import com.gagein.adapter.CompetitorIndustryAdapter;
import com.gagein.model.FacetItemIndustry;
import com.gagein.ui.main.BaseActivity;
import com.gagein.util.Constant;

public class CompetitorFilterIndustryActivity extends BaseActivity implements OnItemClickListener{
	
	private CompetitorIndustryAdapter adapter;
	private ListView listView;
	private List<Boolean> checkedList;
	private List<FacetItemIndustry> industries;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_filter_news);
		
		doInit();
	}
	
	@Override
	protected void initView() {
		super.initView();
		
		setTitle(R.string.industry);
		setLeftImageButton(R.drawable.back_arrow);
		setRightButton(R.string.done);
		
		listView = (ListView) findViewById(R.id.listView);
	}
	
	@Override
	protected void initData() {
		super.initData();
		
		industries = Constant.currentCompetitorIndustries;
		checkedList = new ArrayList<Boolean>();
		for (int i = 0; i < industries.size(); i ++) {
			checkedList.add(false);
		}
		
		for (int i = 0; i < industries.size(); i ++) {
			if (Constant.COMPETITOR_FILTER_PARAM_VALUE.equalsIgnoreCase(industries.get(i).filter_param_value)) {
				checkedList.set(i, true);
			}
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
			finish();
		} else if (v == rightBtn) {
			finish();
			for (int i= 0; i < checkedList.size(); i ++) {
				if (checkedList.get(i) == true) {
					Constant.COMPETITOR_FILTER_PARAM_VALUE = industries.get(i).filter_param_value;
				}
			}
		}
	}
	
	@Override
	protected void setData() {
		super.setData();
		adapter = new CompetitorIndustryAdapter(mContext, checkedList);
		listView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		adapter.notifyDataSetInvalidated();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		
		for (int i= 0; i < checkedList.size(); i ++) {
			if (i == arg2) {
				checkedList.set(i, true);
			} else {
				checkedList.set(i, false);
			}
		}
		adapter.notifyDataSetChanged();
	}

}
