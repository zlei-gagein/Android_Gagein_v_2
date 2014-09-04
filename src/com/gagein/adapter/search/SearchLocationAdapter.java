package com.gagein.adapter.search;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gagein.R;
import com.gagein.model.filter.Location;

public class SearchLocationAdapter extends BaseAdapter {
	
	private Context mContext;
	private List<Location> mLocations = new ArrayList<Location>();
	
	public SearchLocationAdapter(Context mContext, List<Location> mLocations) {
		super();
		this.mContext = mContext;
		this.mLocations = mLocations;
	}

	@Override
	public int getCount() {
		return mLocations.size();
	}

	@Override
	public Object getItem(int position) {
		return mLocations.get(position);
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
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_filter_location, null);
			viewHolder.name = (TextView) convertView.findViewById(R.id.name);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		Location location = mLocations.get(position);
		viewHolder.name.setText(location.getLocation());
//		final Agent agent = agents.get(position);
//		viewHolder.name.setText(mFilterItem.getValue());
//		viewHolder.button.setBackgroundResource(mFilterItem.getChecked() ? R.drawable.button_select : R.drawable.button_unselect);
		return convertView;
	}
	
	public final class ViewHolder {
		
		public TextView name;
		
	}

}
