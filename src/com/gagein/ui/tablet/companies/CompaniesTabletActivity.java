package com.gagein.ui.tablet.companies;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.LinearLayout;

import com.gagein.R;
import com.gagein.model.Company;
import com.gagein.ui.main.BaseFragmentActivity;
import com.gagein.ui.tablet.companies.CompaniesFragment.OnFilterClickListener;
import com.gagein.ui.tablet.companies.FilterFragment.OnFilterCancle;
import com.gagein.ui.tablet.companies.FilterFragment.OnFilterDone;
import com.gagein.util.CommonUtil;
import com.gagein.util.Constant;

public class CompaniesTabletActivity extends BaseFragmentActivity implements OnFilterClickListener, 
	OnFilterCancle, OnFilterDone{
	
	protected boolean doubleBackToExitPressedOnce = false;
	private LinearLayout leftLayout;
	private FragmentTransaction transaction;
	private CompaniesFragment companiesFragment;
	private FilterFragment filterFragment;
	
	private BroadcastReceiver authReceiver;
	
	@Override
	public void handleNotifications(Context aContext, Intent intent) {
		super.handleNotifications(aContext, intent);
		
		String actionName = intent.getAction();
		if (null != companiesFragment) {
			
			if (actionName.equals(Constant.BROADCAST_REFRESH_COMPANIES) || actionName.equals(Constant.BROADCAST_ADDED_PENDING_COMPANY)) {
				
					companiesFragment.edit = false;
					companiesFragment.setEditStatus();
					companiesFragment.setBottomLayoutStatus();
					companiesFragment.nextPage = "";
					companiesFragment.getCompaniesOfGroup(false, false);
					
			} else if (actionName.equals(Constant.BROADCAST_ADD_COMPANIES_FROM_FOLLOW_COMPANIES)) {
				
				companiesFragment.setNoFollowedCompaniesLayoutGone();
				companiesFragment.edit = false;
				companiesFragment.nextPage = "";
				companiesFragment.setEditStatus();
				companiesFragment.setBottomLayoutStatus();
				companiesFragment.getCompaniesOfGroup(false, false);
				
			} else if (actionName.equals(Constant.BROADCAST_FOLLOW_COMPANY) || actionName.equals(Constant.BROADCAST_UNFOLLOW_COMPANY)) {
				
				companiesFragment.nextPage = "";
				companiesFragment.getCompaniesOfGroup(false, false);
				
			} else if (actionName.equals(Constant.BROADCAST_ADD_COMPANIES)) {
				
				companiesFragment.setAllCompaniesUnSelect();
				
				companiesFragment.selectAllBtn.setTag(R.id.tag_select, false);
				companiesFragment.selectAllBtn.setImageResource(R.drawable.button_unselect);
				if (null != companiesFragment.noSectionIndexAdapter) companiesFragment.noSectionIndexAdapter.notifyDataSetChanged();
				
			}
		}
	}
	
	@Override
	protected List<String> observeNotifications() {
		return stringList(Constant.BROADCAST_REFRESH_COMPANIES, Constant.BROADCAST_ADDED_PENDING_COMPANY,
				Constant.BROADCAST_ADD_COMPANIES_FROM_FOLLOW_COMPANIES, Constant.BROADCAST_FOLLOW_COMPANY,
				Constant.BROADCAST_UNFOLLOW_COMPANY, Constant.BROADCAST_ADD_COMPANIES);
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_tablet_main);
		leftLayout = (LinearLayout) findViewById(R.id.leftLayout);
		
		transaction = getSupportFragmentManager().beginTransaction();
		companiesFragment = new CompaniesFragment();
		
		transaction.add(R.id.rightLayout, companiesFragment);
		transaction.commit();
		
		setLeftLayoutVisible(View.GONE);
		
		authReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {//TODO
//				companiesFragment.refreshCompanies();
			}
		};
		registerReceiver(authReceiver, new IntentFilter(Constant.BROADCAST_REFRESH_COMPANIES));
	}
	
	//设置左边布局隐藏或显示
	private void setLeftLayoutVisible(int visible) {
		leftLayout.setVisibility(visible);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		List<Company> currentPendingCompanies = new ArrayList<Company>();
		for (int i = 0; i < Constant.CURRENT_PENDING_COMPANY.size(); i ++) {
			Company company = Constant.CURRENT_PENDING_COMPANY.get(i);
			if (company.followed) currentPendingCompanies.add(company);
		}
		Constant.CURRENT_PENDING_COMPANY = currentPendingCompanies;
		
//		if (null != companiesFragment) companiesFragment.setPendingNum();
	}
	
	@Override
	public void onBackPressed() {
		if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        CommonUtil.showShortToast(this.getResources().getString(R.string.press_again_exit));
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
            	doubleBackToExitPressedOnce=false;   
            }
        }, 2000);
	}

	@Override
	public void onFilterClickListener() {
		if (null == filterFragment) {
			filterFragment = new FilterFragment();
			transaction = getSupportFragmentManager().beginTransaction();
			transaction.add(R.id.leftLayout, filterFragment);
			transaction.commit();
		}
		
		setLeftLayoutVisible(View.VISIBLE);
	}

	@Override
	public void onFilterCancle() {
		setLeftLayoutVisible(View.GONE);
	}

	@Override
	public void onFilterDone() {
		setLeftLayoutVisible(View.GONE);
		if (null == companiesFragment) {
			companiesFragment = new CompaniesFragment();
		}
		
		companiesFragment.nextPage = ""; 
		companiesFragment.getCompaniesOfGroup(true, false);
		setLeftLayoutVisible(View.GONE);
	}
}
