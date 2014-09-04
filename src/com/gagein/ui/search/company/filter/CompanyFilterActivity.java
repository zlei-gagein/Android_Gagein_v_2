package com.gagein.ui.search.company.filter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.gagein.R;
import com.gagein.ui.main.BaseActivity;
import com.gagein.ui.search.SearchCompanyActivity;
import com.gagein.util.CommonUtil;
import com.gagein.util.Constant;

public class CompanyFilterActivity extends BaseActivity {
	
	private Button newsTriggers;
	private Button headquarters;
	private Button companies;
	private Button industry;
	private Button employeeSize;
	private Button revenueSize;
	private Button ownership;
	private Button milestone;
	private Button rank;
	private Button fiscalYearEnd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_company_filter);

		doInit();
	}

	@Override
	protected void initView() {
		super.initView();
		setTitle(R.string.company_filters);
		setLeftImageButton(R.drawable.back_arrow);
		setRightButton(R.string.u_search);
		
		newsTriggers = (Button) findViewById(R.id.newsTriggers);
		companies = (Button) findViewById(R.id.companies);
		headquarters = (Button) findViewById(R.id.headquarters);
		industry = (Button) findViewById(R.id.industry);
		employeeSize = (Button) findViewById(R.id.employeeSize);
		revenueSize = (Button) findViewById(R.id.revenueSize);
		ownership = (Button) findViewById(R.id.ownership);
		milestone = (Button) findViewById(R.id.milestone);
		rank = (Button) findViewById(R.id.rank);
		fiscalYearEnd = (Button) findViewById(R.id.fiscalYearEnd);
	}

	@Override
	protected void initData() {
		super.initData();
		if (null != Constant.MFILTERS) {
			Constant.MFILTERS.getHeadquarters().clear();
		}
		Constant.REVERSE = false;
		Constant.COMPANY_SEARCH_KEYWORDS = "";
		CommonUtil.resetFilters();
		CommonUtil.initBuzSortBy();
	}
	
	@Override
	protected void setOnClickListener() {
		super.setOnClickListener();
		newsTriggers.setOnClickListener(this);
		companies.setOnClickListener(this);
		headquarters.setOnClickListener(this);
		industry.setOnClickListener(this);
		employeeSize.setOnClickListener(this);
		revenueSize.setOnClickListener(this);
		ownership.setOnClickListener(this);
		milestone.setOnClickListener(this);
		rank.setOnClickListener(this);
		fiscalYearEnd.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		if (v == leftImageBtn) {
			finish();
		} else if (v == rightBtn) {
			startActivitySimple(SearchCompanyActivity.class);
		} else if (v == newsTriggers) {
			Intent intent = new Intent();
			intent.setClass(mContext, NewsTriggersActivity.class);
			startActivity(intent);
		} else if (v == companies) {
			startActivitySimple(CompaniesTypeFromBuzAcivity.class);
		} else if (v == headquarters) {
			startActivitySimple(HeadquartersActivity.class);
		} else if (v == industry) {
			startActivitySimple(IndustryActivity.class);
		} else if (v == employeeSize) {
			startActivitySimple(EmployeeSizeActivity.class);
		} else if (v == revenueSize) {
			startActivitySimple(RevenueSizeActivity.class);
		} else if (v == ownership) {
			startActivitySimple(OwnershipActivity.class);
		} else if (v == milestone) {
			startActivitySimple(MilestoneActivity.class);
		} else if (v == rank) {
			startActivitySimple(RankActivity.class);
		} else if (v == fiscalYearEnd) {
			startActivitySimple(FiscalYearEndActivity.class);
		}
	}

}
