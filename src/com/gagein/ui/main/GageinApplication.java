package com.gagein.ui.main;

import org.json.JSONObject;

import android.app.Application;
import android.content.Context;
import cn.sharesdk.framework.ShareSDK;

import com.gagein.R;
import com.gagein.util.ApplicationUrl;
import com.gagein.util.CommonUtil;
import com.gagein.util.Constant;
import com.gagein.util.Log;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.L;

public class GageinApplication extends Application {
	private static Context mContext;

	public static Context getContext() {
		return mContext;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mContext = this;
		initImageLoader(this);
		Log.init(true);
		ShareSDK.initSDK(this);
		Constant.APP_VERSION_NAME = CommonUtil.getVersion(mContext);
		try {
			initUrl();
		} catch (Exception e) {
			Log.e("GageInApplication", mContext.getResources().getString(R.string.init_url_fail));
		}
	}
	
	/**
	 * init URL
	 */
	private void initUrl() throws Exception {
		String strJson = "";
		strJson = CommonUtil.parseRawToString(R.raw.url, mContext);

		JSONObject jsonObject = null;
		jsonObject = new JSONObject(strJson);
		
		ApplicationUrl.ProductionURL = jsonObject.optString("productionURL", "");
		ApplicationUrl.CnURL = jsonObject.optString("cnURL", "");
		ApplicationUrl.CustomURL = jsonObject.optString("customURL", "");
		ApplicationUrl.DemoURL = jsonObject.optString("demoURL", "");
		ApplicationUrl.StagingURL = jsonObject.optString("stagingURL", "");
		ApplicationUrl.Q2URL = jsonObject.optString("q2URL", "");
	}
	
	public static void initImageLoader(Context context) {
		// Create default options which will be used for every
		// displayImage(...) call if no options will be passed to this method
		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisc(true).build();

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context).threadPriority(Thread.NORM_PRIORITY - 2)
				.tasksProcessingOrder(QueueProcessingType.LIFO).defaultDisplayImageOptions(defaultOptions)
				// .writeDebugLogs() // Remove for release app
				.build();

		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
		L.disableLogging();
	}

}
