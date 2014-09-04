package com.gagein.model;

import org.json.JSONObject;

public class ModelHelper {

    public static final int kGGSnTypeFacebook = 1;
    public static final int kGGSnTypeLinkedIn = 2;
	public static final int kGGSnTypeTwitter = 3;
	public static final int kGGSnTypeSalesforce = 101;
	public static final int kGGSnTypeYammer = 102;
	
	public static long optLongFromJSONObject(JSONObject aJSONObject, String aOuterKey, String aInnerKey) {
		if (aJSONObject == null || aOuterKey == null || aInnerKey == null) return 0;
		if (aOuterKey.length() <= 0 || aInnerKey.length() <= 0) return 0;
		
		JSONObject wrapObj = aJSONObject.optJSONObject(aOuterKey);
		if (wrapObj == null) return 0;
		
		return wrapObj.optLong(aInnerKey);
	}
	
	public static int optIntFromJSONObject(JSONObject aJSONObject, String aOuterKey, String aInnerKey) {
		if (aJSONObject == null || aOuterKey == null || aInnerKey == null) return 0;
		if (aOuterKey.length() <= 0 || aInnerKey.length() <= 0) return 0;
		
		JSONObject wrapObj = aJSONObject.optJSONObject(aOuterKey);
		if (wrapObj == null) return 0;
		
		return wrapObj.optInt(aInnerKey);
	}
	
	public static Boolean optBooleanFromJSONObject(JSONObject aJSONObject, String aOuterKey, String aInnerKey) {
		if (aJSONObject == null || aOuterKey == null || aInnerKey == null) return false;
		if (aOuterKey.length() <= 0 || aInnerKey.length() <= 0) return false;
		
		JSONObject wrapObj = aJSONObject.optJSONObject(aOuterKey);
		if (wrapObj == null) return false;
		
		return wrapObj.optBoolean(aInnerKey);
	}
	
	public static double optDoubleFromJSONObject(JSONObject aJSONObject, String aOuterKey, String aInnerKey) {
		if (aJSONObject == null || aOuterKey == null || aInnerKey == null) return 0;
		if (aOuterKey.length() <= 0 || aInnerKey.length() <= 0) return 0;
		
		JSONObject wrapObj = aJSONObject.optJSONObject(aOuterKey);
		if (wrapObj == null) return 0;
		
		return wrapObj.optDouble(aInnerKey);
	}
	
	public static String optStringFromJSONObject(JSONObject aJSONObject, String aOuterKey, String aInnerKey) {
		if (aJSONObject == null || aOuterKey == null || aInnerKey == null) return null;
		if (aOuterKey.length() <= 0 || aInnerKey.length() <= 0) return null;
		
		JSONObject wrapObj = aJSONObject.optJSONObject(aOuterKey);
		if (wrapObj == null) return null;
		
		return wrapObj.optString(aInnerKey);
	}
}
