package com.gagein.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gagein.R;
import com.gagein.model.Update;

public class SECFilingsAdapter extends BaseAdapter {
	
	private Context mContext;
	private List<Update> filings = new ArrayList<Update>();
	
	public SECFilingsAdapter(Context mContext, List<Update> filings) {
		super();
		this.mContext = mContext;
		this.filings = filings;
	}

	@Override
	public int getCount() {
		return filings.size();
	}

	@Override
	public Object getItem(int position) {
		return filings.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("SimpleDateFormat")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_secfilings, null);
			viewHolder.newsHead = (TextView) convertView.findViewById(R.id.newsHead);
			viewHolder.date = (TextView) convertView.findViewById(R.id.date);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		Update filing = filings.get(position);
		viewHolder.newsHead.setText(filing.headline);
		SimpleDateFormat formatter = new SimpleDateFormat("dd, yyyy");
		String time = String.format("%tb", filing.date) + " " + formatter.format(filing.date);
		viewHolder.date.setText(time);
		return convertView;
	}
	
	public final class ViewHolder {
		
		public TextView newsHead;
		public TextView date;
	}

}
