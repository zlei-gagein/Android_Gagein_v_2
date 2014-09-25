package com.gagein.ui.tablet.scores;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.LinearLayout;

import com.gagein.R;
import com.gagein.ui.main.BaseFragmentActivity;
import com.gagein.ui.tablet.scores.GroupsForScoresFragment.OnChangeGroupsFilterListener;
import com.gagein.ui.tablet.scores.GroupsForScoresFragment.OnFinishGroupsListener;
import com.gagein.ui.tablet.scores.ScoresFragment.OnGroupFilterClickListener;
import com.gagein.util.CommonUtil;
import com.gagein.util.Constant;

public class ScoresTabletActivity extends BaseFragmentActivity implements OnGroupFilterClickListener, OnFinishGroupsListener, 
	OnChangeGroupsFilterListener{
	
	protected boolean doubleBackToExitPressedOnce = false;
	private ScoresFragment scoresFragment;
	private GroupsForScoresFragment groupsFilterFragment;
	private FragmentTransaction transaction;
	private LinearLayout leftLayout;
	
	@Override
	protected List<String> observeNotifications() {
		
		return stringList(Constant.BROADCAST_UNFOLLOW_COMPANY, Constant.BROADCAST_FOLLOW_COMPANY, Constant.BROADCAST_REMOVE_COMPANIES, 
				Constant.BROADCAST_ADD_COMPANIES, Constant.BROADCAST_ADD_NEW_COMPANIES, Constant.BROADCAST_ADDED_PENDING_COMPANY,
				Constant.BROADCAST_REMOVE_PENDING_COMPANIES, Constant.BROADCAST_ADD_COMPANIES_FROM_FOLLOW_COMPANIES);
		
	}
	
	@Override
	public void handleNotifications(Context aContext, Intent intent) {
		super.handleNotifications(aContext, intent);
		
		if (null == scoresFragment) return;
		
		String actionName = intent.getAction();
		if (actionName.equals(Constant.BROADCAST_UNFOLLOW_COMPANY) || actionName.equals(Constant.BROADCAST_FOLLOW_COMPANY) ||
				actionName.equals(Constant.BROADCAST_REMOVE_COMPANIES) || actionName.equals(Constant.BROADCAST_ADD_COMPANIES)
				|| actionName.equals(Constant.BROADCAST_ADD_NEW_COMPANIES) || actionName.equals(Constant.BROADCAST_ADDED_PENDING_COMPANY)
				|| actionName.equals(Constant.BROADCAST_REMOVE_PENDING_COMPANIES) || actionName.equals(Constant.BROADCAST_ADD_COMPANIES_FROM_FOLLOW_COMPANIES)) {
			
			groupsFilterFragment.getAllCompanyGroups(false);
			scoresFragment.getCompaniesOfGroup(false, false, true);
			
		}
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		
		setContentView(R.layout.activity_tablet_main);
		leftLayout = (LinearLayout) findViewById(R.id.leftLayout);
		
		transaction = getSupportFragmentManager().beginTransaction();
		groupsFilterFragment = new GroupsForScoresFragment();
		scoresFragment = new ScoresFragment();
		
		transaction.add(R.id.leftLayout, groupsFilterFragment);
		transaction.add(R.id.rightLayout, scoresFragment);
		transaction.commit();
		
		setLeftLayoutVisible(View.GONE);
		
	}
	
	private void setLeftLayoutVisible(int visible) {
		leftLayout.setVisibility(visible);
	}
	
	@Override
	public void onBackPressed() {
		
		if (leftLayout.getVisibility() == View.VISIBLE) {
			setLeftLayoutVisible(View.GONE);
			return;
		}
		
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
	public void onGroupFilterClickListener() {
		setLeftLayoutVisible(leftLayout.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		Constant.scoresSorts.clear();
		Constant.GroupsForScores.clear();
		
	}

	@Override
	public void onChangeGroupsFilterListener() {
		scoresFragment.getCompaniesOfGroup(true, false, true);
	}

	@Override
	public void onFinishGroupsListener() {
		setLeftLayoutVisible(View.GONE);
	}
}
