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

public class RemoveFromGroupDialog implements OnClickListener{
	private Dialog dialog;
	private View view;
	private LayoutInflater inflater;
	private Button remove;
	private Button cancel;
	private TextView removePt;
	private Context mContext;

	public RemoveFromGroupDialog(Context context) {
		this.mContext = context;
		dialog = new Dialog(context, R.style.dialog);
		inflater = LayoutInflater.from(context);
		view = inflater.inflate(R.layout.dialog_remove_from_group, null);
		remove = (Button) view.findViewById(R.id.remove);
		cancel = (Button) view.findViewById(R.id.cancel);
		removePt = (TextView) view.findViewById(R.id.removePt);
		cancel.setOnClickListener(this);
		dialog.setContentView(view);
	}
	
	public void setRemovePt(Boolean isSingular) {
		String removeText = mContext.getResources().getString(R.string.remove_from_group_pt);
		removeText = String.format(removeText, isSingular ? "company" : "companies");
		removePt.setText(removeText);
	}

	public void showDialog(View.OnClickListener removeListener) {
		remove.setOnClickListener(removeListener);
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

	@Override
	public void onClick(View view) {
		if (view == cancel) {
			dismissDialog();
		}
	}
}
