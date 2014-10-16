package com.gagein.ui.company;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.gagein.R;
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
		setTitle(R.string.filters);
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
	protected void setOnClickListener() {
		super.setOnClickListener();
		industry.setOnClickListener(this);
		industryBtn.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		if (v == rightBtn) {
			Intent intent = new Intent();
			intent.setAction(Constant.BROADCAST_FILTER_REFRESH_COMPETITORS);
			mContext.sendBroadcast(intent);
			finish();
		} else if (v == industry || v == industryBtn) {
			startActivitySimple(CompetitorFilterIndustryActivity.class);
		}
	}
	
}
