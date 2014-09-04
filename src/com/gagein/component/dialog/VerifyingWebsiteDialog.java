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
public class VerifyingWebsiteDialog implements OnClickListener {
	private Context mContext;
	private Dialog dialog;
	private View view;
	private LayoutInflater inflater;
	private Button no;
	private Button yes;
	private TextView verifyingWebsite;

	/**
	 * 
	 * @param mContext
	 * @param enable true ? enable : closer
	 */
	public VerifyingWebsiteDialog(Context mContext) {
		this.mContext = mContext;
		dialog = new Dialog(mContext, R.style.dialog);
		inflater = LayoutInflater.from(mContext);
		view = inflater.inflate(R.layout.dialog_verifying_website, null);
		verifyingWebsite = (TextView) view.findViewById(R.id.verifyingWebsite);
		no = (Button) view.findViewById(R.id.no);
		no.setOnClickListener(this);
		yes = (Button) view.findViewById(R.id.yes);
		dialog.setContentView(view);
	}

	public void showDialog(String website, OnClickListener yesClickListener) {
		verifyingWebsite.setText(String.format(mContext.getResources().getString(R.string.verifying_website), website));
		yes.setOnClickListener(yesClickListener);
		CommonUtil.setDialogWith(dialog);
		dialog.show();
	}

	@Override
	public void onClick(View v) {
		if (v == no) {
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
