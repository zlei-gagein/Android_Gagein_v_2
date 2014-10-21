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
import com.gagein.util.CommonUtil;
import com.gagein.util.Constant;

public class EmployeeSizeFragment extends BaseFragment implements OnItemClickListener{
	
	private ListView listView;
	private FilterAdapter adapter;
	private Filters mFilters;
	private List<FilterItem> mEmployeeSizes;
	private OnEmployeeSizeFinish onEmployeeSizeFinish;
	private OnSearchFromEmployeeSize onSearchFromEmployeeSize;
	
	public void refreshAdapter() {
		if (null != adapter) adapter.notifyDataSetChanged();
 	}
	
	public interface OnEmployeeSizeFinish {
		public void onEmployeeSizeFinish();
	}
	public interface OnSearchFromEmployeeSize {
		public void onSearchFromEmployeeSize();
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			onEmployeeSizeFinish = (OnEmployeeSizeFinish) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + "must implement OnEmployeeSizeFinish");
		}
		try {
			onSearchFromEmployeeSize = (OnSearchFromEmployeeSize) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + "must implement OnSearchFromEmployeeSize");
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
		setTitle(R.string.employee_size);
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
		mEmployeeSizes = mFilters.getEmployeeSizeFromBuz();
		
		CommonUtil.setFilterToDefault(mEmployeeSizes);
		
		adapter = new FilterAdapter(mContext, mEmployeeSizes);
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
			onEmployeeSizeFinish.onEmployeeSizeFinish();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
		Boolean checked = mEmployeeSizes.get(position).getChecked();
		
		if (position == 0) {
			Boolean haveChecked = false;
			for (int i = 0; i < mEmployeeSizes.size(); i ++) {
				if (i == 0) continue;
				if (mEmployeeSizes.get(i).getChecked()) {
					haveChecked = true;
					break;
				}
			}
			if (!haveChecked) {
				return;
			} else {
				mEmployeeSizes.get(position).setChecked(true);
				for (int i = 0; i < mEmployeeSizes.size(); i ++) {
					if (i != 0) mEmployeeSizes.get(i).setChecked(false);
				}
			}
		} else {
			mEmployeeSizes.get(position).setChecked(!checked);
			Boolean haveChecked = false;
			for (int i = 0; i < mEmployeeSizes.size(); i ++) {
				if (i == 0) {
					continue;
				} else {
					if (mEmployeeSizes.get(i).getChecked()) {
						haveChecked = true;
						mEmployeeSizes.get(0).setChecked(false);
					}
				}
			}
			if (!haveChecked) {
				mEmployeeSizes.get(0).setChecked(true);
			}
		}
		adapter.notifyDataSetChanged();
		
		onSearchFromEmployeeSize.onSearchFromEmployeeSize();
	}
}
