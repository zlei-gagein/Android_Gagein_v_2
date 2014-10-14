package com.gagein.ui.settings;

import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.gagein.R;
import com.gagein.component.dialog.CommonDialog;
import com.gagein.component.dialog.RateDialog;
import com.gagein.http.APIHttp;
import com.gagein.ui.main.BaseActivity;
import com.gagein.ui.main.LoginActivity;
import com.gagein.util.CommonUtil;

public class SettingsActivity extends BaseActivity {
	
	private Button switchAccount;
	private Button myAccountBtn;
	private Button newsFilterBtn;
	private Button privacyPolicyBtn;
	private Button termsBtn;
	private Button tutorialBtn;
	private Button loginOutBtn;
	private TextView versionCode;
	private TextView copyright;
	private Boolean tutorial = false;
	private Button rateBtn;
	private Button shareBtn;
	private Button feedbackBtn;
//	private Button upgradeBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		
		doInit();
	}
	
	@Override
	protected void initView() {
		super.initView();
		
		setTitle(R.string.u_settings);
		
		switchAccount = (Button) findViewById(R.id.switchAccount);
		myAccountBtn = (Button) findViewById(R.id.myAccountBtn);
		newsFilterBtn = (Button) findViewById(R.id.newsFilterBtn);
		privacyPolicyBtn = (Button) findViewById(R.id.privacyPolicyBtn);
//		upgradeBtn = (Button) findViewById(R.id.upgradeBtn);
		termsBtn = (Button) findViewById(R.id.termsBtn);
		tutorialBtn = (Button) findViewById(R.id.tutorialBtn);
		loginOutBtn = (Button) findViewById(R.id.loginOutBtn);
		rateBtn = (Button) findViewById(R.id.rateBtn);
		shareBtn = (Button) findViewById(R.id.shareBtn);
		feedbackBtn = (Button) findViewById(R.id.feedbackBtn);
		versionCode = (TextView) findViewById(R.id.versionCode);
		copyright = (TextView) findViewById(R.id.copyright);
	}
	
	@Override
	protected void initData() {
		super.initData();
		
//		Calendar calendar = Calendar.getInstance();
//		copyright.setText(String.format(stringFromResID(R.string.copyright), calendar.get(Calendar.YEAR) + ""));
		copyright.setText(String.format(stringFromResID(R.string.copyright), "2014"));
		versionCode.setText(String.format(stringFromResID(R.string.version_code), CommonUtil.getVersion(mContext)));
	}

	@Override
	protected void setOnClickListener() {
		super.setOnClickListener();
		switchAccount.setOnClickListener(this);
		myAccountBtn.setOnClickListener(this);
		newsFilterBtn.setOnClickListener(this);
		privacyPolicyBtn.setOnClickListener(this);
		termsBtn.setOnClickListener(this);
		tutorialBtn.setOnClickListener(this);
		loginOutBtn.setOnClickListener(this);
		rateBtn.setOnClickListener(this);
//		upgradeBtn.setOnClickListener(this);
		shareBtn.setOnClickListener(this);
		feedbackBtn.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		
		if (v == switchAccount) {
			
			startActivitySimple(SwitchPlanActivity.class);
			
		} else if (v == myAccountBtn) {
			
			startActivitySimple(MyAccountActivity.class);
			
		} else if (v == newsFilterBtn) {
			
			startActivitySimple(SettingsNewsFilterActivity.class);
			
		} else if (v == privacyPolicyBtn) {
			
			startWebActivity(APIHttp.serverURL + "/html/privacy_v2.0.html");
			
		} else if (v == termsBtn) {
			
			startWebActivity(APIHttp.serverURL + "/html/tos_v2.0.html");
			
		} else if (v == tutorialBtn) {
			
			tutorial = !tutorial;
			tutorialBtn.setBackgroundResource(tutorial ? R.drawable.tutorial_on : R.drawable.tutorial_off);
			
		} else if (v == loginOutBtn) {
			
			final CommonDialog dialog = new CommonDialog(mContext);
			dialog.setCancelable(false);
			Window window = dialog.getWindow();
			window.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			window.setGravity(Gravity.BOTTOM);
			window.setWindowAnimations(R.style.dialog_animation);
			dialog.showDialog("Log Out", new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					CommonUtil.clearLoginInfo(mContext);
					dialog.dismissDialog();
					startActivitySimple(LoginActivity.class);
					finish();
				}
			}, new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					dialog.dismissDialog();
				}
			});
		} else if (v == rateBtn) {
			RateDialog dialog = new RateDialog(mContext);
			dialog.showDialog();
//		} else if (v == upgradeBtn) {
//			UpgradeDialog dialog = new UpgradeDialog(mContext);
//			dialog.showDialog();
		} else if (v == shareBtn) {
			startActivitySimple(ShareActivity.class);
		} else if (v == feedbackBtn) {
			startActivitySimple(FeedbackActivity.class);
		}
	}
	
	@Override
	public void onBackPressed() {
		if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        showShortToast(R.string.press_again_exit);
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
             doubleBackToExitPressedOnce=false;   

            }
        }, 2000);
	}

}
