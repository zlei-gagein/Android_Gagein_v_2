package com.gagein.ui.tablet.search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gagein.R;
import com.gagein.http.APIHttp;
import com.gagein.ui.BaseFragment;

public class PeopleEmptyViewFragment extends BaseFragment {
	
	private TextView build;
	private TextView select;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.search_empty_view, container, false);
		mContext = getActivity();
		mApiHttp = new APIHttp(mContext);
		
		doInit();
		return view;
	}
	
	@Override
	protected void initView() {
		super.initView();
		build = (TextView) view.findViewById(R.id.build);
		select = (TextView) view.findViewById(R.id.select);
	}
	
	@Override
	protected void initData() {
		super.initData();
		String buildStr = String.format(mContext.getResources().getString(R.string.build_list), "people");
		build.setText(buildStr);
		String selectStr = String.format(mContext.getResources().getString(R.string.select_filters), "people");
		select.setText(selectStr);
	}
	

}
