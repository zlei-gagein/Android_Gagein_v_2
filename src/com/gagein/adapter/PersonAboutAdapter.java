package com.gagein.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.emilsjolander.components.stickylistheaders.StickyListHeadersAdapter;
import com.gagein.R;
import com.gagein.model.Person;
import com.gagein.model.SocialProfile;
import com.gagein.ui.company.EmploymentHistoryActivity;
import com.gagein.util.ActivityHelper;
import com.gagein.util.Constant;
import com.gagein.util.Log;

public class PersonAboutAdapter extends BaseAdapter implements StickyListHeadersAdapter{
	
	private Context mContext;
	private Person mPerson;
	private View sectionView;
	
	public PersonAboutAdapter(Context mContext, Person person, View sectionView) {
		super();
		this.mContext = mContext;
		this.mPerson = person;
		this.sectionView = sectionView;
	}
	
	@Override
	public int getCount() {
		return 1;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public final class ViewHolder {
		public TextView name;
		public TextView description;
		public TextView more;
		public LinearLayout socialLayout;
		public LinearLayout social;
		public LinearLayout descriptionLayout;
		public LinearLayout information;
		public LinearLayout informationLayout;
		public LinearLayout employmentHistoryLayout;
		public Button subsidiaries;
		public Button divisions;
		public Button website;
		public Button employmentHistory;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.person_about, null);
			viewHolder.socialLayout = (LinearLayout) convertView.findViewById(R.id.socialLayout);
			viewHolder.social = (LinearLayout) convertView.findViewById(R.id.social);
			viewHolder.descriptionLayout = (LinearLayout) convertView.findViewById(R.id.descriptionLayout);
			viewHolder.information = (LinearLayout) convertView.findViewById(R.id.information);
			viewHolder.informationLayout = (LinearLayout) convertView.findViewById(R.id.informationLayout);
			viewHolder.employmentHistoryLayout = (LinearLayout) convertView.findViewById(R.id.employmentHistoryLayout);
			viewHolder.employmentHistory = (Button) convertView.findViewById(R.id.employmentHistory);
			viewHolder.description = (TextView) convertView.findViewById(R.id.description);
			viewHolder.more = (TextView) convertView.findViewById(R.id.more);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		setSocialProfiles(viewHolder.socialLayout, viewHolder.social);
		setDescription(viewHolder.descriptionLayout, viewHolder.description, viewHolder.more);
		setInformation(viewHolder.information, viewHolder.informationLayout);
		
		viewHolder.employmentHistory.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(mContext, EmploymentHistoryActivity.class);
				Constant.employmentHistory = mPerson.prevCompanies;
				mContext.startActivity(intent);
			}
		});
		return convertView;
	}
	
	private void setSocialProfiles(LinearLayout layout, LinearLayout social) {
		if (null != mPerson.socialProfiles) {
			List<SocialProfile> listSocialProfiles = mPerson.socialProfiles;
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
		if (TextUtils.isEmpty(mPerson.summary)) return;
		layout.setVisibility(View.VISIBLE);
		description.setText(mPerson.summary);
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
		String jobLevel = mPerson.jobLevel;
		List<String> schools = mPerson.schools;
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

	@Override
	public long getHeaderId(int arg0) {
		return 0;
	}

	@SuppressLint("NewApi")
	@Override
	public View getHeaderView(int arg0, View convertView, ViewGroup parent) {
		return sectionView;
	}

}
