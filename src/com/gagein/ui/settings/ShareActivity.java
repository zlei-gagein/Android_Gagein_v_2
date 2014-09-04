package com.gagein.ui.settings;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gagein.R;
import com.gagein.ui.main.BaseActivity;
import com.gagein.util.CommonUtil;

public class ShareActivity extends BaseActivity {
	
	private ImageButton mail;
	private ImageButton linkedIn;
	private ImageButton twitter;
	private ImageButton facebook;
	private ImageButton googlePlus;
	private TextView signUpNum;
	private TextView resultFrom;
	private TextView creditNum;
	private LinearLayout signUpLayout;
	private LinearLayout creditLayout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share);
		
		doInit();
	}

	@Override
	protected void initView() {
		super.initView();
		setTitle(R.string.share);
		setLeftImageButton(R.drawable.back_arrow);
		
		mail = (ImageButton) findViewById(R.id.mail);
		linkedIn = (ImageButton) findViewById(R.id.linkedIn);
		twitter = (ImageButton) findViewById(R.id.twitter);
		facebook = (ImageButton) findViewById(R.id.facebook);
		googlePlus = (ImageButton) findViewById(R.id.googlePlus);
		signUpNum = (TextView) findViewById(R.id.signUpNum);
		resultFrom = (TextView) findViewById(R.id.resultFrom);
		creditNum = (TextView) findViewById(R.id.creditNum);
		signUpLayout = (LinearLayout) findViewById(R.id.signUpLayout);
		creditLayout = (LinearLayout) findViewById(R.id.creditLayout);
	}
	
	@Override
	protected void initData() {
		super.initData();
	}
	
	@Override
	protected void setOnClickListener() {
		super.setOnClickListener();
		mail.setOnClickListener(this);
		linkedIn.setOnClickListener(this);
		twitter.setOnClickListener(this);
		facebook.setOnClickListener(this);
		googlePlus.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		if (v == mail) {
			CommonUtil.sendEmail(CommonUtil.stringFromResID(R.string.share_email_subject, mContext), CommonUtil.stringFromResID(R.string.share_email_content, mContext), mContext);
		} else if (v == linkedIn) {
			startWebActivity(CommonUtil.stringFromResID(R.string.share_to_linkedin_url));
		} else if (v == twitter) {
			startWebActivity(CommonUtil.stringFromResID(R.string.share_to_twitter_url));
		} else if (v == facebook) {
			startWebActivity(CommonUtil.stringFromResID(R.string.share_to_facebook_url));
		} else if (v == googlePlus) {
			startWebActivity(CommonUtil.stringFromResID(R.string.share_to_googleplus_url));
		} else if (v == leftImageBtn) {
			finish();
		}
	}
}
