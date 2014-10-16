package com.gagein.ui.companies;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.gagein.R;
import com.gagein.adapter.AddToGroupAdapter;
import com.gagein.component.dialog.AddNewGroupDialog;
import com.gagein.http.APIParser;
import com.gagein.model.DataPage;
import com.gagein.model.Group;
import com.gagein.ui.main.BaseActivity;
import com.gagein.util.CommonUtil;
import com.gagein.util.Constant;
import com.gagein.util.Log;

public class AddToGroupActivity extends BaseActivity implements OnItemClickListener{
	
	private ListView listView;
	private AddToGroupAdapter adapter;
	private List<Group> groups = new ArrayList<Group>();
	private RelativeLayout newGroup;
	private Group group;
	private String fromGroupId;
	private String toGroupId;
	private ArrayList<String> companiesId = new ArrayList<String>();
	private Timer timer;
	private TimerTask timerTask;
	
	@Override
	public void handleNotifications(Context aContext, Intent intent) {
		super.handleNotifications(aContext, intent);
		String actionName = intent.getAction();
		if (actionName.equals(Constant.BROADCAST_NEW_GROUP_AND_FOLLOW)) {
			String groupId = intent.getStringExtra(Constant.GROUPID);
			addCompaniesToGroup(groupId);
			getAllCompanyGroups(false, false);
		}
	}
	
	@Override
	protected List<String> observeNotifications() {
		return stringList( Constant.BROADCAST_NEW_GROUP_AND_FOLLOW);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_to_groups);
		
		doInit();
	}
	
	@Override
	protected void initView() {
		super.initView();
		setTitle(R.string.add_to_group);
		setRightButton(R.string.cancel);
		
		listView = (ListView) findViewById(R.id.listView);
		newGroup = (RelativeLayout) findViewById(R.id.newGroup);
	}
	
	@Override
	protected void initData() {
		super.initData();
		
		group = (Group) getIntent().getSerializableExtra(Constant.GROUP);
		fromGroupId = group.getMogid();
		companiesId = getIntent().getStringArrayListExtra(Constant.COMPANIESID);
		
		getAllCompanyGroups(true, false);
	}

	@Override
	protected void setData() {
		super.setData();
		
		adapter = new AddToGroupAdapter(mContext, groups, fromGroupId);
		listView.setAdapter(adapter);
		CommonUtil.setViewHeight(listView, listView.getAdapter().getCount() * CommonUtil.dp2px(mContext, 71));
		adapter.notifyDataSetChanged();
		
		newGroup.setVisibility(View.VISIBLE);
		
	}
	
	public void getAllCompanyGroups(Boolean showDialog, final Boolean needRefresh) {
		
		if (showDialog) showLoadingDialog();
		mApiHttp.getAllCompanyGroups(new Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject jsonObject) {
				
				APIParser parser = new APIParser(jsonObject);
				if (parser.isOK()) {
					
					groups.clear();
					DataPage dataPage = parser.parseGetAllCompanyGroups();
					List<Object> items = dataPage.items;
					if (items != null) {
						for (Object obj : items) {
							groups.add((Group) obj);
						}
						setData();
					}
					
					timer = new Timer();
					timerTask = new TimerTask() {
						
						@Override
						public void run() {
							if (needRefresh) finish();
						}
					};
					timer.schedule(timerTask, 800);
					
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

	};
	
	@Override
	protected void setOnClickListener() {
		super.setOnClickListener();
		
		listView.setOnItemClickListener(this);
		newGroup.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		
		if (v == rightBtn) {
			
			finish();
			
		} else if (v == newGroup) {
			
			AddNewGroupDialog dialog = new AddNewGroupDialog(mContext, Constant.NEW_GROUP_AND_FOLLOW);
			dialog.showDialog();
			
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
		toGroupId = groups.get(position).getMogid();
		if (TextUtils.isEmpty(toGroupId) || toGroupId.equalsIgnoreCase(fromGroupId) || Long.parseLong(toGroupId) < 0) return;
		addCompaniesToGroup(toGroupId);
	}
	
	private void addCompaniesToGroup(String toGroupId) {
		
		showLoadingDialog();
		mApiHttp.addCompanies(toGroupId, companiesId, new Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject jsonObject) {
				
				Log.v("silen", "jsonObject = " + jsonObject.toString());
				APIParser parser = new APIParser(jsonObject);
				if (parser.isOK()) {
					showShortToast(R.string.companies_added);
					
					//sent broadcast to groups activity
					Intent intent = new Intent();
					intent.setAction(Constant.BROADCAST_ADD_COMPANIES);
					sendBroadcast(intent);
					
					getAllCompanyGroups(false, true);
					
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

}
