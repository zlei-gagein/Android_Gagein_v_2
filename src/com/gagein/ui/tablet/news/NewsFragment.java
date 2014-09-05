package com.gagein.ui.tablet.news;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;

import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.gagein.R;
import com.gagein.adapter.NewsAdapter;
import com.gagein.component.xlistview.XListView;
import com.gagein.component.xlistview.XListView.IXListViewListener;
import com.gagein.http.APIHttp;
import com.gagein.http.APIHttpMetadata;
import com.gagein.http.APIParser;
import com.gagein.model.Company;
import com.gagein.model.DataPage;
import com.gagein.model.Update;
import com.gagein.ui.BaseFragment;
import com.gagein.ui.bookmark.BookMarksActivity;
import com.gagein.ui.companies.PendingCompaniesActivity;
import com.gagein.util.CommonUtil;
import com.gagein.util.Constant;
import com.gagein.util.Log;

public class NewsFragment extends BaseFragment implements OnClickListener, IXListViewListener, OnItemClickListener{
	
	private Context mContext;
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
	private OnFilterClickListener filterListener;
	
	
	public interface OnFilterClickListener {
		public void onFilterClickListener();
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			filterListener = (OnFilterClickListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + "must implement onFilterClickListener");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.activity_news, container, false);
		mContext = getActivity();
		mApiHttp = new APIHttp(mContext);
		
		doInit();
		return view;
	}
	
	@Override
	protected void initView() {
		super.initView();
		listview = (XListView) view.findViewById(R.id.listview);
		noNews = (LinearLayout) view.findViewById(R.id.noNews);
		firstSignUp = (LinearLayout) view.findViewById(R.id.firstSignUp);
		pendingLayout = (LinearLayout) view.findViewById(R.id.pendingLayout);
		pending = (Button) view.findViewById(R.id.pending);
		
		setRightImageButton(R.drawable.bookmarks);
		setLeftButton(R.string.filters);
		setTitle(R.string.u_news);
	}
	
	public void setTopBtnVisible(int visible) {
		setTitleVisible(visible);
		setRightImageVisible(visible);
		setLeftBtnVisible(visible);
	}
	
	protected void initData() {
		
		if (Constant.SIGNUP) {
			firstSignUp.setVisibility(View.VISIBLE);
		} else {
			getPendingCompany();
			getNews(0, APIHttpMetadata.kGGPageFlagFirstPage, 0, true, false, false);
		}
	}
	
	public void setLikeFromBroadcast(long newsId, Boolean like) {
		
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
		if (v == leftBtn) {
			filterListener.onFilterClickListener();
		} else if (v == rightImageBtn) {
			Intent intent = new Intent();
			intent.setClass(mContext, BookMarksActivity.class);
			startActivity(intent);
		} else if (v == noNews) {
			CommonUtil.setSimilarIdToNullFromUpadates(updates);
			getNews(0, APIHttpMetadata.kGGPageFlagFirstPage, 0, true, false, false);
		} else if (v == pending) {
			Intent intent = new Intent();
			intent.setClass(mContext, PendingCompaniesActivity.class);
			startActivity(intent);
		}
	}
	
	private void getNews(long aNewsID, byte aPageFlag, long aPageTime, final Boolean showDialog, final Boolean loadMore, Boolean fromBroadcast) {
//		if (showDialog && fromBroadcast) showLoadingDialog(mContext);
		if (showDialog) showLoadingDialog(mContext);
		mApiHttp.getCompanyUpdates(companyId, aNewsID, aPageFlag, aPageTime, CommonUtil.stringSimilarIDsWithUpdates(updates), new Listener<JSONObject>() {
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
					if (updates != null) {
						listview.setPullLoadEnable(false);
						updates.clear();
						if (null != newsAdapter) newsAdapter.notifyDataSetChanged();
					}
//					String msg = jsonObject.optString("msg");
//					if (msg.equalsIgnoreCase("error")) {
//					}
					noNews.setVisibility(View.VISIBLE);
				}
				firstSignUp.setVisibility(View.GONE);
				dismissLoadingDialog();
				handler.sendEmptyMessage(FRESHOK);
			}
			
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				showConnectionError(mContext);
				handler.sendEmptyMessage(FRESHOK);
			}
		});

	};
	
	public void getPendingCompany() {
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
				CommonUtil.dissmissLoadingDialog();
				CommonUtil.showLongToast(mContext.getResources().getString(R.string.connection_error));
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
	protected void setData() {
		super.setData();
		newsAdapter = new NewsAdapter(this, getActivity(), updates);
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
				
				refreshNews(false);
				getPendingCompany();
			}
		}, 10);
	}
	
	public void refreshNews(Boolean fromBroadcast) {
		CommonUtil.setSimilarIdToNullFromUpadates(updates);
		getNews(0, APIHttpMetadata.kGGPageFlagFirstPage, 0, true, false, fromBroadcast);
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
				getNews(aNewsID, APIHttpMetadata.kGGPageFlagMoveDown, aPageTime, false, true, false);
			}
		}, 10);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
	}
}
