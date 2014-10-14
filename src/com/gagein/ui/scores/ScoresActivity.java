package com.gagein.ui.scores;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.gagein.R;
import com.gagein.adapter.ScoresAdapter;
import com.gagein.component.xlistview.XListView;
import com.gagein.component.xlistview.XListView.IXListViewListener;
import com.gagein.http.APIHttpMetadata;
import com.gagein.http.APIParser;
import com.gagein.model.Company;
import com.gagein.model.DataPage;
import com.gagein.model.Group;
import com.gagein.model.ScoresSort;
import com.gagein.ui.company.CompanyActivity;
import com.gagein.ui.main.BaseActivity;
import com.gagein.ui.tablet.company.CompanyTabletActivity;
import com.gagein.util.CommonUtil;
import com.gagein.util.Constant;

public class ScoresActivity extends BaseActivity implements IXListViewListener, OnItemClickListener{
	
	private XListView listview; 
	private TextView sort;
	private String nextPage = "";
	private List<ScoresSort> scoresSorts = new ArrayList<ScoresSort>();
	private List<Company> companies = new ArrayList<Company>();
	private ScoresAdapter adapter;
	private int requestCodeForGroups = 1;
	private int resultCodeForGroups = 11;
	private int requestCodeForScoresSort = 2;
	private int resultCodeForScoresSort = 22;
	private LinearLayout emptyLayout;
	private LinearLayout sortLayout;
	
	@Override
	public void handleNotifications(Context aContext, Intent intent) {
		super.handleNotifications(aContext, intent);
		
		String actionName = intent.getAction();
		if (actionName.equals(Constant.BROADCAST_UNFOLLOW_COMPANY) || actionName.equals(Constant.BROADCAST_FOLLOW_COMPANY) ||
				actionName.equals(Constant.BROADCAST_REMOVE_COMPANIES) || actionName.equals(Constant.BROADCAST_ADD_COMPANIES)
				|| actionName.equals(Constant.BROADCAST_ADD_NEW_COMPANIES) || actionName.equals(Constant.BROADCAST_ADDED_PENDING_COMPANY)
				|| actionName.equals(Constant.BROADCAST_REMOVE_PENDING_COMPANIES)) {
			
			getCompaniesOfGroup(false, false, true);
		}
	}
	
	@Override
	protected List<String> observeNotifications() {
		return stringList(Constant.BROADCAST_UNFOLLOW_COMPANY, Constant.BROADCAST_FOLLOW_COMPANY, Constant.BROADCAST_REMOVE_COMPANIES, 
				Constant.BROADCAST_ADD_COMPANIES, Constant.BROADCAST_ADD_NEW_COMPANIES, Constant.BROADCAST_ADDED_PENDING_COMPANY,
				Constant.BROADCAST_REMOVE_PENDING_COMPANIES);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scores);
		
