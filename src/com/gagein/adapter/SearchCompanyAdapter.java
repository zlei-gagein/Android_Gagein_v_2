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

public class SearchCompanyAdapter extends BaseAdapter {
	
	private Context mContext;
	private List<Company> companies;
	public Boolean edit = false;
	private APIHttp mApiHttp;
	
	public SearchCompanyAdapter(Context mContext, List<Company> companies) {
		super();
		this.mContext = mContext;
		this.companies = companies;
		mApiHttp = new APIHttp(mContext);
	}

	@Override
	public int getCount() {
		return companies.size();
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
		

		final Company company = companies.get(position);
		String webSite = company.website;
		viewHolder.image.setImageResource(R.drawable.logo_company_default);
		CommonUtil.loadImage(company.logoPath, viewHolder.image);
		viewHolder.name.setText(company.name);
		if (!webSite.isEmpty()) viewHolder.website.setText(webSite.replace("www.", ""));
		viewHolder.address.setText(company.address());
		viewHolder.button.setVisibility(company.hasFollowFiled ? View.VISIBLE : View.GONE);
		viewHolder.button.setImageResource(company.followed ? R.drawable.follow : R.drawable.add);
		
		final ImageView button = viewHolder.button;
		button.setVisibility(View.VISIBLE);
		viewHolder.button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				CommonUtil.showLoadingDialog(mContext);
				if (company.followed) {
					mApiHttp.unfollowCompany(company.orgID, new Listener<JSONObject>() {
						
						@Override
						public void onResponse(JSONObject jsonObject) {
							
							APIParser parser = new APIParser(jsonObject);
							if (parser.isOK()) {
								company.followed = false;
								Intent intent = new Intent();
								intent.setAction(Constant.BROADCAST_UNFOLLOW_COMPANY);
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
					mApiHttp.followCompany(company.orgID, new Listener<JSONObject>() {
						
						@Override
						public void onResponse(JSONObject jsonObject) {
							
							APIParser parser = new APIParser(jsonObject);
							if (parser.isOK()) {
								company.followed = true;
								Intent intent = new Intent();
								intent.setAction(Constant.BROADCAST_FOLLOW_COMPANY);
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
			
			viewHolder.ownership.setText(company.ownership);
			
			if (!company.revenueSize.trim().equals(Constant.ZERO + "")) {
				viewHolder.revenue.setText(CommonUtil.revenueFormat(company.revenueSize));
			} else {
				viewHolder.revenue.setText("");
			}
			
			if (!company.employeeSize.trim().equals(Constant.ZERO + "") && !TextUtils.isEmpty(company.employeeSize)) {
				viewHolder.employees.setText(CommonUtil.employeesSizeFromat(company.employeeSize)); 
			} else {
				viewHolder.employees.setText("");
			}
			
			if (!TextUtils.isEmpty(company.fortuneRank)) {
				viewHolder.rank.setText("Fortune " + company.fortuneRank);
			} else if (!TextUtils.isEmpty(company.inc5000_rank)) {
				viewHolder.rank.setText("Inc " + company.inc5000_rank);
			} else if (!TextUtils.isEmpty(company.global_rank)) {
				viewHolder.rank.setText("Global " + company.global_rank);
			} else if (!TextUtils.isEmpty(company.russell_rank)) {
				viewHolder.rank.setText("Russell " + company.russell_rank);
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
