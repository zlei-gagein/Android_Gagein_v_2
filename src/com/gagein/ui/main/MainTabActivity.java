package com.gagein.ui.main;

import java.util.List;

import org.json.JSONObject;

import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.gagein.R;
import com.gagein.http.APIHttp;
import com.gagein.http.APIHttpMetadata;
import com.gagein.http.APIParser;
import com.gagein.ui.companies.GroupsActivity;
import com.gagein.ui.news.NewsActivity;
import com.gagein.ui.scores.ScoresActivity;
import com.gagein.ui.search.SearchActivity;
import com.gagein.ui.settings.SettingsActivity;
import com.gagein.ui.tablet.news.NewsTabletActivity;
import com.gagein.ui.tablet.scores.ScoresTabletActivity;
import com.gagein.ui.tablet.settins.SettingsTabletActivity;
import com.gagein.util.CommonUtil;
import com.gagein.util.Constant;
import com.gagein.util.Log;
import com.gagein.util.MessageCode;
import com.gagein.util.RuntimeData;

/**
 * Tabhost controll page
 * 
 * @author silen
 * 
 */

@SuppressWarnings("deprecation")
public class MainTabActivity extends TabActivity implements OnClickListener {

	public static final int SNAP_VELOCITY = 300;
	private RelativeLayout layout_tab; 
	private TabHost tabHost;
	private Context mContext;
	private APIHttp mApiHttp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		mApiHttp = new APIHttp(mContext);
		
		CommonUtil.addActivity(this);
		
		setContentView(R.layout.main_tab_layout);

