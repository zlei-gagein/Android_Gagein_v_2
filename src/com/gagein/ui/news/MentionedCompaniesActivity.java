package com.gagein.ui.news;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.gagein.R;
import com.gagein.adapter.MentionedComaniesAdapter;
import com.gagein.model.Update;
import com.gagein.ui.company.CompanyActivity;
import com.gagein.ui.main.BaseActivity;
import com.gagein.ui.tablet.company.CompanyTabletActivity;
import com.gagein.util.CommonUtil;
import com.gagein.util.Constant;

public class MentionedCompaniesActivity extends BaseActivity implements OnItemClickListener{
	
	private ListView listView;
	private MentionedComaniesAdapter adapter;
	private Update mUpdate;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mentioned_companies);
		
		doInit();
	}
	
	@Override
	protected void initView() {
		super.initView();
		setTitle(R.string.mentioned_companies);
		setLeftImageButton(R.drawable.back_arrow);
		
		listView = (ListView) findViewById(R.id.listView);
	}
	
	@Override
	protected void initData() {
		super.initData();
		
		mUpdate = (Update) getIntent().getSerializableExtra(Constant.UPDATE);
		adapter = new MentionedComaniesAdapter(mContext, mUpdate.mentionedCompanies);
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
		Intent intent = new Intent();
		intent.putExtra(Constant.COMPANYID, mUpdate.mentionedCompanies.get(position).orgID);
		intent.setClass(mContext, CommonUtil.isTablet(mContext) ? CompanyTabletActivity.class : CompanyActivity.class);
		startActivity(intent);
	}

}
