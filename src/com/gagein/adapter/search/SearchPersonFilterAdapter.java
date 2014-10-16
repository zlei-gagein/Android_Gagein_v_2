package com.gagein.adapter.search;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gagein.R;
import com.gagein.model.SearchPerson;
import com.gagein.model.SocialProfile;
import com.gagein.util.ActivityHelper;
import com.gagein.util.CommonUtil;
import com.gagein.util.Constant;
import com.gagein.util.Log;

public class SearchPersonFilterAdapter extends BaseAdapter {
	
	private Context mContext;
	private List<SearchPerson> persons;
	public Boolean edit = false;
	
	public SearchPersonFilterAdapter(Context mContext, List<SearchPerson> persons) {
		super();
		this.mContext = mContext;
		this.persons = persons;
	}

	@Override
	public int getCount() {
		return persons.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_search_person, null);
			viewHolder.image = (ImageView) convertView.findViewById(R.id.image);
			viewHolder.name = (TextView) convertView.findViewById(R.id.name);
			viewHolder.search = (ImageView) convertView.findViewById(R.id.search);
			viewHolder.jobTitle = (TextView) convertView.findViewById(R.id.jobTitle);
			viewHolder.socialProfilesLayout = (LinearLayout) convertView.findViewById(R.id.socialProfiles);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		final SearchPerson person = persons.get(position);
		viewHolder.image.setImageResource(R.drawable.logo_person_default);
		CommonUtil.loadImage(person.photoPath, viewHolder.image);
		viewHolder.name.setText(person.fullName);
		viewHolder.jobTitle.setText(person.orgTitle);
		
		List<SocialProfile> listSocialProfiles = person.socialProfiles;
		if (listSocialProfiles.size() > Constant.ZERO) {
			viewHolder.socialProfilesLayout.removeAllViews();
			for (int i = 0; i < listSocialProfiles.size(); i++) {
				View view = LayoutInflater.from(mContext).inflate(R.layout.social_image, null);
				final SocialProfile socialProfile = listSocialProfiles.get(i);
				ImageView image = (ImageView) view.findViewById(R.id.image);
				String type = socialProfile.capitalizedType();
				if (type.equalsIgnoreCase("Google plus")) break;
				setSocialImageView(type, image);
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
				viewHolder.socialProfilesLayout.addView(view);
			}
		}
		
		viewHolder.search.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				ActivityHelper.startWebActivity(CommonUtil.jointPersonNameAndCompanyNameAndJobTitle(person.fullName, person.company.name, person.orgTitle), mContext);
			}
		});
		return convertView;
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
	
	public final class ViewHolder {
		public ImageView image;
		public TextView name;
		public TextView jobTitle;
		public ImageView search;
		public LinearLayout socialProfilesLayout;
	}

}
