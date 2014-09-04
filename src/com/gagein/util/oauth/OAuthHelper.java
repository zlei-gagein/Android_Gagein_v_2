package com.gagein.util.oauth;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.LinkedInApi;
import org.scribe.oauth.OAuthService;


public class OAuthHelper {

	public static final String CALLBACK_URL = "https://www.gagein.com";
	public static final String CALLBACK_URL_SALESFORCE = "https://login.salesforce.com/services/oauth2/success";
	
	public static final String OAUTH_VERIFIER_URL = "OAUTH_VERIFIER_URL";
	public static final String OAUTH_WEB_CLIENT = "OAUTH_WEB_CLIENT";
	
	
	public static OAuthService linkedInService() {
		
		return new ServiceBuilder()
        .provider(LinkedInApi.class)
        .apiKey("JG-8S3mdrOdAUT2tuZPadrAYX-Y7tWol-z3fashTf44aDRNLhqkmz6GD0XrKIhfx")
        .apiSecret("4UUmZlEbsoMr3tk9FiDmNFmKOHWsvYgt0o0QrESsS1tVjQRRXKlvZWKnjUox9qnJ")
        .callback(CALLBACK_URL)
        .build();
		
	}
	
	public static OAuthService salesforceService() {
		
		return new ServiceBuilder()
        .provider(OAuthSalesforceAPI.class)
        .apiKey("3MVG9QDx8IX8nP5Rg7yD2yhM0mSIoG5JhtwAfaXVxWdWvLQ2c9dbC5IdPIt8bV9wAgE4sLNdWDWrvrHs7izVe")
        .apiSecret("2673905608344491606")
        .scope("full refresh_token")
        .callback(CALLBACK_URL_SALESFORCE)
        .build();
		
	}
}
