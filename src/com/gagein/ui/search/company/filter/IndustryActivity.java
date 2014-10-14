package com.gagein.ui.search.company.filter;

import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.gagein.R;
import com.gagein.adapter.search.IndustryFilterAdapter;
import com.gagein.model.filter.Filters;
import com.gagein.model.filter.Industry;
import com.gagein.ui.main.BaseActivity;
import com.gagein.util.Constant;

public class IndustryActivity extends BaseActivity implements OnItemClickListener{
	
	private ListView parentListView;
	private ListView childrenListView;
	private Filters mFilters;
	public List<Industry> mIndustries;
	private IndustryFilterAdapter parentAdapter;
	private IndustryFilterAdapter childrenAdapter;
	private LinearLayout childrenLayout;
	private int checkedIndustryPosition = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_company_filter_industry);
		
		doInit();
	}
	
	@Override
	protected void initView() {
		super.initView();
		setTitle(R.string.industry);
		setLeftImageButton(R.drawable.back_arrow);
		
		parentListView = (ListView) findViewById(R.id.parentListView);
		childrenListView = (ListView) findViewById(R.id.childrenListView);
		childrenLayout = (LinearLayout) findViewById(R.id.childrenLayout);
	}
	
	@Override
	protected void initData() {
		super.initData();
		mFilters = Constant.MFILTERS;
		mIndustries = mFilters.getIndustries();
		
		parentAdapter = new IndustryFilterAdapter(mContext, mIndustries);
		setListViewHeight(parentAdapter, parentListView);
		parentListView.setAdapter(parentAdapter);
		parentAdapter.notifyDataSetChanged();
		parentAdapter.notifyDataSetInvalidated();
		
		setChildrenView(false);
	}
	
	private void setChildrenListView(int position, Boolean fromOnClickParentItem) {
		List<Industry> childrenIndustries = mIndustries.get(position).getChildrens();
		if (fromOnClickParentItem) {
			if (null != childrenIndustries) {
				for (int i = 0; i < childrenIndustries.size(); i ++) {
					childrenIndustries.get(i).setChecked((i == 0) ? true : false);
				}
			}
		}
		if (null != childrenIndustries) {
			childrenAdapter = new IndustryFilterAdapter(mContext, childrenIndustries);
			setListViewHeight(childrenAdapter, childrenListView);
			childrenListView.setAdapter(childrenAdapter);
			childrenAdapter.notifyDataSetChanged();
			childrenAdapter.notifyDataSetInvalidated();
		}
	}
	
	private void setListViewHeight(IndustryFilterAdapter adapter, ListView listView) {
		int totalHeight = 0; 
        for (int i = 0; i < adapter.getCount(); i++) { 
            View listItem = adapter.getView(i, null, listView); 
            listItem.measure(0, 0); 
            totalHeight += listItem.getMeasuredHeight(); 
        } 
        ViewGroup.LayoutParams params = listView.getLayoutParams(); 
        params.height = totalHeight + (listView.getDividerHeight() * (adapter.getCount() - 1)); 
        listView.setLayoutParams(params);
	}
	
	@Override
	protected void setOnClickListener() {
		super.setOnClickListener();
		parentListView.setOnItemClickListener(this);
		childrenListView.setOnItemClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		if (v == leftImageBtn) {
			finish();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int position, long arg3) {
		if (adapterView == parentListView) {
			Boolean checked = mIndustries.get(position).getChecked();
			if (position == 0 ) {
				Boolean haveChecked = false;
				for (int i = 0; i < mIndustries.size(); i ++) {
					if (i == 0) continue;
					if (mIndustries.get(i).getChecked()) {
						haveChecked = true;
						break;
					}
				}
				if (!haveChecked) {
					return;
				} else {
					mIndustries.get(position).setChecked(true);
					for (int i = 0; i < mIndustries.size(); i ++) {
						if (i != 0) mIndustries.get(i).setChecked(false);
					}
				}
			} else {
				mIndustries.get(position).setChecked(!checked);
				Boolean haveChecked = false;
				for (int i = 0; i < mIndustries.size(); i ++) {
					if (i == 0) continue;
					if (mIndustries.get(i).getChecked()) {
						haveChecked = true;
						break;
					}
				}
				mIndustries.get(0).setChecked(haveChecked ? false : true);
			}
			parentAdapter.notifyDataSetChanged();
			//if checked number greater than two, then hide children layout
			setChildrenView(true);
		} else if (adapterView == childrenListView) {
			List<Industry> childrenIndustries = mIndustries.get(checkedIndustryPosition).getChildrens();
			Boolean checked = childrenIndustries.get(position).getChecked();

			if (position == 0 ) {
				Boolean haveChecked = false;
				for (int i = 0; i < childrenIndustries.size(); i ++) {
					if (i == 0) continue;
					if (childrenIndustries.get(i).getChecked()) {
						haveChecked = true;
						break;
					}
				}
				if (!haveChecked) {
					return;
				} else {
					childrenIndustries.get(position).setChecked(true);
					for (int i = 0; i < childrenIndustries.size(); i ++) {
						if (i != 0) childrenIndustries.get(i).setChecked(false);
					}
				}
			} else {
				childrenIndustries.get(position).setChecked(!checked);
				Boolean haveChecked = false;
				for (int i = 0; i < childrenIndustries.size(); i ++) {
					if (i == 0) continue;
					if (childrenIndustries.get(i).getChecked()) {
						haveChecked = true;
						break;
					}
				}
				childrenIndustries.get(0).setChecked(haveChecked ? false : true);
			}
			
			childrenIndustries.get(position).setChecked(!checked);
			childrenAdapter.notifyDataSetChanged();
		}
		
	}

	/**
	 * 
	 * @param fromOnClickParentItem
	 */
	private void setChildrenView(Boolean fromOnClickParentItem) {
		int checkedNum = 0;
		for (int i = 0; i < mIndustries.size(); i ++) {
			if (i == 0) continue;
			if (mIndustries.get(i).getChecked()) checkedNum ++;
			if (checkedNum == 2) break;
		}
		if (checkedNum == 0 || checkedNum == 2) {
			childrenLayout.setVisibility(View.GONE);
		} else {
			//children layout will invisible when only one item checked
			for (int i = 0; i < mIndustries.size(); i ++) {
				if (mIndustries.get(i).getChecked()) {
					checkedIndustryPosition = i;
				}
			}
			if (mIndustries.get(checkedIndustryPosition).getName().equalsIgnoreCase("Other")) {
				childrenLayout.setVisibility(View.GONE);
			} else {
				childrenLayout.setVisibility(View.VISIBLE);
			}
			setChildrenListView(checkedIndustryPosition, fromOnClickParentItem);
		}
	}

}
