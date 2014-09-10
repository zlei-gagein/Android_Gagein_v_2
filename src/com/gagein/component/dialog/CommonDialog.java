package com.gagein.component.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.gagein.R;
import com.gagein.util.CommonUtil;

public class CommonDialog {
	private Dialog dialog;
	private View view;
	private LayoutInflater inflater;
	private Button confirm;
	private Button cancel;

	public CommonDialog(Context context) {
		dialog = new Dialog(context, R.style.dialog);
		inflater = LayoutInflater.from(context);
		view = inflater.inflate(R.layout.dialog_common, null);
		confirm = (Button) view.findViewById(R.id.confirm);
		cancel = (Button) view.findViewById(R.id.cancel);
		dialog.setContentView(view);
	}

	public void showDialog(String buttonOneText, View.OnClickListener listener_confirm, View.OnClickListener listener_cancel) {
		confirm.setText(buttonOneText);
		confirm.setOnClickListener(listener_confirm);
		cancel.setOnClickListener(listener_cancel);
		CommonUtil.showDialogFromBottom(dialog);
		dialog.setCancelable(false);
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
	
	public Window getWindow() {
		return dialog.getWindow();
	}

	public void setCancelable(boolean is) {
		dialog.setCancelable(is);
	}
}
