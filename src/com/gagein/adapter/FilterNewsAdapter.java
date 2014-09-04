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
import com.gagein.model.Agent;

public class FilterNewsAdapter extends BaseAdapter {
	
	private Context mContext;
	private List<Agent> agents = new ArrayList<Agent>();
	
	public FilterNewsAdapter(Context mContext, List<Agent> agents) {
		super();
		this.mContext = mContext;
		this.agents = agents;
	}

	@Override
	public int getCount() {
		return agents.size();
	}

	@Override
	public Object getItem(int position) {
		return agents.get(position);
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
		
		final Agent agent = agents.get(position);
		viewHolder.name.setText(agent.name);
		viewHolder.button.setBackgroundResource(agent.checked ? R.drawable.button_select : R.drawable.button_unselect);
//		if (agent.checked) {
//		} else {
//			viewHolder.button.setBackgroundResource(R.drawable.button_unselect);
//		}
		return convertView;
	}
	
	public final class ViewHolder {
		
		public TextView name;
		public ImageView button;
		
	}

}
