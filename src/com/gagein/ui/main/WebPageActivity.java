package com.gagein.ui.main;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.gagein.R;
import com.gagein.http.APIHttp;
import com.gagein.util.Constant;
import com.gagein.util.Log;
import com.gagein.util.WebPageClient;

/**
 * webpage
 * 
 * @author silen
 * 
 */
public class WebPageActivity extends BaseActivity {


	private WebView webView;
	private String url;
	private WebSettings webSettings;
	private String privacyURL;
	private String termsURL;
	private WebPageClient webPageClient;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_webpage);

		doInit();
	}

	@Override
	protected void initView() {
		super.initView();
		
		setLeftImageButton(R.drawable.back_arrow);
	}

	@SuppressLint({ "NewApi", "SetJavaScriptEnabled" })
	@Override
	protected void initData() {
		super.initData();
		webPageClient = new WebPageClient(mContext);
		privacyURL = APIHttp.serverURL + "/html/privacy_v2.0.html";
		termsURL = APIHttp.serverURL + "/html/tos_v2.0.html";
		url = getIntent().getStringExtra(Constant.URL);
		if (!url.contains("http://") && !url.contains("https://")) {
			url = "http://" + url;
		}
		Log.v("silen", "webPage-url = " + url);
		if (url.equals(privacyURL)) {
			setTitle(R.string.privacy_policy);
		} else if (url.equals(termsURL)) {
			setTitle(R.string.terms_of_service);
		} else {
			setTitle(R.string.webpage);
		}
		webView = (WebView) findViewById(R.id.webview);
		webSettings = webView.getSettings();
		
		if (url.equals(privacyURL) || url.equals(termsURL)) {
			webView.setInitialScale(60);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
				webSettings.setUseWideViewPort(true);
				webSettings.setLoadWithOverviewMode(true);
			}
		} else {
			webSettings.setSupportZoom(true);
			webSettings.setBuiltInZoomControls(true);
			webSettings.setDisplayZoomControls(false);
		}
		webSettings.setJavaScriptEnabled(true);
		webView.setWebViewClient(webPageClient);
		webView.loadUrl(url);
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		if (v == leftImageBtn) {
			finish();
		}
	}

}
