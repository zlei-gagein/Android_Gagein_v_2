package com.gagein.ui.tablet.settins;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Response.Listener;
import com.gagein.R;
import com.gagein.adapter.SwitchPlanAdapter;
import com.gagein.http.APIHttpMetadata;
import com.gagein.http.APIParser;
import com.gagein.model.PlanInfo;
import com.gagein.ui.BaseFragment;
import com.gagein.ui.main.MainTabActivity;
import com.gagein.util.CommonUtil;

public class SwitchPlanFragment extends BaseFragment implements OnItemClickListener{
	
	private ListView listView;
	private List<PlanInfo> planInfos = new ArrayList<PlanInfo>();
	private List<Boolean> selecteds = new ArrayList<Boolean>();
	private SwitchPlanAdapter adapter;
	
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
		
		setTitle(R.string.switch_plan);
		setRightButton(R.string.u_switch);
		
		listView = (ListView) view.findViewById(R.id.listView);
	}
	
	@Override
	protected void initData() {
		super.initData();
		
		String mMemid = CommonUtil.getMemid(mContext);
		planInfos = CommonUtil.readPlanInfos();
		for (int i = 0; i < planInfos.size(); i++) {
			if (planInfos.get(i).getMemid().equalsIgnoreCase(mMemid)) {
				selecteds.add(true);
			} else {
				selecteds.add(false);
			}
		}
		
		adapter = new SwitchPlanAdapter(mContext, planInfos, selecteds);
		listView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
	}
	
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
			
			String token = "";
			String memid = "";
			for (int i = 0; i < selecteds.size(); i++) {
				if (selecteds.get(i)) {
					token = planInfos.get(i).getAccessToken();
					memid = planInfos.get(i).getMemid();
				}
			}
			CommonUtil.setLoginInfo(mContext, token, memid);
			
			CommonUtil.removeActivity();
			
			startActivitySimple(MainTabActivity.class);
			
		}
		
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		
		if (selecteds.get(position)) return;
		
		for (int i = 0; i < selecteds.size(); i++) {
			selecteds.set(i, false);
		}
		selecteds.set(position, true);
		
		adapter.notifyDataSetChanged();
	}
	
}







