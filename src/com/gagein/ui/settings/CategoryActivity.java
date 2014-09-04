package com.gagein.ui.settings;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.gagein.R;
import com.gagein.adapter.CategoryAdapter;
import com.gagein.model.Category;
import com.gagein.ui.main.BaseActivity;
import com.gagein.util.Constant;

public class CategoryActivity extends BaseActivity implements OnItemClickListener{
	
	private CategoryAdapter adapter;
	private List<Category> categories = new ArrayList<Category>();
	private ListView listView;
	private int resultCode = 1;
	private int categoryType = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_category);
		
		doInit();
	}
	
	@Override
	protected void initView() {
		super.initView();
		setTitle(R.string.u_category);
		setLeftImageButton(R.drawable.back_arrow);
		
		listView = (ListView) findViewById(R.id.listView);
	}
	
	@Override
	protected void initData() {
		super.initData();
		
		categoryType = getIntent().getIntExtra(Constant.CategoryType, 0);
		
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
			finish();
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
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			setResult();
        }
        return super.onKeyDown(keyCode, event);
	}

	private void setResult() {
		for (int i = 0; i < categories.size(); i ++) {
			Category category = categories.get(i);
			if (category.getChecked()) {
				Intent intent = new Intent(mContext, FeedbackActivity.class);
				intent.putExtra(Constant.Category, category.getType());
				setResult(resultCode, intent);
			}
		}
	}

}
