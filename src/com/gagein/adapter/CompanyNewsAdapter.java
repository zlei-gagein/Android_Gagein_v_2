package com.gagein.adapter;

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
import android.widget.HorizontalScrollView;
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
import com.gagein.ui.tablet.company.CompanyFragment;
import com.gagein.util.CommonUtil;
import com.gagein.util.Constant;
import com.gagein.util.MessageCode;

public class CompanyNewsAdapter extends BaseAdapter {

	private Context mContext;
	private APIHttp mApiHttp;
	private int screenWidth;
	public Boolean edit = false;
	private List<Update> mUpdates;
	private Fragment mFragment;

	public CompanyNewsAdapter(Fragment mFragment, Context mContext, List<Update> updates) {
		super();
		this.mContext = mContext;
		this.mUpdates = updates;
		this.mFragment = mFragment;
		mApiHttp = new APIHttp(mContext);
		screenWidth = mContext.getResources().getDisplayMetrics().widthPixels;
	}

	@Override
	public int getCount() {
		return mUpdates.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
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
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_news, null);
			viewHolder.shareBtn = (TextView) convertView.findViewById(R.id.shareBtn);
			viewHolder.delete = (TextView) convertView.findViewById(R.id.delete);
			viewHolder.imageview = (ImageView) convertView.findViewById(R.id.imageview);
			viewHolder.fromsource = (TextView) convertView.findViewById(R.id.fromsource);
			viewHolder.headline = (TextView) convertView.findViewById(R.id.headline);
			viewHolder.content = (TextView) convertView.findViewById(R.id.content);
			viewHolder.leftLayout = (LinearLayout) convertView.findViewById(R.id.leftLayout);
			viewHolder.scrollview = (HorizontalScrollView) convertView.findViewById(R.id.scrollview);
			viewHolder.leftLayout.getLayoutParams().width = screenWidth;
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.delete.setVisibility(edit ? View.VISIBLE : View.GONE);
		final HorizontalScrollView scrollview = viewHolder.scrollview;
		scrollview.scrollTo(0, 0);

		final Update update = mUpdates.get(position);
		viewHolder.imageview.setImageResource(R.drawable.logo_company_default);
		CommonUtil.loadImage(update.org_logo_path, viewHolder.imageview);
		viewHolder.headline.setText(update.headline);
		if (TextUtils.isEmpty(update.content)) {
			viewHolder.content.setVisibility(View.GONE);
		} else {
			viewHolder.content.setVisibility(View.VISIBLE);
			viewHolder.content.setText(update.content);
		}
		viewHolder.fromsource.setText(CommonUtil.dateFormat(update.date) + " via " + update.fromSource);
		if (mUpdates.get(position).hasBeenRead) {
			viewHolder.headline.setTextColor(mContext.getResources().getColor(R.color.text_weak));
		} else {
			viewHolder.headline.setTextColor(mContext.getResources().getColor(R.color.text_dark));
		}

		viewHolder.leftLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra("position", position);
				intent.setClass(mContext, StoryActivity.class);
				mContext.startActivity(intent);
				Constant.setUpdates(mUpdates);
			}
		});

		viewHolder.shareBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				scrollview.scrollTo(0, 0);
				if (CommonUtil.isTablet(mContext)) {
//					if (update == null) Log.v("silen", "update == null");
//					if (mFragment == null) Log.v("silen", "mFragment == null");
					((CompanyFragment)mFragment).showShareLayout(update, mContext);
				} else {
					((CompanyActivity) mContext).showShareLayout(update);
				}
				
			}
		});

		viewHolder.delete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				CommonUtil.showLoadingDialog(mContext);
				mApiHttp.unsaveUpdate(update.newsId,
						new Listener<JSONObject>() {

							@Override
							public void onResponse(JSONObject jsonObject) {

								APIParser parser = new APIParser(jsonObject);
								if (parser.isOK()) {
									mUpdates.remove(position);
									CompanyNewsAdapter.this.notifyDataSetChanged();
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
				Intent intent = new Intent();
				intent.putExtra(Constant.COMPANYID, update.orgID);
				intent.setClass(mContext, CompanyActivity.class);
				mContext.startActivity(intent);
			}
		});
		return convertView;
	}

	public final class ViewHolder {
		public TextView shareBtn;
		public TextView delete;
		public ImageView imageview;
		public TextView headline;
		public TextView content;
		public TextView fromsource;
		public LinearLayout leftLayout;
		public HorizontalScrollView scrollview;
	}
}
