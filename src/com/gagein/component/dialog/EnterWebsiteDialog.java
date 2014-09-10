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
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.gagein.R;
import com.gagein.http.APIHttp;
import com.gagein.http.APIParser;
import com.gagein.util.CommonUtil;
import com.gagein.util.Constant;
import com.gagein.util.Utils;

/**
 * 
 * @author silen
 */
public class EnterWebsiteDialog implements OnClickListener {
	private Context mContext;
	private Dialog dialog;
	private View view;
	private LayoutInflater inflater;
	private TextView enter;
	private EditText websiteEdt;
	private Button cancel;
	private Button save;
	private long companyId;

	/**
	 * 
	 * @param mContext
	 * @param enable true ? enable : closer
	 */
	public EnterWebsiteDialog(final Context mContext) {
		this.mContext = mContext;
		dialog = new Dialog(mContext, R.style.dialog);
		inflater = LayoutInflater.from(mContext);
		view = inflater.inflate(R.layout.dialog_enter_website, null);
		enter = (TextView) view.findViewById(R.id.enter);
		websiteEdt = (EditText) view.findViewById(R.id.websiteEdt);
		cancel = (Button) view.findViewById(R.id.cancel);
		save = (Button) view.findViewById(R.id.save);
		dialog.setContentView(view);
		
		websiteEdt.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void afterTextChanged(Editable s) {
				save.setTextColor(mContext.getResources().getColor(TextUtils.isEmpty(s) ? R.color.c8f8f8f : R.color.yellow));
			}
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
		});
	}

	public void showDialog(String companyName, long companyId) {
		
		String enterName = String.format(enter.getText().toString(), companyName);
		this.companyId = companyId;
		enter.setText(enterName);
		cancel.setOnClickListener(this);
		save.setOnClickListener(this);
		CommonUtil.setDialogWith(dialog);
		dialog.setCancelable(false);
		dialog.show();
	}

	@Override
	public void onClick(View v) {
		if (v == cancel) {
			dismissDialog();
		} else if (v == save) {
			String website = websiteEdt.getText().toString().trim();
			if (TextUtils.isEmpty(website)) {
				CommonUtil.showShortToast(mContext.getResources().getString(R.string.pls_input_website));
				return;
			}
			APIHttp mApiHttp = new APIHttp(mContext);
			Pattern pattern1 = Pattern.compile(Utils.regular_url1);
			Matcher matcher1 = pattern1.matcher(website);
			Pattern pattern2 = Pattern.compile(Utils.regular_url2);
			Matcher matcher2 = pattern2.matcher(website);
			if (matcher1.matches() || matcher2.matches()) {
				CommonUtil.showLoadingDialog(mContext);
				mApiHttp.addCompanyWebsite(companyId, "" , website, false, new Listener<JSONObject>() {//TODO
	
					@Override
					public void onResponse(JSONObject jsonObject) {
						APIParser parser = new APIParser(jsonObject);
						if (parser.isOK()) {
							CommonUtil.showShortToast(mContext.getResources().getString(R.string.website_added));
							dismissDialog();
							// sent a broadcast to finish activity and refresh website
							Intent intent = new Intent();
							intent.setAction(Constant.BROADCAST_ADDED_PENDING_COMPANY);
							mContext.sendBroadcast(intent);
						} else {
							CommonUtil.alertMessageForParser(parser);
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
