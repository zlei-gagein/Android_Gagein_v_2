package com.gagein.adapter;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.gagein.R;
import com.gagein.http.APIHttp;
import com.gagein.http.APIParser;
import com.gagein.model.Update;
import com.gagein.ui.company.CompanyActivity;
import com.gagein.ui.news.StoryActivity;
import com.gagein.ui.tablet.company.CompanyTabletActivity;
import com.gagein.util.CommonUtil;
import com.gagein.util.Constant;
import com.gagein.util.MessageCode;

public class BookMarksAdapter extends BaseAdapter {
	
	private Context mContext;
	private List<Update> updates = new ArrayList<Update>();
	private APIHttp mApiHttp;
	private int screenWidth;
	public Boolean edit = false;
	private Fragment mFragment;
	
	public BookMarksAdapter(Fragment mFragment, Context mContext, List<Update> updates) {
		super();
		this.mContext = mContext;
		this.updates = updates;
		this.mFragment = mFragment;
		mApiHttp = new APIHttp(mContext);
		screenWidth = mContext.getResources().getDisplayMetrics().widthPixels;
	}

	@Override
	public int getCount() {
		return updates.size();
	}

	@Override
	public Object getItem(int position) {
		return updates.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_bookmarks, null);
			viewHolder.delete = (TextView) convertView.findViewById(R.id.delete);
			viewHolder.imageview = (ImageView) convertView.findViewById(R.id.imageview);
			viewHolder.fromsource = (TextView) convertView.findViewById(R.id.fromsource);
			viewHolder.headline = (TextView) convertView.findViewById(R.id.headline);
			viewHolder.content = (TextView) convertView.findViewById(R.id.content);
			viewHolder.leftLayout = (LinearLayout) convertView.findViewById(R.id.leftLayout);
			viewHolder.leftLayout.getLayoutParams().width = screenWidth;
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		if (edit) {
			viewHolder.delete.setVisibility(View.VISIBLE);
		} else {
			viewHolder.delete.setVisibility(View.GONE);
		}
		
		final Update update = updates.get(position);
		CommonUtil.loadImage(update.org_logo_path, viewHolder.imageview);
		viewHolder.headline.setText(update.headline);
		if (TextUtils.isEmpty(update.content)) {
			viewHolder.content.setVisibility(View.GONE);
		} else {
			viewHolder.content.setVisibility(View.VISIBLE);
			viewHolder.content.setText(update.content);
		}
		viewHolder.fromsource.setText(CommonUtil.dateFormat(update.date) + " via " + update.fromSource);//TODO write to commonUtil method
		if (updates.get(position).hasBeenRead) {
			viewHolder.headline.setTextColor(mContext.getResources().getColor(R.color.text_weak));
		} else {
			viewHolder.headline.setTextColor(mContext.getResources().getColor(R.color.text_dark));
		}
		
		viewHolder.leftLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (!edit) {
					Intent intent = new Intent();
					intent.putExtra("position", position);
					intent.setClass(mContext, StoryActivity.class);
					mContext.startActivity(intent);
					Constant.setUpdates(updates);
				}
			}
		});
		
		viewHolder.delete.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				CommonUtil.showLoadingDialog(mContext);
				mApiHttp.unsaveUpdate(update.newsId, new Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject jsonObject) {

						APIParser parser = new APIParser(jsonObject);
						if (parser.isOK()) {
							CommonUtil.showShortToast("Deleted");
							updates.remove(position);
							BookMarksAdapter.this.notifyDataSetChanged();
						} else {
							String msg = MessageCode.messageForCode(parser.messageCode());
							if (msg != null && msg.length() > 0) {
								CommonUtil.showDialog(msg);
							}
						}
						CommonUtil.dissmissLoadingDialog();
					}

				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						CommonUtil.dissmissLoadingDialog();
						CommonUtil.showLongToast(mContext.getResources().getString(R.string.connection_error));
					}
				});
			}
		});
		
		viewHolder.imageview.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//
				Intent intent = new Intent();
				intent.putExtra(Constant.COMPANYID, update.orgID);
				intent.setClass(mContext, CommonUtil.isTablet(mContext) ? CompanyTabletActivity.class : CompanyActivity.class);
				mContext.startActivity(intent);
			}
		});
		
		return convertView;
	}
	
	public final class ViewHolder {
		
		public TextView delete;
		public ImageView imageview;
		public TextView headline;
		public TextView content;
		public TextView fromsource;
		public LinearLayout leftLayout;
	}

}
