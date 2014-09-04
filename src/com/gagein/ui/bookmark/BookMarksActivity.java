package com.gagein.ui.bookmark;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

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
		
		getSaved(true, false);
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
	
	private void getSaved(final Boolean isShowDialog, final Boolean loadMore) {

		if (isShowDialog) showLoadingDialog();
		mApiHttp.getSavedUpdates(page, false,new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject jsonObject) {

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

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onRefresh() {
		page = 1;
		getSaved(false, false);
	}

	@Override
	public void onLoadMore() {
		getSaved(false, true);
	}
}
