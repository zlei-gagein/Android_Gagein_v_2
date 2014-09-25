package com.gagein.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gagein.R;
import com.gagein.http.APIHttpMetadata;
import com.gagein.model.Company;
import com.gagein.util.CommonUtil;
import com.gagein.util.Constant;

public class ScoresAdapter extends BaseAdapter {
	
	private Context mContext;
	private List<Company> companies;
	public Boolean edit = false;
	private String scoreRank = "";
	
	public ScoresAdapter(Context mContext, List<Company> companies) {
		super();
		this.mContext = mContext;
		this.companies = companies;
		
		for (int i = 0; i < Constant.scoresSorts.size(); i++) {
			if (Constant.scoresSorts.get(i).getChecked()) {
				scoreRank = Constant.scoresSorts.get(i).getKey();
			}
		}
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_scores, null);
			viewHolder.image = (ImageView) convertView.findViewById(R.id.image);
			viewHolder.name = (TextView) convertView.findViewById(R.id.name);
			viewHolder.website = (TextView) convertView.findViewById(R.id.website);
			viewHolder.address = (TextView) convertView.findViewById(R.id.address);
			viewHolder.scoresLayout = (RelativeLayout) convertView.findViewById(R.id.scoresLayout);
			viewHolder.folRank = (TextView) convertView.findViewById(R.id.folRank);
			viewHolder.folScore = (TextView) convertView.findViewById(R.id.folScore);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		final Company company = companies.get(position);
		String webSite = company.website;
		viewHolder.image.setImageResource(R.drawable.logo_company_default);
		CommonUtil.loadImage(company.logoPath, viewHolder.image);
		viewHolder.name.setText(company.name);
		if (!webSite.isEmpty()) viewHolder.website.setText(webSite.replace("www.", ""));
		viewHolder.address.setText(company.address());
		
		if (scoreRank.equalsIgnoreCase(APIHttpMetadata.kGGScoreRank1)) {
//			viewHolder.folRank.setText(company.fol_rank1 + "");
//			viewHolder.folScore.setText(company.fol_score1 + "");
//			setScoreBgTextColor(viewHolder, company.fol_score1);
			CommonUtil.setScoreLayout(viewHolder.scoresLayout, company.fol_rank1, company.fol_score1, viewHolder.folRank, viewHolder.folScore, mContext);
		} else if (scoreRank.equalsIgnoreCase(APIHttpMetadata.kGGScoreRank7)) {
//			viewHolder.folRank.setText(company.fol_rank7 + "");
//			viewHolder.folScore.setText(company.fol_score7 + "");
//			setScoreBgTextColor(viewHolder, company.fol_score7);
			CommonUtil.setScoreLayout(viewHolder.scoresLayout, company.fol_rank7, company.fol_score7, viewHolder.folRank, viewHolder.folScore, mContext);
		} else if (scoreRank.equalsIgnoreCase(APIHttpMetadata.kGGScoreRank30)) {
//			viewHolder.folRank.setText(company.fol_rank30 + "");
//			viewHolder.folScore.setText(company.fol_score30 + "");
//			setScoreBgTextColor(viewHolder, company.fol_score30);
			CommonUtil.setScoreLayout(viewHolder.scoresLayout, company.fol_rank30, company.fol_score30, viewHolder.folRank, viewHolder.folScore, mContext);
		}
		
		return convertView;
	}

//	private void setScoreBgTextColor(ViewHolder viewHolder, int score) {
//		if (score >= 85 && score <= 100) {
//			viewHolder.scoresLayout.setBackgroundResource(R.drawable.score_orange_fire);
//			viewHolder.folRank.setTextColor(mContext.getResources().getColor(R.color.white));
//			viewHolder.folScore.setTextColor(mContext.getResources().getColor(R.color.white));
//		} else if (score >= 70 && score <= 84) {
//			viewHolder.scoresLayout.setBackgroundResource(R.drawable.score_orange_circle);
//			viewHolder.folRank.setTextColor(mContext.getResources().getColor(R.color.white));
//			viewHolder.folScore.setTextColor(mContext.getResources().getColor(R.color.white));
//		} else if (score >= 50 && score <= 69) {
//			viewHolder.scoresLayout.setBackgroundResource(R.drawable.score_gray_circle);
//			viewHolder.folRank.setTextColor(mContext.getResources().getColor(R.color.white));
//			viewHolder.folScore.setTextColor(mContext.getResources().getColor(R.color.white));
//		} else if (score >= 1 && score <= 49) {
//			viewHolder.scoresLayout.setBackgroundResource(R.drawable.score_light_gray_circle);
//			viewHolder.folRank.setTextColor(mContext.getResources().getColor(R.color.white));
//			viewHolder.folScore.setTextColor(mContext.getResources().getColor(R.color.white));
//		} else if (score == 0) {
//			viewHolder.scoresLayout.setBackgroundResource(R.drawable.score_white_circle);
//			viewHolder.folRank.setTextColor(mContext.getResources().getColor(R.color.text_weak));
//			viewHolder.folScore.setTextColor(mContext.getResources().getColor(R.color.text_weak));
//		}
//	}
	
	public final class ViewHolder {
		
		public ImageView image;
		public TextView name;
		public TextView website;
		public TextView address;
		public RelativeLayout scoresLayout;
		public TextView folRank;
		public TextView folScore;
		
	}
}
