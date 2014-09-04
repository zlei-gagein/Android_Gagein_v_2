package com.gagein.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gagein.R;

public class FilterRelevanceAdapter extends BaseAdapter {
	
	private Context mContext;
	private Boolean moreNews;
	
	public FilterRelevanceAdapter(Context mContext, Boolean moreNews) {
		super();
		this.mContext = mContext;
		this.moreNews = moreNews;
		
	}

	@Override
	public int getCount() {
		return 2;
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
		
		if (position == 0) {
			viewHolder.name.setText(mContext.getResources().getString(R.string.more_relevant));
			viewHolder.button.setBackgroundResource(moreNews ? R.drawable.button_unselect : R.drawable.button_select);
		} else if (position == 1){
			viewHolder.name.setText(mContext.getResources().getString(R.string.more_news));
			viewHolder.button.setBackgroundResource(moreNews ? R.drawable.button_select : R.drawable.button_unselect);
		}
		return convertView;
	}
	
	public final class ViewHolder {
		
		public TextView name;
		public ImageView button;
		
	}

}
