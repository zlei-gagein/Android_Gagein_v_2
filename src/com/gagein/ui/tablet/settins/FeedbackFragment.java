package com.gagein.ui.tablet.settins;

import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
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
import com.gagein.ui.BaseFragment;
import com.gagein.util.CommonUtil;
import com.gagein.util.Constant;

public class FeedbackFragment extends BaseFragment implements OnClickListener{
	
	private Button categoryBtn;
	private LinearLayout importanceLayout;
	private LinearLayout subjectLayout;
	private EditText subjectEdt;
	private EditText msgEdt;
	private RadioButton minorBtn;
	private RadioButton majorBtn;
	private RadioButton criticalBtn;
	private int categoryType = 0;
	private String importance;
	private OnCategoryListener onCategoryListener;
	
	public interface OnCategoryListener {
		public void onCategoryListener();
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		try {
			onCategoryListener = (OnCategoryListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + "must implement onCategoryListener");
		}
		
	}
	
	private void resetAllStatus() {
		
		Constant.CURRENT_CATEGORY_TYPE_IN_SETTINGS = 0;
		
		categoryBtn.setText("");
		importanceLayout.setVisibility(View.GONE);
		subjectEdt.setText("");
		msgEdt.setText("");
		subjectEdt.clearFocus();
		msgEdt.clearFocus();
		minorBtn.setChecked(true);
		majorBtn.setChecked(false);
		criticalBtn.setChecked(false);
		subjectLayout.setVisibility(View.GONE);
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.activity_feedback, container, false);
		
		doInit();
		return view;
	}
	
	@Override
	protected void initView() {
		super.initView();

		setTitle(R.string.feedback);
		setRightButton(R.string.send);
		
		categoryBtn = (Button) view.findViewById(R.id.categoryBtn);
		importanceLayout = (LinearLayout) view.findViewById(R.id.importanceLayout);
		subjectLayout = (LinearLayout) view.findViewById(R.id.subjectLayout);
		subjectEdt = (EditText) view.findViewById(R.id.subjectEdt);
		msgEdt = (EditText) view.findViewById(R.id.msgEdt);
		minorBtn = (RadioButton) view.findViewById(R.id.minorBtn);
		majorBtn = (RadioButton) view.findViewById(R.id.majorBtn);
		criticalBtn = (RadioButton) view.findViewById(R.id.criticalBtn);
		
	}
	
	@Override
	protected void setOnClickListener() {
		super.setOnClickListener();
		categoryBtn.setOnClickListener(this);
		minorBtn.setOnClickListener(this);
		majorBtn.setOnClickListener(this);
		criticalBtn.setOnClickListener(this);
	}
	
	public void setCategoryButton() {
		
		int type = Constant.CURRENT_CATEGORY_TYPE_IN_SETTINGS;
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
		
		showLoadingDialog(mContext);
		mApiHttp.sendFeedback(categoryType, importance, message, subject, new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject jsonObject) {
				
				APIParser parser = new APIParser(jsonObject);
				if (parser.isOK()) {
					
					showShortToast(R.string.feedback_success);
					resetAllStatus();
					
				}
				dismissLoadingDialog();
			}
			
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				showConnectionError(mContext);
			}
		});
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		
		if (v == categoryBtn) {
			
			Constant.CURRENT_CATEGORY_TYPE_IN_SETTINGS = categoryType;
			onCategoryListener.onCategoryListener();
			
			
		} else if (v == rightBtn) {
			
			categoryType = Constant.CURRENT_CATEGORY_TYPE_IN_SETTINGS;
			
			boolean canShowExtra = (APIHttpMetadata.kGGFeedbackCategoryBug == categoryType 
					|| APIHttpMetadata.kGGFeedbackCategoryFeatureRequest == categoryType 
					|| APIHttpMetadata.kGGFeedbackCategoryImprovement == categoryType);
			
			String message = msgEdt.getText().toString().trim();
			String subject = subjectEdt.getText().toString().trim();
			if (categoryType == 0) {
				CommonUtil.showPromotDialog(mContext, R.string.requiry_category);
				return;
			} else {
				if (canShowExtra && TextUtils.isEmpty(subject)) {
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
			
			sendFeedback(categoryType, importance, message, subject);
		}
	}
}