		doInit();
	}
	
	@Override
	protected void initView() {
		super.initView();
		
		setTitle(R.string.u_scores);
		setLeftButton(R.string.filters);
		
		listview = (XListView) findViewById(R.id.listview);
		sort = (TextView) findViewById(R.id.sort);
		emptyLayout = (LinearLayout) findViewById(R.id.emptyLayout);
		sortLayout = (LinearLayout) findViewById(R.id.sortLayout);
		
	}
	
	@Override
	protected void initData() {
		super.initData();
		
		initScoresSort();
		setScoresSort();
		getCompaniesOfGroup(true, false, true);
		
	}
	
	@Override
	protected void setData() {
		super.setData();
		
		adapter = new ScoresAdapter(mContext, companies);
		listview.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		adapter.notifyDataSetInvalidated();
		
	}
	
	private void initScoresSort() {
		
		scoresSorts.clear();
		for (int i = 0; i < 3; i++) {
			ScoresSort scoresSort = new ScoresSort();
			if (i == 0) {
				scoresSort.setName(mContext.getResources().getString(R.string.one_day_score));
				scoresSort.setKey(APIHttpMetadata.kGGScoreRank1);
				scoresSort.setChecked(true);
			} else if (i == 1) {
				scoresSort.setName(mContext.getResources().getString(R.string.one_week_score));
				scoresSort.setKey(APIHttpMetadata.kGGScoreRank7);
				scoresSort.setChecked(false);
			} else if (i == 2) {
				scoresSort.setName(mContext.getResources().getString(R.string.one_month_score));
				scoresSort.setKey(APIHttpMetadata.kGGScoreRank30);
				scoresSort.setChecked(false);
			}
			scoresSorts.add(scoresSort);
		}
		
		Constant.scoresSorts = scoresSorts;
	}
	
	@Override
	protected void setOnClickListener() {
		super.setOnClickListener();
		
		listview.setXListViewListener(this);
		listview.setPullLoadEnable(false);
		listview.setOnItemClickListener(this);
		sort.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		
		if (v == leftBtn) {
			
			Intent intent = new Intent();
			intent.setClass(mContext, GroupsForScoresActivity.class);
			startActivityForResult(intent, requestCodeForGroups);
			
		} else if (v == sort) {
			
			Intent intent = new Intent();
			intent.setClass(mContext, ScoresSortActivity.class);
			startActivityForResult(intent, requestCodeForScoresSort);
			
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if ((requestCode == requestCodeForGroups || requestCode == requestCodeForScoresSort) &&
				(resultCode == resultCodeForGroups || resultCode == resultCodeForScoresSort)) { 
			setScoresSort();
			getCompaniesOfGroup(true, false, true);
		}
	}
	
	private void setScoresSort() {
		for (int i = 0; i < Constant.scoresSorts.size(); i++) {
			if (scoresSorts.get(i).getChecked()) {
				sort.setText(scoresSorts.get(i).getName());
			}
		}
	}

	@Override
	public void onRefresh() {
		getCompaniesOfGroup(true, false, true);
	}

	@Override
	public void onLoadMore() {
		getCompaniesOfGroup(false, true, false);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		Constant.scoresSorts.clear();
		Constant.GroupsForScores.clear();
		
	}
	
	private ArrayList<String> getIndustriesIds() {
		
		ArrayList<String> industriesIds = new ArrayList<String>();
		return industriesIds;
		
	}
	
	private String getFolRank() {
		String folRank = "";
		
		for (int i = 0; i < Constant.scoresSorts.size(); i++) {
			if (scoresSorts.get(i).getChecked()) folRank = scoresSorts.get(i).getKey();
		}
		
		return folRank;
	}
	
	private ArrayList<String> getGroupIds() {
		
		ArrayList<String> groupIds = new ArrayList<String>();
		for (int i = 0; i < Constant.GroupsForScores.size() ; i++) {
			Group group = Constant.GroupsForScores.get(i);
			if (group.selected) {
				groupIds.add(group.getMogid());
			}
		}
		
		return groupIds;
	}
	
	private void getCompaniesOfGroup(Boolean showDialog, final Boolean loadMore, Boolean refresh) {
		
		if (showDialog) showLoadingDialog();
		if (refresh) nextPage = "";
		
		mApiHttp.getCompaniesOfGroupNew(getFolRank() , 0, nextPage, getGroupIds(), getIndustriesIds(), APIHttpMetadata.kGGExceptPendingFollowCompanies, new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject jsonObject) {
				
				APIParser parser = new APIParser(jsonObject);
				if (parser.isOK()) {
					
					if (!loadMore) companies.clear();
					
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
					
					nextPage = parser.data().optString("next_page");
					
					Boolean haveMoreNews = page.hasMore;
					listview.setPullLoadEnable(haveMoreNews);
					
					if (!loadMore) setData();
					
					setEmptyLayoutVisible();
					
				} else {
					
					if (companies != null) {
						companies.clear();
						if (null != adapter) adapter.notifyDataSetChanged();
					}
					setEmptyLayoutVisible();
					
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
	
	private void setEmptyLayoutVisible() {
		emptyLayout.setVisibility(companies.size() == 0 ? View.VISIBLE : View.GONE);
		sortLayout.setVisibility(companies.size() != 0 ? View.VISIBLE : View.GONE);
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

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		if (position == 0 || position == companies.size() + 1) return;
		Intent intent = new Intent();
		intent.putExtra(Constant.COMPANYID, companies.get(position - 1).orgID);
		intent.setClass(mContext, CommonUtil.isTablet(mContext) ? CompanyTabletActivity.class : CompanyActivity.class);
		startActivity(intent);
	}

}
