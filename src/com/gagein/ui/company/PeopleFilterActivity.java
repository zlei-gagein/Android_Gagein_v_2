package com.gagein.ui.company;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.gagein.R;
import com.gagein.ui.main.BaseActivity;
import com.gagein.ui.newsfilter.FilterFunctionalRoleActivity;
import com.gagein.ui.newsfilter.FilterJobLevelActivity;
import com.gagein.ui.newsfilter.FilterLinkedProfileActivity;
import com.gagein.util.Constant;

public class PeopleFilterActivity extends BaseActivity {
	
	private RelativeLayout jobLevel;
	private RelativeLayout functionalRole;
	private RelativeLayout linkedProfile;
	private Button jobLevelBtn;
	private Button functionalRoleBtn;
	private Button linkedProfileBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_peoplefilter);
		
		doInit();
	}
	
	@Override
	protected void initView() {
		super.initView();
		setTitle(R.string.filters);
		setRightButton(R.string.done);
		
		jobLevel = (RelativeLayout) findViewById(R.id.jobLevel);
		functionalRole = (RelativeLayout) findViewById(R.id.functionalRole);
		linkedProfile = (RelativeLayout) findViewById(R.id.linkedProfile);
		jobLevelBtn = (Button) findViewById(R.id.jobLevelBtn);
		functionalRoleBtn = (Button) findViewById(R.id.functionalRoleBtn);
		linkedProfileBtn = (Button) findViewById(R.id.linkedProfileBtn);
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		initData();
	}
	
	@Override
	protected void initData() {
		super.initData();
		
		setJobLevel();
		setFunctionalRole();
		setLinkedProfile();
	}
	
	private void setJobLevel() {
		if (Constant.JOB_LEVEL_ID == 0) {
			jobLevelBtn.setText(mContext.getResources().getString(R.string.all));
		} else {
			jobLevelBtn.setText(Constant.JOB_LEVEL_NAME);
		}
	}
	
	private void setFunctionalRole() {
		if (Constant.FUNCTIONAL_ROLE_ID == 0) {
			functionalRoleBtn.setText(mContext.getResources().getString(R.string.all));
		} else {
			functionalRoleBtn.setText(Constant.FUNCTIONAL_ROLE_NAME);
		}
	}
	
	private void setLinkedProfile() {
		if (Constant.LINKED_PROFILE_ID == 0) {
			linkedProfileBtn.setText(mContext.getResources().getString(R.string.all));
		} else {
			linkedProfileBtn.setText(Constant.LINKED_PROFILE_NAME);
		}
	}
	
	@Override
	protected void setOnClickListener() {
		super.setOnClickListener();
		jobLevel.setOnClickListener(this);
		functionalRole.setOnClickListener(this);
		linkedProfile.setOnClickListener(this);
		jobLevelBtn.setOnClickListener(this);
		functionalRoleBtn.setOnClickListener(this);
		linkedProfileBtn.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		if (v == rightBtn) {
			Intent intent = new Intent(PeopleFilterActivity.this, CompanyActivity.class);
			setResult(RESULT_OK, intent);
			finish();
		} else if (v == jobLevel || v == jobLevelBtn) {
			startActivitySimple(FilterJobLevelActivity.class);
		} else if (v == functionalRole || v == functionalRoleBtn) {
			startActivitySimple(FilterFunctionalRoleActivity.class);
		} else if (v == linkedProfile || v == linkedProfileBtn) {
			startActivitySimple(FilterLinkedProfileActivity.class);
		}
	}
	
}
