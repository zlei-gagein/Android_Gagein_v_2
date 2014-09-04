package com.gagein.ui.search.company.filter;

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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.gagein.R;
import com.gagein.adapter.search.FilterAdapter;
import com.gagein.model.filter.FilterItem;
import com.gagein.model.filter.Filters;
import com.gagein.ui.main.BaseActivity;
import com.gagein.util.CommonUtil;
import com.gagein.util.Constant;
import com.gagein.util.Log;

public class CompaniesTypeFromBuzAcivity extends BaseActivity implements OnItemClickListener{
	
	private ListView listView;
	private EditText edit;
	private FilterAdapter adapter;
	private Filters mFilters;
	private List<FilterItem> companyTypes = new ArrayList<FilterItem>();
	private LinearLayout savedSearchLayout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_company_filter_company);

		doInit();
	}
	
	@Override
	protected void initView() {
		super.initView();
		setTitle(R.string.u_companies);
		setLeftImageButton(R.drawable.back_arrow);
		
		listView = (ListView) findViewById(R.id.listView);
		edit = (EditText) findViewById(R.id.edit);
		
		edit.setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
				
				if (actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {// COMPANY_SEARCH_KEYWORDS
					
					String text = textView.getText().toString();
					CommonUtil.hideSoftKeyBoard(mContext, CompaniesTypeFromBuzAcivity.this);
					
					if (TextUtils.isEmpty(text)) {
						return false;
					} else {
						if (text.trim().length() < 2) {
							showShortToast("Keywords must be at least two characters long.");
							return false;
						} else {
							Constant.COMPANY_SEARCH_KEYWORDS = text;
						}
					}
					
				}
				return false;
			}
		});
		savedSearchLayout = (LinearLayout) findViewById(R.id.savedSearchLayout);
	}
	
	@Override
	protected void initData() {
		super.initData();
		
		mFilters = Constant.MFILTERS;
		companyTypes = mFilters.getCompanyTypesFromCompany();
		
		adapter = new FilterAdapter(mContext, companyTypes);
		listView.setAdapter(adapter);
		CommonUtil.setListViewHeight(listView);
		adapter.notifyDataSetChanged();
		adapter.notifyDataSetInvalidated();
		
		for (int i = 0; i < companyTypes.size(); i ++) {
			if (companyTypes.get(i).getValue().equalsIgnoreCase("Specific Companies")) {
				if (companyTypes.get(i).getChecked()) {
					savedSearchLayout.setVisibility(View.VISIBLE);
					edit.setText(Constant.COMPANY_SEARCH_KEYWORDS);
				}
			}
		}
		
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
	public void onItemClick(AdapterView<?> parent, View arg1, int position, long arg3) {
		// when clicked saved company search...
		Boolean checked = companyTypes.get(position).getChecked();
		
		if (checked) {
			return;
		} else {// this item is not checked
			
			if (companyTypes.get(position).getValue().equalsIgnoreCase("Specific Companies")) {
				savedSearchLayout.setVisibility(View.VISIBLE);
			} else {
				savedSearchLayout.setVisibility(View.GONE);
				edit.setText("");
				edit.clearFocus();
				CommonUtil.hideSoftKeyBoard(mContext, CompaniesTypeFromBuzAcivity.this);
			}
			
			for (int i = 0; i < companyTypes.size(); i ++) {
				companyTypes.get(i).setChecked(false);
			}
			companyTypes.get(position).setChecked(true);//set checked item
			
		}
		adapter.notifyDataSetChanged();
		
		if (position == 0) {
//			onSearchFromCompanies.onSearchFromCompanies();
		} else if (position == 2) {//TODO
			String requestStr = CommonUtil.packageRequestDataForCompanyOrPeople(true);
			String baseStr = "{'sortBy':'noe','search_company_for_type':'0','reverse':false}";
			if (requestStr.equalsIgnoreCase(baseStr)) {
				Log.v("silen", "00000");
			}
		}
	}

}
