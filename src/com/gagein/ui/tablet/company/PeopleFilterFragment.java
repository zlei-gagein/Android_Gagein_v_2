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

public class PeopleFilterFragment extends BaseFragment {
	
	private RelativeLayout jobLevel;
	private RelativeLayout functionalRole;
	private RelativeLayout linkedProfile;
	private Button jobLevelBtn;
	private Button functionalRoleBtn;
	private Button linkedProfileBtn;
	private OnPeopleFilterClosed onPeopleFilterClosed;
	private OnStartFilterJobLevel onStartFilterJobLevel;
	private OnStartFilterFunctionalRole onStartFilterFunctionalRole;
	private OnStartFilterLinkedProfile onStartFilterLinkedProfile;
	
	
	public interface OnPeopleFilterClosed {
		public void onPeopleFilterClosed();
	}
	public interface OnStartFilterJobLevel {
		public void onStartFilterJobLevel();
	}
	public interface OnStartFilterFunctionalRole {
		public void onStartFilterFunctionalRole();
	}
	public interface OnStartFilterLinkedProfile {
		public void onStartFilterLinkedProfile();
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			onPeopleFilterClosed = (OnPeopleFilterClosed) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + "must implement OnPeopleFilterClosed");
		}
		try {
			onStartFilterJobLevel = (OnStartFilterJobLevel) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + "must implement OnStartFilterJobLevel");
		}
		try {
			onStartFilterFunctionalRole = (OnStartFilterFunctionalRole) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + "must implement OnStartFilterFunctionalRole");
		}
		try {
			onStartFilterLinkedProfile = (OnStartFilterLinkedProfile) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + "must implement OnStartFilterLinkedProfile");
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.activity_peoplefilter, container, false);
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
		
		jobLevel = (RelativeLayout) view.findViewById(R.id.jobLevel);
		functionalRole = (RelativeLayout) view.findViewById(R.id.functionalRole);
		linkedProfile = (RelativeLayout) view.findViewById(R.id.linkedProfile);
		jobLevelBtn = (Button) view.findViewById(R.id.jobLevelBtn);
		functionalRoleBtn = (Button) view.findViewById(R.id.functionalRoleBtn);
		linkedProfileBtn = (Button) view.findViewById(R.id.linkedProfileBtn);
		
	}
	
	@Override
	protected void setOnClickListener() {
		super.setOnClickListener();
		jobLevel.setOnClickListener(this);
		functionalRole.setOnClickListener(this);
		linkedProfile.setOnClickListener(this);
		jobLevelBtn.setOnClickListener(this);
		functionalRoleBtn.setOnClickListener(this);
		linkedProfileBtn.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		if (v == rightBtn) {
			onPeopleFilterClosed.onPeopleFilterClosed();
		} else if (v == jobLevel || v == jobLevelBtn) {
			onStartFilterJobLevel.onStartFilterJobLevel();
		} else if (v == functionalRole || v == functionalRoleBtn) {
			onStartFilterFunctionalRole.onStartFilterFunctionalRole();
		} else if (v == linkedProfile || v == linkedProfileBtn) {
			onStartFilterLinkedProfile.onStartFilterLinkedProfile();
		}
	}

}
