package com.gagein.ui.news;

import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.gagein.R;
import com.gagein.http.APIParser;
import com.gagein.model.Company;
import com.gagein.model.Update;
import com.gagein.ui.main.BaseActivity;
import com.gagein.util.CommonUtil;
import com.gagein.util.Constant;
import com.gagein.util.MessageCode;

public class StoryActivity extends BaseActivity {
	
	private ImageView picture;
	private TextView fromsource;
	private TextView headline;
	private TextView content;
	private List<Update> updates;
	private int position;
	private Update update;
	private WebSettings webSettings;
	private WebView webview;
	private ScrollView scrollView;
	private Button mentionedCompaniesBtn;
	private Button twitters;
	private Button webPage;
	private List<Company> mentionedCompanies;

	@Override
	protected List<String> observeNotifications() {
		return stringList(Constant.BROADCAST_FOLLOW_COMPANY, Constant.BROADCAST_UNFOLLOW_COMPANY);
	}
	
	@Override
	public void handleNotifications(Context aContext, Intent intent) {
		super.handleNotifications(aContext, intent);
		
		String actionName = intent.getAction();
		
		if (actionName.equals(Constant.BROADCAST_FOLLOW_COMPANY)) {
			
			refreshCompanyFollowStatus(intent, true);
			
		} else if (actionName.equals(Constant.BROADCAST_UNFOLLOW_COMPANY)) {
			
			refreshCompanyFollowStatus(intent, false);
		}
	}
	
	private void refreshCompanyFollowStatus(Intent intent, Boolean follow) {
		
		long companyId = intent.getLongExtra(Constant.COMPANYID, 0);
		
		for (int i = 0; i < update.mentionedCompanies.size(); i ++) {
			long mCompanyId = update.mentionedCompanies.get(i).orgID;
			if (companyId == mCompanyId) {
				update.mentionedCompanies.get(i).followed = follow;
			}
		}
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_story);
		
		doInit();
	}
	
	@Override
	protected void initView() {
		super.initView();

		setLeftImageButton(R.drawable.back_arrow);
		setRightImageButton(R.drawable.share);
		setRightImageButton2(0);
		
		scrollView = (ScrollView) findViewById(R.id.scrollView);
		webview = (WebView) findViewById(R.id.webview);
		picture = (ImageView) findViewById(R.id.picture);
		fromsource = (TextView) findViewById(R.id.fromsource);
		headline = (TextView) findViewById(R.id.headline);
		content = (TextView) findViewById(R.id.content);
		mentionedCompaniesBtn = (Button) findViewById(R.id.mentionedCompanies);
		twitters = (Button) findViewById(R.id.twitters);
		webPage = (Button) findViewById(R.id.webPage);
	}
	
	@Override
	protected void initData() {
		super.initData();
		
		updates = Constant.getUpdates();
		if (null == updates || updates.size() == 0) {
			return;
		} else {
			position = getIntent().getIntExtra("position", 0);
			getUpdateDetailFromHttp(position);
		}
	}
	
	@Override
	protected void setData() {
		super.setData();
		
		String textviewStr = update.textview;
//		if (null == textviewStr || textviewStr.trim().equals(Constant.NOTHING)) {
//			showWebPage();
//			return;
//		} else {
//		}
		webview.setVisibility(View.GONE);
		
		if (null != update.pictures && update.pictures.size() > 0) {
			picture.setVisibility(View.VISIBLE);
			picture.setImageBitmap(null);
			loadImage(update.pictures.get(0), picture);
		} else {
			picture.setVisibility(View.GONE);
		}
		
		String fromsourceStr = CommonUtil.dateFormat(update.date) + " via " + update.fromSource;
//		fromsource.setText(fromsourceStr);//TODO write to commonUtil method
		SpannableString sp = new SpannableString(fromsourceStr);
		sp.setSpan(new ForegroundColorSpan(Color.parseColor("#FF8C44")), fromsourceStr.length() - update.fromSource.length(),
				fromsourceStr.length() , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		fromsource.setText(sp);
		headline.setText(update.headline);
		content.setText(textviewStr);
		scrollView.setVisibility(View.VISIBLE);
		
		mentionedCompanies = update.mentionedCompanies;
	}
	
	private void showWebPage() {
		webSettings = webview.getSettings();
		webSettings.setBuiltInZoomControls(true);
		webSettings.setDisplayZoomControls(false);
		webSettings.setSupportZoom(true);
		webview.loadUrl(update.url);
		scrollView.setVisibility(View.GONE);
		scrollView.scrollTo(0, 0);
		webview.setVisibility(View.VISIBLE);
	}
	
//	private void setLikeBackground() {
//		if (update.liked) {
//			rightImageBtn2.setBackgroundResource(R.drawable.like);
//		} else {
//			rightImageBtn2.setBackgroundResource(R.drawable.unlike);
//		}
//	}
	
	@Override
	protected void setOnClickListener() {
		super.setOnClickListener();
		mentionedCompaniesBtn.setOnClickListener(this);
		twitters.setOnClickListener(this);
		webPage.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		
		if (v == leftImageBtn) {
			
			finish();
			
		} else if (v == rightImageBtn) {//share
			
			showShareLayout(update, true);
			
		} else if (v == mentionedCompaniesBtn) {
			
			if (null == mentionedCompanies || mentionedCompanies.size() == 0) return;//TODO like the ios if show the button
			Intent intent = new Intent();
			intent.putExtra(Constant.UPDATE, update);
			intent.setClass(mContext, MentionedCompaniesActivity.class);
			startActivity(intent);
			
		} else if (v == twitters) {
			
			startWebActivity(update.twitterTweets);
			
		} else if (v == webPage) {
			
			startWebActivity(update.url);
			
		}
	}
	
	private void getUpdateDetailFromHttp(final int position) {
		showLoadingDialog();
		final long newsId = updates.get(position).newsId;
		mApiHttp.getCompanyUpdateDetail(newsId, false,
				new Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject jsonObject) {

						APIParser parser = new APIParser(jsonObject);
						if (parser.isOK()) {
							
							Intent intent = new Intent();
							intent.putExtra(Constant.NEWSID, newsId);
							intent.setAction(Constant.BROADCAST_HAVE_READ_STORY);
							sendBroadcast(intent);
							
							update = parser.parseGetCompanyUpdateDetail();
							updates.get(position).hasBeenRead = true;
							
							//TODO
							update.fromSource = updates.get(position).fromSource;
							update.date = updates.get(position).date;
							update.dateStr = updates.get(position).dateStr;
							update.liked = updates.get(position).liked;
							update.irrelevant = updates.get(position).irrelevant;
							setData();
						} else {
							String msg = MessageCode.messageForCode(parser.messageCode());
							if (msg != null && msg.length() > 0) {
								CommonUtil.showDialog(msg);
							}
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
