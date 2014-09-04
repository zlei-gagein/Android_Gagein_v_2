package com.gagein.adapter;

import java.util.List;

import org.json.JSONObject;

import android.annotation.SuppressLint;
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
import com.emilsjolander.components.stickylistheaders.StickyListHeadersAdapter;
import com.gagein.R;
import com.gagein.http.APIHttp;
import com.gagein.http.APIParser;
import com.gagein.model.Company;
import com.gagein.model.Person;
import com.gagein.util.CommonUtil;
import com.gagein.util.Constant;

public class SearchAdapter extends BaseAdapter implements StickyListHeadersAdapter{
	
	private Context mContext;
	private List<Company> companies;
	private List<Person> persons;
	public Boolean edit = false;
	private APIHttp mApiHttp;
	
	public SearchAdapter(Context mContext, List<Company> companies, List<Person> persons) {
		super();
		this.mContext = mContext;
		this.companies = companies;
		this.persons = persons;
		mApiHttp = new APIHttp(mContext);
	}

	@Override
	public int getCount() {
		return companies.size() + persons.size();
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
		ViewHolder viewHolder = new ViewHolder();
		convertView = LayoutInflater.from(mContext).inflate(R.layout.item_company_competitor, null);
		
		viewHolder.image = (ImageView) convertView.findViewById(R.id.image);
		viewHolder.name = (TextView) convertView.findViewById(R.id.name);
		viewHolder.button = (ImageView) convertView.findViewById(R.id.button);
		viewHolder.website = (TextView) convertView.findViewById(R.id.website);
		viewHolder.address = (TextView) convertView.findViewById(R.id.address);

		if (position < companies.size()) {
			final Company company = companies.get(position);
			String webSite = company.website;
			viewHolder.image.setImageResource(R.drawable.logo_company_default);
			CommonUtil.loadImage(company.logoPath, viewHolder.image);
			viewHolder.name.setText(company.name);
			viewHolder.website.setText(webSite.replace("www.", ""));
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
						mApiHttp.followCompany(company.orgID, new Listener<JSONObject>() {
							
							@Override
							public void onResponse(JSONObject jsonObject) {
								
								APIParser parser = new APIParser(jsonObject);
								if (parser.isOK()) {
									company.followed = true;
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
		} else {
			Person person = persons.get(position - companies.size());
			viewHolder.image.setImageResource(R.drawable.logo_person_default);
			CommonUtil.loadImage(person.photoPath, viewHolder.image);
			viewHolder.button.setVisibility(View.GONE);
			viewHolder.name.setText(person.name);
			viewHolder.website.setText(person.orgTitle);
		}
		return convertView;
	}
	
	public final class ViewHolder {
		
		public ImageView image;
		public TextView name;
		public TextView website;
		public TextView address;
		public ImageView button;
	}

	@Override
	public long getHeaderId(int arg0) {
		return arg0;
	}

	@SuppressLint("NewApi")
	@Override
	public View getHeaderView(int arg0, View convertView, ViewGroup parent) {
		if (arg0 == 0) {
			return LayoutInflater.from(mContext).inflate(R.layout.section_company_search, null);
		} else if (arg0 == companies.size()){
			return LayoutInflater.from(mContext).inflate(R.layout.section_person_search, null);
		} else {
			return LayoutInflater.from(mContext).inflate(R.layout.empty, null);
		}
	}
}
