package com.gagein.ui.tablet.search.people;

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
import com.gagein.adapter.search.FilterAdapter;
import com.gagein.http.APIHttp;
import com.gagein.model.filter.FilterItem;
import com.gagein.model.filter.Filters;
import com.gagein.ui.BaseFragment;
import com.gagein.util.Constant;

public class RankFragment extends BaseFragment implements OnItemClickListener{
	
	private ListView listView;
	private FilterAdapter adapter;
	private Filters mFilters;
	private List<FilterItem> mRanks;
	private OnRankFinish onRankFinish;
	private OnSearchFromRank onSearchFromRank;
	
	public void refreshAdapter() {
		if (null != adapter) adapter.notifyDataSetChanged();
 	}
	
	public interface OnRankFinish {
		public void onRankFinish();
	}
	public interface OnSearchFromRank {
		public void onSearchFromRank();
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			onRankFinish = (OnRankFinish) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + "must implement OnRankFinish");
		}
		try {
			onSearchFromRank = (OnSearchFromRank) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + "must implement OnSearchFromRank");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.activity_search_company_filter_employee_size, container, false);
		mContext = getActivity();
		mApiHttp = new APIHttp(mContext);
		
		doInit();
		return view;
	}
	
	@Override
	protected void initView() {
		super.initView();
		setTitle(R.string.rank);
		setLeftImageButton(R.drawable.back_arrow);
		
		listView = (ListView) view.findViewById(R.id.listView);
	}
	
	@Override
	protected void initData() {
		super.initData();
		
		mFilters = Constant.MFILTERS;
		mRanks = mFilters.getRanks();
		
		adapter = new FilterAdapter(mContext, mRanks);
		listView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		adapter.notifyDataSetInvalidated();
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
			onRankFinish.onRankFinish();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		Boolean checked = mRanks.get(position).getChecked();
		if (position == 0) {
			Boolean haveChecked = false;
			for (int i = 0; i < mRanks.size(); i ++) {
				if (i == 0) continue;
				if (mRanks.get(i).getChecked()) {
					haveChecked = true;
					break;
				}
			}
			if (!haveChecked) {
				return;
			} else {
				mRanks.get(position).setChecked(true);
				for (int i = 0; i < mRanks.size(); i ++) {
					if (i != 0) mRanks.get(i).setChecked(false);
				}
			}
		} else {
			mRanks.get(position).setChecked(!checked);
			Boolean haveChecked = false;
			for (int i = 0; i < mRanks.size(); i ++) {
				if (i == 0) {
					continue;
				} else {
					if (mRanks.get(i).getChecked()) {
						haveChecked = true;
						mRanks.get(0).setChecked(false);
					}
				}
			}
			if (!haveChecked) {
				mRanks.get(0).setChecked(true);
			}
		}
		adapter.notifyDataSetChanged();
		
		onSearchFromRank.onSearchFromRank();
	}
}
