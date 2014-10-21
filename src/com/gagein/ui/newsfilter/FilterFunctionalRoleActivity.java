package com.gagein.ui.newsfilter;

import java.util.ArrayList;

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
	private Facet mFacet;
	private ArrayList<FacetItem> functionalRoles;
	
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
		
		if (Constant.currentFunctionRoleForCompanyPeopleFilter.size() > 0) {
			functionalRoles = Constant.currentFunctionRoleForCompanyPeopleFilter;
		} else {
			mFacet = Constant.currentFacet;
			if (null == mFacet) return;
			functionalRoles = mFacet.functionalRoles;
			if (null == functionalRoles) return;
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
			for (int i= 0; i < functionalRoles.size(); i ++) {
				if (functionalRoles.get(i).selected) {
					Constant.currentFunctionRoleForCompanyPeopleFilter = functionalRoles;
				}
			}
		}
	}
	
	@Override
	protected void setData() {
		super.setData();
		adapter = new FilterFunctionalRoleAdapter(mContext, functionalRoles);
		listView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		adapter.notifyDataSetInvalidated();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		
		Boolean checked = functionalRoles.get(position).selected;
		if (position == 0) {
			Boolean haveChecked = false;
			for (int i = 0; i < functionalRoles.size(); i ++) {
				if (i == 0) continue;
				if (functionalRoles.get(i).selected) {
					haveChecked = true;
					break;
				}
			}
			if (!haveChecked) {
				return;
			} else {
				functionalRoles.get(position).selected = true;
				
				for (int i = 0; i < functionalRoles.size(); i ++) {
					if (i != 0) functionalRoles.get(i).selected = false;
				}
			}
		} else {
			
			//circle 
			functionalRoles.get(position).selected = !checked;
			Boolean haveChecked = false;
			for (int i = 0; i < functionalRoles.size(); i ++) {
				if (i == 0) {
					continue;
				} else {
					if (functionalRoles.get(i).selected) {
						haveChecked = true;
						functionalRoles.get(0).selected = false;
					}
				}
			}
			if (!haveChecked) {
				functionalRoles.get(0).selected = true;
			}
		}
		
		adapter.notifyDataSetChanged();//refresh listview state
		
	}

}
