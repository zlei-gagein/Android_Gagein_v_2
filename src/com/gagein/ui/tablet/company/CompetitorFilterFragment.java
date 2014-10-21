package com.gagein.ui.tablet.company;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.gagein.R;
import com.gagein.http.APIHttp;
import com.gagein.ui.BaseFragment;

public class CompetitorFilterFragment extends BaseFragment {
	
	private RelativeLayout industry;
	private Button industryBtn;
	private OnCompetitorFilterCancleBtnClickListener onCompetitorFilterCancleBtnClickListener;
	private OnCompetitorIndustryClickListener onCompetitorIndustryClickListener;
	
	public interface OnCompetitorFilterCancleBtnClickListener {
		public void onCompetitorFilterCancleBtnClickListener();
	}
	
	public interface OnCompetitorIndustryClickListener {
		public void onCompetitorIndustryClickListener();
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			onCompetitorFilterCancleBtnClickListener = (OnCompetitorFilterCancleBtnClickListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + "must implement OnCompetitorFilterCancleBtnClickListener");
		}
		try {
			onCompetitorIndustryClickListener = (OnCompetitorIndustryClickListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + "must implement OnCompetitorIndustryClickListener");
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.activity_competitorfilter, container, false);
		mContext = getActivity();
		mApiHttp = new APIHttp(mContext);
		
		doInit();
		return view;
	}
	
	@Override
	protected void initView() {
		super.initView();
		setTitle(R.string.filters);
		setRightButton(R.string.done);
		
		industry = (RelativeLayout) view.findViewById(R.id.industry);
		industryBtn = (Button) view.findViewById(R.id.industryBtn);
		
	}
	
//	@OVERRIDE
//	PROTECTED VOID INITDATA() {
//		SUPER.INITDATA();
//		
//		SETINDUSTRY();
//	}
//	
//	PRIVATE VOID SETINDUSTRY() {
//		LIST<FACETITEMINDUSTRY> INDUSTRIES = CONSTANT.CURRENTCOMPETITORINDUSTRIES;
//		FOR (INT I = 0; I < INDUSTRIES.SIZE(); I ++) {
//			IF (CONSTANT.COMPETITOR_FILTER_PARAM_VALUE.EQUALSIGNORECASE(INDUSTRIES.GET(I).FILTER_PARAM_VALUE)) {
//				INDUSTRYBTN.SETTEXT(INDUSTRIES.GET(I).ITEM_NAME);
//			}
//		}
//	}
	
	@Override
	protected void setOnClickListener() {
		super.setOnClickListener();
		industry.setOnClickListener(this);
		industryBtn.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		if (v == leftBtn) {//TODO
			onCompetitorFilterCancleBtnClickListener.onCompetitorFilterCancleBtnClickListener();
		} else if (v == rightBtn) {//TODO
			onCompetitorFilterCancleBtnClickListener.onCompetitorFilterCancleBtnClickListener();
		} else if (v == industry || v == industryBtn) {
			onCompetitorIndustryClickListener.onCompetitorIndustryClickListener();
		}
	}

}
