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
public class ConfirmSubscriptionDialog implements OnClickListener {
	private Context mContext;
	private Dialog dialog;
	private View view;
	private LayoutInflater inflater;
	private Button cancel;
	private Button confirm;

	/**
	 * 
	 * @param mContext
	 */
	public ConfirmSubscriptionDialog(Context mContext) {
		this.mContext = mContext;
		dialog = new Dialog(mContext, R.style.dialog);
		inflater = LayoutInflater.from(mContext);
		view = inflater.inflate(R.layout.dialog_confirm_subscription, null);
		cancel = (Button) view.findViewById(R.id.cancel);
		confirm = (Button) view.findViewById(R.id.confirm);
		cancel.setOnClickListener(this);
		confirm.setOnClickListener(this);
		dialog.setContentView(view);
	}

	public void showDialog() {
		CommonUtil.setDialogWith(dialog);
		dialog.show();
	}

	@Override
	public void onClick(View v) {//TODO
		if (v == cancel) {
			dismissDialog();
		} else if (v == confirm) {
			dismissDialog();
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
