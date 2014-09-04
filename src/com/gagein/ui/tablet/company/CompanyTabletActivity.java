package com.gagein.ui.tablet.company;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.LinearLayout;

import com.gagein.R;
import com.gagein.http.APIHttpMetadata;
import com.gagein.ui.main.BaseFragmentActivity;
import com.gagein.ui.tablet.company.CompanyFilterNewsFragment.OnFilterNewsLeftBtnClickListener;
import com.gagein.ui.tablet.company.CompanyFilterNewsFragment.OnRefreshNewsFilterFromNewsListener;
import com.gagein.ui.tablet.company.CompanyFilterRelevanceFragment.OnFilterRelevanceLeftBtnClickListener;
import com.gagein.ui.tablet.company.CompanyFilterRelevanceFragment.RefreshNewsFilterFromRelevance;
import com.gagein.ui.tablet.company.CompanyFragment.LeftBtnClick;
import com.gagein.ui.tablet.company.CompanyFragment.OnCompetitorFilterClickListener;
import com.gagein.ui.tablet.company.CompanyFragment.OnNewsFilterClickListener;
import com.gagein.ui.tablet.company.CompanyFragment.OnPeopleFilterClickListener;
import com.gagein.ui.tablet.company.CompanyNewsFilterFragment.CloseLeftLayoutListener;
import com.gagein.ui.tablet.company.CompanyNewsFilterFragment.NewsBtnClickListener;
import com.gagein.ui.tablet.company.CompanyNewsFilterFragment.OnNewsFilterLeftBtnClickListener;
import com.gagein.ui.tablet.company.CompanyNewsFilterFragment.RelevanceBtnClickListener;
import com.gagein.ui.tablet.company.CompetitorFilterFragment.OnCompetitorFilterCancleBtnClickListener;
import com.gagein.ui.tablet.company.CompetitorFilterFragment.OnCompetitorIndustryClickListener;
import com.gagein.ui.tablet.company.CompetitorFilterIndustryFragment.OnRefreshCompetitors;
import com.gagein.ui.tablet.company.CompetitorFilterIndustryFragment.OnShowCompetitorsFilterFromIndustryListener;
import com.gagein.ui.tablet.company.CompetitorFilterSortByFragment.OnShowCompetitorsFilterFromSortByListener;
import com.gagein.ui.tablet.company.FilterFunctionalRoleFragment.OnClosedFunctionRole;
import com.gagein.ui.tablet.company.FilterJobLevelFragment.OnClosedJoblevel;
import com.gagein.ui.tablet.company.FilterLinkedProfileFragment.OnClosedLinkedProfile;
import com.gagein.ui.tablet.company.PeopleFilterFragment.OnPeopleFilterClosed;
import com.gagein.ui.tablet.company.PeopleFilterFragment.OnStartFilterFunctionalRole;
import com.gagein.ui.tablet.company.PeopleFilterFragment.OnStartFilterJobLevel;
import com.gagein.ui.tablet.company.PeopleFilterFragment.OnStartFilterLinkedProfile;
import com.gagein.util.Constant;

