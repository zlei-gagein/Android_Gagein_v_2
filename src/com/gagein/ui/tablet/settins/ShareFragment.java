package com.gagein.ui.tablet.settins;

import org.json.JSONObject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Response.Listener;
import com.gagein.R;
import com.gagein.http.APIHttpMetadata;
import com.gagein.http.APIParser;
import com.gagein.ui.BaseFragment;
import com.gagein.util.CommonUtil;

public class ShareFragment extends BaseFragment{
	
	private ImageButton mail;
	private ImageButton linkedIn;
	private ImageButton twitter;
	private ImageButton facebook;
	private ImageButton googlePlus;
	private TextView freeMonthLeftTx;
	private TextView freeMonthTotalTx;
	private TextView coworkerCountTx;
	private LinearLayout shareLayout;
	private LinearLayout referalResultLayout;
	
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
		freeMonthLeftTx = (TextView) view.findViewById(R.id.freeMonthLeft);
		freeMonthTotalTx = (TextView) view.findViewById(R.id.freeMonthTotal);
		coworkerCountTx = (TextView) view.findViewById(R.id.coworkerCount);
		shareLayout = (LinearLayout) view.findViewById(R.id.shareLayout);
		referalResultLayout = (LinearLayout) view.findViewById(R.id.referalResultLayout);
	}
	
	@Override
	protected void initData() {
		super.initData();
		
		billingGetInfo();
		shareReferalResult();
	}
	
private void setShareLayoutVisible(String planId) {
		
		Boolean isProfessional = APIHttpMetadata.kGGPlanTypeProfessional.equalsIgnoreCase(planId) ? true : false;
		shareLayout.setVisibility(isProfessional ? View.VISIBLE : View.GONE);
		referalResultLayout.setVisibility(isProfessional ? View.VISIBLE : View.GONE);
		
	}
	
	private void setReferalResultValue (int freeMonthLeft, int freeMonthTotal, int coworkerCount) {
		
		freeMonthLeftTx.setText(freeMonthLeft + "");
		freeMonthTotalTx.setText(freeMonthTotal + "");
		coworkerCountTx.setText(coworkerCount + "");
		
	}
	
	private void billingGetInfo() {
		
		showLoadingDialog(mContext);
		
		mApiHttp.billingGetInfo(new Listener<JSONObject>() {
			
			@Override
			public void onResponse(JSONObject jsonObject) {
				
				APIParser parser = new APIParser(jsonObject);
				if (parser.isOK()) {
					
					String planId = parser.data().optString("plan_id");
					setShareLayoutVisible(planId);
					
				} else {
				}
				dismissLoadingDialog();
			}
		}, new Response.ErrorListener() {
			
			@Override
			public void onErrorResponse(VolleyError error) {
				showConnectionError(mContext);
			}
		});
	}
	
	private void shareReferalResult() {
		
		mApiHttp.shareReferalResult(new Listener<JSONObject>() {
			
			@Override
			public void onResponse(JSONObject jsonObject) {
				
				APIParser parser = new APIParser(jsonObject);
				if (parser.isOK()) {
					
					int freeMonthLeft = parser.data().optInt("free_month_left");
					int freeMonthTotal = parser.data().optInt("free_month_total");
					int coworkerCount = parser.data().optInt("coworker_count");
					setReferalResultValue(freeMonthLeft, freeMonthTotal, coworkerCount);
					
				} else {
				}
			}
		}, new Response.ErrorListener() {
			
			@Override
			public void onErrorResponse(VolleyError error) {
				showConnectionError(mContext);
			}
		});
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
