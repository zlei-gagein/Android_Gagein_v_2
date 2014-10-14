package com.gagein.ui.scores;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.gagein.R;
import com.gagein.adapter.ScoresSortAdapter;
import com.gagein.model.ScoresSort;
import com.gagein.ui.main.BaseActivity;
import com.gagein.util.Constant;

public class ScoresSortActivity extends BaseActivity implements OnItemClickListener{
	
	private ListView listView; 
	private ScoresSortAdapter adapter;
	private List<Boolean> checkedList = new ArrayList<Boolean>();
	private List<ScoresSort> scoresSorts = new ArrayList<ScoresSort>();
	private int resultCode = 22;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_filter_news);
		
		doInit();
	}
	
	@Override
	protected void initView() {
		super.initView();
		
		setTitle(R.string.u_show);
		setRightButton(R.string.done);
		
		listView = (ListView) findViewById(R.id.listView);
		
	}
	
	@Override
	protected void initData() {
		super.initData();
		
		for (int i = 0; i < Constant.scoresSorts.size(); i++) {
			scoresSorts.add(Constant.scoresSorts.get(i));
			checkedList.add(Constant.scoresSorts.get(i).getChecked());
		}
		
		adapter = new ScoresSortAdapter(mContext, scoresSorts, checkedList);
		listView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		adapter.notifyDataSetInvalidated();
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
			setResult();
		}
		
	}

	private void setResult() {
		
		for (int i = 0; i < Constant.scoresSorts.size(); i++) {
			Constant.scoresSorts.get(i).setChecked(false);
		}
		for (int i = 0; i < checkedList.size(); i++) {
			if (checkedList.get(i)) Constant.scoresSorts.get(i).setChecked(true);
		}
		
		Intent intent = new Intent();
		intent.setClass(this, ScoresActivity.class);
		setResult(resultCode, intent);
		finish();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		Boolean selected = checkedList.get(position);
		if (selected) return;
		for (int i = 0; i < checkedList.size(); i++) {
			checkedList.set(i, false);
		}
		checkedList.set(position, true);
		adapter.notifyDataSetChanged();
	}
	
}





