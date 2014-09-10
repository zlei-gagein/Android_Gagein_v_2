package com.gagein.adapter;

import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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

public class CompanyItemAdapter extends BaseAdapter {
	
	private Context mContext;
	private List<Company> mCompanies;
	private APIHttp mApiHttp;
	
	public CompanyItemAdapter(Context mContext, List<Company> companies) {
		super();
		this.mContext = mContext;
		this.mCompanies = companies;
		mApiHttp = new APIHttp(mContext);
	}

	@Override
	public int getCount() {
		return mCompanies.size();
	}

	@Override
	public Object getItem(int position) {
		return mCompanies.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_company, null);
			viewHolder.image = (ImageView) convertView.findViewById(R.id.image);
			viewHolder.button = (ImageView) convertView.findViewById(R.id.button);
			viewHolder.name = (TextView) convertView.findViewById(R.id.name);
			viewHolder.website = (TextView) convertView.findViewById(R.id.website);
			viewHolder.address = (TextView) convertView.findViewById(R.id.address);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		final Company mCompany = mCompanies.get(position);
		CommonUtil.loadImage(mCompany.logoPath, viewHolder.image);
		viewHolder.button.setVisibility(mCompany.hasFollowFiled ? View.VISIBLE : View.GONE);
		viewHolder.button.setImageResource(mCompany.followed ? R.drawable.follow : R.drawable.add);
		viewHolder.name.setText(mCompany.name);
		viewHolder.website.setText(CommonUtil.removeWebsite3W(mCompany.website));
		viewHolder.address.setText(mCompany.address());
		
		final ImageView button = viewHolder.button;
		viewHolder.button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				CommonUtil.showLoadingDialog(mContext);
				
				final long companyId = mCompany.orgID;
				if (mCompany.followed) {
					
					mApiHttp.unfollowCompany(companyId, new Listener<JSONObject>() {
						
						@Override
						public void onResponse(JSONObject jsonObject) {
							
							APIParser parser = new APIParser(jsonObject);
							if (parser.isOK()) {
								
								mCompany.followed = false;
								
								Intent intent = new Intent();
								intent.putExtra(Constant.COMPANYID, companyId);
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
					
					mApiHttp.followCompany(companyId, new Listener<JSONObject>() {
						
						@Override
						public void onResponse(JSONObject jsonObject) {
							
							APIParser parser = new APIParser(jsonObject);
							if (parser.isOK()) {
								
								mCompany.followed = true;
								Intent intent = new Intent();
								intent.putExtra(Constant.COMPANYID, companyId);
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
		return convertView;
	}
	
	public final class ViewHolder {
		
		public ImageView image;
		public ImageView button;
		public TextView name;
		public TextView website;
		public TextView address;
		
	}

}
