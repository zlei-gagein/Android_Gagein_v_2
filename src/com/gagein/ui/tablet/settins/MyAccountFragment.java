package com.gagein.ui.tablet.settins;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.gagein.R;
import com.gagein.adapter.SettingCompanyAdapter;
import com.gagein.http.APIHttp;
import com.gagein.http.APIParser;
import com.gagein.model.Company;
import com.gagein.model.DataPage;
import com.gagein.model.UserProfile;
import com.gagein.ui.BaseFragment;
import com.gagein.util.CommonUtil;
import com.gagein.util.Log;
import com.gagein.util.MessageCode;

public class MyAccountFragment extends BaseFragment implements OnClickListener, OnFocusChangeListener, OnEditorActionListener, OnItemClickListener{
	
	private EditText nameEdt;
	private EditText emailEdt;
	private EditText companyEdt;
	private EditText jobTitleEdt;
	private LinearLayout clearName;
	private LinearLayout clearEmail;
	private LinearLayout clearCompany;
	private LinearLayout clearJobTitle;
	private LinearLayout layout;
	private ListView companyListView;
	private UserProfile profile;
	private UserProfile temporaryProfile;
	private int setSelection = 1;
	private int searchCompany = 0;
	private TimerTask timerTask;
	private Timer timer;
	private List<Company> searchCompanies = new ArrayList<Company>();
	private SettingCompanyAdapter companyAdapter;

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
		companyListView = (ListView) view.findViewById(R.id.companyListView);
		layout = (LinearLayout) view.findViewById(R.id.layout);
	}
	
	@Override
	protected void initData() {
		super.initData();
		
		companyAdapter = new SettingCompanyAdapter(mContext, searchCompanies);
		companyListView.setAdapter(companyAdapter);
		companyAdapter.notifyDataSetChanged();
		companyAdapter.notifyDataSetInvalidated();
		
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
		
		companyEdt.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void afterTextChanged(Editable s) {
				
				String character = s.toString().trim();
				if (TextUtils.isEmpty(character) || null == character){ 
					
					cancelSearchTask();
					searchCompanies.clear();
					companyAdapter.notifyDataSetChanged();
					
				} else {
					
					scheduleSearchTask();
					
				};
			}
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
		});
		
		companyListView.setOnItemClickListener(this);
		
	}
	
	/**schedule search*/
	private void scheduleSearchTask() {
		
		cancelSearchTask();
		timer = new Timer();
		timerTask = new TimerTask() {
			@Override
			public void run() {
				
				Message message = new Message();
				message.what = searchCompany;
				handler.sendMessage(message);
				
			}
		};
		timer.schedule(timerTask, 2000);
		
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
			clearAllEditFocus();
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

	private void clearAllEditFocus() {
		nameEdt.clearFocus();
		emailEdt.clearFocus();
		companyEdt.clearFocus();
		jobTitleEdt.clearFocus();
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
			if (canSave) saveProfileWithParams();
			
			return true;
		}

		return false;
	}

	private void saveProfileWithParams() {
		hideSoftKey();
		
		String name = nameEdt.getText().toString().trim();
		String email = emailEdt.getText().toString().trim();
		String company = companyEdt.getText().toString().trim();
		String jobTitle = jobTitleEdt.getText().toString().trim();
		
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
	
	@SuppressLint("HandlerLeak")
	Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			
			if (msg.what == setSelection) {
				
				nameEdt.setSelection(nameEdt.getText().length());
				emailEdt.setSelection(emailEdt.getText().length());
				companyEdt.setSelection(companyEdt.getText().length());
				jobTitleEdt.setSelection(jobTitleEdt.getText().length());
				
			} else {
				
				getCompanySuggestions(companyEdt.getText().toString());
				
			}
		}
	};
	
	public void getCompanySuggestions(String keyWords) {
		
		showLoadingDialog(getActivity());
		mApiHttp.getCompanySuggestions(keyWords, new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject jsonObject) {
				
				Log.v("silen", "jsonObject = " + jsonObject.toString());
				
				APIParser parser = new APIParser(jsonObject);
				if (parser.isOK()) {
					
					searchCompanies.clear();
					
					DataPage dataPage = parser.parseSearchCompany();
					List<Object> items = dataPage.items;
					if (items != null) {
						for (Object obj : items) {
							searchCompanies.add((Company)obj);
						}
					}
					companyAdapter.notifyDataSetChanged();
					companyAdapter.notifyDataSetInvalidated();
					CommonUtil.setViewHeight(companyListView, companyListView.getAdapter().getCount() * CommonUtil.dp2px(mContext, 71));
					
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
	public void onFocusChange(View view, boolean changed) {
		
		if (view == nameEdt) {
			clearName.setVisibility(changed ? View.VISIBLE : View.GONE);
		} else if (view == emailEdt) {
			clearEmail.setVisibility(changed ? View.VISIBLE : View.GONE);
		} else if (view == companyEdt) {
			clearCompany.setVisibility(changed ? View.VISIBLE : View.GONE);
		} else if (view == jobTitleEdt) {
			clearJobTitle.setVisibility(changed ? View.VISIBLE : View.GONE);
		}
		setEditSelectionPosition();
		
	}

	private void setEditSelectionPosition() {
		Timer timer = new Timer();
		timer.schedule(new TimerTask(){
		    @Override
		    public void run() {
		    	Message message = new Message();
		    	message.what = setSelection;
		    	handler.sendMessage(message);
		    }
		}, 20);
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
	
	/**stop schedule search*/
	private void cancelSearchTask() {
		if (timerTask != null) {
			timerTask.cancel();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		
		String companyName = searchCompanies.get(position).name;
		companyEdt.setText(companyName);
		cancelSearchTask();
		clearCompany.setVisibility(View.GONE);
		clearAllEditFocus();
		
		searchCompanies.clear();
		companyAdapter.notifyDataSetChanged();
		CommonUtil.setViewHeight(companyListView, 0);
		
		
		saveProfileWithParams();
	}
	
}


