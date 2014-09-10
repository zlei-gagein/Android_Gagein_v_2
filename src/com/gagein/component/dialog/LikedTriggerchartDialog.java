package com.gagein.component.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.gagein.R;
import com.gagein.ui.settings.SettingsNewsFilterActivity;
import com.gagein.util.CommonUtil;

/**
 * 
 * @author silen
 */
public class LikedTriggerchartDialog implements OnClickListener {
	private Context mContext;
	private Dialog dialog;
	private View view;
	private LayoutInflater inflater;
	private Button button;
	private Button close;
	private Button viewBtn;
	private int layout;
	private Boolean enable;

	/**
	 * 
	 * @param mContext
	 * @param enable true ? enable : closer
	 */
	public LikedTriggerchartDialog(Context mContext, Boolean enable) {
		this.mContext = mContext;
		this.enable = enable;
		layout = enable ? R.layout.dialog_trigger_enable : R.layout.dialog_trigger_closer;
		dialog = new Dialog(mContext, R.style.dialog);
		inflater = LayoutInflater.from(mContext);
		view = inflater.inflate(layout, null);
		if (enable) {
			close = (Button) view.findViewById(R.id.close);
			viewBtn = (Button) view.findViewById(R.id.view);
			close.setOnClickListener(this);
			viewBtn.setOnClickListener(this);
		} else {
			button = (Button) view.findViewById(R.id.button);
			button.setOnClickListener(this);
		}
		dialog.setContentView(view);
	}

	public void showDialog() {
		CommonUtil.setDialogWith(dialog);
		dialog.setCancelable(false);
		dialog.show();
	}

	@Override
	public void onClick(View v) {
		if (v == button) {
			dismissDialog();
		} else if (v == close) {
			dismissDialog();
		} else if (v == viewBtn) {
			dismissDialog();
			if (CommonUtil.isTablet(mContext)) {//TODO
				
			} else {
				Intent intent = new Intent();
				intent.setClass(mContext, SettingsNewsFilterActivity.class);
				mContext.startActivity(intent);
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
