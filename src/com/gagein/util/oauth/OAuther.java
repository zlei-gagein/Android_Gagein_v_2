package com.gagein.util.oauth;

import java.io.Serializable;
import java.util.HashMap;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import cn.sharesdk.facebook.Facebook;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.PlatformDb;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.twitter.Twitter;

import com.gagein.util.Log;

public class OAuther {

	public static final String MSG_DATA_KEY = "data";
	public static final String AUTH_SN_NAME = "snName";
	public static final String AUTH_ACCESS_TOKEN = "accessToken";
	public static final String AUTH_SECRET = "secret";
	public static final String AUTH_OPEN_ID = "openID";
	
	public static final String SN_NAME_LINKEDIN = "LinkedIn";
	public static final String SN_NAME_SALESFORCE = "Salesforce";
	public static final String SN_NAME_FACEBOOK = Facebook.NAME;
	public static final String SN_NAME_TWITTER = Twitter.NAME;
	
	public static final int kStatusComplete = 0;
	public static final int kStatusError = 1;
	public static final int kStatusCancel = 2;
	
	private Context ctx = null;
	private OAuthWorker worker = null;
	
	public OAuther(Context aContext) {
		ctx = aContext;
		worker = new OAuthWorker(ctx);
	}
	
	public void authLinkedIn(final Handler aHandler) {
		//authorize(aHandler, LinkedIn.NAME);
		worker.executeLinkedInAuth(aHandler);
	}
	
	public void authSalesforce(final Handler aHandler) {
		worker.executeSalesforceAuth(aHandler);
	}
	
	public void authFacebook(final Handler aHandler) {
		authorize(aHandler, Facebook.NAME);
	}
	
	public void authTwitter(final Handler aHandler) {
		authorize(aHandler, Twitter.NAME);
	}
	
	private HashMap<String, String> mapFromPLatformDb(String aSnName) {
		if (aSnName != null) {
			Platform linkedIn = ShareSDK.getPlatform(ctx, aSnName);
			PlatformDb db = linkedIn.getDb();
			if (db != null) {
				HashMap<String, String> dbMap = new HashMap<String, String>();
				dbMap.put(AUTH_SN_NAME, aSnName);
				dbMap.put(AUTH_ACCESS_TOKEN, db.getToken());
				dbMap.put(AUTH_OPEN_ID, db.getUserId());
				dbMap.put(AUTH_SECRET, db.getTokenSecret());
				
				return dbMap;
			}
		}
		
		return null;
	}

	private void authorize (final Handler aHandler, final String aSnName) {
		Platform platform = ShareSDK.getPlatform(ctx, aSnName);
		platform.setPlatformActionListener(new PlatformActionListener() {

			public void onError(Platform platform, int action, Throwable t) {
				// 操作失败
				Log.v("silen", "authorize.onError");
				sendResultMessage(aHandler, aSnName, kStatusError, action);//TODO
			}

			public void onComplete(Platform platform, int action, HashMap<String, Object> res) {
				// 操作成功
				Log.v("silen", "authorize.onComplete");
				sendResultMessage(aHandler, aSnName, kStatusComplete, action);
			}

			public void onCancel(Platform platform, int action) {
				// 操作取消
				Log.v("silen", "authorize.onCancel");
				sendResultMessage(aHandler, aSnName, kStatusCancel, action);//TODO
			}
			
			
			private void sendResultMessage(final Handler aHandler, final String aSnName, int aStatus, int action) {
				Message msg = aHandler.obtainMessage();
				msg.arg1 = aStatus;
				msg.arg2 = action;
				
				HashMap<String, String> dbMap = mapFromPLatformDb(aSnName);
				if (dbMap != null) {
					Bundle bundle = new Bundle();
					bundle.putSerializable(MSG_DATA_KEY, ((Serializable)dbMap));
					msg.setData(bundle);
				}
				
				aHandler.sendMessage(msg);
			}

		});
		
		platform.authorize();
	}
}
