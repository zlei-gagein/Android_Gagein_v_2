package com.gagein.adapter;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gagein.R;
import com.gagein.model.Group;

public class AddToGroupAdapter extends BaseAdapter {
	
	private Context mContext;
	private List<Group> groups;
	private String groupId;
	
	public AddToGroupAdapter(Context mContext, List<Group> groups, String groupId) {
		super();
		this.mContext = mContext;
		this.groups = groups;
		this.groupId = groupId;
	}

	@Override
	public int getCount() {
		return groups.size();
	}

	@Override
	public Object getItem(int position) {
		return groups.get(position);
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
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_add_to_group, null);
			viewHolder.name = (TextView) convertView.findViewById(R.id.name);
			viewHolder.count = (TextView) convertView.findViewById(R.id.count);
			viewHolder.aboveLayout = (RelativeLayout) convertView.findViewById(R.id.aboveLayout);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		final Group group = groups.get(position);
		
		viewHolder.name.setText(group.getName());
		viewHolder.count.setText(group.getCount() + "");
		
		viewHolder.aboveLayout.setVisibility(group.getMogid().equalsIgnoreCase(groupId) || 
				TextUtils.isEmpty(group.getMogid()) || Long.parseLong(group.getMogid()) < 0 ? View.VISIBLE : View.GONE);
		
		return convertView;
	}
	
	public final class ViewHolder {
		
		public TextView name;
		public TextView count;
		public RelativeLayout aboveLayout;
		
	}

}
