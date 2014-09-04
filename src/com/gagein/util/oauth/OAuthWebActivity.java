package com.gagein.util.oauth;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.gagein.R;
import com.gagein.ui.main.BaseActivity;

public class OAuthWebActivity extends BaseActivity {
	
	public static WebViewClient webClient = null;
	private String oauthUrl = null;
	private WebView webView = null;
	private static OAuthWebActivity me = null;
	
	public static OAuthWebActivity getInstance(){
		   return me;
		 }
	
	public OAuthWebActivity() {
		me = this;
		OAuthWorker.OAuthWebViewClient client = (OAuthWorker.OAuthWebViewClient)webClient;
		client.context = this;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.oauth_web);
		
		// get data from intent
		Intent intent = getIntent();
		Bundle data = intent.getExtras();
		oauthUrl = data.getString(OAuthHelper.OAUTH_VERIFIER_URL);		
		//webClient = (WebViewClient)data.getSerializable(OAuthHelper.OAUTH_WEB_CLIENT);
		
		doInit();
		
		loadWeb();
	}
	
	
	@Override
	protected void initView() {
		super.initView();
		// webview load oauth web
		webView = (WebView)findViewById(R.id.webview);
	}

	@Override
	protected void initData() {
		super.initData();
		setTitle("Social Network Authentication");
	}

	private void loadWeb() {
		if (webClient != null && oauthUrl != null) {
			webView.clearCache(true);
			webView.getSettings().setJavaScriptEnabled(true);
		    webView.getSettings().setSupportZoom(true);
		    webView.getSettings().setBuiltInZoomControls(true);
			webView.setWebViewClient(webClient); 
		    webView.loadUrl(oauthUrl);
		}
	}
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		if (webClient != null) {
			OAuthWorker.OAuthWebViewClient client = (OAuthWorker.OAuthWebViewClient)webClient;
			client.dismissHud();
		}
	}
}
