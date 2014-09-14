package com.gagein.ui.newsfilter;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
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

public class FilterRelevanceActivity extends BaseActivity implements OnItemClickListener{
	
	private ListView listView;
	private FilterRelevanceAdapter adapter;
	private int relevance;
	private int selectionChanged = 0;
	private Boolean moreNews;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_filter_relevance);
		
		doInit();
	}
	
	@Override
	protected void initView() {
		super.initView();
		
		setTitle(R.string.u_relevance);
		setLeftImageButton(R.drawable.back_arrow);
		setRightButton(R.string.done);
		
		listView = (ListView) findViewById(R.id.listView);
	}
	
	@Override
	protected void initData() {
		super.initData();
		
		getRelevance();
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
			finish();
		} else if (v == rightBtn) {
			if (selectionChanged != 0) {
				saveRelevance();
			} else {
				finish();
			}
		}
	}
	
	@Override
	protected void setData() {
		super.setData();
		
		adapter = new FilterRelevanceAdapter(mContext, moreNews);
		listView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		adapter.notifyDataSetInvalidated();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		selectionChanged = 1;
		
		moreNews = !moreNews;
		setData();
	}
	
	private void getRelevance() {
		showLoadingDialog();
		mApiHttp.getFilterRelevance(new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject jsonObject) {
				
				APIParser parser = new APIParser(jsonObject);
				if (parser.isOK()) {
					relevance = parser.data().optInt("relevance_value");
					if (APIHttpMetadata.kGGCompanyUpdateRelevanceNormal == relevance) {
						moreNews = true;
					} else {
						moreNews = false;
					}
					setData();
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
	
	private void saveRelevance() {
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
					Intent intent = new Intent(FilterRelevanceActivity.this, FilterActivity.class);
					intent.setAction(moreNews + "");
					setResult(RESULT_OK, intent);
					finish();
				} else {
					alertMessageForParser(parser);
				}
				
				dismissLoadingDialog();
				finish();
			}
			
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				showConnectionError();
			}
		});
	}

}