public class CompanyTabletActivity extends BaseFragmentActivity implements OnNewsFilterClickListener, OnNewsFilterLeftBtnClickListener,
	NewsBtnClickListener, RelevanceBtnClickListener, CloseLeftLayoutListener, OnFilterNewsLeftBtnClickListener, OnRefreshNewsFilterFromNewsListener
	, RefreshNewsFilterFromRelevance, OnFilterRelevanceLeftBtnClickListener, LeftBtnClick, OnCompetitorFilterClickListener, 
	OnCompetitorFilterCancleBtnClickListener, OnCompetitorIndustryClickListener, 
	OnShowCompetitorsFilterFromIndustryListener, OnShowCompetitorsFilterFromSortByListener, OnPeopleFilterClickListener, 
	OnPeopleFilterClosed, OnStartFilterJobLevel, OnStartFilterFunctionalRole, OnStartFilterLinkedProfile, OnClosedFunctionRole, 
	OnClosedLinkedProfile, OnClosedJoblevel, OnRefreshCompetitors{
	
	private LinearLayout leftLayout;
	private FragmentTransaction transaction;
	private CompanyFragment companyFragment;
	private CompanyNewsFilterFragment newsFilterFragment;
	private CompanyFilterNewsFragment filterNewsFragment;
	private CompanyFilterRelevanceFragment filterRelevanceFragment;
	private CompetitorFilterFragment competitorFilterFragment;
	private CompetitorFilterSortByFragment competitorFilterSortByFragment;
	private CompetitorFilterIndustryFragment competitorFilterIndustryFragment;
	private PeopleFilterFragment peopleFilterFragment;
	private long mCompanyId;
	private FilterJobLevelFragment jobLevelFragment;
	private FilterFunctionalRoleFragment filterFunctionalRoleFragment;
	private FilterLinkedProfileFragment linkedProfileFragment;
	
	@Override
	protected List<String> observeNotifications() {
		return stringList(Constant.BROADCAST_REFRESH_COMPANY_NEWS, Constant.BROADCAST_REFRESH_COMPANY_PEOPLE,
				Constant.BROADCAST_FILTER_REFRESH_COMPETITORS, Constant.BROADCAST_FOLLOW_COMPANY, 
				Constant.BROADCAST_UNFOLLOW_COMPANY);
	}
	
	@Override
	public void handleNotifications(Context aContext, Intent intent) {
		super.handleNotifications(aContext, intent);
		
		if (null == companyFragment) return;
		String actionName = intent.getAction();
		if (actionName.equals(Constant.BROADCAST_REFRESH_COMPANY_NEWS)) {
			
			companyFragment.getNews(false, 0, APIHttpMetadata.kGGPageFlagFirstPage, 0, true);
			
		} else if (actionName.equals(Constant.BROADCAST_REFRESH_COMPANY_PEOPLE)) {
			
			companyFragment.getPersons(false, Constant.PEOPLE_SORT_BY, Constant.JOB_LEVEL_ID, Constant.FUNCTIONAL_ROLE_ID, Constant.LINKED_PROFILE_ID);
			
		} else if (actionName.equals(Constant.BROADCAST_FILTER_REFRESH_COMPETITORS)) {
			
			companyFragment.competitorSortBy = Constant.COMPETITOR_SORT_BY;
			companyFragment.industryid = Constant.COMPETITOR_FILTER_PARAM_VALUE;
			companyFragment.getCompetitors(false, false);
			
		}else if (actionName.equals(Constant.BROADCAST_FOLLOW_COMPANY)) {
			
			companyFragment.refreshParentsAndSubsidiariesStatus(intent, true);
			
		} else if (actionName.equals(Constant.BROADCAST_UNFOLLOW_COMPANY)) {
			
			companyFragment.refreshParentsAndSubsidiariesStatus(intent, false);
			
		}
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_tablet_main);
		leftLayout = (LinearLayout) findViewById(R.id.leftLayout);
		mCompanyId = getIntent().getLongExtra(Constant.COMPANYID, 0);
		
		transaction = getSupportFragmentManager().beginTransaction();
		companyFragment = new CompanyFragment(mCompanyId);
		
		transaction.add(R.id.rightLayout, companyFragment);
		transaction.commit();
		
		setLeftLayoutVisible(View.GONE);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Constant.PEOPLE_SORT_BY = 0;
		Constant.JOB_LEVEL_ID = 0;
		Constant.FUNCTIONAL_ROLE_ID = 0;
		Constant.LINKED_PROFILE_ID = 0;
		Constant.JOB_LEVEL_NAME = "";
		Constant.FUNCTIONAL_ROLE_NAME = "";
		Constant.LINKED_PROFILE_NAME = "";
		
		Constant.COMPETITOR_SORT_BY = "noe";
		Constant.COMPETITOR_FILTER_PARAM_VALUE = "";
		Constant.currentCompetitorIndustries.clear();
	}
	
	//设置左边布局隐藏或显示
	private void setLeftLayoutVisible(int visible) {
		leftLayout.setVisibility(visible);
	}

	@Override
	public void onNewsFilterClickListener() {
		transaction = getSupportFragmentManager().beginTransaction();
		if (null == newsFilterFragment) {
			newsFilterFragment = new CompanyNewsFilterFragment();
			transaction.add(R.id.leftLayout, newsFilterFragment);
		}
		
		transaction.show(newsFilterFragment);
		
		if (null != competitorFilterFragment) {
			transaction.hide(competitorFilterFragment);
		}
		if (null != peopleFilterFragment) {
			transaction.hide(peopleFilterFragment);
		}
		
		
		transaction.commit();
		setLeftLayoutVisible(View.VISIBLE);
	}

	@Override
	public void onNewsFilterLeftbtnClickListener() {
		setLeftLayoutVisible(View.GONE);
	}

	@Override
	public void closeLeftLayoutListener() {
		setLeftLayoutVisible(View.GONE);
	}
	
	@Override
	public void onNewsBtnListener() {
		transaction = getSupportFragmentManager().beginTransaction();
		if (null == filterNewsFragment) {
			filterNewsFragment = new CompanyFilterNewsFragment();
			transaction.add(R.id.leftLayout, filterNewsFragment);
		}
		transaction.show(filterNewsFragment);
		if (null != newsFilterFragment) {
			transaction.hide(newsFilterFragment);
		}
		if (null != filterRelevanceFragment) {
			transaction.hide(filterRelevanceFragment);
		}
		transaction.commit();
	}

	@Override
	public void onRelevanceBtnListener() {
		transaction = getSupportFragmentManager().beginTransaction();
		if (null == filterRelevanceFragment) {
			filterRelevanceFragment = new CompanyFilterRelevanceFragment();
			transaction.add(R.id.leftLayout, filterRelevanceFragment);
		}
		transaction.show(filterRelevanceFragment);
		if (null != filterNewsFragment) {
			transaction.hide(filterNewsFragment);
		}
		if (null != newsFilterFragment) {
			transaction.hide(newsFilterFragment);
		}
		transaction.commit();
	}

	@Override
	public void onFilterRelevanceLeftBtnClickListener() {
		transaction = getSupportFragmentManager().beginTransaction();
		if (null == newsFilterFragment) {
			newsFilterFragment = new CompanyNewsFilterFragment();
			transaction.add(R.id.leftLayout, newsFilterFragment);
		}
		transaction.show(newsFilterFragment);
		if (null != filterNewsFragment) {
			transaction.hide(filterNewsFragment);
		}
		if (null != filterRelevanceFragment) {
			transaction.hide(filterRelevanceFragment);
		}
		transaction.commit();
	}

	@Override
	public void refreshNewsFilterFromRelevance() {
		if (null == newsFilterFragment) {
			newsFilterFragment = new CompanyNewsFilterFragment();
		}
		newsFilterFragment.getRelevance();
		companyFragment.refreshNewsForFilter();
	}

	@Override
	public void onRefreshNewsFilterFromNewsListener() {
		if (null == newsFilterFragment) {
			newsFilterFragment = new CompanyNewsFilterFragment();
		}
		newsFilterFragment.getRelevance();
		
		if (null == companyFragment) {
			companyFragment = new CompanyFragment(mCompanyId);
		}
		companyFragment.refreshNewsForFilter();
	}

	@Override
	public void onFilterNewsLeftbtnClickListener() {
		transaction = getSupportFragmentManager().beginTransaction();
		if (null == newsFilterFragment) {
			newsFilterFragment = new CompanyNewsFilterFragment();
			transaction.add(R.id.leftLayout, newsFilterFragment);
		}
		transaction.show(newsFilterFragment);
		if (null != filterNewsFragment) {
			transaction.hide(filterNewsFragment);
		}
		if (null != filterRelevanceFragment) {
			transaction.hide(filterRelevanceFragment);
		}
		transaction.commit();
	}

	@Override
	public void onLeftBtnClick() {
		if (null != leftLayout) {
			if (leftLayout.getVisibility() == View.VISIBLE) {
				setLeftLayoutVisible(View.GONE);
				return;
			}
		}
		finish();
	}

	@Override
	public void onCompetitorFilterClickListener() {
		transaction = getSupportFragmentManager().beginTransaction();
		if (null == competitorFilterFragment) {
			competitorFilterFragment = new CompetitorFilterFragment();
			transaction.add(R.id.leftLayout, competitorFilterFragment);
		}
		transaction.show(competitorFilterFragment);
		
		if (null != newsFilterFragment) {
			transaction.hide(newsFilterFragment);
		}
		
		if (null != peopleFilterFragment) {
			transaction.hide(peopleFilterFragment);
		}
		
		transaction.commit();
		setLeftLayoutVisible(View.VISIBLE);
	}

	@Override
	public void onCompetitorFilterCancleBtnClickListener() {
		setLeftLayoutVisible(View.GONE);
	}

	//TODO
