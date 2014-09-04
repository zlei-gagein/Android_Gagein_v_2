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
import com.gagein.adapter.FilterLinkedProfileAdapter;
import com.gagein.http.APIHttp;
import com.gagein.model.Facet;
import com.gagein.model.FacetItem;
import com.gagein.ui.BaseFragment;
import com.gagein.util.Constant;

public class FilterLinkedProfileFragment extends BaseFragment implements OnItemClickListener{
	
	private FilterLinkedProfileAdapter adapter;
	private ListView listView;
	private List<Boolean> checkedList;
	private Facet mFacet;
	private List<FacetItem> linkedProfiles;
	private OnClosedLinkedProfile onClosedLinkedProfile;

	public interface OnClosedLinkedProfile {
		public void onClosedLinkedProfile();
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			onClosedLinkedProfile = (OnClosedLinkedProfile) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + "must implement OnClosedLinkedProfile");
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
		
		setTitle(R.string.Linked_profile);
		setLeftImageButton(R.drawable.back_arrow);
		setRightButton(R.string.done);
		
		listView = (ListView) view.findViewById(R.id.listView);
	}
	
	@Override
	protected void initData() {
		super.initData();
		
		mFacet = Constant.currentFacet;
		if (null == mFacet) return;
		linkedProfiles = mFacet.linkedProfiles;
		if (null == linkedProfiles) return;
		checkedList = new ArrayList<Boolean>();
		for (int i = 0; i < linkedProfiles.size(); i ++) {
			checkedList.add(false);
		}
		
		if (Constant.LINKED_PROFILE_ID == 0) {
			checkedList.set(0, true);
		} else {
			for (int i = 0; i < linkedProfiles.size(); i ++) {
				if (Constant.LINKED_PROFILE_ID == linkedProfiles.get(i).id) {
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
			onClosedLinkedProfile.onClosedLinkedProfile();
		} else if (v == rightBtn) {
			onClosedLinkedProfile.onClosedLinkedProfile();
			for (int i= 0; i < checkedList.size(); i ++) {
				if (checkedList.get(i) == true) {
					Constant.LINKED_PROFILE_ID = linkedProfiles.get(i).id;
					Constant.LINKED_PROFILE_NAME = linkedProfiles.get(i).name;
				}
			}
		}
	}
	
	@Override
	protected void setData() {
		super.setData();
		adapter = new FilterLinkedProfileAdapter(mContext, checkedList);
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

