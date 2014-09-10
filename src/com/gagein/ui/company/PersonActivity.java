package com.gagein.ui.company;

import org.json.JSONObject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.emilsjolander.components.stickylistheaders.StickyListHeadersListView;
import com.gagein.R;
import com.gagein.adapter.PersonAboutAdapter;
import com.gagein.http.APIParser;
import com.gagein.model.Person;
import com.gagein.ui.main.BaseActivity;
import com.gagein.util.Constant;
import com.gagein.util.Log;

public class PersonActivity extends BaseActivity {
	
	private long personId;
	private Person person;
	private StickyListHeadersListView listView;
	private PersonAboutAdapter adapter;
	private View sectionView;
	private View headView;
	private ImageView personLog;
	private TextView personName;
	private TextView jobTitle;
	private TextView address;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_person);
		
		doInit();
	}
	
	@Override
	protected void initData() {
		super.initData();
		setLeftImageButton(R.drawable.back_arrow);
		
		personId = getIntent().getLongExtra(Constant.PERSONID, 0);
		getPerson();
	}
	
	@Override
	protected void initView() {
		super.initView();
		
		listView = (StickyListHeadersListView) findViewById(R.id.list);
		sectionView = LayoutInflater.from(mContext).inflate(R.layout.section_person, null);
		
		headView = LayoutInflater.from(mContext).inflate(R.layout.header_person, null);
		personLog = (ImageView) headView.findViewById(R.id.personLog);
		personName = (TextView) headView.findViewById(R.id.personName);
		jobTitle = (TextView) headView.findViewById(R.id.jobTitle);
		address = (TextView) headView.findViewById(R.id.address);
	}
	
	@Override
	protected void setOnClickListener() {
		super.setOnClickListener();
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		if (v == leftImageBtn) {
			finish();
		}
	}
	
	@Override
	protected void setData() {
		super.setData();
		
		setTitle(person.name);
		setHeadData();
		
		adapter = new PersonAboutAdapter(mContext, person, sectionView);
		listView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		adapter.notifyDataSetInvalidated();
	}
	
	private void setHeadData() {
		listView.addHeaderView(headView);
		personLog.setImageResource(R.drawable.logo_person_default);
		loadImage(person.photoPath, personLog);
		Log.v("silen", "person.photoPath = " + person.photoPath);
		personName.setText(person.name);
		jobTitle.setText(person.orgTitle);
		address.setText(person.address);
	}
	
	private void getPerson() {
		showLoadingDialog();
		mApiHttp.getPersonOverview(personId, true, new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject jsonObject) {
						APIParser parser = new APIParser(jsonObject);
						if (parser.isOK()) {
							person = parser.parseGetPersonOverview();
							
							setData();
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