//	@Override
//	public void onCompetitorSortByClickListener() {
//		transaction = getSupportFragmentManager().beginTransaction();
//		if (null == competitorFilterSortByFragment) {
//			competitorFilterSortByFragment = new CompetitorFilterSortByFragment();
//			transaction.add(R.id.leftLayout, competitorFilterSortByFragment);
//		}
//		transaction.show(competitorFilterSortByFragment);
//		
//		if (null != newsFilterFragment) {
//			transaction.hide(newsFilterFragment);
//		}
//		
//		if (null != competitorFilterFragment) {
//			transaction.hide(competitorFilterFragment);
//		}
//		
//		transaction.commit();
//	}

	@Override
	public void onCompetitorIndustryClickListener() {
		transaction = getSupportFragmentManager().beginTransaction();
		if (null == competitorFilterIndustryFragment) {
			competitorFilterIndustryFragment = new CompetitorFilterIndustryFragment();
			transaction.add(R.id.leftLayout, competitorFilterIndustryFragment);
		}
		transaction.show(competitorFilterIndustryFragment);
		
		if (null != newsFilterFragment) {
			transaction.hide(newsFilterFragment);
		}
		
		if (null != competitorFilterFragment) {
			transaction.hide(competitorFilterFragment);
		}
		
		transaction.commit();
	}

	@Override
	public void onShowCompetitorsFilterFromIndustryListener() {
		transaction = getSupportFragmentManager().beginTransaction();
		if (null == competitorFilterFragment) {
			competitorFilterFragment = new CompetitorFilterFragment();
			transaction.add(R.id.leftLayout, competitorFilterFragment);
		}
		transaction.show(competitorFilterFragment);
		
		if (null != newsFilterFragment) {
			transaction.hide(newsFilterFragment);
		}
		
		if (null != competitorFilterIndustryFragment) {
			transaction.hide(competitorFilterIndustryFragment);
		}
		
		transaction.commit();
	}

	@Override
	public void onShowCompetitorsFilterFromSortByListener() {
		transaction = getSupportFragmentManager().beginTransaction();
		if (null == competitorFilterFragment) {
			competitorFilterFragment = new CompetitorFilterFragment();
			transaction.add(R.id.leftLayout, competitorFilterFragment);
		}
		transaction.show(competitorFilterFragment);
		
		if (null != newsFilterFragment) {
			transaction.hide(newsFilterFragment);
		}
		
		if (null != competitorFilterIndustryFragment) {
			transaction.hide(competitorFilterIndustryFragment);
		}
		
		if (null != competitorFilterSortByFragment) {
			transaction.hide(competitorFilterSortByFragment);
		}
		
		transaction.commit();
	}

	@Override
	public void onPeopleFilterClickListener() {
		transaction = getSupportFragmentManager().beginTransaction();
		if (null == peopleFilterFragment) {
			peopleFilterFragment = new PeopleFilterFragment();
			transaction.add(R.id.leftLayout, peopleFilterFragment);
		}
		transaction.show(peopleFilterFragment);
		
		if (null != newsFilterFragment) {
			transaction.hide(newsFilterFragment);
		}
		
		if (null != competitorFilterIndustryFragment) {
			transaction.hide(competitorFilterIndustryFragment);
		}
		
		if (null != competitorFilterSortByFragment) {
			transaction.hide(competitorFilterSortByFragment);
		}
		
		transaction.commit();
		setLeftLayoutVisible(View.VISIBLE);
	}

	@Override
	public void onPeopleFilterClosed() {
		setLeftLayoutVisible(View.GONE);
		refreshPeopleForFilter();
	}
	
	public void refreshPeopleForFilter() {
		companyFragment.refreshPeopleForFilter();
	}

	@Override
	public void onStartFilterLinkedProfile() {//TODO
		transaction = getSupportFragmentManager().beginTransaction();
//		if (null == linkedProfileFragment) {
			linkedProfileFragment = new FilterLinkedProfileFragment();
			transaction.add(R.id.leftLayout, linkedProfileFragment);
//		}
		transaction.show(linkedProfileFragment);
		
		if (null != peopleFilterFragment) {
			transaction.hide(peopleFilterFragment);
		}
		
		if (null != competitorFilterIndustryFragment) {
			transaction.hide(competitorFilterIndustryFragment);
		}
		
		if (null != competitorFilterSortByFragment) {
			transaction.hide(competitorFilterSortByFragment);
		}
		
		transaction.commit();
		setLeftLayoutVisible(View.VISIBLE);
	}

	@Override
	public void onStartFilterFunctionalRole() {//TODO
		transaction = getSupportFragmentManager().beginTransaction();
//		if (null == filterFunctionalRoleFragment) {
			filterFunctionalRoleFragment = new FilterFunctionalRoleFragment();
			transaction.add(R.id.leftLayout, filterFunctionalRoleFragment);
//		}
		transaction.show(filterFunctionalRoleFragment);
		
		if (null != peopleFilterFragment) {
			transaction.hide(peopleFilterFragment);
		}
		
		if (null != competitorFilterIndustryFragment) {
			transaction.hide(competitorFilterIndustryFragment);
		}
		
		if (null != competitorFilterSortByFragment) {
			transaction.hide(competitorFilterSortByFragment);
		}
		
		transaction.commit();
		setLeftLayoutVisible(View.VISIBLE);
	}

	@Override
	public void onStartFilterJobLevel() {//TODO
		transaction = getSupportFragmentManager().beginTransaction();
//		if (null == jobLevelFragment) {
			jobLevelFragment = new FilterJobLevelFragment();
			transaction.add(R.id.leftLayout, jobLevelFragment);
//		}
		transaction.show(jobLevelFragment);
		
		if (null != peopleFilterFragment) {
			transaction.hide(peopleFilterFragment);
		}
		
		if (null != competitorFilterIndustryFragment) {
			transaction.hide(competitorFilterIndustryFragment);
		}
		
		if (null != competitorFilterSortByFragment) {
			transaction.hide(competitorFilterSortByFragment);
		}
		
		transaction.commit();
		setLeftLayoutVisible(View.VISIBLE);
	}

	@Override
	public void onClosedFunctionRole() {
		transaction = getSupportFragmentManager().beginTransaction();
		if (null == peopleFilterFragment) {
			peopleFilterFragment = new PeopleFilterFragment();
			transaction.add(R.id.leftLayout, peopleFilterFragment);
		}
		transaction.show(peopleFilterFragment);
		
		if (null != filterFunctionalRoleFragment) {
			transaction.hide(filterFunctionalRoleFragment);
		}
		
		if (null != competitorFilterIndustryFragment) {
			transaction.hide(competitorFilterIndustryFragment);
		}
		
		if (null != competitorFilterSortByFragment) {
			transaction.hide(competitorFilterSortByFragment);
		}
		
		transaction.commit();
		setLeftLayoutVisible(View.VISIBLE);
	}

	@Override
	public void onClosedLinkedProfile() {
		transaction = getSupportFragmentManager().beginTransaction();
		if (null == peopleFilterFragment) {
			peopleFilterFragment = new PeopleFilterFragment();
			transaction.add(R.id.leftLayout, peopleFilterFragment);
		}
		transaction.show(peopleFilterFragment);
		
		if (null != linkedProfileFragment) {
			transaction.hide(linkedProfileFragment);
		}
		
		if (null != competitorFilterIndustryFragment) {
			transaction.hide(competitorFilterIndustryFragment);
		}
		
		if (null != competitorFilterSortByFragment) {
			transaction.hide(competitorFilterSortByFragment);
		}
		
		transaction.commit();
		setLeftLayoutVisible(View.VISIBLE);
	}

	@Override
	public void onClosedJoblevel() {
		transaction = getSupportFragmentManager().beginTransaction();
		if (null == peopleFilterFragment) {
			peopleFilterFragment = new PeopleFilterFragment();
			transaction.add(R.id.leftLayout, peopleFilterFragment);
		}
		transaction.show(peopleFilterFragment);
		
		if (null != jobLevelFragment) {
			transaction.hide(jobLevelFragment);
		}
		
		if (null != competitorFilterIndustryFragment) {
			transaction.hide(competitorFilterIndustryFragment);
		}
		
		if (null != competitorFilterSortByFragment) {
			transaction.hide(competitorFilterSortByFragment);
		}
		
		transaction.commit();
		setLeftLayoutVisible(View.VISIBLE);
	}

	@Override
	public void onRefreshCompetitors() {
		companyFragment.refreshCompetitorsForFilter();
	}
}
