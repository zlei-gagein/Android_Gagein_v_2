package com.gagein.ui.newsfilter;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.gagein.R;
import com.gagein.adapter.FilterFunctionalRoleAdapter;
import com.gagein.model.Facet;
import com.gagein.model.FacetItem;
import com.gagein.ui.main.BaseActivity;
import com.gagein.util.Constant;

public class FilterFunctionalRoleActivity extends BaseActivity implements OnItemClickListener{
	
	private FilterFunctionalRoleAdapter adapter;
	private ListView listView;
	private List<Boolean> checkedList;
	private Facet mFacet;
	private List<FacetItem> functionalRoles;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_filter_news);
		
		doInit();
	}
	
	@Override
	protected void initView() {
		super.initView();
		
		setTitle(R.string.functional_role);
		setLeftImageButton(R.drawable.back_arrow);
		setRightButton(R.string.done);
		
		listView = (ListView) findViewById(R.id.listView);
	}
	
	@Override
	protected void initData() {
		super.initData();
		
		mFacet = Constant.currentFacet;
		if (null == mFacet) return;
		functionalRoles = mFacet.functionalRoles;
		checkedList = new ArrayList<Boolean>();
		if (null == functionalRoles) return;
		for (int i = 0; i < functionalRoles.size(); i ++) {
			checkedList.add(false);
		}
		
		if (Constant.FUNCTIONAL_ROLE_ID == 0) {
			checkedList.set(0, true);
		} else {
			for (int i = 0; i < functionalRoles.size(); i ++) {
				if (Constant.FUNCTIONAL_ROLE_ID == functionalRoles.get(i).id) {
					checkedList.set(i, true);
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
			finish();
		} else if (v == rightBtn) {
			finish();
			for (int i= 0; i < checkedList.size(); i ++) {
				if (checkedList.get(i) == true) {
					Constant.FUNCTIONAL_ROLE_ID = functionalRoles.get(i).id;
					Constant.FUNCTIONAL_ROLE_NAME = functionalRoles.get(i).name;
				}
			}
		}
	}
	
	@Override
	protected void setData() {
		super.setData();
		adapter = new FilterFunctionalRoleAdapter(mContext, checkedList);
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
