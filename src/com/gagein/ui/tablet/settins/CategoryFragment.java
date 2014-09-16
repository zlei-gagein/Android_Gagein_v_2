package com.gagein.ui.tablet.settins;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.gagein.R;
import com.gagein.adapter.CategoryAdapter;
import com.gagein.model.Category;
import com.gagein.ui.BaseFragment;
import com.gagein.util.Constant;

public class CategoryFragment extends BaseFragment implements OnItemClickListener{
	
	private CategoryAdapter adapter;
	private List<Category> categories = new ArrayList<Category>();
	private ListView listView;
	private int categoryType = 0;
	private OnBackToFeedbackListener onBackToFeedbackListener;
	
	public interface OnBackToFeedbackListener {
		public void onBackToFeedbackListener();
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		try {
			onBackToFeedbackListener = (OnBackToFeedbackListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + "must implement onBackToFeedbackListener");
		}
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.activity_category, container, false);
		
		doInit();
		return view;
	}
	
	@Override
	protected void initView() {
		super.initView();
		
		setTitle(R.string.u_category);
		setLeftImageButton(R.drawable.back_arrow);
		
		listView = (ListView) view.findViewById(R.id.listView);
		
	}
	
	@Override
	protected void initData() {
		super.initData();
		
		categoryType = Constant.CURRENT_CATEGORY_TYPE_IN_SETTINGS;
		
		for (int i = 0; i < 5; i++) {
			Category category = new Category();
			if (i == 0) {
				category.setName(mContext.getResources().getString(R.string.general_comment));
				category.setChecked(categoryType == 5 ? true : false);
				category.setType(5);
			} else if (i == 1) {
				category.setName(mContext.getResources().getString(R.string.u_question));
				category.setChecked(categoryType == 4 ? true : false);
				category.setType(4);
			} else if (i == 2) {
				category.setName(mContext.getResources().getString(R.string.u_bug));
				category.setChecked(categoryType == 1 ? true : false);
				category.setType(1);
			} else if (i == 3) {
				category.setName(mContext.getResources().getString(R.string.u_improvement));
				category.setChecked(categoryType == 2 ? true : false);
				category.setType(2);
			} else if (i == 4) {
				category.setName(mContext.getResources().getString(R.string.new_feature_request));
				category.setChecked(categoryType == 3 ? true : false);
				category.setType(3);
			}
			categories.add(category);
		}
		adapter = new CategoryAdapter(mContext, categories);
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
			
			setResult();
			onBackToFeedbackListener.onBackToFeedbackListener();
			
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
		Category category = categories.get(position);
		if (category.getChecked()) {
			category.setChecked(!category.getChecked());
		} else {
			for (int i = 0; i < categories.size(); i ++) {
				Category category1 = categories.get(i);
				category1.setChecked(false);
			}
			category.setChecked(true);
		}
		adapter.notifyDataSetChanged();
	}
	
	private void setResult() {
		
		for (int i = 0; i < categories.size(); i ++) {
			
			Category category = categories.get(i);
			
			if (category.getChecked()) {
				
				Constant.CURRENT_CATEGORY_TYPE_IN_SETTINGS = category.getType();
				
			}
		}
	}
	
}
