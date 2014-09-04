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
import com.gagein.util.Constant;

public class FilterSortByAdapter extends BaseAdapter {
	
	private Context mContext;
	private String [] nameStr;
	private List<Boolean> checkedList;
	
	public FilterSortByAdapter(Context mContext, List<Boolean> checkedList, int type) {
		super();
		this.mContext = mContext;
		if (type == Constant.COMPANY_PERSON) {
			nameStr = new String[]{mContext.getResources().getString(R.string.job_level), 
					mContext.getResources().getString(R.string.name),
					mContext.getResources().getString(R.string.recent)};
		} else if (type == Constant.COMPANY_COMPETITOR) {
			nameStr = new String[]{mContext.getResources().getString(R.string.employee_size), 
					mContext.getResources().getString(R.string.revenue_size),
					mContext.getResources().getString(R.string.name),
					mContext.getResources().getString(R.string.u_relevance)};
		}
		
		
		this.checkedList = checkedList;
	}

	@Override
	public int getCount() {
		return nameStr.length;
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
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_filter_news, null);
			viewHolder.name = (TextView) convertView.findViewById(R.id.name);
			viewHolder.button = (ImageView) convertView.findViewById(R.id.button);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		viewHolder.name.setText(nameStr[position]);
		if (checkedList.get(position)) {
			viewHolder.button.setBackgroundResource(R.drawable.button_select);
		} else {
			viewHolder.button.setBackgroundResource(R.drawable.button_unselect);
		}
		
		return convertView;
	}
	
	public final class ViewHolder {
		
		public TextView name;
		public ImageView button;
		
	}

}
