package com.gagein.ui.tablet.company;

import java.util.ArrayList;

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
	private Facet mFacet;
	private ArrayList<FacetItem> functionalRoles;
	
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
		
		listView = (ListView) view.findViewById(R.id.listView);
	}
	
	@Override
	protected void initData() {
		super.initData();
		
		mFacet = Constant.currentFacet;
		if (null == mFacet) return;
		functionalRoles = mFacet.functionalRoles;
		if (null == functionalRoles) return;
		
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
			
		}
	}
	
	@Override
	protected void setData() {
		super.setData();
		adapter = new FilterFunctionalRoleAdapter(mContext, functionalRoles);
		listView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		adapter.notifyDataSetInvalidated();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		
		Boolean checked = functionalRoles.get(position).selected;
		if (position == 0) {
			Boolean haveChecked = false;
			for (int i = 0; i < functionalRoles.size(); i ++) {
				if (i == 0) continue;
				if (functionalRoles.get(i).selected) {
					haveChecked = true;
					break;
				}
			}
			if (!haveChecked) {
				return;
			} else {
				functionalRoles.get(position).selected = true;
				
				for (int i = 0; i < functionalRoles.size(); i ++) {
					if (i != 0) functionalRoles.get(i).selected = false;
				}
			}
		} else {
			
			//circle 
			functionalRoles.get(position).selected = !checked;
			Boolean haveChecked = false;
			for (int i = 0; i < functionalRoles.size(); i ++) {
				if (i == 0) {
					continue;
				} else {
					if (functionalRoles.get(i).selected) {
						haveChecked = true;
						functionalRoles.get(0).selected = false;
					}
				}
			}
			if (!haveChecked) {
				functionalRoles.get(0).selected = true;
			}
		}
		
		adapter.notifyDataSetChanged();//refresh listview state
		
		for (int i= 0; i < functionalRoles.size(); i ++) {
			if (functionalRoles.get(i).selected) {
				Constant.currentFunctionRoleForCompanyPeopleFilter = functionalRoles;
			}
		}
		
	}

}
