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
import com.gagein.util.Log;

public class CompetitorFilterIndustryActivity extends BaseActivity implements OnItemClickListener{
	
	private CompetitorIndustryAdapter adapter;
	private ListView listView;
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
			int j = 0;
			List<String> industryIds = new ArrayList<String>();
			for (int i= 0; i < industries.size(); i ++) {
				if (industries.get(i).getSelected() == true) {
					industryIds.add(j, industries.get(i).filter_param_value);
					j ++;
				}
			}
			Constant.COMPETITOR_FILTER_PARAM_VALUE = industryIds;
		}
	}
	
	@Override
	protected void setData() {
		super.setData();
		
		adapter = new CompetitorIndustryAdapter(mContext, industries);
		listView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		adapter.notifyDataSetInvalidated();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		
		Boolean checked = industries.get(position).getSelected();
		if (position == 0) {
			Boolean haveChecked = false;
			for (int i = 0; i < industries.size(); i ++) {
				if (i == 0) continue;
				if (industries.get(i).getSelected()) {
					haveChecked = true;
					break;
				}
			}
			if (!haveChecked) {
				return;
			} else {
				industries.get(position).setSelected(true);
				
				for (int i = 0; i < industries.size(); i ++) {
					if (i != 0) industries.get(i).setSelected(false);
				}
			}
		} else {
			
			//circle 
			industries.get(position).setSelected(!checked);
			Boolean haveChecked = false;
			for (int i = 0; i < industries.size(); i ++) {
				if (i == 0) {
					continue;
				} else {
					if (industries.get(i).getSelected()) {
						haveChecked = true;
						industries.get(0).setSelected(false);
					}
				}
			}
			if (!haveChecked) {
				industries.get(0).setSelected(true);
			}
		}
		
		adapter.notifyDataSetChanged();//refresh listview state
		
	}

}
