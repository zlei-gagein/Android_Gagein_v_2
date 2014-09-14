package com.gagein.ui.search.company.filter;

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
import android.view.View.OnFocusChangeListener;
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
import com.gagein.adapter.search.FilterAdapter;
import com.gagein.adapter.search.SearchAgentAdapter;
import com.gagein.http.APIParser;
import com.gagein.model.Agent;
import com.gagein.model.filter.FilterItem;
import com.gagein.model.filter.Filters;
import com.gagein.ui.main.BaseActivity;
import com.gagein.util.CommonUtil;
import com.gagein.util.Constant;
import com.gagein.util.Log;

public class NewsTriggersActivity extends BaseActivity implements OnItemClickListener, OnFocusChangeListener{
	
	private Filters mFilters;
	private ListView systemAgentsListView;
	private ListView dataRankListView;
	private FilterAdapter systemAgentAdapter;
	private FilterAdapter dataRangesAdapter;
	public List<FilterItem> mNewsTriggers;
	public List<FilterItem> mDateRanks;
	private LinearLayout thePastLayout;
	private LinearLayout difineLayout;
	private EditText allWordsEdt;
	private EditText exactPhraseEdt;
	private EditText anyWordsEdt;
	private EditText noneWordsEdt;
	private ListView allWordsListView;
	private ListView exactPhraseListView;
	private ListView anyWordsListView;
	private ListView noneWordsListView;
	private int checkedSystemAgentPosition;
	private TimerTask timerTask;
	private Timer timer;
	private List<Agent> agents = new ArrayList<Agent>();
	private SearchAgentAdapter searchAdapter;
	private String allWords = "";
	private String exactWords = "";
	private String anyWords = "";
	private String noneWords = "";
	private ImageView search;
	private ListView currentListView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_company_filter_news_triggers);
		
		doInit();
	}
	
	@Override
	protected void initView() {
		super.initView();
		setTitle(R.string.news_triggers);
		setLeftImageButton(R.drawable.back_arrow);
		
		systemAgentsListView = (ListView) findViewById(R.id.systemAgentsList);
		dataRankListView = (ListView) findViewById(R.id.dataRankList);
		search = (ImageView) findViewById(R.id.search);
		
		allWordsListView = (ListView) findViewById(R.id.allWordsListView);
		exactPhraseListView = (ListView) findViewById(R.id.exactPhraseListView);
		anyWordsListView = (ListView) findViewById(R.id.anyWordsListView);
		noneWordsListView = (ListView) findViewById(R.id.noneWordsListView);
		
		thePastLayout = (LinearLayout) findViewById(R.id.thePastLayout);
		difineLayout = (LinearLayout) findViewById(R.id.difineLayout);
		allWordsEdt = (EditText) findViewById(R.id.allWordsEdt);
		exactPhraseEdt = (EditText) findViewById(R.id.exactPhraseEdt);
		anyWordsEdt = (EditText) findViewById(R.id.anyWordsEdt);
		noneWordsEdt = (EditText) findViewById(R.id.noneWordsEdt);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Constant.ALLWORDS = allWordsEdt.getText().toString().trim();
		Constant.EXACTWORDS = exactPhraseEdt.getText().toString().trim();
		Constant.ANYWORDS = anyWordsEdt.getText().toString().trim();
		Constant.NONEWORDS = noneWordsEdt.getText().toString().trim();
	}
	
	@Override
	protected void initData() {
		super.initData();
		mFilters = Constant.MFILTERS;
		mNewsTriggers = mFilters.getNewsTriggers();
		mDateRanks = mFilters.getDateRanges();
		allWords = Constant.ALLWORDS;
		exactWords = Constant.EXACTWORDS;
		anyWords = Constant.ANYWORDS;
		noneWords = Constant.NONEWORDS;
		
		searchAdapter = new SearchAgentAdapter(mContext, agents);
		systemAgentAdapter = new FilterAdapter(mContext, mNewsTriggers);
		systemAgentsListView.setAdapter(systemAgentAdapter);
		CommonUtil.setListViewHeight(systemAgentsListView); 
		systemAgentAdapter.notifyDataSetChanged();
		systemAgentAdapter.notifyDataSetInvalidated();
		
		setPastDateShow();//cycle for check which item is checked
		
		dataRangesAdapter = new FilterAdapter(mContext, mDateRanks);
		dataRankListView.setAdapter(dataRangesAdapter);
		CommonUtil.setListViewHeight(dataRankListView);
		dataRangesAdapter.notifyDataSetChanged();
		dataRangesAdapter.notifyDataSetInvalidated();
		
		allWordsEdt.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				
				String character = s.toString().trim();
				if (TextUtils.isEmpty(character) || null == character){ 
					
					cancelSearchTask();
					removeListView(allWordsListView);
					
					difineLayout.setVisibility(View.GONE);
					
				} else {
					
					scheduleSearchTask(character, 800, allWordsListView);
					
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
		
		allWordsEdt.setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
				
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					cancelSearchTask();
					if (TextUtils.isEmpty(textView.getText().toString())) {
						return false;
					}
					CommonUtil.hideSoftKeyBoard(mContext, NewsTriggersActivity.this);
					
					scheduleSearchTask(textView.getText().toString(), 0, allWordsListView);
					return true;
				}
				return false;
			}
		});
		
		exactPhraseEdt.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				String character = s.toString().trim();
				if (TextUtils.isEmpty(character) || null == character){ 
					removeListView(exactPhraseListView);
				} else {
					scheduleSearchTask(character, 800, exactPhraseListView);
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
		
		anyWordsEdt.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				String character = s.toString().trim();
				if (TextUtils.isEmpty(character) || null == character){ 
					removeListView(anyWordsListView);
				} else {
					scheduleSearchTask(character, 800, anyWordsListView);
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
		
		noneWordsEdt.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				String character = s.toString().trim();
				if (TextUtils.isEmpty(character) || null == character){ 
					removeListView(noneWordsListView);
				} else {
					scheduleSearchTask(character, 800, noneWordsListView);
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
		
		allWordsEdt.setOnFocusChangeListener(this);
		exactPhraseEdt.setOnFocusChangeListener(this);
		anyWordsEdt.setOnFocusChangeListener(this);
		noneWordsEdt.setOnFocusChangeListener(this);
	}
	
	@Override
	public void onFocusChange(View view, boolean changed) {
		if (!changed) {
			if (view == allWordsEdt) {
				removeListView(allWordsListView);
			} else if (view == exactPhraseEdt) {
				removeListView(exactPhraseListView);
			} else if (view == anyWordsEdt) {
				removeListView(anyWordsListView);
			} else if (view == noneWordsEdt) {
				removeListView(noneWordsListView);
			}
		}
	}
	
	private void removeListView(ListView listView) {
		cancelSearchTask();
		agents.clear();
		searchAdapter.notifyDataSetChanged();
		CommonUtil.setListViewHeight(listView);
	}
	
	/**schedule search*/
	private void scheduleSearchTask(final String character, final long time, final ListView listView) {
		
		cancelSearchTask();
		timer = new Timer();
		timerTask = new TimerTask() {
			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						currentListView = listView;
						Message msg = new Message();
						Bundle data = new Bundle();
						data.putString("character", character);
						msg.what = 101;
						msg.setData(data);
						handler.sendMessage(msg);
					}
				});
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
				searchAgent(character, currentListView);
			}
		}
		
	};
	
	private void searchAgent(String character, final ListView listView) {
		showLoadingDialog();
		mApiHttp.searchAgent(character, new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject jsonObject) {
				APIParser parser = new APIParser(jsonObject);
				if (parser.isOK()) {
					agents = parser.parserAgent();
					setSearchAgents(listView);
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
	
	private void setSearchAgents(ListView listView) {
		searchAdapter = new SearchAgentAdapter(mContext, agents);
		listView.setAdapter(searchAdapter);
		CommonUtil.setListViewHeight(listView);
		searchAdapter.notifyDataSetChanged();
		searchAdapter.notifyDataSetInvalidated();
	}

	@Override
	protected void setOnClickListener() {
		super.setOnClickListener();
		systemAgentsListView.setOnItemClickListener(this);
		dataRankListView.setOnItemClickListener(this);
		
		allWordsListView.setOnItemClickListener(this);
		exactPhraseListView.setOnItemClickListener(this);
		anyWordsListView.setOnItemClickListener(this);
		noneWordsListView.setOnItemClickListener(this);
	}
	
	@Override
	public void onItemClick(AdapterView<?> parentView, View view, int position, long arg3) {
		if (parentView == systemAgentsListView) {
			Boolean checked = mNewsTriggers.get(position).getChecked();
			if (position == 0) {
				Boolean haveChecked = false;
				for (int i = 0; i < mNewsTriggers.size(); i ++) {
					if (i == 0) continue;
					if (mNewsTriggers.get(i).getChecked()) {
						haveChecked = true;
						break;
					}
				}
				if (!haveChecked) {
					return;
				} else {
					mNewsTriggers.get(position).setChecked(true);
					for (int i = 0; i < mNewsTriggers.size(); i ++) {
						if (i != 0) mNewsTriggers.get(i).setChecked(false);
					}
				}
			} else {
				//circle 
				Boolean haveCheck= false;
				for (int i = 0; i < mNewsTriggers.size(); i ++) {
					if (i == 0) continue;
					if (mNewsTriggers.get(i).getChecked()) {
						haveCheck = true;
						break;
					}
				}
				if (!haveCheck) {
					for (int i = 0; i < mDateRanks.size(); i ++) {
						if (i == 3) {
							mDateRanks.get(i).setChecked(true);
						} else {
							mDateRanks.get(i).setChecked(false);
						}
					}
					dataRangesAdapter.notifyDataSetChanged();
				}
				
				mNewsTriggers.get(position).setChecked(!checked);
				Boolean haveChecked = false;
				for (int i = 0; i < mNewsTriggers.size(); i ++) {
					if (i == 0) {
						continue;
					} else {
						if (mNewsTriggers.get(i).getChecked()) {
							haveChecked = true;
							mNewsTriggers.get(0).setChecked(false);
						}
					}
				}
				if (!haveChecked) {
					mNewsTriggers.get(0).setChecked(true);
				}
			}
			
			systemAgentAdapter.notifyDataSetChanged();//refresh listview state
			setPastDateShow();//set past date show or hide
			
			Constant.DEFINEWORDS = false;
			search.setBackgroundResource(R.drawable.search_gray);
		} else if (parentView == dataRankListView) {
			Boolean checked = mDateRanks.get(position).getChecked();
			if (!checked) {
				for (int i = 0; i < mDateRanks.size(); i ++) {
					mDateRanks.get(i).setChecked(false);
				}
				mDateRanks.get(position).setChecked(true);
				dataRangesAdapter.notifyDataSetChanged();
				
				mNewsTriggers.get(checkedSystemAgentPosition).setPastDatePosition(position);
			}
		} else {
			
			String name = agents.get(position).getName();
			
			if (parentView == allWordsListView) {
				allWordsEdt.setText(name);
				removeListView(allWordsListView);
				Constant.ALLWORDS = name;
			} else if (parentView == exactPhraseListView) {
				exactPhraseEdt.setText(name);
				removeListView(exactPhraseListView);
				Constant.EXACTWORDS = name;
			} else if (parentView == anyWordsListView) {
				anyWordsEdt.setText(name);
				removeListView(anyWordsListView);
				Constant.ANYWORDS = name;
			} else if (parentView == noneWordsListView) {
				noneWordsEdt.setText(name);
				removeListView(noneWordsListView);
				Constant.NONEWORDS = name;
			}
			for (int i = 0; i < mNewsTriggers.size(); i ++) {
				mNewsTriggers.get(i).setChecked(false);
			}
			Constant.DEFINEWORDS = true;
			search.setBackgroundResource(R.drawable.search_orange);
			for (int i = 0; i < mNewsTriggers.size(); i ++) {
				mNewsTriggers.get(i).setChecked(false);
			}
			systemAgentAdapter.notifyDataSetChanged();
			
			Boolean haveChecked = false;
			for (int i = 0 ; i < mDateRanks.size(); i++) {
				if (mDateRanks.get(i).getChecked()) {
					haveChecked = true;
				}
			}
			
			if (!haveChecked) {
				mDateRanks.get(mDateRanks.size() - 2).setChecked(true);
			}
			dataRangesAdapter.notifyDataSetChanged();
			
			thePastLayout.setVisibility(View.VISIBLE);
			difineLayout.setVisibility(View.VISIBLE);
		}
			
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		if (v == leftImageBtn) {
			finish(); 
		}
	}
	
	private void setPastDateShow() {
		Boolean haveChecked = false;
		for (int i = 0; i < mNewsTriggers.size(); i ++) {
			if (mNewsTriggers.get(i).getChecked()) {
				if (i != 0) {
					haveChecked = true;
					thePastLayout.setVisibility(View.VISIBLE);
					break;
				}
			}
		}
		if (!haveChecked) thePastLayout.setVisibility(View.GONE);
		
		allWordsEdt.setText(allWords);
		exactPhraseEdt.setText(exactWords);
		anyWordsEdt.setText(anyWords);
		noneWordsEdt.setText(noneWords);
		if (Constant.DEFINEWORDS) {
			search.setBackgroundResource(R.drawable.search_orange);
			thePastLayout.setVisibility(View.VISIBLE);
		}
	}
	
	/**stop schedule search*/
	private void cancelSearchTask() {
		if (timerTask != null) {
			timerTask.cancel();
		}
	}


}
