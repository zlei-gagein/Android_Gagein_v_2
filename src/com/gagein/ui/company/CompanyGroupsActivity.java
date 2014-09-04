package com.gagein.ui.company;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.gagein.R;
import com.gagein.ui.main.BaseActivity;

public class CompanyGroupsActivity extends BaseActivity {
	
	private LinearLayout unsupportPt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_companygroups);
		
		doInit();
	}
	
	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		super.initView();
		
		setTitle(R.string.company_groups);
		setLeftButton(R.string.cancel);
		setRightButton(R.string.done);
		
		unsupportPt = (LinearLayout) findViewById(R.id.unsupportPt);
		
	}
	
	@Override
	protected void initData() {
		// TODO Auto-generated method stub
		super.initData();
		
		//TODO
		unsupportPt.setVisibility(View.VISIBLE);
	}
	
	@Override
	protected void setOnClickListener() {
		// TODO Auto-generated method stub
		super.setOnClickListener();
		leftBtn.setOnClickListener(this);
		rightBtn.setOnClickListener(this);
		
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		if (v == leftBtn) {
			finish();
		} else if (v == rightBtn) {
			
		}
	}
}
