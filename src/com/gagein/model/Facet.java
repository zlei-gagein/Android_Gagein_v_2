package com.gagein.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.gagein.util.Log;

public class Facet extends DataModel {

	public List<FacetItem> jobLevels = new ArrayList<FacetItem>();
	public List<FacetItem> functionalRoles = new ArrayList<FacetItem>();
	public List<FacetItem> linkedProfiles = new ArrayList<FacetItem>();
	public List<FacetItemIndustry> industries = new ArrayList<FacetItemIndustry>();
	public List<FacetItemIndustry> industryFacets = new ArrayList<FacetItemIndustry>();
	private int length;
	
	@Override
	public void parseData(JSONObject aJSONObject) {
		if (aJSONObject == null) return;
		super.parseData(aJSONObject);
		
		// job levels
		JSONArray jobLevelsData = aJSONObject.optJSONArray("job_level");
		length = (jobLevelsData != null) ? jobLevelsData.length() : 0;
		length += 1;
		for (int i = 0; i < length; i ++) {
			if (i == 0) {
				FacetItem item = new FacetItem();
				item.id = 0;
				item.name = "All";
				jobLevels.add(item);
			} else {
				JSONObject jobLevelObj = jobLevelsData.optJSONObject(i-1);
				FacetItem item = new FacetItem();
				item.parseData(jobLevelObj);
				jobLevels.add(item);
			}
		}
		
		// functional roles
		JSONArray rolesData = aJSONObject.optJSONArray("functional_role");
		length = (rolesData != null) ? rolesData.length() : 0;
		length += 1;
		for (int i = 0; i < length; i ++) {
			if (i == 0) {
				FacetItem item = new FacetItem();
				item.id = 0;
				item.name = "All";
				functionalRoles.add(item);
			} else {
				JSONObject roleObj = rolesData.optJSONObject(i-1);
				FacetItem item = new FacetItem();
				item.parseData(roleObj);
				functionalRoles.add(item);
			}
		}
		
		// linked profiles
		JSONArray profilesData = aJSONObject.optJSONArray("linked_profile");
		length = (profilesData != null) ? profilesData.length() : 0;
		length += 1;
		for (int i = 0; i < length; i ++) {
			if (i == 0) {
				FacetItem item = new FacetItem();
				item.id = 0;
				item.name = "All";
				linkedProfiles.add(item);
			} else {
				JSONObject profileObj = profilesData.optJSONObject(i-1);
				FacetItem item = new FacetItem();
				item.parseData(profileObj);
				linkedProfiles.add(item);
			}
		}
		
		//Industry
		JSONArray industryData = aJSONObject.optJSONArray("Industry");
		length = (industryData != null) ? industryData.length() : 0;
		
		for (int i = 0; i < length; i ++) {
			JSONObject industryObj = industryData.optJSONObject(i);
			FacetItemIndustry item = new FacetItemIndustry();
			item.parseData(industryObj);
			industries.add(item);
		}
		
		//Industry
		JSONArray industryFacetData = aJSONObject.optJSONArray("org_industry_facet");
		length = (industryFacetData != null) ? industryFacetData.length() : 0;
		
		for (int i = 0; i < length; i ++) {
			JSONObject industryObj = industryFacetData.optJSONObject(i);
			FacetItemIndustry item = new FacetItemIndustry();
			item.parseData(industryObj);
			industryFacets.add(item);
		}
		
	}
}
