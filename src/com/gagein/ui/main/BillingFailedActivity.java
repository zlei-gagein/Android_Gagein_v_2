package com.gagein.ui.main;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.gagein.R;
import com.gagein.http.APIHttpMetadata;
import com.gagein.util.CommonUtil;
import com.gagein.util.Constant;

public class BillingFailedActivity extends BaseActivity {
	
	private TextView titleTx;
	private TextView content;
	private Button logOut;
	private int billingStatus;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_billing_failed);
		
		doInit();
	}
	
	@Override
	protected void initView() {
		super.initView();
		
		setTitle(R.string.u_gagein);
		
		titleTx = (TextView) findViewById(R.id.titlePt);
		content = (TextView) findViewById(R.id.content);
		logOut = (Button) findViewById(R.id.logOut);
	}
	
	@Override
	protected void initData() {
		super.initData();
		
		billingStatus = getIntent().getIntExtra(Constant.BILLINGSTATUS, 0);
		
		String titleStr = "";
		String contentStr = "";
		
		titleStr = CommonUtil.stringFromResID(R.string.trial_ended);
		contentStr = CommonUtil.stringFromResID(R.string.trial_ended_pt);
		
		switch (billingStatus) {
		case APIHttpMetadata.kBillingStatusNotPurchased:
			titleStr = CommonUtil.stringFromResID(R.string.your_payment_has_failed);
			contentStr = CommonUtil.stringFromResID(R.string.payment_pt);
			break;
			
		case APIHttpMetadata.kBillingStatusPurchaseExpired:
			titleStr = CommonUtil.stringFromResID(R.string.trial_ended);
			contentStr = CommonUtil.stringFromResID(R.string.trial_ended_pt);
			break;
			
		case APIHttpMetadata.kBillingStatusSeatRemoved:
			titleStr = CommonUtil.stringFromResID(R.string.seat_required);
			contentStr = CommonUtil.stringFromResID(R.string.seat_required_pt);
			break;
			
		case APIHttpMetadata.kBillingStatusTeamMemberExpired:
			titleStr = CommonUtil.stringFromResID(R.string.account_suspended);
			contentStr = CommonUtil.stringFromResID(R.string.account_suspended_pt);
			break;

		default:
			break;
		}
		
		titleTx.setText(titleStr);
		content.setText(contentStr);
		
	}
	
	@Override
	protected void setOnClickListener() {
		super.setOnClickListener();
		
		logOut.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		
		if (v == logOut) {
			startActivitySimple(LoginActivity.class);
			finish();
		}
	}
	
}
