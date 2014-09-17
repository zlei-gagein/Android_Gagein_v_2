package com.gagein.ui.news;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.gagein.R;
import com.gagein.adapter.NewsAdapter;
import com.gagein.component.xlistview.XListView;
import com.gagein.component.xlistview.XListView.IXListViewListener;
import com.gagein.http.APIHttpMetadata;
import com.gagein.http.APIParser;
import com.gagein.model.Agent;
import com.gagein.model.Company;
import com.gagein.model.DataPage;
import com.gagein.model.Group;
import com.gagein.model.Update;
import com.gagein.ui.bookmark.BookMarksActivity;
import com.gagein.ui.companies.PendingCompaniesActivity;
import com.gagein.ui.main.BaseActivity;
import com.gagein.ui.newsfilter.FilterActivity;
import com.gagein.util.CommonUtil;
import com.gagein.util.Constant;
import com.gagein.util.Log;

@SuppressLint("HandlerLeak")
public class NewsActivity extends BaseActivity implements IXListViewListener, OnItemClickListener{
	private long companyId = APIHttpMetadata.GETALL;
	private List<Update> updates = new ArrayList<Update>();
	private NewsAdapter newsAdapter;
	private DataPage dataPage;
	private final int FRESHOK = 0;
	private Boolean haveMoreNews = false;
	private XListView listview;
	public int showShareItem;
	private LinearLayout noNews;
	private LinearLayout firstSignUp;
	private List<Company> pendingCompanies = new ArrayList<Company>();
	private LinearLayout pendingLayout;
	private Button pending;
	
	@Override
	protected List<String> observeNotifications() {
		return stringList(Constant.BROADCAST_REFRESH_NEWS, Constant.BROADCAST_REFRESH_COMPANIES, Constant.BROADCAST_LIKED_NEWS
				,Constant.BROADCAST_UNLIKE_NEWS, Constant.BROADCAST_ADD_NEW_COMPANIES, Constant.BROADCAST_ADDED_PENDING_COMPANY, 
				Constant.BROADCAST_FOLLOW_COMPANY, Constant.BROADCAST_UNFOLLOW_COMPANY, Constant.BROADCAST_REMOVE_PENDING_COMPANIES, 
				Constant.BROADCAST_REMOVE_COMPANIES, Constant.BROADCAST_REMOVE_BOOKMARKS, Constant.BROADCAST_ADD_BOOKMARKS, 
				Constant.BROADCAST_IRRELEVANT_TRUE, Constant.BROADCAST_IRRELEVANT_FALSE, Constant.BROADCAST_HAVE_READ_STORY);
	}
	
	@Override
	public void handleNotifications(Context aContext, Intent intent) {
		super.handleNotifications(aContext, intent);
		
		String actionName = intent.getAction();
		if (actionName.equals(Constant.BROADCAST_REFRESH_NEWS)) {
			
			refreshNews(false);
			
		} else if (actionName.equals(Constant.BROADCAST_REFRESH_COMPANIES)) {
			
			refreshNews(false);
			
		} else if (actionName.equals(Constant.BROADCAST_LIKED_NEWS)) {
			
			long newsId = intent.getLongExtra(Constant.UPDATEID, 0);
			setLikeFromBroadcast(newsId, true);
			
		} else if (actionName.equals(Constant.BROADCAST_UNLIKE_NEWS)) {
			
			long newsId = intent.getLongExtra(Constant.UPDATEID, 0);
			setLikeFromBroadcast(newsId, false);
			
//		} else if (actionName.equals(Constant.BROADCAST_UNLIKE_NEWS)) {
//			
//			refreshNews(false);
			
		} else if (actionName.equals(Constant.BROADCAST_ADD_NEW_COMPANIES)) {
			
			getPendingCompany();
			
		} else if (actionName.equals(Constant.BROADCAST_ADDED_PENDING_COMPANY)) {
			
			getPendingCompany();
			
		} else if (actionName.equals(Constant.BROADCAST_FOLLOW_COMPANY) || actionName.equals(Constant.BROADCAST_UNFOLLOW_COMPANY)) {
			
			refreshNews(false);
			
		} else if (actionName.equals(Constant.BROADCAST_REMOVE_PENDING_COMPANIES)) {
			
			getPendingCompany();
			
		} else if (actionName.equals(Constant.BROADCAST_REMOVE_COMPANIES)) {
			
			getPendingCompany();
			refreshNews(false);
			
		} else if (actionName.equals(Constant.BROADCAST_REMOVE_BOOKMARKS) || actionName.equals(Constant.BROADCAST_ADD_BOOKMARKS)) {
			
			refreshNews(false);
			
		} else if (actionName.equals(Constant.BROADCAST_IRRELEVANT_TRUE) || actionName.equals(Constant.BROADCAST_IRRELEVANT_FALSE)) {
			
			refreshNews(false);
			
		} else if (actionName.equals(Constant.BROADCAST_HAVE_READ_STORY)) {
			
			haveReadStory(intent);
			newsAdapter.notifyDataSetChanged();
			
		}
		
	}
	
