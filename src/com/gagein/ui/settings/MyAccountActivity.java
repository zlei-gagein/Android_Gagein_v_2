package com.gagein.ui.settings;

import org.json.JSONObject;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.gagein.R;
import com.gagein.http.APIParser;
import com.gagein.model.UserProfile;
import com.gagein.ui.main.BaseActivity;
import com.gagein.util.CommonUtil;
import com.gagein.util.MessageCode;

public class MyAccountActivity extends BaseActivity implements OnFocusChangeListener, OnEditorActionListener{
	
	private EditText nameEdt;
	private EditText emailEdt;
	private EditText companyEdt;
	private EditText jobTitleEdt;
	private LinearLayout clearName;
	private LinearLayout clearEmail;
	private LinearLayout clearCompany;
	private LinearLayout clearJobTitle;
	private LinearLayout layout;
	private UserProfile profile;
	private UserProfile temporaryProfile;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_myaccount);
		
		doInit();
	}
	
	@Override
	protected void initView() {
		super.initView();
		
		setTitle(R.string.my_account);
		setLeftImageButton(R.drawable.back_arrow);
		
		nameEdt = (EditText) findViewById(R.id.nameEdt);
		emailEdt = (EditText) findViewById(R.id.emailEdt);
		companyEdt = (EditText) findViewById(R.id.companyEdt);
		jobTitleEdt = (EditText) findViewById(R.id.jobTitleEdt);
		clearName = (LinearLayout) findViewById(R.id.clearNameLayout);
		clearEmail = (LinearLayout) findViewById(R.id.clearEmailLayout);
		clearCompany = (LinearLayout) findViewById(R.id.clearCompanyLayout);
		clearJobTitle = (LinearLayout) findViewById(R.id.clearJobTitleLayout);
		layout = (LinearLayout) findViewById(R.id.layout);
	}
	
	@Override
	protected void initData() {
		super.initData();
		getMyOverView();
	}
	
	@Override
	protected void setData() {
		super.setData();
		nameEdt.setText(profile.firstName);
		emailEdt.setText(profile.email);
		companyEdt.setText(profile.orgName);
		jobTitleEdt.setText(profile.orgTitle);
	}
	
	private void getMyOverView() {
		showLoadingDialog();
		mApiHttp.getMyOverview(new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject jsonObject) {
				APIParser parser = new APIParser(jsonObject);
				if (parser.isOK()) {
					profile = parser.parseGetMyOverview();
					setData();
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
	
	@Override
	protected void setOnClickListener() {
		super.setOnClickListener();
		layout.setOnClickListener(this);
		leftImageBtn.setOnClickListener(this);
		clearName.setOnClickListener(this);
		clearEmail.setOnClickListener(this);
		clearCompany.setOnClickListener(this);
		clearJobTitle.setOnClickListener(this);
		nameEdt.setOnFocusChangeListener(this);
		emailEdt.setOnFocusChangeListener(this);
		companyEdt.setOnFocusChangeListener(this);
		jobTitleEdt.setOnFocusChangeListener(this);
		nameEdt.setOnEditorActionListener(this);
		emailEdt.setOnEditorActionListener(this);
		companyEdt.setOnEditorActionListener(this);
		jobTitleEdt.setOnEditorActionListener(this);
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		if (v == leftImageBtn) {
			finish();
		} else if (v == layout) {
			nameEdt.clearFocus();
			emailEdt.clearFocus();
			companyEdt.clearFocus();
			jobTitleEdt.clearFocus();
		} else if (v == clearName) {
			nameEdt.setText("");
			clearName.setVisibility(View.GONE);
		} else if (v == clearEmail) {
			emailEdt.setText("");
			clearEmail.setVisibility(View.GONE);
		} else if (v == clearCompany) {
			companyEdt.setText("");
			clearCompany.setVisibility(View.GONE);
		} else if (v == clearJobTitle) {
			jobTitleEdt.setText("");
			clearJobTitle.setVisibility(View.GONE);
		}
	}

	@Override
	public void onFocusChange(View view, boolean changed) {
		if (view == nameEdt) {
			clearName.setVisibility(changed ? View.VISIBLE : View.GONE);
			nameEdt.setSelection(nameEdt.getText().length());
		} else if (view == emailEdt) {
			clearEmail.setVisibility(changed ? View.VISIBLE : View.GONE);
			emailEdt.setSelection(emailEdt.getText().length());
		} else if (view == companyEdt) {
			clearCompany.setVisibility(changed ? View.VISIBLE : View.GONE);
			companyEdt.setSelection(companyEdt.getText().length());
		} else if (view == jobTitleEdt) {
			clearJobTitle.setVisibility(changed ? View.VISIBLE : View.GONE);
			jobTitleEdt.setSelection(jobTitleEdt.getText().length());
		}
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if (actionId == EditorInfo.IME_ACTION_DONE) {
			String name = nameEdt.getText().toString().trim();
			String email = emailEdt.getText().toString().trim();
			String company = companyEdt.getText().toString().trim();
			String jobTitle = jobTitleEdt.getText().toString().trim();
			if (!CommonUtil.isValidEmail(email)) {
				showShortToast(stringFromResID(R.string.invalid_email_format));
				return false;
			}
			
			boolean canSave = false;
			if (v == nameEdt && !name.isEmpty()) {
				clearName.setVisibility(View.GONE);
				nameEdt.clearFocus();
				canSave = true;
			} else if (v == emailEdt && !email.isEmpty()) {
				clearEmail.setVisibility(View.GONE);
				emailEdt.clearFocus();
				canSave = true;
			} else if (v == companyEdt && !company.isEmpty()) {
				clearCompany.setVisibility(View.GONE);
				companyEdt.clearFocus();
				canSave = true;
			} else if (v == jobTitleEdt && !jobTitle.isEmpty()) {
				clearJobTitle.setVisibility(View.GONE);
				jobTitleEdt.clearFocus();
				canSave = true;
			}
			
			// 468494# setting-my account,name, company, job title为空的时候不允许保存
			if (canSave) {
				hideSoftKey();
				saveProfile(name.isEmpty() ? profile.firstName : name
						, email.isEmpty() ? profile.email : email
								, company.isEmpty() ? profile.orgName : company
										, jobTitle.isEmpty() ? profile.orgTitle : jobTitle);
			}
			
			return true;
		}

		return false;
	}
	
	private void saveProfile(String name, String email, String company, String jobTitle) {
		showLoadingDialog();
		temporaryProfile = new UserProfile();
		temporaryProfile.firstName = name;
		temporaryProfile.email = email;
		temporaryProfile.orgName = company;
		temporaryProfile.orgTitle = jobTitle;
		
		mApiHttp.changeProfile(name, email, company, jobTitle,
				new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject jsonObject) {

						APIParser parser = new APIParser(jsonObject);
						if (parser.isOK()) {
//							profile.fullName() = name;
							showShortToast(mContext.getResources().getString(R.string.Saved));
							
							profile.firstName = temporaryProfile.firstName;
							profile.email = temporaryProfile.email;
							profile.orgName = temporaryProfile.orgName;
							profile.orgTitle = temporaryProfile.orgTitle;
							
							nameEdt.setText(temporaryProfile.firstName);
							emailEdt.setText(temporaryProfile.email);
							companyEdt.setText(temporaryProfile.orgName);
							jobTitleEdt.setText(temporaryProfile.orgTitle);
							
						} else {
							String msg = MessageCode.messageForCode(parser.messageCode());
							if (msg != null && msg.length() > 0) {
								CommonUtil.showDialog(msg);
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
	
	private void saveName(String name) {
		showLoadingDialog();
		mApiHttp.changeProfileName(name, "",
				new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject jsonObject) {

						APIParser parser = new APIParser(jsonObject);
						if (parser.isOK()) {
//							profile.fullName() = name;
						} else {
							String msg = MessageCode.messageForCode(parser.messageCode());
							if (msg != null && msg.length() > 0) {
								CommonUtil.showDialog(msg);
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
	
	private void saveEmail(String email) {
		showLoadingDialog();
		mApiHttp.changeProfileEmail(email, new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject jsonObject) {
				
				APIParser parser = new APIParser(jsonObject);
				if (parser.isOK()) {
//					profile.email = email;
				} else {
					alertMessageForParser(parser);
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
	
	private void saveCompany(String company) {
		showLoadingDialog();
		mApiHttp.changeProfileCompany(company, new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject jsonObject) {
						
						APIParser parser = new APIParser(jsonObject);
						if (parser.isOK()) {
						} else {
							alertMessageForParser(parser);
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
	
	private void saveJobTitle(String jobTitle) {
		showLoadingDialog();
		mApiHttp.changeProfileJobTitle(jobTitle, new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject jsonObject) {
				
				APIParser parser = new APIParser(jsonObject);
				if (parser.isOK()) {
				} else {
					String msg = MessageCode.messageForCode(parser.messageCode());
					if (msg != null && msg.length() > 0) {
						CommonUtil.showDialog(msg);
					}
				}
				CommonUtil.dissmissLoadingDialog();
			}
			
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				showConnectionError();
			}
		});
	}
	
	private void hideSoftKey() {
		CommonUtil.hideSoftKeyBoard(mContext, MyAccountActivity.this);
	}
}
