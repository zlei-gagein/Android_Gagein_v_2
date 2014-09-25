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
import com.gagein.model.ScoresSort;

public class ScoresSortAdapter extends BaseAdapter {
	
	private Context mContext;
	private List<ScoresSort> scoresSorts = new ArrayList<ScoresSort>();
	private List<Boolean> checkedList = new ArrayList<Boolean>();
	
	public ScoresSortAdapter(Context mContext, List<ScoresSort> scoresSorts, List<Boolean> checkedList) {
		super();
		this.mContext = mContext;
		this.scoresSorts = scoresSorts;
		this.checkedList = checkedList;
	}

	@Override
	public int getCount() {
		return scoresSorts.size();
	}

	@Override
	public Object getItem(int position) {
		return scoresSorts.get(position);
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
		
		final ScoresSort scoresSort = scoresSorts.get(position);
		viewHolder.name.setText(scoresSort.getName());
		viewHolder.button.setBackgroundResource(checkedList.get(position) ? R.drawable.button_select : R.drawable.button_unselect);
		return convertView;
	}
	
	public final class ViewHolder {
		
		public TextView name;
		public ImageView button;
		
	}

}
