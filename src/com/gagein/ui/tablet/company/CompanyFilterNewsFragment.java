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
import com.gagein.util.Log;

public class CompanyFilterNewsFragment extends BaseFragment implements OnItemClickListener{
	
	private FilterNewsAdapter adapter;
	private List<Agent> agents = new ArrayList<Agent>();
	private DataPage agentsPage;
	private ListView listView;
	private int selectionChanged = 0;			// flag if the selection has ever been changed
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
		setRightButton(R.string.done);
		
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
		} else if (v == rightBtn) {
			if (selectionChanged != 0) {
				saveFilter();
			} else {
				back();
			}
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
						for (Object obj : items) {
							agents.add((Agent)obj);
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
	
	private void saveFilter() {
		showLoadingDialog(mContext);
		List<String> selectedAgentIDs = new ArrayList<String>();
		for (Agent agent : agents) {
			if (agent.checked) {
				selectedAgentIDs.add(agent.agentID);
			}
		}
		mApiHttp.saveAgents(selectedAgentIDs, new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject jsonObject) {
				
				APIParser parser = new APIParser(jsonObject);
				if (parser.isOK()) {
					Log.v("silen", "parser.isOK()");
				} else {
					alertMessageForParser(parser);
				}
				
				dismissLoadingDialog();
				back();
				refreshNewsFilterFromNewsListener.onRefreshNewsFilterFromNewsListener();
			}
			
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				showConnectionError(mContext);
			}
		});
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Log.v("silen", "arg1 = " + arg1);
		selectionChanged = 1;
		Agent agent = (Agent)adapter.getItem(arg2);
		agent.checked = !agent.checked;
		adapter.notifyDataSetChanged();
	}
}
