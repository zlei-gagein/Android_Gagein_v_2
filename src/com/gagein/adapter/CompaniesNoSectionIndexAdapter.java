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

public class CompaniesNoSectionIndexAdapter extends BaseAdapter {
	
	private Context mContext;
	private List<Company> companies;
	private Boolean edit = false;
	
	public CompaniesNoSectionIndexAdapter(Context mContext, List<Company> companies) {
		super();
		this.mContext = mContext;
		this.companies = companies;
	}
	
	public void setEdit(Boolean edit) {
		this.edit = edit;
	}

	@Override
	public int getCount() {
		return companies.size();
	}

	@Override
	public Object getItem(int position) {
		return companies.get(position);
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
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_companies, null);
			viewHolder.image = (ImageView) convertView.findViewById(R.id.image);
			viewHolder.checkBtn = (ImageView) convertView.findViewById(R.id.checkBtn);
			viewHolder.name = (TextView) convertView.findViewById(R.id.name);
			viewHolder.website = (TextView) convertView.findViewById(R.id.website);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		viewHolder.checkBtn.setVisibility(edit ? View.VISIBLE : View.GONE);
		final Company company = companies.get(position);
		CommonUtil.loadImage(company.orgLargerLogoPath, viewHolder.image);
		viewHolder.name.setText(company.name);
		final ImageView checkBtn = viewHolder.checkBtn;
		checkBtn.setImageResource(company.select ? R.drawable.button_select : R.drawable.button_unselect);
		//TODO
//		viewHolder.checkBtn.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View arg0) {
//				company.select = !company.select;
//				checkBtn.setImageResource(company.select ? R.drawable.button_select : R.drawable.button_unselect);
//				if (CommonUtil.isTablet(mContext)) {//TODO
////					((CompaniesFragment)).setBottomButton(companies);
//				} else {
//					((CompaniesActivity)mContext).setBottomButton(companies);
//				}
//			}
//		});
		viewHolder.website.setText(CommonUtil.removeWebsite3W(company.website) + "  " +  company.address());
		
		return convertView;
	}
	
	public final class ViewHolder {
		
		public ImageView image;
		public ImageView checkBtn;
		public TextView name;
		public TextView website;
		
	}

}
