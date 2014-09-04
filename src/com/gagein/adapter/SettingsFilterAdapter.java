package com.gagein.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SeekBar;
import android.widget.TextView;

import com.gagein.R;
import com.gagein.model.Agent;

public class SettingsFilterAdapter extends BaseAdapter {
	
	private Context mContext;
	private List<Agent> agents = new ArrayList<Agent>();
	
	public SettingsFilterAdapter(Context mContext, List<Agent> agents) {
		super();
		this.mContext = mContext;
		this.agents = agents;
	}
	
	public List<Agent> getAgents() {
		return agents;
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
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_news_filter, null);
			viewHolder.select = (TextView) convertView.findViewById(R.id.select);
			viewHolder.name = (TextView) convertView.findViewById(R.id.name);
			viewHolder.progress = (SeekBar) convertView.findViewById(R.id.progress);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		Agent agent = agents.get(position);
		int percentage = (int)(agent.chartPercentage * 100);
		viewHolder.name.setText(agent.name);
		viewHolder.progress.setVisibility((agent.agentID.equalsIgnoreCase("0")) ? View.GONE : View.VISIBLE);
		viewHolder.progress.setProgress(percentage);
		viewHolder.select.setBackgroundResource(agent.checked ? R.drawable.button_select : R.drawable.button_unselect);
		return convertView;
	}
	
	public final class ViewHolder {
		
		public TextView select;
		public TextView name;
		public SeekBar progress;
	}

}
