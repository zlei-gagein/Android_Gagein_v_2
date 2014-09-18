package com.gagein.ui.tablet.settins;

import org.json.JSONObject;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.gagein.R;
import com.gagein.http.APIHttp;
import com.gagein.http.APIParser;
import com.gagein.model.UserProfile;
import com.gagein.ui.BaseFragment;
import com.gagein.util.CommonUtil;
import com.gagein.util.MessageCode;

public class MyAccountFragment extends BaseFragment implements OnClickListener, OnFocusChangeListener, OnEditorActionListener{
	
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
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.activity_myaccount, container, false);
		mContext = getActivity();
		mApiHttp = new APIHttp(mContext);

		doInit();
		return view;
	}
	
	@Override
	protected void initView() {
		super.initView();
		
		setTitle(R.string.my_account);
		
		nameEdt = (EditText) view.findViewById(R.id.nameEdt);
		emailEdt = (EditText) view.findViewById(R.id.emailEdt);
		companyEdt = (EditText) view.findViewById(R.id.companyEdt);
		jobTitleEdt = (EditText) view.findViewById(R.id.jobTitleEdt);
		clearName = (LinearLayout) view.findViewById(R.id.clearNameLayout);
		clearEmail = (LinearLayout) view.findViewById(R.id.clearEmailLayout);
		clearCompany = (LinearLayout) view.findViewById(R.id.clearCompanyLayout);
		clearJobTitle = (LinearLayout) view.findViewById(R.id.clearJobTitleLayout);
		layout = (LinearLayout) view.findViewById(R.id.layout);
	}
	
	@Override
	protected void initData() {
		super.initData();
		
		getMyOverView();
	}
	
	@Override
	protected void setOnClickListener() {
		super.setOnClickListener();
		layout.setOnClickListener(this);
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
	protected void setData() {
		super.setData();
		nameEdt.setText(profile.firstName + " " + profile.lastName);
		emailEdt.setText(profile.email);
		companyEdt.setText(profile.orgName);
		jobTitleEdt.setText(profile.orgTitle);
	}
	
	public void getMyOverView() {
		showLoadingDialog(getActivity());
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
				showConnectionError(getActivity());
			}
		});
	}

	@Override
	public void onClick(View v) {
		if (v == layout) {
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
	public boolean onEditorAction(TextView v, int actionId, KeyEvent arg2) {
		
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
			if (v == nameEdt && !TextUtils.isEmpty(name)) {
				clearName.setVisibility(View.GONE);
				nameEdt.clearFocus();
				canSave = true;
			} else if (v == emailEdt && !TextUtils.isEmpty(email)) {
				clearEmail.setVisibility(View.GONE);
				emailEdt.clearFocus();
				canSave = true;
			} else if (v == companyEdt && !TextUtils.isEmpty(company)) {
				clearCompany.setVisibility(View.GONE);
				companyEdt.clearFocus();
				canSave = true;
			} else if (v == jobTitleEdt && !TextUtils.isEmpty(jobTitle)) {
				clearJobTitle.setVisibility(View.GONE);
				jobTitleEdt.clearFocus();
				canSave = true;
			}
			
			// 468494# setting-my account,name, company, job title为空的时候不允许保存
			if (canSave) {
				hideSoftKey();
				
				String firstName = null;
				String lastName = null;
				if (!TextUtils.isEmpty(name)) {
					int index = name.indexOf(" ");
					if (index > 0) {
						firstName = name.substring(0, index);
						lastName = name.substring(index).trim();
					} else {
						firstName = name;
					}
				}
				
				if (TextUtils.isEmpty(firstName)) {
					firstName = profile.firstName;
				}
				
				if (TextUtils.isEmpty(lastName)) {
					lastName = profile.lastName;
				}
				
				saveProfile(firstName, lastName
						, TextUtils.isEmpty(email) ? profile.email : email
								, TextUtils.isEmpty(company) ? profile.orgName : company
										, TextUtils.isEmpty(jobTitle) ? profile.orgTitle : jobTitle);
			}
			
			return true;
		}

		return false;
	}

	@Override
	public void onFocusChange(View view, boolean changed) {
		if (view == nameEdt) {
			clearName.setVisibility(changed ? View.VISIBLE : View.GONE);
			nameEdt.setSelection(nameEdt.getText().length());
		} else if (view == emailEdt) {
			clearEmail.setVisibility(changed ? View.VISIBLE : View.GONE);
		} else if (view == companyEdt) {
			clearCompany.setVisibility(changed ? View.VISIBLE : View.GONE);
		} else if (view == jobTitleEdt) {
			clearJobTitle.setVisibility(changed ? View.VISIBLE : View.GONE);
		}
	}
	
	private void saveProfile(String firstName, String lastName, String email, String company, String jobTitle) {
		showLoadingDialog(mContext);
		temporaryProfile = new UserProfile();
		temporaryProfile.firstName = firstName;
		temporaryProfile.lastName = lastName;
		temporaryProfile.email = email;
		temporaryProfile.orgName = company;
		temporaryProfile.orgTitle = jobTitle;
		
		mApiHttp.changeProfile(firstName, lastName, email, company, jobTitle,
				new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject jsonObject) {

						APIParser parser = new APIParser(jsonObject);
						if (parser.isOK()) {
//							profile.fullName() = name;
							showShortToast(mContext.getResources().getString(R.string.Saved));
							
							profile.firstName = temporaryProfile.firstName;
							profile.lastName = temporaryProfile.lastName;
							profile.email = temporaryProfile.email;
							profile.orgName = temporaryProfile.orgName;
							profile.orgTitle = temporaryProfile.orgTitle;
							
							String nameStr = TextUtils.isEmpty(temporaryProfile.lastName) ? temporaryProfile.firstName : temporaryProfile.firstName + " " + temporaryProfile.lastName;
							nameEdt.setText(nameStr);
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
						showConnectionError(mContext);
					}
				});
	}
	
	private void hideSoftKey() {
		CommonUtil.hideSoftKeyBoard(getActivity(), getActivity());
	}
}


