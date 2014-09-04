package com.gagein.adapter.search;

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
import com.gagein.model.SavedSearch;

public class PeopleFilterCompaniesAdapter extends BaseAdapter {
	
	private Context mContext;
	private List<SavedSearch> savedSearchs = new ArrayList<SavedSearch>();
	
	public PeopleFilterCompaniesAdapter(Context mContext, List<SavedSearch> savedSearchs) {
		super();
		this.mContext = mContext;
		this.savedSearchs = savedSearchs;
	}

	@Override
	public int getCount() {
		return savedSearchs.size();
	}

	@Override
	public Object getItem(int position) {
		return savedSearchs.get(position);
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
		
		SavedSearch savedSearch = savedSearchs.get(position);
		viewHolder.name.setText(savedSearch.getName());
		viewHolder.button.setBackgroundResource(savedSearch.getChecked() ? R.drawable.button_select : R.drawable.button_unselect);
		return convertView;
	}
	
	public final class ViewHolder {
		
		public TextView name;
		public ImageView button;
		
	}

}
