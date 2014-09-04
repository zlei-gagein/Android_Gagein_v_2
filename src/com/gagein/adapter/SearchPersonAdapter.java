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
import com.gagein.model.Person;
import com.gagein.util.CommonUtil;

public class SearchPersonAdapter extends BaseAdapter {
	
	private Context mContext;
	private List<Person> persons;
	public Boolean edit = false;
	
	public SearchPersonAdapter(Context mContext, List<Person> persons) {
		super();
		this.mContext = mContext;
		this.persons = persons;
	}

	@Override
	public int getCount() {
		return persons.size();
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_company_competitor, null);
			viewHolder.image = (ImageView) convertView.findViewById(R.id.image);
			viewHolder.name = (TextView) convertView.findViewById(R.id.name);
			viewHolder.button = (ImageView) convertView.findViewById(R.id.button);
			viewHolder.website = (TextView) convertView.findViewById(R.id.website);
			viewHolder.address = (TextView) convertView.findViewById(R.id.address);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		Person person = persons.get(position);
		viewHolder.image.setImageResource(R.drawable.logo_person_default);
		CommonUtil.loadImage(person.photoPath, viewHolder.image);
		viewHolder.button.setVisibility(View.GONE);
		viewHolder.name.setText(person.name);
		viewHolder.website.setText(person.orgTitle);
		return convertView;
	}
	
	public final class ViewHolder {
		
		public ImageView image;
		public TextView name;
		public TextView website;
		public TextView address;
		public ImageView button;
	}

}
