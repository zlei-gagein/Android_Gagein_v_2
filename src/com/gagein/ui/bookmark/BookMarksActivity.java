package com.gagein.ui.bookmark;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;

import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.gagein.R;
import com.gagein.adapter.BookMarksAdapter;
import com.gagein.component.xlistview.XListView;
import com.gagein.component.xlistview.XListView.IXListViewListener;
import com.gagein.http.APIParser;
import com.gagein.model.DataPage;
import com.gagein.model.Update;
import com.gagein.ui.main.BaseActivity;
import com.gagein.util.Constant;
import com.gagein.util.Log;
import com.gagein.util.MessageCode;

public class BookMarksActivity extends BaseActivity implements IXListViewListener, OnItemClickListener{
	private LinearLayout noBookmarks;
	private BookMarksAdapter adapter;
	private XListView listview;
	private int page = 1;
	private List<Update> updates;
	private DataPage dataPage;
	public int showShareItem;
	private Boolean edit = false;
	
	@Override
	protected List<String> observeNotifications() {
		return stringList(Constant.BROADCAST_LIKED_NEWS, Constant.BROADCAST_UNLIKE_NEWS, Constant.BROADCAST_IRRELEVANT_FALSE, 
				Constant.BROADCAST_IRRELEVANT_TRUE, Constant.BROADCAST_REMOVE_BOOKMARKS);
	}
	
	@Override
	public void handleNotifications(Context aContext, Intent intent) {
		super.handleNotifications(aContext, intent);
		
		String actionName = intent.getAction();
		
		if (actionName.equals(Constant.BROADCAST_LIKED_NEWS)) {
			
			refreshUpdatesLikeStatus(intent, true);
			
		} else if (actionName.equals(Constant.BROADCAST_UNLIKE_NEWS)) {
			
			refreshUpdatesLikeStatus(intent, false);
			
		} else if (actionName.equals(Constant.BROADCAST_IRRELEVANT_FALSE)) {
			
			refreshUpdatesIrrelevantStatus(intent, false);
			
		} else if (actionName.equals(Constant.BROADCAST_IRRELEVANT_TRUE)) {
			
			refreshUpdatesIrrelevantStatus(intent, true);
			
		} else if (actionName.equals(Constant.BROADCAST_REMOVE_BOOKMARKS)) {
			
			removeBookmark(intent);
			
		}
		
	}
	
	private void refreshUpdatesLikeStatus(Intent intent, Boolean like) {
		
		long mUpdateId = intent.getLongExtra(Constant.UPDATEID, 0);
		
		for (int i = 0; i < updates.size(); i ++) {
			long updateId = updates.get(i).newsId;
			if (updateId == mUpdateId) {
				updates.get(i).liked = like;
				adapter.notifyDataSetChanged();
			}
		}
		
	}
	
	private void refreshUpdatesIrrelevantStatus(Intent intent, Boolean irrelevant) {
		
		long mUpdateId = intent.getLongExtra(Constant.UPDATEID, 0);
		
		for (int i = 0; i < updates.size(); i ++) {
			long updateId = updates.get(i).newsId;
			if (updateId == mUpdateId) {
				Log.v("silen", "irrelevant = " + irrelevant);
				updates.get(i).irrelevant = irrelevant;
				adapter.notifyDataSetChanged();
			}
		}
		
	}
	
