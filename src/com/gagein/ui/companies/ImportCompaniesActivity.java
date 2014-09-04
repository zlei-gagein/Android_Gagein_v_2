package com.gagein.ui.companies;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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
import com.gagein.adapter.ImportCompanyAdapter;
import com.gagein.http.APIParser;
import com.gagein.model.Company;
import com.gagein.model.Contact;
import com.gagein.model.DataPage;
import com.gagein.model.Group;
import com.gagein.ui.company.CompanyActivity;
import com.gagein.ui.main.BaseActivity;
import com.gagein.ui.tablet.company.CompanyTabletActivity;
import com.gagein.util.CommonUtil;
import com.gagein.util.Constant;
import com.gagein.util.Log;

public class ImportCompaniesActivity extends BaseActivity implements OnItemClickListener{
	
	private ListView listView;
	private ImportCompanyAdapter adapter;
	private Timer timer;
	private TimerTask timerTask;
	private List<Company> importCompanies = new ArrayList<Company>();
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
		
		for (int i = 0; i < importCompanies.size() ; i++) {
			Company company = importCompanies.get(i);
			if (company.orgID == companyId) {
				company.followed = isFollow;
			}
		}
		adapter.notifyDataSetChanged();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_import_companies);
		
		doInit();
	}
	
	@Override
	protected void initView() {
		super.initView();
		setTitle(R.string.import_companies);
		setRightButton(R.string.done);
		
		listView = (ListView) findViewById(R.id.listView);
	}
	
	@Override
	protected void initData() {
		super.initData();
		
		group = (Group) getIntent().getSerializableExtra(Constant.GROUP);
		showLoadingDialog();
		
		timer = new Timer();
		timerTask = new TimerTask() {
			
			@Override
			public void run() {
				importCompaniesWithContactsDomain(CommonUtil.readAllContacts());
			}
		};
		timer.schedule(timerTask, 10);
		
	}
	
	@Override
	protected void setData() {
		super.setData();
		adapter = new ImportCompanyAdapter(mContext, importCompanies);
		adapter.setGroup(group);
		listView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		adapter.notifyDataSetInvalidated();
	}
	
	private void importCompaniesWithContactsDomain(List<Contact> contacts) {
		
		mApiHttp.importCompaniesWithContactsDomain(contacts, new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject jsonObject) {
				
				Log.v("silen", "jsonObject.toString = " + jsonObject.toString());
				
				APIParser parser = new APIParser(jsonObject);
				if (parser.isOK()) {
					importCompanies.clear();
					
					DataPage page = parser.parseImportCompanies();
					if (page != null && page.items != null) {
						for (Object obj : page.items) {
							
							Company company = (Company)obj;
							String websiteKey = company.websiteKey;
							List<String> nameList = new ArrayList<String>();
							for (int i = 0; i < Constant.contacts.size(); i ++) {
								Contact contact = Constant.contacts.get(i);
								List<String> emails = contact.getEmails();
								for (int j = 0; j < emails.size(); j ++) {
									String email = emails.get(j);
									if (websiteKey.equalsIgnoreCase(email)) {
										nameList.add(contact.getName());
									}
								}
							}
							company.setNameList(nameList);
							
							importCompanies.add(company);
						}
					}
					
					Collections.sort(importCompanies);
					
					setData();
				} else {
					alertMessageForParser(parser);
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
		if (v == rightBtn) {
			finish();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
		Intent intent = new Intent();
		intent.putExtra(Constant.COMPANYID, importCompanies.get(position).orgID);
		intent.setClass(mContext, CommonUtil.isTablet(mContext) ? CompanyTabletActivity.class : CompanyActivity.class);
		startActivity(intent);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		cancelSearchTask();
	}
	
	private void cancelSearchTask() {
		if (timerTask != null) {
			timerTask.cancel();
		}
	}

}
