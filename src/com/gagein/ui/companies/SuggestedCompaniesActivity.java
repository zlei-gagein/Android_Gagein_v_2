package com.gagein.ui.companies;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.gagein.R;
import com.gagein.adapter.SuggestedCompanyAdapter;
import com.gagein.http.APIHttpMetadata;
import com.gagein.http.APIParser;
import com.gagein.model.Company;
import com.gagein.model.DataPage;
import com.gagein.model.Group;
import com.gagein.ui.company.CompanyActivity;
import com.gagein.ui.main.BaseActivity;
import com.gagein.ui.tablet.company.CompanyTabletActivity;
import com.gagein.util.CommonUtil;
import com.gagein.util.Constant;

public class SuggestedCompaniesActivity extends BaseActivity implements OnItemClickListener{
	private ListView listView;
	private List<Company> suggestedCompanies;
	private SuggestedCompanyAdapter adapter;
	private Group group;
	
	@Override
	protected List<String> observeNotifications() {
		return stringList(Constant.BROADCAST_FOLLOW_COMPANY, Constant.BROADCAST_UNFOLLOW_COMPANY);
	}
	
	@Override
	public void handleNotifications(Context aContext, Intent intent) {
		
		super.handleNotifications(aContext, intent);
		
		String actionName = intent.getAction();
		if (actionName.equals(Constant.BROADCAST_FOLLOW_COMPANY)) {
			setFollowOrUnFollow(intent, true);
		} else if (actionName.equals(Constant.BROADCAST_UNFOLLOW_COMPANY)) {
			setFollowOrUnFollow(intent, false);
		}
		
	}

	private void setFollowOrUnFollow(Intent intent, Boolean isFollow) {
		
		long companyId = intent.getLongExtra(Constant.COMPANYID, 0);
		
		for (int i = 0; i < suggestedCompanies.size() ; i++) {
			Company company = suggestedCompanies.get(i);
			if (company.orgID == companyId) {
				company.followed = isFollow;
			}
		}
		adapter.notifyDataSetChanged();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_suggested_companies);
	
		doInit();
	}
	
	@Override
	protected void initView() {
		super.initView();
		
		setTitle(R.string.suggested_companies);
		setRightButton(R.string.done);
		
		listView = (ListView) findViewById(R.id.listView);
	}
	
	@Override
	protected void initData() {
		super.initData();
		
		group = (Group) getIntent().getSerializableExtra(Constant.GROUP);
		
		suggestedCompanies = new ArrayList<Company>();
		getSuggestedCompanies();
		
	}
	
	@Override
	protected void setOnClickListener() {
		super.setOnClickListener();
		
		listView.setOnItemClickListener(this);
		
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v); 
		
		if (v == rightBtn) {
			finish();
		} 
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
		
		Intent intent = new Intent();
		intent.putExtra(Constant.COMPANYID, suggestedCompanies.get(position).orgID);
		intent.setClass(mContext, CommonUtil.isTablet(mContext) ? CompanyTabletActivity.class : CompanyActivity.class);
		startActivity(intent);
		
	}
	
	@Override
	protected void setData() {
		super.setData();
		
		adapter = new SuggestedCompanyAdapter(mContext, suggestedCompanies);
		adapter.setGroup(group);
		listView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		adapter.notifyDataSetInvalidated();
		
	}
	
	private void getSuggestedCompanies() {
		
		showLoadingDialog();
		mApiHttp.getRecommendedCompanies(APIHttpMetadata.GETALL, false, new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject jsonObject) {
				
				dismissLoadingDialog();
				APIParser parser = new APIParser(jsonObject);
				if (parser.isOK()) {
					
					DataPage page = parser.parseGetRecommendedCompanies();
					
					suggestedCompanies.clear();
					if (page != null && page.items != null) {
						for (Object obj : page.items) {
							suggestedCompanies.add((Company)obj);
						}
					}
					
//					Collections.sort(suggestedCompanies);
					
					if (suggestedCompanies.size() == 0) return;
					setData();
					
				} else {
					alertMessageForParser(parser);
				}
			}
			
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				showConnectionError();
			}
		});
	};
	
}
