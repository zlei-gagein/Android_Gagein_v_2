package com.gagein.ui.tablet.search.company;

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
import com.gagein.ui.tablet.search.CompanyEmptyViewFragment;
import com.gagein.ui.tablet.search.CompanyEmptyViewFragment.OnCompanyEmptyViewFinish;
import com.gagein.ui.tablet.search.company.CompaniesFragment.OnCanleTask;
import com.gagein.ui.tablet.search.company.CompaniesFragment.OnCompaniesFinish;
import com.gagein.ui.tablet.search.company.CompaniesFragment.OnSearchFromCompanies;
import com.gagein.ui.tablet.search.company.CompanyFilterFragment.OnFinishActivity;
import com.gagein.ui.tablet.search.company.CompanyFilterFragment.OnStartCompanies;
import com.gagein.ui.tablet.search.company.CompanyFilterFragment.OnStartEmployeeSize;
import com.gagein.ui.tablet.search.company.CompanyFilterFragment.OnStartFiscalYear;
import com.gagein.ui.tablet.search.company.CompanyFilterFragment.OnStartHeadquarters;
import com.gagein.ui.tablet.search.company.CompanyFilterFragment.OnStartIndustry;
import com.gagein.ui.tablet.search.company.CompanyFilterFragment.OnStartMilestone;
import com.gagein.ui.tablet.search.company.CompanyFilterFragment.OnStartNewsTriggers;
import com.gagein.ui.tablet.search.company.CompanyFilterFragment.OnStartOwnership;
import com.gagein.ui.tablet.search.company.CompanyFilterFragment.OnStartRank;
import com.gagein.ui.tablet.search.company.CompanyFilterFragment.OnStartRevenueSize;
import com.gagein.ui.tablet.search.company.CompanyFilterFragment.OnStartSearchFromCompany;
import com.gagein.ui.tablet.search.company.EmployeeSizeFragment.OnEmployeeSizeFinish;
import com.gagein.ui.tablet.search.company.EmployeeSizeFragment.OnSearchFromEmployeeSize;
import com.gagein.ui.tablet.search.company.FiscalYearFragment.OnFiscalYearFinish;
import com.gagein.ui.tablet.search.company.FiscalYearFragment.OnSearchFromFiscalYear;
import com.gagein.ui.tablet.search.company.HeadquartersFragment.OnHeadquartersFinish;
import com.gagein.ui.tablet.search.company.HeadquartersFragment.OnSearchFromHeadquarters;
import com.gagein.ui.tablet.search.company.IndustryFragment.OnIndustryFinish;
import com.gagein.ui.tablet.search.company.IndustryFragment.OnSearchFromIndustry;
import com.gagein.ui.tablet.search.company.MileStoneFragment.OnMileStoneFinish;
import com.gagein.ui.tablet.search.company.MileStoneFragment.OnSearchFromMileStone;
import com.gagein.ui.tablet.search.company.NewsTriggersFragment.OnNewsTriggersFinish;
import com.gagein.ui.tablet.search.company.NewsTriggersFragment.OnSearchFromNewsTriggers;
import com.gagein.ui.tablet.search.company.OwnershipFragment.OnOwnershipFinish;
import com.gagein.ui.tablet.search.company.OwnershipFragment.OnSearchFromOwnership;
import com.gagein.ui.tablet.search.company.RankFragment.OnRankFinish;
import com.gagein.ui.tablet.search.company.RankFragment.OnSearchFromRank;
import com.gagein.ui.tablet.search.company.RevenueSizeFragment.OnRevenueSizeFinish;
import com.gagein.ui.tablet.search.company.RevenueSizeFragment.OnSearchFromRevenueSize;
import com.gagein.ui.tablet.search.company.SearchCompanyResultFragment.OnHideLeftLayout;
import com.gagein.util.CommonUtil;
import com.gagein.util.Constant;
import com.gagein.util.Log;

