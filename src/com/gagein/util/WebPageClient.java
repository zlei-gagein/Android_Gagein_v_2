package com.gagein.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.gagein.R;
import com.gagein.ui.main.WebPageActivity;

public class WebPageClient extends WebViewClient {
	
	protected ProgressDialog hud = null;
	public Context context = null;
	
    public WebPageClient(Context aContext) {
		super();
		context = aContext;
	}


	@Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        
        Log.d("silen", "url2 = " + url);
        if (url.contains("m.youtube.com") || url.contains("touch.www.linkedin.com") || url.contains("m.crunchbase.com")) {
        	Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        	context.startActivity(intent);
        	if(context instanceof WebPageActivity) {
        		((Activity)context).finish();
        	}
        	return true;
        } else {
        	view.loadUrl(url);
        }
        return false;
    }

	/* (non-Javadoc)
	 * @see android.webkit.WebViewClient#onPageStarted(android.webkit.WebView, java.lang.String, android.graphics.Bitmap)
	 */
	@Override
	public void onPageStarted(WebView view, String url, Bitmap favicon) {
		super.onPageStarted(view, url, favicon);
		
		dismissHud();
		
		String message = CommonUtil.stringFromResID(R.string.loading);
		hud = CommonUtil.progressWithMessage(message, context);
		try {
			hud.show();
		} catch (Exception e) {
		}
	}

	@Override
    public void onPageFinished(WebView view, String url) {
		super.onPageFinished(view, url);
		dismissHud();
	}
	
	public void dismissHud() {
		try {
			if (hud != null) {
				hud.dismiss();
				hud = null;
			}
		} catch (Exception e) {
		}
	}
}
