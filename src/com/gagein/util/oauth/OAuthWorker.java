package com.gagein.util.oauth;

import java.io.Serializable;
import java.util.HashMap;

import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.webkit.WebView;

import com.gagein.util.CommonUtil;
import com.gagein.util.Log;
import com.gagein.util.WebPageClient;


public class OAuthWorker {

	String tag = "Gagein.OAuthWorker";
	
	private Context ctx = null;
	private OAuthService service = null;
	private Token requestToken = null;
	private Token accessToken = null;
	private Handler callbackHandler = null;
	
	
	public OAuthWorker(Context aContext) {
		ctx = aContext;
	}
	
	/**
	 * Salesforce OAuth
	 * @param aHandler
	 */
	public void executeSalesforceAuth(final Handler aHandler) {
		callbackHandler = aHandler;
		service = OAuthHelper.salesforceService();
		
		String url = service.getAuthorizationUrl(null);
	    url += "&display=touch";
	    loadWeb(url);
		
	}
	
	/**
	 * LinkedIn OAuth
	 * @param aHandler
	 */
	public void executeLinkedInAuth(final Handler aHandler) {
		callbackHandler = aHandler;
		service = OAuthHelper.linkedInService();
		
		new AsyncTask<Void, String, String>() {
			@Override
			protected String doInBackground(Void... params) {
				String authUrl = null;
				try {
					// get request token
					requestToken = service.getRequestToken();
					// get oauth url
					authUrl = service.getAuthorizationUrl(requestToken);
					
				} catch (Exception e) {
//					Log.d(tag, e.toString());
				}
				
				return authUrl;
			}

			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				// open web to let user authorize
				loadWeb(result);
			}
			
			
		}.execute();
		
	}
	
	private void loadWeb(String url) {
		Intent intent = new Intent(ctx, OAuthWebActivity.class);
		intent.putExtra(OAuthHelper.OAUTH_VERIFIER_URL, url);
		//intent.putExtra(OAuthHelper.OAUTH_WEB_CLIENT, (Serializable)(new OAuthWebViewClient()));
		OAuthWebActivity.webClient = new OAuthWebViewClient(ctx);
		ctx.startActivity(intent);
	}
	
	public class OAuthWebViewClient extends WebPageClient {
		
		String oauthVerifier = null;
		
		public OAuthWebViewClient(Context aContext) {
			super(aContext);
		}

		
	    @Override
	    public boolean shouldOverrideUrlLoading(WebView webView, String url) {
	    	if (url.contains("user_refused")) {
	    		OAuthWebActivity.getInstance().finish();
	    	} else if (url.contains(OAuthHelper.CALLBACK_URL)) { // oauth callback 
	            Uri uri = Uri.parse(url);
	            oauthVerifier = uri.getQueryParameter("oauth_verifier");
	            new AsyncTask<String, Void, Void>() {

					@Override
					protected Void doInBackground(String... params) {
						if (null == oauthVerifier) return null;
						Verifier verifier = new Verifier(oauthVerifier);
						try {
							accessToken = service.getAccessToken(requestToken, verifier);
						} catch (Exception e) {
							e.printStackTrace();
						}
						return null;
					}

					@Override
					protected void onPostExecute(Void result) {
						super.onPostExecute(result);
						
						if (accessToken != null) {
							String accessTokenStr = accessToken.getToken();
							String accessSecretStr = accessToken.getSecret();
							//String rawResponseStr = accessToken.getRawResponse();
							//Log.d(tag, accessTokenStr + ": " + accessSecretStr);
							//Log.d(tag, "raw: " + rawResponseStr);
							
							HashMap<String, String> dbMap = new HashMap<String, String>();
							dbMap.put(OAuther.AUTH_SN_NAME, OAuther.SN_NAME_LINKEDIN);
							dbMap.put(OAuther.AUTH_ACCESS_TOKEN, accessTokenStr);
							dbMap.put(OAuther.AUTH_OPEN_ID, "0");
							dbMap.put(OAuther.AUTH_SECRET, accessSecretStr);
							
							OAuthWebViewClient.this.notifyHandler(dbMap);
						} else {
							CommonUtil.showShortToast("Authentication failed, please try again.");
						}
						
						OAuthWebActivity.getInstance().finish();

					}
					
	            }.execute();
	            
	        } else if (url.contains(OAuthHelper.CALLBACK_URL_SALESFORCE)) {
	        	String urlModifiedStr = url.replace("#", "?");
	        	Uri uri=Uri.parse(urlModifiedStr);
	        	
	        	String accessToken = uri.getQueryParameter(OAuthSalesforceAPI.KEY_ACCESS_TOKEN);
	        	String refreshToken = uri.getQueryParameter(OAuthSalesforceAPI.KEY_REFRESH_TOKEN);
	        	String accountID = uri.getQueryParameter(OAuthSalesforceAPI.KEY_ACCOUNT_ID);
				if (accountID.contains("/")) {
					accountID = accountID.substring(accountID.lastIndexOf("/") + 1);
				}
	        	String instanceUrl = uri.getQueryParameter(OAuthSalesforceAPI.KEY_INSTANCE_URL);
	        	String issueAt = uri.getQueryParameter(OAuthSalesforceAPI.KEY_ISSUE_AT);
	        	String signature = uri.getQueryParameter(OAuthSalesforceAPI.KEY_SIGNATURE);
	        	
	        	HashMap<String, String> dbMap = new HashMap<String, String>();
				dbMap.put(OAuther.AUTH_SN_NAME, OAuther.SN_NAME_SALESFORCE);
				dbMap.put(OAuther.AUTH_ACCESS_TOKEN, accessToken);
				dbMap.put(OAuther.AUTH_OPEN_ID, accountID);
				dbMap.put(OAuther.AUTH_SECRET, "");		// oauth 2.0 no secret
				dbMap.put(OAuthSalesforceAPI.KEY_REFRESH_TOKEN, refreshToken);
				dbMap.put(OAuthSalesforceAPI.KEY_INSTANCE_URL, instanceUrl);
				dbMap.put(OAuthSalesforceAPI.KEY_ISSUE_AT, issueAt);
				dbMap.put(OAuthSalesforceAPI.KEY_SIGNATURE, signature);
				
				OAuthWebViewClient.this.notifyHandler(dbMap);
				OAuthWebActivity.getInstance().finish();
	        }
	        
	        //Boolean result = super.shouldOverrideUrlLoading(wv, url);
	        webView.loadUrl(url);
	        return true;
	    }
	    
	    
	    private void notifyHandler(HashMap<String, String> dbMap) {
			Message msg = callbackHandler.obtainMessage();
			Bundle bundle = new Bundle();
			bundle.putSerializable(OAuther.MSG_DATA_KEY, ((Serializable)dbMap));
			msg.setData(bundle);
			callbackHandler.sendMessage(msg);
			Log.v("silen", "notifyHandler.msg = " + msg);
		}
	    
	}
}
