package com.gagein.ui.newsfilter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.gagein.R;
import com.gagein.ui.company.CompanyActivity;
import com.gagein.ui.main.BaseActivity;
import com.gagein.ui.news.NewsActivity;
import com.gagein.util.Constant;
import com.gagein.util.Log;

public class FilterActivity extends BaseActivity {
	
	private Button companyGroupsBtn;
	private Button newsBtn;
//	private Button relevanceBtn;
	private RelativeLayout companyGroups;
	private RelativeLayout news;
//	private RelativeLayout relevance;
//	private Boolean moreNews;
	private String fromActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_newsfilter);
		
		doInit();
	}
	
	@Override
	protected void initView() {
		super.initView();
		setTitle(R.string.filters);
		setRightButton(R.string.done);
		
		companyGroupsBtn = (Button) findViewById(R.id.companyGroupsBtn);
		newsBtn = (Button) findViewById(R.id.newsBtn);
//		relevanceBtn = (Button) findViewById(R.id.relevanceBtn);
		companyGroups = (RelativeLayout) findViewById(R.id.companyGroups);
		news = (RelativeLayout) findViewById(R.id.news);
//		relevance = (RelativeLayout) findViewById(R.id.relevance);
		
		fromActivity = getIntent().getStringExtra(Constant.ACTIVITYNAME); 
		Log.v("silen", "fromActivity = " + fromActivity);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		initData();
//		getRelevance();
	}
	
	@Override
	protected void setOnClickListener() {
		super.setOnClickListener();
		companyGroupsBtn.setOnClickListener(this);
		newsBtn.setOnClickListener(this);
//		relevanceBtn.setOnClickListener(this);
		companyGroups.setOnClickListener(this);
		news.setOnClickListener(this);
//		relevance.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		
		if (v == rightBtn) {
			
			if (fromActivity.equals("CompanyActivity")) {
				Intent intent = new Intent(FilterActivity.this, CompanyActivity.class);
				setResult(RESULT_OK, intent);
				finish();
			} else if (fromActivity.equals("NewsActivity")) {
				Intent intent = new Intent(FilterActivity.this, NewsActivity.class);
				setResult(RESULT_OK, intent);
				finish();
			}
		} else if (v == companyGroups || v == companyGroupsBtn) {//TODO
			
			startActivitySimple(GroupsFilterActivity.class);
			
		} else if (v == news || v == newsBtn) {
			
			startActivitySimple(FilterNewsActivity.class);
			
		}
//		else if (v == relevance || v == relevanceBtn) {
//			
//			Intent intent = new Intent();
//			intent.setClass(mContext, FilterRelevanceActivity.class);
//			startActivityForResult(intent, 0);
//			
//		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (resultCode == RESULT_OK) {
			if (requestCode == 0) {
//				String action = intent.getAction();
//				relevanceBtn.setText(mContext.getResources().getString(action.equalsIgnoreCase("true") ? R.string.more_news: R.string.more_relevant));
			}
		}
	}
	
//	private void getRelevance() {
//		mApiHttp.getFilterRelevance(new Listener<JSONObject>() {
//
//			@Override
//			public void onResponse(JSONObject jsonObject) {
//				
//				APIParser parser = new APIParser(jsonObject);
//				if (parser.isOK()) {
//					int relevance = parser.data().optInt("relevance_value");
//					if (APIHttpMetadata.kGGCompanyUpdateRelevanceNormal == relevance) {
//						moreNews = true;
//					} else {
//						moreNews = false;
//					}
//					setRelevanceText();
//				}
//			}
//			
//		}, new Response.ErrorListener() {
//
//			@Override
//			public void onErrorResponse(VolleyError error) {
//				showConnectionError();
//			}
//		});
//	}
	
//	private void setRelevanceText() {
//		relevanceBtn.setText(mContext.getResources().getString(moreNews ? R.string.more_news: R.string.more_relevant));
//	}
}
