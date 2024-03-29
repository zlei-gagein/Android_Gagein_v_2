package com.gagein.ui.tablet.search.people;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.LinearLayout;

import com.gagein.R;
import com.gagein.ui.main.BaseFragmentActivity;
import com.gagein.ui.tablet.search.PeopleEmptyViewFragment;
import com.gagein.ui.tablet.search.people.CompaniesFragment.OnCanleTask;
import com.gagein.ui.tablet.search.people.CompaniesFragment.OnCompaniesFinish;
import com.gagein.ui.tablet.search.people.CompaniesFragment.OnSearchFromCompanies;
import com.gagein.ui.tablet.search.people.EmployeeSizeFragment.OnEmployeeSizeFinish;
import com.gagein.ui.tablet.search.people.EmployeeSizeFragment.OnSearchFromEmployeeSize;
import com.gagein.ui.tablet.search.people.FiscalYearFragment.OnFiscalYearFinish;
import com.gagein.ui.tablet.search.people.FiscalYearFragment.OnSearchFromFiscalYear;
import com.gagein.ui.tablet.search.people.FunctionalRoleFragment.OnFunctionalRoleFinish;
import com.gagein.ui.tablet.search.people.FunctionalRoleFragment.OnSearchFromFunctionalRole;
import com.gagein.ui.tablet.search.people.HeadquartersFragment.OnHeadquartersFinish;
import com.gagein.ui.tablet.search.people.HeadquartersFragment.OnSearchFromHeadquarters;
import com.gagein.ui.tablet.search.people.IndustryFragment.OnIndustryFinish;
import com.gagein.ui.tablet.search.people.IndustryFragment.OnSearchFromIndustry;
import com.gagein.ui.tablet.search.people.JobLevelFragment.OnJobLevelFinish;
import com.gagein.ui.tablet.search.people.JobLevelFragment.OnSearchFromJobLevel;
import com.gagein.ui.tablet.search.people.JobTitleFragment.OnJobTitleFinish;
import com.gagein.ui.tablet.search.people.JobTitleFragment.OnSearchFromJobTitle;
import com.gagein.ui.tablet.search.people.LocationFragment.OnLocationFinish;
import com.gagein.ui.tablet.search.people.LocationFragment.OnSearchFromLocation;
import com.gagein.ui.tablet.search.people.MileStoneFragment.OnMileStoneFinish;
import com.gagein.ui.tablet.search.people.MileStoneFragment.OnSearchFromMileStone;
import com.gagein.ui.tablet.search.people.NewsTriggersFragment.OnNewsTriggersFinish;
import com.gagein.ui.tablet.search.people.NewsTriggersFragment.OnSearchFromNewsTriggers;
import com.gagein.ui.tablet.search.people.OwnershipFragment.OnOwnershipFinish;
import com.gagein.ui.tablet.search.people.OwnershipFragment.OnSearchFromOwnership;
import com.gagein.ui.tablet.search.people.PeopleFilterFragment.OnFinishActivity;
import com.gagein.ui.tablet.search.people.PeopleFilterFragment.OnStartCompanies;
import com.gagein.ui.tablet.search.people.PeopleFilterFragment.OnStartEmployeeSize;
import com.gagein.ui.tablet.search.people.PeopleFilterFragment.OnStartFiscalYear;
import com.gagein.ui.tablet.search.people.PeopleFilterFragment.OnStartFunctionalRole;
import com.gagein.ui.tablet.search.people.PeopleFilterFragment.OnStartHeadquarters;
import com.gagein.ui.tablet.search.people.PeopleFilterFragment.OnStartIndustry;
import com.gagein.ui.tablet.search.people.PeopleFilterFragment.OnStartJobLevel;
import com.gagein.ui.tablet.search.people.PeopleFilterFragment.OnStartJobTitle;
import com.gagein.ui.tablet.search.people.PeopleFilterFragment.OnStartLocation;
import com.gagein.ui.tablet.search.people.PeopleFilterFragment.OnStartMilestone;
import com.gagein.ui.tablet.search.people.PeopleFilterFragment.OnStartNewsTriggers;
import com.gagein.ui.tablet.search.people.PeopleFilterFragment.OnStartOwnership;
import com.gagein.ui.tablet.search.people.PeopleFilterFragment.OnStartRank;
import com.gagein.ui.tablet.search.people.PeopleFilterFragment.OnStartRevenueSize;
import com.gagein.ui.tablet.search.people.PeopleFilterFragment.OnStartSearchFromPeople;
import com.gagein.ui.tablet.search.people.RankFragment.OnRankFinish;
import com.gagein.ui.tablet.search.people.RankFragment.OnSearchFromRank;
import com.gagein.ui.tablet.search.people.RevenueSizeFragment.OnRevenueSizeFinish;
import com.gagein.ui.tablet.search.people.RevenueSizeFragment.OnSearchFromRevenueSize;
import com.gagein.ui.tablet.search.people.SearchPeopleResultFragment.OnHideLeftLayout;
import com.gagein.ui.tablet.search.people.SearchPeopleResultFragment.OnUpdateFilterStatusForPeople;
import com.gagein.util.CommonUtil;
import com.gagein.util.Constant;
import com.gagein.util.Log;

