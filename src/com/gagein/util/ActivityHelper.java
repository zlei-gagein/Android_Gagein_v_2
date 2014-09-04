package com.gagein.util;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.gagein.ui.main.WebPageActivity;

public class ActivityHelper {

	/** start web activity */
	public static final void startWebActivity(String url, Context aContext) {
		if (TextUtils.isEmpty(url) || aContext == null) return;
		
		Intent intent = new Intent(aContext, WebPageActivity.class);
		intent.putExtra(Constant.URL, url);
		intent.putExtra(Constant.WEBVIEW_FONT_SIZE, 1);
		aContext.startActivity(intent);
	}
	
	//TODO
//	public static final void startCompanyDetailActivity(long aCompanyID, Context aContext) {
//		if (aCompanyID > 0 && aContext != null) {
//			Intent intent = new Intent();
//			intent.setClass(aContext, CompanyDetailActivity.class);
//			intent.putExtra(Constant.COMPANYID, aCompanyID);
//			aContext.startActivity(intent);
//		}
//	}
}
