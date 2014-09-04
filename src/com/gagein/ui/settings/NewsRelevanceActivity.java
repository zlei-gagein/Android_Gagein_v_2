package com.gagein.ui.settings;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.gagein.R;
import com.gagein.adapter.FilterRelevanceAdapter;
import com.gagein.http.APIHttpMetadata;
import com.gagein.http.APIParser;
import com.gagein.ui.main.BaseActivity;
import com.gagein.util.Constant;

public class NewsRelevanceActivity extends BaseActivity implements OnItemClickListener{
	
	private ListView listView;
	private FilterRelevanceAdapter adapter;
	private Boolean moreNews;
	private int resultCode = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_category);
		
		doInit();
	}
	
	@Override
	protected void initView() {
		super.initView();
		setTitle(R.string.news_relevance);
		setLeftImageButton(R.drawable.back_arrow);
		setRightButton(R.string.done);
		
		listView = (ListView) findViewById(R.id.listView);
	}
	
	@Override
	protected void initData() {
		super.initData();
		
		moreNews = getIntent().getBooleanExtra(Constant.MORENEWS, false);
		
		setData();
	}
	
	@Override
	protected void setData() {
		super.setData();
		
		adapter = new FilterRelevanceAdapter(mContext, moreNews);
		listView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		
	}
	
	@Override
	protected void setOnClickListener() {
		super.setOnClickListener();
		
		listView.setOnItemClickListener(this);
		
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		
		if (v == leftImageBtn) {
			setResult();
			finish();
		} else if (v == rightBtn) {
			setResult();
			finish();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
		if ((position == 0 && moreNews) || (position == 1 && !moreNews)) {
			moreNews = !moreNews;
			setData();
			saveRelevance();
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			setResult();
        }
		return super.onKeyDown(keyCode, event);
	}
	
	private void setResult() {
		Intent intent = new Intent(mContext, SettingsNewsFilterActivity.class);
		intent.putExtra(Constant.MORENEWS, moreNews);
		setResult(resultCode, intent);
	}
	
	private void saveRelevance() {
		int relevance = 0;
		if (moreNews) {
			relevance = APIHttpMetadata.kGGCompanyUpdateRelevanceNormal;
		} else {
			relevance = APIHttpMetadata.kGGCompanyUpdateRelevanceVeryHigh;
		}
		showLoadingDialog();
		mApiHttp.setFilterRelevance(relevance, new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject jsonObject) {
				
				APIParser parser = new APIParser(jsonObject);
				if (parser.isOK()) {
				} else {
					alertMessageForParser(parser);
					moreNews = !moreNews;
					setData();
				}
				
				dismissLoadingDialog();
			}
			
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				showConnectionError();
				moreNews = !moreNews;
				setData();
			}
		});
	}

}
