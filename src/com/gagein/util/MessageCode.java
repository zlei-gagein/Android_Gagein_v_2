package com.gagein.util;

import com.gagein.R;

public class MessageCode {

	public static final int ErrorNull = -1;         // user login failed
	
	public static final int AuthError = 10001;         // user login failed
		    
    // register
    public static final int RegEmailExists = 11001;             // email user entered exists when signing up
    public static final int RegEmailExistsAccountNotConfirmed  = 11002;   // N/A FOR IOS
    public static final int RegEmailInvalid  = 11003;                    // email format invalid
    public static final int RegWorkEmailNotEqualAdditional  = 11004;     // user's working email is the same as additional emailpublic static final int which is illegal
    public static final int RegError = 11005;                           // registration failed
    
    // billing
    public static final int BillingCantFollowMoreCompany        = 20001;    // user reaches the max limitation for companies he can follow
    public static final int BillingCantFollowMorePeople         = 20002;             // user reaches the max limitation for people he can follow
    public static final int BillingCantAccessUpdateNeedPay      = 20003;         // user need to pay for further access
    public static final int BillingCantAccessUpdateNeedUpgrade  = 20004;      // user need to upgrad for further access
    public static final int BillingExceededQuota                = 20005;                   // user reaches the limitation of his plan
    public static final int BillingCantSaveAnyMoreUpdate        = 20006;           // user reaches the limitation for updates he can save
    public static final int BillingFreeCantFollowGradeC         = 20007;
    
    // company
    public static final int CompanyAlreadyFollowed = 30001;          // company has already been followed
    public static final int CompanyNotFollowed = 30002;              // company has not benn followed yet
    public static final int CompanyBuzExists = 30003;                // N/A FOR IOS
    public static final int CompanyWebConnectFailed = 30004;           // N/A FOR IOS
    public static final int CompanyCantFollowGradeB = 30007;
    public static final int CompanyFollowedGradC = 30008;
    public static final int CompanyGradeBNoUpdate = 30009;           // not_updates_for_the_grade_b_company
    public static final int CompanyGradeCNoUpdate = 30010;           // not_updates_for_the_grade_c_company
    public static final int CompanyCantUnfollowLinkedByCRM = 30011;      // can_not_unfollow_the_company_linked_by_crm
    public static final int CompanyIsMergedOrAquired       = 30015;      // This company is merged or acquired
    public static final int CompanyIsClosed                = 30016;      // company_already_has_been_closed = "30016";
    public static final int CompanyWebConnectTimeout                = 30020;      // company_already_has_been_closed = "30016";
    
    // group
    public static final int MemberHasAlreadyOwnedGroup     = 30023;      // Member has already owned group
    
    // people
    public static final int MemberProfileLoadError  = 31001;          // member's profile cant be found in database
    public static final int PeopleNotFound          = 31002;          // people not found in database
    public static final int PeopleNotFollowed       = 31003;          // not_follow_the_contact
    public static final int PeopleCantUnfollowLinkedByCRM = 31004;       // can_not_unfollow_the_people_linked_by_crm
    
    // updates
    public static final int NoUpdateForLessFollowedCompanies = 32001;   // Empty Page Message No.2
    public static final int NoUpdateForMoreFollowedCompanies = 32002;          // Empty Page Message No.3
    public static final int NoUpdateForAllSalesTriggers      = 32003;          // Empty Page Message No.1
    public static final int NoUpdateForTheCompany            = 32004;          // Empty Page Message No.5 & No.7
    public static final int NoUpdateTheSaleTrigger           = 32005;           // Empty Page Message No.??? -- no update for a specific agent
    public static final int NoUpdateTheUnfollowedCompany     = 32006;           // Empty Page Message No.8
    public static final int NoUpdateTheUnavailableCompany    = 32007;           // Empty Page Message No.9
    public static final int NoUpdateForNewCompany            = 32008;           // no_updates_for_new_company = "32008"
    public static final int NoUpdateNoFollowedCompanies      = 32009;           // no_updates_for_no_followed_companies = "32009"
    
    // events
    public static final int NoEventForLessFollowedCompanies = 33001;    // Empty Page Message No.4
    public static final int NoEventForMoreFollowedCompanies = 33002;           // Empty Page Message No.4
    public static final int NoEventForTheCompany            = 33003;          // Empty Page Message No.6 & No.10
    public static final int NoEventForLessFollowedContacts  = 33004;            // Empty Page Message No.12
    public static final int NoEventForMoreFollowedContacts  = 33005;            // Empty Page Message No.13
    public static final int NoEventForTheContact            = 33006;            // Empty Page Message No.14
    public static final int NoEventForTheAllSelectedFunctionals  = 33007;       // Empty Page Message No.11
    public static final int NoEventForTheFunctional              = 33008;       // Empty Page Message No.??? -- no happening for a specific func area
    public static final int NoEventForTheCategory                = 33010;
    public static final int NoEventForNewCompany                 = 33011;       // no_events_for_new_company = "33011"
    public static final int NoEventNoFollowedCompanies           = 33012;       // no_events_for_no_followed_companies = "33012";
    
