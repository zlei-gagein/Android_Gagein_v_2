package com.gagein.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.support.v4.app.FragmentActivity;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gagein.R;
import com.gagein.component.dialog.PromotDialog;
import com.gagein.http.APIHttpMetadata;
import com.gagein.http.APIParser;
import com.gagein.model.AutoLoginInfo;
import com.gagein.model.Contact;
import com.gagein.model.Group;
import com.gagein.model.SavedSearch;
import com.gagein.model.Update;
import com.gagein.model.filter.FilterItem;
import com.gagein.model.filter.Filters;
import com.gagein.model.filter.Industry;
import com.gagein.model.filter.JobTitle;
import com.gagein.model.filter.Location;
import com.gagein.ui.main.GageinApplication;
import com.gagein.util.oauth.OAuther;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * CommonUtil: 一般工具方法类
 */
@SuppressLint("DefaultLocale")
public class CommonUtil {

	/**
	 * list Activity
	 */
	public static List<Activity> list;
	
	public static ProgressDialog mProgressDialog;
	
	/**
	 * 读取资源，返回字符串
	 * 
	 * @param aResId
	 *            资源id
	 * @param aContext
	 *            context
	 * @return 资源内容
	 */
	public static String parseRawToString(int aResId, Context aContext) {

		InputStream is = aContext.getResources().openRawResource(aResId);
		InputStreamReader isReader = null;
		try {
			isReader = new InputStreamReader(is, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}

		if (isReader != null) {
			BufferedReader br = new BufferedReader(isReader);
			StringBuilder result = new StringBuilder();
			String readLine = null;
			try {
				while ((readLine = br.readLine()) != null) {
					result.append(readLine);
				}
				is.close();
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return result.toString();
		}

		return null;
	}

	/**
	 * 获取设备ID
	 * 
	 * @param aContext
	 * @return 设备ID
	 */
	public static String deviceID(Context aContext) {
		TelephonyManager telephonyManager = (TelephonyManager) aContext.getSystemService(Context.TELEPHONY_SERVICE);
		return telephonyManager.getDeviceId();
	}

	/**
	 * 将activity添加到list中
	 * 
	 * @param activity
	 */
	public static void addActivity(Activity activity) {
		list = new ArrayList<Activity>();
		list.add(activity);
	}

	/**
	 * 将activity从list中移除
	 */
	public static void removeActivity() {
		if (list != null) {
			for (int i = 0; i < list.size(); i++) {
				list.get(i).finish();
			}
		}
	}

	/**
	 * 显示服务器端返回的错误信息
	 * 
	 * @param 上下文
	 * @param 状态码
	 */
	public static void showErrorInfo(Context mContext, String status) {
		String strStatusInfo = "";
		if (Constant.PARAMETER_WRONG.equals(status)) {
			strStatusInfo = mContext.getResources().getString(R.string.parameter_wrong);
		} else if (Constant.VERIFY_FAIL.equals(status)) {
			strStatusInfo = mContext.getResources().getString(R.string.verify_fail);
		} else if (Constant.SYSTEM_INTERNAL_ERROR.equals(status)) {
			strStatusInfo = mContext.getResources().getString(R.string.system_internal_error);
		} else if (Constant.USER_OPERATION_MISTAKE.equals(status)) {
			strStatusInfo = mContext.getResources().getString(R.string.user_operation_mistake);
		}

		showShortToast(strStatusInfo);
	}

	/**
	 * 保存登录信息
	 * 
	 * @param jObject
	 */
	@SuppressLint("CommitPrefEdits")
	public static void saveLoginInfo(Context mContext, AutoLoginInfo autoLoginInfo) {
		if (autoLoginInfo == null) return;
		
		SharedPreferences sharedPreferences = mContext.getSharedPreferences("LOGIN_INFO", Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		if (autoLoginInfo.accessToken != null)
			editor.putString("access_token", autoLoginInfo.accessToken);
		if (autoLoginInfo.memberFullName != null)
			editor.putString("mem_full_name", autoLoginInfo.memberFullName);
		if (autoLoginInfo.memberTimeZone != null)
			editor.putString("mem_timezone", autoLoginInfo.memberTimeZone);
		editor.putString("memid", autoLoginInfo.memberID + "");
		editor.putString("signup_process_status", autoLoginInfo.signupProcessStatus + "");
		editor.commit();

		CommonUtil.setToken(mContext);
	}
	
	@SuppressLint("CommitPrefEdits")
	public static void saveAccessInfo(Context mContext, int allowAccessToContact) {
		SharedPreferences sharedPreferences = mContext.getSharedPreferences("ACCESS_INFO", Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		editor.putInt("allowAccessToContact", allowAccessToContact);
		editor.commit();
	}
	
	public static int getAccessInfo(Context mContext) {
		SharedPreferences sharedPreferences = mContext.getSharedPreferences("ACCESS_INFO", Context.MODE_PRIVATE);
		return sharedPreferences.getInt("allowAccessToContact", -1);
	}

	/**
	 * 获取token
	 * 
	 * @param mContext
	 * @return
	 */
	public static String getToken(Context mContext) {
		if (mContext != null) {
			SharedPreferences sharedPreferences = mContext.getSharedPreferences("LOGIN_INFO", Context.MODE_PRIVATE);
			String token = "";
			token = sharedPreferences.getString("access_token", "");
			return token;
		} else {
			return "";
		}
	}

	/**
	 * 设置token
	 * 
	 * @param mContext
	 */
	public static void setToken(Context mContext) {
		SharedPreferences sharedPreferences = mContext.getSharedPreferences("LOGIN_INFO", Context.MODE_PRIVATE);
		String token = "";
		token = sharedPreferences.getString("access_token", "");
		APIHttpMetadata.TOKEN = token;
	}

	/**
	 * 清除login信息
	 * 
	 * @param mContext
	 */
	public static void clearLoginInfo(Context mContext) {
		SharedPreferences sharedPreferences = mContext.getSharedPreferences(
				"LOGIN_INFO", Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		editor.clear();
		editor.commit();
	}

	/**
	 * 
	 * @param mContext
	 * @param msg
	 * @param stringId
	 */
	public static void showDialog(Context mContext, String msg) {
		if (mContext == null) {
			return;
		}

		showLongToast(msg, mContext);
	}

	public static void showDialog(String msg) {
		showDialog(GageinApplication.getContext(), msg);
	}

	/**
	 * 显示连接失败信息
	 * 
	 * @param mContext
	 */
	public static void showHttpFailed(Context mContext) {
		showShortToast(stringFromResID(R.string.http_failed));
	}

	public static void showHttpFailed() {
		showHttpFailed(GageinApplication.getContext());
	}

	/**
	 * 安全string
	 * 
	 * @param aString
	 * @return
	 */
	public static String safeString(String aString) {
		return (aString != null) ? aString : "";
	}

	/**
	 * 时间转化
	 * 
	 * @param date
	 * @return
	 */
	public static String dateFormat(long date) {

		long currentTimeMillis = System.currentTimeMillis();
		long interval = (currentTimeMillis - date) / 1000;

		int days = (int) (interval / (3600 * 24));
		long secondsInADay = interval % (3600 * 24);
		//
		if (days < 1) {
			int hours = (int) (secondsInADay / 3600);
			if (hours > 0) {
				return hours + "h ago";
			} else {
				return "Just now";
			}
		} else if (days < 31) {
			return days + "d ago";
		} else {
			int months = days / 31;
			return months + "m ago";
		}
	}
	
	/** 根据ID获取字符串资源 */
	public static String stringFromResID(int aResID) {
		return stringFromResID(aResID, GageinApplication.getContext());
	}

	public static String stringFromResID(int aResID, Context aContext) {
		return aContext.getResources().getString(aResID);
	}

	/** email string format check */
	public static boolean isValidEmail(CharSequence target) {
		if (target == null) {
			return false;
		} else {
			return android.util.Patterns.EMAIL_ADDRESS.matcher(target)
					.matches();
		}
	}

	/** Toast */
	public static void showShortToast(String aMessage, Context aContext) {
		if (aMessage != null && aMessage.length() > 0 && aContext != null) {
			Toast toast = Toast.makeText(aContext, aMessage, Toast.LENGTH_SHORT);
			
			LayoutInflater inflater = (LayoutInflater)aContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View toastRoot = inflater.inflate(R.layout.normal_toast, null);
			toast.setView(toastRoot);
			TextView textView = (TextView) toastRoot.findViewById(R.id.text);
			textView.setText(aMessage);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
		}
	}
	
	public static void showImageShortToast(String aMessage, Context aContext) {
		if (aMessage != null && aMessage.length() > 0 && aContext != null) {
			Toast toast = Toast.makeText(aContext, aMessage, Toast.LENGTH_SHORT);
			
			LayoutInflater inflater = (LayoutInflater)aContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View toastRoot = inflater.inflate(R.layout.image_toast, null);
			toast.setView(toastRoot);
			TextView textView = (TextView) toastRoot.findViewById(R.id.text);
			textView.setText(aMessage);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
		}
	}

	public static void showShortToast(String aMessage) {
		showShortToast(aMessage, GageinApplication.getContext());
	}
	
	public static void showShortToast(int stringId) {
		showShortToast(stringFromResID(stringId));
	}

	public static void showLongToast(String aMessage, Context aContext) {
		if (aMessage != null && aMessage.length() > 0 && aContext != null) {
			Toast toast = Toast.makeText(aContext, aMessage, Toast.LENGTH_LONG);
			
			LayoutInflater inflater = (LayoutInflater)aContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View toastRoot = inflater.inflate(R.layout.normal_toast, null);
			toast.setView(toastRoot);
			TextView textView = (TextView) toastRoot.findViewById(R.id.text);
			textView.setText(aMessage);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
		}
	}

	public static void showLongToast(String aMessage) {
		showLongToast(aMessage, GageinApplication.getContext());
	}

	/** alert message for parser */
	public static void alertMessageForParser(APIParser parser) {
		String msg = MessageCode.messageForCode(parser.messageCode());
		if (msg != null && msg.length() > 0) {
			showLongToast(msg);
		} else {
			msg = parser.message();
			if (!msg.equalsIgnoreCase("ok") && !msg.equalsIgnoreCase("error")) {
				showLongToast(msg);
			}
		}
	}

	/** get class TAG */
	public static String tagForClass(Class<?> aClass) {
		if (aClass != null) {
			String className = aClass.getSimpleName();
			return "Gagein." + className;
		}

		return null;
	}

	/** download image for imageView */
	public static void loadImage(String aUrl, ImageView aImageView) {
		if (aUrl != null && aUrl.length() > 0 && aImageView != null) {
			ImageLoader.getInstance().displayImage(aUrl, aImageView);
		}
	}

	/** check if the social account has been linked */
	public static Boolean hasLinkedSnType(int aSnType) {
		String snTypeStr = aSnType + "";
		List<String> snTypes = RuntimeData.sharedInstance().getSnTypes();
		if (snTypes != null) {
			for (String type : snTypes) {
				if (snTypeStr.equalsIgnoreCase(type)) {
					return true;
				}
			}
		}

		return false;
	}

	/** remove linked social account type */
	public static void removeSnType(int aSnType) {
		String snTypeStr = aSnType + "";
		List<String> snTypes = RuntimeData.sharedInstance().getSnTypes();
		if (snTypes != null) {
			Log.v("silen", "snTypes.size = " + snTypes.size());
			for (int i = 0; i < snTypes.size(); i++) {
				if (snTypeStr.equalsIgnoreCase(snTypes.get(i))) {
					snTypes.remove(snTypes.get(i));
				}
			}
		}
	}

	/** add linked social account type */
	public static void addSnType(int aSnType) {
		String snTypeStr = aSnType + "";
		List<String> snTypes = RuntimeData.sharedInstance().getSnTypes();
		if (snTypes != null) {
			for (String type : snTypes) {
				if (snTypeStr.equalsIgnoreCase(type)) {
					return;
				}
			}

			snTypes.add(snTypeStr);
		}
	}

	/** sn type from name */
	public static int typeFromSnName(String aSnName) {
		if (aSnName != null) {
			if (aSnName.equalsIgnoreCase(OAuther.SN_NAME_FACEBOOK)) {
				return APIHttpMetadata.kGGSnTypeFacebook;
			} else if (aSnName.equalsIgnoreCase(OAuther.SN_NAME_TWITTER)) {
				return APIHttpMetadata.kGGSnTypeTwitter;
			} else if (aSnName.equalsIgnoreCase(OAuther.SN_NAME_LINKEDIN)) {
				return APIHttpMetadata.kGGSnTypeLinkedIn;
			} else if (aSnName.equalsIgnoreCase(OAuther.SN_NAME_SALESFORCE)) {
				return APIHttpMetadata.kGGSnTypeSalesforce;
			}
		}

		return APIHttpMetadata.kGGSnTypeUnknown;
	}

	/** get social network name from types */
	public static String nameFromSnType(int aSnType) {
		switch (aSnType) {
		case APIHttpMetadata.kGGSnTypeFacebook:
			return OAuther.SN_NAME_FACEBOOK;

		case APIHttpMetadata.kGGSnTypeTwitter:
			return OAuther.SN_NAME_TWITTER;

		case APIHttpMetadata.kGGSnTypeLinkedIn:
			return OAuther.SN_NAME_LINKEDIN;

		case APIHttpMetadata.kGGSnTypeSalesforce:
			return OAuther.SN_NAME_SALESFORCE;
		}

		return null;
	}

	/** max message length for social network sharing */
	public static int maxMessageLengthForSharing(int aSnType) {
		switch (aSnType) {
		case APIHttpMetadata.kGGSnTypeFacebook:
			return 340;

		case APIHttpMetadata.kGGSnTypeSalesforce:
			return 390;

		case APIHttpMetadata.kGGSnTypeTwitter:
			return 114;

		case APIHttpMetadata.kGGSnTypeLinkedIn:
			return 190;
		}

		return 400;
	}

	public static int px2dp(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	public static int dp2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 首字母大写
	 * 
	 * @param str
	 * @return
	 */
	public static String firstCharToUpper(String str) {
		if (null != str && str.length() > 0) {
			str = Character.toUpperCase(str.charAt(0)) + str.substring(1);
		}
		return str;
	}

	@SuppressLint("DefaultLocale")
	public static String revenueFormat(String revenueStr) {
		if (null == revenueStr || revenueStr.isEmpty()) return "";
		if (revenueStr.contains(".")) {
			revenueStr = revenueStr.substring(0, revenueStr.indexOf("."));
		}
		long revenue = Long.parseLong(revenueStr);
		long BILLION = Long.parseLong("1000000000");
		long MILLION = Long.parseLong("1000000");

		if (revenue > BILLION) {
			revenueStr = String.format("%.2f", (double) revenue / BILLION);
			return "$" + revenueStr + "B";
		} else if (revenue > MILLION) {
			revenueStr = String.format("%.2f", (double) revenue / MILLION);
			if (revenueStr.contains(".")) {
				String str = revenueStr.substring(revenueStr.indexOf("."));
				if (str.equals(".00")) {
					revenueStr = revenueStr.substring(0, revenueStr.indexOf("."));
				}
			}
			return "$" + revenueStr + "M";
		}
		return "$" + parserNum(revenueStr);
	}
	
	public static String employeesSizeFromat(String employeesStr) {//TODO
		long employeeSize = Long.parseLong(employeesStr);
		if (employeeSize >= 1000) {
			employeeSize = employeeSize/1000;
			return employeeSize + "K";
		}
		return employeeSize + "";
	}
	

	public static int getDeviceHeight(Activity activity) {
		DisplayMetrics displaymetrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		return displaymetrics.heightPixels;
	}

	public static int getDeviceWidth(Activity activity) {
		DisplayMetrics displaymetrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		return displaymetrics.widthPixels;
	}

	public static int getStatusBarHeight(Context context) {
		Class<?> c = null;
		Object obj = null;
		Field field = null;
		int x = 0, statusBarHeight = 0;
		try {
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			field = c.getField("status_bar_height");
			x = Integer.parseInt(field.get(obj).toString());
			statusBarHeight = context.getResources().getDimensionPixelSize(x);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return statusBarHeight;
	}

	public static boolean canSendSMS(Context mContext) {
		if (mContext.getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_TELEPHONY)) {
			// THIS PHONE HAS SMS FUNCTIONALITY
			return true;
		} else {
			// NO SMS HERE :(
			return false;
		}
	}

	/** create a progress dialog with message */
	public static ProgressDialog progressWithMessage(String aMessage,
			Context aContext) {
		if (aContext == null)
			return null;

		if (aMessage == null)
			aMessage = "";

		ProgressDialog hud = new ProgressDialog(aContext);
		hud.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		hud.setMessage(aMessage);
		hud.setIndeterminate(false);
		hud.setCancelable(true);
		hud.setCanceledOnTouchOutside(false);

		return hud;
	}

	public static boolean isTablet(Context context) {
		return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
	}

	/** assemble a string of update similar IDs */
	public static String stringSimilarIDsWithUpdates(List<Update> aUpdates) {
		if (aUpdates == null || aUpdates.size() <= 0)
			return "";

		String result = "";
		for (Update update : aUpdates) {
			result += update.newsSimilarID + ",";
		}

		if (result.length() > 1) {
			result = result.substring(0, result.length() - 2);
		}

		return result;
	}

	public static List<Update> setSimilarIdToNullFromUpadates(
			List<Update> updates) {
		if (updates == null || updates.size() <= 0)
			return updates;
		for (int i = 0; i < updates.size(); i++) {
			updates.get(i).newsSimilarID = 0;
		}
		return updates;
	}

	public static int getMenuWith(Context mContext, Activity activity) {
		int menuWith = 0;
		if (CommonUtil.isTablet(mContext)) {
			if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
				menuWith = CommonUtil.getDeviceHeight(activity) / 3;
			} else {
				menuWith = CommonUtil.getDeviceWidth(activity) / 3;
			}
		} else {
			menuWith = CommonUtil.getDeviceWidth(activity) * 4 / 5;
		}

		return menuWith;
	}

	public static void setTabletLandContentWith(Context mContext,
			Activity mActivity, View layout) {
		if (CommonUtil.isTablet(mContext)) {
			if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
				int height = getDeviceHeight(mActivity);
				int width = getDeviceWidth(mActivity);
				int padding = (width - height) / 2;
				layout.setPadding(padding, 0, padding, 0);
			} else if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
				layout.setPadding(0, 0, 0, 0);
			}
		}
	}

	public static String parserNum(String string) {
		if (null == string || string.isEmpty()) return "";
		String str1 = string;
		str1 = new StringBuilder(str1).reverse().toString(); // 先将字符串颠倒顺序
		String str2 = "";
		for (int i = 0; i < str1.length(); i++) {
			if (i * 3 + 3 > str1.length()) {
				str2 += str1.substring(i * 3, str1.length());
				break;
			}
			str2 += str1.substring(i * 3, i * 3 + 3) + ",";
		}
		if (str2.endsWith(",")) {
			str2 = str2.substring(0, str2.length() - 1);
		}
		// 最后再将顺序反转过来
		str1 = new StringBuilder(str2).reverse().toString();
		return str1;
	}

	public static boolean isTopOfOtherActivities(Activity anActivity) {
		if (anActivity == null)
			return false;

		String activityName = anActivity.getClass().getName();
		ActivityManager m = (ActivityManager) anActivity.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> runningTaskInfoList = m.getRunningTasks(1);

		if (runningTaskInfoList != null && runningTaskInfoList.size() > 0) {
			RunningTaskInfo runningTaskInfo = runningTaskInfoList.get(0);
			if (runningTaskInfo.numActivities > 1) {
				ComponentName topActivity = runningTaskInfo.topActivity;
				String topName = topActivity.getClassName();
				if (topName.equalsIgnoreCase(activityName)) {
					return true;
				}
			}
		}

		return false;
	}

	public static void roteNinety(Context mContext, TextView textView) {
		RotateAnimation rotate = (RotateAnimation) AnimationUtils.loadAnimation(mContext, R.anim.rotate);
		textView.setAnimation(rotate);
	}
	
	public static String setMapSize(String mapAddress, int width, int height) {
		if (null == mapAddress) return ""; 
		int position = mapAddress.indexOf("size=") + 5;
		String newStr = mapAddress.substring(position);
		int newPosition = newStr.indexOf("&");
		String str = newStr.substring(0, newPosition);
		String iconSize = mapAddress.replaceAll(str, width + "x" + height);
		return iconSize;
	}
	
	public static String capitalizedType(String aString) {
		String capStr = aString;
		if (aString != null && aString.length() > 0) {
			capStr = aString.substring(0, 1).toUpperCase(Locale.ENGLISH);
			if (aString.length() > 1) {
				capStr += aString.substring(1);
			}
		}

		return capStr;
	}
	@SuppressLint("SimpleDateFormat")
	public static String formartTime(long time) {
		Date date = new Date(time);
		String fromatString = "MMM d,yyyy";
		SimpleDateFormat myFormat = new SimpleDateFormat(fromatString); 
	    return myFormat .format(date);
	}
	
	public static void hideSoftKeyBoard(Context mContext, Activity mActivity) {
		if (null == mContext || null == mActivity) return;
		InputMethodManager inputManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (null == inputManager || null == mActivity.getCurrentFocus() || null == mActivity.getCurrentFocus().getWindowToken()) return;
		inputManager.hideSoftInputFromWindow(mActivity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}
	
	public static void showSoftKeyBoard(int afterTime) {
		Timer timer = new Timer();
		timer.schedule(new TimerTask(){
		    @Override
		    public void run() {
		    	InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);  
		    	imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
		    }
		}, afterTime);
	}
	
	public static Context getContext() {
		return GageinApplication.getContext();
	}
	
	public static String urlEncodedString(String aString) {
		if (TextUtils.isEmpty(aString))
			return "";
		//TODO
//		if (aString.contains("&agentid=") && aString.contains("agentid")) {
//			return aString;
//		}
		
		try {
			aString = URLEncoder.encode(aString, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return aString;
	}
	
	/** max lengh for sn type*/
	public static int maxLengthForSnType(int aSnType) {
		switch (aSnType) {
		case APIHttpMetadata.kGGSnTypeLinkedIn:
			return 190;
			
		case APIHttpMetadata.kGGSnTypeFacebook:
			return 340;
			
		case APIHttpMetadata.kGGSnTypeTwitter:
			return 114;
			
		case APIHttpMetadata.kGGSnTypeSalesforce:
			return 390;
		}
		
		return 400;
	}
	
	@SuppressLint("NewApi")
	public static void sendSms(String smsText, Activity anActivity) {
		
		if (TextUtils.isEmpty(smsText) || anActivity == null) return;
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//At least KitKat
	        String defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(anActivity); //Need to change the build to API 19

	        Intent sendIntent = new Intent(Intent.ACTION_SEND);
	        sendIntent.setType("text/plain");
	        sendIntent.putExtra(Intent.EXTRA_TEXT, smsText);

	        if (defaultSmsPackageName != null) {//Can be null in case that there is no default, then the user would be able to choose any app that support this intent.
	            sendIntent.setPackage(defaultSmsPackageName);
	        }
	        anActivity.startActivity(sendIntent);

	    }
	    else {//For early versions, do what worked for you before.
	        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
	        sendIntent.setData(Uri.parse("sms:"));
	        sendIntent.putExtra("sms_body", smsText);
	        anActivity.startActivity(sendIntent);
	    }
	}
	
	public static void sendEmail(String aSubject, String aContent, Context aContext) {
		
		if (aContext == null || TextUtils.isEmpty(aContent))
			return;
		
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("plain/text");
		intent.putExtra(Intent.EXTRA_EMAIL, "emailaddress@emailaddress.com");
		intent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(aContent));
		intent.putExtra(Intent.EXTRA_SUBJECT, aSubject);
		aContext.startActivity(Intent.createChooser(intent, "Send Email"));
	}
	
	public static String getVersion(Context context) {// 获取版本号
		try {
			PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			return pi.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return "";
		}
	}
	
	public static void showLoadingMsgDialog(Context mContext,String msg) {
		mProgressDialog = new ProgressDialog(mContext);
		mProgressDialog.setMessage(msg);
		try {
			if (!mProgressDialog.isShowing()) mProgressDialog.show();
		} catch (Exception e) {
		}
	}
	
	public static void showLoadingDialog(Context mContext) {
		mProgressDialog = new ProgressDialog(mContext);
		mProgressDialog.setCanceledOnTouchOutside(false);
		mProgressDialog.setMessage(mContext.getResources().getString(R.string.loading));
		
		try {
			if (!mProgressDialog.isShowing()) mProgressDialog.show();
		} catch (Exception e) {
		}
	}
	
	public static void dissmissLoadingDialog() {
		try {
			mProgressDialog.dismiss();
		} catch (Exception e) {
		}
	}
	
	public static int getViewWith(View view) {
		int w = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
		int h = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
		view.measure(w, h);
		int width =view.getMeasuredWidth();
		return width;
	}
	
	public static int getViewHeight(View view) {
		int w = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
		int h = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
		view.measure(w, h);
		int height =view.getMeasuredHeight();
		return height;
	}
	
	public static int dip2px(Context context, float dpValue) { 
        final float scale = context.getResources().getDisplayMetrics().density; 
        return (int) (dpValue * scale + 0.5f);
	}
	
	public static void setListViewHeight(ListView listView) {
		ListAdapter adapter = listView.getAdapter();
		if (adapter == null) return;
		
		int totalHeight = 0; 
        for (int i = 0; i < adapter.getCount(); i++) {
            View listItem = adapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        } 
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (adapter.getCount() - 1)); 
        listView.setLayoutParams(params);
	}
	
	public static void setViewHeight(View view, int height) {
		 ViewGroup.LayoutParams params = view.getLayoutParams();
		 params.height = height; 
	     view.setLayoutParams(params);
	}
	
	public static void setFilterMaxWith(TextView textView) {
		ViewGroup.LayoutParams params = textView.getLayoutParams();
		int viewWith = getViewWith(textView);
		int screenWith = GageinApplication.getContext().getResources().getDisplayMetrics().widthPixels;
		Log.v("silen", "viewWith = " + viewWith);
		Log.v("silen", "screenWith = " + screenWith);
		if (viewWith > screenWith - 300) {
			params.width = screenWith - 200;
		} else {
			params.width = viewWith;
		}
		
		textView.setLayoutParams(params);
	}
	
	public static void resetFilters() {//TODO
		
		Constant.ALLWORDS_FOR_TRIGGERS = "";
//		Constant.EXACTWORDS = "";
//		Constant.ANYWORDS = "";
//		Constant.NONEWORDS = "";
		
		Filters mFilters = Constant.MFILTERS;
		
		//News Triggers
		if (mFilters == null) return;
		List<FilterItem> newsTriggers = mFilters.getNewsTriggers();
		for (int i = 0; i < newsTriggers.size(); i ++) {
			if (i == 0) {
				newsTriggers.get(i).setChecked(true);
			} else {
				newsTriggers.get(i).setChecked(false);
			}
		}
		
		//DateRanges
		List<FilterItem> dateRanges = mFilters.getDateRanges();
		for (int i = 0; i < dateRanges.size(); i ++) {
			if (i == 3) {
				dateRanges.get(i).setChecked(true);
			} else {
				dateRanges.get(i).setChecked(false);
			}
		}
		
		//Headquarters
//		List<Location> mHeadquarters = mFilters.getHeadquarters();
//		mHeadquarters.clear();
		
		//Industry
		List<Industry> mIndustries = mFilters.getIndustries();
		for (int i = 0; i < mIndustries.size(); i ++) {
			if (i == 0) {
				mIndustries.get(i).setChecked(true);
			} else {
				mIndustries.get(i).setChecked(false);
				for (int j = 0; j < mIndustries.get(i).getChildrens().size(); j ++) {
					if (j == 0) {
						mIndustries.get(i).getChildrens().get(0).setChecked(true);
					} else {
						mIndustries.get(i).getChildrens().get(j).setChecked(false);
					}
				}
			}
		}
		
		//Employee Size
		List<FilterItem> mEmployeeSizes = mFilters.getEmployeeSizeFromBuz();
		for (int i = 0; i < mEmployeeSizes.size(); i ++) {
			if (i == 0) {
				mEmployeeSizes.get(i).setChecked(true);
			} else {
				mEmployeeSizes.get(i).setChecked(false);
			}
		}
		
		//Revenue Size
		List<FilterItem> mSalesVolumes = mFilters.getSalesVolumeFromBuz();
		for (int i = 0; i < mSalesVolumes.size(); i ++) {
			if (i == 0) {
				mSalesVolumes.get(i).setChecked(true);
			} else {
				mSalesVolumes.get(i).setChecked(false);
			}
		}
		
		//Ownership
		List<FilterItem> mOwnerships = mFilters.getOwnerships();
		for (int i = 0; i < mOwnerships.size(); i ++) {
			if (i == 0) {
				mOwnerships.get(i).setChecked(true);
			} else {
				mOwnerships.get(i).setChecked(false);
			}
		}
		
		//Milestone
		List<FilterItem> mMilestones = mFilters.getMileStones();
		for (int i = 0; i < mMilestones.size(); i ++) {
			mMilestones.get(i).setChecked(false);
		}
		
		//Rank
		List<FilterItem> mRanks = mFilters.getRanks();
		for (int i = 0; i < mRanks.size(); i ++) {
			if (i == 0) {
				mRanks.get(i).setChecked(true);
			} else {
				mRanks.get(i).setChecked(false);
			}
		}
		
		//Fiscal Year End
		List<FilterItem> mFiscalYearEndMonths = mFilters.getFiscalYearEndMonths();
		for (int i = 0; i < mFiscalYearEndMonths.size(); i ++) {
			if (i == 0) {
				mFiscalYearEndMonths.get(i).setChecked(true);
			} else {
				mFiscalYearEndMonths.get(i).setChecked(false);
			}
		}
		
//		//Job Title
//		List<JobTitle> mJobTitles = mFilters.getJobTitles();
//		mJobTitles.clear();
		
		//Job Level
		List<FilterItem> mJobLevels = mFilters.getJobLevel();
		for (int i = 0; i < mJobLevels.size(); i ++) {
			if (i == 0) {
				mJobLevels.get(i).setChecked(true);
			} else {
				mJobLevels.get(i).setChecked(false);
			}
		}
		
//		//Location
//		List<Location> mLocations = mFilters.getLocations();
//		mLocations.clear();
		
		//Functional Role
		List<FilterItem> mFunctionalRoles = mFilters.getFunctionalRoles();
		for (int i = 0; i < mFunctionalRoles.size(); i ++) {
			if (i == 0) {
				mFunctionalRoles.get(i).setChecked(true);
			} else {
				mFunctionalRoles.get(i).setChecked(false);
			}
		}
		
		//Company types from People
		List<FilterItem> companyTypesFromPeople = mFilters.getCompanyTypesFromPeople();
		for (int i = 0; i < companyTypesFromPeople.size(); i ++) {
			if (i == 0) {
				companyTypesFromPeople.get(i).setChecked(true);
			} else {
				companyTypesFromPeople.get(i).setChecked(false);
			}
		}
		
		//Saved Companies
		List<FilterItem> savedCompanies = mFilters.getSavedCompanies();
		for (int i = 0; i < savedCompanies.size(); i ++) {
			if (i == 0) {
				savedCompanies.get(i).setChecked(true);
			} else {
				savedCompanies.get(i).setChecked(false);
			}
		}
		
		//Company types from Company
		List<FilterItem> companyTypesFromCompany = mFilters.getCompanyTypesFromCompany();
		for (int i = 0; i < companyTypesFromCompany.size(); i ++) {
			if (i == 0) {
				companyTypesFromCompany.get(i).setChecked(true);
			} else {
				companyTypesFromCompany.get(i).setChecked(false);
			}
		}
		
	}

	//Init Company Sort By
	public static void initBuzSortBy() {
		
		if (null == Constant.MFILTERS) return;
		
		List<FilterItem> sortBy = Constant.MFILTERS.getSortByFromBuz();
		for (int i = 0; i < sortBy.size(); i ++) {
			if (i == 0) {
				sortBy.get(i).setChecked(true);
			} else {
				sortBy.get(i).setChecked(false);
			}
		}
	}
	
	//Init Person Sort By
	public static void initConSortBy() {
		
		if (null == Constant.MFILTERS) return;
		
		List<FilterItem> sortBy = Constant.MFILTERS.getSortByFromContact();
		for (int i = 0; i < sortBy.size(); i ++) {
			if (i == 0) {
				sortBy.get(i).setChecked(true);
			} else {
				sortBy.get(i).setChecked(false);
			}
		}
	}
	
	/**
	 *  String List 中的 0 为 requestInfo , 1 为 是否选择了条件
	 *  needContainsSortBy search result 时需要传递sort by ， 保存时不需要
	 * @param isCompany
	 * @return
	 */
	public static List<String> packageRequestDataForCompanyOrPeople(Boolean isCompany, Boolean needContainsSortBy) {//TODO
		
		List<String> requestList = new ArrayList<String>();
		
		Boolean haveSelectCondition = false;
		
		Filters mFilters = Constant.MFILTERS;
		JSONObject jsonObject = new JSONObject();
		
		try {
			
			if (needContainsSortBy) {
				
				if (isCompany) {
					//Sort By From Buz
					List<FilterItem> sortByFromBuzList = mFilters.getSortByFromBuz();
					JSONArray sortByFromBuzArray = new JSONArray();
					for (int i = 0; i < sortByFromBuzList.size(); i ++) {
						if (sortByFromBuzList.get(i).getChecked()) {
							String key = sortByFromBuzList.get(i).getKey();
							sortByFromBuzArray.put(key);
							jsonObject.put("sortBy", key);
						}
					}
				} else {
					//Sort By From Con
					List<FilterItem> sortByFromConList = mFilters.getSortByFromContact();
					JSONArray sortByFromConArray = new JSONArray();
					for (int i = 0; i < sortByFromConList.size(); i ++) {
						if (sortByFromConList.get(i).getChecked()) {
							String key = sortByFromConList.get(i).getKey();
							sortByFromConArray.put(key);
							jsonObject.put("sortBy", key);
						}
					}
				}
				
				jsonObject.put("reverse", Constant.REVERSE);
				
			}
			

			//Job Title
			List<JobTitle> jobTitleList = mFilters.getJobTitles();
			
			for (int i = 0; i < jobTitleList.size(); i ++) {
				
				if (jobTitleList.get(i).getChecked()) {
					
					String name = jobTitleList.get(i).getName();
					jsonObject.put("dop_title", name);
					haveSelectCondition = true;
					
				}
			}
			
			//Job Level
			List<FilterItem> jobLevelList = mFilters.getJobLevel();
			JSONArray jobLevelArray = new JSONArray();
			for (int i = 0; i < jobLevelList.size(); i ++) {
				if (jobLevelList.get(i).getChecked()) {
					String name = jobLevelList.get(i).getValue();
					if (!name.equalsIgnoreCase("All")) {
						jobLevelArray.put(jobLevelList.get(i).getKey());
					}
				}
			}
			if (jobLevelArray.length() > 0) {
				jsonObject.put("dop_job_level", jobLevelArray);
				haveSelectCondition = true;
			}
			

			//People Location Code 
			List<Location> locationList = mFilters.getLocations();
			JSONArray locationArray = new JSONArray();
			for (int i = 0; i < locationList.size(); i ++) {
				if (locationList.get(i).getChecked()) {
					locationArray.put(locationList.get(i).getCode());
				}
			}
			if (locationArray.length() > 0) {
				jsonObject.put("people_location_code", locationArray);
				haveSelectCondition = true;
			}
			
			//Functional Role
			List<FilterItem> funcationRoleList = mFilters.getFunctionalRoles();
			JSONArray funcationRoleArray = new JSONArray();
			for (int i = 0; i < funcationRoleList.size(); i ++) {
				if (funcationRoleList.get(i).getChecked()) {
					String name = funcationRoleList.get(i).getValue();
					String key = funcationRoleList.get(i).getKey();
					if (!name.equalsIgnoreCase("All")) {
						funcationRoleArray.put(key);
					}
				}
			}
			if (funcationRoleArray.length() > 0) {
				jsonObject.put("dop_functional_role", funcationRoleArray);
				haveSelectCondition = true;
			}
			
			//News Triggers
			List<FilterItem> mDateRanges;
			List<FilterItem> mNewsTriggers;
			String dateRange = "";
			mDateRanges = mFilters.getDateRanges();
			if (!TextUtils.isEmpty(Constant.ALLWORDS_FOR_TRIGGERS)) {
				String searchKeywords;
				searchKeywords = Constant.ALLWORDS_FOR_TRIGGERS;
//				if (!TextUtils.isEmpty(Constant.EXACTWORDS)) {
//					searchKeywords = searchKeywords + " " + "\"" + Constant.EXACTWORDS + "\"";
//				} 
//				if (!TextUtils.isEmpty(Constant.ANYWORDS)) {
//					searchKeywords = searchKeywords + " " + "(" + Constant.ANYWORDS + ")";
//				} 
//				
//				if (!TextUtils.isEmpty(Constant.NONEWORDS.trim())) {
//					String noneWords = Constant.NONEWORDS.trim();
//					String[] result = noneWords.split("\\s+");
//					for (int i = 0; i < result.length; i++) {
//						String word = result[i];
//						searchKeywords = searchKeywords + " " + "-" + word;
//					}
//					
//					noneWords.replace(" ", " -");//TODO 如果遇到两个空格怎么办
//					searchKeywords = searchKeywords + " " + "-" + noneWords;
//				}
				Log.v("silen", "keyword = " + searchKeywords);
				jsonObject.put("event_search_keywords", searchKeywords);
				haveSelectCondition = true;
				
				for (int i = 0; i < mDateRanges.size(); i ++) {
					if (mDateRanges.get(i).getChecked()) {
						dateRange = mDateRanges.get(i).getKey();
					}
				}
				jsonObject.put("search_date_range", dateRange);
			} else {
				mNewsTriggers = mFilters.getNewsTriggers();
				JSONArray newsTriggersArray = new JSONArray();
				String key = "";
				for (int i = 0; i < mNewsTriggers.size(); i ++) {
					if (mNewsTriggers.get(i).getChecked()) {
						key = mNewsTriggers.get(i).getKey();
						if (!key.isEmpty()) {
							newsTriggersArray.put(key);
						}
					}
				}
				if (newsTriggersArray.length() > 0) {
					jsonObject.put("mer_for_id", newsTriggersArray);
					
					for (int i = 0; i < mDateRanges.size(); i ++) {
						if (mDateRanges.get(i).getChecked()) {
							dateRange = mDateRanges.get(i).getKey();
						}
					}
					jsonObject.put("search_date_range", dateRange);
				}
			}

			//search_company_for_type
			List<FilterItem> searchCompanyForTypes = isCompany ? mFilters.getCompanyTypesFromCompany() : mFilters.getCompanyTypesFromPeople();
			
			for (int i = 0; i < searchCompanyForTypes.size(); i ++) {
				
				if (searchCompanyForTypes.get(i).getChecked()) {
					
					String key = searchCompanyForTypes.get(i).getKey();
					jsonObject.put("search_company_for_type", key);
					
					Log.v("silen", "key = " + key);
					
					if (key.equalsIgnoreCase("1")) {//specific
						
						Log.v("silen", "Constant.COMPANY_SEARCH_KEYWORDS = " + Constant.COMPANY_SEARCH_KEYWORDS);
						jsonObject.put("company_search_keywords", Constant.COMPANY_SEARCH_KEYWORDS);
						
					} else if (key.equalsIgnoreCase("3")) {//saved companies search
						
						if (!isCompany) {
							
							List<FilterItem> savedCompanies = mFilters.getSavedCompanies();
							for (int k = 0; k < savedCompanies.size(); k ++) {
								
								if (savedCompanies.get(k).getChecked()) {
									
									String savedCompanyId = savedCompanies.get(k).getKey();
									jsonObject.put("filter_saved_company_search", savedCompanyId);
									
								}
							}
							
						}
						
					}//TODO
				}
			}
			
			//TODO
			//filter_saved_company_search
//			if (!TextUtils.isEmpty(Constant.FILTER_SAVED_COMPANY_SEARCH_NAME)) {
//				jsonObject.put("filter_saved_company_search", Constant.FILTER_SAVED_COMPANY_SEARCH_ID);
//			}
			
			//Headquarters
			List<Location> mHeadquarters = mFilters.getHeadquarters();
			JSONArray headquarterArray = new JSONArray();
			for (int i = 0; i < mHeadquarters.size(); i ++) {
				if (mHeadquarters.get(i).getChecked()) {
					String code = mHeadquarters.get(i).getCode();
					headquarterArray.put(code);
				}
			}
			if (headquarterArray.length() > 0) {
				jsonObject.put("location_code", headquarterArray);
				haveSelectCondition = true;
			}
			
			
			//TODO
			//Industry
			List<Industry> mIndustries = mFilters.getIndustries();
			List<Industry> mIndustriesChildren;
			int industryNum = 0;
			JSONArray industryArray = new JSONArray();
			for (int i = 0; i < mIndustries.size(); i ++) {
				if (mIndustries.get(i).getChecked()) {
					String id = mIndustries.get(i).getId();
					if (!id.isEmpty()) {
						industryNum ++;
						industryArray.put(id);
					}
				}
			}
			if (industryArray.length() > 0) {
				jsonObject.put("org_industries", industryArray);
				haveSelectCondition = true;
			}
			if (industryNum == 1) {//有子公司
				JSONArray industryChildrenArray = new JSONArray();
				for (int i = 0; i < mIndustries.size(); i ++) {
					if (mIndustries.get(i).getChecked()) {
						mIndustriesChildren = mIndustries.get(i).getChildrens();
						if (null != mIndustriesChildren) {
							for (int j = 0; j < mIndustriesChildren.size(); j ++) {
								if (mIndustriesChildren.get(j).getChecked()) {
									String id = mIndustriesChildren.get(j).getId();
									if (!id.isEmpty()) {
										industryChildrenArray.put(id);
									}
								}
							}
						}
					}
				}
				if (industryChildrenArray.length() > 0) {
					jsonObject.put("org_sub_industries", industryChildrenArray);
					haveSelectCondition = true;
				}
			}
			
			//Employee Size
			List<FilterItem> mEmployeeSizes = mFilters.getEmployeeSizeFromBuz();
			JSONArray employeeArray = new JSONArray();
			for (int i = 0; i < mEmployeeSizes.size(); i ++) {
				if (mEmployeeSizes.get(i).getChecked()) {
					String key = mEmployeeSizes.get(i).getKey();
					if (!key.isEmpty()) {
						employeeArray.put(key);
					}
				}
			}
			if (employeeArray.length() > 0) {
				jsonObject.put("org_employee_size", employeeArray);
				haveSelectCondition = true;
			}
			
			//Revenue Size
			List<FilterItem> mSalesVolumes = mFilters.getSalesVolumeFromBuz();
			JSONArray mSalesVolumeArray = new JSONArray();
			for (int i = 0; i < mSalesVolumes.size(); i ++) {
				if (mSalesVolumes.get(i).getChecked()) {
					String key = mSalesVolumes.get(i).getKey();
					if (!key.isEmpty()) {
						mSalesVolumeArray.put(key);
					}
				}
			}
			if (mSalesVolumeArray.length() > 0) {
				jsonObject.put("org_revenue_size", mSalesVolumeArray);
				haveSelectCondition = true;
			}
			
			//Ownership
			List<FilterItem> mOwnerships = mFilters.getOwnerships();
			JSONArray mOwnershipsArray = new JSONArray();
			for (int i = 0; i < mOwnerships.size(); i ++) {
				if (mOwnerships.get(i).getChecked()) {
					String key = mOwnerships.get(i).getKey();
					if (!key.isEmpty()) {
						mOwnershipsArray.put(key);
					}
				}
			}
			if (mOwnershipsArray.length() > 0) {
				jsonObject.put("org_ownership", mOwnershipsArray);
				haveSelectCondition = true;
			}
			
			//Milestone
			List<FilterItem> mMilestones = mFilters.getMileStones();
			List<FilterItem> milestoneDateRanks = mFilters.getMileStoneDateRange();
			JSONArray mMilestonesArray = new JSONArray();
			JSONArray milestoneDateRanksArray = new JSONArray();
			Boolean haveSelectMilestone = false;
			for (int i = 0; i < mMilestones.size(); i ++) {
				if (mMilestones.get(i).getChecked()) {
					haveSelectMilestone = true;
					String key = mMilestones.get(i).getKey();
					if (!key.isEmpty()) {
						mMilestonesArray.put(key);
					}
				}
			}
			
			if (haveSelectMilestone) {
				for (int i = 0; i < milestoneDateRanks.size(); i ++) {
					if (milestoneDateRanks.get(i).getChecked()) {
						String key = milestoneDateRanks.get(i).getKey();
						milestoneDateRanksArray.put(key);
					}
				}
			}
			
			if (milestoneDateRanksArray.length() > 0) {
				haveSelectCondition = true;
				jsonObject.put("milestone_occurrence_type", milestoneDateRanksArray);
				if (mMilestonesArray.length() > 0) {
					jsonObject.put("milestone_type", mMilestonesArray);
				}
			}
			
			//Rank
			List<FilterItem> mRanks = mFilters.getRanks();
			JSONArray mRanksArray = new JSONArray();
			for (int i = 0; i < mRanks.size(); i ++) {
				if (mRanks.get(i).getChecked()) {
					String key = mRanks.get(i).getKey();
					if (!key.isEmpty()) {
						mRanksArray.put(key);
					}
				}
			}
			if (mRanksArray.length() > 0) {
				jsonObject.put("rank", mRanksArray);
				haveSelectCondition = true;
			}
			
			//Fiscal Year End
			List<FilterItem> mFiscalYearEndMonths = mFilters.getFiscalYearEndMonths();
			JSONArray mFiscalYearEndMonthsArray = new JSONArray();
			for (int i = 0; i < mFiscalYearEndMonths.size(); i ++) {
				if (mFiscalYearEndMonths.get(i).getChecked()) {
					String key = mFiscalYearEndMonths.get(i).getKey();
					if (!key.isEmpty()) {
						mFiscalYearEndMonthsArray.put(key);
					}
				}
			}
			if (mFiscalYearEndMonthsArray.length() > 0) {
				jsonObject.put("org_fiscal_month", mFiscalYearEndMonthsArray);
				haveSelectCondition = true;
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		Log.v("silen", "RequestData = " + jsonObject.toString());
		
		requestList.add(jsonObject.toString());
		requestList.add(haveSelectCondition + "");
		
		return requestList;
	}
	
	public static void setLayoutWith(LinearLayout layout, FragmentActivity activity) {
		LinearLayout.LayoutParams layoutParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		layoutParams.width = CommonUtil.getDeviceWidth(activity);
		layout.setLayoutParams(layoutParams);
	}
	
	/**
	 * remove 'www.' from website
	 * @param website
	 * @return
	 */
	public static String removeWebsite3W(String website) {
		if (website.isEmpty()) return "";
		if (website.length() > 4) {
			if (website.substring(0, 4).equalsIgnoreCase("www.")) {
				website = website.substring(4, website.length());
			}
		}
		return website;
	}
	
	public static String jointPersonNameAndCompanyNameAndJobTitle(String name, String companyName, String title) {
		String result = "";
		if (!name.isEmpty()) {
			result += "";
			result += name;
		}
		if (!companyName.isEmpty()) {
			result += " ";
			result += companyName;
		}
		if (!title.isEmpty()) {
			result += " ";
			result += title;
		}
		
		result = "https://www.google.com/search?q=" + urlEncodedString(result);
		return result;
	}
	
	public static void showPromotDialog(Context mContext, int promotStringId) {
		PromotDialog dialog = new PromotDialog(mContext);
		dialog.showDialog(CommonUtil.stringFromResID(promotStringId));
		dialog.setCancelable(false);
	}
	
	//TODO
	public static void getContactNameForWebsite(String website) {
		
	}
	
	/*
     * 读取联系人的信息
     */
    public static List<Contact> readAllContacts() {
    	
    	List<Contact> contacts = new ArrayList<Contact>();
    	
        Cursor cursor = GageinApplication.getContext().getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, 
                 null, null, null, null);
        int contactIdIndex = 0;
        int nameIndex = 0;
        
        if(cursor.getCount() > 0) {
            contactIdIndex = cursor.getColumnIndex(ContactsContract.Contacts._ID);
            nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
        }
        while(cursor.moveToNext()) {
        	
        	Contact contact = new Contact();
        	
            String contactId = cursor.getString(contactIdIndex);
            String name = cursor.getString(nameIndex);
            Log.v("silen", "name = " + name);
            
            contact.setName(name);
            
            /*
             * 查找该联系人的email信息
             */
            Cursor emailsCursor = GageinApplication.getContext().getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, 
                    null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + "=" + contactId, null, null);
            int emailIndex = 0;
            if(emailsCursor.getCount() > 0) {
                emailIndex = emailsCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA);
            }
            List<String> emails = new ArrayList<String>();
            while(emailsCursor.moveToNext()) {
                String email = emailsCursor.getString(emailIndex);
                Log.v("silen", "email = " + email);
                if (email.contains("@")) {
                	email = email.substring(email.indexOf("@") + 1);
                	emails.add(email);
                }
            }
            
            contact.setEmails(emails);
            
            contacts.add(contact);
        }
        
        Constant.contacts = contacts;
        
        return contacts;
        
    }
	
	public static void setDialogWith(Dialog dialog) {
		
		Window dialogWindow = dialog.getWindow();
	    WindowManager.LayoutParams lp = dialogWindow.getAttributes();
	    int width = 0;
	    
	    if (CommonUtil.isTablet(GageinApplication.getContext())) {
	    	width = 600;
	    } else {
	    	width = GageinApplication.getContext().getResources().getDisplayMetrics().widthPixels - 200;
	    }
	    
	    lp.width = width;
	}
	
	public static void showDialogFromBottom(Dialog dialog) {
		
		Window window = dialog.getWindow();
		window.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		window.setGravity(Gravity.BOTTOM);
		window.setWindowAnimations(R.style.dialog_animation);
		
	}
	
	public static void sendSimpleBroadcast(Context mContext, String action) {
		
		Intent intent = new Intent();
		intent.setAction(action);
		mContext.sendBroadcast(intent);
		
	}
	
	public static Boolean ifExistSameSavedSearchName(String savedName, String searchType) {
		
		for (int i = 0; i < Constant.locationSavedSearchs.size(); i ++) {
			SavedSearch savedSearch = Constant.locationSavedSearchs.get(i);
			if (savedSearch.getType().equalsIgnoreCase(searchType)) {
				if (savedSearch.getName().equalsIgnoreCase(savedName)) {
					return true;
				}
			}
		}
		
		return false;
		
	}
	
	public static Boolean isLinkedCompanies(Group group) {
		
		long groupId = 0;
		if (!TextUtils.isEmpty(group.getMogid())) groupId = Long.parseLong(group.getMogid());
		int followLinkType = group.getFollowLinkType();
		if (groupId == APIHttpMetadata.LinkedCompaniesGroupId && followLinkType == APIHttpMetadata.LinkedCompaniesFollowLinkType) {
			return true;
		} else {
			return false;
		}
		
	}
	
}

