package com.gagein.adapter.search;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.gagein.R;
import com.gagein.http.APIHttp;
import com.gagein.http.APIParser;
import com.gagein.model.SavedSearch;
import com.gagein.util.CommonUtil;
import com.gagein.util.MessageCode;

public class SearchSavedAdapter extends BaseAdapter {
	
	private Context mContext;
	private List<SavedSearch> savedSearchs = new ArrayList<SavedSearch>();
	private APIHttp mApiHttp;
	public Boolean edit = false;
	
	public SearchSavedAdapter(Context mContext, List<SavedSearch> savedSearchs) {
		super();
		this.mContext = mContext;
		this.savedSearchs = savedSearchs;
		mApiHttp = new APIHttp(mContext);
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_saved_search, null);
			viewHolder.delete = (TextView) convertView.findViewById(R.id.delete);
			viewHolder.imageview = (ImageView) convertView.findViewById(R.id.imageview);
			viewHolder.name = (TextView) convertView.findViewById(R.id.name);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		if (edit) {
			viewHolder.delete.setVisibility(View.VISIBLE);
		} else {
			viewHolder.delete.setVisibility(View.GONE);
		}
		
		final SavedSearch savedSearch = savedSearchs.get(position);
		viewHolder.name.setText(savedSearch.getName());
		if (savedSearch.getType().equalsIgnoreCase("buz")) {
			viewHolder.imageview.setImageResource(R.drawable.saved_com_list);
		} else {
			viewHolder.imageview.setImageResource(R.drawable.saved_peo_list);
		}
		
		viewHolder.delete.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				CommonUtil.showLoadingDialog(mContext);
				mApiHttp.deleteSavedSearch(savedSearch.getId(), new Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject jsonObject) {

						APIParser parser = new APIParser(jsonObject);
						if (parser.isOK()) {
							CommonUtil.showShortToast(mContext.getResources().getString(R.string.deleted));
							savedSearchs.remove(position);
							SearchSavedAdapter.this.notifyDataSetChanged();
						} else {
							String msg = MessageCode.messageForCode(parser.messageCode());
							if (msg != null && msg.length() > 0) {
								CommonUtil.showDialog(msg);
							}
						}
						CommonUtil.dissmissLoadingDialog();
					}

				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						CommonUtil.dissmissLoadingDialog();
						CommonUtil.showLongToast(mContext.getResources().getString(R.string.connection_error));
					}
				});
			}
		});
		
		return convertView;
	}
	
	public final class ViewHolder {
		
		public TextView delete;
		public ImageView imageview;
		public TextView name;
	}

}
