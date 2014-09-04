package com.gagein.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gagein.R;
import com.gagein.model.Company;
import com.gagein.model.SocialProfile;
import com.gagein.model.Update;
import com.gagein.ui.company.DivisionActivity;
import com.gagein.ui.company.JointVenturesActivity;
import com.gagein.ui.company.ParentsActivity;
import com.gagein.ui.company.SecFilingsActivity;
import com.gagein.ui.company.SubsidiaryActivity;
import com.gagein.ui.main.ImageViewActivity;
import com.gagein.ui.main.WebPageActivity;
import com.gagein.util.ActivityHelper;
import com.gagein.util.CommonUtil;
import com.gagein.util.Constant;
import com.gagein.util.Log;

public class CompanyAboutAdapter extends BaseAdapter{
	
	private Context mContext;
	private Company mCompany;
	private List<Update> filings;
	
	public CompanyAboutAdapter(Context mContext, Company mCompany, List<Update> filings) {
		super();
		this.mContext = mContext;
		this.mCompany = mCompany;
		this.filings = filings;
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
		public TextView revenuesText;
		public TextView phone;
		public TextView fax;
		public TextView address;
		public ImageView button;
		public ImageView revenuesImage;
		public ImageView map;
		public LinearLayout socialLayout;
		public LinearLayout social;
		public LinearLayout descriptionLayout;
		public LinearLayout information;
		public LinearLayout jointVenturesLayout;
		public LinearLayout parentsLayout;
		public LinearLayout subsidiariesLayout;
		public LinearLayout divisionsLayout;
		public LinearLayout filingsLayout;
		public LinearLayout revenuesLayout;
		public LinearLayout contactLayout;
		public LinearLayout phoneLayout;
		public LinearLayout faxLayout;
		public LinearLayout addressLayout;
		public LinearLayout websiteLayout;
		public Button jointVentures;
		public Button parents;
		public Button subsidiaries;
		public Button divisions;
		public Button filings;
		public Button website;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = new ViewHolder();
		convertView = LayoutInflater.from(mContext).inflate(R.layout.company_about, null);
		viewHolder.socialLayout = (LinearLayout) convertView.findViewById(R.id.socialLayout);
		viewHolder.social = (LinearLayout) convertView.findViewById(R.id.social);
		viewHolder.descriptionLayout = (LinearLayout) convertView.findViewById(R.id.descriptionLayout);
		viewHolder.information = (LinearLayout) convertView.findViewById(R.id.information);
		viewHolder.jointVenturesLayout = (LinearLayout) convertView.findViewById(R.id.jointVenturesLayout);
		viewHolder.parentsLayout = (LinearLayout) convertView.findViewById(R.id.parentsLayout);
		viewHolder.subsidiariesLayout = (LinearLayout) convertView.findViewById(R.id.subsidiariesLayout);
		viewHolder.jointVentures = (Button) convertView.findViewById(R.id.jointVentures);
		viewHolder.parents = (Button) convertView.findViewById(R.id.parents);
		viewHolder.subsidiaries = (Button) convertView.findViewById(R.id.subsidiaries);
		viewHolder.website = (Button) convertView.findViewById(R.id.website);
		viewHolder.divisionsLayout = (LinearLayout) convertView.findViewById(R.id.divisionsLayout);
		viewHolder.filingsLayout = (LinearLayout) convertView.findViewById(R.id.filingsLayout);
		viewHolder.revenuesLayout = (LinearLayout) convertView.findViewById(R.id.revenuesLayout);
		viewHolder.contactLayout = (LinearLayout) convertView.findViewById(R.id.contactLayout);
		viewHolder.phoneLayout = (LinearLayout) convertView.findViewById(R.id.phoneLayout);
		viewHolder.faxLayout = (LinearLayout) convertView.findViewById(R.id.faxLayout);
		viewHolder.addressLayout = (LinearLayout) convertView.findViewById(R.id.addressLayout);
		viewHolder.websiteLayout = (LinearLayout) convertView.findViewById(R.id.websiteLayout);
		viewHolder.divisions = (Button) convertView.findViewById(R.id.divisions);
		viewHolder.filings = (Button) convertView.findViewById(R.id.filings);
		viewHolder.description = (TextView) convertView.findViewById(R.id.description);
		viewHolder.more = (TextView) convertView.findViewById(R.id.more);
		viewHolder.revenuesText = (TextView) convertView.findViewById(R.id.revenuesText);
		viewHolder.phone = (TextView) convertView.findViewById(R.id.phone);
		viewHolder.fax = (TextView) convertView.findViewById(R.id.fax);
		viewHolder.address = (TextView) convertView.findViewById(R.id.address);
		viewHolder.revenuesImage = (ImageView) convertView.findViewById(R.id.revenuesImage);
		viewHolder.map = (ImageView) convertView.findViewById(R.id.map);
		convertView.setTag(viewHolder);
		
		setSocialProfiles(viewHolder.socialLayout, viewHolder.social);
		setDescription(viewHolder.descriptionLayout, viewHolder.description, viewHolder.more);
		setInformation(viewHolder.information);
		setJointVentures(viewHolder.jointVenturesLayout, viewHolder.jointVentures);
		setParents(viewHolder.parentsLayout, viewHolder.parents);
		setSubsidiaries(viewHolder.subsidiariesLayout, viewHolder.subsidiaries);
		setDivisions(viewHolder.divisionsLayout, viewHolder.divisions);
		setRevenues(viewHolder.revenuesLayout, viewHolder.revenuesText, viewHolder.revenuesImage);
		setContact(viewHolder.contactLayout, viewHolder.phoneLayout, viewHolder.faxLayout, viewHolder.addressLayout, 
				viewHolder.phone, viewHolder.fax, viewHolder.address);
		setMap(viewHolder.map);
		setWebsite(viewHolder.websiteLayout, viewHolder.website);
		
		if (null != filings && filings.size() > 0) viewHolder.filingsLayout.setVisibility(View.VISIBLE);
		viewHolder.filings.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				Constant.SECFILINGS = filings;
				intent.setClass(mContext, SecFilingsActivity.class);
				mContext.startActivity(intent);
			}
		});
		return convertView;
	}
	
	private void setJointVentures(LinearLayout layout, Button button) {
		List<Company> joinVentures = mCompany.joinVentures;
		if (null == joinVentures || joinVentures.size() == 0) return;
		layout.setVisibility(View.VISIBLE);
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Intent intent = new Intent();
				intent.putExtra(Constant.COMPANY, mCompany);
				intent.setClass(mContext, JointVenturesActivity.class);
				mContext.startActivity(intent);
			}
		});
	}
	
	private void setParents(LinearLayout layout, Button button) {
		List<Company> parents = mCompany.parents;
		if (null == parents || parents.size() == 0) return;
		layout.setVisibility(View.VISIBLE);
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra(Constant.COMPANY, mCompany);
				intent.setClass(mContext, ParentsActivity.class);
				mContext.startActivity(intent);
			}
		});
	}
	
	public void setFilingsLayout(LinearLayout layout, List<Update> filings) {
		
	}
	
	private void setWebsite(LinearLayout layout, Button button) {
		String website = mCompany.website;
		if (TextUtils.isEmpty(website)) return;
		layout.setVisibility(View.VISIBLE);
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra(Constant.URL, mCompany.website);
				intent.setClass(mContext, WebPageActivity.class);
				mContext.startActivity(intent);
			}
		});
	}
	
	private void setMap(ImageView map) {
		if (TextUtils.isEmpty(mCompany.googleMapUrl)) return; 
		map.setVisibility(View.VISIBLE);
		CommonUtil.loadImage(mCompany.googleMapUrl, map);
	}
	
	private void setContact(LinearLayout layout, LinearLayout phoneLayout, LinearLayout faxLayout, LinearLayout addressLayout, 
			TextView phone, TextView fax, TextView address) {
		String phoneStr = mCompany.telephone;
		String faxStr = mCompany.faxNumber;
		String addressStr = mCompany.fullAddress();
		
		if (null != phoneStr && null != faxStr && null != addressStr) {
			if (phoneStr.trim().length() != 0 || faxStr.trim().length() != 0 || addressStr.trim().length() != 0 ) {
				layout.setVisibility(View.VISIBLE);
				if (phoneStr.trim().length() != 0) {
					phoneLayout.setVisibility(View.VISIBLE);
					phone.setText("(T) " + phoneStr);
				}
				if (faxStr.trim().length() != 0) {
					faxLayout.setVisibility(View.VISIBLE);
					fax.setText("(F) " + faxStr);
				}
				if (addressStr.trim().length() != 0) {
					addressLayout.setVisibility(View.VISIBLE);
					address.setText(addressStr);
				}
			}
		}
	}
	
	private void setRevenues(LinearLayout layout, TextView text, ImageView image) {
		final String revenuesUrl = mCompany.revenuesChartUrl;
		if (TextUtils.isEmpty(revenuesUrl)) return;
		layout.setVisibility(View.VISIBLE);
		CommonUtil.loadImage(revenuesUrl, image);
		image.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(mContext, ImageViewActivity.class);
				intent.putExtra(Constant.URL, revenuesUrl);
				intent.putExtra(Constant.IMAGE_TYPE, Constant.IMAGE_TYPE_REVENUE);
				mContext.startActivity(intent);
			}
		});

		RotateAnimation rotate = (RotateAnimation) AnimationUtils.loadAnimation(mContext, R.anim.rotate);
		text.setAnimation(rotate);
	}
	
	private void setDivisions(LinearLayout layout, Button button) {
		List<Company> mDivisions = mCompany.divisions;
		if (null == mDivisions || mDivisions.size() == 0) return;
		layout.setVisibility(View.VISIBLE);
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra(Constant.COMPANY, mCompany);
				intent.setClass(mContext, DivisionActivity.class);
				mContext.startActivity(intent);
			}
		});
	}
	
	private void setSubsidiaries(LinearLayout layout, Button button) {
		List<Company> mSubsidiaries = mCompany.subsidiaries;
		if (null == mSubsidiaries || mSubsidiaries.size() == 0) return;
		layout.setVisibility(View.VISIBLE);
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra(Constant.COMPANY, mCompany);
				intent.setClass(mContext, SubsidiaryActivity.class);
				mContext.startActivity(intent);
			}
		});
	}
	
	private void setSocialProfiles(LinearLayout layout, LinearLayout social) {
		if (null != mCompany.socialProfiles) {
			List<SocialProfile> listSocialProfiles = mCompany.socialProfiles;
			if (listSocialProfiles.size() > Constant.ZERO) {
				for (int i = 0; i < listSocialProfiles.size(); i++) {
					View view = LayoutInflater.from(mContext).inflate(R.layout.social_image, null);
					final SocialProfile socialProfile = listSocialProfiles.get(i);
					ImageView image = (ImageView) view.findViewById(R.id.image);
					String type = socialProfile.capitalizedType();
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
			image.setBackgroundResource(R.drawable.twitter);
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
			image.setBackgroundResource(R.drawable.button_slide_share);
		} else if (type.trim().equalsIgnoreCase(Constant.SALESFORCE)) {
			image.setBackgroundResource(R.drawable.salesforce_button);
		}  
	}
	
	private void setDescription(LinearLayout layout, final TextView description, final TextView more) {
		if (TextUtils.isEmpty(mCompany.description)) return;
		layout.setVisibility(View.VISIBLE);
		description.setText(mCompany.description);
		//TODO
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
				description.setMaxLines(Integer.MAX_VALUE);
				more.setVisibility(View.GONE);
			}
		});
	}
	
	private void setInformation(LinearLayout layout) {
		String founed = mCompany.founded;
		String industry = mCompany.industries;
		String employees = CommonUtil.parserNum(mCompany.employeeSize);
		String revenueStr = mCompany.revenueSize;
		String fiscalyear = mCompany.fiscalYear;
		String fortuneRank = mCompany.fortuneRank;
		String specialities = mCompany.specialities;
		String ownership = mCompany.ownership;
		
		setInformationValue(founed, R.string.founded, R.layout.item_about_detail, layout);
		setInformationValue(industry, R.string.industry, R.layout.item_about_detail, layout);
		if (!employees.trim().equals(Constant.ZERO + "")) {
			setInformationValue(employees, R.string.employees, R.layout.item_about_detail, layout);
		}
		if (null != revenueStr) {
			if (!revenueStr.trim().equals(Constant.ZERO + "")) {
				setInformationValue(CommonUtil.revenueFormat(revenueStr), R.string.revenue, R.layout.item_about_detail, layout);
			}
		}
		setInformationValue(fortuneRank, R.string.fortune_rank, R.layout.item_about_detail, layout);
		setInformationValue(fiscalyear, R.string.fiscal_year, R.layout.item_about_detail, layout);
		setInformationValue(specialities, R.string.specialty, R.layout.item_about_detail, layout);
		setInformationValue(ownership, R.string.ownership, R.layout.item_about_detail, layout);
		
	}
	
	private void setInformationValue(String str, int strId, int layoutId, LinearLayout layout) {
		if (null != str && !Constant.NOTHING.equals(str) && !str.equals("0")) {
			View view = LayoutInflater.from(mContext).inflate(layoutId, null);
			TextView textview_name = (TextView) view.findViewById(R.id.name);
			TextView textview_value = (TextView) view.findViewById(R.id.value);
			textview_name.setText(mContext.getResources().getString(strId));
			if (strId == R.string.ownership) {
				if (null != mCompany.tickerSymbols && mCompany.tickerSymbols.size() > 0) {
					String tickerName = mCompany.tickerSymbols.get(0).name.trim();
					final String tickerUrl = mCompany.tickerSymbols.get(0).url.trim();
					if (str.equalsIgnoreCase("Public")) {
						str = str + " (" +tickerName + ")";
						SpannableString sp = new SpannableString(str);
						sp.setSpan(new ForegroundColorSpan(Color.parseColor("#FF8C44")), str.length() - tickerName.length() - 2,
								str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						textview_value.setText(sp);
						
						textview_value.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View arg0) {
								Intent intent = new Intent();
								intent.putExtra(Constant.URL, tickerUrl);
								intent.setClass(mContext, WebPageActivity.class);
								mContext.startActivity(intent);
							}
						});
					} else {
						textview_value.setText(str);
					}
				} else {
					textview_value.setText(str);
				}
			} else {
				textview_value.setText(str);
			}
			layout.addView(view);
			if (str.equals(Constant.ZERO)) {
				layout.setVisibility(View.GONE);
			} else {
				layout.setVisibility(View.VISIBLE);
			}
		}
	}

}
