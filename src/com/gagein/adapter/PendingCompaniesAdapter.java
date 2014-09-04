package com.gagein.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gagein.R;
import com.gagein.http.APIHttp;
import com.gagein.model.Company;

public class PendingCompaniesAdapter extends BaseAdapter {
	
	private Context mContext;
	private List<Company> pendingCompanies = new ArrayList<Company>();
	private APIHttp mApiHttp;
	public Boolean edit = false;
	
	public PendingCompaniesAdapter(Context mContext, List<Company> pendingCompanies) {
		super();
		this.mContext = mContext;
		this.pendingCompanies = pendingCompanies;
		mApiHttp = new APIHttp(mContext);
	}
	
	public void setEdit(Boolean edit) {
		this.edit = edit;
	}

	@Override
	public int getCount() {
		return pendingCompanies.size();
	}

	@Override
	public Object getItem(int position) {
		return pendingCompanies.get(position);
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
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_pendingcompany, null);
			viewHolder.checkBtn = (ImageView) convertView.findViewById(R.id.checkBtn);
			viewHolder.image = (ImageView) convertView.findViewById(R.id.image);
			viewHolder.name = (TextView) convertView.findViewById(R.id.name);
			viewHolder.website = (TextView) convertView.findViewById(R.id.website);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		final Company company = pendingCompanies.get(position);
		final ImageView checkBtn = viewHolder.checkBtn;
		checkBtn.setVisibility(edit ? View.VISIBLE : View.GONE);
		checkBtn.setImageResource(company.select ? R.drawable.button_select : R.drawable.button_unselect);
//		button.setImageResource(company.followed ? R.drawable.follow : R.drawable.add);
//		button.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View arg0) {
//				// TODO Auto-generated method stub
//				CommonUtil.showLoadingDialog(mContext);
//				if (company.followed) {
//					mApiHttp.unfollowCompany(company.orgID, new Listener<JSONObject>() {
//
//						@Override
//						public void onResponse(JSONObject jsonObject) {
//							
//							APIParser parser = new APIParser(jsonObject);
//							if (parser.isOK()) {
//								company.followed = false;
////								Intent intent = new Intent();
////								intent.setAction(Constant.BROADCAST_REFRESH_COMPANIES);
////								mContext.sendBroadcast(intent);
//								button.setImageResource(R.drawable.add);
//							} else {
//								CommonUtil.alertMessageForParser(parser);
//							}
//							CommonUtil.dissmissLoadingDialog();
//						}
//						
//					}, new Response.ErrorListener() {
//
//						@Override
//						public void onErrorResponse(VolleyError error) {
//							CommonUtil.dissmissLoadingDialog();
//							CommonUtil.showLongToast(mContext.getResources().getString(R.string.connection_error));
//						}
//					});
//				} else {
//					mApiHttp.followCompany(company.orgID, new Listener<JSONObject>() {
//
//						@Override
//						public void onResponse(JSONObject jsonObject) {
//							
//							APIParser parser = new APIParser(jsonObject);
//							if (parser.isOK()) {
//								company.followed = true;
////								Intent intent = new Intent();
////								intent.setAction(Constant.BROADCAST_REFRESH_COMPANIES);
////								mContext.sendBroadcast(intent);
//								button.setImageResource(R.drawable.follow);
//							} else {
//								CommonUtil.alertMessageForParser(parser);
//							}
//							CommonUtil.dissmissLoadingDialog();
//						}
//						
//					}, new Response.ErrorListener() {
//
//						@Override
//						public void onErrorResponse(VolleyError error) {
//							CommonUtil.dissmissLoadingDialog();
//							CommonUtil.showLongToast(mContext.getResources().getString(R.string.connection_error));
//						}
//					});
//				}
//			}
//		});
		viewHolder.name.setText(company.name);
		String website = company.website;
		if (website.isEmpty()) {
			website = "Enter Website";
			viewHolder.website.setTextColor(mContext.getResources().getColor(R.color.yellow_bright));
		} else{
			viewHolder.website.setTextColor(mContext.getResources().getColor(R.color.c8d8d8d));
		}
		viewHolder.website.setText(website);
		
		return convertView;
	}
	
	public final class ViewHolder {
		
		public ImageView checkBtn;
		public ImageView image;
		public TextView name;
		public TextView website;
	}

}
