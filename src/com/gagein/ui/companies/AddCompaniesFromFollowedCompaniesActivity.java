package com.gagein.ui.companies;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.gagein.R;
import com.gagein.adapter.CompaniesNoSectionIndexAdapter;
import com.gagein.http.APIHttpMetadata;
import com.gagein.http.APIParser;
import com.gagein.model.Company;
import com.gagein.model.DataPage;
import com.gagein.ui.main.BaseActivity;
import com.gagein.util.Constant;
import com.gagein.util.Log;

public class AddCompaniesFromFollowedCompaniesActivity extends BaseActivity implements OnItemClickListener{
	
	private ListView listView;
	private TextView addCompaniesTo;
	private List<Company> companies = new ArrayList<Company>();
	private LinearLayout noFollowedCompanies;
	private View headSelectAll;
	private ImageView selectAllBtn;
	private CompaniesNoSectionIndexAdapter adapter;
	private String groupName;
	private String groupId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_companies_from_followed_companies);
		
		doInit();
	}
	
	@Override
	protected void initView() {
		super.initView();
		
		setTitle(R.string.u_followed_companies);
		setLeftImageButton(R.drawable.back_arrow);
		setRightButton(R.string.done);
		
		listView = (ListView) findViewById(R.id.listView);
		addCompaniesTo = (TextView) findViewById(R.id.addCompaniesTo);
		noFollowedCompanies = (LinearLayout) findViewById(R.id.noFollowedCompanies);
		headSelectAll = LayoutInflater.from(mContext).inflate(R.layout.header_select_all, null);
		selectAllBtn = (ImageView) headSelectAll.findViewById(R.id.selectBtn);
		selectAllBtn.setTag(R.id.tag_select, false);
	}
	
	@Override
	protected void initData() {
		super.initData();
		
		groupId = getIntent().getStringExtra(Constant.GROUPID);
		groupName = getIntent().getStringExtra(Constant.GROUPNAME);
		
		String toGroup = mContext.getResources().getString(R.string.add_companies_to_group);
		addCompaniesTo.setText(String.format(toGroup, groupName));
		
		getFollowedCompanies();
	}
	
	@Override
	protected void setData() {
		super.setData();
		
		adapter = new CompaniesNoSectionIndexAdapter(mContext, companies);
		adapter.setEdit(true);
		if (companies.size() > 0) listView.addHeaderView(headSelectAll);
		listView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		adapter.notifyDataSetInvalidated();
		
	}
	
	private void getFollowedCompanies() {
		
		showLoadingDialog();
		companies = new ArrayList<Company>();
		mApiHttp.getFollowedCompanies(0, Constant.INDUSTRYID, APIHttpMetadata.kGGExceptPendingFollowCompanies, false, new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject jsonObject) {
				
				APIParser parser = new APIParser(jsonObject);
				if (parser.isOK()) {
					
					
					DataPage page = parser.parseFollowedCompanies();
					if (page.items != null) {
						for (Object obj : page.items) {
							if (obj instanceof Company) {
								Company company = (Company)obj;
								if (company.orgID != 0) {
									companies.add(company);
								}
							}
						}
					}
					
					Collections.sort(companies);
					
					setData();
					
				} else {
					noFollowedCompanies.setVisibility(View.VISIBLE);
				}
				
				dismissLoadingDialog();
			}

			
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				showConnectionError();
			}
		});
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
			
			ArrayList<String> companiesId = new ArrayList<String>();
			for (int i = 0; i < companies.size(); i ++) {
				if (companies.get(i).select) 
					companiesId.add(companies.get(i).orgID + ""); 
			}
			if (companiesId.size() == 0) {
				finish();
				return;
			}
			addCompaniesToGroup(groupId, companiesId);
			
		}
	}
	
	private void addCompaniesToGroup(String toGroupId, ArrayList<String> companiesId) {
		
		showLoadingDialog();
		mApiHttp.addCompanies(toGroupId, companiesId, new Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject jsonObject) {
				
				Log.v("silen", "jsonObject = " + jsonObject.toString());
				APIParser parser = new APIParser(jsonObject);
				if (parser.isOK()) {
					
					//sent broadcast to groups activity
					Intent intent = new Intent();
					intent.setAction(Constant.BROADCAST_ADD_COMPANIES_FROM_FOLLOW_COMPANIES);
					sendBroadcast(intent);
					finish();
					
				} else {
				}
				dismissLoadingDialog();
			}
			
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				showConnectionError();
			}
		});
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
		
		if (position == 0) {
			Boolean isSelect = (Boolean) selectAllBtn.getTag(R.id.tag_select);
			selectAllBtn.setTag(R.id.tag_select, !isSelect);
			selectAllBtn.setImageResource(!isSelect ? R.drawable.button_select : R.drawable.button_unselect);
			
			for (int i = 0; i < companies.size(); i ++) {
				companies.get(i).select = isSelect ? false : true;
			}
			adapter.notifyDataSetChanged();
			return;
		}
		
		Boolean select = companies.get(position - 1).select;
		companies.get(position - 1).select = !select;
		setSelectAllBtnStatus();
		adapter.notifyDataSetChanged();
		
	}
	
	private void setSelectAllBtnStatus() {
		
		Boolean isAllSelected = true;
		for (int i = 0; i < companies.size(); i ++) {
			if (!companies.get(i).select) isAllSelected = false;
		}
		selectAllBtn.setImageResource(!isAllSelected ? R.drawable.button_unselect : R.drawable.button_select);
		
	}

}
