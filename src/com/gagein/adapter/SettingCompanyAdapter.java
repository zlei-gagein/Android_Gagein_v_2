package com.gagein.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gagein.R;
import com.gagein.model.Company;
import com.gagein.util.CommonUtil;

public class SettingCompanyAdapter extends BaseAdapter {
	
	private Context mContext;
	private List<Company> competitors;
	public Boolean edit = false;
	
	public SettingCompanyAdapter(Context mContext, List<Company> competitors) {
		super();
		this.mContext = mContext;
		this.competitors = competitors;
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
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_setting_company, null);
			viewHolder.image = (ImageView) convertView.findViewById(R.id.image);
			viewHolder.name = (TextView) convertView.findViewById(R.id.name);
			viewHolder.website = (TextView) convertView.findViewById(R.id.website);
			viewHolder.address = (TextView) convertView.findViewById(R.id.address);
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
		
		return convertView;
	}
	
	public final class ViewHolder {
		
		public ImageView image;
		public TextView name;
		public TextView website;
		public TextView address;
	}
}
