package com.gagein.ui.newsfilter;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.gagein.R;
import com.gagein.ui.company.CompanyActivity;
import com.gagein.ui.main.BaseActivity;

public class FilterNewsCompanyActivity extends BaseActivity {
	
	private Button newsBtn;
	private RelativeLayout news;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_newsfilter_company);
		
		doInit();
	}
	
	@Override
	protected void initView() {
		super.initView();
		setTitle(R.string.filters);
		setRightButton(R.string.done);
		
		newsBtn = (Button) findViewById(R.id.newsBtn);
		news = (RelativeLayout) findViewById(R.id.news);
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		initData();
	}
	
	@Override
	protected void setOnClickListener() {
		super.setOnClickListener();
		newsBtn.setOnClickListener(this);
		news.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		
		if (v == rightBtn) {
			
			setResult();
			
		} else if (v == news || v == newsBtn) {
			
			startActivitySimple(FilterNewsActivity.class);
			
		}
	}

	private void setResult() {
		Intent intent = new Intent(FilterNewsCompanyActivity.this, CompanyActivity.class);
		setResult(RESULT_OK, intent);
		finish();
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		 
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            //do something...
        	setResult();
         }
         return super.onKeyDown(keyCode, event);
	}
	
}
