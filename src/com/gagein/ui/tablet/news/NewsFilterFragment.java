package com.gagein.ui.tablet.news;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.gagein.R;
import com.gagein.http.APIHttp;
import com.gagein.ui.BaseFragment;

public class NewsFilterFragment extends BaseFragment implements OnClickListener {
	
	private Button newsBtn;
	private RelativeLayout news;
	private Context mContext;
	private OnNewsFilterLeftBtnClickListener newsFilterLeftBtnListener;
	private NewsBtnClickListener newsBtnListener;
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
		return view;
	}
	
	@Override
	protected void initView() {
		super.initView();
		
		newsBtn = (Button) view.findViewById(R.id.newsBtn);
		news = (RelativeLayout) view.findViewById(R.id.news);
		
		setRightButton(R.string.done);
		setTitle(R.string.filters);
	}
	
	@Override
	protected void setOnClickListener() {
		super.setOnClickListener();
		newsBtn.setOnClickListener(this);
		news.setOnClickListener(this);
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
		}
	}

	private void back() {
		newsFilterLeftBtnListener.onNewsFilterLeftbtnClickListener();
	}
	
	
}
