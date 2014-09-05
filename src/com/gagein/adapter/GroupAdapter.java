package com.gagein.adapter;

import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.gagein.R;
import com.gagein.component.dialog.DeleteGroupDialog;
import com.gagein.component.dialog.RenameGroupDialog;
import com.gagein.http.APIHttp;
import com.gagein.http.APIParser;
import com.gagein.model.Group;
import com.gagein.ui.companies.CompaniesActivity;
import com.gagein.util.CommonUtil;
import com.gagein.util.Constant;

public class GroupAdapter extends BaseAdapter {
	
	private Context mContext;
	private Boolean isEdit = false;
	private List<Group> groups;
	private int screenWidth;
	private APIHttp mApiHttp;
	private GroupAdapter adapter;
	
	public GroupAdapter(Context mContext, List<Group> groups) {
		super();
		this.mContext = mContext;
		this.groups = groups;
		mApiHttp = new APIHttp(mContext);
		screenWidth = mContext.getResources().getDisplayMetrics().widthPixels;
		adapter = this;
	}
	
	public void setEdit(Boolean edit) {
		isEdit = edit;
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
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_group, null);
			viewHolder.deleteImg = (ImageView) convertView.findViewById(R.id.deleteImg);
			viewHolder.chevron = (ImageView) convertView.findViewById(R.id.chevron);
			viewHolder.leftLayout = (RelativeLayout) convertView.findViewById(R.id.leftLayout);
			viewHolder.middleLayout = (RelativeLayout) convertView.findViewById(R.id.middleLayout);
			viewHolder.renameBtn = (TextView) convertView.findViewById(R.id.renameBtn);
			viewHolder.deleteBtn = (TextView) convertView.findViewById(R.id.deleteBtn);
			viewHolder.name = (TextView) convertView.findViewById(R.id.name);
			viewHolder.count = (TextView) convertView.findViewById(R.id.count);
			viewHolder.aboveLayout = (RelativeLayout) convertView.findViewById(R.id.aboveLayout);
			viewHolder.scrollview = (HorizontalScrollView) convertView.findViewById(R.id.scrollview);
			viewHolder.leftLayout.getLayoutParams().width = screenWidth;
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		final Group group = groups.get(position);
		
		viewHolder.name.setText(group.getName());
		viewHolder.count.setText(group.getCount() + "");
		
		viewHolder.deleteImg.setVisibility(isEdit && !group.getIsSsystem() ? View.VISIBLE : View.GONE);
		viewHolder.aboveLayout.setVisibility(isEdit && group.getIsSsystem() ? View.VISIBLE : View.GONE);
		
		viewHolder.leftLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				if (isEdit) return;
				
//				if (!TextUtils.isEmpty(group.getMogid())) {
//					if (Long.parseLong(group.getMogid()) < 0 && group.getCount() <= 0) return;
//				}
				Intent intent = new Intent();
				intent.setClass(mContext, CompaniesActivity.class);
				intent.putExtra(Constant.GROUP, group);
				mContext.startActivity(intent);
			}
		});
		
		if (!isEdit || group.getIsSsystem()) viewHolder.scrollview.scrollTo(0, 0);
		viewHolder.scrollview.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View view, MotionEvent event) {
				
				if  (group.getIsSsystem()) {
					return true;
				} else {
					return isEdit ?  false : true;
				}
			}
		});
		
		final HorizontalScrollView scrollView = viewHolder.scrollview;
        viewHolder.deleteImg.setOnClickListener(new OnClickListener() {
        	
			@Override
			public void onClick(View arg0) {
				scrollView.scrollTo(screenWidth, 0);
			}
        	
        });
        
        viewHolder.renameBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				scrollView.scrollTo(0, 0);
				RenameGroupDialog dialog = new RenameGroupDialog(mContext);
				dialog.showDialog(group.getMogid(), group, adapter);
				
			}
		});
        
        viewHolder.deleteBtn.setOnClickListener(new OnClickListener() {
        	
        	@Override
        	public void onClick(View arg0) {
        		
        		scrollView.scrollTo(0, 0);
        		
        		final DeleteGroupDialog dialog = new DeleteGroupDialog(mContext);
        		dialog.showDialog(group.getName(), new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						dialog.dismissDialog();
						CommonUtil.showLoadingDialog(mContext);
						
						mApiHttp.deleteCompanyGroup(group.getMogid() , new Listener<JSONObject>() {

							@Override
							public void onResponse(JSONObject jsonObject) {
								
								APIParser parser = new APIParser(jsonObject);
								if (parser.isOK()) {
									CommonUtil.showShortToast(R.string.delete_success);
									groups.remove(group);
									notifyDataSetChanged();
								} else {
									CommonUtil.alertMessageForParser(parser);
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
        	}
        });
		return convertView;
	}
	
	public final class ViewHolder {
		
		public ImageView deleteImg;
		public ImageView chevron;
//		public LinearLayout modifyLayout;
		public HorizontalScrollView scrollview;
		public RelativeLayout leftLayout;
		public RelativeLayout middleLayout;
		public TextView renameBtn;
		public TextView deleteBtn;
		public TextView name;
		public TextView count;
		public RelativeLayout aboveLayout;
		
	}

}
