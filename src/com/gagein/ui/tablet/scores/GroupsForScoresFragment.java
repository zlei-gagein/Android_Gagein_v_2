package com.gagein.ui.tablet.scores;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.gagein.R;
import com.gagein.adapter.FilterGroupsAdapter;
import com.gagein.http.APIParser;
import com.gagein.model.DataPage;
import com.gagein.model.Group;
import com.gagein.ui.BaseFragment;
import com.gagein.util.Constant;

public class GroupsForScoresFragment extends BaseFragment implements OnItemClickListener{
	
	private ListView listView;
	private List<Group> groups = new ArrayList<Group>();
	private FilterGroupsAdapter adapter;
	private OnFinishGroupsListener onFinishGroupsListener;
	private OnChangeGroupsFilterListener onChangeGroupsFilterListener;
	
	public interface OnFinishGroupsListener {
		public void onFinishGroupsListener();
	}
	
	public interface OnChangeGroupsFilterListener {
		public void onChangeGroupsFilterListener();
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			onFinishGroupsListener = (OnFinishGroupsListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + "must implement OnFinishGroupsListener");
		}
		try {
			onChangeGroupsFilterListener = (OnChangeGroupsFilterListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + "must implement OnChangeGroupsFilterListener");
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.activity_companygroups, container, false);
		
		doInit();
		return view;
	}
	
	@Override
	protected void initView() {
		super.initView();
		
		setTitle(R.string.u_groups);
		setRightButton(R.string.done);
		
		listView = (ListView) view.findViewById(R.id.listView);
	}
	
	@Override
	protected void initData() {
		super.initData();
		
		getAllCompanyGroups(true);
	}
	
	@Override
	protected void setData() {
		super.setData();
		
		adapter = new FilterGroupsAdapter(mContext, groups);
		listView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		
	}
	
	public void getAllCompanyGroups(Boolean showDialog) {
		
		mApiHttp.getAllCompanyGroups(new Listener<JSONObject>() {
			
			@Override
			public void onResponse(JSONObject jsonObject) {
				dismissLoadingDialog();
				
				APIParser parser = new APIParser(jsonObject);
				
				if (parser.isOK()) {
					groups.clear();
					DataPage dataPage = parser.parseGetAllCompanyGroups();
					List<Object> items = dataPage.items;
					if (items != null) {
						for (Object obj : items) {
							
							Group group = (Group) obj;
							int groupCount = group.getCount();
							if (groupCount > 0) {
								groups.add(group);
							}
							
							//如果当地没有保存group filter,设置followed companies 为默认
							if (Constant.GroupsForScores.size() == 0) {
								for (int i = 0; i < groups.size(); i++) {
									if (groups.get(i).getMogid().equalsIgnoreCase("-10")) {
										groups.get(i).selected = true;
									}
								}
							} else {
								
								for (int i = 0; i < Constant.GroupsForScores.size() ; i++) {
									
									Group selectedGroup = Constant.GroupsForScores.get(i);
									
									for (int j = 0; j < groups.size(); j++) {
										
										if (selectedGroup.getMogid().equalsIgnoreCase(groups.get(j).getMogid())) {
											groups.get(j).selected = true;
										}
										
									}
								}
							}
							
						}
						
						setData();
					}
				} else {
				}
			}
			
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
			}
		});

	};
	
	@Override
	protected void setOnClickListener() {
		super.setOnClickListener();
		
		leftBtn.setOnClickListener(this);
		rightBtn.setOnClickListener(this);
		listView.setOnItemClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		
		if (v == rightBtn) {
			
			onFinishGroupsListener.onFinishGroupsListener();
			
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		
		Boolean checked = groups.get(position).selected;
		
		if (position == 0) {
			Boolean haveChecked = false;
			for (int i = 0; i < groups.size(); i ++) {
				if (i == 0) continue;
				if (groups.get(i).selected) {
					haveChecked = true;
					break;
				}
			}
			if (!haveChecked) {
				return;
			} else {
				groups.get(position).selected = true;
				for (int i = 0; i < groups.size(); i ++) {
					if (i != 0) groups.get(i).selected = false;
				}
			}
		} else {
			groups.get(position).selected = (!checked);
			Boolean haveChecked = false;
			for (int i = 0; i < groups.size(); i ++) {
				if (i == 0) {
					continue;
				} else {
					if (groups.get(i).selected) {
						haveChecked = true;
						groups.get(0).selected = false;
					}
				}
			}
			if (!haveChecked) {
				groups.get(0).selected = true;
			}
		}
		adapter.notifyDataSetChanged();
		
		Constant.GroupsForScores.clear();
		for (int i = 0; i < groups.size(); i++) {
			if (groups.get(i).selected) {
				Constant.GroupsForScores.add(groups.get(i));
			}
		}
		
		onChangeGroupsFilterListener.onChangeGroupsFilterListener();
		
	}
}
