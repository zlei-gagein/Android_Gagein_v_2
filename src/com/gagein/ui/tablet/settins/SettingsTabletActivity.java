package com.gagein.ui.tablet.settins;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.gagein.R;
import com.gagein.ui.tablet.settins.CategoryFragment.OnBackToFeedbackListener;
import com.gagein.ui.tablet.settins.FeedbackFragment.OnCategoryListener;
import com.gagein.ui.tablet.settins.SettingsFragment.onFeedbackListener;
import com.gagein.ui.tablet.settins.SettingsFragment.onMyAccountSelectedListener;
import com.gagein.ui.tablet.settins.SettingsFragment.onNewsFilterSelectedListener;
import com.gagein.ui.tablet.settins.SettingsFragment.onPrivacyListener;
import com.gagein.ui.tablet.settins.SettingsFragment.onShareListener;
import com.gagein.ui.tablet.settins.SettingsFragment.onTermsListener;
import com.gagein.util.CommonUtil;

public class SettingsTabletActivity extends FragmentActivity implements onNewsFilterSelectedListener, onMyAccountSelectedListener, 
	onPrivacyListener, onTermsListener, onShareListener, onFeedbackListener, OnCategoryListener, OnBackToFeedbackListener{
	
	protected boolean doubleBackToExitPressedOnce = false;
	private FragmentTransaction transaction;
	private SettingsFragment settingsFragment;
	private MyAccountFragment myAccountFragment;
	private NewsFilterFragment newsFilterFragment;
	private PrivacyFragment privacyFragment;
	private TermsFragment termsFragment;
	private ShareFragment shareFragment;
	private FeedbackFragment feedbackFragment;
	private CategoryFragment categoryFragment;
	

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_tablet_main);
		
		transaction = getSupportFragmentManager().beginTransaction();
		
		settingsFragment = new SettingsFragment();
		myAccountFragment = new MyAccountFragment();
		
		transaction.add(R.id.leftLayout, settingsFragment);
		transaction.add(R.id.rightLayout, myAccountFragment);
		transaction.commit();
	}
	
	@Override
	public void onMyAccountClickListener() {
		transaction = getSupportFragmentManager().beginTransaction();
		if (null == myAccountFragment) {
			myAccountFragment = new MyAccountFragment();
			transaction.add(R.id.rightLayout, myAccountFragment);
		} else {
			myAccountFragment.getMyOverView();
		}
		transaction.show(myAccountFragment);
		if (null != newsFilterFragment) {
			transaction.hide(newsFilterFragment);
		}
		if (null != shareFragment) {
			transaction.hide(shareFragment);
		}
		if (null != feedbackFragment) {
			transaction.hide(feedbackFragment);
		}
		if (null != categoryFragment) {
			transaction.hide(categoryFragment);
		}
		if (null != termsFragment) {
			transaction.hide(termsFragment);
		}
		if (null != privacyFragment) {
			transaction.hide(privacyFragment);
		}
		transaction.commit();
	}
	
	@Override
	public void onNewsFilterClickListener() {
		transaction = getSupportFragmentManager().beginTransaction();
		if (null == newsFilterFragment) {
			newsFilterFragment = new NewsFilterFragment();
			transaction.add(R.id.rightLayout, newsFilterFragment);
		}
		transaction.show(newsFilterFragment);
		if (null != myAccountFragment) {
			transaction.hide(myAccountFragment);
		}
		if (null != shareFragment) {
			transaction.hide(shareFragment);
		}
		if (null != feedbackFragment) {
			transaction.hide(feedbackFragment);
		}
		if (null != categoryFragment) {
			transaction.hide(categoryFragment);
		}
		if (null != termsFragment) {
			transaction.hide(termsFragment);
		}
		if (null != privacyFragment) {
			transaction.hide(privacyFragment);
		}
		transaction.commit();
	}

	@Override
	public void onPrivacyClickListener() {
		transaction = getSupportFragmentManager().beginTransaction();
		if (null == privacyFragment) {
			privacyFragment = new PrivacyFragment();
			transaction.add(R.id.rightLayout, privacyFragment);
		}
		transaction.show(privacyFragment);
		if (null != myAccountFragment) {
			transaction.hide(myAccountFragment);
		}
		if (null != newsFilterFragment) {
			transaction.hide(newsFilterFragment);
		}
		if (null != shareFragment) {
			transaction.hide(shareFragment);
		}
		if (null != feedbackFragment) {
			transaction.hide(feedbackFragment);
		}
		if (null != categoryFragment) {
			transaction.hide(categoryFragment);
		}
		if (null != termsFragment) {
			transaction.hide(termsFragment);
		}
		transaction.commit();
	}

	@Override
	public void onTermsClickListener() {
		
		transaction = getSupportFragmentManager().beginTransaction();
		
		if (null == termsFragment) {
			termsFragment = new TermsFragment();
			transaction.add(R.id.rightLayout, termsFragment);
		}
		
		transaction.show(termsFragment);
		
		if (null != myAccountFragment) {
			transaction.hide(myAccountFragment);
		}
		if (null != newsFilterFragment) {
			transaction.hide(newsFilterFragment);
		}
		if (null != shareFragment) {
			transaction.hide(shareFragment);
		}
		if (null != feedbackFragment) {
			transaction.hide(feedbackFragment);
		}
		if (null != categoryFragment) {
			transaction.hide(categoryFragment);
		}
		if (null != privacyFragment) {
			transaction.hide(privacyFragment);
		}
		transaction.commit();
		
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
	public void onShareListener() {
		
		transaction = getSupportFragmentManager().beginTransaction();
		
		if (null == shareFragment) {
			shareFragment = new ShareFragment();
			transaction.add(R.id.rightLayout, shareFragment);
		}
		
		transaction.show(shareFragment);
		
		if (null != myAccountFragment) {
			transaction.hide(myAccountFragment);
		}
		if (null != newsFilterFragment) {
			transaction.hide(newsFilterFragment);
		}
		if (null != feedbackFragment) {
			transaction.hide(feedbackFragment);
		}
		if (null != categoryFragment) {
			transaction.hide(categoryFragment);
		}
		if (null != termsFragment) {
			transaction.hide(termsFragment);
		}
		if (null != privacyFragment) {
			transaction.hide(privacyFragment);
		}
		transaction.commit();
		
	}

	@Override
	public void onFeedbackListener() {
		
		transaction = getSupportFragmentManager().beginTransaction();
		
		if (null == feedbackFragment) {
			feedbackFragment = new FeedbackFragment();
			transaction.add(R.id.rightLayout, feedbackFragment);
		}
		
		transaction.show(feedbackFragment);
		
		if (null != myAccountFragment) {
			transaction.hide(myAccountFragment);
		}
		if (null != newsFilterFragment) {
			transaction.hide(newsFilterFragment);
		}
		
		if (null != shareFragment) {
			transaction.hide(shareFragment);
		}
		if (null != categoryFragment) {
			transaction.hide(categoryFragment);
		}
	
		if (null != termsFragment) {
			transaction.hide(termsFragment);
		}
		if (null != privacyFragment) {
			transaction.hide(privacyFragment);
		}
		transaction.commit();
		
	}

	@Override
	public void onCategoryListener() {
		transaction = getSupportFragmentManager().beginTransaction();
		
		if (null == categoryFragment) {
			categoryFragment = new CategoryFragment();
			transaction.add(R.id.rightLayout, categoryFragment);
		}
		
		transaction.show(categoryFragment);
		
		if (null != myAccountFragment) {
			transaction.hide(myAccountFragment);
		}
		if (null != newsFilterFragment) {
			transaction.hide(newsFilterFragment);
		}
		
		if (null != shareFragment) {
			transaction.hide(shareFragment);
		}
		if (null != feedbackFragment) {
			transaction.hide(feedbackFragment);
		}
	
		if (null != termsFragment) {
			transaction.hide(termsFragment);
		}
		if (null != privacyFragment) {
			transaction.hide(privacyFragment);
		}
		transaction.commit();
	}

	@Override
	public void onBackToFeedbackListener() {
		transaction = getSupportFragmentManager().beginTransaction();
		
		if (null == feedbackFragment) {
			feedbackFragment = new FeedbackFragment();
			transaction.add(R.id.rightLayout, feedbackFragment);
		}
		
		feedbackFragment.setCategoryButton();
		transaction.show(feedbackFragment);
		
		if (null != categoryFragment) {
			transaction.hide(categoryFragment);
		}
		transaction.commit();
	}
	
	
}
