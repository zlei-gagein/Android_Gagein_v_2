package com.gagein.ui.newsfilter;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.gagein.R;
import com.gagein.adapter.FilterNewsAdapter;
import com.gagein.http.APIParser;
import com.gagein.model.Agent;
import com.gagein.model.DataPage;
import com.gagein.ui.main.BaseActivity;
import com.gagein.util.Log;

public class FilterNewsActivity extends BaseActivity implements OnItemClickListener{
	
	private FilterNewsAdapter adapter;
	private List<Agent> agents;
	private DataPage agentsPage;
	private ListView listView;
	private int selectionChanged = 0;			// flag if the selection has ever been changed
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_filter_news);
		
		doInit();
	}
	
	@Override
	protected void initView() {
		super.initView();
		
		setTitle(R.string.news_triggers);
		setRightButton(R.string.done);
		setLeftImageButton(R.drawable.back_arrow);
		
		listView = (ListView) findViewById(R.id.listView);
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
		if (v == rightBtn) {
			if (selectionChanged != 0) {
				if (null != agents) {
					Boolean haveSelected = false;
					for (int i = 0; i < agents.size(); i ++) {
						if (agents.get(i).checked) {
							haveSelected = true;
							break;
						}
					}
					if (!haveSelected) {
						showShortToast(R.string.select_least_one_trigger);
						return;
					}
				}
				saveFilter();
			} else {
				finish();
			}
		} else if (v == leftImageBtn) {
			finish();
		}
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
		showLoadingDialog();
		mApiHttp.getAgentFilters(new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject jsonObject) {
				
				agents = new ArrayList<Agent>();
				
				APIParser parser = new APIParser(jsonObject);
				if (parser.isOK()) {

					agentsPage = parser.parseGetAgentFiltersList();
					List<Object> items = agentsPage.items;
					if (items != null) {
						Agent agent = new Agent();
						//TODO
						agent.agentID = "0";
						agent.name = "All";
						agent.checked = false;
						agents.add(agent);
						for (Object obj : items) {
							agents.add((Agent)obj);
						}
						Boolean isAllChecked = true;
						for (int i = 0; i < agents.size(); i ++) {
							if (i == 0) continue;
							if (!agents.get(i).checked) isAllChecked = false;
						}
						agents.get(0).checked = isAllChecked ? true : false; 
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
				showConnectionError();
			}
		});
	}
	
	private void saveFilter() {
		showLoadingDialog();
		List<String> selectedAgentIDs = new ArrayList<String>();
		for (Agent agent : agents) {
			if (agent.checked) {
				if (!agent.agentID.equalsIgnoreCase("0")) selectedAgentIDs.add(agent.agentID);
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
				finish();
			}
			
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				showConnectionError();
			}
		});
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		selectionChanged = 1;
		Agent agent = (Agent)adapter.getItem(position);
		if (position == 0) {
			if (agent.checked) {
				for (int i = 0; i < agents.size(); i ++) {
					Agent mAgent = (Agent)adapter.getItem(i);
					mAgent.checked = false;
				}
			} else {
				for (int i = 0; i < agents.size(); i ++) {
					Agent mAgent = (Agent)adapter.getItem(i);
					mAgent.checked = true;
				}
			}
		} else {
			agent.checked = !agent.checked;
			//TODO
//			Boolean haveNoSelected = false;
//			for (int i = 0; i < agents.size(); i ++) {
//				Agent mAgent = (Agent)adapter.getItem(i);
//				if (mAgent.checked = false) haveNoSelected = true;
//			}
//			if (haveNoSelected) {
//				Agent mAgent = (Agent)adapter.getItem(0);
//				mAgent.checked = false;
//			} else {
//				Agent mAgent = (Agent)adapter.getItem(0);
//				mAgent.checked = true;
//			}
		}
		adapter.notifyDataSetChanged();
	}

}