public class PeopleSearchTabletActivity extends BaseFragmentActivity implements OnFinishActivity, OnStartNewsTriggers, OnStartHeadquarters
 	, OnStartIndustry, OnStartEmployeeSize, OnStartRevenueSize, OnStartOwnership, OnStartMilestone, OnStartRank, OnStartFiscalYear, 
 	OnStartJobTitle, OnStartJobLevel, OnStartLocation, OnStartFunctionalRole, OnStartCompanies ,OnNewsTriggersFinish, OnHeadquartersFinish,
 	OnIndustryFinish, OnEmployeeSizeFinish, OnRevenueSizeFinish, OnOwnershipFinish, OnJobTitleFinish, OnJobLevelFinish, OnFunctionalRoleFinish,
 	OnLocationFinish, OnCompaniesFinish , OnMileStoneFinish, OnRankFinish, OnFiscalYearFinish, OnSearchFromFiscalYear, OnSearchFromHeadquarters
 	, OnSearchFromIndustry, OnSearchFromMileStone, OnSearchFromOwnership, OnSearchFromRevenueSize, OnSearchFromEmployeeSize, OnSearchFromNewsTriggers
 	, OnSearchFromRank, OnSearchFromCompanies, OnSearchFromFunctionalRole, OnSearchFromJobLevel, OnSearchFromJobTitle, OnSearchFromLocation
 	, OnHideLeftLayout, OnStartSearchFromPeople, OnCanleTask, OnUpdateFilterStatusForPeople{
	
	
	private FragmentTransaction transaction;
	private PeopleFilterFragment filterFragment;
	private PeopleEmptyViewFragment peopleEmptyViewFragment;
	private FiscalYearFragment fiscalYearFragment;
	private RankFragment rankFragment;
	private MileStoneFragment mileStoneFragment;
	private OwnershipFragment ownershipFragment;
	private RevenueSizeFragment revenueSizeFragment;
	private EmployeeSizeFragment employeeSizeFragment;
	private IndustryFragment industryFragment;
	private HeadquartersFragment headquartersFragment;
	private NewsTriggersFragment newsTriggersFragment;
	private JobTitleFragment jobTitleFragment;
	private JobLevelFragment jobLevelFragment;
	private LocationFragment locationFragment;
	private FunctionalRoleFragment functionalRoleFragment;
	private CompaniesFragment companiesFragment;
	private SearchPeopleResultFragment searchPeopleResultFragment;
	private Timer timer;
	private TimerTask timerTask;
	private LinearLayout leftLayout;
	private String savedId = "";
	private IntentFilter intentFilter = new IntentFilter(Constant.BROADCAST_REFRESH_FILTER);
	private RefreshFilterBroadcastReceiver receiver = new RefreshFilterBroadcastReceiver();
	
	@Override
	protected List<String> observeNotifications() {
		return stringList(Constant.BROADCAST_SAVED_SEARCH);
	}
	
	@Override
	public void handleNotifications(Context aContext, Intent intent) {
		super.handleNotifications(aContext, intent);
		
		String actionName = intent.getAction();
		if (actionName.equals(Constant.BROADCAST_SAVED_SEARCH)) {
			
			searchPeopleResultFragment.setSaved();
			
		}
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_tablet_main);
		leftLayout = (LinearLayout) findViewById(R.id.leftLayout);
		
		initSearchFilter();
		
		savedId = getIntent().getStringExtra(Constant.SAVEDID);
		transaction = getSupportFragmentManager().beginTransaction();
		filterFragment = new PeopleFilterFragment();
		
		if (null == savedId || savedId.isEmpty()) {
			peopleEmptyViewFragment = new PeopleEmptyViewFragment();
			
			transaction.add(R.id.leftLayout, filterFragment);
			transaction.add(R.id.rightLayout, peopleEmptyViewFragment);
		} else {
			searchPeopleResultFragment = new SearchPeopleResultFragment();
			transaction.add(R.id.leftLayout, filterFragment);
			transaction.add(R.id.rightLayout, searchPeopleResultFragment);
			setLeftLayoutVisible(View.GONE);
			
			timer = new Timer();
			timerTask = new TimerTask() {
				@Override
				public void run() {
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							searchPeopleResultFragment.searchSavedResult(savedId);
						}
					});
				}
			};
			timer.schedule(timerTask, 300);
		}
		transaction.commit();
		
		registerReceiver(receiver, intentFilter);
	}
	
	public class RefreshFilterBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			
			if (null != jobTitleFragment) jobTitleFragment.refreshAdapter();
			if (null != jobLevelFragment) jobLevelFragment.refreshAdapter();
			if (null != locationFragment) locationFragment.refreshAdapter();
			if (null != functionalRoleFragment) functionalRoleFragment.refreshAdapter();
			
			if (null != newsTriggersFragment) newsTriggersFragment.refreshAdapter();
			if (null != companiesFragment) companiesFragment.refreshAdapter();
			if (null != headquartersFragment) headquartersFragment.refreshAdapter();
			if (null != industryFragment) industryFragment.refreshAdapter();
			if (null != employeeSizeFragment) employeeSizeFragment.refreshAdapter();
			if (null != revenueSizeFragment) revenueSizeFragment.refreshAdapter();
			if (null != ownershipFragment) ownershipFragment.refreshAdapter();
			if (null != mileStoneFragment) mileStoneFragment.refreshAdapter();
			if (null != rankFragment) rankFragment.refreshAdapter();
			if (null != fiscalYearFragment) fiscalYearFragment.refreshAdapter();
		}
		
	}
	
	private void initSearchFilter() {
		
		if (null != Constant.MFILTERS) {
			Constant.MFILTERS.getHeadquarters().clear();
			Constant.MFILTERS.getJobTitles().clear();
			Constant.MFILTERS.getLocations().clear();
		}
		
		Constant.REVERSE = false;
		CommonUtil.resetFilters();
		CommonUtil.initConSortBy();
		
	}

	@Override
	public void onFinishActivity() {
		finish();
	}

	@Override
	public void onStartFiscalYear() {
		transaction = getSupportFragmentManager().beginTransaction();
		if (null == fiscalYearFragment) {
			fiscalYearFragment = new FiscalYearFragment();
			transaction.add(R.id.leftLayout, fiscalYearFragment);
		}
		transaction.show(fiscalYearFragment);
		if (null != filterFragment) {
			transaction.hide(filterFragment);
		}
		transaction.commit();
	}

	@Override
	public void onStartRank() {
		transaction = getSupportFragmentManager().beginTransaction();
		if (null == rankFragment) {
			rankFragment = new RankFragment();
			transaction.add(R.id.leftLayout, rankFragment);
		}
		transaction.show(rankFragment);
		if (null != filterFragment) {
			transaction.hide(filterFragment);
		}
		transaction.commit();
	}

	@Override
	public void onStartMilestone() {
		transaction = getSupportFragmentManager().beginTransaction();
		if (null == mileStoneFragment) {
			mileStoneFragment = new MileStoneFragment();
			transaction.add(R.id.leftLayout, mileStoneFragment);
		}
		transaction.show(mileStoneFragment);
		if (null != filterFragment) {
			transaction.hide(filterFragment);
		}
		transaction.commit();
	}

	@Override
	public void onStartOwnership() {
		transaction = getSupportFragmentManager().beginTransaction();
		if (null == ownershipFragment) {
			ownershipFragment = new OwnershipFragment();
			transaction.add(R.id.leftLayout, ownershipFragment);
		}
		transaction.show(ownershipFragment);
		if (null != filterFragment) {
			transaction.hide(filterFragment);
		}
		transaction.commit();
	}

	@Override
	public void onStartRevenueSize() {
		transaction = getSupportFragmentManager().beginTransaction();
		if (null == revenueSizeFragment) {
			revenueSizeFragment = new RevenueSizeFragment();
			transaction.add(R.id.leftLayout, revenueSizeFragment);
		}
		transaction.show(revenueSizeFragment);
		if (null != filterFragment) {
			transaction.hide(filterFragment);
		}
		transaction.commit();
	}

	@Override
	public void onStartEmployeeSize() {
		transaction = getSupportFragmentManager().beginTransaction();
		if (null == employeeSizeFragment) {
			employeeSizeFragment = new EmployeeSizeFragment();
			transaction.add(R.id.leftLayout, employeeSizeFragment);
		}
		transaction.show(employeeSizeFragment);
		if (null != filterFragment) {
			transaction.hide(filterFragment);
		}
		transaction.commit();
	}

	@Override
	public void onStartIndustry() {
		transaction = getSupportFragmentManager().beginTransaction();
		if (null == industryFragment) {
			industryFragment = new IndustryFragment();
			transaction.add(R.id.leftLayout, industryFragment);
		}
		transaction.show(industryFragment);
		if (null != filterFragment) {
			transaction.hide(filterFragment);
		}
		transaction.commit();
	}

	@Override
	public void onStartHeadquarters() {
		transaction = getSupportFragmentManager().beginTransaction();
		if (null == headquartersFragment) {
			headquartersFragment = new HeadquartersFragment();
			transaction.add(R.id.leftLayout, headquartersFragment);
		}
		transaction.show(headquartersFragment);
		if (null != filterFragment) {
			transaction.hide(filterFragment);
		}
		transaction.commit();
	}

	@Override
	public void onStartNewsTriggers() {
		transaction = getSupportFragmentManager().beginTransaction();
		if (null == newsTriggersFragment) {
			newsTriggersFragment = new NewsTriggersFragment();
			transaction.add(R.id.leftLayout, newsTriggersFragment);
		}
		transaction.show(newsTriggersFragment);
		if (null != filterFragment) {
			transaction.hide(filterFragment);
		}
		transaction.commit();
	}

	@Override
	public void onFiscalYearFinish() {
		transaction = getSupportFragmentManager().beginTransaction();
		if (null == filterFragment) {
			filterFragment = new PeopleFilterFragment();
			transaction.add(R.id.leftLayout, filterFragment);
		}
		transaction.show(filterFragment);
		if (null != fiscalYearFragment) {
			transaction.hide(fiscalYearFragment);
		}
		transaction.commit();
	}

	@Override
	public void onRankFinish() {
		transaction = getSupportFragmentManager().beginTransaction();
		if (null == filterFragment) {
			filterFragment = new PeopleFilterFragment();
			transaction.add(R.id.leftLayout, filterFragment);
		}
		transaction.show(filterFragment);
		if (null != rankFragment) {
			transaction.hide(rankFragment);
		}
		transaction.commit();
	}

	@Override
	public void onMileStoneFinish() {
		transaction = getSupportFragmentManager().beginTransaction();
		if (null == filterFragment) {
			filterFragment = new PeopleFilterFragment();
			transaction.add(R.id.leftLayout, filterFragment);
		}
		transaction.show(filterFragment);
		if (null != mileStoneFragment) {
			transaction.hide(mileStoneFragment);
		}
		transaction.commit();
	}

	@Override
	public void onOwnershipFinish() {
		transaction = getSupportFragmentManager().beginTransaction();
		if (null == filterFragment) {
			filterFragment = new PeopleFilterFragment();
			transaction.add(R.id.leftLayout, filterFragment);
		}
		transaction.show(filterFragment);
		if (null != ownershipFragment) {
			transaction.hide(ownershipFragment);
		}
		transaction.commit();
	}

	@Override
	public void onRevenueSizeFinish() {
		transaction = getSupportFragmentManager().beginTransaction();
		if (null == filterFragment) {
			filterFragment = new PeopleFilterFragment();
			transaction.add(R.id.leftLayout, filterFragment);
		}
		transaction.show(filterFragment);
		if (null != revenueSizeFragment) {
			transaction.hide(revenueSizeFragment);
		}
		transaction.commit();
	}

	@Override
	public void onEmployeeSizeFinish() {
		transaction = getSupportFragmentManager().beginTransaction();
		if (null == filterFragment) {
			filterFragment = new PeopleFilterFragment();
			transaction.add(R.id.leftLayout, filterFragment);
		}
		transaction.show(filterFragment);
		if (null != employeeSizeFragment) {
			transaction.hide(employeeSizeFragment);
		}
		transaction.commit();
	}

	@Override
	public void onIndustryFinish() {
		transaction = getSupportFragmentManager().beginTransaction();
		if (null == filterFragment) {
			filterFragment = new PeopleFilterFragment();
			transaction.add(R.id.leftLayout, filterFragment);
		}
		transaction.show(filterFragment);
		if (null != industryFragment) {
			transaction.hide(industryFragment);
		}
		transaction.commit();
	}

	@Override
	public void onHeadquartersFinish() {
		transaction = getSupportFragmentManager().beginTransaction();
		if (null == filterFragment) {
			filterFragment = new PeopleFilterFragment();
			transaction.add(R.id.leftLayout, filterFragment);
		}
		transaction.show(filterFragment);
		if (null != headquartersFragment) {
			transaction.hide(headquartersFragment);
		}
		transaction.commit();
	}

	@Override
	public void onNewsTriggersFinish() {
		transaction = getSupportFragmentManager().beginTransaction();
		if (null == filterFragment) {
			filterFragment = new PeopleFilterFragment();
			transaction.add(R.id.leftLayout, filterFragment);
		}
		transaction.show(filterFragment);
		if (null != newsTriggersFragment) {
			transaction.hide(newsTriggersFragment);
		}
		transaction.commit();
	}

	@Override
	public void onStartCompanies() {
		transaction = getSupportFragmentManager().beginTransaction();
		if (null == companiesFragment) {
			companiesFragment = new CompaniesFragment();
			transaction.add(R.id.leftLayout, companiesFragment);
		}
		transaction.show(companiesFragment);
		if (null != filterFragment) {
			transaction.hide(filterFragment);
		}
		transaction.commit();
	}

	@Override
	public void onStartFunctionalRole() {
		transaction = getSupportFragmentManager().beginTransaction();
		if (null == functionalRoleFragment) {
			functionalRoleFragment = new FunctionalRoleFragment();
			transaction.add(R.id.leftLayout, functionalRoleFragment);
		}
		transaction.show(functionalRoleFragment);
		if (null != filterFragment) {
			transaction.hide(filterFragment);
		}
		transaction.commit();
	}

	@Override
	public void onStartLocation() {
		transaction = getSupportFragmentManager().beginTransaction();
		if (null == locationFragment) {
			locationFragment = new LocationFragment();
			transaction.add(R.id.leftLayout, locationFragment);
		}
		transaction.show(locationFragment);
		if (null != filterFragment) {
			transaction.hide(filterFragment);
		}
		transaction.commit();
	}

	@Override
	public void onStartJobLevel() {
		transaction = getSupportFragmentManager().beginTransaction();
		if (null == jobLevelFragment) {
			jobLevelFragment = new JobLevelFragment();
			transaction.add(R.id.leftLayout, jobLevelFragment);
		}
		transaction.show(jobLevelFragment);
		if (null != filterFragment) {
			transaction.hide(filterFragment);
		}
		transaction.commit();
	}

	@Override
	public void onStartJobTitle() {
		transaction = getSupportFragmentManager().beginTransaction();
		if (null == jobTitleFragment) {
			jobTitleFragment = new JobTitleFragment();
			transaction.add(R.id.leftLayout, jobTitleFragment);
		}
		transaction.show(jobTitleFragment);
		if (null != filterFragment) {
			transaction.hide(filterFragment);
		}
		transaction.commit();
	}

	@Override
	public void onCompaniesFinish() {
		transaction = getSupportFragmentManager().beginTransaction();
		if (null == filterFragment) {
			filterFragment = new PeopleFilterFragment();
			transaction.add(R.id.leftLayout, filterFragment);
		}
		transaction.show(filterFragment);
		if (null != companiesFragment) {
			transaction.hide(companiesFragment);
		}
		transaction.commit();
	}

	@Override
	public void onLocationFinish() {
		transaction = getSupportFragmentManager().beginTransaction();
		if (null == filterFragment) {
			filterFragment = new PeopleFilterFragment();
			transaction.add(R.id.leftLayout, filterFragment);
		}
		transaction.show(filterFragment);
		if (null != locationFragment) {
			transaction.hide(locationFragment);
		}
		transaction.commit();
	}

	@Override
	public void onFunctionalRoleFinish() {
		transaction = getSupportFragmentManager().beginTransaction();
		if (null == filterFragment) {
			filterFragment = new PeopleFilterFragment();
			transaction.add(R.id.leftLayout, filterFragment);
		}
		transaction.show(filterFragment);
		if (null != functionalRoleFragment) {
			transaction.hide(functionalRoleFragment);
		}
		transaction.commit();
	}

	@Override
	public void onJobLevelFinish() {
		transaction = getSupportFragmentManager().beginTransaction();
		if (null == filterFragment) {
			filterFragment = new PeopleFilterFragment();
			transaction.add(R.id.leftLayout, filterFragment);
		}
		transaction.show(filterFragment);
		if (null != jobLevelFragment) {
			transaction.hide(jobLevelFragment);
		}
		transaction.commit();
	}

	@Override
	public void onJobTitleFinish() {
		transaction = getSupportFragmentManager().beginTransaction();
		if (null == filterFragment) {
			filterFragment = new PeopleFilterFragment();
			transaction.add(R.id.leftLayout, filterFragment);
		}
		transaction.show(filterFragment);
		if (null != jobTitleFragment) {
			transaction.hide(jobTitleFragment);
		}
		transaction.commit();
	}

	@Override
	public void onSearchFromLocation() {
		if (timerTask != null) {
			timerTask.cancel();
		}
		
		transaction = getSupportFragmentManager().beginTransaction();
		if (null == searchPeopleResultFragment) {
			searchPeopleResultFragment = new SearchPeopleResultFragment();
			transaction.add(R.id.rightLayout, searchPeopleResultFragment);
		}
		if (!searchPeopleResultFragment.isVisible()) {
			transaction.show(searchPeopleResultFragment);
		}
		if (null != peopleEmptyViewFragment) {
			transaction.hide(peopleEmptyViewFragment);
		}
		transaction.commit();
		timer = new Timer();
		timerTask = new TimerTask() {
			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						searchPeopleResultFragment.initSearch();
						Log.v("silen", "run()");
					}
				});
			}
		};
		timer.schedule(timerTask, 30);
	}

	@Override
	public void onSearchFromJobTitle() {
		if (timerTask != null) {
			timerTask.cancel();
		}
		
		transaction = getSupportFragmentManager().beginTransaction();
		if (null == searchPeopleResultFragment) {
			searchPeopleResultFragment = new SearchPeopleResultFragment();
			transaction.add(R.id.rightLayout, searchPeopleResultFragment);
		}
		if (!searchPeopleResultFragment.isVisible()) {
			transaction.show(searchPeopleResultFragment);
		}
		if (null != peopleEmptyViewFragment) {
			transaction.hide(peopleEmptyViewFragment);
		}
		transaction.commit();
		timer = new Timer();
		timerTask = new TimerTask() {
			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						searchPeopleResultFragment.initSearch();
						Log.v("silen", "run()");
					}
				});
			}
		};
		timer.schedule(timerTask, 30);
	}

	@Override
	public void onSearchFromJobLevel() {
		if (timerTask != null) {
			timerTask.cancel();
		}
		
		transaction = getSupportFragmentManager().beginTransaction();
		if (null == searchPeopleResultFragment) {
			searchPeopleResultFragment = new SearchPeopleResultFragment();
			transaction.add(R.id.rightLayout, searchPeopleResultFragment);
		}
		if (!searchPeopleResultFragment.isVisible()) {
			transaction.show(searchPeopleResultFragment);
		}
		if (null != peopleEmptyViewFragment) {
			transaction.hide(peopleEmptyViewFragment);
		}
		transaction.commit();
		timer = new Timer();
		timerTask = new TimerTask() {
			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						searchPeopleResultFragment.initSearch();
						Log.v("silen", "run()");
					}
				});
			}
		};
		timer.schedule(timerTask, 30);
	}

	@Override
	public void onSearchFromFunctionalRole() {
		if (timerTask != null) {
			timerTask.cancel();
		}
		
		transaction = getSupportFragmentManager().beginTransaction();
		if (null == searchPeopleResultFragment) {
			searchPeopleResultFragment = new SearchPeopleResultFragment();
			transaction.add(R.id.rightLayout, searchPeopleResultFragment);
		}
		if (!searchPeopleResultFragment.isVisible()) {
			transaction.show(searchPeopleResultFragment);
		}
		if (null != peopleEmptyViewFragment) {
			transaction.hide(peopleEmptyViewFragment);
		}
		transaction.commit();
		timer = new Timer();
		timerTask = new TimerTask() {
			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						searchPeopleResultFragment.initSearch();
						Log.v("silen", "run()");
					}
				});
			}
		};
		timer.schedule(timerTask, 30);
	}

	@Override
	public void onSearchFromCompanies() {
		if (timerTask != null) {
			timerTask.cancel();
		}
		
		transaction = getSupportFragmentManager().beginTransaction();
		if (null == searchPeopleResultFragment) {
			searchPeopleResultFragment = new SearchPeopleResultFragment();
			transaction.add(R.id.rightLayout, searchPeopleResultFragment);
		}
		if (!searchPeopleResultFragment.isVisible()) {
			transaction.show(searchPeopleResultFragment);
		}
		if (null != peopleEmptyViewFragment) {
			transaction.hide(peopleEmptyViewFragment);
		}
		transaction.commit();
		timer = new Timer();
		timerTask = new TimerTask() {
			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						searchPeopleResultFragment.initSearch();
						Log.v("silen", "run()");
					}
				});
			}
		};
		timer.schedule(timerTask, 30);
	}

	@Override
	public void onSearchFromRank() {
		if (timerTask != null) {
			timerTask.cancel();
		}
		
		transaction = getSupportFragmentManager().beginTransaction();
		if (null == searchPeopleResultFragment) {
			searchPeopleResultFragment = new SearchPeopleResultFragment();
			transaction.add(R.id.rightLayout, searchPeopleResultFragment);
		}
		if (!searchPeopleResultFragment.isVisible()) {
			transaction.show(searchPeopleResultFragment);
		}
		if (null != peopleEmptyViewFragment) {
			transaction.hide(peopleEmptyViewFragment);
		}
		transaction.commit();
		timer = new Timer();
		timerTask = new TimerTask() {
			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						searchPeopleResultFragment.initSearch();
						Log.v("silen", "run()");
					}
				});
			}
		};
		timer.schedule(timerTask, 30);
	}

	@Override
	public void onSearchFromNewsTriggers() {
		if (timerTask != null) {
			timerTask.cancel();
		}
		
		transaction = getSupportFragmentManager().beginTransaction();
		if (null == searchPeopleResultFragment) {
			searchPeopleResultFragment = new SearchPeopleResultFragment();
			transaction.add(R.id.rightLayout, searchPeopleResultFragment);
		}
		if (!searchPeopleResultFragment.isVisible()) {
			transaction.show(searchPeopleResultFragment);
		}
		if (null != peopleEmptyViewFragment) {
			transaction.hide(peopleEmptyViewFragment);
		}
		transaction.commit();
		timer = new Timer();
		timerTask = new TimerTask() {
			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						searchPeopleResultFragment.initSearch();
						Log.v("silen", "run()");
					}
				});
			}
		};
		timer.schedule(timerTask, 30);
	}

	@Override
	public void onSearchFromEmployeeSize() {
		if (timerTask != null) {
			timerTask.cancel();
		}
		
		transaction = getSupportFragmentManager().beginTransaction();
		if (null == searchPeopleResultFragment) {
			searchPeopleResultFragment = new SearchPeopleResultFragment();
			transaction.add(R.id.rightLayout, searchPeopleResultFragment);
		}
		if (!searchPeopleResultFragment.isVisible()) {
			transaction.show(searchPeopleResultFragment);
		}
		if (null != peopleEmptyViewFragment) {
			transaction.hide(peopleEmptyViewFragment);
		}
		transaction.commit();
		timer = new Timer();
		timerTask = new TimerTask() {
			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						searchPeopleResultFragment.initSearch();
						Log.v("silen", "run()");
					}
				});
			}
		};
		timer.schedule(timerTask, 30);
	}

	@Override
	public void onSearchFromRevenueSize() {
		if (timerTask != null) {
			timerTask.cancel();
		}
		
		transaction = getSupportFragmentManager().beginTransaction();
		if (null == searchPeopleResultFragment) {
			searchPeopleResultFragment = new SearchPeopleResultFragment();
			transaction.add(R.id.rightLayout, searchPeopleResultFragment);
		}
		if (!searchPeopleResultFragment.isVisible()) {
			transaction.show(searchPeopleResultFragment);
		}
		if (null != peopleEmptyViewFragment) {
			transaction.hide(peopleEmptyViewFragment);
		}
		transaction.commit();
		timer = new Timer();
		timerTask = new TimerTask() {
			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						searchPeopleResultFragment.initSearch();
						Log.v("silen", "run()");
					}
				});
			}
		};
		timer.schedule(timerTask, 30);
	}

	@Override
	public void onSearchFromOwnership() {
		if (timerTask != null) {
			timerTask.cancel();
		}
		
		transaction = getSupportFragmentManager().beginTransaction();
		if (null == searchPeopleResultFragment) {
			searchPeopleResultFragment = new SearchPeopleResultFragment();
			transaction.add(R.id.rightLayout, searchPeopleResultFragment);
		}
		if (!searchPeopleResultFragment.isVisible()) {
			transaction.show(searchPeopleResultFragment);
		}
		if (null != peopleEmptyViewFragment) {
			transaction.hide(peopleEmptyViewFragment);
		}
		transaction.commit();
		timer = new Timer();
		timerTask = new TimerTask() {
			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						searchPeopleResultFragment.initSearch();
						Log.v("silen", "run()");
					}
				});
			}
		};
		timer.schedule(timerTask, 30);
	}

	@Override
	public void onSearchFromMileStone() {
		if (timerTask != null) {
			timerTask.cancel();
		}
		
		transaction = getSupportFragmentManager().beginTransaction();
		if (null == searchPeopleResultFragment) {
			searchPeopleResultFragment = new SearchPeopleResultFragment();
			transaction.add(R.id.rightLayout, searchPeopleResultFragment);
		}
		if (!searchPeopleResultFragment.isVisible()) {
			transaction.show(searchPeopleResultFragment);
		}
		if (null != peopleEmptyViewFragment) {
			transaction.hide(peopleEmptyViewFragment);
		}
		transaction.commit();
		timer = new Timer();
		timerTask = new TimerTask() {
			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						searchPeopleResultFragment.initSearch();
						Log.v("silen", "run()");
					}
				});
			}
		};
		timer.schedule(timerTask, 30);
	}

	@Override
	public void onSearchFromIndustry() {
		if (timerTask != null) {
			timerTask.cancel();
		}
		
		transaction = getSupportFragmentManager().beginTransaction();
		if (null == searchPeopleResultFragment) {
			searchPeopleResultFragment = new SearchPeopleResultFragment();
			transaction.add(R.id.rightLayout, searchPeopleResultFragment);
		}
		if (!searchPeopleResultFragment.isVisible()) {
			transaction.show(searchPeopleResultFragment);
		}
		if (null != peopleEmptyViewFragment) {
			transaction.hide(peopleEmptyViewFragment);
		}
		transaction.commit();
		timer = new Timer();
		timerTask = new TimerTask() {
			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						searchPeopleResultFragment.initSearch();
						Log.v("silen", "run()");
					}
				});
			}
		};
		timer.schedule(timerTask, 30);
	}

	@Override
	public void onSearchFromHeadquarters() {
		if (timerTask != null) {
			timerTask.cancel();
		}
		
		transaction = getSupportFragmentManager().beginTransaction();
		if (null == searchPeopleResultFragment) {
			searchPeopleResultFragment = new SearchPeopleResultFragment();
			transaction.add(R.id.rightLayout, searchPeopleResultFragment);
		} 
		
		if (!searchPeopleResultFragment.isVisible()) {
			transaction.show(searchPeopleResultFragment);
		}
		if (null != peopleEmptyViewFragment) {
			transaction.hide(peopleEmptyViewFragment);
		}
		transaction.commit();
		timer = new Timer();
		timerTask = new TimerTask() {
			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						searchPeopleResultFragment.initSearch();
						Log.v("silen", "run()");
					}
				});
			}
		};
		timer.schedule(timerTask, 30);
	}

	@Override
	public void onSearchFromFiscalYear() {
		if (timerTask != null) {
			timerTask.cancel();
		}
		
		transaction = getSupportFragmentManager().beginTransaction();
		if (null == searchPeopleResultFragment) {
			searchPeopleResultFragment = new SearchPeopleResultFragment();
			transaction.add(R.id.rightLayout, searchPeopleResultFragment);
		}
		if (!searchPeopleResultFragment.isVisible()) {
			transaction.show(searchPeopleResultFragment);
		}
		if (null != peopleEmptyViewFragment) {
			transaction.hide(peopleEmptyViewFragment);
		}
		transaction.commit();
		timer = new Timer();
		timerTask = new TimerTask() {
			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						searchPeopleResultFragment.initSearch();
						Log.v("silen", "run()");
					}
				});
			}
		};
		timer.schedule(timerTask, 30);
	}
	
	private void setLeftLayoutVisible(int visible) {
		leftLayout.setVisibility(visible);
	}

	@Override
	public void onHideLeftLayout() {
		setLeftLayoutVisible(leftLayout.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
	}

	@Override
	public void onStartSearchFromPeople() {
		onHideLeftLayout();
		if (timerTask != null) {
			timerTask.cancel();
		}
		
		transaction = getSupportFragmentManager().beginTransaction();
		if (null == searchPeopleResultFragment) {
			searchPeopleResultFragment = new SearchPeopleResultFragment();
			transaction.add(R.id.rightLayout, searchPeopleResultFragment);
			
			timer = new Timer();
			timerTask = new TimerTask() {
				@Override
				public void run() {
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							searchPeopleResultFragment.initSearch();
							Log.v("silen", "run()");
						}
					});
				}
			};
			timer.schedule(timerTask, 200);
		}
		if (!searchPeopleResultFragment.isVisible()) {
			transaction.show(searchPeopleResultFragment);
		}
		if (null != peopleEmptyViewFragment) {
			transaction.hide(peopleEmptyViewFragment);
		}
		transaction.commit();
	}

	@Override
	public void onCanleTask() {
		if (timerTask != null) {
			timerTask.cancel();
		}
	}

	@Override
	public void onUpdateFilterStatusForPeople() {
		
		if (null != jobTitleFragment) {
			jobTitleFragment.setInitialData();
		}
		
		if (null != jobLevelFragment) {
			jobLevelFragment.setInitialData();
		}
		
		if (null != locationFragment) {
			locationFragment.setInitialData();
		}
		
		if (null != functionalRoleFragment) {
			functionalRoleFragment.setInitialData();
		}
		
		if (null != newsTriggersFragment) {
			newsTriggersFragment.setInitialData();
		}
		
		if (null != companiesFragment) {
			companiesFragment.setInitialData();
		}
		
		if (null != headquartersFragment) {
			headquartersFragment.setInitialData();
		}
		
		if (null != industryFragment) {
			industryFragment.setInitialData();
		}
		
		if (null != employeeSizeFragment) {
			employeeSizeFragment.setInitialData();
		}
		
		if (null != revenueSizeFragment) {
			revenueSizeFragment.setInitialData();
		}
		
		if (null != ownershipFragment) {
			ownershipFragment.setInitialData();
		}
		
		if (null != mileStoneFragment) {
			mileStoneFragment.setInitialData();
		}
		
		if (null != rankFragment) {
			rankFragment.setInitialData();
		}
		
		if (null != fiscalYearFragment) {
			fiscalYearFragment.setInitialData();
		}
		
		
	}
	
	@Override
	public void onBackPressed() {
		if (leftLayout.getVisibility() == View.VISIBLE) {
			if (companiesFragment != null && companiesFragment.isVisible()) {
				if (!companiesFragment.back()) return;
			}
			setLeftLayoutVisible(View.GONE);
		} else {
			super.onBackPressed();
			return;
		}
	}
}
