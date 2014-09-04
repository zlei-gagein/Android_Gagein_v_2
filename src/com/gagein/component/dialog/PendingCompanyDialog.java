package com.gagein.component.dialog;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.gagein.R;
import com.gagein.http.APIHttp;
import com.gagein.http.APIParser;
import com.gagein.util.CommonUtil;
import com.gagein.util.Constant;
import com.gagein.util.MessageCode;
import com.gagein.util.Utils;

/**
 * 
 * @author silen
 */
public class PendingCompanyDialog implements OnClickListener {
	private Context mContext;
	private Dialog dialog;
	private View view;
	private LayoutInflater inflater;
	private int layout;
	private EditText nameEdt;
	private EditText websiteEdt;
	private Button cancel;
	private Button save;
	private long companyId;
	private APIHttp mApiHttp;

	/**
	 * 
	 * @param mContext
	 * @param enable true ? enable : closer
	 */
	public PendingCompanyDialog(final Context mContext) {
		this.mContext = mContext;
		mApiHttp = new APIHttp(mContext);
		layout = R.layout.dialog_pending_company;
		dialog = new Dialog(mContext, R.style.dialog);
		inflater = LayoutInflater.from(mContext);
		view = inflater.inflate(layout, null);
		nameEdt = (EditText) view.findViewById(R.id.nameEdt);
		websiteEdt = (EditText) view.findViewById(R.id.websiteEdt);
		cancel = (Button) view.findViewById(R.id.cancel);
		save = (Button) view.findViewById(R.id.save);
		dialog.setContentView(view);
		
		
		nameEdt.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void afterTextChanged(Editable s) {
				setSaveButtonColor();
			}
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
		});
		
		websiteEdt.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void afterTextChanged(Editable s) {
				setSaveButtonColor();
			}
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
		});
	}

	private void setSaveButtonColor() {
		Boolean allFiledEntered = !TextUtils.isEmpty(nameEdt.getText().toString()) && !TextUtils.isEmpty(websiteEdt.getText().toString());
		save.setTextColor(mContext.getResources().getColor(allFiledEntered ? R.color.blue_dialog : R.color.C7C7C7));
	}

	public void showDialog(String name,String website, long companyId) {
		this.companyId = companyId;
		nameEdt.setText(name);
		websiteEdt.setText(website);
		cancel.setOnClickListener(this);
		save.setOnClickListener(this);
		CommonUtil.setDialogWith(dialog);
		dialog.show();
	}
	
	@Override
	public void onClick(View v) {
		if (v == cancel) {
			dismissDialog();
		} else if (v == save) {
			final String name = nameEdt.getText().toString().trim();
			final String website = websiteEdt.getText().toString().trim();
			
			if (TextUtils.isEmpty(name) || TextUtils.isEmpty(website)) return;
			if (TextUtils.isEmpty(name)) {
				CommonUtil.showShortToast(mContext.getResources().getString(R.string.pls_input_name));
				return;
			}
			if (TextUtils.isEmpty(website)) {
				CommonUtil.showShortToast(mContext.getResources().getString(R.string.pls_input_website));
				return;
			}
			
			Pattern pattern1 = Pattern.compile(Utils.regular_url1);
			Matcher matcher1 = pattern1.matcher(website);
			Pattern pattern2 = Pattern.compile(Utils.regular_url2);
			Matcher matcher2 = pattern2.matcher(website);
			if (matcher1.matches() || matcher2.matches()) {
				CommonUtil.showLoadingDialog(mContext);
				mApiHttp.addCompanyWebsite(companyId, name , website, false, new Listener<JSONObject>() {
	
					@Override
					public void onResponse(JSONObject jsonObject) {
						
						APIParser parser = new APIParser(jsonObject);
						if (parser.isOK()) {
							
							dismissDialog();
							
							CommonUtil.showShortToast(mContext.getResources().getString(R.string.website_added));
							// sent a broadcast to finish activity and refresh website
							Intent intent = new Intent();
							intent.setAction(Constant.BROADCAST_ADDED_PENDING_COMPANY);
							mContext.sendBroadcast(intent);
							
						} else if (parser.messageCode() == MessageCode.CompanyWebConnectFailed){
							
							final VerifingWebsiteConnectTimeOutDialog dialog = new VerifingWebsiteConnectTimeOutDialog(mContext);
							dialog.setCancelable(false);
							dialog.showDialog(website, new OnClickListener() {
								
								@Override
								public void onClick(View arg0) {
									
									dismissDialog();
									
									dialog.dismissDialog();
								}
							});
							
				            
				        } else if (parser.messageCode() == MessageCode.CompanyWebConnectTimeout) {
				        	
				        	final VerifyingWebsiteDialog dialog = new VerifyingWebsiteDialog(mContext);
				        	dialog.setCancelable(false);
				        	dialog.showDialog(website, new OnClickListener() {
								
								@Override
								public void onClick(View arg0) {
									
									dialog.dismissDialog();
									
									CommonUtil.showLoadingDialog(mContext);
									mApiHttp.addNewCompanyWithName(name , website, true, new Listener<JSONObject>() {

										@Override
										public void onResponse(JSONObject jsonObject) {
											
											dismissDialog();
											
											CommonUtil.dissmissLoadingDialog();
											APIParser parser = new APIParser(jsonObject);
											
											if (parser.isOK()) {
												
												CommonUtil.showShortToast(R.string.companies_added);
												// sent a broadcast to finish activity and refresh website
												Intent intent = new Intent();
												intent.setAction(Constant.BROADCAST_ADDED_PENDING_COMPANY);
												mContext.sendBroadcast(intent);
												
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
							});
				        	
				        }
						CommonUtil.dissmissLoadingDialog();
					}
				}, new Response.ErrorListener() {
	
					@Override
					public void onErrorResponse(VolleyError error) {
						CommonUtil.dissmissLoadingDialog();
						CommonUtil.showLongToast(mContext.getResources().getString(R.string.connection_error));
					}
				});
			} else {
				CommonUtil.showShortToast(mContext.getResources().getString(R.string.enter_valid_url));
			}
		}
	}
	
	public void dismissDialog() {
		dialog.dismiss();
	}

	public boolean isShow() {
		if (dialog.isShowing()) {
			return true;
		} else {
			return false;
		}
	}

	public Dialog getDialog() {
		return dialog;
	}

	public void setCancelable(boolean is) {
		dialog.setCancelable(is);
	}

}
