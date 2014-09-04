package com.gagein.ui.company;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.gagein.R;
import com.gagein.adapter.CompanyItemAdapter;
import com.gagein.model.Company;
import com.gagein.ui.main.BaseActivity;
import com.gagein.ui.tablet.company.CompanyTabletActivity;
import com.gagein.util.CommonUtil;
import com.gagein.util.Constant;

public class EmploymentHistoryActivity extends BaseActivity implements OnItemClickListener{
	
	private ListView listView;
	private CompanyItemAdapter adapter;
	private List<Company> companies;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_employment_history);
		
		doInit();
	}
	
	@Override
	protected void initView() {
		super.initView();
		setTitle(R.string.employment_history);
		setLeftImageButton(R.drawable.back_arrow);
		
		listView = (ListView) findViewById(R.id.listView);
		
	}
	
	@Override
	protected void initData() {
		super.initData();
		companies = Constant.employmentHistory;
		adapter = new CompanyItemAdapter(mContext, companies);
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
			finish();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		long companyId = companies.get(position).orgID;
		if (companyId <= 0) return;
		Intent intent = new Intent();
		intent.putExtra(Constant.COMPANYID, companyId);
		intent.setClass(mContext, CommonUtil.isTablet(mContext) ? CompanyTabletActivity.class : CompanyActivity.class);
		startActivity(intent);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Constant.employmentHistory = new ArrayList<Company>();
	}

}
