package com.gagein.ui.search.company.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONObject;

import android.os.Bundle;
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
	private int checkedSystemAgentPosition;
	private TimerTask timerTask;
	private Timer timer;
	private List<Agent> agents = new ArrayList<Agent>();
	private SearchAgentAdapter searchAdapter;
	private ImageView searchIcon;
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
		searchIcon = (ImageView) findViewById(R.id.search);
		
		allWordsListView = (ListView) findViewById(R.id.allWordsListView);
		
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
		
		Constant.ALLWORDS_FOR_TRIGGERS = allWordsEdt.getText().toString().trim();
		
	}
	
	@Override
	protected void initData() {
		super.initData();
		
		mFilters = Constant.MFILTERS;
		mNewsTriggers = mFilters.getNewsTriggers();
		
		Boolean allChecked = true;
		for (int i = 0; i < mNewsTriggers.size() ; i++) {
			if (i == 0) continue;
			if (!mNewsTriggers.get(i).getChecked()) {
				allChecked = false;
				break;
			}
		}
		if (allChecked) mNewsTriggers.get(0).setChecked(true);
		
		mDateRanks = mFilters.getDateRanges();
		
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
				
				setSearchImageColor();
				
				String character = s.toString().trim();
				if (TextUtils.isEmpty(character) || null == character){ 
					
					cancelSearchTask();
					removeListView(allWordsListView);
					
					if (difineLayout.getVisibility() == View.VISIBLE) {
						allWordsEdt.setHint(R.string.all_of_these_words);
					} else {
						allWordsEdt.setHint(R.string.search_keyword);
					}
					
					
				} else {
					
					scheduleSearchTask(character, 1000, allWordsListView);
					
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
				
				if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
					
					setSearchImageColor();
					
					allWordsEdt.setText(textView.getText().toString());
					
					CommonUtil.hideSoftKeyBoard(mContext, NewsTriggersActivity.this);
					
					allWordsEdt.clearFocus();
					
					agents.clear();
					
					cancelSearchTask();
					
					packageAllWords();
					
					if (TextUtils.isEmpty(textView.getText().toString())) return false;
					
					difineLayout.setVisibility(View.VISIBLE);
//					scheduleSearchTask(textView.getText().toString(), 0, allWordsListView);
					return true;
				}
				return false;
			}
		});
		
		exactPhraseEdt.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
				
				if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
					packageAllWords();
				}
				
				return false;
				
			}
		});
		
		anyWordsEdt.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
				
				if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
					packageAllWords();
				}
				
				return false;
				
			}
		});
		
		noneWordsEdt.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
				
				if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
					packageAllWords();
					
				}
				
				return false;
				
			}
		});
		
		allWordsEdt.setOnFocusChangeListener(this);
		exactPhraseEdt.setOnFocusChangeListener(this);
		anyWordsEdt.setOnFocusChangeListener(this);
		noneWordsEdt.setOnFocusChangeListener(this);
	}
	
	private void packageAllWords() {
		
		String allWordsStr = allWordsEdt.getText().toString();
		String exactStr = exactPhraseEdt.getText().toString();
		String anyStr = anyWordsEdt.getText().toString();
		String noneStr = noneWordsEdt.getText().toString();
		
		if (!TextUtils.isEmpty(exactStr)) {
			allWordsStr = allWordsStr + " " + "\"" + exactStr + "\"";
		} 
		if (!TextUtils.isEmpty(anyStr)) {
			allWordsStr = allWordsStr + " " + "(" + anyStr + ")";
		} 
		
		if (!TextUtils.isEmpty(noneStr)) {
			String noneWords = noneStr;
			String[] result = noneWords.split("\\s+");
			for (int i = 0; i < result.length; i++) {
				String word = result[i];
				allWordsStr = allWordsStr + " " + "-" + word;
			}
		}
		
		allWordsEdt.setText(allWordsStr);
		cancelSearchTask();
		exactPhraseEdt.setText("");
		anyWordsEdt.setText("");
		noneWordsEdt.setText("");
		
		//reset triggers
		for (int i = 0; i < mNewsTriggers.size(); i ++) {
			mNewsTriggers.get(i).setChecked(false);
		}
		systemAgentAdapter.notifyDataSetChanged();
		
		Constant.ALLWORDS_FOR_TRIGGERS = allWordsStr;
		
	}
	
	private void setSearchImageColor() {
		
		Boolean allEditHaveText = false;
		
		if (!TextUtils.isEmpty(allWordsEdt.getText().toString()) || !TextUtils.isEmpty(exactPhraseEdt.getText().toString())
				|| !TextUtils.isEmpty(anyWordsEdt.getText().toString()) || !TextUtils.isEmpty(noneWordsEdt.getText().toString())) {
			allEditHaveText = true;
		}
		
		searchIcon.setBackgroundResource(allEditHaveText ? R.drawable.search_orange : R.drawable.search_gray);
		
	}
	
	@Override
	public void onFocusChange(View view, boolean changed) {
		if (!changed) {
			if (view == allWordsEdt) {
				removeListView(allWordsListView);
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
						searchAgent(character, currentListView);
					}
				});
			}
		};
		timer.schedule(timerTask, time);
	}
	
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
	}
	
	@Override
	public void onItemClick(AdapterView<?> parentView, View view, int position, long arg3) {
		
		if (parentView == systemAgentsListView) {
			
			Boolean selected = mNewsTriggers.get(position).getChecked();
			
			if (position == 0) {
				
				if (selected) {
					for (int i = 0; i < mNewsTriggers.size(); i ++) {
						mNewsTriggers.get(i).setChecked(false);
					}
				} else {
					for (int i = 0; i < mNewsTriggers.size(); i ++) {
						mNewsTriggers.get(i).setChecked(true);
					}
					
				}
				
				
			} else {
				//circle 
				
				if (selected) {
					mNewsTriggers.get(position).setChecked(false);
					mNewsTriggers.get(0).setChecked(false);
				} else {
					mNewsTriggers.get(position).setChecked(true);
					
					Boolean allChecked = true;
					for (int i = 0; i < mNewsTriggers.size(); i ++) {
						if (i == 0) continue;
						if (!mNewsTriggers.get(i).getChecked()) {
							allChecked = false;
						}
					}
					if (allChecked) {
						mNewsTriggers.get(0).setChecked(true);
					}
				}
				
				Boolean haveSelected= false;
				for (int i = 0; i < mNewsTriggers.size(); i ++) {
					if (i == 0) continue;
					if (mNewsTriggers.get(i).getChecked()) {
						haveSelected = true;
						break;
					}
				}
				
				if (!TextUtils.isEmpty(allWordsEdt.getText().toString())) {
					haveSelected = true;
				}
				
				if (!haveSelected) {
					for (int i = 0; i < mDateRanks.size(); i ++) {
						if (i == 3) {
							mDateRanks.get(i).setChecked(true);
						} else {
							mDateRanks.get(i).setChecked(false);
						}
					}
					dataRangesAdapter.notifyDataSetChanged();
				}
				
			}
			
			systemAgentAdapter.notifyDataSetChanged();//refresh listview state
			setPastDateShow();//set past date show or hide
			
			searchIcon.setBackgroundResource(R.drawable.search_gray);
			
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
				Constant.ALLWORDS_FOR_TRIGGERS = name;
			}
			for (int i = 0; i < mNewsTriggers.size(); i ++) {
				mNewsTriggers.get(i).setChecked(false);
			}
			searchIcon.setBackgroundResource(R.drawable.search_orange);
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

	private void setAllWordsEmpty() {
		allWordsEdt.setText("");
		Constant.ALLWORDS_FOR_TRIGGERS = "";
		searchIcon.setBackgroundResource(R.drawable.search_gray);
	}
	
	private void setAllWords() {
		allWordsEdt.setText(Constant.ALLWORDS_FOR_TRIGGERS);
		searchIcon.setBackgroundResource(R.drawable.search_orange);
		thePastLayout.setVisibility(View.VISIBLE);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		if (v == leftImageBtn) {
			finish(); 
		}
	}
	
	private void setPastDateShow() {
		
		Boolean haveSelecked = false;
		for (int i = 0; i < mNewsTriggers.size(); i ++) {
			if (mNewsTriggers.get(i).getChecked()) {
				haveSelecked = true;
				break;
			}
		}
		
		if (!haveSelecked) {
			
			if (!TextUtils.isEmpty(Constant.ALLWORDS_FOR_TRIGGERS)) {
				setAllWords();
			}
			
		} else {
			
			setAllWordsEmpty();
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
