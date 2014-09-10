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
public class VerifingWebsiteConnectTimeOutDialog {
	private Context mContext;
	private Dialog dialog;
	private View view;
	private LayoutInflater inflater;
	private Button okay;
	private TextView verifyingWebsite;

	/**
	 * 
	 * @param mContext
	 */
	public VerifingWebsiteConnectTimeOutDialog(Context mContext) {
		this.mContext = mContext;
		
		dialog = new Dialog(mContext, R.style.dialog);
		inflater = LayoutInflater.from(mContext);
		view = inflater.inflate(R.layout.dialog_verifingwebsite_connect_timeout, null);
		okay = (Button) view.findViewById(R.id.okay);
		verifyingWebsite = (TextView) view.findViewById(R.id.verifyingWebsite);
		dialog.setContentView(view);
		
	}

	public void showDialog(String website, OnClickListener clickListener) {
		
		okay.setOnClickListener(clickListener);
		verifyingWebsite.setText(String.format(mContext.getResources().getString(R.string.verifying_website), website));
		CommonUtil.setDialogWith(dialog);
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

	public void setCancelable(boolean is) {
		dialog.setCancelable(is);
	}

}
