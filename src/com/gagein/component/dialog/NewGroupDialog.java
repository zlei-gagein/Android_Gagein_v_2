package com.gagein.component.dialog;

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
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.gagein.R;
import com.gagein.http.APIHttp;
import com.gagein.http.APIParser;
import com.gagein.ui.main.BaseActivity;
import com.gagein.util.CommonUtil;
import com.gagein.util.Constant;
import com.gagein.util.Log;
import com.gagein.util.MessageCode;

/**
 * 
 * @author silen
 */
public class NewGroupDialog implements OnClickListener {
	private Context mContext;
	private Dialog dialog;
	private View view;
	private LayoutInflater inflater;
	private EditText nameEdt;
	private Button cancel;
	private Button save;

	/**
	 * 
	 * @param mContext
	 * @param enable true ? enable : closer
	 */
	public NewGroupDialog(final Context mContext) {
		this.mContext = mContext;
		dialog = new Dialog(mContext, R.style.dialog);
		inflater = LayoutInflater.from(mContext);
		view = inflater.inflate(R.layout.dialog_new_group, null);
		nameEdt = (EditText) view.findViewById(R.id.nameEdt);
		cancel = (Button) view.findViewById(R.id.cancel);
		save = (Button) view.findViewById(R.id.save);
		dialog.setContentView(view);
		
		nameEdt.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void afterTextChanged(Editable s) {
				save.setTextColor(mContext.getResources().getColor(TextUtils.isEmpty(s) ? R.color.c8f8f8f : R.color.blue_dialog));
			}
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
		});
	}

	public void showDialog() {
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
			final String groupName = nameEdt.getText().toString().trim();
			if (TextUtils.isEmpty(groupName)) return;
			dismissDialog();
			
			APIHttp mApiHttp = new APIHttp(mContext);
			CommonUtil.showLoadingDialog(mContext);
			mApiHttp.newCompanyGroup(groupName, new Listener<JSONObject>() {

				@Override
				public void onResponse(JSONObject jsonObject) {
					
					
					CommonUtil.dissmissLoadingDialog();
					APIParser parser = new APIParser(jsonObject);
					
					if (parser.isOK()) {
						
						String groupId = jsonObject.optString("mogid");
						Intent intent = new Intent();
						intent.putExtra(Constant.GROUPID, groupId);
						intent.putExtra(Constant.GROUPNAME, groupName);
						intent.setAction(Constant.BROADCAST_ADD_COMPANYGROUP);
						mContext.sendBroadcast(intent);
						
					} else {//response status 
						
						String msgCode = jsonObject.optString("msg_code");
						if (MessageCode.MemberHasAlreadyOwnedGroup == Integer.parseInt(msgCode)) {
							CommonUtil.showPromotDialog(mContext, R.string.group_already);
						}
						
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
