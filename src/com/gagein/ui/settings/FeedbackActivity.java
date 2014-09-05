package com.gagein.ui.settings;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.gagein.R;
import com.gagein.http.APIHttpMetadata;
import com.gagein.http.APIParser;
import com.gagein.ui.main.BaseActivity;
import com.gagein.util.CommonUtil;
import com.gagein.util.Constant;
import com.gagein.util.Log;

public class FeedbackActivity extends BaseActivity {
	
	private Button categoryBtn;
	private LinearLayout importanceLayout;
	private LinearLayout subjectLayout;
	private EditText subjectEdt;
	private EditText msgEdt;
	private RadioButton minorBtn;
	private RadioButton majorBtn;
	private RadioButton criticalBtn;
	private int requestCode = 1;
	private int categoryType = 0;
	private String importance;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feedback);
		
		doInit();
	}
	
	@Override
	protected void initView() {
		super.initView();
		setTitle(R.string.feedback);
		setLeftImageButton(R.drawable.back_arrow);
		setRightButton(R.string.send);
		
		categoryBtn = (Button) findViewById(R.id.categoryBtn);
		importanceLayout = (LinearLayout) findViewById(R.id.importanceLayout);
		subjectLayout = (LinearLayout) findViewById(R.id.subjectLayout);
		subjectEdt = (EditText) findViewById(R.id.subjectEdt);
		msgEdt = (EditText) findViewById(R.id.msgEdt);
		minorBtn = (RadioButton) findViewById(R.id.minorBtn);
		majorBtn = (RadioButton) findViewById(R.id.majorBtn);
		criticalBtn = (RadioButton) findViewById(R.id.criticalBtn);
	}
	
	@Override
	protected void initData() {
		super.initData();
	}
	
	@Override
	protected void setOnClickListener() {
		super.setOnClickListener();
		categoryBtn.setOnClickListener(this);
		minorBtn.setOnClickListener(this);
		majorBtn.setOnClickListener(this);
		criticalBtn.setOnClickListener(this);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == this.requestCode) {
			categoryType = (null == data) ? 0 : data.getIntExtra(Constant.Category, 0);
			setCategoryButton(categoryType);
		}
	}
	
	private void setCategoryButton(int type) {
		if (APIHttpMetadata.kGGFeedbackCategoryBug == type) {
			categoryBtn.setText(CommonUtil.stringFromResID(R.string.u_bug));
		} else if (APIHttpMetadata.kGGFeedbackCategoryComment == type) {
			categoryBtn.setText(CommonUtil.stringFromResID(R.string.general_comment));
		} else if (APIHttpMetadata.kGGFeedbackCategoryFeatureRequest == type) {
			categoryBtn.setText(CommonUtil.stringFromResID(R.string.new_feature_request));
		} else if (APIHttpMetadata.kGGFeedbackCategoryImprovement == type) {
			categoryBtn.setText(CommonUtil.stringFromResID(R.string.u_improvement));
		} else if (APIHttpMetadata.kGGFeedbackCategoryQuestion == type) {
			categoryBtn.setText(CommonUtil.stringFromResID(R.string.u_question));
		} 
		
		boolean canShowExtra = (APIHttpMetadata.kGGFeedbackCategoryBug == type 
				|| APIHttpMetadata.kGGFeedbackCategoryFeatureRequest == type 
				|| APIHttpMetadata.kGGFeedbackCategoryImprovement == type);
		importanceLayout.setVisibility(canShowExtra ? View.VISIBLE : View.GONE);
		subjectLayout.setVisibility(canShowExtra ? View.VISIBLE : View.GONE);
	}
	
	private void sendFeedback(int categoryType, String importance, String message, String subject) {
		showLoadingDialog();
		mApiHttp.sendFeedback(categoryType, importance, message, subject, new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject jsonObject) {
				APIParser parser = new APIParser(jsonObject);
				if (parser.isOK()) {
					showShortToast(R.string.feedback_success);
					finish();
				}
				dismissLoadingDialog();
			}
			
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				showConnectionError();
			}
		});
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		if (v == leftImageBtn) {
			finish();
		} else if (v == categoryBtn) {
			Intent intent = new Intent();
			intent.setClass(mContext, CategoryActivity.class);
			intent.putExtra(Constant.CategoryType, categoryType);
			startActivityForResult(intent, requestCode);
		} else if (v == rightBtn) {
			String message = msgEdt.getText().toString().trim();
			String subject = subjectEdt.getText().toString().trim();
			if (categoryType == 0) {
				CommonUtil.showPromotDialog(mContext, R.string.requiry_category);
				return;
			} else {
				if (TextUtils.isEmpty(subject)) {
					CommonUtil.showPromotDialog(mContext, R.string.requiry_subject);
					return;
				} else if (TextUtils.isEmpty(message)) {
					CommonUtil.showPromotDialog(mContext, R.string.requiry_message);
					return;
				}
			}
			
			if (minorBtn.isChecked()) { 
				importance = APIHttpMetadata.GG_FEEDBACK_IMPORTANCE_MINOR;
			} else if (majorBtn.isChecked()) {
				importance = APIHttpMetadata.GG_FEEDBACK_IMPORTANCE_MAJOR;
			} else if (criticalBtn.isChecked()) {
				importance = APIHttpMetadata.GG_FEEDBACK_IMPORTANCE_CRITICAL;
			}
			Log.v("silen", "importance = " + importance);
			sendFeedback(categoryType, importance, message, subject);
		}
	}

}
