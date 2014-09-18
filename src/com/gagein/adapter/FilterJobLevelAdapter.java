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
import com.gagein.model.FacetItem;

public class FilterJobLevelAdapter extends BaseAdapter {
	
	private Context mContext;
	private List<FacetItem> jobLevels;
	
	public FilterJobLevelAdapter(Context mContext, List<FacetItem> jobLevels) {
		super();
		this.mContext = mContext;
		this.jobLevels = jobLevels;
	}

	@Override
	public int getCount() {
		return jobLevels.size();
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
		
		viewHolder.name.setText(jobLevels.get(position).name);
		if (jobLevels.get(position).selected) {
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
