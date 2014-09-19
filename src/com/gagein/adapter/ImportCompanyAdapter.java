package com.gagein.adapter;

import java.util.ArrayList;
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
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.gagein.R;
import com.gagein.component.dialog.FollowAndGroupDialog;
import com.gagein.component.dialog.UnfollowCompanyDialog;
import com.gagein.http.APIHttp;
import com.gagein.http.APIParser;
import com.gagein.model.Company;
import com.gagein.model.Group;
import com.gagein.ui.companies.AddToGroupActivity;
import com.gagein.util.CommonUtil;
import com.gagein.util.Constant;
import com.gagein.util.Log;

public class ImportCompanyAdapter extends BaseAdapter{
	
	private Context mContext;
	private List<Company> suggestedCompanies;
	public Boolean edit = false;
	private APIHttp mApiHttp;
	private Group group;
	
	public ImportCompanyAdapter(Context mContext, List<Company> suggestedCompanies) {
		super();
		this.mContext = mContext;
		this.suggestedCompanies = suggestedCompanies;
		mApiHttp = new APIHttp(mContext);
	}
	
	public void setGroup(Group group) {
		this.group = group;
	}

	@Override
	public int getCount() {
		return suggestedCompanies.size();
	}

	@Override
	public Object getItem(int position) {
		return suggestedCompanies.get(position);
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
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_import_company, null);
			viewHolder.image = (ImageView) convertView.findViewById(R.id.image);
			viewHolder.button = (ImageView) convertView.findViewById(R.id.button);
			viewHolder.name = (TextView) convertView.findViewById(R.id.name);
			viewHolder.website = (TextView) convertView.findViewById(R.id.website);
			if (CommonUtil.isTablet(mContext)) viewHolder.contactName = (TextView) convertView.findViewById(R.id.contactName);
			if (CommonUtil.isTablet(mContext)) viewHolder.others = (TextView) convertView.findViewById(R.id.others);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		final Company mCompany = suggestedCompanies.get(position);
		CommonUtil.loadImage(mCompany.logoPath, viewHolder.image);
		viewHolder.name.setText(mCompany.name);
		viewHolder.website.setText(CommonUtil.removeWebsite3W(mCompany.website) + "  " +  mCompany.address());
		
		if (CommonUtil.isTablet(mContext)) {
			List<String> nameList = mCompany.getNameList();
			String contactName = "";
			String others = "";
			if (nameList.size() == 1) {
				contactName = nameList.get(0);
			} else if (nameList.size() >= 2) {
				contactName = nameList.get(0);
				others = " & " + (nameList.size() - 1) + " others";
			}
			
			viewHolder.others.setText(others);
			viewHolder.contactName.setText(contactName);
			if (!TextUtils.isEmpty(others)) {
				Log.v("silen", "!TextUtils.isEmpty(others)");
				CommonUtil.setContactNameViewWidth(viewHolder.contactName);
			}
			
		}
		
		final ImageView button = viewHolder.button;
		button.setImageResource(mCompany.followed ? R.drawable.follow : R.drawable.add);
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (mCompany.followed) {
					
					final UnfollowCompanyDialog dialog = new UnfollowCompanyDialog(mContext);
					dialog.showDialog(new OnClickListener() {
						
						@Override
						public void onClick(View arg0) {
							dialog.dismissDialog();
							unFollowCompany(mCompany, button);
						}
					}, new OnClickListener() {
						
						@Override
						public void onClick(View arg0) {
							dialog.dismissDialog();
						}
					});
					
				} else {
					
					final FollowAndGroupDialog dialog = new FollowAndGroupDialog(mContext);
					dialog.showDialog(new OnClickListener() {
						
						@Override
						public void onClick(View arg0) {
							dialog.dismissDialog();
							followCompany(mCompany, button, false);
						}
					}, new OnClickListener() {
						
						@Override
						public void onClick(View arg0) {
							followCompany(mCompany, button, true);
							dialog.dismissDialog();
						}
					});
					
				}
			}

			private void followCompany(final Company mCompany, final ImageView button, final Boolean addToGroup) {
				CommonUtil.showLoadingDialog(mContext);
				mApiHttp.followCompany(mCompany.orgID, new Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject jsonObject) {
						
						CommonUtil.dissmissLoadingDialog();
						APIParser parser = new APIParser(jsonObject);
						if (parser.isOK()) {
							mCompany.followed = true;
							Intent intent = new Intent();
							intent.setAction(Constant.BROADCAST_FOLLOW_COMPANY);
							mContext.sendBroadcast(intent);
							button.setImageResource(R.drawable.follow);
							
							if (addToGroup) {
								ArrayList<String> companiesIds = new ArrayList<String>();
								companiesIds.add(mCompany.orgID + "");
								Intent intentTo = new Intent();
								intentTo.putExtra(Constant.GROUP, group);
								intentTo.putExtra(Constant.COMPANIESID, companiesIds);
								intentTo.setClass(mContext, AddToGroupActivity.class);
								mContext.startActivity(intentTo);
							}
								
						} else {
							CommonUtil.alertMessageForParser(parser);
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

			private void unFollowCompany(final Company mCompany, final ImageView button) {
				CommonUtil.showLoadingDialog(mContext);
				mApiHttp.unfollowCompany(mCompany.orgID, new Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject jsonObject) {
						
						APIParser parser = new APIParser(jsonObject);
						if (parser.isOK()) {
							mCompany.followed = false;
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
			}
		});
		return convertView;
	}
	
	public final class ViewHolder {
		
		public ImageView image;
		public ImageView button;
		public TextView name;
		public TextView website;
		public TextView contactName;
		public TextView others;
	}

}
