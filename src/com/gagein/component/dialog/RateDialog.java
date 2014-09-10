package com.gagein.component.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.gagein.R;
import com.gagein.ui.main.WebPageActivity;
import com.gagein.util.CommonUtil;
import com.gagein.util.Constant;

/**
 * 
 * @author silen
 */
public class RateDialog implements OnClickListener {
	private Context mContext;
	private Dialog dialog;
	private View view;
	private LayoutInflater inflater;
	private Button no;
	private Button rate;

	public RateDialog(Context mContext) {
		this.mContext = mContext;
		dialog = new Dialog(mContext, R.style.dialog);
		inflater = LayoutInflater.from(mContext);
		view = inflater.inflate(R.layout.dialog_rate, null);
		no = (Button) view.findViewById(R.id.no);
		rate = (Button) view.findViewById(R.id.rate);
		no.setOnClickListener(this);
		rate.setOnClickListener(this);
		dialog.setContentView(view);
	}

	public void showDialog() {
		CommonUtil.setDialogWith(dialog);
		dialog.setCancelable(false);
		dialog.show();
	}

	@Override
	public void onClick(View v) {
		if (v == no) {
			dismissDialog();
		} else if (v == rate) {
			Intent intent = new Intent();
			intent.setClass(mContext, WebPageActivity.class);
			intent.putExtra(Constant.URL, Constant.RATE_URL);
			mContext.startActivity(intent);
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
