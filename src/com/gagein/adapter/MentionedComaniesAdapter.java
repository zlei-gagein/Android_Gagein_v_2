package com.gagein.adapter;

import java.util.List;

import org.json.JSONObject;

import android.content.Context;
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

public class MentionedComaniesAdapter extends BaseAdapter {
	
	private Context mContext;
	private List<Company> mentionedCompanies;
	private APIHttp mApiHttp;
	
	public MentionedComaniesAdapter(Context mContext,List<Company> mentionedCompanies) {
		super();
		this.mContext = mContext;
		this.mentionedCompanies = mentionedCompanies;
		mApiHttp = new APIHttp(mContext);
	}

	@Override
	public int getCount() {
		return mentionedCompanies.size();
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
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_mentioned_companies, null);
			viewHolder.picture = (ImageView) convertView.findViewById(R.id.picture);
			viewHolder.button = (ImageView) convertView.findViewById(R.id.button);
			viewHolder.name = (TextView) convertView.findViewById(R.id.name);
			viewHolder.website = (TextView) convertView.findViewById(R.id.website);
			viewHolder.address = (TextView) convertView.findViewById(R.id.address);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		final Company company = mentionedCompanies.get(position);
		CommonUtil.loadImage(company.logoPath, viewHolder.picture);
		viewHolder.button.setVisibility(company.hasFollowFiled ? View.VISIBLE : View.GONE);
		viewHolder.button.setImageResource(company.followed ? R.drawable.follow : R.drawable.add);
		viewHolder.name.setText(company.name);
		viewHolder.website.setText(CommonUtil.removeWebsite3W(company.website));
		viewHolder.address.setText(company.address());
		final ImageView button = viewHolder.button;
		viewHolder.button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				CommonUtil.showLoadingDialog(mContext);
				if (company.followed) {
					mApiHttp.unfollowCompany(company.orgID, new Listener<JSONObject>() {

						@Override
						public void onResponse(JSONObject jsonObject) {
							
							APIParser parser = new APIParser(jsonObject);
							if (parser.isOK()) {
								company.followed = false;
//								Broadcaster.post(Broadcaster.Name.COMPANY_UNFOLLOWED, Broadcaster.Key.COMPANY_DATA, company.getData());//TODO
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
//								Broadcaster.post(Broadcaster.Name.COMPANY_FOLLOWED, Broadcaster.Key.COMPANY_DATA, company.getData());//TODO
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
		
		public ImageView picture;
		public ImageView button;
		public TextView name;
		public TextView website;
		public TextView address;
	}

}
