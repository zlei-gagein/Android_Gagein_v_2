package com.gagein.ui.main;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.gagein.R;
import com.gagein.component.MenuItem;
import com.gagein.component.PopupMenu;
import com.gagein.http.APIHttpMetadata;
import com.gagein.http.APIParser;
import com.gagein.model.AutoLoginInfo;
import com.gagein.model.Member;
import com.gagein.model.PlanInfo;
import com.gagein.model.SnUserInfo;
import com.gagein.util.CommonUtil;
import com.gagein.util.Log;
import com.gagein.util.Utils;
import com.gagein.util.oauth.OAuthHandler;
import com.gagein.util.oauth.OAuther;

public class LoginActivity extends BaseActivity {
	
	private Button loginBtn;
	private EditText emailEdt;
	private EditText passwordEdt;
	private String email;
	private String password;
	private ImageView salesforceBtn;
	private ImageView facebookBtn;
	private ImageView twitterBtn;
	private OAuthHandler authHandler;
	private OAuther oauther;
	private BroadcastReceiver authReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		doInit();
	}
	
	@Override
	protected void initView() {
		super.initView();
		setTitle(R.string.login);
		
		loginBtn = (Button) findViewById(R.id.loginBtn);
		emailEdt = (EditText) findViewById(R.id.emailEdt);
		passwordEdt = (EditText) findViewById(R.id.passwordEdt);
		salesforceBtn = (ImageView) findViewById(R.id.salesforceBtn);
		facebookBtn = (ImageView) findViewById(R.id.facebookBtn);
		twitterBtn = (ImageView) findViewById(R.id.twitterBtn);
	}
	
	@Override
	protected void initData() {
		super.initData();
		
		if (ifHaveToken()) {
			startActivitySimple(MainTabActivity.class);
			finish();
		}
		
		authHandler = new OAuthHandler(this, "");
		oauther = new OAuther(this);
		
		authReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				
				final OAuthHandler.OAuthHandlerData data = (OAuthHandler.OAuthHandlerData)intent.getSerializableExtra(OAuthHandler.KEY_INTENT_DATA);
				
				if (data !=null && data.snName.equalsIgnoreCase(authHandler.data.snName) && !TextUtils.isEmpty(data.accessToken)) {
					final int snType = CommonUtil.typeFromSnName(data.snName);
					Log.v("silen", "snType = " + snType);
					switch (snType) {
						case APIHttpMetadata.kGGSnTypeLinkedIn: {
							CommonUtil.showLoadingMsgDialog(mContext, "Accessing data from LinkedIn  ");
							mApiHttp.snGetUserInfoLinkedIn(data.accessToken, data.secret, new Listener<JSONObject>() {

								@Override
								public void onResponse(JSONObject jsonObject) {
									
									handleSnUserInfoRetrieved(data, snType, jsonObject);
									dismissLoadingDialog();
								}
								
							}, new Response.ErrorListener() {

								@Override
								public void onErrorResponse(VolleyError error) {
									showConnectionError();
								}
							});
							
						}
						break;
						
						case APIHttpMetadata.kGGSnTypeSalesforce: {
							
							// defensive codes
							if (TextUtils.isEmpty(data.openId)) return;
							int slashIndex = data.openId.lastIndexOf("/");
							
							// extract account id
							String accountId = data.openId.substring(slashIndex + 1);
							
							// call API to get user info
							CommonUtil.showLoadingMsgDialog(mContext, "Accessing data from Salesforce  ");
							mApiHttp.snGetUserInfoSalesforce(data.accessToken, accountId, data.refreshToken, data.instanceUrl, new Listener<JSONObject>() {

								@Override
								public void onResponse(JSONObject jsonObject) {
									handleSnUserInfoRetrieved(data, snType, jsonObject);
									dismissLoadingDialog();
								}
								
							}, new Response.ErrorListener() {

								@Override
								public void onErrorResponse(VolleyError error) {
									showConnectionError();
								}
							});
						}
						break;
						
						case APIHttpMetadata.kGGSnTypeFacebook: {
							CommonUtil.showLoadingMsgDialog(mContext, "Accessing data from Facebook  ");
							mApiHttp.snGetUserInfoFacebook(data.accessToken, new Listener<JSONObject>() {

								@Override
								public void onResponse(JSONObject jsonObject) {
									handleSnUserInfoRetrieved(data, snType, jsonObject);
									dismissLoadingDialog();
								}
								
							}, new Response.ErrorListener() {

								@Override
								public void onErrorResponse(VolleyError error) {
									showConnectionError();
								}
							});
						}
						break;
						
						case APIHttpMetadata.kGGSnTypeTwitter: {
							CommonUtil.showLoadingMsgDialog(mContext, "Accessing data from Twitter  ");
							mApiHttp.snGetUserInfoTwitter(data.accessToken, data.secret, new Listener<JSONObject>() {

								@Override
								public void onResponse(JSONObject jsonObject) {
									handleSnUserInfoRetrieved(data, snType, jsonObject);
									dismissLoadingDialog();
								}
								
							}, new Response.ErrorListener() {

								@Override
								public void onErrorResponse(VolleyError error) {
									showConnectionError();
								}
							});
						}
						break;
					}
				}
			}

			/** handle after sn saved to back-end */
			private void handleSnUserInfoRetrieved(final OAuthHandler.OAuthHandlerData data, final int snType, JSONObject jsonObject) {
				APIParser parser = new APIParser(jsonObject);
				if (parser.isOK()) {
					
					// parse the userInfo....
					SnUserInfo info = parser.parseSnGetUserInfo();
					if (info != null) {
						info.snType = snType;
						final List<AutoLoginInfo> autoLoginInfos = info.autoLoginInfos;
						if (autoLoginInfos != null && autoLoginInfos.size() > 1) {
							// show user a menu to select the right account
							PopupMenu menu = new PopupMenu(mContext);
							menu.setHeaderTitle("Select a Gagein login account");
							// Set Listener
							menu.setOnItemSelectedListener(new PopupMenu.OnItemSelectedListener() {
								@Override
								public void onItemSelected(MenuItem item) {
									int id = item.getItemId();
									loginWithUserInfo(autoLoginInfos.get(id));
								}
							});
							
							for (int i = 0; i < autoLoginInfos.size(); i++) {
								menu.add(i, autoLoginInfos.get(i).memberEmail);
							}
							
							menu.show();
						} else if (autoLoginInfos != null && autoLoginInfos.size() == 1) {
							// login directly with this account
							loginWithUserInfo(autoLoginInfos.get(0));
						} else {
							if (info.emailExisted) {
								// goto login page with the info
								startActivityWithData(LoginActivity.class, info);
								finish();
							} 
//							else {
//								// goto signup page with the info
//								String message = String.format(stringFromResID(R.string.sn_not_allow_to_access_email), data.snName);
//								CommonUtil.showLongToast(message);
//								startActivityWithData(SignUpActivity.class, info);
//							}
						}
					}
				} else {
					alertMessageForParser(parser);
				}
			}
		};
		
		this.registerReceiver(authReceiver, new IntentFilter(OAuthHandler.KEY_INTENT_ACTION_SUCCESS));
	}
	
	private void loginWithUserInfo(AutoLoginInfo loginInfo) {
		CommonUtil.saveLoginInfo(mContext, loginInfo);
		
		finish();
		CommonUtil.removeActivity();
		startActivitySimple(MainTabActivity.class);
	}
	
	/**
	 * 是否含有token
	 */
	private Boolean ifHaveToken() {
		
		String token = CommonUtil.getToken(mContext);
		if ("".equals(token)) {
			return false;
		} else {
			return true;
		}
	}
	
	@Override
	protected void setOnClickListener() {
		super.setOnClickListener();
		
		leftImageBtn.setOnClickListener(this);
		loginBtn.setOnClickListener(this);
		salesforceBtn.setOnClickListener(this);
		facebookBtn.setOnClickListener(this);
		twitterBtn.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		
		if (v == loginBtn) {
			CommonUtil.hideSoftKeyBoard(mContext, LoginActivity.this);
			if (emailAndPasswordValid()) login();
		} else if (v == salesforceBtn) {
			authAppWithSnType(APIHttpMetadata.kGGSnTypeSalesforce);
		} else if (v == facebookBtn) {
			authAppWithSnType(APIHttpMetadata.kGGSnTypeFacebook);
		} else if (v == twitterBtn) {
			authAppWithSnType(APIHttpMetadata.kGGSnTypeTwitter);
		} 
		
	}
	
	/** auth the app */
	private void authAppWithSnType(final int aSnType) {
		switch (aSnType) {
		case APIHttpMetadata.kGGSnTypeSalesforce:
			oauther.authSalesforce(authHandler);
			break;
			
		case APIHttpMetadata.kGGSnTypeFacebook:
			oauther.authFacebook(authHandler);
			break;
			
		case APIHttpMetadata.kGGSnTypeTwitter:
			oauther.authTwitter(authHandler);
			break;
			
		default:
		}
	}
	
	private void login() {
		
		showLoadingDialog();
		mApiHttp.userLogin(email, password, new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject jsonObject) {
				
				if (null != jsonObject) {
					
					Log.v("silen", "jsonObject = " + jsonObject.toString());
					
					APIParser parser = new APIParser(jsonObject);
					if (parser.isOK()) {
						
						List<PlanInfo> planInfos = parser.parsePlanInfos();
						CommonUtil.savePlanInfos(planInfos);
						
						
						Member member = parser.parseLogin();
						AutoLoginInfo loginInfo = AutoLoginInfo.loginInfoFromMember(member);
						CommonUtil.saveLoginInfo(mContext, loginInfo);
						
						startActivitySimple(MainTabActivity.class);
						finish();
						
					} else {
						alertMessageForParser(parser);
					}
				}
				dismissLoadingDialog();
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				showConnectionError();
			}
		});
	}
	
	private boolean emailAndPasswordValid() {
		
		email = emailEdt.getText().toString();
		if (!TextUtils.isEmpty(email)) {
			Pattern pattern = Pattern.compile(Utils.regular_email);
			Matcher matcher = pattern.matcher(email.trim());
			if (!matcher.matches()) {
				showShortToast(R.string.invalid_email_format);
				return false;
			}
		} else {
			showShortToast(R.string.must_enter_email);
			return false;
		}
		
		password = passwordEdt.getText().toString();
		if (!TextUtils.isEmpty(password)) {
			Pattern pattern = Pattern.compile(Utils.regular_password);
			Matcher matcher = pattern.matcher(password);
			if (!matcher.matches()) {
				showShortToast(R.string.password_must_in_range);
				return false;
			}
		} else {
			showShortToast(R.string.must_enter_password);
			return false;
		}
		
		return true;
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		this.unregisterReceiver(authReceiver);
	}

}
