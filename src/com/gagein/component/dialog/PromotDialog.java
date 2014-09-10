package com.gagein.component.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.gagein.R;
import com.gagein.util.CommonUtil;

/**
 * 
 * @author silen
 */
public class PromotDialog implements OnClickListener {
	private Context mContext;
	private Dialog dialog;
	private View view;
	private LayoutInflater inflater;
	private Button okay;
	private TextView promotTxt;

	/**
	 * 
	 * @param mContext
	 */
	public PromotDialog(Context mContext) {
		this.mContext = mContext;
		dialog = new Dialog(mContext, R.style.dialog);
		inflater = LayoutInflater.from(mContext);
		view = inflater.inflate(R.layout.dialog_promot, null);
		okay = (Button) view.findViewById(R.id.okay);
		promotTxt = (TextView) view.findViewById(R.id.promotTxt);
		okay.setOnClickListener(this);
		dialog.setContentView(view);
	}

	public void showDialog(String promot) {
		promotTxt.setText(promot);
		CommonUtil.setDialogWith(dialog);
		dialog.setCancelable(false);
		dialog.show();
	}

	@Override
	public void onClick(View v) {
		if (v == okay) {
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
