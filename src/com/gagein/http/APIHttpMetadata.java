package com.gagein.http;


public class APIHttpMetadata {
	
	/**
	 * token
	 */
	public static String TOKEN = "";
	
	// method
	public static final String GET_METHOD = "GET";
	public static final String POST_METHOD = "POST";
	
	// server urls
	public static final String URL_KEY_PRODUCTION = "productionURL";
	public static final String URL_KEY_DEMO = "demoURL";
	public static final String URL_KEY_CN = "cnURL";
	public static final String URL_KEY_STAGING = "stagingURL";
	public static final String URL_KEY_CUSTOM = "customURL";
	
	public static final int kGGFeedbackCategoryBug                = 1;
	public static final int kGGFeedbackCategoryImprovement        = 2;
	public static final int kGGFeedbackCategoryFeatureRequest     = 3;
	public static final int kGGFeedbackCategoryQuestion           = 4;
	public static final int kGGFeedbackCategoryComment            = 5;
	
	public static final String kGGLocationTypeForCompany            = "0";
	public static final String kGGLocationTypeForContact            = "1";
	
	public static final String GG_FEEDBACK_IMPORTANCE_BLOCKER    = "Blocker";
	public static final String GG_FEEDBACK_IMPORTANCE_CRITICAL   = "Critical";
	public static final String GG_FEEDBACK_IMPORTANCE_MAJOR      = "Major";
	public static final String GG_FEEDBACK_IMPORTANCE_MINOR      = "Minor";
	
	// page flags
	/**
	 *  0
	 */
	public static final byte kGGPageFlagFirstPage = 0;
	public static final byte kGGPageFlagMoveDown  = 1;
	public static final byte kGGPageFlagMoveUp    = 2;
	
	public static final long LinkedCompaniesGroupId   = -20;
	public static final byte LinkedCompaniesFollowLinkType    = 4;
	
	// basic API parameters keys & values
	public static final String API_VERSION_KEY = "api_ver";
	public static final String API_VERSION_VALUE = "500";
	
	public static final String APP_CODE_KEY = "appcode";
	public static final String APP_CODE_ANDROID = "fdca0367ef5feae8";
	public static final String APP_VERION = "app_ver";
	
	public static final String APP_DEVICE_ID = "deviceid";
	public static final String ACCESS_TOKEN_KEY = "access_token";
	
	// API status code
	public static final byte kGGApiStatusNull = -1;
	public static final byte kGGApiStatusSuccess = 1;
	public static final byte kGGApiStatusWrongParam = 2;
	public static final byte kGGApiStatusVerificationFailed = 3;
	public static final byte kGGApiStatusInternalSystemError = 4;
	public static final byte kGGApiStatusUserOperationError = 5;
	
	// Test Data
	public static final String TEST_ACCESS_TOKEN_CN = "728f368b39053cdf58d0c2f27f8e8e85"; // production
	public static final long TEST_COMPANY_ID_CN = 1231041;
	
	/**
	 * -10
	 */
	public static final long GETALL = -10;
	
	// Contacts order by
	public static final byte kGGContactsOrderByJobLevel = 0;
	public static final byte kGGContactsOrderByName = 1;
	public static final byte kGGContactsOrderByRecent = 2;
	
	// happening type
	public static final byte EGGApiHappeningTypeCompany = 0;
	public static final byte EGGApiHappeningTypePerson = 1;
	public static final byte EGGApiHappeningTypeFunctionalArea = 2;
	public static final byte EGGApiHappeningTypeCategory = 3;
	
	// relevance level
	public static final byte kGGCompanyUpdateRelevanceUnKnown 	= 0;
	public static final byte kGGCompanyUpdateRelevanceNormal  	= 10;
	public static final byte kGGCompanyUpdateRelevanceHigh    	= 20;
	public static final byte kGGCompanyUpdateRelevanceVeryHigh 	= 30;
	public static final byte kGGCompanyUpdateRelevanceHighest  	= 40;
	
	// sn type
	public static final byte kGGSnTypeUnknown = -100;
	public static final byte kGGSnTypeFacebook = 1;
	public static final byte kGGSnTypeLinkedIn = 2;
	public static final byte kGGSnTypeTwitter = 3;
	public static final byte kGGSnTypeSalesforce = 101;
	public static final byte kGGSnTypeYammer = 102;

	// menu type
	public static final String kGGStrMenuTypeCompanies  = "companies";
	public static final String kGGStrMenuTypePeople  = "people";
	public static final String HAPPENING_CHANGE_LEAVE  = "LEAVE";
	public static final String HAPPENING_CHANGE_JOIN  = "JOIN";
	
	public static final int kGGExceptPendingFollowCompanies  = 1;
	public static final int kGGPendingFollowCompanies  = 2;
	public static final int kGGAllFollowCompanies  = 3;
	
	public static final String LIKED_CLOSER  = "closer";
	public static final String LIKED_ENABLE  = "enable";

	public static final String kCompetitorOrderByRevenueSize  = "revenue";
	public static final String kCompetitorOrderByEmployeeSize  = "noe";
	public static final String kCompetitorOrderByName  = "buzname";
	public static final String KCOMPETITORORDERBYRELEVANCE  = "relevance";
	
	//plan stype
	public static final String kGGPlanTypeProfessional = "101";
	public static final String kGGPlanTypeBusiness = "201";
	public static final String kGGPlanTypeSF = "301";
	
	//scores sort
	public static final String kGGScoreRank1 = "fol_rank1";
	public static final String kGGScoreRank7 = "fol_rank7";
	public static final String kGGScoreRank30 = "fol_rank30";
	
	
}







