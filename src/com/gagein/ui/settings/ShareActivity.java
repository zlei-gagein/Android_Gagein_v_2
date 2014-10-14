package com.gagein.ui.settings;

import org.json.JSONObject;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Response.Listener;
import com.gagein.R;
import com.gagein.http.APIHttpMetadata;
import com.gagein.http.APIParser;
import com.gagein.ui.main.BaseActivity;
import com.gagein.util.CommonUtil;

public class ShareActivity extends BaseActivity {
	
	private ImageButton mail;
	private ImageButton linkedIn;
	private ImageButton twitter;
	private ImageButton facebook;
	private ImageButton googlePlus;
	private TextView refer;
	private TextView freeMonthLeftTx;
	private TextView freeMonthTotalTx;
	private TextView coworkerCountTx;
	private LinearLayout freeDaysLayout;
	private LinearLayout freeMonthLayout;
	private ScrollView wholeLayout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share);
		
		doInit();
	}

	@Override
	protected void initView() {
		super.initView();
		
		setTitle(R.string.share_gagein);
		setLeftImageButton(R.drawable.back_arrow);
		
		mail = (ImageButton) findViewById(R.id.mail);
		linkedIn = (ImageButton) findViewById(R.id.linkedIn);
		twitter = (ImageButton) findViewById(R.id.twitter);
		facebook = (ImageButton) findViewById(R.id.facebook);
		googlePlus = (ImageButton) findViewById(R.id.googlePlus);
		refer = (TextView) findViewById(R.id.refer);
		freeMonthLeftTx = (TextView) findViewById(R.id.freeMonthLeft);
		freeMonthTotalTx = (TextView) findViewById(R.id.freeMonthTotal);
		coworkerCountTx = (TextView) findViewById(R.id.coworkerCount);
		freeDaysLayout = (LinearLayout) findViewById(R.id.freeDaysLayout);
		freeMonthLayout = (LinearLayout) findViewById(R.id.freeMonthLayout);
		wholeLayout = (ScrollView) findViewById(R.id.wholeLayout);
	}
	
	@Override
	protected void initData() {
		super.initData();
		
		billingGetInfo();
		shareReferalResult();
	}
	
	private void setShareLayoutVisible(String planId) {
		
		Boolean isProfessional = APIHttpMetadata.kGGPlanTypeProfessional.equalsIgnoreCase(planId) ? true : false;
		refer.setVisibility(isProfessional ? View.VISIBLE : View.GONE);
		freeDaysLayout.setVisibility(isProfessional ? View.VISIBLE : View.GONE);
		freeMonthLayout.setVisibility(isProfessional ? View.VISIBLE : View.GONE);
		
	}
	
	private void setReferalResultValue (int freeMonthLeft, int freeMonthTotal, int coworkerCount) {
		
		freeMonthLeftTx.setText(freeMonthLeft + "");
		freeMonthTotalTx.setText(freeMonthTotal + "");
		coworkerCountTx.setText(coworkerCount + "");
		
	}
	
	private void billingGetInfo() {
		
		showLoadingDialog();
		
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
				showConnectionError();
			}
		});
	}
	
	private void shareReferalResult() {
		
		mApiHttp.shareReferalResult(new Listener<JSONObject>() {
			
			@Override
			public void onResponse(JSONObject jsonObject) {
				
				APIParser parser = new APIParser(jsonObject);
				if (parser.isOK()) {
					
					wholeLayout.setVisibility(View.VISIBLE);
					
					int freeMonthLeft = parser.data().optInt("free_month_left");
					int freeMonthTotal = parser.data().optInt("free_month_total");
					int coworkerCount = parser.data().optInt("coworker_count");
					setReferalResultValue(freeMonthLeft, freeMonthTotal, coworkerCount);
					
				} else {
					
					wholeLayout.setVisibility(View.GONE);
					
				}
			}
		}, new Response.ErrorListener() {
			
			@Override
			public void onErrorResponse(VolleyError error) {
				showConnectionError();
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
		} else if (v == leftImageBtn) {
			finish();
		}
	}
}
