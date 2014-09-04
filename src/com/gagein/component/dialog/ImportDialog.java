package com.gagein.component.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.gagein.R;
import com.gagein.util.CommonUtil;

/**
 * 
 * @author silen
 */
public class ImportDialog {
	private Context mContext;
	private Dialog dialog;
	private View view;
	private LayoutInflater inflater;
	private Button okBtn;
	private Button notAllowBtn;

	/**
	 * 
	 * @param mContext
	 */
	public ImportDialog(Context mContext) {
		this.mContext = mContext;
		dialog = new Dialog(mContext, R.style.dialog);
		inflater = LayoutInflater.from(mContext);
		view = inflater.inflate(R.layout.dialog_import, null);
		okBtn = (Button) view.findViewById(R.id.okBtn);
		notAllowBtn = (Button) view.findViewById(R.id.notAllowBtn);
		dialog.setContentView(view);
	}

	public void showDialog(OnClickListener notAllowClickListener, OnClickListener okClickListener) {
		notAllowBtn.setOnClickListener(notAllowClickListener);
		okBtn.setOnClickListener(okClickListener);
		CommonUtil.setDialogWith(dialog);
		dialog.show();
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
