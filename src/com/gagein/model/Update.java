package com.gagein.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.text.TextUtils;

public class Update extends DataModel implements Serializable{

	private static final long serialVersionUID = 1L;

	/** ------ member variables ----- */
	public String headline = "";
	public String content = "";
	public String contentInDetail = "";
	public String textview = "";
	public String url = "";
	public String note = "";

	public Company company = null;
	public Boolean saved;
	public List<Tag> tags = null;
	public String fromSource = "";
	public long date = 0;
	public String dateStr;
	public int type = 0;
	public List<String> pictures = null; // when parsing update detail
	public Boolean liked; // if the user have read this update
	public Boolean hasBeenRead = false;
	public long newsSimilarID = 0;
	public int newsSimilarCount = 0;
	public String linkedInSignal = "";
	public String twitterTweets = "";

	public List<Company> mentionedCompanies = null;
	public int mentionedComIndex = 0; // for salesGraph display
	public List<Agent> agents = null;
	public String newsPicURL = "";
	public String newsDisplayURL = "";
	public long newsId = 0;

	public long orgID;
	
	public String org_logo_path = "";
	public Boolean irrelevant;

	/** ----- methods ----- */
	@Override
	public void parseData(JSONObject jsonObject) {
		if (jsonObject == null)
			return;

		this.date = jsonObject.optLong("date");
		this.dateStr = jsonObject.optString("date_str");
		this.fromSource = jsonObject.optString("from_source");
		this.content = jsonObject.optString("news_content");
		this.contentInDetail = jsonObject.optString("content"); // when parsing
		// update detail
		this.headline = jsonObject.optString("news_headline");
		this.url = jsonObject.optString("news_url");
		this.newsId = jsonObject.optLong("newsid");
		this.saved = jsonObject.optInt("saved") == 1 ? true : false;
		this.type = jsonObject.optInt("news_type");
		this.textview = jsonObject.optString("textview");
		this.linkedInSignal = jsonObject.optString("linkedin_signal");
		this.twitterTweets = jsonObject.optString("tweet_tweets");
		this.liked = jsonObject.optBoolean("liked");
		this.newsSimilarID = jsonObject.optLong("news_similarid");
		this.newsSimilarCount = jsonObject.optInt("news_similar_count");
		this.newsPicURL = jsonObject.optString("news_pic_url");
		this.newsDisplayURL = jsonObject.optString("news_display_url");
		this.hasBeenRead = jsonObject.optInt("readed") == 1 ? true : false;
		
		this.orgID = jsonObject.optLong("orgid");
		this.org_logo_path = jsonObject.optString("org_logo_path");

		// pictures
		JSONArray picturesData = jsonObject.optJSONArray("pictures");
		int length = (picturesData != null) ? picturesData.length() : -1;
		if (length > 0) {
			this.pictures = new ArrayList<String>();
			for (int i = 0; i < length; i++) {
				String picture = picturesData.optString(i);
				this.pictures.add(picture);
			}
		}

		// memtioned companies
		JSONArray mentionedComsData = jsonObject
				.optJSONArray("mentioned_companies");
		length = (mentionedComsData != null) ? mentionedComsData.length() : -1;
		if (length > 0) {
			this.mentionedCompanies = new ArrayList<Company>();
			for (int i = 0; i < length; i++) {
				JSONObject comData = mentionedComsData.optJSONObject(i);
				if (comData != null) {
					Company company = new Company();
					company.parseData(comData);
					this.mentionedCompanies.add(company);
				}
			}
		}

		// agents
		JSONArray agentsData = jsonObject.optJSONArray("agents");
		length = (agentsData != null) ? agentsData.length() : -1;
		if (length > 0) {
			this.agents = new ArrayList<Agent>();
			for (int i = 0; i < length; i++) {
				JSONObject agentData = agentsData.optJSONObject(i);
				if (agentData != null) {
					Agent agent = new Agent();
					agent.parseData(agentData);
					this.agents.add(agent);
				}
			}
		}

		// company
		this.company = new Company();
		this.company.parseData(jsonObject);
		this.irrelevant = jsonObject.optInt("irrelevant_flagged") == 1 ? true : false;

	}
	
	public String shortDateString() {
		if (!TextUtils.isEmpty(dateStr)) {
			String[] strings = dateStr.split(",");
			if (strings.length > 0) {
				return strings[0];
			}
		}
		
		return "";
	}
	
	public String displayURL() {
		return (newsDisplayURL != null) && (!newsDisplayURL.trim().equals(""))? newsDisplayURL : url;
	}
	
}


