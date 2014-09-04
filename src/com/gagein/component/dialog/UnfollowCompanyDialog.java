package com.gagein.component.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.gagein.R;
import com.gagein.util.CommonUtil;

public class UnfollowCompanyDialog {
	private Dialog dialog;
	private View view;
	private LayoutInflater inflater;
	private Button unfollow;
	private Button cancel;

	public UnfollowCompanyDialog(Context context) {
		dialog = new Dialog(context, R.style.dialog);
		inflater = LayoutInflater.from(context);
		view = inflater.inflate(R.layout.dialog_unfollow, null);
		unfollow = (Button) view.findViewById(R.id.unfollow);
		cancel = (Button) view.findViewById(R.id.cancel);
		dialog.setContentView(view);
	}

	public void showDialog(View.OnClickListener listener_unfollow, View.OnClickListener listener_cancel) {
		unfollow.setOnClickListener(listener_unfollow);
		cancel.setOnClickListener(listener_cancel);
		CommonUtil.showDialogFromBottom(dialog);
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
