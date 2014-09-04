package com.gagein.ui.companies;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.AdapterView.OnItemClickListener;

import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.gagein.R;
import com.gagein.adapter.GroupAdapter;
import com.gagein.component.dialog.NewGroupDialog;
import com.gagein.component.xlistview.XListView;
import com.gagein.component.xlistview.XListView.IXListViewListener;
import com.gagein.http.APIHttpMetadata;
import com.gagein.http.APIParser;
import com.gagein.model.Company;
import com.gagein.model.DataPage;
import com.gagein.model.Group;
import com.gagein.ui.main.BaseActivity;
import com.gagein.util.CommonUtil;
import com.gagein.util.Constant;
import com.gagein.util.Log;

public class GroupsActivity extends BaseActivity implements OnItemClickListener, IXListViewListener{
	
	private XListView listView;
	private GroupAdapter adapter;
	private Boolean isEdit = false;
	private List<Group> groups = new ArrayList<Group>();
	private List<Company> pendingCompanies = new ArrayList<Company>();
	private LinearLayout pendingLayout;
	private Button pending;
	
	@Override
	protected List<String> observeNotifications() {
		return stringList(Constant.BROADCAST_ADD_COMPANIES, Constant.BROADCAST_ADD_COMPANYGROUP, 
				Constant.BROADCAST_REMOVE_COMPANIES, Constant.BROADCAST_UNFOLLOW_COMPANY, 
				Constant.BROADCAST_ADD_COMPANIES_FROM_FOLLOW_COMPANIES, Constant.BROADCAST_FOLLOW_COMPANY,
				Constant.BROADCAST_ADD_NEW_COMPANIES, Constant.BROADCAST_ADDED_PENDING_COMPANY);
	}
	
	@Override
	public void handleNotifications(Context aContext, Intent intent) {
		super.handleNotifications(aContext, intent);
		String actionName = intent.getAction();
		if (actionName.equals(Constant.BROADCAST_ADD_COMPANIES) || actionName.equals(Constant.BROADCAST_ADD_COMPANYGROUP) || 
				actionName.equals(Constant.BROADCAST_REMOVE_COMPANIES) || actionName.equals(Constant.BROADCAST_UNFOLLOW_COMPANY) || 
				actionName.equals(Constant.BROADCAST_ADD_COMPANIES_FROM_FOLLOW_COMPANIES) || actionName.equals(Constant.BROADCAST_FOLLOW_COMPANY)
				) {
			getAllCompanyGroups(false);
		} else if (actionName.equals(Constant.BROADCAST_ADD_NEW_COMPANIES)) {
			getPendingCompany();
		} else if (actionName.equals(Constant.BROADCAST_ADDED_PENDING_COMPANY)) {
			getPendingCompany();
		}
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_groups);
		
