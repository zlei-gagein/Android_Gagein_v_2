package com.gagein.ui.newsfilter;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.gagein.R;
import com.gagein.adapter.FilterLinkedProfileAdapter;
import com.gagein.model.Facet;
import com.gagein.model.FacetItem;
import com.gagein.ui.main.BaseActivity;
import com.gagein.util.Constant;

public class FilterLinkedProfileActivity extends BaseActivity implements OnItemClickListener{
	
	private FilterLinkedProfileAdapter adapter;
	private ListView listView;
	private Facet mFacet;
	private ArrayList<FacetItem> linkedProfiles;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_filter_news);
		
		doInit();
	}
	
	@Override
	protected void initView() {
		super.initView();
		
		setTitle(R.string.Linked_profile);
		setLeftImageButton(R.drawable.back_arrow);
		setRightButton(R.string.done);
		
		listView = (ListView) findViewById(R.id.listView);
	}
	
	@Override
	protected void initData() {
		super.initData();
		
		if (Constant.currentLinkedProfileForCompanyPeopleFilter.size() > 0) {
			linkedProfiles = Constant.currentLinkedProfileForCompanyPeopleFilter;
		} else {
			mFacet = Constant.currentFacet;
			if (null == mFacet) return;
			linkedProfiles = mFacet.linkedProfiles;
			if (null == linkedProfiles) return;
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
			for (int i= 0; i < linkedProfiles.size(); i ++) {
				if (linkedProfiles.get(i).selected) {
					Constant.currentLinkedProfileForCompanyPeopleFilter = linkedProfiles;
				}
			}
			finish();
		}
	}
	
	@Override
	protected void setData() {
		super.setData();
		adapter = new FilterLinkedProfileAdapter(mContext, linkedProfiles);
		listView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		adapter.notifyDataSetInvalidated();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		
		Boolean checked = linkedProfiles.get(position).selected;
		if (position == 0) {
			Boolean haveChecked = false;
			for (int i = 0; i < linkedProfiles.size(); i ++) {
				if (i == 0) continue;
				if (linkedProfiles.get(i).selected) {
					haveChecked = true;
					break;
				}
			}
			if (!haveChecked) {
				return;
			} else {
				linkedProfiles.get(position).selected = true;
				
				for (int i = 0; i < linkedProfiles.size(); i ++) {
					if (i != 0) linkedProfiles.get(i).selected = false;
				}
			}
		} else {
			
			//circle 
			linkedProfiles.get(position).selected = !checked;
			Boolean haveChecked = false;
			for (int i = 0; i < linkedProfiles.size(); i ++) {
				if (i == 0) {
					continue;
				} else {
					if (linkedProfiles.get(i).selected) {
						haveChecked = true;
						linkedProfiles.get(0).selected = false;
					}
				}
			}
			if (!haveChecked) {
				linkedProfiles.get(0).selected = true;
			}
		}
		
		adapter.notifyDataSetChanged();//refresh listview state
		
	}

}
