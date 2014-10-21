package com.gagein.ui.tablet.settins;

import java.util.Date;

import org.json.JSONObject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Response.Listener;
import com.gagein.R;
import com.gagein.http.APIHttp;
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
	private TextView refer;
	private TextView freeMonthLeftTx;
	private TextView freeMonthTotalTx;
	private TextView coworkerCountTx;
	private LinearLayout freeDaysLayout;
	private LinearLayout freeMonthLayout;
	private ScrollView wholeLayout;;
	
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
		refer = (TextView) view.findViewById(R.id.refer);
		freeMonthLeftTx = (TextView) view.findViewById(R.id.freeMonthLeft);
		freeMonthTotalTx = (TextView) view.findViewById(R.id.freeMonthTotal);
		coworkerCountTx = (TextView) view.findViewById(R.id.coworkerCount);
		freeDaysLayout = (LinearLayout) view.findViewById(R.id.freeDaysLayout);
		freeMonthLayout = (LinearLayout) view.findViewById(R.id.freeMonthLayout);
		wholeLayout = (ScrollView) view.findViewById(R.id.wholeLayout);
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
	
	private void setReferalResultValue (int freeDaysLeft, int freeMonthTotal, int coworkerCount) {
		freeMonthLeftTx.setText(freeDaysLeft + "");
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
					
					wholeLayout.setVisibility(View.VISIBLE);
					
					int freeDaysLeft = parser.data().optInt("free_days_left");
					int freeMonthTotal = parser.data().optInt("free_month_total");
					int coworkerCount = parser.data().optInt("coworker_count");
					setReferalResultValue(freeDaysLeft, freeMonthTotal, coworkerCount);
					
				} else {
					
					wholeLayout.setVisibility(View.GONE);
					
				}
			}
		}, new Response.ErrorListener() {
			
			@Override
			public void onErrorResponse(VolleyError error) {
				showConnectionError(mContext);
			}
		});
	}
	
	private void shareGetId(final String source) {
		showLoadingDialog(mContext);
		mApiHttp.shareGetId(source, new Listener<JSONObject>() {
			
			@Override
			public void onResponse(JSONObject jsonObject) {
				dismissLoadingDialog();
				APIParser parser = new APIParser(jsonObject);
				if (parser.isOK()) {
					long time = new Date().getTime();
					String shareId = parser.data().optString("shareid");
					String owenerId = CommonUtil.getMemid(mContext);
					String url = "http://www.gagein.com/referral/?t=%1$s&source=%2$s&owner=%3$s&shareid=%4$s";
					
					if (source.equalsIgnoreCase("se100")) {
						String url1 = APIHttp.serverURL + "/dragon/ShareProxy?url=" + CommonUtil.urlEncodedString(String.format(url, time, source, owenerId, shareId));
//						String content = CommonUtil.stringFromResID(R.string.share_email_content, mContext) + 
//								APIHttp.serverURL + "/dragon/ShareProxy?url=" + CommonUtil.urlEncodedString(String.format(url, time, source, owenerId, shareId));
						String content = "<div><p>I highly recommend award winning Gagein as the best way to find new high value sales prospects.For about the cost of 2 Starbucks ventis a month, Gagein sends me actionable news every day that pinpoints when and why to call my prospects to have the best chance of closing a deal.</p>"
								+ "<p>Click here to start your free 30 day trial.</p>"
								+ "<p>%s</p>"
								+ "</div>";
						CommonUtil.sendEmail(CommonUtil.stringFromResID(R.string.share_email_subject, mContext), String.format(content, url1), mContext);
					} else if (source.equalsIgnoreCase("sln100")) {
						startWebActivity(APIHttp.serverURL + "/dragon/ShareProxy?url=" + 
								CommonUtil.urlEncodedString(String.format(url, time, source, owenerId, shareId) + "&type=linkedin"));
					} else if (source.equalsIgnoreCase("stw100")) {
						startWebActivity(APIHttp.serverURL + "/dragon/ShareProxy?url=" + 
								CommonUtil.urlEncodedString(String.format(url, time, source, owenerId, shareId) + "&type=twitter&title=I'm finding high value sales prospects with Gagein. Get your FREE account today"));
					}else if (source.equalsIgnoreCase("sfb100")) {
						startWebActivity(APIHttp.serverURL + "/dragon/ShareProxy?url=" + 
								CommonUtil.urlEncodedString(String.format(url, time, source, owenerId, shareId) + "&type=facebook"));
					} else if (source.equalsIgnoreCase("sgp100")) {
						startWebActivity(APIHttp.serverURL + "/dragon/ShareProxy?url=" + 
								CommonUtil.urlEncodedString(String.format(url, time, source, owenerId, shareId) + "&type=google+plus"));
					}
					
				} else {
				}
			}
		}, new Response.ErrorListener() {
			
			@Override
			public void onErrorResponse(VolleyError error) {
				dismissLoadingDialog();
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
			shareGetId("se100");
		} else if (v == linkedIn) {
			shareGetId("sln100");
		} else if (v == twitter) {
			shareGetId("stw100");
		} else if (v == facebook) {
			shareGetId("sfb100");
		} else if (v == googlePlus) {
			shareGetId("sgp100");
		}
	}
	
}