    // saved updates/searches
//#warning TODO: search keywords need to be saved locally
    public static final int SavedSearchDoesNotExist = 34001;             // N/A FOR IOS
    public static final int UpdateNotSaved = 34002;                              // N/A FOR IOS
    public static final int TagNameCreated = 34003;                              // N/A FOR IOS
    public static final int NoSavedUpdates = 34004;                      // no saved update
    
    // social network
    public static final int SnShareUpdateError = 40001;                  // failed sharing an update
    public static final int SnShareEventError = 40002;                           // failed sharing a happening
    public static final int SnLinkedInAccountDisconnected = 40003;               // try to send a message to a linkedIn contactpublic static final int but not connected
    public static final int SnCantGetUserInfo = 40004;                        // failed to get user info using social account
    public static final int SnSaleforceCantAuth = 40005;                         // salesforce_account_edition_cannot_authorize_actions = "40005"
    public static final int SnShareError = 40006;								// error share message to social network
    
    // crm
    public static final int CrmAlreadyConnected = 80001;               // N/A FOR IOS
    
    
    
    /** ----- Methods  ------*/
    
    private static String stringFromResID(int aResID) {
    	return CommonUtil.stringFromResID(aResID);
    }
    
    /**
     * get title for a message code
     * @param aMessageCode
     * @return
     */
    public static String titleForCode(int aMessageCode) {
    	
    	switch(aMessageCode) {
    		
    	case NoUpdateForAllSalesTriggers:
    		return stringFromResID(R.string.have_trouble_seeing_updates);
    		
    	case NoEventForTheAllSelectedFunctionals:
        	return stringFromResID(R.string.have_trouble_seeing_updates);
    		
    	case NoUpdateForNewCompany:
    		return stringFromResID(R.string.company_update_available_soon);
    		
    	case NoEventForNewCompany:
    		return stringFromResID(R.string.company_event_available_soon);
    	}
    	
    	return null;
    }
    