public class CompanySearchTabletActivity extends BaseFragmentActivity implements OnFinishActivity, OnStartNewsTriggers, OnNewsTriggersFinish
 	, OnStartHeadquarters , OnHeadquartersFinish, OnIndustryFinish, OnStartIndustry, OnStartEmployeeSize , OnEmployeeSizeFinish,
 	OnRevenueSizeFinish, OnStartRevenueSize, OnStartOwnership, OnOwnershipFinish, OnStartMilestone, OnMileStoneFinish, OnStartRank
 	, OnRankFinish, OnStartFiscalYear, OnFiscalYearFinish, OnCompanyEmptyViewFinish, OnSearchFromRank, OnSearchFromNewsTriggers, 
 	OnSearchFromEmployeeSize, OnSearchFromFiscalYear, OnSearchFromHeadquarters, OnSearchFromIndustry, OnSearchFromMileStone, 
 	OnSearchFromOwnership, OnSearchFromRevenueSize, OnHideLeftLayout, OnStartSearchFromCompany, OnStartCompanies, OnCompaniesFinish, 
 	OnSearchFromCompanies, OnCanleTask{
	
	private FragmentTransaction transaction;
	private LinearLayout leftLayout;
	private CompanyFilterFragment filterFragment;
	private SearchCompanyResultFragment searchCompanyResultFragment;
	private NewsTriggersFragment newsTriggersFragment;
	private CompaniesFragment companiesFragment;
	private CompanyEmptyViewFragment companyEmptyViewFragment;
	private HeadquartersFragment headquartersFragment;
	private IndustryFragment industryFragment;
	private EmployeeSizeFragment employeeSizeFragment;
	private RevenueSizeFragment revenueSizeFragment;
	private OwnershipFragment ownershipFragment;
	private MileStoneFragment mileStoneFragment;
	private RankFragment rankFragment;
	private FiscalYearFragment fiscalYearFragment;
	private Timer timer;
	private TimerTask timerTask;
	private String savedId = "";
	private IntentFilter intentFilter = new IntentFilter(Constant.BROADCAST_REFRESH_FILTER);
	private RefreshFilterBroadcastReceiver receiver = new RefreshFilterBroadcastReceiver();
	
	@Override
	protected List<String> observeNotifications() {
		return stringList(Constant.BROADCAST_FOLLOW_COMPANY, Constant.BROADCAST_UNFOLLOW_COMPANY);
	}
	
	@Override
	public void handleNotifications(Context aContext, Intent intent) {
		super.handleNotifications(aContext, intent);
		
		String actionName = intent.getAction();
		if (actionName.equals(Constant.BROADCAST_FOLLOW_COMPANY)) {
			
			searchCompanyResultFragment.setFollowOrUnFollow(intent, true);
			
		} else if (actionName.equals(Constant.BROADCAST_UNFOLLOW_COMPANY)) {
			
			searchCompanyResultFragment.setFollowOrUnFollow(intent, false);
			
		}
	}
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_tablet_main);
		leftLayout = (LinearLayout) findViewById(R.id.leftLayout);
		
		initFilterData();
		
		savedId = getIntent().getStringExtra(Constant.SAVEDID);
		transaction = getSupportFragmentManager().beginTransaction();
		filterFragment = new CompanyFilterFragment();
		
		if (null == savedId || savedId.isEmpty()) {
			companyEmptyViewFragment= new CompanyEmptyViewFragment();
			
			transaction.add(R.id.leftLayout, filterFragment);
			transaction.add(R.id.rightLayout, companyEmptyViewFragment);
		} else {//带参数进来
			searchCompanyResultFragment = new SearchCompanyResultFragment();
			transaction.add(R.id.leftLayout, filterFragment);
			transaction.add(R.id.rightLayout, searchCompanyResultFragment);
			setLeftLayoutVisible(View.GONE);
			
			timer = new Timer();
			timerTask = new TimerTask() {
				@Override
				public void run() {
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							searchCompanyResultFragment.searchSavedResult(savedId);
							Log.v("silen", "run()");
						}
					});
				}
			};
			timer.schedule(timerTask, 500);
		}
		transaction.commit();
		
		registerReceiver(receiver, intentFilter);
	}
	
	public class RefreshFilterBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
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

	private void initFilterData() {
		if (null != Constant.MFILTERS) {
			Constant.MFILTERS.getHeadquarters().clear();
		}
		Constant.REVERSE = false;
		Constant.COMPANY_SEARCH_KEYWORDS = "";
		CommonUtil.resetFilters();
		CommonUtil.initBuzSortBy();
	}
	
	private void setLeftLayoutVisible(int visible) {
		leftLayout.setVisibility(visible);
	}

	@Override
	public void onFinishActivity() {
		finish();
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
	public void onNewsTriggersFinish() {
		transaction = getSupportFragmentManager().beginTransaction();
		if (null == filterFragment) {
			filterFragment = new CompanyFilterFragment();
			transaction.add(R.id.leftLayout, filterFragment);
		}
		transaction.show(filterFragment);
		if (null != newsTriggersFragment) {
			transaction.hide(newsTriggersFragment);
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
	public void onHeadquartersFinish() {
		transaction = getSupportFragmentManager().beginTransaction();
		if (null == filterFragment) {
			filterFragment = new CompanyFilterFragment();
			transaction.add(R.id.leftLayout, filterFragment);
		}
		transaction.show(filterFragment);
		if (null != headquartersFragment) {
			transaction.hide(headquartersFragment);
		}
		transaction.commit();
	}

	@Override
	public void onIndustryFinish() {
		transaction = getSupportFragmentManager().beginTransaction();
		if (null == filterFragment) {
			filterFragment = new CompanyFilterFragment();
			transaction.add(R.id.leftLayout, filterFragment);
		}
		transaction.show(filterFragment);
		if (null != industryFragment) {
			transaction.hide(industryFragment);
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
	public void onEmployeeSizeFinish() {
		transaction = getSupportFragmentManager().beginTransaction();
		if (null == filterFragment) {
			filterFragment = new CompanyFilterFragment();
			transaction.add(R.id.leftLayout, filterFragment);
		}
		transaction.show(filterFragment);
		if (null != employeeSizeFragment) {
			transaction.hide(employeeSizeFragment);
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
	public void onRevenueSizeFinish() {
		transaction = getSupportFragmentManager().beginTransaction();
		if (null == filterFragment) {
			filterFragment = new CompanyFilterFragment();
			transaction.add(R.id.leftLayout, filterFragment);
		}
		transaction.show(filterFragment);
		if (null != revenueSizeFragment) {
			transaction.hide(revenueSizeFragment);
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
	public void onOwnershipFinish() {
		transaction = getSupportFragmentManager().beginTransaction();
		if (null == filterFragment) {
			filterFragment = new CompanyFilterFragment();
			transaction.add(R.id.leftLayout, filterFragment);
		}
		transaction.show(filterFragment);
		if (null != ownershipFragment) {
			transaction.hide(ownershipFragment);
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
	public void onMileStoneFinish() {
		transaction = getSupportFragmentManager().beginTransaction();
		if (null == filterFragment) {
			filterFragment = new CompanyFilterFragment();
			transaction.add(R.id.leftLayout, filterFragment);
		}
		transaction.show(filterFragment);
		if (null != mileStoneFragment) {
			transaction.hide(mileStoneFragment);
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
	public void onFiscalYearFinish() {
		transaction = getSupportFragmentManager().beginTransaction();
		if (null == filterFragment) {
			filterFragment = new CompanyFilterFragment();
			transaction.add(R.id.leftLayout, filterFragment);
		}
		transaction.show(filterFragment);
		if (null != fiscalYearFragment) {
			transaction.hide(fiscalYearFragment);
		}
		transaction.commit();
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
	public void onRankFinish() {
		transaction = getSupportFragmentManager().beginTransaction();
		if (null == filterFragment) {
			filterFragment = new CompanyFilterFragment();
			transaction.add(R.id.leftLayout, filterFragment);
		}
		transaction.show(filterFragment);
		if (null != rankFragment) {
			transaction.hide(rankFragment);
		}
		transaction.commit();
	}

	@Override
	public void onCompanyEmptyViewFinish() {
		setLeftLayoutVisible(leftLayout.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
	}

	@Override
	public void onSearchFromRank() {
		if (timerTask != null) {
			timerTask.cancel();
		}
		
		transaction = getSupportFragmentManager().beginTransaction();
		if (null == searchCompanyResultFragment) {
			searchCompanyResultFragment = new SearchCompanyResultFragment();
			transaction.add(R.id.rightLayout, searchCompanyResultFragment);
		}
		if (!searchCompanyResultFragment.isVisible()) {
			transaction.show(searchCompanyResultFragment);
		}
		if (null != companyEmptyViewFragment) {
			transaction.hide(companyEmptyViewFragment);
		}
		transaction.commit();
		timer = new Timer();
		timerTask = new TimerTask() {
			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						searchCompanyResultFragment.initSearch();
						Log.v("silen", "run()");
					}
				});
			}
		};
		timer.schedule(timerTask, 1000);
	}

	@Override
	public void onSearchFromNewsTriggers() {
		if (timerTask != null) {
			timerTask.cancel();
		}
		
		transaction = getSupportFragmentManager().beginTransaction();
		if (null == searchCompanyResultFragment) {
			searchCompanyResultFragment = new SearchCompanyResultFragment();
			transaction.add(R.id.rightLayout, searchCompanyResultFragment);
		}
		if (!searchCompanyResultFragment.isVisible()) {
			transaction.show(searchCompanyResultFragment);
		}
		if (null != companyEmptyViewFragment) {
			transaction.hide(companyEmptyViewFragment);
		}
		transaction.commit();
		timer = new Timer();
		timerTask = new TimerTask() {
			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						searchCompanyResultFragment.initSearch();
						Log.v("silen", "run()");
					}
				});
			}
		};
		timer.schedule(timerTask, 1000);
	}

	@Override
	public void onSearchFromRevenueSize() {
		if (timerTask != null) {
			timerTask.cancel();
		}
		
		transaction = getSupportFragmentManager().beginTransaction();
		if (null == searchCompanyResultFragment) {
			searchCompanyResultFragment = new SearchCompanyResultFragment();
			transaction.add(R.id.rightLayout, searchCompanyResultFragment);
		}
		if (!searchCompanyResultFragment.isVisible()) {
			transaction.show(searchCompanyResultFragment);
		}
		if (null != companyEmptyViewFragment) {
			transaction.hide(companyEmptyViewFragment);
		}
		transaction.commit();
		timer = new Timer();
		timerTask = new TimerTask() {
			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						searchCompanyResultFragment.initSearch();
						Log.v("silen", "run()");
					}
				});
			}
		};
		timer.schedule(timerTask, 1000);
	}

	@Override
	public void onSearchFromOwnership() {
		if (timerTask != null) {
			timerTask.cancel();
		}
		
		transaction = getSupportFragmentManager().beginTransaction();
		if (null == searchCompanyResultFragment) {
			searchCompanyResultFragment = new SearchCompanyResultFragment();
			transaction.add(R.id.rightLayout, searchCompanyResultFragment);
		}
		if (!searchCompanyResultFragment.isVisible()) {
			transaction.show(searchCompanyResultFragment);
		}
		if (null != companyEmptyViewFragment) {
			transaction.hide(companyEmptyViewFragment);
		}
		transaction.commit();
		timer = new Timer();
		timerTask = new TimerTask() {
			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						searchCompanyResultFragment.initSearch();
						Log.v("silen", "run()");
					}
				});
			}
		};
		timer.schedule(timerTask, 1000);
	}

	@Override
	public void onSearchFromMileStone() {
		if (timerTask != null) {
			timerTask.cancel();
		}
		
		transaction = getSupportFragmentManager().beginTransaction();
		if (null == searchCompanyResultFragment) {
			searchCompanyResultFragment = new SearchCompanyResultFragment();
			transaction.add(R.id.rightLayout, searchCompanyResultFragment);
		}
		if (!searchCompanyResultFragment.isVisible()) {
			transaction.show(searchCompanyResultFragment);
		}
		if (null != companyEmptyViewFragment) {
			transaction.hide(companyEmptyViewFragment);
		}
		transaction.commit();
		timer = new Timer();
		timerTask = new TimerTask() {
			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						searchCompanyResultFragment.initSearch();
						Log.v("silen", "run()");
					}
				});
			}
		};
		timer.schedule(timerTask, 1000);
	}

	@Override
	public void onSearchFromIndustry() {
		if (timerTask != null) {
			timerTask.cancel();
		}
		
		transaction = getSupportFragmentManager().beginTransaction();
		if (null == searchCompanyResultFragment) {
			searchCompanyResultFragment = new SearchCompanyResultFragment();
			transaction.add(R.id.rightLayout, searchCompanyResultFragment);
		}
		if (!searchCompanyResultFragment.isVisible()) {
			transaction.show(searchCompanyResultFragment);
		}
		if (null != companyEmptyViewFragment) {
			transaction.hide(companyEmptyViewFragment);
		}
		transaction.commit();
		timer = new Timer();
		timerTask = new TimerTask() {
			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						searchCompanyResultFragment.initSearch();
						Log.v("silen", "run()");
					}
				});
			}
		};
		timer.schedule(timerTask, 1000);
	}

	@Override
	public void onSearchFromHeadquarters() {
		if (timerTask != null) {
			timerTask.cancel();
		}
		
		transaction = getSupportFragmentManager().beginTransaction();
		if (null == searchCompanyResultFragment) {
			searchCompanyResultFragment = new SearchCompanyResultFragment();
			transaction.add(R.id.rightLayout, searchCompanyResultFragment);
		}
		if (!searchCompanyResultFragment.isVisible()) {
			transaction.show(searchCompanyResultFragment);
		}
		if (null != companyEmptyViewFragment) {
			transaction.hide(companyEmptyViewFragment);
		}
		transaction.commit();
		timer = new Timer();
		timerTask = new TimerTask() {
			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						searchCompanyResultFragment.initSearch();
						Log.v("silen", "run()");
					}
				});
			}
		};
		timer.schedule(timerTask, 1000);
	}

	@Override
	public void onSearchFromFiscalYear() {
		if (timerTask != null) {
			timerTask.cancel();
		}
		
		transaction = getSupportFragmentManager().beginTransaction();
		if (null == searchCompanyResultFragment) {
			searchCompanyResultFragment = new SearchCompanyResultFragment();
			transaction.add(R.id.rightLayout, searchCompanyResultFragment);
		}
		if (!searchCompanyResultFragment.isVisible()) {
			transaction.show(searchCompanyResultFragment);
		}
		if (null != companyEmptyViewFragment) {
			transaction.hide(companyEmptyViewFragment);
		}
		transaction.commit();
		timer = new Timer();
		timerTask = new TimerTask() {
			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						searchCompanyResultFragment.initSearch();
						Log.v("silen", "run()");
					}
				});
			}
		};
		timer.schedule(timerTask, 1000);
	}

	@Override
	public void onSearchFromEmployeeSize() {
		if (timerTask != null) {
			timerTask.cancel();
		}
		
		transaction = getSupportFragmentManager().beginTransaction();
		if (null == searchCompanyResultFragment) {
			searchCompanyResultFragment = new SearchCompanyResultFragment();
			transaction.add(R.id.rightLayout, searchCompanyResultFragment);
		}
		if (!searchCompanyResultFragment.isVisible()) {
			transaction.show(searchCompanyResultFragment);
		}
		if (null != companyEmptyViewFragment) {
			transaction.hide(companyEmptyViewFragment);
		}
		transaction.commit();
		timer = new Timer();
		timerTask = new TimerTask() {
			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						searchCompanyResultFragment.initSearch();
					}
				});
			}
		};
		timer.schedule(timerTask, 1000);
	}

	@Override
	public void onHideLeftLayout() {
		setLeftLayoutVisible(leftLayout.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
	}

	@Override
	public void onStartSearchFromCompany() {
		if (timerTask != null) {
			timerTask.cancel();
		}
		onHideLeftLayout();
		
		transaction = getSupportFragmentManager().beginTransaction();
		if (null == searchCompanyResultFragment) {
			searchCompanyResultFragment = new SearchCompanyResultFragment();
			transaction.add(R.id.rightLayout, searchCompanyResultFragment);
			
			timer = new Timer();
			timerTask = new TimerTask() {
				@Override
				public void run() {
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							searchCompanyResultFragment.initSearch();
							Log.v("silen", "run()");
						}
					});
				}
			};
			timer.schedule(timerTask, 200);
		}
		if (!searchCompanyResultFragment.isVisible()) {
			transaction.show(searchCompanyResultFragment);
		}
		if (null != companyEmptyViewFragment) {
			transaction.hide(companyEmptyViewFragment);
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
	public void onSearchFromCompanies() {
		if (timerTask != null) {
			timerTask.cancel();
		}
		
		transaction = getSupportFragmentManager().beginTransaction();
		if (null == searchCompanyResultFragment) {
			searchCompanyResultFragment = new SearchCompanyResultFragment();
			transaction.add(R.id.rightLayout, searchCompanyResultFragment);
		}
		if (!searchCompanyResultFragment.isVisible()) {
			transaction.show(searchCompanyResultFragment);
		}
		if (null != companyEmptyViewFragment) {
			transaction.hide(companyEmptyViewFragment);
		}
		transaction.commit();
		timer = new Timer();
		timerTask = new TimerTask() {
			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						searchCompanyResultFragment.initSearch();
						Log.v("silen", "run()");
					}
				});
			}
		};
		timer.schedule(timerTask, 1000);
	}

	@Override
	public void onCompaniesFinish() {
		transaction = getSupportFragmentManager().beginTransaction();
		if (null == filterFragment) {
			filterFragment = new CompanyFilterFragment();
			transaction.add(R.id.leftLayout, filterFragment);
		}
		transaction.show(filterFragment);
		if (null != companiesFragment) {
			transaction.hide(companiesFragment);
		}
		transaction.commit();
	}

	@Override
	public void onCanleTask() {
		if (timerTask != null) {
			timerTask.cancel();
		}
	}
	
//	@Override
//	public void onBackPressed() {
//		if (!filterFragment.isVisible()) {
//			transaction = getSupportFragmentManager().beginTransaction();
//			transaction.show(filterFragment);
//			transaction.commit();
//		} else {
//			super.onBackPressed();
//			return;
//		}
//	}

}
                                                