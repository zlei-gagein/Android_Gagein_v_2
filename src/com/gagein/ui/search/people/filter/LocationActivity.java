package com.gagein.ui.search.people.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.gagein.R;
import com.gagein.adapter.search.HeadquartersAdapter;
import com.gagein.adapter.search.SearchLocationAdapter;
import com.gagein.http.APIHttpMetadata;
import com.gagein.http.APIParser;
import com.gagein.model.filter.Filters;
import com.gagein.model.filter.Location;
import com.gagein.ui.main.BaseActivity;
import com.gagein.util.CommonUtil;
import com.gagein.util.Constant;
import com.gagein.util.Log;

public class LocationActivity extends BaseActivity implements OnItemClickListener{
	
	private Filters mFilters;
	private EditText addEdt;
	private ListView listView;
	private List<Location> searchLocations = new ArrayList<Location>();
	private SearchLocationAdapter searchLocationAdapter;
	private HeadquartersAdapter headerquartersAdapter;
	private ListView searchListView;
	private TimerTask timerTask;
	private Timer timer;
	private ImageView add;
	private List<Location> mLocations = new ArrayList<Location>();
	private LinearLayout noResultLayout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_company_filter_headquarters);
		
		doInit();
	}
	
	@Override
	protected void initView() {
		super.initView();
		setTitle(R.string.location);
		setLeftImageButton(R.drawable.back_arrow);
		
		addEdt = (EditText) findViewById(R.id.addEdt);
		listView = (ListView) findViewById(R.id.listView);
		searchListView = (ListView) findViewById(R.id.searchListView);
		add = (ImageView) findViewById(R.id.add);
		noResultLayout = (LinearLayout) findViewById(R.id.noResultLayout);
		
	}
	
	@Override
	protected void initData() {
		super.initData();
		mFilters = Constant.MFILTERS;
		mLocations = mFilters.getLocations();
		
		headerquartersAdapter = new HeadquartersAdapter(mContext, mLocations);
		listView.setAdapter(headerquartersAdapter);
		CommonUtil.setListViewHeight(listView);
		headerquartersAdapter.notifyDataSetChanged();
		headerquartersAdapter.notifyDataSetInvalidated();
		
		addEdt.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				
				String character = s.toString().trim();
				
				if (TextUtils.isEmpty(character) || null == character){ 
					
					cancelSearchTask();
					searchLocations.clear();
					if (null != searchLocationAdapter) searchLocationAdapter.notifyDataSetChanged();
					noResultLayout.setVisibility(View.GONE);
					
				} else {
					scheduleSearchTask(character, 2000);
				};
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
			}
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
			}
		});
		
		addEdt.setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
				
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					cancelSearchTask();
					if (TextUtils.isEmpty(textView.getText().toString())) {
						return false;
					}
					CommonUtil.hideSoftKeyBoard(mContext, LocationActivity.this);
					scheduleSearchTask(textView.getText().toString(), 0);
					return true;
				}
				return false;
			}
		});
		
	}
	
	/**schedule search*/
	private void scheduleSearchTask(final String character, final long time) {
		
		cancelSearchTask();
		timer = new Timer();
		timerTask = new TimerTask() {
			@Override
			public void run() {
				Message msg = new Message();
				Bundle data = new Bundle();
				data.putString("character", character);
				msg.what = 101;
				msg.setData(data);
				handler.sendMessage(msg);
			}
		};
		timer.schedule(timerTask, time);
	}
	
	@SuppressLint("HandlerLeak")
	Handler handler = new Handler(){

		@SuppressLint("NewApi")
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 101) {
				String character = msg.getData().getString("character", "");
				Log.v("silen", "search_character = " + character);
				searchHeadquarters(character);
			}
		}
		
	};
	
	private void searchHeadquarters(String character) {
		
		showLoadingDialog();
		mApiHttp.searchLocation(character, APIHttpMetadata.kGGLocationTypeForContact, new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject jsonObject) {
				
				APIParser parser = new APIParser(jsonObject);
				if (parser.isOK()) {
					
					searchLocations = parser.parserLocation();
					setSearchLocation();
					
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
	
	private void setSearchLocation() {
		
		noResultLayout.setVisibility(searchLocations.size() == 0 ? View.VISIBLE : View.GONE);
		
		searchLocationAdapter = new SearchLocationAdapter(mContext, searchLocations);
		searchListView.setAdapter(searchLocationAdapter);
		CommonUtil.setListViewHeight(searchListView);
		searchLocationAdapter.notifyDataSetChanged();
		searchLocationAdapter.notifyDataSetInvalidated();
		
	}
	
	/**stop schedule search*/
	private void cancelSearchTask() {
		if (timerTask != null) {
			timerTask.cancel();
		}
	}
	
	@Override
	protected void setOnClickListener() {
		super.setOnClickListener();
		searchListView.setOnItemClickListener(this);
		listView.setOnItemClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		if (v == leftImageBtn) {
			finish();
			CommonUtil.hideSoftKeyBoard(mContext, LocationActivity.this);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {
		
		if (parent == searchListView) {
			
			Location location = searchLocations.get(position);
			
			for (int i = 0; i < mLocations.size(); i ++) {
				if (location.getCode().equalsIgnoreCase(mLocations.get(i).getCode())) {
					showShortToast("Already added");
					return;
				}
			}
			
			location.setChecked(true);
			mLocations.add(location);
			addEdt.setText("");//attention there ,once the edittext set null , the list will clear
			add.requestFocus();//make addedt lost focus
			CommonUtil.hideSoftKeyBoard(mContext, LocationActivity.this);
			CommonUtil.setListViewHeight(listView);
			
		} else if (parent == listView){
			
			Boolean checked = mLocations.get(position).getChecked();
			mLocations.get(position).setChecked(!checked);
			headerquartersAdapter.notifyDataSetChanged();
			
		}
		
	}
}