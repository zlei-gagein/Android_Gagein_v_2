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
import com.gagein.model.Company;

public class PendingCompaniesAdapter extends BaseAdapter {
	
	private Context mContext;
	private List<Company> pendingCompanies = new ArrayList<Company>();
	public Boolean edit = false;
	
	public PendingCompaniesAdapter(Context mContext, List<Company> pendingCompanies) {
		super();
		this.mContext = mContext;
		this.pendingCompanies = pendingCompanies;
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
			viewHolder.button = (ImageView) convertView.findViewById(R.id.button);
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
		viewHolder.image.setImageResource(company.orgID > 0 ? R.drawable.logo_company_default : R.drawable.pending_img);
		viewHolder.button.setVisibility(company.orgID > 0 ? View.VISIBLE : View.GONE);
		viewHolder.button.setImageResource(company.orgID > 0 ? R.drawable.follow : R.drawable.add);
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
		public ImageView button;
		public TextView name;
		public TextView website;
	}

}
