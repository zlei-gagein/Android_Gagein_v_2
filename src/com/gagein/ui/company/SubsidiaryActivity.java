package com.gagein.ui.company;

import java.util.List;

import android.annotation.SuppressLint;
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

public class SubsidiaryActivity extends BaseActivity implements OnItemClickListener{
	
	private ListView listview;
	private Company mCompany;
	private List<Company> subsidiaries;
	private CompanyItemAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_subsidiary);
		
		doInit();
	}
	
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void initView() {
		super.initView();
		setLeftImageButton(R.drawable.back_arrow);
		setTitle(R.string.u_subsidiaries);
		
		listview = (ListView) findViewById(R.id.listview);
	}
	
	@Override
	protected void initData() {
		super.initData();
		
		mCompany = (Company) getIntent().getSerializableExtra(Constant.COMPANY);
		setData();
	}
	
	@Override
	protected void setData() {
		super.setData();
		
		if (null == mCompany) return;
		subsidiaries = mCompany.subsidiaries;
		adapter = new CompanyItemAdapter(mContext, subsidiaries);
		listview.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		adapter.notifyDataSetInvalidated();
			
	}
	
	@Override
	protected void setOnClickListener() {
		super.setOnClickListener();
		listview.setOnItemClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		if (v == leftImageBtn) {
			finish();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {
		Intent intent = new Intent();
		intent.putExtra(Constant.COMPANYID, subsidiaries.get(position).orgID);
		intent.setClass(mContext, CommonUtil.isTablet(mContext) ? CompanyTabletActivity.class : CompanyActivity.class);
		mContext.startActivity(intent);
	}
	
}