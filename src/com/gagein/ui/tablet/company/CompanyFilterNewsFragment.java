package com.gagein.ui.tablet.company;

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
import com.gagein.adapter.FilterNewsAdapter;
import com.gagein.http.APIHttp;
import com.gagein.http.APIParser;
import com.gagein.model.Agent;
import com.gagein.model.DataPage;
import com.gagein.ui.BaseFragment;
import com.gagein.util.Constant;

public class CompanyFilterNewsFragment extends BaseFragment implements OnItemClickListener{
	
	private FilterNewsAdapter adapter;
	private List<Agent> agents = new ArrayList<Agent>();
	private DataPage agentsPage;
	private ListView listView;
	private OnFilterNewsLeftBtnClickListener filterNewsLeftBtnListener;
	private OnRefreshNewsFilterFromNewsListener refreshNewsFilterFromNewsListener;
	
	
	public interface OnFilterNewsLeftBtnClickListener {
		public void onFilterNewsLeftbtnClickListener();
	}
	
	public interface OnRefreshNewsFilterFromNewsListener {
		public void onRefreshNewsFilterFromNewsListener();
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			filterNewsLeftBtnListener = (OnFilterNewsLeftBtnClickListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + "must implement OnFilterNewsLeftBtnClickListener");
		}
		try {
			refreshNewsFilterFromNewsListener = (OnRefreshNewsFilterFromNewsListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + "must implement OnRefreshNewsFilterFromNewsListener");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.activity_filter_news, container, false);
		mContext = getActivity();
		mApiHttp = new APIHttp(mContext);
		doInit();
		return view;
	}
	
	@Override
	protected void initView() {
		super.initView();
		
		setTitle(R.string.filters);
		setLeftImageButton(R.drawable.back_arrow);
		
		listView = (ListView) view.findViewById(R.id.listView);
	}
	
	@Override
	protected void initData() {
		super.initData();
		
		getFiltersData();
	}
	
	@Override
	protected void setOnClickListener() {
		super.setOnClickListener();
		
		listView.setOnItemClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		
		if (v == leftImageBtn) {
			back();
		}
		
	}
	
	private void back() {
		filterNewsLeftBtnListener.onFilterNewsLeftbtnClickListener();
	}
	
	@Override
	protected void setData() {
		super.setData();
		adapter = new FilterNewsAdapter(mContext, agents);
		listView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		adapter.notifyDataSetInvalidated();
	}
	
	private void getFiltersData() {
		
		showLoadingDialog(mContext);
		mApiHttp.getAgentFilters(new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject jsonObject) {
				
				agents = new ArrayList<Agent>();
				
				APIParser parser = new APIParser(jsonObject);
				if (parser.isOK()) {

					agentsPage = parser.parseGetAgentFiltersList();
					List<Object> items = agentsPage.items;
					if (items != null) {
						
						Agent allTriggers = new Agent();
						
						allTriggers.agentID = "-10";
						allTriggers.name = "All Triggers";
						agents.add(allTriggers);
						
						for (Object obj : items) {
							Agent agent = (Agent)obj;
							if (agent.checked) {
								agents.add(agent);
							}
						}
						Boolean isAllChecked = true;
						for (int i = 0; i < agents.size(); i ++) {
							if (i == 0) continue;
							if (!agents.get(i).checked) isAllChecked = false;
						}
						agents.get(0).checked = isAllChecked ? true : false; 
						
						Agent allNews = new Agent();
						
						allNews.agentID = "0";
						allNews.name = "All News";
						agents.add(allNews);
						
						for (int j = 0; j < agents.size(); j ++) {
							agents.get(j).checked = false;
						}
						
						Boolean haveSelected = false;
						for (int i = 0; i < Constant.locationNewsTriggersForCompany.size(); i ++) {
							
							Agent locationAgent = Constant.locationNewsTriggersForCompany.get(i);
							
							for (int j = 0; j < agents.size(); j ++) {
								
								if (locationAgent.agentID.equalsIgnoreCase(agents.get(j).agentID)) {
									agents.get(j).checked = true;
								}
							}
							
							if (locationAgent.checked) haveSelected = true;
								
						}
						
						if (!haveSelected) {
							for (int i = 0; i < agents.size(); i ++) {
								if (agents.get(i).agentID.equalsIgnoreCase("-10")) {
									agents.get(i).checked = true;
								}
							}
						}
						
					}
					
					if (agents.size() > 0) setData();
					
				} else {
					alertMessageForParser(parser);
				}
				
				dismissLoadingDialog();
			}
			
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				showConnectionError(mContext);
			}
		});
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		
		Agent agent = (Agent)adapter.getItem(position);
		Boolean checked = agent.checked;
		
		if (position == 0) {
			
			if (checked) {
				return;
			} else {
				
				for (int i = 0; i < agents.size(); i ++) {
					agents.get(i).checked = (i == 0) ? true : false;
				}
				
			}
			
		} else if (position == agents.size() - 1) {
			
			if (checked) {
				return;
			} else {
				
				for (int i = 0; i < agents.size(); i ++) {
					agents.get(i).checked = (i == agents.size() - 1) ? true : false;
				}
				
			}
			
		} else {
			
			if (checked) {
				
				agent.checked = !agent.checked;
				
				Boolean haveSelected = false;
				for (int i = 0; i < agents.size(); i ++) {
					
					if (agents.get(i).checked) {
						haveSelected = true;
					}
					
				}
				
				if (!haveSelected) {
					agents.get(0).checked = true;
				}
				
			} else {
				
				agents.get(0).checked = false;
				agents.get(agents.size() - 1).checked = false;
				agent.checked = !agent.checked;
				
			}
			
		}
			
		adapter.notifyDataSetChanged();
		
		saveAgentToLocation();
		
	}
	
	private void saveAgentToLocation() {
		
		Constant.locationNewsTriggersForCompany.clear();
		
		for (int i = 0; i < agents.size(); i ++) {
			if (agents.get(i).checked) {
				Constant.locationNewsTriggersForCompany.add(agents.get(i));
			}
		}
		
		refreshNewsFilterFromNewsListener.onRefreshNewsFilterFromNewsListener();
		
	}
	
}
