package com.gagein.ui.tablet.settins;

import java.util.Calendar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.gagein.R;
import com.gagein.component.dialog.CommonDialog;
import com.gagein.component.dialog.RateDialog;
import com.gagein.http.APIHttp;
import com.gagein.ui.BaseFragment;
import com.gagein.ui.main.LoginActivity;
import com.gagein.ui.settings.FeedbackActivity;
import com.gagein.ui.settings.ShareActivity;
import com.gagein.util.CommonUtil;
import com.gagein.util.Constant;

public class SettingsFragment extends BaseFragment implements OnClickListener{
	
	private Button myAccountBtn;
	private Button newsFilterBtn;
	private Button privacyPolicyBtn;
	private Button termsBtn;
	private Button tutorialBtn;
	private Button loginOutBtn;
//	private TextView version;
	private Boolean tutorial = false;
	private View view;
	private Context mContext;
	private onNewsFilterSelectedListener newsFilterSelectedListener;
	private onMyAccountSelectedListener myAccountSelectedListener;
	private onPrivacyListener privacySelectedListener;
	private onTermsListener termsSelectedListener;
	private TextView versionCode;
	private TextView copyright;
	private Button rateBtn;
	private Button shareBtn;
	private Button feedbackBtn;
	
	public interface onNewsFilterSelectedListener {
		public void onNewsFilterClickListener();
	}
	
	public interface onMyAccountSelectedListener {
		public void onMyAccountClickListener();
	}
	
	public interface onPrivacyListener {
		public void onPrivacyClickListener();
	}
	
	public interface onTermsListener {
		public void onTermsClickListener();
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			newsFilterSelectedListener = (onNewsFilterSelectedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + "must implement onNewsFilterSelectedListener");
		}
		try {
			myAccountSelectedListener = (onMyAccountSelectedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + "must implement onMyAccountSelectedListener");
		}
		try {
			privacySelectedListener = (onPrivacyListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + "must implement onWebPageListener");
		}
		try {
			termsSelectedListener = (onTermsListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + "must implement onTermsListener");
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.activity_settings, container, false);
		mContext = getActivity();
		
		doInit();
		return view;
	}
	
	@Override
	protected void initView() {
//		setTitle(R.string.u_settings);
		
		myAccountBtn = (Button) view.findViewById(R.id.myAccountBtn);
		newsFilterBtn = (Button) view.findViewById(R.id.newsFilterBtn);
		privacyPolicyBtn = (Button) view.findViewById(R.id.privacyPolicyBtn);
		termsBtn = (Button) view.findViewById(R.id.termsBtn);
		tutorialBtn = (Button) view.findViewById(R.id.tutorialBtn);
		loginOutBtn = (Button) view.findViewById(R.id.loginOutBtn);
//		version = (TextView) view.findViewById(R.id.version);
		versionCode = (TextView) view.findViewById(R.id.versionCode);
		copyright = (TextView) view.findViewById(R.id.copyright);
		rateBtn = (Button) view.findViewById(R.id.rateBtn);
		shareBtn = (Button) view.findViewById(R.id.shareBtn);
		feedbackBtn = (Button) view.findViewById(R.id.feedbackBtn);
	}
	
	@Override
	protected void initData() {
		super.initData();
//		version.setText(CommonUtil.getVersion(getActivity()));
		
		Calendar calendar = Calendar.getInstance();
		copyright.setText("© 2014 Gagein, Inc.");
		versionCode.setText("Version " + CommonUtil.getVersion(mContext));
	}

	@Override
	protected void setOnClickListener() {
		myAccountBtn.setOnClickListener(this);
		newsFilterBtn.setOnClickListener(this);
		privacyPolicyBtn.setOnClickListener(this);
		termsBtn.setOnClickListener(this);
		tutorialBtn.setOnClickListener(this);
		loginOutBtn.setOnClickListener(this);
		rateBtn.setOnClickListener(this);
		shareBtn.setOnClickListener(this);
		feedbackBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		 if (v == loginOutBtn) {
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
						Intent intent = new Intent();
						intent.setClass(getActivity(), LoginActivity.class);
						startActivity(intent);
						getActivity().finish();
					}
				}, new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						dialog.dismissDialog();
					}
				});
		} else {
			resetAllButton();
			if (v == myAccountBtn) {
				
				setSelectedButton(myAccountBtn);
				
				myAccountSelectedListener.onMyAccountClickListener();
				
			} else if (v == newsFilterBtn) {
				
				setSelectedButton(newsFilterBtn);
				
				newsFilterSelectedListener.onNewsFilterClickListener();
				
			} else if (v == privacyPolicyBtn) {
				
				setSelectedButton(privacyPolicyBtn);
				Constant.URL = APIHttp.serverURL + "/html/privacy.html";
				privacySelectedListener.onPrivacyClickListener();
				
			} else if (v == termsBtn) {
				
				setSelectedButton(termsBtn);
				Constant.URL = APIHttp.serverURL + "/html/tos.html";
				termsSelectedListener.onTermsClickListener();
				
			} else if (v == tutorialBtn) {//TODO 暂时没有改设计
				
				tutorial = !tutorial;
				tutorialBtn.setBackgroundResource(tutorial ? R.drawable.tutorial_on : R.drawable.tutorial_off);
				
			} else if (v == rateBtn) {
				
				RateDialog dialog = new RateDialog(mContext);
				dialog.showDialog();
				
			} else if (v == shareBtn) {
				
				Intent intent = new Intent();
				intent.setClass(mContext, ShareActivity.class);
				mContext.startActivity(intent);
				
			} else if (v == feedbackBtn) {
				
				Intent intent = new Intent();
				intent.setClass(mContext, FeedbackActivity.class);
				mContext.startActivity(intent);
				
			}
		}
	}
	
	private void resetAllButton() {
		myAccountBtn.setBackgroundColor(mContext.getResources().getColor(R.color.white));
		myAccountBtn.setTextColor(mContext.getResources().getColor(R.color.text_dark));
		newsFilterBtn.setBackgroundColor(mContext.getResources().getColor(R.color.white));
		newsFilterBtn.setTextColor(mContext.getResources().getColor(R.color.text_dark));
		privacyPolicyBtn.setBackgroundColor(mContext.getResources().getColor(R.color.white));
		privacyPolicyBtn.setTextColor(mContext.getResources().getColor(R.color.text_dark));
		termsBtn.setBackgroundColor(mContext.getResources().getColor(R.color.white));
		termsBtn.setTextColor(mContext.getResources().getColor(R.color.text_dark));
		loginOutBtn.setBackgroundColor(mContext.getResources().getColor(R.color.white));
		loginOutBtn.setTextColor(mContext.getResources().getColor(R.color.text_dark));
	}
	
	private void setSelectedButton (Button button) {
		button.setBackgroundColor(mContext.getResources().getColor(R.color.yellow));
		button.setTextColor(mContext.getResources().getColor(R.color.white));
	}
}
