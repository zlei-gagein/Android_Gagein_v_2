package com.gagein.ui.search.people.filter;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.gagein.R;
import com.gagein.ui.main.BaseActivity;
import com.gagein.ui.search.SearchPersonActivity;
import com.gagein.ui.search.company.filter.EmployeeSizeActivity;
import com.gagein.ui.search.company.filter.FiscalYearEndActivity;
import com.gagein.ui.search.company.filter.HeadquartersActivity;
import com.gagein.ui.search.company.filter.IndustryActivity;
import com.gagein.ui.search.company.filter.MilestoneActivity;
import com.gagein.ui.search.company.filter.NewsTriggersActivity;
import com.gagein.ui.search.company.filter.OwnershipActivity;
import com.gagein.ui.search.company.filter.RankActivity;
import com.gagein.ui.search.company.filter.RevenueSizeActivity;
import com.gagein.util.CommonUtil;
import com.gagein.util.Constant;

public class PeopleFilterActivity extends BaseActivity {
	
	private Button jobTitle;
	private Button jobLevel;
	private Button location;
	private Button functionalRole;
	private Button newsTriggers;
	private Button companies;
	private Button headquarters;
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
		setContentView(R.layout.activity_search_people_filter);
		
		doInit();
	}
	
	@Override
	protected void initView() {
		super.initView();
		setTitle(R.string.people_filters);
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
		jobTitle = (Button) findViewById(R.id.jobTitle);
		jobLevel = (Button) findViewById(R.id.jobLevel);
		location = (Button) findViewById(R.id.location);
		functionalRole = (Button) findViewById(R.id.functionalRole);
	}
	
	@Override
	protected void initData() {
		super.initData();
		if (null != Constant.MFILTERS) {
			Constant.MFILTERS.getHeadquarters().clear();
			Constant.MFILTERS.getJobTitles().clear();
			Constant.MFILTERS.getLocations().clear();
		}
		Constant.REVERSE = false;
		CommonUtil.resetFilters();
		CommonUtil.initConSortBy();
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
		jobTitle.setOnClickListener(this);
		jobLevel.setOnClickListener(this);
		location.setOnClickListener(this);
		functionalRole.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		if (v == leftImageBtn) {
			finish();
		} else if (v == rightBtn) {
			startActivitySimple(SearchPersonActivity.class);
		} else if (v == newsTriggers) {
			startActivitySimple(NewsTriggersActivity.class);
		} else if (v == companies) {
			startActivitySimple(PeopleFilterCompaniesActivity.class);
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
		} else if (v == jobTitle) {
			startActivitySimple(JobTitleActivity.class);
		} else if (v == jobLevel) {
			startActivitySimple(JobLevelActivity.class);
		} else if (v == location) {
			startActivitySimple(LocationActivity.class);
		} else if (v == functionalRole) {
			startActivitySimple(FunctionalRoleActivity.class);
		}
	} 

}
