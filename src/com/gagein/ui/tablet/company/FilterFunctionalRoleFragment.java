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
import com.gagein.adapter.FilterFunctionalRoleAdapter;
import com.gagein.http.APIHttp;
import com.gagein.model.Facet;
import com.gagein.model.FacetItem;
import com.gagein.ui.BaseFragment;
import com.gagein.util.Constant;

public class FilterFunctionalRoleFragment extends BaseFragment implements OnItemClickListener{
	
	private FilterFunctionalRoleAdapter adapter;
	private ListView listView;
	private List<Boolean> checkedList;
	private Facet mFacet;
	private List<FacetItem> functionalRoles;
	
	private OnClosedFunctionRole onClosedFunctionRole;
	
	public interface OnClosedFunctionRole {
		public void onClosedFunctionRole();
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			onClosedFunctionRole = (OnClosedFunctionRole) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + "must implement OnClosedFunctionRole");
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
		
		setTitle(R.string.functional_role);
		setLeftImageButton(R.drawable.back_arrow);
		setRightButton(R.string.done);
		
		listView = (ListView) view.findViewById(R.id.listView);
	}
	
	@Override
	protected void initData() {
		super.initData();
		
		mFacet = Constant.currentFacet;
		if (null == mFacet) return;
		functionalRoles = mFacet.functionalRoles;
		checkedList = new ArrayList<Boolean>();
		if (null == functionalRoles) return;
		for (int i = 0; i < functionalRoles.size(); i ++) {
			checkedList.add(false);
		}
		
		if (Constant.FUNCTIONAL_ROLE_ID == 0) {
			checkedList.set(0, true);
		} else {
			for (int i = 0; i < functionalRoles.size(); i ++) {
				if (Constant.FUNCTIONAL_ROLE_ID == functionalRoles.get(i).id) {
					checkedList.set(i + 1, true);
				}
			}
		}
		
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
			onClosedFunctionRole.onClosedFunctionRole();
		} else if (v == rightBtn) {
			onClosedFunctionRole.onClosedFunctionRole();
			for (int i= 0; i < checkedList.size(); i ++) {
				if (checkedList.get(i) == true) {
					Constant.FUNCTIONAL_ROLE_ID = functionalRoles.get(i).id;
					Constant.FUNCTIONAL_ROLE_NAME = functionalRoles.get(i).name;
				}
			}
		}
	}
	
	@Override
	protected void setData() {
		super.setData();
		adapter = new FilterFunctionalRoleAdapter(mContext, checkedList);
		listView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		adapter.notifyDataSetInvalidated();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		
		for (int i= 0; i < checkedList.size(); i ++) {
			if (i == arg2) {
				checkedList.set(i, true);
			} else {
				checkedList.set(i, false);
			}
		}
		adapter.notifyDataSetChanged();
	}

}
