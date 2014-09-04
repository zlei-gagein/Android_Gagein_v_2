package com.gagein.component.dialog;

import java.util.ArrayList;

import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.gagein.R;
import com.gagein.util.CommonUtil;

/**
 * 
 * @author silen
 */
public class AddNewCompanyDialog implements OnClickListener {
	private Context mContext;
	private Dialog dialog;
	private View view;
	private LayoutInflater inflater;
	private int layout;
	private EditText nameEdt;
	private EditText websiteEdt;
	private Button cancel;
	private Button add;

	/**
	 * 
	 * @param mContext
	 */
	public AddNewCompanyDialog(final Context mContext) {
		this.mContext = mContext;
		layout = R.layout.dialog_add_new_company;
		dialog = new Dialog(mContext, R.style.dialog);
		inflater = LayoutInflater.from(mContext);
		view = inflater.inflate(layout, null);
		nameEdt = (EditText) view.findViewById(R.id.nameEdt);
		websiteEdt = (EditText) view.findViewById(R.id.websiteEdt);
		cancel = (Button) view.findViewById(R.id.cancel);
		add = (Button) view.findViewById(R.id.add);
		dialog.setContentView(view);
		
		
		nameEdt.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void afterTextChanged(Editable s) {
				setSaveButtonColor();
			}
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
		});
		
		websiteEdt.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void afterTextChanged(Editable s) {
				setSaveButtonColor();
			}
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
		});
	}

	private void setSaveButtonColor() {
		Boolean allFiledEntered = !TextUtils.isEmpty(nameEdt.getText().toString()) && !TextUtils.isEmpty(websiteEdt.getText().toString());
		add.setTextColor(mContext.getResources().getColor(allFiledEntered ? R.color.blue_dialog : R.color.C7C7C7));
	}

	public void showDialog(OnClickListener addClickListener) {
		cancel.setOnClickListener(this);
		add.setOnClickListener(addClickListener);
		CommonUtil.setDialogWith(dialog);
		dialog.setCancelable(false);
		dialog.show();
	}
	
	public ArrayList<String> getNameAndWebsite() {
		String name = nameEdt.getText().toString().trim();
		String website = websiteEdt.getText().toString().trim();
		if (TextUtils.isEmpty(name)) {
			CommonUtil.showShortToast(mContext.getResources().getString(R.string.pls_input_name));
			return null;
		}
		if (TextUtils.isEmpty(website)) {
			CommonUtil.showShortToast(mContext.getResources().getString(R.string.pls_input_website));
			return null;
		}
		ArrayList<String> nameWebsite = new ArrayList<String>();
		nameWebsite.add(name);
		nameWebsite.add(website);
		return nameWebsite;
	}
	
	@Override
	public void onClick(View v) {
		if (v == cancel) {
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
