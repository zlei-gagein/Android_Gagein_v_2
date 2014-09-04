package com.gagein.ui.tablet.news;

import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.gagein.R;
import com.gagein.adapter.FilterRelevanceAdapter;
import com.gagein.http.APIHttp;
import com.gagein.http.APIHttpMetadata;
import com.gagein.http.APIParser;
import com.gagein.ui.BaseFragment;

public class FilterRelevanceFragment extends BaseFragment implements OnItemClickListener{
	
	private ListView listView;
	private FilterRelevanceAdapter adapter;
	private int relevance;
	private int selectionChanged = 0;
	private Boolean moreNews;
	private OnFilterRelevanceLeftBtnClickListener filterRelevanceLeftBtnListener;
	private RefreshNewsFilterFromRelevance refreshNewsFilterFromRelevance;
	
	
	public interface OnFilterRelevanceLeftBtnClickListener {
		public void onFilterRelevanceLeftBtnClickListener();
	}
	
	public interface RefreshNewsFilterFromRelevance {
		public void refreshNewsFilterFromRelevance();
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			filterRelevanceLeftBtnListener = (OnFilterRelevanceLeftBtnClickListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + "must implement OnFilterRelevanceLeftBtnClickListener");
		}
		try {
			refreshNewsFilterFromRelevance = (RefreshNewsFilterFromRelevance) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + "must implement RefreshNewsFilterFromRelevance");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.activity_filter_news, container, false);
		mContext = getActivity();
		mApiHttp = new APIHttp(mContext);
		doInit();
		return view;
	}
	
	@Override
	protected void initView() {
		super.initView();
		
		setTitle(R.string.u_relevance);
		setLeftImageButton(R.drawable.back_arrow);
		setRightButton(R.string.done);
		
		listView = (ListView) view.findViewById(R.id.listView);
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
			back();
		} else if (v == rightBtn) {
			if (selectionChanged != 0) {
				saveRelevance();
			} else {
				back();
			}
		}
	}

	private void back() {
		filterRelevanceLeftBtnListener.onFilterRelevanceLeftBtnClickListener();
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
		showLoadingDialog(mContext);
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
				showConnectionError(mContext);
			}
		});
	}
	
	private void saveRelevance() {
		if (moreNews) {
			relevance = APIHttpMetadata.kGGCompanyUpdateRelevanceNormal;
		} else {
			relevance = APIHttpMetadata.kGGCompanyUpdateRelevanceVeryHigh;
		}
		showLoadingDialog(mContext);
		mApiHttp.setFilterRelevance(relevance, new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject jsonObject) {
				
				APIParser parser = new APIParser(jsonObject);
				if (parser.isOK()) {
				} else {
					alertMessageForParser(parser);
				}
				
				dismissLoadingDialog();
				back();
				refreshNewsFilterFromRelevance.refreshNewsFilterFromRelevance();
			}
			
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				showConnectionError(mContext);
			}
		});
	}
}