	private void removeBookmark(Intent intent) {
		
		long mUpdateId = intent.getLongExtra(Constant.UPDATEID, 0);
		
		for (int i = 0; i < updates.size(); i ++) {
			long updateId = updates.get(i).newsId;
			if (updateId == mUpdateId) {
				updates.remove(i);
				adapter.notifyDataSetChanged();
			}
		}
		
	}
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bookmarks);
		
		doInit();
	}
	
	@Override
	protected void initView() {
		super.initView();
		
		setTitle(R.string.u_bookmarks);
		setLeftButton(R.string.u_edit);
		setRightButton(R.string.done);
		
		noBookmarks = (LinearLayout) findViewById(R.id.noBookmarks);
		listview = (XListView) findViewById(R.id.listview);
		listview.setPullLoadEnable(false);
	}
	
	@Override
	protected void initData() {
		super.initData();
		
		getBookmarks(true, false);
	}
	
	@Override
	protected void setOnClickListener() {
		super.setOnClickListener();
		
		leftBtn.setOnClickListener(this);
		rightBtn.setOnClickListener(this);
		
		listview.setXListViewListener(this);
		listview.setOnItemClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		
		if (v == leftBtn) {
			if (null == updates || updates.size() == 0) return;
			if (null == adapter) return;
			adapter.edit = !adapter.edit;
			adapter.notifyDataSetChanged();
			
			edit = adapter.edit ? true : false;
			if (edit) {
				setRightButtonVisible(View.GONE);
				setLeftButtonVisible(View.GONE);
				setLeftImageButton(R.drawable.back_arrow);
			}
		} else if (v == rightBtn) {
			finish();
		} else if (v == leftImageBtn) {
			if (null == updates || updates.size() == 0) return;
			if (null == adapter) return;
			adapter.edit = !adapter.edit;
			adapter.notifyDataSetChanged();
			
			edit = adapter.edit ? true : false;
			if (!edit) {
				setRightButtonVisible(View.VISIBLE);
				setLeftButtonVisible(View.VISIBLE);
				setLeftImageButtonVisible(View.GONE);
			}
		}
	}
	
	@Override
	protected void setData() {
		super.setData();
		adapter = new BookMarksAdapter(null, BookMarksActivity.this, updates);
		listview.setAdapter(adapter);
		adapter.edit = edit;
		adapter.notifyDataSetChanged();
		adapter.notifyDataSetInvalidated();
	}
	
	private void getBookmarks(final Boolean isShowDialog, final Boolean loadMore) {

		if (isShowDialog) showLoadingDialog();
		mApiHttp.getSavedUpdates(page, false, new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject jsonObject) {
				
				Log.v("silen", "jsonObject = " + jsonObject.toString());

				dismissLoadingDialog();
				APIParser parser = new APIParser(jsonObject);
				if (parser.isOK()) {
					
					Boolean hasMore = parser.dataHasMore();
					if (hasMore) page++;
					listview.setPullLoadEnable(hasMore);
					
					if (!loadMore) {
						updates = new ArrayList<Update>();
					}
					dataPage = parser.parseGetCompanyUpdates();
					List<Object> items = dataPage.items;
					if (items != null) {
						for (Object obj : items) {
							updates.add((Update) obj);
							Log.v("silen", "irrelevant = " + ((Update) obj).irrelevant);
						}
					}
					
					noBookmarks.setVisibility((updates.size() == 0) ? View.VISIBLE : View.GONE);
					listview.setVisibility(View.VISIBLE);
					if (!loadMore) setData();
					
				} else {
					
					noBookmarks.setVisibility(View.VISIBLE);
					
					if (MessageCode.NoSavedUpdates == parser.messageCode()) {
						updates = new ArrayList<Update>();
					}
					String msg = MessageCode.messageForCode(parser.messageCode());
					if (msg != null && msg.length() > 0) {
						showDialog(msg);
					}
				}
			}
			
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				showConnectionError();
			}
		});
	}
	
	public void setNoBookMarks() {
		
		noBookmarks.setVisibility(View.VISIBLE);
		edit = false;
		setLeftButton(R.string.u_edit);
		setLeftImageButtonVisible(View.GONE);
		setRightButton(R.string.done);
		
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
	}

	@Override
	public void onRefresh() {
		page = 1;
		getBookmarks(false, false);
	}

	@Override
	public void onLoadMore() {
		getBookmarks(false, true);
	}
}
