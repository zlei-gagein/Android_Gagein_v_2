package com.gagein.adapter;

import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
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
import com.gagein.model.Company;
import com.gagein.util.CommonUtil;
import com.gagein.util.Constant;

public class CompanyCompetitorsAdapter extends BaseAdapter {
	
	private Context mContext;
	private List<Company> competitors;
	public Boolean edit = false;
	private APIHttp mApiHttp;
	
	public CompanyCompetitorsAdapter(Context mContext, List<Company> competitors) {
		super();
		this.mContext = mContext;
		this.competitors = competitors;
		mApiHttp = new APIHttp(mContext);
	}

	@Override
	public int getCount() {
		return competitors.size();
	}

	@Override
	public Object getItem(int position) {
		return competitors.get(position);
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
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_company_competitor, null);
			viewHolder.image = (ImageView) convertView.findViewById(R.id.image);
			viewHolder.name = (TextView) convertView.findViewById(R.id.name);
			viewHolder.button = (ImageView) convertView.findViewById(R.id.button);
			viewHolder.website = (TextView) convertView.findViewById(R.id.website);
			viewHolder.address = (TextView) convertView.findViewById(R.id.address);
			viewHolder.typeLayout = (LinearLayout) convertView.findViewById(R.id.typeLayout);
			viewHolder.employeesLayout = (LinearLayout) convertView.findViewById(R.id.employeesLayout);
			viewHolder.revenueLayout = (LinearLayout) convertView.findViewById(R.id.revenueLayout);
			viewHolder.ownership = (TextView) convertView.findViewById(R.id.ownership);
			viewHolder.rank = (TextView) convertView.findViewById(R.id.rank);
			viewHolder.employees = (TextView) convertView.findViewById(R.id.employees);
			viewHolder.revenue = (TextView) convertView.findViewById(R.id.revenue);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		final Company competitor = competitors.get(position);
		String webSite = competitor.website;
		viewHolder.image.setImageResource(R.drawable.logo_company_default);
		CommonUtil.loadImage(competitor.logoPath, viewHolder.image);
		viewHolder.name.setText(competitor.name);
		viewHolder.website.setText(CommonUtil.removeWebsite3W(webSite));
		viewHolder.address.setText(competitor.address());
		viewHolder.button.setVisibility(competitor.hasFollowFiled ? View.VISIBLE : View.GONE);
		viewHolder.button.setImageResource(competitor.followed ? R.drawable.follow : R.drawable.add);
		final ImageView button = viewHolder.button;
		viewHolder.button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				CommonUtil.showLoadingDialog(mContext);
				if (competitor.followed) {
					mApiHttp.unfollowCompany(competitor.orgID, new Listener<JSONObject>() {

						@Override
						public void onResponse(JSONObject jsonObject) {
							
							APIParser parser = new APIParser(jsonObject);
							if (parser.isOK()) {
								competitor.followed = false;
								Intent intent = new Intent();
								intent.setAction(Constant.BROADCAST_REFRESH_COMPANIES);
								mContext.sendBroadcast(intent);
								button.setImageResource(R.drawable.add);
							} else {
								CommonUtil.alertMessageForParser(parser);
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
				} else {
					mApiHttp.followCompany(competitor.orgID, new Listener<JSONObject>() {

						@Override
						public void onResponse(JSONObject jsonObject) {
							
							APIParser parser = new APIParser(jsonObject);
							if (parser.isOK()) {
								competitor.followed = true;
								Intent intent = new Intent();
								intent.setAction(Constant.BROADCAST_REFRESH_COMPANIES);
								mContext.sendBroadcast(intent);
								button.setImageResource(R.drawable.follow);
							} else {
								CommonUtil.alertMessageForParser(parser);
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
			}
		});
		
		if (CommonUtil.isTablet(mContext)) {//TODO
			viewHolder.typeLayout.setVisibility(View.VISIBLE);
			viewHolder.employeesLayout.setVisibility(View.VISIBLE);
			viewHolder.revenueLayout.setVisibility(View.VISIBLE);
			
			viewHolder.ownership.setText(competitor.ownership);
			
			if (!competitor.revenueSize.trim().equals(Constant.ZERO + "")) {
				viewHolder.revenue.setText(CommonUtil.revenueFormat(competitor.revenueSize));
			} else {
				viewHolder.revenue.setText("");
			}
			
			if (!competitor.employeeSize.trim().equals(Constant.ZERO + "")) {
				viewHolder.employees.setText(CommonUtil.employeesSizeFromat(competitor.employeeSize)); 
			} else {
				viewHolder.employees.setText("");
			}
			
			if (!TextUtils.isEmpty(competitor.fortuneRank)) {
				viewHolder.rank.setText("Fortune " + competitor.fortuneRank);
			} else if (!TextUtils.isEmpty(competitor.inc5000_rank)) {
				viewHolder.rank.setText("Inc " + competitor.inc5000_rank);
			} else if (!TextUtils.isEmpty(competitor.global_rank)) {
				viewHolder.rank.setText("Global " + competitor.global_rank);
			} else if (!TextUtils.isEmpty(competitor.russell_rank)) {
				viewHolder.rank.setText("Russell " + competitor.russell_rank);
			}
		}
		
		return convertView;
	}
	
	public final class ViewHolder {
		
		public ImageView image;
		public TextView name;
		public TextView website;
		public TextView address;
		public ImageView button;
		public LinearLayout typeLayout;
		public LinearLayout employeesLayout;
		public LinearLayout revenueLayout;
		public TextView ownership;
		public TextView rank;
		public TextView employees;
		public TextView revenue;
	}
}
