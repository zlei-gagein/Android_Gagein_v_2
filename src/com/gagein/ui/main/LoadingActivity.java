package com.gagein.ui.main;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.gagein.R;
import com.gagein.http.APIParser;
import com.gagein.util.CommonUtil;
import com.gagein.util.Constant;

/**
 * 启动页，该页面用于数据初始化和展示作用
 * 数据初始化：判断用户是否登录（如果已登录就直接带着token跳到home页，否则跳到loginstyle）  
 * @author silen
 *
 */
public class LoadingActivity extends BaseActivity {
	
	private Timer timer;
	private TimerTask timerTask;
	private int TIME = 1000;
	private int TIME_MESSAGE = 0;
	private SharedPreferences shPreferences_firstLauncher; 
	private Boolean boolean_firstLauncher;
	private Editor editor_firstLauncher;
	private String token;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loading);
		
		//获取高级搜索选项
		getFilter();
		
		ifFirstLaunch();
		setFixedTimeJump();
	}
	
	/**
	 * 是否第一次启动AP
	 */
	private void ifFirstLaunch() {
		shPreferences_firstLauncher = mContext.getSharedPreferences("FIRST", MODE_PRIVATE);
		boolean_firstLauncher = shPreferences_firstLauncher.getBoolean("first", true);
		
		//保存启动痕迹
		editor_firstLauncher = shPreferences_firstLauncher.edit();
		editor_firstLauncher.putBoolean("first", false);
		editor_firstLauncher.commit();
	}

	/**
	 * 设置固定时间跳转页面
	 */
	private void setFixedTimeJump() {
		timer = new Timer();
		timerTask = new TimerTask() {
			
			@Override
			public void run() {
				handler.sendEmptyMessage(TIME_MESSAGE);
			}
		};
		timer.schedule(timerTask, TIME);
	}
	
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (TIME_MESSAGE == msg.what) {
				if (timer != null) {
					timer.cancel();
				}
				
				if (boolean_firstLauncher) {
					//跳转到导航页
					startActivitySimple(NavigationActivity.class);
				} else {
					// 判断是否已经登录
					//跳转到loginstyle页
					if (ifHaveToken()) {
						startActivitySimple(MainTabActivity.class);
					} else {
						startActivitySimple(NavigationActivity.class);
					}
				}
				finish();
			}
		}
	};
	
	/**
	 * 是否含有token
	 */
	private Boolean ifHaveToken() {
		token = CommonUtil.getToken(mContext);
		if ("".equals(token)) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (timer != null) {
			timer.cancel();
		}
	}
	
//	private void getFilter() {
//		mApiHttp.getFilter(new Listener<JSONObject>() {
//
//			@Override
//			public void onResponse(JSONObject jsonObject) {
//				APIParser parser = new APIParser(jsonObject);
//				if (parser.isOK()) {
//					Constant.MFILTERS = parser.parserFilters();
//				}
//			}
//		}, new Response.ErrorListener() {
//
//			@Override
//			public void onErrorResponse(VolleyError error) {
//			}
//		});
//	}
	
}
