package com.gagein.ui.tablet.company;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.gagein.R;
import com.gagein.adapter.CompetitorIndustryAdapter;
import com.gagein.http.APIHttp;
import com.gagein.model.FacetItemIndustry;
import com.gagein.ui.BaseFragment;
import com.gagein.util.Constant;

public class CompetitorFilterIndustryFragment extends BaseFragment implements OnItemClickListener{
	
	private CompetitorIndustryAdapter adapter;
	private ListView listView;
	private List<FacetItemIndustry> industries;
	private OnShowCompetitorsFilterFromIndustryListener onShowCompetitorsFilterFromIndustryListener; 
	private OnRefreshCompetitors onRefreshCompetitors; 
	
	public interface OnShowCompetitorsFilterFromIndustryListener {
		public void onShowCompetitorsFilterFromIndustryListener();
	}
	public interface OnRefreshCompetitors {
		public void onRefreshCompetitors();
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			onShowCompetitorsFilterFromIndustryListener = (OnShowCompetitorsFilterFromIndustryListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + "must implement OnShowCompetitorsFilterFromIndustryListener");
		}
		try {
			onRefreshCompetitors = (OnRefreshCompetitors) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + "must implement OnRefreshCompetitors");
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
		
		setTitle(R.string.industry);
		setLeftImageButton(R.drawable.back_arrow);
		setRightButton(R.string.done);
		
		listView = (ListView) view.findViewById(R.id.listView);
	}
	
	@Override
	protected void initData() {
		super.initData();
		
		industries = Constant.currentCompetitorIndustries;
		
		setData();
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
			onShowCompetitorsFilterFromIndustryListener.onShowCompetitorsFilterFromIndustryListener();
		} else if (v == rightBtn) {
			onShowCompetitorsFilterFromIndustryListener.onShowCompetitorsFilterFromIndustryListener();
		}
	}
	
	@Override
	protected void setData() {
		super.setData();
		
		adapter = new CompetitorIndustryAdapter(mContext, industries);
		listView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		adapter.notifyDataSetInvalidated();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		
		Boolean checked = industries.get(position).getSelected();
		if (position == 0) {
			Boolean haveChecked = false;
			for (int i = 0; i < industries.size(); i ++) {
				if (i == 0) continue;
				if (industries.get(i).getSelected()) {
					haveChecked = true;
					break;
				}
			}
			if (!haveChecked) {
				return;
			} else {
				industries.get(position).setSelected(true);
				
				for (int i = 0; i < industries.size(); i ++) {
					if (i != 0) industries.get(i).setSelected(false);
				}
			}
		} else {
			
			//circle 
			industries.get(position).setSelected(!checked);
			Boolean haveChecked = false;
			for (int i = 0; i < industries.size(); i ++) {
				if (i == 0) {
					continue;
				} else {
					if (industries.get(i).getSelected()) {
						haveChecked = true;
						industries.get(0).setSelected(false);
					}
				}
			}
			if (!haveChecked) {
				industries.get(0).setSelected(true);
			}
		}
		
		adapter.notifyDataSetChanged();//refresh listview state
		
		int j = 0;
		List<String> industryIds = new ArrayList<String>();
		for (int i= 0; i < industries.size(); i ++) {
			if (industries.get(i).getSelected() == true) {
				industryIds.add(j, industries.get(i).filter_param_value);
				j ++;
			}
		}
		Constant.COMPETITOR_FILTER_PARAM_VALUE = industryIds;
		
		onRefreshCompetitors.onRefreshCompetitors();
	}

}
