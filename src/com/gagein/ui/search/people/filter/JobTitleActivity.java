package com.gagein.ui.search.people.filter;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.gagein.R;
import com.gagein.adapter.search.JobTitleAdapter;
import com.gagein.model.filter.Filters;
import com.gagein.model.filter.JobTitle;
import com.gagein.ui.main.BaseActivity;
import com.gagein.util.CommonUtil;
import com.gagein.util.Constant;

public class JobTitleActivity extends BaseActivity implements OnItemClickListener{
	
	private Filters mFilters;
	private EditText addEdt;
	private ListView listView;
	private List<JobTitle> mJobTitles = new ArrayList<JobTitle>();
	private JobTitleAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_company_filter_headquarters);
		
		doInit();
	}
	
	@Override
	protected void initView() {
		super.initView();
		setTitle(R.string.job_title);
		setLeftImageButton(R.drawable.back_arrow);
		
		addEdt = (EditText) findViewById(R.id.addEdt);
		listView = (ListView) findViewById(R.id.listView);
		
		addEdt.setOnEditorActionListener(new OnEditorActionListener() {
		
			@Override
			public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
				
				if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
					
					CommonUtil.hideSoftKeyBoard(mContext, JobTitleActivity.this);
					if (TextUtils.isEmpty(textView.getText().toString())) return false;
					
					for (int i = 0; i < mJobTitles.size(); i ++) {
						mJobTitles.get(i).setChecked(false);
					}
					JobTitle jobTitle = new JobTitle();
					jobTitle.setName(textView.getText().toString());
					jobTitle.setChecked(true);
					mJobTitles.add(jobTitle);
					adapter.notifyDataSetChanged();
					
					CommonUtil.setListViewHeight(listView);
					addEdt.setText("");
					addEdt.requestFocus();
					return true;
				}
				return false;
				
			}
		});
	}
	
	@Override
	protected void initData() {
		super.initData();
		
		mFilters = Constant.MFILTERS;
		mJobTitles = mFilters.getJobTitles();
		
		adapter = new JobTitleAdapter(mContext, mJobTitles);
		listView.setAdapter(adapter);
		CommonUtil.setListViewHeight(listView);
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
			CommonUtil.hideSoftKeyBoard(mContext, JobTitleActivity.this);
			addEdt.setText("");
		}
		
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
		
		Boolean checked = mJobTitles.get(position).getChecked();
		if (checked) {
			mJobTitles.get(position).setChecked(!checked);
		} else {
			for (int i = 0; i < mJobTitles.size(); i ++) {
				mJobTitles.get(i).setChecked(false);
			}
			mJobTitles.get(position).setChecked(true);//set checked item
		}
		adapter.notifyDataSetChanged();
		
	}

}
