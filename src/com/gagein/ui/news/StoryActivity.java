package com.gagein.ui.news;

import java.util.List;

import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
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
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_story);
		
		doInit();
	}
	
	@Override
	protected void initView() {
		super.initView();
//		setTitle(R.string.news);
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
			showShareLayout(update);
//		} else if (v == rightImageBtn2) {//like
//			if (update.liked) {
//				setUnLike(update);
//			} else {
//				setLike(update);
//			}
		} else if (v == mentionedCompaniesBtn) {
			if (null == mentionedCompanies || mentionedCompanies.size() == 0) return;//TODO like the ios if show the button
			Intent intent = new Intent();
			intent.putExtra(Constant.UPDATE, update);
			intent.setClass(mContext, MentionedCompaniesActivity.class);
			startActivity(intent);
		} else if (v == twitters) {
			startWebActivity(update.twitterTweets);
		} else if (v == webPage) {
			Log.v("silen", "update.url == " + update.url);
			startWebActivity(update.url);
		}
	}
	
	private void getUpdateDetailFromHttp(final int position) {
		showLoadingDialog();
		mApiHttp.getCompanyUpdateDetail(updates.get(position).newsId, false,
				new Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject jsonObject) {

						APIParser parser = new APIParser(jsonObject);
						if (parser.isOK()) {
							update = parser.parseGetCompanyUpdateDetail();
							updates.get(position).hasBeenRead = true;

							//TODO
							update.fromSource = updates.get(position).fromSource;
							update.date = updates.get(position).date;
							update.dateStr = updates.get(position).dateStr;
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
	
//	private void setUnLike(final Update update) {
//		showLoadingDialog();
//		mApiHttp.unlikeUpdate(update.newsId, new Listener<JSONObject>() {
//
//			@Override
//			public void onResponse(JSONObject jsonObject) {
//
//				APIParser parser = new APIParser(jsonObject);
//				if (parser.isOK()) {
//					update.liked = false;
////					setLikeBackground();
//				} else {
//					String msg = MessageCode.messageForCode(parser.messageCode());
//					if (msg != null && msg.length() > 0) {
//						CommonUtil.showDialog(msg);
//					}
//				}
//				dismissLoadingDialog();
//			}
//
//		}, new Response.ErrorListener() {
//
//			@Override
//			public void onErrorResponse(VolleyError error) {
//				showConnectionError();
//			}
//		});
//	}
	
//	private void setLike(final Update update) {
//		showLoadingDialog();
//		mApiHttp.likeUpdate(update.newsId, new Listener<JSONObject>() {
//
//			@Override
//			public void onResponse(JSONObject jsonObject) {
//				APIParser parser = new APIParser(jsonObject);
//				if (parser.isOK()) {
//					update.liked = true;
////					setLikeBackground();
//					if (null != parser.data()) {
//						String msgStatus = parser.data().optString("message_status");
//						if (msgStatus.equals(APIHttpMetadata.LIKED_CLOSER)) {
//							LikedTriggerchartDialog closerDialog = new LikedTriggerchartDialog(mContext, false);
//							closerDialog.showDialog();
//						} else if (msgStatus.equals(APIHttpMetadata.LIKED_ENABLE)){
//							LikedTriggerchartDialog closerDialog = new LikedTriggerchartDialog(mContext, true);
//							closerDialog.showDialog();
//						}
//						dismissLoadingDialog();
//						return;
//					}
////					CommonUtil.showImageShortToast(mContext.getResources().getString(R.string.liked), mContext);
//					showImageShortToast(R.string.liked);
//				} else {
//					String msg = MessageCode.messageForCode(parser.messageCode());
//					if (msg != null && msg.length() > 0) {
//						CommonUtil.showDialog(msg);
//					}
//				}
//				dismissLoadingDialog();
//			}
//
//		}, new Response.ErrorListener() {
//
//			@Override
//			public void onErrorResponse(VolleyError error) {
//				showConnectionError();
//			}
//		});
//	}

}
