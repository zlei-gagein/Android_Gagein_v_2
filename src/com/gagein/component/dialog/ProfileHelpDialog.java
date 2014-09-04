package com.gagein.component.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.gagein.R;
import com.gagein.http.APIHttp;
import com.gagein.util.CommonUtil;

/**
 * 
 * @author silen
 */
public class ProfileHelpDialog implements OnClickListener{
	private Context mContext;
	private Dialog dialog;
	private View view;
	private LayoutInflater inflater;
	private APIHttp mApiHttp;
	private Button cancel;
	private Button save;
	private TextView match;
	private TextView enter;
	private ListView listView;
	private EditText websiteEdt;

	public ProfileHelpDialog(Context context) {
		dialog = new Dialog(context, R.style.dialog);
		this.mContext = context;
		mApiHttp = new APIHttp(mContext);
		inflater = LayoutInflater.from(context);
		view = inflater.inflate(R.layout.dialog_profile_help, null);
		cancel = (Button) view.findViewById(R.id.cancel);
		save = (Button) view.findViewById(R.id.save);
		match = (TextView) view.findViewById(R.id.match);
		enter = (TextView) view.findViewById(R.id.enter);
		listView = (ListView) view.findViewById(R.id.listView);
		websiteEdt = (EditText) view.findViewById(R.id.websiteEdt);
		
		dialog.setContentView(view);
	}

	public void showDialog() {
		cancel.setOnClickListener(this);
		CommonUtil.setDialogWith(dialog);
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
	public void onClick(View v) {
		if (v == cancel) {
			dismissDialog();
		}
	}

}
