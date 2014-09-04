package com.gagein.ui.tablet.search.company;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.gagein.R;
import com.gagein.http.APIHttp;
import com.gagein.ui.BaseFragment;

public class CompanyFilterFragment extends BaseFragment {
	
	private Button newsTriggers;
	private Button headquarters;
	private Button companies;
	private Button industry;
	private Button employeeSize;
	private Button revenueSize;
	private Button ownership;
	private Button milestone;
	private Button rank;
	private Button fiscalYearEnd;
	private OnFinishActivity onFinishActivity;
	private OnStartNewsTriggers onStartNewsTriggers;
	private OnStartCompanies onStartCompanies;
	private OnStartHeadquarters onStartHeadquarters;
	private OnStartIndustry onStartIndustry;
	private OnStartEmployeeSize onStartEmployeeSize;
	private OnStartRevenueSize onStartRevenueSize;
	private OnStartOwnership onStartOwnership;
	private OnStartMilestone onStartMilestone;
	private OnStartRank onStartRank;
	private OnStartFiscalYear onStartFiscalYear;
	private OnStartSearchFromCompany onStartSearchFromCompany;
	
	
	public interface OnFinishActivity {
		public void onFinishActivity();
	}
	
	public interface OnStartNewsTriggers {
		public void onStartNewsTriggers();
	}
	public interface OnStartCompanies {
		public void onStartCompanies();
	}
	
	public interface OnStartHeadquarters {
		public void onStartHeadquarters();
	}
	
	public interface OnStartIndustry {
		public void onStartIndustry();
	}
	
	public interface OnStartEmployeeSize {
		public void onStartEmployeeSize();
	}
	
	public interface OnStartRevenueSize {
		public void onStartRevenueSize();
	}
	public interface OnStartOwnership {
		public void onStartOwnership();
	}
	public interface OnStartMilestone {
		public void onStartMilestone();
	}
	public interface OnStartRank {
		public void onStartRank();
	}
	public interface OnStartFiscalYear {
		public void onStartFiscalYear();
	}
	public interface OnStartSearchFromCompany {
		public void onStartSearchFromCompany();
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			onFinishActivity = (OnFinishActivity) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + "must implement OnFinishActivity");
		}
		try {
			onStartNewsTriggers = (OnStartNewsTriggers) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + "must implement OnStartNewsTriggers");
		}
		try {
			onStartCompanies = (OnStartCompanies) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + "must implement OnStartCompanies");
		}
		try {
			onStartHeadquarters = (OnStartHeadquarters) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + "must implement OnStartHeadquarters");
		}
		try {
			onStartIndustry = (OnStartIndustry) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + "must implement OnStartIndustry");
		}
		try {
			onStartEmployeeSize = (OnStartEmployeeSize) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + "must implement OnStartEmployeeSize");
		}
		try {
			onStartRevenueSize = (OnStartRevenueSize) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + "must implement OnStartRevenueSize");
		}
		try {
			onStartOwnership = (OnStartOwnership) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + "must implement OnStartOwnership");
		}
		try {
			onStartMilestone = (OnStartMilestone) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + "must implement OnStartMilestone");
		}
		try {
			onStartRank = (OnStartRank) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + "must implement OnStartRank");
		}
		try {
			onStartFiscalYear = (OnStartFiscalYear) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + "must implement OnStartFiscalYear");
		}
		try {
			onStartSearchFromCompany = (OnStartSearchFromCompany) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + "must implement OnStartSearchFromCompany");
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.activity_search_company_filter, container, false);
		mContext = getActivity();
		mApiHttp = new APIHttp(mContext);
		
		doInit();
		return view;
	}
	
	@Override
	protected void initView() {
		super.initView();
		setTitle(R.string.company_filters);
		setLeftButton(R.string.cancel);
		setRightButton(R.string.u_search);
		
		newsTriggers = (Button) view.findViewById(R.id.newsTriggers);
		companies = (Button) view.findViewById(R.id.companies);
		headquarters = (Button) view.findViewById(R.id.headquarters);
		industry = (Button) view.findViewById(R.id.industry);
		employeeSize = (Button) view.findViewById(R.id.employeeSize);
		revenueSize = (Button) view.findViewById(R.id.revenueSize);
		ownership = (Button) view.findViewById(R.id.ownership);
		milestone = (Button) view.findViewById(R.id.milestone);
		rank = (Button) view.findViewById(R.id.rank);
		fiscalYearEnd = (Button) view.findViewById(R.id.fiscalYearEnd);
	}
	
	@Override
	protected void setOnClickListener() {
		super.setOnClickListener();
		newsTriggers.setOnClickListener(this);
		companies.setOnClickListener(this);
		headquarters.setOnClickListener(this);
		industry.setOnClickListener(this);
		employeeSize.setOnClickListener(this);
		revenueSize.setOnClickListener(this);
		ownership.setOnClickListener(this);
		milestone.setOnClickListener(this);
		rank.setOnClickListener(this);
		fiscalYearEnd.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		if (v == leftBtn) {
			onFinishActivity.onFinishActivity();
		} else if (v == rightBtn) {
			onStartSearchFromCompany.onStartSearchFromCompany();
		} else if (v == newsTriggers) {
			onStartNewsTriggers.onStartNewsTriggers();
		} else if (v == companies) {
			onStartCompanies.onStartCompanies();
		} else if (v == headquarters) {
			onStartHeadquarters.onStartHeadquarters();
		} else if (v == industry) {
			onStartIndustry.onStartIndustry();
		} else if (v == employeeSize) {
			onStartEmployeeSize.onStartEmployeeSize();
		} else if (v == revenueSize) {
			onStartRevenueSize.onStartRevenueSize();
		} else if (v == ownership) {
			onStartOwnership.onStartOwnership();
		} else if (v == milestone) {
			onStartMilestone.onStartMilestone();
		} else if (v == rank) {
			onStartRank.onStartRank();
		} else if (v == fiscalYearEnd) {
			onStartFiscalYear.onStartFiscalYear();
		}
	}

}
