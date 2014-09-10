package com.gagein.component.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

import com.gagein.R;
import com.gagein.util.CommonUtil;

public class FollowAndGroupDialog implements OnClickListener{
	private Dialog dialog;
	private View view;
	private LayoutInflater inflater;
	private Button follow;
	private Button followGroup;
	private Button cancel;
	private Context mContext;

	public FollowAndGroupDialog(Context context) {
		this.mContext = context;
		dialog = new Dialog(context, R.style.dialog);
		inflater = LayoutInflater.from(context);
		view = inflater.inflate(R.layout.dialog_follow_group, null);
		follow = (Button) view.findViewById(R.id.follow);
		followGroup = (Button) view.findViewById(R.id.followGroup);
		cancel = (Button) view.findViewById(R.id.cancel);
		cancel.setOnClickListener(this);
		dialog.setContentView(view);
	}

	public void showDialog(View.OnClickListener followListener, View.OnClickListener followGroupListener) {
		follow.setOnClickListener(followListener);
		followGroup.setOnClickListener(followGroupListener);
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
