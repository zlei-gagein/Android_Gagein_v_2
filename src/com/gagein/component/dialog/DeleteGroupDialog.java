package com.gagein.component.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.gagein.R;
import com.gagein.util.CommonUtil;

public class DeleteGroupDialog implements OnClickListener{
	private Dialog dialog;
	private View view;
	private LayoutInflater inflater;
	private Button delete;
	private Button cancel;
	private Context mContext;
	private TextView groupPt;

	public DeleteGroupDialog(Context context) {
		this.mContext = context;
		dialog = new Dialog(context, R.style.dialog);
		inflater = LayoutInflater.from(context);
		view = inflater.inflate(R.layout.dialog_delete_group, null);
		delete = (Button) view.findViewById(R.id.delete);
		cancel = (Button) view.findViewById(R.id.cancel);
		groupPt = (TextView) view.findViewById(R.id.groupPt);
		cancel.setOnClickListener(this);
		dialog.setContentView(view);
	}

	public void showDialog(String groupName, View.OnClickListener deleteListener) {
		String.format(groupPt.getText().toString(), groupName);
		groupPt.setText(String.format(groupPt.getText().toString(), groupName));
		delete.setOnClickListener(deleteListener);
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

	@Override
	public void onClick(View view) {
		if (view == cancel) {
			dismissDialog();
		}
	}
}
