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
import com.gagein.util.Constant;
import com.gagein.util.WebPageClient;

public class PrivacyFragment extends BaseFragment{
	
	private View view;
	private WebView webView;
	private String url;
	private WebSettings webSettings;
	private String privacyURL;
	private String termsURL;
	private WebPageClient webPageClient;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.activity_webpage, container, false);
		
		initData();
		return view;
	}
	
	protected void initData() {
		webPageClient = new WebPageClient(mContext);
		privacyURL = APIHttp.serverURL + "/html/privacy.html";
		termsURL = APIHttp.serverURL + "/html/tos.html";
		url = Constant.URL;
		if (null == url) return;
		if (!url.contains("http://") && !url.contains("https://")) {
			url = "http://" + url;
		}
		webView = (WebView) view.findViewById(R.id.webview);
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
		webView.setWebViewClient(webPageClient);
		webView.loadUrl(url);
	}
}
