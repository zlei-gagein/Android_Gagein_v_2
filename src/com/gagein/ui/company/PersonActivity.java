package com.gagein.ui.company;

import java.util.List;

import org.json.JSONObject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.gagein.R;
import com.gagein.http.APIParser;
import com.gagein.model.Person;
import com.gagein.model.SocialProfile;
import com.gagein.ui.main.BaseActivity;
import com.gagein.util.ActivityHelper;
import com.gagein.util.Constant;
import com.gagein.util.Log;

public class PersonActivity extends BaseActivity {
	
	private long personId;
	private Person person;
	private ImageView personLog;
	private TextView personName;
	private TextView jobTitle;
	private TextView address;
	private TextView description;
	private TextView more;
	private LinearLayout socialLayout;
	private LinearLayout social;
	private LinearLayout descriptionLayout;
	private LinearLayout information;
	private LinearLayout informationLayout;
	private Button employmentHistory;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_person);
		
		doInit();
	}
	
	@Override
	protected void initData() {
		super.initData();
		setLeftImageButton(R.drawable.back_arrow);
		
		personId = getIntent().getLongExtra(Constant.PERSONID, 0);
		getPerson();
	}
	
	@Override
	protected void initView() {
		super.initView();
		
		personLog = (ImageView) findViewById(R.id.personLog);
		personName = (TextView) findViewById(R.id.personName);
		jobTitle = (TextView) findViewById(R.id.jobTitle);
		address = (TextView) findViewById(R.id.address);
		
		socialLayout = (LinearLayout) findViewById(R.id.socialLayout);
		social = (LinearLayout) findViewById(R.id.social);
		descriptionLayout = (LinearLayout) findViewById(R.id.descriptionLayout);
		information = (LinearLayout) findViewById(R.id.information);
		informationLayout = (LinearLayout) findViewById(R.id.informationLayout);
		employmentHistory = (Button) findViewById(R.id.employmentHistory);
		description = (TextView) findViewById(R.id.description);
		more = (TextView) findViewById(R.id.more);
	}
	
	@Override
	protected void setOnClickListener() {
		super.setOnClickListener();
		employmentHistory.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		
		if (v == leftImageBtn) {
			
			finish();
			
		} else if (v == employmentHistory){
			
			Intent intent = new Intent();
			intent.setClass(mContext, EmploymentHistoryActivity.class);
			Constant.employmentHistory = person.prevCompanies;
			startActivity(intent);
			
		}
	}
	
	@Override
	protected void setData() {
		super.setData();
		
		setTitle(person.name);
		setHeadData();
		
		setSocialProfiles(socialLayout, social);
		setDescription(descriptionLayout, description, more);
		setInformation(information, informationLayout);
		
	}
	
	private void setHeadData() {
		personLog.setImageResource(R.drawable.logo_person_default);
		loadImage(person.photoPath, personLog);
		Log.v("silen", "person.photoPath = " + person.photoPath);
		personName.setText(person.name);
		jobTitle.setText(person.orgTitle);
		address.setText(person.address);
	}
	
	private void getPerson() {
		showLoadingDialog();
		mApiHttp.getPersonOverview(personId, true, new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject jsonObject) {
						APIParser parser = new APIParser(jsonObject);
						if (parser.isOK()) {
							person = parser.parseGetPersonOverview();
							
							setData();
						} else {
							alertMessageForParser(parser);
						}
						dismissLoadingDialog();
					}
					
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						showConnectionError();
					}
				});
	}
	
	private void setSocialProfiles(LinearLayout layout, LinearLayout social) {
		if (null != person.socialProfiles) {
			List<SocialProfile> listSocialProfiles = person.socialProfiles;
			if (listSocialProfiles.size() > Constant.ZERO) {
				for (int i = 0; i < listSocialProfiles.size(); i++) {
					View view = LayoutInflater.from(mContext).inflate(R.layout.social_image, null);
					final SocialProfile socialProfile = listSocialProfiles.get(i);
					ImageView image = (ImageView) view.findViewById(R.id.image);
					String type = socialProfile.capitalizedType();
					setSocialImageView(type, image);
					Log.v("silen", "typeStr = " + type);
					image.setOnClickListener(new OnClickListener() {
						
						@SuppressWarnings("unused")
						@Override
						public void onClick(View v) {
							
							if (!TextUtils.isEmpty(socialProfile.url)) {
								if (true) {
									ActivityHelper.startWebActivity(socialProfile.url, mContext);
								} else {
									Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(socialProfile.url));
									mContext.startActivity(intent);
								}
							}
						}
					});
					social.addView(view);
				}
				layout.setVisibility(View.VISIBLE);
			}
		}
	}
	
	private void setSocialImageView(String type, ImageView image) {
		if (type.trim().equalsIgnoreCase(Constant.FACEBOOK)) {
			image.setBackgroundResource(R.drawable.share_facebook);
		} else if (type.trim().equalsIgnoreCase(Constant.TWITTER)) {
			image.setBackgroundResource(R.drawable.share_twitter);
		} else if (type.trim().equalsIgnoreCase(Constant.YOUTUBE)) {
			image.setBackgroundResource(R.drawable.youtube);
		} else if (type.trim().equalsIgnoreCase(Constant.HOOVERS)) {
			image.setBackgroundResource(R.drawable.hoovers);
		} else if (type.trim().equalsIgnoreCase(Constant.YAHOO)) {
			image.setBackgroundResource(R.drawable.yahoo);
		} else if (type.trim().equalsIgnoreCase(Constant.CRUNCHBASE)) {
			image.setBackgroundResource(R.drawable.crunchbase);
		} else if (type.trim().equalsIgnoreCase(Constant.YAMMER)) {
			image.setBackgroundResource(R.drawable.yammer);
		} else if (type.trim().equalsIgnoreCase(Constant.LINKEDIN)) {
			image.setBackgroundResource(R.drawable.linkedin);
		} else if (type.trim().equalsIgnoreCase(Constant.SLIDESHARE)) {
			image.setBackgroundResource(R.drawable.salesforce_button);
		} else if (type.trim().equalsIgnoreCase(Constant.SALESFORCE)) {
			image.setBackgroundResource(R.drawable.salesforce_button);
		}  
	}
	
	private void setDescription(LinearLayout layout, final TextView description, final TextView more) {
		if (TextUtils.isEmpty(person.summary)) return;
		layout.setVisibility(View.VISIBLE);
		description.setText(person.summary);
		description.post(new Runnable() {  
            @Override  
            public void run() {  
                Log.v("silen", "description.getLineCount() = " + description.getLineCount());
                int position = description.getLineCount();
                more.setVisibility(position >= 5 ? View.VISIBLE : View.GONE);
            }  
		});
		more.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				more.setVisibility(View.GONE);
				description.setMaxLines(Integer.MAX_VALUE);
			}
		});
	}
	
	private void setInformation(LinearLayout layout, LinearLayout wholeLayout) {
		String jobLevel = person.jobLevel;
		List<String> schools = person.schools;
		setInformationValue(jobLevel, R.string.job_level, R.layout.item_about_detail, layout);
		if (!TextUtils.isEmpty(jobLevel)) {
			Log.v("silen", "jobLevel = " + jobLevel);
		}
		if (!TextUtils.isEmpty(jobLevel) || (null != schools && schools.size() > 0)) wholeLayout.setVisibility(View.VISIBLE);
		if (null == schools || schools.size() == 0) return;
		String school = "";
		for (int i = 0; i < schools.size(); i++) {//only display one school
			if (i == 0) school = school + schools.get(i);
		}
		setInformationValue(school, R.string.u_schools, R.layout.item_about_detail, layout);
	}
	
	private void setInformationValue(String str, int strId, int layoutId, LinearLayout layout) {
		if (!TextUtils.isEmpty(str)) {
			View view = LayoutInflater.from(mContext).inflate(layoutId, null);
			TextView textview_name = (TextView) view.findViewById(R.id.name);
			TextView textview_value = (TextView) view.findViewById(R.id.value);
			textview_name.setText(mContext.getResources().getString(strId));
			textview_value.setText(str);
			layout.addView(view);
			if (str.equals(Constant.ZERO)) {
				layout.setVisibility(View.GONE);
			} else {
				layout.setVisibility(View.VISIBLE);
			}
		}
	}

}
