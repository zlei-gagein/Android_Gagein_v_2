package com.gagein.ui.tablet.settins;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.gagein.R;
import com.gagein.http.APIHttp;
import com.gagein.ui.BaseFragment;
import com.gagein.util.WebPageClient;

public class PrivacyFragment extends BaseFragment{
	
	private WebView webView;
	private WebSettings webSettings;
	private String privacyURL;
	private WebPageClient webPageClient;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.activity_webpage, container, false);
		
		doInit();
		
		return view;
	}
	
	@Override
	protected void initView() {
		super.initView();
		
		setTitle(R.string.privacy_policy);
	}
	
	@Override
	protected void initData() {
		super.initData();
		
		webPageClient = new WebPageClient(mContext);
		privacyURL = APIHttp.serverURL + "/html/privacy_v2.0.html";
		
		webView = (WebView) view.findViewById(R.id.webview);
		webSettings = webView.getSettings();
		
		webView.setInitialScale(60);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			webSettings.setUseWideViewPort(true);
			webSettings.setLoadWithOverviewMode(true);
		}
		
		webView.setWebViewClient(webPageClient);
		webView.loadUrl(privacyURL);
	}
}
