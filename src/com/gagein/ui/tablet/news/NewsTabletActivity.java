package com.gagein.ui.tablet.news;

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
import com.gagein.ui.tablet.news.FilterNewsFragment.OnFilterNewsLeftBtnClickListener;
import com.gagein.ui.tablet.news.FilterNewsFragment.OnRefreshNewsFilterFromNewsListener;
import com.gagein.ui.tablet.news.FilterRelevanceFragment.OnFilterRelevanceLeftBtnClickListener;
import com.gagein.ui.tablet.news.FilterRelevanceFragment.RefreshNewsFilterFromRelevance;
import com.gagein.ui.tablet.news.NewsFilterFragment.CloseLeftLayoutListener;
import com.gagein.ui.tablet.news.NewsFilterFragment.NewsBtnClickListener;
import com.gagein.ui.tablet.news.NewsFilterFragment.OnNewsFilterLeftBtnClickListener;
import com.gagein.ui.tablet.news.NewsFilterFragment.RelevanceBtnClickListener;
import com.gagein.ui.tablet.news.NewsFragment.OnFilterClickListener;
import com.gagein.util.CommonUtil;
import com.gagein.util.Constant;

public class NewsTabletActivity extends BaseFragmentActivity implements OnFilterClickListener, OnNewsFilterLeftBtnClickListener, 
	NewsBtnClickListener, OnFilterNewsLeftBtnClickListener, RelevanceBtnClickListener, OnFilterRelevanceLeftBtnClickListener, 
	OnRefreshNewsFilterFromNewsListener, RefreshNewsFilterFromRelevance, CloseLeftLayoutListener{
	
	protected boolean doubleBackToExitPressedOnce = false;
	private NewsFilterFragment newsFilterFragment;
	private NewsFragment newsFragment;
	private FilterNewsFragment filterNewsFragment;
	private FilterRelevanceFragment filterRelevanceFragment;
	private FragmentTransaction transaction;
	private LinearLayout leftLayout;
	
	@Override
	protected List<String> observeNotifications() {
		
		return stringList(Constant.BROADCAST_REFRESH_NEWS, Constant.BROADCAST_REFRESH_COMPANIES, Constant.BROADCAST_LIKED_NEWS
				,Constant.BROADCAST_UNLIKE_NEWS, Constant.BROADCAST_ADD_NEW_COMPANIES, Constant.BROADCAST_ADDED_PENDING_COMPANY, 
				Constant.BROADCAST_FOLLOW_COMPANY, Constant.BROADCAST_UNFOLLOW_COMPANY, Constant.BROADCAST_REMOVE_PENDING_COMPANIES,
				Constant.BROADCAST_REMOVE_COMPANIES, Constant.BROADCAST_REMOVE_BOOKMARKS, Constant.BROADCAST_ADD_BOOKMARKS);
		
	}
	
	@Override
	public void handleNotifications(Context aContext, Intent intent) {
		super.handleNotifications(aContext, intent);
		
		if (null == newsFragment) return;
		
		String actionName = intent.getAction();
		if (actionName.equals(Constant.BROADCAST_REFRESH_NEWS)) {
			
			newsFragment.refreshNews(false);
			
		} else if (actionName.equals(Constant.BROADCAST_REFRESH_COMPANIES)) {
			
			newsFragment.refreshNews(false);
			
		} else if (actionName.equals(Constant.BROADCAST_LIKED_NEWS)) {
			
			long newsId = intent.getLongExtra(Constant.UPDATEID, 0);
			newsFragment.setLikeFromBroadcast(newsId, true);
			
		} else if (actionName.equals(Constant.BROADCAST_UNLIKE_NEWS)) {
			
			long newsId = intent.getLongExtra(Constant.UPDATEID, 0);
			newsFragment.setLikeFromBroadcast(newsId, false);
			
		} else if (actionName.equals(Constant.BROADCAST_UNLIKE_NEWS)) {
			
			newsFragment.refreshNews(false);
			
		} else if (actionName.equals(Constant.BROADCAST_ADD_NEW_COMPANIES)) {
			
			newsFragment.getPendingCompany();
			
		} else if (actionName.equals(Constant.BROADCAST_ADDED_PENDING_COMPANY)) {
			
			newsFragment.getPendingCompany();
			
		} else if (actionName.equals(Constant.BROADCAST_FOLLOW_COMPANY) || actionName.equals(Constant.BROADCAST_UNFOLLOW_COMPANY)) {
			
			newsFragment.refreshNews(false);
			
		} else if (actionName.equals(Constant.BROADCAST_REMOVE_PENDING_COMPANIES)) {
			
			newsFragment.getPendingCompany();
			
		} else if (actionName.equals(Constant.BROADCAST_REMOVE_COMPANIES)) {
			
			newsFragment.getPendingCompany();
			newsFragment.refreshNews(false);
			
		} else if (actionName.equals(Constant.BROADCAST_REMOVE_BOOKMARKS) || actionName.equals(Constant.BROADCAST_REMOVE_BOOKMARKS)) {
			
			newsFragment.refreshNews(false);
			
		}
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		
		setContentView(R.layout.activity_tablet_main);
		leftLayout = (LinearLayout) findViewById(R.id.leftLayout);
		
		transaction = getSupportFragmentManager().beginTransaction();
		newsFilterFragment = new NewsFilterFragment();
		newsFragment = new NewsFragment();
		
		transaction.add(R.id.leftLayout, newsFilterFragment);
		transaction.add(R.id.rightLayout, newsFragment);
		transaction.commit();
		
		setLeftLayoutVisible(View.GONE);
		
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Constant.SIGNUP = false;
	}

	//设置左边布局隐藏或显示
	private void setLeftLayoutVisible(int visible) {
		leftLayout.setVisibility(visible);
		if (null == newsFragment) {
			newsFragment = new NewsFragment();
		}
		newsFragment.setTopBtnVisible((View.VISIBLE == visible) ? View.GONE : View.VISIBLE);
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
		setLeftLayoutVisible(View.VISIBLE);
	}

	@Override
	public void onNewsFilterLeftbtnClickListener() {
		setLeftLayoutVisible(View.GONE);
	}

	@Override
	public void onNewsBtnListener() {
		transaction = getSupportFragmentManager().beginTransaction();
		if (null == filterNewsFragment) {
			filterNewsFragment = new FilterNewsFragment();
			transaction.add(R.id.leftLayout, filterNewsFragment);
		}
		transaction.show(filterNewsFragment);
		if (null != newsFilterFragment) {
			transaction.hide(newsFilterFragment);
		}
		if (null != filterRelevanceFragment) {
			transaction.hide(filterRelevanceFragment);
		}
		transaction.commit();
	}

	@Override
	public void onFilterNewsLeftbtnClickListener() {
		transaction = getSupportFragmentManager().beginTransaction();
		if (null == newsFilterFragment) {
			newsFilterFragment = new NewsFilterFragment();
			transaction.add(R.id.leftLayout, newsFilterFragment);
		}
		transaction.show(newsFilterFragment);
		if (null != filterNewsFragment) {
			transaction.hide(filterNewsFragment);
		}
		if (null != filterRelevanceFragment) {
			transaction.hide(filterRelevanceFragment);
		}
		transaction.commit();
	}

	@Override
	public void onRelevanceBtnListener() {
		transaction = getSupportFragmentManager().beginTransaction();
		if (null == filterRelevanceFragment) {
			filterRelevanceFragment = new FilterRelevanceFragment();
			transaction.add(R.id.leftLayout, filterRelevanceFragment);
		}
		transaction.show(filterRelevanceFragment);
		if (null != filterNewsFragment) {
			transaction.hide(filterNewsFragment);
		}
		if (null != newsFilterFragment) {
			transaction.hide(newsFilterFragment);
		}
		transaction.commit();
	}

	@Override
	public void onFilterRelevanceLeftBtnClickListener() {
		transaction = getSupportFragmentManager().beginTransaction();
		if (null == newsFilterFragment) {
			newsFilterFragment = new NewsFilterFragment();
			transaction.add(R.id.leftLayout, newsFilterFragment);
		}
		transaction.show(newsFilterFragment);
		if (null != filterNewsFragment) {
			transaction.hide(filterNewsFragment);
		}
		if (null != filterRelevanceFragment) {
			transaction.hide(filterRelevanceFragment);
		}
		transaction.commit();
	}

	@Override
	public void onRefreshNewsFilterFromNewsListener() {
		if (null == newsFilterFragment) {
			newsFilterFragment = new NewsFilterFragment();
		}
		refreshNews();
	}

	@Override
	public void refreshNewsFilterFromRelevance() {
		if (null == newsFilterFragment) {
			newsFilterFragment = new NewsFilterFragment();
		}
		refreshNews();
	}

	private void refreshNews() {
		newsFilterFragment.getRelevance();
		if (null == newsFragment) {
			newsFragment = new NewsFragment();
		}
		newsFragment.refreshNews(true);
	}
	
	@Override
	public void closeLeftLayoutListener() {
		setLeftLayoutVisible(View.GONE);
	}
}
