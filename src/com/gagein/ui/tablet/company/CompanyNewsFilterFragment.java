package com.gagein.ui.tablet.company;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.gagein.R;
import com.gagein.http.APIHttp;
import com.gagein.http.APIHttpMetadata;
import com.gagein.http.APIParser;
import com.gagein.ui.BaseFragment;

public class CompanyNewsFilterFragment extends BaseFragment implements OnClickListener {
	
	private Button newsBtn;
	private Button relevanceBtn;
	private RelativeLayout news;
	private RelativeLayout relevance;
	private Boolean moreNews;
	private Context mContext;
	private OnNewsFilterLeftBtnClickListener newsFilterLeftBtnListener;
	private NewsBtnClickListener newsBtnListener;
	private RelevanceBtnClickListener relevanceBtnListener;
	private CloseLeftLayoutListener closeLeftLayoutListener;
	
	
	public interface OnNewsFilterLeftBtnClickListener {
		public void onNewsFilterLeftbtnClickListener();
	}
	
	public interface NewsBtnClickListener {
		public void onNewsBtnListener();
	}
	
	public interface RelevanceBtnClickListener {
		public void onRelevanceBtnListener();
	}
	
	public interface CloseLeftLayoutListener {
		public void closeLeftLayoutListener();
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			newsFilterLeftBtnListener = (OnNewsFilterLeftBtnClickListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + "must implement OnNewsFilterLeftBtnClickListener");
		}
		try {
			newsBtnListener = (NewsBtnClickListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + "must implement NewsBtnClickListener");
		}
		try {
			relevanceBtnListener = (RelevanceBtnClickListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + "must implement RelevanceBtnClickListener");
		}
		try {
			closeLeftLayoutListener = (CloseLeftLayoutListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + "must implement CloseLeftLayoutListener");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.activity_newsfilter, container, false);
		mContext = getActivity();
		mApiHttp = new APIHttp(mContext);
		doInit();
		getRelevance();
		return view;
	}
	
	@Override
	protected void initView() {
		super.initView();
		
		newsBtn = (Button) view.findViewById(R.id.newsBtn);
		relevanceBtn = (Button) view.findViewById(R.id.relevanceBtn);
		news = (RelativeLayout) view.findViewById(R.id.news);
		relevance = (RelativeLayout) view.findViewById(R.id.relevance);
		
//		setLeftImageButton(R.drawable.back_arrow);
		setRightButton(R.string.done);
		setTitle(R.string.news_triggers);
	}
	
	@Override
	protected void setOnClickListener() {
		super.setOnClickListener();
		newsBtn.setOnClickListener(this);
		relevanceBtn.setOnClickListener(this);
		news.setOnClickListener(this);
		relevance.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		
		if (v == leftImageBtn) {
			
			back();
			
		} else if (v == rightBtn) {
			
			closeLeftLayoutListener.closeLeftLayoutListener();
			
		} else if (v == news || v == newsBtn) {
			
			newsBtnListener.onNewsBtnListener();
			
		} else if (v == relevance || v == relevanceBtn) {
			
			relevanceBtnListener.onRelevanceBtnListener();
			
		}
	}

	private void back() {
		newsFilterLeftBtnListener.onNewsFilterLeftbtnClickListener();
	}
	
	public void getRelevance() {
		mApiHttp.getFilterRelevance(new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject jsonObject) {
				
				APIParser parser = new APIParser(jsonObject);
				if (parser.isOK()) {
					int relevance = parser.data().optInt("relevance_value");
					if (APIHttpMetadata.kGGCompanyUpdateRelevanceNormal == relevance) {
						moreNews = true;
					} else {
						moreNews = false;
					}
					setRelevanceText();
				}
			}
			
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				showConnectionError(mContext);
			}
		});
	}
	
	private void setRelevanceText() {
		relevanceBtn.setText(mContext.getResources().getString(moreNews ? R.string.more_news: R.string.more_relevant));
	}
}
