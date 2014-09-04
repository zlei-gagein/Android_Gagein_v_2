package com.gagein.ui.main;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
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
import com.gagein.model.SnUserInfo;
import com.gagein.util.CommonUtil;
import com.gagein.util.Constant;
import com.gagein.util.Log;
import com.gagein.util.Utils;
import com.gagein.util.oauth.OAuthHandler;
import com.gagein.util.oauth.OAuther;

public class SignUpActivity extends BaseActivity {
	
	private Button creatAccountBtn;
	private ImageView salesforceBtn;
	private ImageView facebookBtn;
	private ImageView twitterBtn;
	private EditText firstNameEdt;
	private EditText lastNameEdt;
	private EditText emailEdt;
	private EditText passwordEdt;
	private String firstName;
	private String lastName;
	private String email;
	private String password;
	private String timeZone;
	private WebView webviewHidden;			// webview used to execute a javascript
	private OAuthHandler authHandler;
	private OAuther oauther;
	private BroadcastReceiver authReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signup);
		
		doInit();
	}
	
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void initView() {
		super.initView();
		setLeftImageButton(R.drawable.back_arrow);
		setTitle(R.string.sign_up);
		
		creatAccountBtn = (Button) findViewById(R.id.creatAccountBtn);
		salesforceBtn = (ImageView) findViewById(R.id.salesforceBtn);
		facebookBtn = (ImageView) findViewById(R.id.facebookBtn);
		twitterBtn = (ImageView) findViewById(R.id.twitterBtn);
		
		firstNameEdt = (EditText) findViewById(R.id.firstNameEdt);
		lastNameEdt = (EditText) findViewById(R.id.lastNameEdt);
		emailEdt = (EditText) findViewById(R.id.emailEdt);
		passwordEdt = (EditText) findViewById(R.id.passwordEdt);
		
		webviewHidden = (WebView) findViewById(R.id.webviewHidden);
		WebSettings settings = webviewHidden.getSettings();
		settings.setJavaScriptEnabled(true); 
		webviewHidden.setWebChromeClient(new MyChromClient());
	}
	
	@Override
	protected void initData() {
		super.initData();
		webviewHidden.loadUrl("file:///android_asset/detect_timezone.html");
		authHandler = new OAuthHandler(this, "");
		oauther = new OAuther(this);
	}
	
	@Override
	protected void setOnClickListener() {
		super.setOnClickListener();
		leftImageBtn.setOnClickListener(this);
		creatAccountBtn.setOnClickListener(this);
		salesforceBtn.setOnClickListener(this);
		facebookBtn.setOnClickListener(this);
		twitterBtn.setOnClickListener(this);
		
		authReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				Log.v("silen", "9999999999999999999");
				final OAuthHandler.OAuthHandlerData data = (OAuthHandler.OAuthHandlerData)intent.getSerializableExtra(OAuthHandler.KEY_INTENT_DATA);
				Log.v("silen", "data = " + data);
				Log.v("silen", "data.accessToken = " + data.accessToken);
				Log.v("silen", "data.snName = " + data.snName);
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
									CommonUtil.dissmissLoadingDialog();
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
							Log.v("silen", "data.openId = " + data.openId);
							if (TextUtils.isEmpty(data.openId)) return;
							int slashIndex = data.openId.lastIndexOf("/");
							Log.v("silen", "slashIndex = " + slashIndex);
//							if (slashIndex < 0 || slashIndex + 1 >= data.openId.length()) return; //TODO
							
							// extract account id
							String accountId = data.openId.substring(slashIndex + 1);
							Log.v("silen", "accountId = " + accountId);
							
							// call API to get user info
							CommonUtil.showLoadingMsgDialog(mContext, "Accessing data from Salesforce  ");
							mApiHttp.snGetUserInfoSalesforce(data.accessToken, accountId, data.refreshToken, data.instanceUrl, new Listener<JSONObject>() {

								@Override
								public void onResponse(JSONObject jsonObject) {
									handleSnUserInfoRetrieved(data, snType, jsonObject);
									CommonUtil.dissmissLoadingDialog();
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
									CommonUtil.dissmissLoadingDialog();
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
									CommonUtil.dissmissLoadingDialog();
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
					Log.v("silen", data.snName + "user info retreived OK!");
					
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
							} else {
								// goto signup page with the info
								String message = String.format(stringFromResID(R.string.sn_not_allow_to_access_email), data.snName);
								CommonUtil.showLongToast(message);
								startActivityWithData(SignUpActivity.class, info);
							}
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
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		if (v == leftImageBtn) {
			startActivitySimple(NavigationActivity.class);
			finish();
		} else if (v == creatAccountBtn) {
			if (verifyInfoValidity()) {
				showLoadingDialog();
				mApiHttp.userRegister(email, password, firstName,
						lastName, (TextUtils.isEmpty(timeZone) ? Constant.TIME_ZONE : timeZone), new Listener<JSONObject>() {

							@Override
							public void onResponse(JSONObject jsonObject) {
								dismissLoadingDialog();
						if (null != jsonObject) {
							APIParser parser = new APIParser(jsonObject);
							if (parser.isOK()) {
								CommonUtil.showLoadingDialog(mContext);
								mApiHttp.userLogin(email, password, new Listener<JSONObject>() {

									@Override
									public void onResponse(JSONObject jsonObject) {
										if (null != jsonObject) {
											APIParser parser = new APIParser(jsonObject);
											if (parser.isOK()) {
												//JSONObject jObject = jsonObject.optJSONObject("data");
												Member member = parser.parseLogin();
												AutoLoginInfo loginInfo = AutoLoginInfo.loginInfoFromMember(member);
												CommonUtil.saveLoginInfo(mContext, loginInfo);
												Constant.SIGNUP = true;
												
												finish();
												CommonUtil.removeActivity();
												startActivitySimple(MainTabActivity.class);
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
							} else {
								alertMessageForParser(parser);
							}
						}
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						showConnectionError();
					}
				});
			}
		} else if (v == salesforceBtn) {//TODO
			authAppWithSnType(APIHttpMetadata.kGGSnTypeSalesforce);
		} else if (v == facebookBtn) {//TODO
			authAppWithSnType(APIHttpMetadata.kGGSnTypeFacebook);
		} else if (v == twitterBtn) {//TODO
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
	
	/**
	 * verify sign up info validity
	 */
	public boolean verifyInfoValidity() {

		// verify first name validity
		firstName = firstNameEdt.getText().toString();
		if (TextUtils.isEmpty(firstName)) {
			CommonUtil.showShortToast(stringFromResID(R.string.must_enter_first_name));
			return false;
		} else if (firstName.length() < 2) {
			CommonUtil.showShortToast(stringFromResID(R.string.first_name_least_2_char));
			return false;
		}

		// verify last name validity
		lastName = lastNameEdt.getText().toString();
		if (TextUtils.isEmpty(lastName)) {
			CommonUtil.showShortToast(stringFromResID(R.string.must_enter_last_name));
			return false;
		} else if (lastName.length() < 2) {
			CommonUtil.showShortToast(stringFromResID(R.string.last_name_least_2_char));
			return false;
		}

		// verify email validity
		email = emailEdt.getText().toString();
		if (!TextUtils.isEmpty(email)) {
			Pattern pattern = Pattern.compile(Utils.regular_email);
			Matcher matcher = pattern.matcher(email);
			if (!matcher.matches()) {
				CommonUtil.showShortToast(stringFromResID(R.string.invalid_email_format));
				return false;
			}
		} else {
			CommonUtil.showShortToast(stringFromResID(R.string.must_enter_email));
			return false;
		}

		// verify password validity
		password = passwordEdt.getText().toString();
		if (!TextUtils.isEmpty(password)) {
			Pattern pattern = Pattern.compile(Utils.regular_password);
			Matcher matcher = pattern.matcher(password);
			if (!matcher.matches()) {
				CommonUtil.showShortToast(stringFromResID(R.string.password_must_in_range));
				return false;
			}
		} else {
			CommonUtil.showShortToast(stringFromResID(R.string.must_enter_password));
			return false;
		}
		
		return true;
	}
	
	final class MyChromClient extends WebChromeClient {

		@Override
		public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
			Log.d("silen", message);
			timeZone = message;
			result.confirm();
			webviewHidden.destroy();
			return true;
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		webviewHidden.destroy();
		this.unregisterReceiver(authReceiver);
	}

}
