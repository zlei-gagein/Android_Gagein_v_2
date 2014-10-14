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
import com.gagein.model.PlanInfo;

public class SwitchPlanAdapter extends BaseAdapter {
	
	private Context mContext;
	private List<PlanInfo> planInfos = new ArrayList<PlanInfo>();
	private List<Boolean> selecteds = new ArrayList<Boolean>();
	
	public SwitchPlanAdapter(Context mContext, List<PlanInfo> planInfos, List<Boolean> selecteds) {
		super();
		this.mContext = mContext;
		this.planInfos = planInfos;
		this.selecteds = selecteds;
	}

	@Override
	public int getCount() {
		return planInfos.size();
	}

	@Override
	public Object getItem(int position) {
		return planInfos.get(position);
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
		
		final PlanInfo planInfo = planInfos.get(position);
		viewHolder.name.setText(planInfo.getTeamName());
		viewHolder.button.setBackgroundResource(selecteds.get(position) ? R.drawable.button_select : R.drawable.button_unselect);
		return convertView;
	}
	
	public final class ViewHolder {
		
		public TextView name;
		public ImageView button;
		
	}

}
