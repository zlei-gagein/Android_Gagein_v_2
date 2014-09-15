package com.gagein.ui.tablet.search.people;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.gagein.R;
import com.gagein.adapter.search.JobTitleAdapter;
import com.gagein.http.APIHttp;
import com.gagein.model.filter.Filters;
import com.gagein.model.filter.JobTitle;
import com.gagein.ui.BaseFragment;
import com.gagein.util.CommonUtil;
import com.gagein.util.Constant;

public class JobTitleFragment extends BaseFragment implements OnItemClickListener{
	
	private Filters mFilters;
	private EditText addEdt;
	private ListView listView;
	private List<JobTitle> mJobTitles;
	private JobTitleAdapter adapter;
	private OnJobTitleFinish onJobTitleFinish;
	private OnSearchFromJobTitle onSearchFromJobTitle;
	
	public void refreshAdapter() {
		if (null != adapter) adapter.notifyDataSetChanged();
 	}
	
	public interface OnJobTitleFinish {
		public void onJobTitleFinish();
	}
	public interface OnSearchFromJobTitle {
		public void onSearchFromJobTitle();
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		try {
			onJobTitleFinish = (OnJobTitleFinish) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + "must implement OnJobTitleFinish");
		}
		try {
			onSearchFromJobTitle = (OnSearchFromJobTitle) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + "must implement OnSearchFromJobTitle");
		}
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		view = inflater.inflate(R.layout.activity_search_company_filter_headquarters, container, false);
		mContext = getActivity();
		mApiHttp = new APIHttp(mContext);
		
		doInit();
		return view;
		
	}
	
	@Override
	protected void initView() {
		super.initView();
		
		setTitle(R.string.job_title);
		setLeftImageButton(R.drawable.back_arrow);
		
		addEdt = (EditText) view.findViewById(R.id.addEdt);
		listView = (ListView) view.findViewById(R.id.listView);
		
		addEdt.setOnEditorActionListener(new OnEditorActionListener() {
		
			@Override
			public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
				
				if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
					
					CommonUtil.hideSoftKeyBoard(mContext, getActivity());
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
					onSearchFromJobTitle.onSearchFromJobTitle();
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
		CommonUtil.setListViewHeight(listView);
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
			onJobTitleFinish.onJobTitleFinish();
			CommonUtil.hideSoftKeyBoard(mContext, getActivity());
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
		onSearchFromJobTitle.onSearchFromJobTitle();
	}

}
