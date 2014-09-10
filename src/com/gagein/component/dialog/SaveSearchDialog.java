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

/**
 * 
 * @author silen
 */
public class SaveSearchDialog implements OnClickListener {
	private Context mContext;
	private Dialog dialog;
	private View view;
	private LayoutInflater inflater;
	private int layout;
	private EditText websiteEdt;
	private Button cancel;
	private Button save;
	private String type;
	private String queryInfo;

	/**
	 * 
	 * @param mContext
	 * @param enable true ? enable : closer
	 */
	public SaveSearchDialog(final Context mContext, String type, String queryInfo, String queryInfoResult) {
		this.mContext = mContext;
		this.type = type;
		this.queryInfo = queryInfo;
		layout = R.layout.dialog_save_search;
		dialog = new Dialog(mContext, R.style.dialog);
		inflater = LayoutInflater.from(mContext);
		view = inflater.inflate(layout, null);
		websiteEdt = (EditText) view.findViewById(R.id.websiteEdt);
		cancel = (Button) view.findViewById(R.id.cancel);
		save = (Button) view.findViewById(R.id.save);
		
		// bug fix: 461378# saved search时,name长度限制在50个字符，如果第50个字符时某个单词的中间字符的时候，最后的整个单词都截取掉 -start
		if (queryInfoResult.length() > 50) {
			String lastStr = queryInfoResult.substring(queryInfoResult.length() - 1);
			if (lastStr.equalsIgnoreCase(";") || lastStr.equalsIgnoreCase(",")) {
				queryInfoResult = queryInfoResult.substring(0, queryInfoResult.length() - 1);
			}
			
			while (queryInfoResult.length() > 50) {
				int index1 = queryInfoResult.lastIndexOf(";");
				int index2 = queryInfoResult.lastIndexOf(",");
				int maxIndex = (index1 > index2 ? index1 : index2);
				queryInfoResult = queryInfoResult.substring(0, maxIndex);
			}
		}
		// bug fix: 461378# saved search时,name长度限制在50个字符，如果第50个字符时某个单词的中间字符的时候，最后的整个单词都截取掉 -end
		
		//设置QueryInfoResult
		websiteEdt.setText(queryInfoResult);
		setSaveButtonColor(mContext, queryInfoResult);
		
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

	private void setSaveButtonColor(final Context mContext, String string) {
		save.setTextColor(mContext.getResources().getColor(TextUtils.isEmpty(string) ? R.color.c8f8f8f : R.color.yellow));
	}

	public void showDialog() {
		CommonUtil.setDialogWith(dialog);
		cancel.setOnClickListener(this);
		save.setOnClickListener(this);
		dialog.setCancelable(false);
		dialog.show();
	}
	
	@Override
	public void onClick(View v) {
		if (v == cancel) {
			
			dismissDialog();

		} else if (v == save) {
			
			String website = websiteEdt.getText().toString().trim();
			if (TextUtils.isEmpty(website)) return;
			
			dismissDialog();
			CommonUtil.showLoadingDialog(mContext);
			
			if (type.equalsIgnoreCase("buz")) {
				
				new APIHttp(mContext).saveSearchCompanies(website, queryInfo,  new Listener<JSONObject>() {
					
					@Override
					public void onResponse(JSONObject jsonObject) {
						APIParser parser = new APIParser(jsonObject);
						if (parser.isOK()) {//TODO send broadcast
							Intent intent = new Intent();
							intent.setAction(Constant.BROADCAST_REFRESH_SEARCH);
							mContext.sendBroadcast(intent);
							CommonUtil.showShortToast(mContext.getResources().getString(R.string.Saved));
							dismissDialog();
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
				
			} else if (type.equalsIgnoreCase("con")) {
				
				new APIHttp(mContext).saveSearchPersons(website, queryInfo, new Listener<JSONObject>() {
					
					@Override
					public void onResponse(JSONObject jsonObject) {
						APIParser parser = new APIParser(jsonObject);
						if (parser.isOK()) {
							Intent intent = new Intent();
							intent.setAction(Constant.BROADCAST_REFRESH_SEARCH);
							mContext.sendBroadcast(intent);
							CommonUtil.showShortToast(mContext.getResources().getString(R.string.Saved));
							dismissDialog();
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
