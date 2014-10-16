package com.gagein.ui.tablet.settins;

import java.util.ArrayList;
import java.util.List;

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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gagein.R;
import com.gagein.component.dialog.CommonDialog;
import com.gagein.component.dialog.RateDialog;
import com.gagein.http.APIHttp;
import com.gagein.model.PlanInfo;
import com.gagein.ui.BaseFragment;
import com.gagein.ui.main.LoginActivity;
import com.gagein.util.CommonUtil;
import com.gagein.util.Constant;

public class SettingsFragment extends BaseFragment implements OnClickListener{
	
	private Button switchAccount;
	private Button myAccountBtn;
	private Button newsFilterBtn;
	private Button privacyPolicyBtn;
	private Button termsBtn;
	private Button tutorialBtn;
	private Button loginOutBtn;
//	private TextView version;
	private Boolean tutorial = false;
	private Context mContext;
	private TextView versionCode;
	private TextView copyright;
	private Button rateBtn;
	private Button shareBtn;
	private Button feedbackBtn;
	private LinearLayout switchPlanLayout;
	private List<PlanInfo> planInfos = new ArrayList<PlanInfo>();
	private OnSwitchAccountListener switchAccountListener;
	private onMyAccountSelectedListener myAccountSelectedListener;
	private onNewsFilterSelectedListener newsFilterSelectedListener;
	private onPrivacyListener privacySelectedListener;
	private onTermsListener termsSelectedListener;
	private onShareListener shareListener;
	private onFeedbackListener feedbackListener;
	
	public interface OnSwitchAccountListener {
		public void onSwitchAccountListener();
	}
	public interface onFeedbackListener {
		public void onFeedbackListener();
	}
	
	public interface onShareListener {
		public void onShareListener();
	}
	
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
			switchAccountListener = (OnSwitchAccountListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + "must implement switchAccountListener");
		}
		try {
			feedbackListener = (onFeedbackListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + "must implement onFeedbackListener");
		}
		
		try {
			shareListener = (onShareListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + "must implement onShareListener");
		}
		
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
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.activity_settings, container, false);
		mContext = getActivity();
		mApiHttp = new APIHttp(mContext);
		
		doInit();
		return view;
	}
	
	@Override
	protected void initView() {
		super.initView();
		
		setTitle(R.string.u_settings);
		
		switchAccount = (Button) view.findViewById(R.id.switchAccount);
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
		switchPlanLayout = (LinearLayout) view.findViewById(R.id.switchPlanLayout);
	}
	
	@Override
	protected void initData() {
		super.initData();
//		version.setText(CommonUtil.getVersion(getActivity()));
		
		copyright.setText("© 2014 Gagein, Inc.");
		versionCode.setText("Version " + CommonUtil.getVersion(mContext));
		
		planInfos = CommonUtil.readPlanInfos();
		if (planInfos.size() > 1) switchPlanLayout.setVisibility(View.VISIBLE);
	}

	@Override
	protected void setOnClickListener() {
		switchAccount.setOnClickListener(this);
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
		} else if (v == rateBtn) {
			RateDialog dialog = new RateDialog(mContext);
			dialog.showDialog();
		} else {
			resetAllButton();
			if (v == switchAccount) {
				setSelectedButton(switchAccount);
				switchAccountListener.onSwitchAccountListener();
			} else if (v == myAccountBtn) {
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
			} else if (v == tutorialBtn) {
				tutorial = !tutorial;
				tutorialBtn.setBackgroundResource(tutorial ? R.drawable.tutorial_on : R.drawable.tutorial_off);
			} else if (v == shareBtn) {
				setSelectedButton(shareBtn);
				shareListener.onShareListener();
			} else if (v == feedbackBtn) {
				setSelectedButton(feedbackBtn);
				feedbackListener.onFeedbackListener();
			}
		}
	}
	
	private void resetAllButton() {
		
		switchAccount.setBackgroundColor(mContext.getResources().getColor(R.color.white));
		switchAccount.setTextColor(mContext.getResources().getColor(R.color.text_dark));
		
		myAccountBtn.setBackgroundColor(mContext.getResources().getColor(R.color.white));
		myAccountBtn.setTextColor(mContext.getResources().getColor(R.color.text_dark));
		
		newsFilterBtn.setBackgroundColor(mContext.getResources().getColor(R.color.white));
		newsFilterBtn.setTextColor(mContext.getResources().getColor(R.color.text_dark));
		
		shareBtn.setBackgroundColor(mContext.getResources().getColor(R.color.white));
		shareBtn.setTextColor(mContext.getResources().getColor(R.color.text_dark));
		
		feedbackBtn.setBackgroundColor(mContext.getResources().getColor(R.color.white));
		feedbackBtn.setTextColor(mContext.getResources().getColor(R.color.text_dark));
		
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