		initView();
		registerBroadcast();
		getSocialNetworkTypes();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (CommonUtil.isTablet(mContext)) {
			int minWith = CommonUtil.getDeviceWidth(MainTabActivity.this) >= CommonUtil
					.getDeviceHeight(MainTabActivity.this) ? CommonUtil
					.getDeviceHeight(MainTabActivity.this) : CommonUtil
					.getDeviceWidth(MainTabActivity.this);

			int paddingLeft = minWith / 5;
			layout_tab.setPadding(paddingLeft, 0, paddingLeft, 0);
		}
	}
	
	/**
	 * init View
	 */
	public static final String TAB_ID_SCORES = "SCORES";
	public static final String TAB_ID_NEWS = "NEWS";
	public static final String TAB_ID_COMPANIES = "COMPANIES";
	public static final String TAB_ID_SEARCH = "SEARCH";
	public static final String TAB_ID_SETTINGS = "SETTINGS";
	
	private void initView() {
		tabHost = getTabHost();
		
		setupTab(mContext.getString(R.string.u_scores), TAB_ID_SCORES,
				new Intent(mContext, CommonUtil.isTablet(mContext) ? ScoresTabletActivity.class : ScoresActivity.class));
		
		setupTab(mContext.getString(R.string.u_news), TAB_ID_NEWS,
				new Intent(mContext, CommonUtil.isTablet(mContext) ? NewsTabletActivity.class : NewsActivity.class));
		
		setupTab(mContext.getString(R.string.u_companies), TAB_ID_COMPANIES, 
				new Intent(mContext, CommonUtil.isTablet(mContext) ? GroupsActivity.class : GroupsActivity.class));
		
		setupTab(mContext.getString(R.string.u_search), TAB_ID_SEARCH, new Intent(mContext, SearchActivity.class));
		
		setupTab(mContext.getString(R.string.u_settings), TAB_ID_SETTINGS, 
				new Intent(mContext, CommonUtil.isTablet(mContext) ? SettingsTabletActivity.class : SettingsActivity.class));
		
		layout_tab = (RelativeLayout) findViewById(R.id.layout_tab);
		
		getTabHost().setOnTabChangedListener(new OnTabChangeListener() {

			@Override
			public void onTabChanged(String tabId) {
				
				if (tabId.equalsIgnoreCase(TAB_ID_COMPANIES)) {
					
					CommonUtil.sendSimpleBroadcast(mContext, Constant.BROADCAST_REFRESH_GROUPSACTIVITY);
					
				} else if (tabId.equalsIgnoreCase(TAB_ID_SEARCH)) {
					
					CommonUtil.sendSimpleBroadcast(mContext, Constant.BROADCAST_GET_SAVED_SEARCH);
					CommonUtil.showSoftKeyBoard(100);
					return;
					
				}
					
				CommonUtil.hideSoftKeyBoard(mContext, MainTabActivity.this);
				
			}
		});
		
		billingGetInfo();
	}

	private void setupTab(String name, String tag, Intent intent) {
		tabHost.addTab(tabHost.newTabSpec(tag).setIndicator(getView(name)).setContent(intent));
	}
	
	private Boolean expired = false;
	private Boolean isAssigned = false;
	private Boolean inTrial = false;
	private Boolean isTeam = false;
	private Boolean isOwner = false;
	
	private void billingGetInfo() {
		
		mApiHttp.billingGetInfo(new Listener<JSONObject>() {
			
			@Override
			public void onResponse(JSONObject jsonObject) {
				
				APIParser parser = new APIParser(jsonObject);
				if (parser.isOK()) {
					
					expired = parser.data().optInt("plan_expired_flag") == 0 ? false : true;
					isAssigned = parser.data().optInt("is_assigned") == 0 ? false : true;
					inTrial = parser.data().optInt("is_trial") == 0 ? false : true;
					isTeam = parser.data().optInt("is_team") == 0 ? false : true;
					isOwner = parser.data().optInt("is_owner") == 0 ? false : true;
					
				}
				
				int billingStatus = getBillingStatus(parser.messageCode());
				Log.v("silen", "billingStatus = " + billingStatus);
				
				if (billingStatus == APIHttpMetadata.kBillingStatusNotPurchased || billingStatus == APIHttpMetadata.kBillingStatusPurchaseExpired
						|| billingStatus == APIHttpMetadata.kBillingStatusSeatRemoved) {
					Intent intent = new Intent();
					intent.setClass(mContext, BillingFailedActivity.class);
					intent.putExtra(Constant.BILLINGSTATUS, billingStatus);
					startActivity(intent);
					
					//清除token
					CommonUtil.clearLoginInfo(mContext);
					CommonUtil.removeActivity();
				}
				
			}
		}, new Response.ErrorListener() {
			
			@Override
			public void onErrorResponse(VolleyError error) {
				billingGetInfo();
			}
		});
	}
	
	private Boolean isAvailable() {
		return !expired && isAssigned;
	}
	
	private int getBillingStatus(int messageCode) {
		
		if (messageCode == MessageCode.MESSAGE_CODE_START_TRIAL) {
	        return APIHttpMetadata.kBillingStatusNotStarted;
	    } else {
	        if (inTrial) {
	            
	            if (isAvailable()) {
	                return APIHttpMetadata.kBillingStatusInTrail;
	            } else {
	                return APIHttpMetadata.kBillingStatusNotPurchased;
	            }
	            
	        } else {
	            
	            if (isAvailable()) {
	                return APIHttpMetadata.kBillingStatusPurchaseInEffect;
	            } else {
	                
	                if (isAssigned) {
	                	if (isTeam && !isOwner) {
	                		return APIHttpMetadata.kBillingStatusTeamMemberExpired;
	                	} else {
	                		return APIHttpMetadata.kBillingStatusPurchaseExpired;
	                	}
	                } else {
	                   return APIHttpMetadata.kBillingStatusSeatRemoved;
	                }
	            }
	        }
	    }
		
	}

	/**
	 * set stye
	 * 
	 * @param name
	 * @param imageId
	 * @return
	 */
	private View getView(String name) {
		View view = (View) LayoutInflater.from(this).inflate(R.layout.main_tab_item, null);
		TextView tab_text = (TextView) view.findViewById(R.id.tab_text);
		tab_text.setText(name);
		return view;
	}

	/**
	 * 注册广播
	 */
	public void registerBroadcast() {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Constant.EXPLORE);
		mContext.registerReceiver(stratExploreReceiver, intentFilter);
	}

	/**
	 * 启动Explore页广播
	 */
	public BroadcastReceiver stratExploreReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (Constant.EXPLORE.equals(action)) {
			}
		}
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(stratExploreReceiver);
		mApiHttp.stop();
		Constant.MFILTERS = null;
		Constant.locationNewsTriggers.clear();
		Constant.locationNewsTriggersForCompany.clear();
		Constant.selectedGroupFilter.clear();
		Constant.locationSavedSearchs.clear();
	}

	/** get social http stype */
	private void getSocialNetworkTypes() {
		mApiHttp.snGetList(new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject jsonObject) {

				APIParser parser = new APIParser(jsonObject);
				if (parser.isOK()) {
					List<String> snTypes = RuntimeData.sharedInstance().getSnTypes();
					snTypes.clear();
					snTypes.addAll(parser.parseSnGetList());
				}
			}
			
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				CommonUtil.dissmissLoadingDialog();
				CommonUtil.showLongToast(mContext.getResources().getString(R.string.connection_error));
			}
		});
	}

	@Override
	public void onClick(View v) {
	}
}
