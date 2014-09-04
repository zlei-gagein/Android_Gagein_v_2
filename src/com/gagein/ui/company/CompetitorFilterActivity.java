package com.gagein.ui.company;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.gagein.R;
import com.gagein.model.FacetItemIndustry;
import com.gagein.ui.main.BaseActivity;
import com.gagein.util.Constant;

public class CompetitorFilterActivity extends BaseActivity {
	
	
	private RelativeLayout industry;
	private Button industryBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_competitorfilter);
		
		doInit();
	}
	
	@Override
	protected void initView() {
		super.initView();
		setTitle(R.string.competitors_filter);
		setRightButton(R.string.done);
		
		industry = (RelativeLayout) findViewById(R.id.industry);
		industryBtn = (Button) findViewById(R.id.industryBtn);
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		initData();
	}
	
	@Override
	protected void initData() {
		super.initData();
		
		setIndustry();
	}
	
	private void setIndustry() {
		List<FacetItemIndustry> industries = Constant.currentCompetitorIndustries;
		for (int i = 0; i < industries.size(); i ++) {
			if (Constant.COMPETITOR_FILTER_PARAM_VALUE.equalsIgnoreCase(industries.get(i).filter_param_value)) {
				industryBtn.setText(industries.get(i).item_name);
			}
		}
	}
	
	@Override
	protected void setOnClickListener() {
		super.setOnClickListener();
		industry.setOnClickListener(this);
		industryBtn.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		if (v == rightBtn) {//TODO
			Intent intent = new Intent();
			intent.setAction(Constant.BROADCAST_FILTER_REFRESH_COMPETITORS);
			mContext.sendBroadcast(intent);
			finish();
		} else if (v == industry || v == industryBtn) {
			startActivitySimple(CompetitorFilterIndustryActivity.class);
		}
	}
	
}