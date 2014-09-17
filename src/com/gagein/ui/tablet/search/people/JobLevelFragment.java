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

public class JobLevelFragment extends BaseFragment implements OnItemClickListener{
	
	private ListView listView;
	private FilterAdapter adapter;
	private Filters mFilters;
	private List<FilterItem> mJobLevels;
	private OnJobLevelFinish onJobLevelFinish;
	private OnSearchFromJobLevel onSearchFromJobLevel;
	
	public void refreshAdapter() {
		if (null != adapter) adapter.notifyDataSetChanged();
 	}
	
	public interface OnJobLevelFinish {
		public void onJobLevelFinish();
	}
	public interface OnSearchFromJobLevel {
		public void onSearchFromJobLevel();
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			onJobLevelFinish = (OnJobLevelFinish) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + "must implement OnJobLevelFinish");
		}
		try {
			onSearchFromJobLevel = (OnSearchFromJobLevel) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + "must implement OnSearchFromJobLevel");
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
		setTitle(R.string.job_level);
		setLeftImageButton(R.drawable.back_arrow);
		
		listView = (ListView) view.findViewById(R.id.listView);
	}
	
	@Override
	protected void initData() {
		super.initData();
		
		setInitialData();
		
	}
	
	public void setInitialData() {
		
		mFilters = Constant.MFILTERS;
		mJobLevels = mFilters.getJobLevel();
		
		adapter = new FilterAdapter(mContext, mJobLevels);
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
			onJobLevelFinish.onJobLevelFinish();
		}
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		Boolean checked = mJobLevels.get(position).getChecked();
		if (position == 0) {
			Boolean haveChecked = false;
			for (int i = 0; i < mJobLevels.size(); i ++) {
				if (i == 0) continue;
				if (mJobLevels.get(i).getChecked()) {
					haveChecked = true;
					break;
				}
			}
			if (!haveChecked) {
				return;
			} else {
				mJobLevels.get(position).setChecked(true);
				for (int i = 0; i < mJobLevels.size(); i ++) {
					if (i != 0) mJobLevels.get(i).setChecked(false);
				}
			}
		} else {
			mJobLevels.get(position).setChecked(!checked);
			Boolean haveChecked = false;
			for (int i = 0; i < mJobLevels.size(); i ++) {
				if (i == 0) {
					continue;
				} else {
					if (mJobLevels.get(i).getChecked()) {
						haveChecked = true;
						mJobLevels.get(0).setChecked(false);
					}
				}
			}
			if (!haveChecked) {
				mJobLevels.get(0).setChecked(true);
			}
		}
		adapter.notifyDataSetChanged();
		
		onSearchFromJobLevel.onSearchFromJobLevel();
	}
}
