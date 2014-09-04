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
import com.gagein.adapter.SECFilingsAdapter;
import com.gagein.model.Update;
import com.gagein.ui.main.BaseActivity;
import com.gagein.ui.news.StoryActivity;
import com.gagein.util.Constant;

public class SecFilingsActivity extends BaseActivity implements OnItemClickListener{
	
	private List<Update> secfilings = new ArrayList<Update>();
	private SECFilingsAdapter adapter;
	private ListView listView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sec_filings);
		
		doInit();
	}
	
	@Override
	protected void initView() {
		super.initView();
		setTitle(R.string.sec_filings);
		setLeftImageButton(R.drawable.back_arrow);
		
		listView = (ListView) findViewById(R.id.listView);
	}
	
	@Override
	protected void initData() {
		super.initData();
		secfilings = Constant.SECFILINGS;
		adapter = new SECFilingsAdapter(mContext, secfilings);
		listView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
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
		intent.putExtra("position", position);
		intent.setClass(mContext, StoryActivity.class);
		mContext.startActivity(intent);
		Constant.setUpdates(secfilings);
	}

}
