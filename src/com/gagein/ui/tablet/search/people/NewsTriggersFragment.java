package com.gagein.ui.tablet.search.people;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.gagein.R;
import com.gagein.adapter.search.FilterAdapter;
import com.gagein.adapter.search.SearchAgentAdapter;
import com.gagein.http.APIHttp;
import com.gagein.http.APIParser;
import com.gagein.model.Agent;
import com.gagein.model.filter.FilterItem;
import com.gagein.model.filter.Filters;
import com.gagein.ui.BaseFragment;
import com.gagein.util.CommonUtil;
import com.gagein.util.Constant;
import com.gagein.util.Log;

public class NewsTriggersFragment extends BaseFragment implements OnItemClickListener, OnFocusChangeListener{
	
	private Filters mFilters;
	private ListView systemAgentsListView;
	private ListView dataRankListView;
	private FilterAdapter systemAgentAdapter;
	private FilterAdapter dataRangAdapter;
	public List<FilterItem> mNewsTriggers;
	public List<FilterItem> mDateRanks;
	private LinearLayout thePastLayout;
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
	private OnNewsTriggersFinish onNewsTriggersFinish;
	private OnSearchFromNewsTriggers  onSearchFromNewsTriggers;
	
	public void refreshAdapter() {
		if (null != systemAgentAdapter) systemAgentAdapter.notifyDataSetChanged();
		if (null != dataRangAdapter) dataRangAdapter.notifyDataSetChanged();
 	}
	
	public interface OnNewsTriggersFinish {
		public void onNewsTriggersFinish();
	}
	public interface OnSearchFromNewsTriggers {
		public void onSearchFromNewsTriggers();
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			onNewsTriggersFinish = (OnNewsTriggersFinish) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + "must implement OnNewsTriggersFinish");
		}
		try {
			onSearchFromNewsTriggers = (OnSearchFromNewsTriggers) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + "must implement OnSearchFromNewsTriggers");
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.activity_search_company_filter_news_triggers, container, false);
		mContext = getActivity();
		mApiHttp = new APIHttp(mContext);
		
		doInit();
		return view;
	}
	
	@Override
	protected void initView() {
		super.initView();
		setTitle(R.string.news_triggers);
		setLeftImageButton(R.drawable.back_arrow);
		
		systemAgentsListView = (ListView) view.findViewById(R.id.systemAgentsList);
		dataRankListView = (ListView) view.findViewById(R.id.dataRankList);
		search = (ImageView) view.findViewById(R.id.search);
		
		allWordsListView = (ListView) view.findViewById(R.id.allWordsListView);
		exactPhraseListView = (ListView) view.findViewById(R.id.exactPhraseListView);
		anyWordsListView = (ListView) view.findViewById(R.id.anyWordsListView);
		noneWordsListView = (ListView) view.findViewById(R.id.noneWordsListView);
		
		thePastLayout = (LinearLayout) view.findViewById(R.id.thePastLayout);
		allWordsEdt = (EditText) view.findViewById(R.id.allWordsEdt);
		exactPhraseEdt = (EditText) view.findViewById(R.id.exactPhraseEdt);
		anyWordsEdt = (EditText) view.findViewById(R.id.anyWordsEdt);
		noneWordsEdt = (EditText) view.findViewById(R.id.noneWordsEdt);
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Constant.ALLWORDS = allWordsEdt.getText().toString().trim();
		Constant.EXACTWORDS = exactPhraseEdt.getText().toString().trim();
		Constant.ANYWORDS = anyWordsEdt.getText().toString().trim();
		Constant.NONEWORDS = noneWordsEdt.getText().toString().trim();
	}
	
//	@Override
//	protected void onDestroy() {
//		super.onDestroy();
//	}
	
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
		
		dataRangAdapter = new FilterAdapter(mContext, mDateRanks);
		dataRankListView.setAdapter(dataRangAdapter);
		CommonUtil.setListViewHeight(dataRankListView);
		dataRangAdapter.notifyDataSetChanged();
		dataRangAdapter.notifyDataSetInvalidated();
		
		allWordsEdt.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				String character = s.toString().trim();
				if (TextUtils.isEmpty(character) || null == character){ 
					removeListView(allWordsListView);
				} else {
					scheduleSearchTask(character, 2000, allWordsListView);
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
		
		exactPhraseEdt.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				String character = s.toString().trim();
				if (TextUtils.isEmpty(character) || null == character){ 
					removeListView(exactPhraseListView);
				} else {
					scheduleSearchTask(character, 2000, exactPhraseListView);
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
					scheduleSearchTask(character, 2000, anyWordsListView);
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
					scheduleSearchTask(character, 2000, noneWordsListView);
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
				currentListView = listView;
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
				searchAgent(character, currentListView);//TODO
			}
		}
		
	};
	
	private void searchAgent(String character, final ListView listView) {
		showLoadingDialog(mContext);
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
				showConnectionError(mContext);
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
				
				for (int i = 0; i < mNewsTriggers.size(); i ++) {
					mNewsTriggers.get(i).setChecked(checked ? false : true);
				}
				
				systemAgentAdapter.notifyDataSetChanged();
				
			} else {
				
				Boolean isAllSelected = true;
				for (int i = 0; i < mNewsTriggers.size(); i ++) {
					if (i == 0) continue;
					if (!mNewsTriggers.get(i).getChecked()) isAllSelected = false;
				}
				
				if (!isAllSelected) {
					for (int i = 0; i < mDateRanks.size(); i ++) {
						if (i == 3) {
							mDateRanks.get(i).setChecked(true);
						} else {
							mDateRanks.get(i).setChecked(false);
						}
					}
					dataRangAdapter.notifyDataSetChanged();
				}
				
				mNewsTriggers.get(position).setChecked(!checked);
				
				Boolean allChecked = true;
				for (int i = 0; i < mNewsTriggers.size(); i ++) {
					if (i == 0) {
						continue;
					} else {
						if (!mNewsTriggers.get(i).getChecked()) {
							allChecked = false;
							mNewsTriggers.get(0).setChecked(false);
						}
					}
				}
				
				if (allChecked) {
					mNewsTriggers.get(0).setChecked(true);
				}
				
				systemAgentAdapter.notifyDataSetChanged();//refresh listview state
			}
			
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
				dataRangAdapter.notifyDataSetChanged();
				
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
			
			for (int i = 0 ; i < mDateRanks.size(); i++) {
				mDateRanks.get(i).setChecked(false);
			}
			mDateRanks.get(mDateRanks.size() - 1).setChecked(true);
			dataRangAdapter.notifyDataSetChanged();
			thePastLayout.setVisibility(View.VISIBLE);
		}
		//TODO
		onSearchFromNewsTriggers.onSearchFromNewsTriggers();
			
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		if (v == leftImageBtn) {
			onNewsTriggersFinish.onNewsTriggersFinish();
			CommonUtil.hideSoftKeyBoard(mContext, getActivity());
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