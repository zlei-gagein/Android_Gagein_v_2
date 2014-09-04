package com.gagein.ui;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.emilsjolander.components.stickylistheaders.StickyListHeadersListView;
import com.gagein.R;
import com.gagein.adapter.CompanyCompetitorsAdapter;
import com.gagein.http.APIParser;
import com.gagein.model.Company;
import com.gagein.model.DataPage;
import com.gagein.ui.main.BaseActivity;
import com.gagein.util.Constant;
import com.gagein.util.Log;


public class TestActivity extends BaseActivity implements OnItemClickListener{
	
	private StickyListHeadersListView mStickyList;
	private View headView;
	private CompanyCompetitorsAdapter adapter;
	private long mCompanyId;
	private int requestPageNumber = Constant.PAGE_NUMBER_START;
	private String competitorOrder;
	private DataPage dpCompetitors;
	private List<Company> competitors;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_company);
		
		doInit();
	}
	
	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		super.initView();
		
		mStickyList = (StickyListHeadersListView) findViewById(R.id.list);
		headView = LayoutInflater.from(mContext).inflate(R.layout.header_company, null);
	}
	
	@Override
	protected void initData() {
		// TODO Auto-generated method stub
		super.initData();
		
		mCompanyId = getIntent().getLongExtra(Constant.COMPANYID, 0);
		getCompetitors();
	}
	
	@Override
	protected void setOnClickListener() {
		// TODO Auto-generated method stub
		super.setOnClickListener();
		mStickyList.setOnItemClickListener(this);
	}
	
	@Override
	protected void setData() {
		// TODO Auto-generated method stub
		super.setData();
//		adapter = new CompanyCompetitorsAdapter(mContext, competitors);
		Log.v("silen", "competitors.size == " + competitors.size());
		mStickyList.addHeaderView(headView);
		mStickyList.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		adapter.notifyDataSetInvalidated();
	}
	
	public void getCompetitors() {
		showLoadingDialog();
		mApiHttp.getSimilarCompanies(mCompanyId,"", requestPageNumber, competitorOrder,  new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject jsonObject) {
						
				competitors = new ArrayList<Company>();
						
						APIParser parser = new APIParser(jsonObject);
						if (parser.isOK()) {
							dpCompetitors = parser.parseGetSimilarCompanies();
							
							List<Object> items = dpCompetitors.items;
							if (items != null) {
								for (Object obj : items) {
									competitors.add((Company)obj);
								}
							}
							
//							if (!loadMore) {
							setData();
//							}
							
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

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		Log.v("silen", "arg2 = " + arg2);
	}
}
