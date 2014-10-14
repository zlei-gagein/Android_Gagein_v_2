package com.gagein.ui.settings;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.gagein.R;
import com.gagein.adapter.SettingsFilterAdapter;
import com.gagein.http.APIHttpMetadata;
import com.gagein.http.APIParser;
import com.gagein.model.Agent;
import com.gagein.model.DataPage;
import com.gagein.ui.main.BaseActivity;
import com.gagein.util.Constant;
import com.gagein.util.Log;

public class SettingsNewsFilterActivity extends BaseActivity implements OnItemClickListener{
	
	private List<Agent> agents;
	private DataPage agentsPage;
	private SettingsFilterAdapter adapter;
	private ListView listView;
	private TextView selectTriggers;
	private Button newsRelevance;
	private int relevance = 0;
	private int requestCode = 1;
	private Boolean moreNews;
	private Timer timer;
	private TimerTask timerTask;
	private int selectionChanged = 0;			// flag if the selection has ever been changed

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_news_filter);
		
		doInit();
		getFiltersData();
	}
	
	@Override
	protected void initView() {
		super.initView();
		
		setTitle(R.string.triggers);
		setLeftImageButton(R.drawable.back_arrow);
		
		listView = (ListView) findViewById(R.id.listView);
		selectTriggers = (TextView) findViewById(R.id.selectTriggers);
		newsRelevance = (Button) findViewById(R.id.newsRelevance);
	}
	
	@Override
	protected void initData() {
		super.initData();
		getRelevance(false);
	}
	
	@Override
	protected void setData() {
		super.setData();
		adapter = new SettingsFilterAdapter(mContext, agents);
		listView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		adapter.notifyDataSetInvalidated();
	}
	
	@Override
	protected void setOnClickListener() {
		super.setOnClickListener();
		listView.setOnItemClickListener(this);
		newsRelevance.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		
		if (v == leftImageBtn) {
			
			saveAgents();
			
		} else if (v == newsRelevance) {
			
			if (relevance == 0) {
				getRelevance(true);
			} else {
				Intent intent = new Intent();
				intent.setClass(mContext, NewsRelevanceActivity.class);
				intent.putExtra(Constant.MORENEWS, moreNews);
				startActivityForResult(intent, requestCode);
			}
			
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == this.requestCode) {
			moreNews = data.getBooleanExtra(Constant.MORENEWS, false);
			setRelevanceButton(moreNews);
		}
	}
	
	private void setRelevanceButton(Boolean moreNews) {
		newsRelevance.setText(moreNews ? R.string.more_news : R.string.more_relevant);
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
					
					JSONObject jObject = jsonObject.optJSONObject("data");
					int chartEnabled = jObject.optInt("chart_enabled");
					selectTriggers.setText((chartEnabled == 0) ? R.string.select_triggers_linking : R.string.select_triggers);
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
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		 
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            //do something...
        	saveAgents();
         }
         return super.onKeyDown(keyCode, event);
	}
	
	private void getRelevance(Boolean showDialog) {
		if (showDialog) showLoadingDialog();
		mApiHttp.getFilterRelevance(new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject jsonObject) {
				
				APIParser parser = new APIParser(jsonObject);
				if (parser.isOK()) {
					relevance = parser.data().optInt("relevance_value");
					moreNews = (APIHttpMetadata.kGGCompanyUpdateRelevanceNormal == relevance) ? true : false;
					setRelevanceButton(moreNews);
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

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
		
		if (null != timer) timer.cancel();
		selectionChanged = 1;
		
		Agent agent = (Agent)adapter.getItem(position);
		if (position == 0) {
			if (agent.checked) {
				for (int i = 0 ; i < agents.size(); i ++) {
					Agent agent2 = (Agent)adapter.getItem(i);
					agent2.checked = false;
				}
			} else {
				for (int i = 0 ; i < agents.size(); i ++) {
					Agent agent2 = (Agent)adapter.getItem(i);
					agent2.checked = true;
				}
			}
		} else {
			if (agent.checked) {
				Agent agent3 = (Agent)adapter.getItem(0);
				agent3.checked = false;
				agent.checked = !agent.checked;
			} else {
				agent.checked = !agent.checked;
				Boolean allChecked = true;
				for (int i = 0; i < agents.size(); i ++) {
					if (i == 0) continue;
					if (!((Agent)adapter.getItem(i)).checked) {
						allChecked = false;
					}
				}
				if (allChecked) {
					((Agent)adapter.getItem(0)).checked = true;
				}
			}
		}
		adapter.notifyDataSetChanged();
		
	}

	private void saveAgents() {
		
		if (selectionChanged == 0) {
			finish();
			return;
		}
		
		List<String> selectedAgentIDs = new ArrayList<String>();
		for (Agent agent : adapter.getAgents()) {
			if (agent.checked) {
				if (!agent.agentID.equalsIgnoreCase("0")) {
					selectedAgentIDs.add(agent.agentID);
				}
			}
		}
		showLoadingDialog();
		mApiHttp.saveAgents(selectedAgentIDs, new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject jsonObject) {
				
				APIParser parser = new APIParser(jsonObject);
				if (parser.isOK()) {
					Log.v("silen", "parser.isOK()");
					
					showShortToast("Saved");
					
					timer = new Timer();
					timerTask = new TimerTask() {
						@Override
						public void run() {
							runOnUiThread(new Runnable() {
								
								@Override
								public void run() {
									finish();
								}
							});
						}
					};
					timer.schedule(timerTask, 1000);
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
	
	
}
