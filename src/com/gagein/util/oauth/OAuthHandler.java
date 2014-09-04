package com.gagein.util.oauth;

import java.io.Serializable;
import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import cn.sharesdk.facebook.Facebook;
import cn.sharesdk.twitter.Twitter;

import com.gagein.util.CommonUtil;
import com.gagein.util.Constant;
import com.gagein.util.Log;

public class OAuthHandler extends Handler{

	public static class OAuthHandlerData implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 2519504640960805596L;
		
		public String snName;
		public String accessToken;
		public String openId;
		public String secret;
		public String refreshToken;
		public String instanceUrl;
		public String issueAt;
		public String signature;
		public static String comeFrom = "";
		public static final int kStatusComplete = 0;
		public static final int kStatusError = 1;
		public static final int kStatusCancel = 2;
		public static final String SN_NAME_FACEBOOK = Facebook.NAME;
		public static final String SN_NAME_TWITTER = Twitter.NAME;

		public OAuthHandlerData() {
		}
	}

	private Context ctx = null;
	public static final String KEY_INTENT_ACTION_SUCCESS = "KEY_INTENT_ACTION_SUCCESS";
	public static final String KEY_INTENT_DATA = "KEY_INTENT_DATA";
	public static final String KEY_INTENT_ACTION_SUCCESS_SUGGEST = "KEY_INTENT_ACTION_SUCCESS_SUGGEST";
	
	public OAuthHandlerData data = new OAuthHandlerData();

	public OAuthHandler(Context aContext, String comeFrom) {
		ctx = aContext;
		OAuthHandlerData.comeFrom = comeFrom;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void handleMessage(Message msg) {
		super.handleMessage(msg);
		
		int status = msg.arg1;
		if (status == OAuthHandlerData.kStatusError) {
			CommonUtil.showShortToast("Authorize Error", ctx);
			Log.v("silen", "OAuthHandlerData.kStatusError");
			return;
		} else if (status == OAuthHandlerData.kStatusCancel) {
			Log.v("silen", "OAuthHandlerData.kStatusCancel");
			return;
		}
		
		Bundle bundle = msg.getData();
		HashMap<String, String> dbMap = (HashMap<String, String>)bundle.getSerializable(OAuther.MSG_DATA_KEY);
		
		data.snName = dbMap.get(OAuther.AUTH_SN_NAME);
		data.accessToken = dbMap.get(OAuther.AUTH_ACCESS_TOKEN); // 获取授权token
		data.openId = dbMap.get(OAuther.AUTH_OPEN_ID); // 获取用户在此平台的ID
		data.secret = dbMap.get(OAuther.AUTH_SECRET); // 获取用户昵称
		
		if (data.snName.equalsIgnoreCase(OAuther.SN_NAME_SALESFORCE)) {
			data.refreshToken = dbMap.get(OAuthSalesforceAPI.KEY_REFRESH_TOKEN);
			data.instanceUrl = dbMap.get(OAuthSalesforceAPI.KEY_INSTANCE_URL);
			data.issueAt = dbMap.get(OAuthSalesforceAPI.KEY_ISSUE_AT);
			data.signature = dbMap.get(OAuthSalesforceAPI.KEY_SIGNATURE);
			
			//log("refresh token:" + refreshToken + "\n instanceUrl:" + instanceUrl
					//+ "\n issue_at:" + issueAt + "\n signature:" +signature);
		}
		
		// 接下来执行您要的操作
		//String msgStr = "\nsnName:" + snName + "\naccess token:" + accessToken + "\nopenID:" + openId + "\nsecret:" + secret + "\nstatus:" + msg.arg1 + "\naction:" + msg.arg2;
		//log(msgStr);
		
		// send broadcaset
		
		if (null != OAuthHandlerData.comeFrom) {
			if (OAuthHandlerData.comeFrom.equals(Constant.SUGGEST)) {
				Intent intent = new Intent(KEY_INTENT_ACTION_SUCCESS_SUGGEST);
				intent.putExtra(KEY_INTENT_DATA, data);
				ctx.sendBroadcast(intent);
				Log.v("silen", "KEY_INTENT_ACTION_SUCCESS_SUGGEST");
			} else {
				Intent intent = new Intent(KEY_INTENT_ACTION_SUCCESS);
				intent.putExtra(KEY_INTENT_DATA, data);
				ctx.sendBroadcast(intent);
				Log.v("silen", "KEY_INTENT_ACTION_SUCCESS");
			}
		}
		Log.v("silen", "OAuthHandler.handleMessage");
		//LocalBroadcastManager.getInstance(ctx).sendBroadcast(intent);
	}
}