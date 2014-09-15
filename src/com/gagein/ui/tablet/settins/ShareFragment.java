package com.gagein.ui.tablet.settins;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gagein.R;
import com.gagein.ui.BaseFragment;
import com.gagein.util.CommonUtil;

public class ShareFragment extends BaseFragment{
	
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
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.activity_share, container, false);
		
		doInit();
		return view;
	}
	
	@Override
	protected void initView() {
		super.initView();
		
		setTitle(R.string.share_gagein);
		
		mail = (ImageButton) view.findViewById(R.id.mail);
		linkedIn = (ImageButton) view.findViewById(R.id.linkedIn);
		twitter = (ImageButton) view.findViewById(R.id.twitter);
		facebook = (ImageButton) view.findViewById(R.id.facebook);
		googlePlus = (ImageButton) view.findViewById(R.id.googlePlus);
		signUpNum = (TextView) view.findViewById(R.id.signUpNum);
		resultFrom = (TextView) view.findViewById(R.id.resultFrom);
		creditNum = (TextView) view.findViewById(R.id.creditNum);
		signUpLayout = (LinearLayout) view.findViewById(R.id.signUpLayout);
		creditLayout = (LinearLayout) view.findViewById(R.id.creditLayout);
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
		}
	}
	
}
