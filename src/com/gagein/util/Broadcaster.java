package com.gagein.util;

import java.io.Serializable;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.gagein.ui.main.GageinApplication;

public class Broadcaster {
	
	public class Name {
		public static final String COMPANY_FOLLOWED = "COMPANY_FOLLOWED";
		public static final String COMPANY_UNFOLLOWED = "COMPANY_UNFOLLOWED";
		public static final String EXLPORE_TAB_TAPPED = "EXLPORE_TAB_TAPPED";
		public static final String FORGET_PWD_EMAIL_SENT = "FORGET_PWD_EMAIL_SENT";
		public static final String TABHOST_CHANGE_TO_SAVED = "TABHOST_CHANGE_TO_SAVED";
		public static final String TABHOST_CHANGE_TO_SETTINGS = "TABHOST_CHANGE_TO_SETTINGS";
		public static final String REFRESH_MENU = "REFRESH_MENU";
		public static final String MENU_RESUME = "MENU_RESUME";
		
	}
	
	public class Key {
		public static final String COMPANY_ID = "COMPANY_ID";
		public static final String COMPANY_DATA = "COMPANY_DATA";
		public static final String DUMMY = "DUMMY";
		public static final String EMAIL = "EMAIL";
	}
	
	public static void post(String aNoteName, String aNoteKey, long aNoteValue) {
		Context aContext = GageinApplication.getContext();
		if (aContext != null && !TextUtils.isEmpty(aNoteName)) {
			Intent noteIntent = new Intent(aNoteName);
			if (!TextUtils.isEmpty(aNoteKey)) {
				noteIntent.putExtra(aNoteKey, aNoteValue);
			}
			aContext.sendBroadcast(noteIntent);
		}
	}
	
	public static void post(String aNoteName, String aNoteKey, String aNoteValue) {
		Context aContext = GageinApplication.getContext();
		if (aContext != null && !TextUtils.isEmpty(aNoteName)) {
			Intent noteIntent = new Intent(aNoteName);
			if (aNoteValue != null && !TextUtils.isEmpty(aNoteKey)) {
				noteIntent.putExtra(aNoteKey, aNoteValue);
			}
			aContext.sendBroadcast(noteIntent);
		}
	}
	
	public static void post(String aNoteName, String aNoteKey, Serializable aNoteValue) {
		Context aContext = GageinApplication.getContext();
		if (aContext != null && !TextUtils.isEmpty(aNoteName)) {
			Intent noteIntent = new Intent(aNoteName);
			if (!TextUtils.isEmpty(aNoteKey) && aNoteValue != null) {
				noteIntent.putExtra(aNoteKey, aNoteValue);
			}
			aContext.sendBroadcast(noteIntent);
		}
	}
}
