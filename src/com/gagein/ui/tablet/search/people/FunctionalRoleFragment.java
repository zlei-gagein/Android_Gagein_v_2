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

public class FunctionalRoleFragment extends BaseFragment implements OnItemClickListener{
	
	private ListView listView;
	private FilterAdapter adapter;
	private Filters mFilters;
	private List<FilterItem> mFunctionalRoles;
	private OnFunctionalRoleFinish onFunctionalRoleFinish;
	private OnSearchFromFunctionalRole onSearchFromFunctionalRole;
	
	public void refreshAdapter() {
		if (null != adapter) adapter.notifyDataSetChanged();
 	}
	
	public interface OnFunctionalRoleFinish {
		public void onFunctionalRoleFinish();
	}
	public interface OnSearchFromFunctionalRole {
		public void onSearchFromFunctionalRole();
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			onFunctionalRoleFinish = (OnFunctionalRoleFinish) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + "must implement OnFunctionalRoleFinish");
		}
		try {
			onSearchFromFunctionalRole = (OnSearchFromFunctionalRole) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + "must implement OnSearchFromFunctionalRole");
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
		setTitle(R.string.functional_role);
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
		mFunctionalRoles = mFilters.getFunctionalRoles();
		
		CommonUtil.setFilterToDefault(mFunctionalRoles);
		
		adapter = new FilterAdapter(mContext, mFunctionalRoles);
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
			onFunctionalRoleFinish.onFunctionalRoleFinish();
		}
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		Boolean checked = mFunctionalRoles.get(position).getChecked();
		if (position == 0) {
			if (checked) {
				return;
			} else {
				mFunctionalRoles.get(position).setChecked(true);
				for (int i = 0; i < mFunctionalRoles.size(); i ++) {
					if (i != 0) mFunctionalRoles.get(i).setChecked(false);
				}
			}
			
		} else {
			mFunctionalRoles.get(position).setChecked(!checked);
			Boolean haveChecked = false;
			for (int i = 0; i < mFunctionalRoles.size(); i ++) {
				if (i != 0) {
					if (mFunctionalRoles.get(i).getChecked()) {
						haveChecked = true;
						mFunctionalRoles.get(0).setChecked(false);
					}
				}
			}
			if (!haveChecked) {
				mFunctionalRoles.get(0).setChecked(true);
			}
		}
		adapter.notifyDataSetChanged();
		
		onSearchFromFunctionalRole.onSearchFromFunctionalRole();
	}

}