		doInit();
	}
	
	@Override
	protected void initView() {
		super.initView();
		setTitle(R.string.u_groups);
		setLeftImageButton(R.drawable.add_group);
		setRightButton(R.string.u_edit);
		setRightButtonVisible(View.GONE);
		
		listView = (XListView) findViewById(R.id.listView);
		pendingLayout = (LinearLayout) findViewById(R.id.pendingLayout);
		pending = (Button) findViewById(R.id.pending);
	}
	
	@Override
	protected void initData() {
		super.initData();
		
		getPendingCompany();
		getAllCompanyGroups(true);
		
	}
	
	@Override
	protected void setData() {
		super.setData();
		
		setEditButtonVisible();
		
		adapter = new GroupAdapter(mContext, groups);
		listView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
	}
	
	private void setEditButtonVisible() {
		
		Boolean haveNotSystemGroup = false;
		for (int i = 0; i < groups.size(); i ++) {
			if (!groups.get(i).getIsSsystem()) haveNotSystemGroup = true;
		}
		
		setRightButtonVisible(haveNotSystemGroup ? View.VISIBLE : View.GONE);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		List<Company> currentPendingCompanies = new ArrayList<Company>();
		for (int i = 0; i < Constant.CURRENT_PENDING_COMPANY.size(); i ++) {
			Company company = Constant.CURRENT_PENDING_COMPANY.get(i);
			if (company.followed) currentPendingCompanies.add(company);
		}
		Constant.CURRENT_PENDING_COMPANY = currentPendingCompanies;
		
	}
	
	public void getAllCompanyGroups(Boolean showDialog) {
		
		if (showDialog) showLoadingDialog();
		
		mApiHttp.getAllCompanyGroups(new Listener<JSONObject>() {
			
			@Override
			public void onResponse(JSONObject jsonObject) {
				dismissLoadingDialog();
				
				APIParser parser = new APIParser(jsonObject);
				if (parser.isOK()) {
					groups.clear();
					DataPage dataPage = parser.parseGetAllCompanyGroups();
					List<Object> items = dataPage.items;
					if (items != null) {
						for (Object obj : items) {
							Group group = (Group) obj;
							if (CommonUtil.isLinkedCompanies(group)) {
								int groupCount = group.getCount();
								if (groupCount > 0) {
									groups.add(group);
								}
							} else {
								groups.add(group);
							}
						}
						setData();
					}
				} else {
				}
			}
			
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				showConnectionError();
			}
		});

	};
	
	private void getPendingCompany() {
		
		mApiHttp.getFollowedCompanies(0, Constant.INDUSTRYID, APIHttpMetadata.kGGPendingFollowCompanies, false, new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject jsonObject) {
				
				APIParser parser = new APIParser(jsonObject);
				if (parser.isOK()) {
					pendingCompanies.clear();
					DataPage page = parser.parseFollowedCompanies();
					if (page.items != null) {
						for (Object obj : page.items) {
							if (obj instanceof Company) {
								pendingCompanies.add((Company)obj);
							}
						}
					}
					Collections.sort(pendingCompanies);
					Log.v("silen", "pendingCompanies.size = " + pendingCompanies.size());
					pendingLayout.setVisibility((pendingCompanies.size() != 0) ? View.VISIBLE : View.GONE);
					Constant.CURRENT_PENDING_COMPANY = pendingCompanies;
					setPendingNum();
				} else {
				}
			}
			
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				showConnectionError();
			}
		});
	}
	
	private void setPendingNum() {
		
		int pendingCompaniesNum = Constant.CURRENT_PENDING_COMPANY.size();
		String text = "";
		if (pendingCompaniesNum == 1) {
			text = "1 company pending";
		} else if (pendingCompaniesNum > 1) {
			text = pendingCompaniesNum + " companies pending";
		}
		pending.setText(text);
		
	}
	
	@Override
	protected void setOnClickListener() {
		super.setOnClickListener();
		
		listView.setPullLoadEnable(false);
		listView.setXListViewListener(this);
		listView.setOnItemClickListener(this);
		pending.setOnClickListener(this);
		
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		
		if (v == leftImageBtn) {
			isEdit = false;
			setRightButton(isEdit ? R.string.done : R.string.u_edit);
			adapter.setEdit(isEdit);
			adapter.notifyDataSetChanged();
			
			NewGroupDialog dialog = new NewGroupDialog(mContext);
			dialog.showDialog();
		} else if (v == rightBtn) {
			isEdit = !isEdit;
			listView.setPullRefreshEnable(isEdit ? false : true);
			
			setRightButton(isEdit ? R.string.done : R.string.u_edit);
			setLeftImageButtonVisible(isEdit ? View.GONE : View.VISIBLE);
			adapter.setEdit(isEdit);
			adapter.notifyDataSetChanged();
		} else if (v == pending) {
			startActivitySimple(PendingCompaniesActivity.class);
		}
		
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
	}

	@Override
	public void onRefresh() {
		if (isEdit) return;
		getPendingCompany();
		getAllCompanyGroups(true);
	}

	@Override
	public void onLoadMore() {
	}
	
	@Override
	public void onBackPressed() {
		if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        showShortToast(R.string.press_again_exit);
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
             doubleBackToExitPressedOnce=false;    	
            }
        }, 2000);
	}

}
