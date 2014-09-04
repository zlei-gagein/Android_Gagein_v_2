package com.gagein.ui.companies;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.gagein.R;
import com.gagein.component.dialog.EnterWebsiteDialog;
import com.gagein.ui.main.BaseActivity;
import com.gagein.util.CommonUtil;
import com.gagein.util.Constant;
import com.gagein.util.Log;

public class ProfileHelpActivity extends BaseActivity {
	
	private String companyName;
	private Button enterWebsite;
	private long companyId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile_help);
		
		doInit();
	}
	
	@Override
	protected void initView() {
		super.initView();
		enterWebsite = (Button) findViewById(R.id.enterWebsite);
	}
	
	@Override
	public void handleNotifications(Context aContext, Intent intent) {
		super.handleNotifications(aContext, intent);
		String actionName = intent.getAction();
		if (actionName.equals(Constant.BROADCAST_ADDED_PENDING_COMPANY)) {
			Log.v("silen", "BROADCAST_FINISH_PROFILE_PAGE");
			CommonUtil.hideSoftKeyBoard(mContext, ProfileHelpActivity.this);
			finish();
		}
	}
	
	@Override
	protected List<String> observeNotifications() {
		return stringList(Constant.BROADCAST_ADDED_PENDING_COMPANY);
	}
	
	@Override
	protected void initData() {
		super.initData();
		
		companyName = getIntent().getStringExtra(Constant.COMPANYNAME);
		companyId = getIntent().getLongExtra(Constant.COMPANYID, 0);
		setTitle(companyName);
		setLeftImageButton(R.drawable.back_arrow);
		Log.v("silen", "companyName = " + companyName);
	}
	
	@Override
	protected void setOnClickListener() {
		super.setOnClickListener();
		enterWebsite.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		if (v == leftImageBtn) {
			finish();
		} else if (v == enterWebsite) {//TODO
			EnterWebsiteDialog dialog = new EnterWebsiteDialog(mContext);
			dialog.setCancelable(false);
			dialog.showDialog(companyName, companyId);
		}
	}

}
