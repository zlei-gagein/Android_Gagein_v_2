package com.gagein.adapter;

import java.util.Collections;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.emilsjolander.components.stickylistheaders.StickyListHeadersAdapter;
import com.gagein.R;
import com.gagein.model.Company;
import com.gagein.ui.tablet.companies.CompaniesFragment;
import com.gagein.util.CommonUtil;

public class CompaniesAdapter extends BaseAdapter implements StickyListHeadersAdapter, SectionIndexer{
	
	private Context mContext;
	private List<Company> companies;
	private LayoutInflater inflater;
	/** select catalog */
	private int[] sectionIndices;
	/** select character */
	private Character[] sectionsLetters;
	private Boolean edit = false;
	private Object object;
	

	public CompaniesAdapter(Context mContext, List<Company> companies, Object object) {
		super();
		this.mContext = mContext;
		this.companies = companies;
		Collections.sort(companies);
		inflater = LayoutInflater.from(mContext);
		sectionIndices = getSectionIndices();
		sectionsLetters = getStartingLetters();
		
		if (object != null) {
			if (object instanceof CompaniesFragment) this.object = object;
		}
	}
	
	public void setEdit(Boolean edit) {
		this.edit = edit;
	}

	@Override
	public int getCount() {
		return companies.size();
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
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_companies, null);
			viewHolder.image = (ImageView) convertView.findViewById(R.id.image);
			viewHolder.checkBtn = (ImageView) convertView.findViewById(R.id.checkBtn);
			viewHolder.name = (TextView) convertView.findViewById(R.id.name);
			viewHolder.website = (TextView) convertView.findViewById(R.id.website);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		viewHolder.checkBtn.setVisibility(edit ? View.VISIBLE : View.GONE);
		final Company company = companies.get(position);
		CommonUtil.loadImage(company.orgLargerLogoPath, viewHolder.image);
		viewHolder.name.setText(company.name);
		final ImageView checkBtn = viewHolder.checkBtn;
		checkBtn.setImageResource(company.select ? R.drawable.button_select : R.drawable.button_unselect);
		//TODO
//		viewHolder.checkBtn.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View arg0) {
//				company.select = !company.select;
//				checkBtn.setImageResource(company.select ? R.drawable.button_select : R.drawable.button_unselect);
//				if (CommonUtil.isTablet(mContext)) {
//					((CompaniesFragment)object).setBottomButton(companies);
//				} else {
//					((CompaniesActivity)mContext).setBottomButton(companies);
//				}
//			}
//		});
		viewHolder.website.setText(CommonUtil.removeWebsite3W(company.website) + "  " +  company.address());
		
		return convertView;
	}
	
	public final class ViewHolder {
		
		public ImageView image;
		public ImageView checkBtn;
		public TextView name;
		public TextView website;
		
	}

	@SuppressLint("DefaultLocale")
	@Override
	public long getHeaderId(int position) {
		if (companies.get(position).name.length() != 0) {
			char letter = companies.get(position).name.toUpperCase().subSequence(0, 1).charAt(0);
			if (!verifyIsLetter(letter)) letter = 35;
			return letter;
		} else {
			return position;
		}
	}

	/**
	 * verify char whether is letter
	 * @param letter
	 * @return
	 */
	private boolean verifyIsLetter(char letter) {
		String reg = "[A-Z]";
		boolean isLetter = String.valueOf(letter).matches(reg);
		return isLetter;
	}

	@Override
	public View getHeaderView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.section_companies, parent, false);
		}
		TextView text = (TextView) convertView.findViewById(R.id.text);
		// set header text as first char in name
		if (companies.get(position).name.length() != 0) {
			char headerChar = companies.get(position).name.toUpperCase().subSequence(0, 1).charAt(0);
			if (!verifyIsLetter(headerChar)) {
				text.setText("#");
			} else {
				text.setText(String.valueOf(headerChar));
			}
		}
		return convertView;
	}

	@Override
	public int getPositionForSection(int section) {
		if (section >= sectionIndices.length) {
			section = sectionIndices.length - 1;
		} else if (section < 0) {
			section = 0;
		}
		return sectionIndices[section];
	}

	@Override
	public int getSectionForPosition(int position) {
		for (int i = 0; i < sectionIndices.length; i++) {
			if (position < sectionIndices[i]) {
				return i - 1;
			}
		}
		return sectionIndices.length - 1;
	}

	@Override
	public Object[] getSections() {
		return sectionsLetters;
	}
	
	private int[] getSectionIndices() {
		int[] sections = new int[companies.size()];
		for (int i = 0; i < companies.size(); i++) {
			sections[i] = i;
		}
		return sections;
	}
	
	private Character[] getStartingLetters() {
		Character[] letters = new Character[sectionIndices.length];
		
		for (int i = 0; i < sectionIndices.length; i++) {
			if (companies.get(sectionIndices[i]).name.length() != 0) {
				char letter = companies.get(sectionIndices[i]).name.toUpperCase().charAt(0);
				if (!verifyIsLetter(letter)) {
					letters[i] = 35;
				} else {
					letters[i] = letter;
				}
			}
		}
		return letters;
	}
	
	public int getSectionStart(int itemPosition) {
		return getPositionForSection(getSectionForPosition(itemPosition));
	} // remember that these have to be static, postion=1 should walys return
		// the

}
