package com.gagein.ui.tablet.settins;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.gagein.R;
import com.gagein.ui.tablet.settins.SettingsFragment.onMyAccountSelectedListener;
import com.gagein.ui.tablet.settins.SettingsFragment.onNewsFilterSelectedListener;
import com.gagein.ui.tablet.settins.SettingsFragment.onPrivacyListener;
import com.gagein.ui.tablet.settins.SettingsFragment.onTermsListener;
import com.gagein.util.CommonUtil;

public class SettingsTabletActivity extends FragmentActivity implements onNewsFilterSelectedListener, onMyAccountSelectedListener, 
	onPrivacyListener, onTermsListener{
	
	protected boolean doubleBackToExitPressedOnce = false;
	private FragmentTransaction transaction;
	private SettingsFragment settingsFragment;
	private MyAccountFragment myAccountFragment;
	private NewsFilterFragment newsFilterFragment;
	private PrivacyFragment privacyFragment;
	private TermsFragment termsFragment;

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
		}
		transaction.show(myAccountFragment);
		if (null != newsFilterFragment) {
			transaction.hide(newsFilterFragment);
		}
		if (null != privacyFragment) {
			transaction.hide(privacyFragment);
		}
		if (null != termsFragment) {
			transaction.hide(termsFragment);
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
		if (null != privacyFragment) {
			transaction.hide(privacyFragment);
		}
		if (null != termsFragment) {
			transaction.hide(termsFragment);
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
	
	
}