	private void haveReadStory(Intent intent) {
		
		long newsId = intent.getLongExtra(Constant.NEWSID, 0);
		
		for (int i = 0; i < updates.size(); i ++) {
			if (updates.get(i).newsId == newsId) {
				updates.get(i).hasBeenRead = true;
				
			}
		}
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_news);
		
		doInit();
	}
	
	@Override
	protected void initView() {
		super.initView();
		
		setTitle(R.string.u_news);
		setLeftButton(R.string.filters);
		setRightImageButton(R.drawable.bookmarks);
		
		listview = (XListView) findViewById(R.id.listview);
		noNews = (LinearLayout) findViewById(R.id.noNews);
		firstSignUp = (LinearLayout) findViewById(R.id.firstSignUp);
		pendingLayout = (LinearLayout) findViewById(R.id.pendingLayout);
		pending = (Button) findViewById(R.id.pending);
	}
	
	@Override
	protected void initData() {
		super.initData();
		
		if (Constant.SIGNUP) {
			firstSignUp.setVisibility(View.VISIBLE);
		} else {
			getPendingCompany();
			getNews(0, APIHttpMetadata.kGGPageFlagFirstPage, 0, true, false);
		}
		
		if (Constant.MFILTERS == null) {
			getFilter();
		}
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
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Constant.SIGNUP = false;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == 0) {
				refreshNews(true);
			}
		}
	}

	private void setLikeFromBroadcast(long newsId, Boolean like) {
		
		for (int i = 0; i < updates.size(); i ++) {
			Update update = updates.get(i);
			if (newsId == update.newsId) {
				update.liked = like;
			}
		}
		
	}
	
	@Override
	protected void setOnClickListener() {
		super.setOnClickListener();
		listview.setXListViewListener(this);
		listview.setOnItemClickListener(this);
		noNews.setOnClickListener(this);
		pending.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		
		if (v == leftBtn) {
			
			Intent intent = new Intent();
			intent.setClass(mContext, FilterActivity.class);
			intent.putExtra(Constant.ACTIVITYNAME, "NewsActivity");
			startActivityForResult(intent, 0);
			
		} else if (v == rightImageBtn) {
			
			startActivitySimple(BookMarksActivity.class);
			
		} else if (v == noNews) {
			
			CommonUtil.setSimilarIdToNullFromUpadates(updates);
			getNews(0, APIHttpMetadata.kGGPageFlagFirstPage, 0, true, false);
			getPendingCompany();
			
		} else if (v == pending) {
			
			startActivitySimple(PendingCompaniesActivity.class);
		}
		
	}
	
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
	
	private ArrayList<String> getAgentsId() {
		
		ArrayList<String> agentsId = new ArrayList<String>();
		for (int i = 0; i < Constant.locationNewsTriggers.size() ; i++) {
			Agent agent = Constant.locationNewsTriggers.get(i);
			if (agent.checked && !agent.agentID.equalsIgnoreCase("0")) {
				agentsId.add(agent.agentID);
			}
		}
		
		return agentsId;
	}
	
	private ArrayList<String> getGroupsId() {
		
		ArrayList<String> groupsId = new ArrayList<String>();
		for (int i = 0; i < Constant.selectedGroupFilter.size() ; i++) {
			Group group = Constant.selectedGroupFilter.get(i);
			if (group.selected && !group.getMogid().equalsIgnoreCase("-10")) {
				groupsId.add(group.getMogid());
			}
		}
		
		return groupsId;
	}
	
	private void getNews(long aNewsID, final byte aPageFlag, long aPageTime, final Boolean showDialog, final Boolean loadMore) {
		if (showDialog) showLoadingDialog();
		mApiHttp.getCompanyUpdates(getAgentsId(), getGroupsId(), companyId, aNewsID, aPageFlag, aPageTime, CommonUtil.stringSimilarIDsWithUpdates(updates), new Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject jsonObject) {
				
				APIParser parser = new APIParser(jsonObject);
				if (parser.isOK()) {
					
					if (!loadMore) updates.clear();
					dataPage = parser.parseGetCompanyUpdates();
					haveMoreNews = dataPage.hasMore;
					listview.setPullLoadEnable(haveMoreNews);
					List<Object> items = dataPage.items;
					if (items != null) {
						for (Object obj : items) {
							updates.add((Update) obj);
						}
						if (!loadMore) {
							setData();
						}
					}
					if (updates.size() == 0) {
						noNews.setVisibility(View.VISIBLE);
					} else {
						noNews.setVisibility(View.GONE);
					}
					
				} else {
					
					updates.clear();
					newsAdapter.notifyDataSetChanged();
					
					if (updates != null) {
						listview.setPullLoadEnable(false);
						//updates.clear();
						if (null != newsAdapter) newsAdapter.notifyDataSetChanged();
					}
					
					if (APIHttpMetadata.kGGPageFlagFirstPage == aPageFlag) {
						noNews.setVisibility(View.VISIBLE);
					} else {
						noNews.setVisibility(View.GONE);
					}
				}
				firstSignUp.setVisibility(View.GONE);
				dismissLoadingDialog();
				handler.sendEmptyMessage(FRESHOK);
			}
			
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				showConnectionError();
				handler.sendEmptyMessage(FRESHOK);
			}
		});

	};
	
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
	protected void setData() {
		super.setData();
		newsAdapter = new NewsAdapter(null, NewsActivity.this, updates);
		listview.setAdapter(newsAdapter);
		newsAdapter.notifyDataSetChanged();
		newsAdapter.notifyDataSetInvalidated();
	}
	
	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (FRESHOK == msg.what) {
				stopRefreshOrLoad();
			}
		}

	};

	@Override
	public void onRefresh() {
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				refreshNews(true);
				getPendingCompany();
			}
		}, 10);
	}
	
	private void refreshNews(Boolean showDialog) {
		CommonUtil.setSimilarIdToNullFromUpadates(updates);
		getNews(0, APIHttpMetadata.kGGPageFlagFirstPage, 0, showDialog, false);
	}
	
	private void stopRefreshOrLoad() {
		listview.stopRefresh();
		listview.stopLoadMore();
	}

	@Override
	public void onLoadMore() {
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				Update lastUpdate = updates.get(updates.size() - 1);
				long aNewsID = lastUpdate.newsId;
				long aPageTime = lastUpdate.date;
				
				getNews(aNewsID, APIHttpMetadata.kGGPageFlagMoveDown, aPageTime, false, true);
			}
		}, 10);
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
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		if (Constant.ZERO == arg2 || arg2 == updates.size() + 1) return;
	}
	

}