    /**
     * get action for a message code
     * @param aMessageCode
     * @return
     */
    public static String actionForCode(int aMessageCode) {
    	 
    	switch(aMessageCode) {
    	case NoUpdateNoFollowedCompanies:
    	case NoUpdateForLessFollowedCompanies:
    		return stringFromResID(R.string.follow_companies);
    		
    	case NoUpdateForMoreFollowedCompanies:
    		return stringFromResID(R.string.follow_more_companies);
    		
    	case NoUpdateForAllSalesTriggers:
    		return stringFromResID(R.string.select_sales_triggers_short);
    		
    	case NoUpdateForTheCompany:
    		return stringFromResID(R.string.check_all_news);
    		
    	case NoUpdateTheUnfollowedCompany:
    		return stringFromResID(R.string.follow);
    		
    	case CompanyGradeCNoUpdate:
    		return stringFromResID(R.string.check_out_profile);
    		
    	case NoEventNoFollowedCompanies:
        case NoEventForLessFollowedCompanies:
        	return stringFromResID(R.string.follow_companies);
        	
        case NoEventForLessFollowedContacts:
        case NoEventForMoreFollowedContacts:
        	return stringFromResID(R.string.add_people_to_follow);
        	
        case NoEventForTheContact:
        	return stringFromResID(R.string.check_out_profile);
        	
        case NoEventForTheAllSelectedFunctionals:
        	return stringFromResID(R.string.select_functional_roles_short);
        	
        case PeopleNotFollowed:
        	return stringFromResID(R.string.follow);
        	
        case CompanyNotFollowed:
        	return stringFromResID(R.string.follow);
        	
        case CompanyGradeBNoUpdate:
        	return stringFromResID(R.string.unfollow);
        	
        case CompanyIsClosed:
        	return stringFromResID(R.string.unfollow);
        	
        case CompanyIsMergedOrAquired:
        	return stringFromResID(R.string.unfollow);
    	
	    case NoUpdateForNewCompany:
	    case NoEventForNewCompany:
	    	return stringFromResID(R.string.add_company_website);
		}
    	return null;
    }
    
    
    /**
     * get message for a message code
     * @param aMessageCode
     * @return
     */
    public static String messageForCode(int aMessageCode) {
    	Log.v("silen", "aMessageCode = " + aMessageCode);
    	switch(aMessageCode) {
    	case NoUpdateNoFollowedCompanies:
    	case NoUpdateForLessFollowedCompanies:
    		return stringFromResID(R.string.no_updates_no_followed_companies);
    		
    	case NoUpdateForMoreFollowedCompanies:
    		return stringFromResID(R.string.no_updates_for_followed_companies);
    		
    	case NoUpdateForAllSalesTriggers:
    		return stringFromResID(R.string.select_sales_triggers_long);
    		
    	case NoUpdateForTheCompany:
    		return stringFromResID(R.string.no_trigger_found_for_the_company);
    		
    	case NoUpdateTheSaleTrigger:
    		return stringFromResID(R.string.no_available_updates);
    	
    	case NoUpdateTheUnfollowedCompany:
    		return stringFromResID(R.string.follow_this_company);
    		
    	case CompanyGradeCNoUpdate:
    		return stringFromResID(R.string.grade_c_company_available_soon);
    		
    	case NoUpdateTheUnavailableCompany:
    		return stringFromResID(R.string.no_updates_180days_for_company);
    		
    	case NoUpdateForNewCompany:
    		return stringFromResID(R.string.promte_add_company_website);
    		
    	case NoEventNoFollowedCompanies:
        case NoEventForLessFollowedCompanies:
        	return stringFromResID(R.string.no_updates_no_followed_companies);
        	
        case NoEventForMoreFollowedCompanies:
        	return stringFromResID(R.string.no_followed_companies_events_found);
        	
        case NoEventForTheCompany:
        	return stringFromResID(R.string.no_company_events_found);
        	
        case NoEventForLessFollowedContacts:
        case NoEventForMoreFollowedContacts:
        	return stringFromResID(R.string.no_updates_for_followed_people);
 
        case NoEventForTheContact:
        	return stringFromResID(R.string.no_person_updates_found);
        	
        case NoEventForTheAllSelectedFunctionals:
        	return stringFromResID(R.string.select_functional_roles_long);
        	
        case NoEventForTheFunctional:
        case NoEventForTheCategory:
        	return stringFromResID(R.string.no_available_updates);
        
        case NoEventForNewCompany:
        	return stringFromResID(R.string.promte_add_company_website);
  
        case PeopleNotFollowed:
        	return stringFromResID(R.string.no_longer_followed_person);
        	
        case CompanyNotFollowed:
        	return stringFromResID(R.string.no_longer_followed_company);
        	
        case CompanyGradeBNoUpdate:
        	return stringFromResID(R.string.not_updates_for_the_grade_b_company);
        	
        case CompanyIsClosed:
        	return stringFromResID(R.string.no_longer_operated_company);
        	
        case CompanyIsMergedOrAquired: //NOTE: if there is an extra info (company which acquired this company), you should use 'company_merged_format' string
        	return stringFromResID(R.string.company_merged_or_acquired);
        	
        //--------------messages for other codes ----------
        case AuthError:
        	return stringFromResID(R.string.auth_error);
        	
        case RegEmailExists:
        case RegEmailExistsAccountNotConfirmed:
        	return stringFromResID(R.string.auth_email_exists);
        	
        case RegEmailInvalid:
        	return stringFromResID(R.string.reg_email_invalid);
        	
        case RegWorkEmailNotEqualAdditional:
        	return stringFromResID(R.string.reg_email_in_use);
        	
        case RegError:
        	return stringFromResID(R.string.reg_error);
        	
        case BillingCantFollowMoreCompany:
        	return stringFromResID(R.string.api_message_cant_follow_more_company);
        	
        case BillingCantFollowMorePeople:
        	return stringFromResID(R.string.upgrade_to_follow_more_people);
        	
        case BillingCantAccessUpdateNeedPay:
        	return stringFromResID(R.string.upgrade_to_access_info);
        	
        case BillingCantAccessUpdateNeedUpgrade:
        	return stringFromResID(R.string.upgrade_to_access_info);
        	
        case BillingExceededQuota:
        	return stringFromResID(R.string.upgrade_to_enable_feature);
        	
        case BillingCantSaveAnyMoreUpdate:
        	return stringFromResID(R.string.upgrade_to_see_more_update);
        	
        case BillingFreeCantFollowGradeC:
        	return stringFromResID(R.string.api_message_free_plan_cant_follow_c_company);
        	
        case CompanyAlreadyFollowed:
        	return stringFromResID(R.string.api_message_already_following_the_company);
        	
        case CompanyCantFollowGradeB:
        	return stringFromResID(R.string.api_message_grade_b_company_cant_be_followed);
        	
        case CompanyFollowedGradC:
        	return stringFromResID(R.string.api_message_grad_c_company_followed);
        	
        case CompanyCantUnfollowLinkedByCRM:
        	return stringFromResID(R.string.can_not_unfollow_the_company_linked_by_crm);
        	
        case PeopleCantUnfollowLinkedByCRM:
        	return stringFromResID(R.string.can_not_unfollow_the_people_linked_by_crm);
        	
        case MemberProfileLoadError:
        	return stringFromResID(R.string.unable_retieve_profile);
        	
        case PeopleNotFound:
        	return stringFromResID(R.string.person_not_exists);
        	
        case SnShareUpdateError:
        case SnShareEventError:
        	return stringFromResID(R.string.share_error);
        	
        case SnLinkedInAccountDisconnected:
        	return stringFromResID(R.string.plz_link_linkedin_account);
        	
        case SnCantGetUserInfo:
        	return stringFromResID(R.string.cant_get_user_info);
        	
        case SnSaleforceCantAuth:
        	return stringFromResID(R.string.salesforce_cant_auth);
    	}
    	
    	return null;
    }  
}
