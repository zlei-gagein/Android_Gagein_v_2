package com.gagein.util.oauth;

import org.scribe.builder.api.DefaultApi20;
import org.scribe.model.OAuthConfig;
import org.scribe.utils.OAuthEncoder;


public class OAuthSalesforceAPI extends DefaultApi20{

	public static final String KEY_ACCESS_TOKEN = "access_token";
	public static final String KEY_REFRESH_TOKEN = "refresh_token";
	public static final String KEY_INSTANCE_URL = "instance_url";
	public static final String KEY_ACCOUNT_ID = "id";
	public static final String KEY_ISSUE_AT = "issued_at";
	public static final String KEY_SIGNATURE = "signature";
	
	private static final String AUTHORIZE_URL = "https://login.salesforce.com/services/oauth2/authorize?response_type=token&client_id=%s&redirect_uri=%s";
	private static final String SCOPED_AUTHORIZE_URL = AUTHORIZE_URL + "&scope=%s";
	  
	@Override
	public String getAccessTokenEndpoint() {
		return "https://login.salesforce.com/services/oauth2/access_token?grant_type=authorization_code";
	}

	@Override
	public String getAuthorizationUrl(OAuthConfig config) {
		if (config.hasScope())
	    {
	      return String.format(SCOPED_AUTHORIZE_URL, config.getApiKey(), OAuthEncoder.encode(config.getCallback()), OAuthEncoder.encode(config.getScope()));
	    }
	    else
	    {
	      return String.format(AUTHORIZE_URL, config.getApiKey(), OAuthEncoder.encode(config.getCallback()));
	    }
	}
}
