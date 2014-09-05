package com.gagein.ui.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.gagein.R;
import com.gagein.adapter.SearchCompanyAdapter;
import com.gagein.adapter.SearchPersonAdapter;
import com.gagein.adapter.search.SearchSavedAdapter;
import com.gagein.component.dialog.AddNewCompanyDialog;
import com.gagein.component.dialog.NewCompanySubmittedPromtDialog;
import com.gagein.component.dialog.UpgradeDialog;
import com.gagein.component.dialog.VerifingWebsiteConnectTimeOutDialog;
import com.gagein.component.dialog.VerifyingWebsiteDialog;
import com.gagein.component.xlistview.XListView;
import com.gagein.component.xlistview.XListView.IXListViewListener;
import com.gagein.http.APIHttp;
import com.gagein.http.APIParser;
import com.gagein.model.Company;
import com.gagein.model.DataPage;
import com.gagein.model.Person;
import com.gagein.model.SavedSearch;
import com.gagein.ui.company.CompanyActivity;
import com.gagein.ui.company.PersonActivity;
import com.gagein.ui.main.BaseActivity;
import com.gagein.ui.search.company.filter.CompanyFilterActivity;
import com.gagein.ui.search.people.filter.PeopleFilterActivity;
import com.gagein.ui.tablet.company.CompanyTabletActivity;
import com.gagein.ui.tablet.search.company.CompanySearchTabletActivity;
import com.gagein.ui.tablet.search.people.PeopleSearchTabletActivity;
import com.gagein.util.CommonUtil;
import com.gagein.util.Constant;
import com.gagein.util.Log;
import com.gagein.util.MessageCode;
import com.gagein.util.Utils;

public class SearchActivity extends BaseActivity implements OnItemClickListener, IXListViewListener{
	
	private EditText searchEdt;
	private TextView addNewCompany;
	private TextView edit;
	private List<Company> searchCompanies = new ArrayList<Company>();
	private List<Person> searchPersons = new ArrayList<Person>();
	private ArrayList<Company> matchedCompanies = new ArrayList<Company>();
	private TimerTask timerTask;
	private Timer timer;
	private int PAGE_NUM_COMPANY = 1;
	private int PAGE_NUM_PERSON = 1;
	private int PAGE_NUM_SAVESEARCH= 1;
	private LinearLayout mainLayout;
	private RelativeLayout noSavedSearches;
	private LinearLayout searchLayout;
	private LinearLayout noResultsLayout;
	private LinearLayout companyFoundLayout;
	private TextView companiesFoundNum;
	private TextView companiesFound;
	private TextView addDifferentCompany;
	private ListView foundCompanyList;
	private Button buildCompany;
	private Button buildPeople;
	private XListView savedList;
	private RadioButton companiesBtn;
	private RadioButton peopleBtn;
	private XListView companyList;
	private XListView personList;
	private SearchCompanyAdapter companyAdapter;
	private SearchPersonAdapter personAdapter;
	private Boolean isCompany = false;
	private Boolean isPerson = false;
	private List<SavedSearch> mSavedSearchs = new ArrayList<SavedSearch>();
	private SearchSavedAdapter savedAdapter;
    private Boolean havePurchased = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_main);
			
		doInit();
	}
    
	@Override
	protected void initView() {
		super.initView();
		
		setTitle(R.string.u_search);
		
		
		searchEdt = (EditText) findViewById(R.id.searchEdt);
		addNewCompany = (TextView) findViewById(R.id.addNewCompany);
		edit = (TextView) findViewById(R.id.edit);
		companiesFoundNum = (TextView) findViewById(R.id.companiesFoundNum);
		companiesFound = (TextView) findViewById(R.id.companiesFound);
		addDifferentCompany = (TextView) findViewById(R.id.addDifferentCompany);
		foundCompanyList = (ListView) findViewById(R.id.foundCompanyListView);
		mainLayout = (LinearLayout) findViewById(R.id.mainLayout);
		noSavedSearches = (RelativeLayout) findViewById(R.id.noSavedSearches);
		searchLayout = (LinearLayout) findViewById(R.id.searchLayout);
		noResultsLayout = (LinearLayout) findViewById(R.id.noResultsLayout);
		companyFoundLayout = (LinearLayout) findViewById(R.id.companyFoundLayout);
		buildCompany = (Button) findViewById(R.id.buildCompany);
		buildPeople = (Button) findViewById(R.id.buildPeople);
		savedList = (XListView) findViewById(R.id.savedList);
		companiesBtn = (RadioButton) findViewById(R.id.companiesBtn);
		peopleBtn = (RadioButton) findViewById(R.id.peopleBtn);
		companyList = (XListView) findViewById(R.id.companyList);
		personList = (XListView) findViewById(R.id.peopleList);
		companyList.setPullLoadEnable(false);
		personList.setPullLoadEnable(false);
		savedList.setPullLoadEnable(false);
		searchEdt.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void afterTextChanged(Editable s) {
				String character = s.toString().trim();
				if (TextUtils.isEmpty(character) || null == character){ 
					cancelSearchTask();
					searchCompanies.clear();
					searchPersons.clear();
					companyList.setPullLoadEnable(false);
					personList.setPullLoadEnable(false);
					companyAdapter.notifyDataSetChanged();
					personAdapter.notifyDataSetChanged();
					
					mainLayout.setVisibility(View.VISIBLE);
					searchLayout.setVisibility(View.GONE);
					companyFoundLayout.setVisibility(View.GONE);
				} else {
					scheduleSearchTask(character, 2000);
					mainLayout.setVisibility(View.GONE);
					searchLayout.setVisibility(View.VISIBLE);
					companyFoundLayout.setVisibility(View.GONE);
				};
			}
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
		});
		
		searchEdt.setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
				
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					cancelSearchTask();
					if (TextUtils.isEmpty(textView.getText().toString())) {
						return false;
					}
					CommonUtil.hideSoftKeyBoard(mContext, SearchActivity.this);
					scheduleSearchTask(textView.getText().toString(), 0);
					return true;
				}
				return false;
			}
		});
	}
	
	@Override
	protected void initData() {
		super.initData();
		
		isCompany = true;
		isPerson = !isCompany;
		
		getSavedSearches(false, false);
		
		setSearchCompanies();
		setSearchPersons();
		
		CommonUtil.showSoftKeyBoard(300);
		
	}
	
	@Override
	public void handleNotifications(Context aContext, Intent intent) {
		super.handleNotifications(aContext, intent);
		String actionName = intent.getAction();
		if (actionName.equals(Constant.BROADCAST_REFRESH_SEARCH)) {
			PAGE_NUM_SAVESEARCH = 1;
			getSavedSearches(false, false);
		}
	}
	
	@Override
	protected List<String> observeNotifications() {
		return stringList(Constant.BROADCAST_REFRESH_SEARCH);
	}
	
	/**schedule search*/
	private void scheduleSearchTask(final String character, final long time) {
		
		cancelSearchTask();
		timer = new Timer();
		timerTask = new TimerTask() {
			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						PAGE_NUM_COMPANY = 1;
						PAGE_NUM_PERSON = 1;
						searchCompanies(character, false);
						if (time == 0) {
							Log.v("silen", "time == 0");
							searchAllPersons(character);
						} else {
							searchPartPersons(character, false);
						}
					}
				});
			}
		};
		timer.schedule(timerTask, time);
	}
	
	/**stop schedule search*/
	private void cancelSearchTask() {
		if (timerTask != null) {
			timerTask.cancel();
		}
	}
	
	private void setSearchCompanies() {
		companyList.setVisibility(isCompany ? View.VISIBLE : View.GONE);
		companyAdapter = new SearchCompanyAdapter(mContext, searchCompanies);
		companyList.setAdapter(companyAdapter);
		companyAdapter.notifyDataSetChanged();
		companyAdapter.notifyDataSetInvalidated();
	}
	
	private void setSearchPersons() {
		personList.setVisibility(isPerson ? View.VISIBLE : View.GONE);
		personAdapter = new SearchPersonAdapter(mContext, searchPersons);
		personList.setAdapter(personAdapter);
		personAdapter.notifyDataSetChanged();
		personAdapter.notifyDataSetInvalidated();
	}
	
	private void getSavedSearches(Boolean showDialog, final Boolean loadMore) {
		
		if (showDialog) showLoadingDialog();
		mApiHttp.getSavedSearchesWithPage(PAGE_NUM_SAVESEARCH, new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject jsonObject) {
				
				APIParser parser = new APIParser(jsonObject);
				if (parser.isOK()) {
					
					if (!loadMore) mSavedSearchs.clear();
					DataPage dataPage = parser.parseGetSavedSearch();
					List<Object> items = dataPage.items;
					if (items != null) {
						for (Object obj : items) {
							mSavedSearchs.add((SavedSearch) obj);
						}
						if (!loadMore) setSavedSearch();
						
					}
					
					savedList.setPullLoadEnable(dataPage.hasMore);
					
					if (mSavedSearchs.size() > 0) {
						noSavedSearches.setVisibility(View.VISIBLE);
					} else {
						noSavedSearches.setVisibility(View.GONE);
					}
					Log.v("silen", "mSavedSearchs.size = " + mSavedSearchs.size());
					
					PAGE_NUM_SAVESEARCH ++;
				} else {
					Log.v("silen", "alertMessageForParser");
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
	
	private void setSavedSearch() {
		savedAdapter = new SearchSavedAdapter(mContext, mSavedSearchs);
		savedList.setAdapter(savedAdapter);
		savedAdapter.notifyDataSetChanged();
		savedAdapter.notifyDataSetInvalidated();
	}
	
	private void searchCompanies(final String character, final Boolean loadMore) {
		
		if (!loadMore) showLoadingDialog();
		mApiHttp.searchCompanies(character , PAGE_NUM_COMPANY, new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject jsonObject) {
				
				APIParser parser = new APIParser(jsonObject);
				if (parser.isOK()) {
					if (!loadMore) searchCompanies.clear();
					DataPage dataPage = parser.parseSearchCompany();
					Boolean haveMoreCompanies = dataPage.hasMore;
					companyList.setPullLoadEnable(haveMoreCompanies);
					List<Object> items = dataPage.items;
					if (items != null) {
						for (Object obj : items) {
							searchCompanies.add((Company)obj);
						}
					}
					if (!loadMore) {
						companyAdapter.notifyDataSetChanged();
						companyAdapter.notifyDataSetInvalidated();
					}
					
					Log.v("silen", "companies.size == " + searchCompanies.size());
					if (searchCompanies.size() == 0) {
						noResultsLayout.setVisibility(View.VISIBLE);
					} else {
						noResultsLayout.setVisibility(View.GONE);
					}
					
					PAGE_NUM_COMPANY ++;
					
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
	
	private void searchPartPersons(String character, final Boolean loadMore) {
		mApiHttp.searchAllPersons(character , PAGE_NUM_PERSON, new Listener<JSONObject>() {
			
			@Override
			public void onResponse(JSONObject jsonObject) {
				APIParser parser = new APIParser(jsonObject);
				Log.v("silen", parser.data().toString());
				
				if (parser.isOK()) {
					if (!loadMore) {
						searchPersons.clear();
					}
					DataPage dataPage = parser.parseSearchForPeople();
					Boolean haveMorePerson = dataPage.hasMore;
					personList.setPullLoadEnable(haveMorePerson);
					List<Object> items = dataPage.items;
					if (items != null) {
						for (Object obj : items) {
							searchPersons.add((Person)obj);
						}
					}
					
					//Log.v("silen", "pserson.size == " + searchPersons.size());
					for (int i = 0; i < searchPersons.size(); i ++) {//TODO
						Log.v("silen", "socialProfiles.size = " + searchPersons.get(i).socialProfiles.size());
					}
					if (!loadMore) {
						personAdapter.notifyDataSetChanged();
						personAdapter.notifyDataSetInvalidated();
					}
//					if (searchPersons.size() == 0) {
//						listView.setVisibility(View.GONE);
//						addCompany.setVisibility(View.VISIBLE);
//					} else {
//						listView.setVisibility(View.VISIBLE);
//						addCompany.setVisibility(View.GONE);
//					}
					
					PAGE_NUM_PERSON ++;
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
	
	private void searchAllPersons(String character) {
		mApiHttp.searchAllPersons(character, PAGE_NUM_PERSON, new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject jsonObject) {
				APIParser parser = new APIParser(jsonObject);
				if (parser.isOK()) {
					searchPersons.clear();
					DataPage page = parser.parseSearchForPeople();
					List<Object> items = page.items;
					if (items != null) {
						for (Object obj : items) {
							searchPersons.add((Person)obj);
						}
					}
					if (searchPersons.size() == 0) {
						noResultsLayout.setVisibility(View.VISIBLE);
					} else {
						noResultsLayout.setVisibility(View.GONE);
					}
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
	
	@Override
	protected void setOnClickListener() {
		super.setOnClickListener();
		companyList.setXListViewListener(this);
		personList.setXListViewListener(this);
		savedList.setXListViewListener(this);
		companiesBtn.setOnClickListener(this);
		peopleBtn.setOnClickListener(this);
		buildCompany.setOnClickListener(this);
		buildPeople.setOnClickListener(this);
		addNewCompany.setOnClickListener(this);
		edit.setOnClickListener(this);
		addDifferentCompany.setOnClickListener(this);
		
		companyList.setOnItemClickListener(this);
		personList.setOnItemClickListener(this);
		savedList.setOnItemClickListener(this);
		foundCompanyList.setOnItemClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		if (v == addNewCompany) {
			
			final AddNewCompanyDialog dialog = new AddNewCompanyDialog(mContext);
			dialog.showDialog(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					
					addNewCompany(dialog);

				}
				
			});
			
		} else if (v == companiesBtn) {
			
			isCompany = true;
			isPerson = !isCompany;
			companyList.setVisibility(View.VISIBLE);
			personList.setVisibility(View.GONE);
			
		} else if (v == peopleBtn) {
			
			isPerson = true;
			isCompany = !isPerson;
			personList.setVisibility(View.VISIBLE);
			companyList.setVisibility(View.GONE);
			
		} else if (v == buildCompany) {

			if (!havePurchased) {
				UpgradeDialog dialog = new UpgradeDialog(mContext);
				dialog.showDialog();
			} else {
				// if mFilters is null , pls get it again
				if (Constant.MFILTERS == null) {
					getFilter();
					return;
				}
				Intent intent = new Intent();
				intent.setClass(mContext, (CommonUtil.isTablet(mContext) ? CompanySearchTabletActivity.class : CompanyFilterActivity.class));
				startActivity(intent);
			}
			
		} else if (v == buildPeople) {
			
			if (!havePurchased) {
				UpgradeDialog dialog = new UpgradeDialog(mContext);
				dialog.showDialog();
			} else {
				if (Constant.MFILTERS == null) {
					getFilter();
					return;
				}
				Intent intent = new Intent();
				intent.setClass(mContext, (CommonUtil.isTablet(mContext) ? PeopleSearchTabletActivity.class : PeopleFilterActivity.class));
				startActivity(intent);
			}
			
		} else if (v == edit) {
			
			if (null == savedAdapter) return; 
			savedAdapter.edit = !savedAdapter.edit;
			edit.setText(savedAdapter.edit ? R.string.done : R.string.edit);
			savedAdapter.notifyDataSetChanged();
			
		} else if (v == addDifferentCompany) {
			final AddNewCompanyDialog dialog = new AddNewCompanyDialog(mContext);
			dialog.showDialog(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					
					CommonUtil.hideSoftKeyBoard(mContext, SearchActivity.this);
					addNewCompany(dialog);
				}
			});
		}
	}
	
	private void addNewCompany(final AddNewCompanyDialog dialog) {
		ArrayList<String> nameWebsite = dialog.getNameAndWebsite();
		if (null == nameWebsite) return;
		
		final String name = nameWebsite.get(0);
		final String website = nameWebsite.get(1);
		
		
		final APIHttp mApiHttp = new APIHttp(mContext);
		Pattern pattern1 = Pattern.compile(Utils.regular_url1);
		Matcher matcher1 = pattern1.matcher(website);
		Pattern pattern2 = Pattern.compile(Utils.regular_url2);
		Matcher matcher2 = pattern2.matcher(website);
		if (matcher1.matches() || matcher2.matches()) {
			CommonUtil.showLoadingDialog(mContext);
			mApiHttp.addNewCompanyWithName(name , website, false, new Listener<JSONObject>() {

				@Override
				public void onResponse(JSONObject jsonObject) {
					CommonUtil.dissmissLoadingDialog();
					
					APIParser parser = new APIParser(jsonObject);
					
					Log.v("silen", "parser.status() = " + parser.status());
					int status = parser.status();
					
					if (parser.isOK()) {
						
						dialog.dismissDialog();
						
						NewCompanySubmittedPromtDialog dialog = new NewCompanySubmittedPromtDialog(mContext);
						dialog.setCancelable(false);
						dialog.showDialog(name, website);
						// sent a broadcast to finish activity and refresh website
						Intent intent = new Intent();
						intent.setAction(Constant.BROADCAST_ADD_NEW_COMPANIES);
						mContext.sendBroadcast(intent);
						
					} else if (status == MessageCode.CompanyBuzExists) {
						
						dialog.dismissDialog();
						
						noResultsLayout.setVisibility(View.GONE);
						companyFoundLayout.setVisibility(View.VISIBLE);
						
						matchedCompanies.clear();
						JSONArray nameArray = parser.data().optJSONArray("orgs_by_name");
						JSONArray websiteArray = parser.data().optJSONArray("orgs_by_website");
						int matchCompanyCount = nameArray.length();
						int matchWebsiteCount = websiteArray.length();
						
						if (matchCompanyCount > 0) {
							
							for (int i = 0; i < nameArray.length(); i ++) {
								JSONObject jObject = nameArray.optJSONObject(i);
								Company company = new Company();
								company.parseData(jObject);
								
								for (int k = 0; k < matchedCompanies.size(); k ++) {
									if (matchedCompanies.get(k).orgID != company.orgID) {
										matchedCompanies.add(company);
									}
								}
								
							}
						}
						if (matchWebsiteCount > 0) {
							
							for (int i = 0; i < websiteArray.length(); i ++) {
								
								JSONObject jObject = websiteArray.optJSONObject(i);
								Company company = new Company();
								company.parseData(jObject);
								matchedCompanies.add(company);
								
								for (int k = 0; k < matchedCompanies.size(); k ++) {
									if (matchedCompanies.get(k).orgID != company.orgID) {
										matchedCompanies.add(company);
									}
								}
								
							}
						}
						String title = "";
						if (matchedCompanies.size() == 1) {
							title = "1 Company Found";
						} else {
							title = matchedCompanies.size() + " Companies Found";
						}
						
						/**
						 *  We found <n> companies for <company name> (<n> >= 2)
				         *  We found 1 company for <company website>
				         *  We found 1 company for <company name> and <company website>
				         *  We found 1 company for <company name> and 1 company for <company website>
				         *  We found <n> companies for <company names> and 1 company for <company website> (<n> > = 2)
						 */
						String message = "";
						String strCom4Name = matchCompanyCount == 1 ? "1 company" : matchCompanyCount + " companies";
						String strCom4Web = matchWebsiteCount == 1 ? "1 company" : matchWebsiteCount + " companies";
				           
						if (matchCompanyCount == 0) {
							
							message = String.format("We found %s for '%s'", strCom4Web, website);
							
						} else if (matchWebsiteCount == 0) {
							
							Boolean oneComMatchNameAndWeb = false;
							if (matchedCompanies.size() == 1) {
								Company company = matchedCompanies.get(0);
								if (company.website.toLowerCase().equalsIgnoreCase(website)) {
									message = String.format("We found 1 company for '%s' and '%s'", name, website);
									oneComMatchNameAndWeb = true;
								}
							}
							
							if (!oneComMatchNameAndWeb) {
								message = String.format("We found %s for '%s'", strCom4Name, name);
							}
							
						} else {
							
							message = String.format("We found %s for '%s' and %s for '%s'", strCom4Name, name, strCom4Web, website);
							
						}
						
						companiesFoundNum.setText(title);
						companiesFound.setText(message);
						
						SearchCompanyAdapter adapter = new SearchCompanyAdapter(mContext, matchedCompanies);
						foundCompanyList.setAdapter(adapter);
						adapter.notifyDataSetChanged();
						adapter.notifyDataSetInvalidated();
						
					} else if (parser.messageCode() == MessageCode.CompanyWebConnectFailed){
						
						final VerifingWebsiteConnectTimeOutDialog dialog = new VerifingWebsiteConnectTimeOutDialog(mContext);
						dialog.setCancelable(false);
						dialog.showDialog(website, new OnClickListener() {
							
							@Override
							public void onClick(View arg0) {
								
								dialog.dismissDialog();
							}
						});
						
			            
			        } else if (parser.messageCode() == MessageCode.CompanyWebConnectTimeout) {
			        	
			        	final VerifyingWebsiteDialog dialog = new VerifyingWebsiteDialog(mContext);
			        	dialog.setCancelable(false);
			        	dialog.showDialog(website, new OnClickListener() {
							
							@Override
							public void onClick(View arg0) {
								
								dialog.dismissDialog();
								
								showLoadingDialog();
								mApiHttp.addNewCompanyWithName(name , website, true, new Listener<JSONObject>() {

									@Override
									public void onResponse(JSONObject jsonObject) {
										
										CommonUtil.dissmissLoadingDialog();
										APIParser parser = new APIParser(jsonObject);
										
										if (parser.isOK()) {
											showShortToast(R.string.companies_added);
										}
									}
									
								}, new Response.ErrorListener() {

									@Override
									public void onErrorResponse(VolleyError error) {
										showConnectionError();
									}
								});
										
							}
						});
			        	
			        }
					
				}
			}, new Response.ErrorListener() {

				@Override
				public void onErrorResponse(VolleyError error) {
					showConnectionError();
				}
			});
		} else {
			CommonUtil.showShortToast(mContext.getResources().getString(R.string.enter_valid_url));
		}
	}
	
	@Override
	public void onBackPressed() {
		if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        showShortToast(R.string.press_again_exit);
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
             doubleBackToExitPressedOnce=false;   

            }
        }, 2000);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {
		
		if (parent == foundCompanyList) {
			
			Intent intent = new Intent();
			intent.setClass(mContext, CommonUtil.isTablet(mContext) ? CompanyTabletActivity.class : CompanyActivity.class);
			intent.putExtra(Constant.COMPANYID, matchedCompanies.get(position).orgID);
			startActivity(intent);
			
		} else {
			
			if (position == 0) return;
			if (parent == companyList) {
				if (position == searchCompanies.size() + 1) return;
				Intent intent = new Intent();
				intent.setClass(mContext, CommonUtil.isTablet(mContext) ? CompanyTabletActivity.class : CompanyActivity.class);
				intent.putExtra(Constant.COMPANYID, searchCompanies.get(position - 1).orgID);
				startActivity(intent);
			} else if (parent == personList) {
				if (position == searchPersons.size() + 1) return;
				Intent intent = new Intent();
				intent.setClass(mContext, PersonActivity.class);
				intent.putExtra(Constant.PERSONID, searchPersons.get(position - 1).id);
				startActivity(intent);
			} else if (parent == savedList) {
				if (position == mSavedSearchs.size() + 1) return;
				
				SavedSearch mSavedSearch = mSavedSearchs.get(position - 1);
				String type = mSavedSearch.getType();
				if (type.equalsIgnoreCase("buz")) {
					Intent intent = new Intent();
					intent.setClass(mContext, CommonUtil.isTablet(mContext) ? CompanySearchTabletActivity.class : SearchCompanyActivity.class);
					intent.putExtra(Constant.SAVEDID, mSavedSearch.getId());
					startActivity(intent);
				} else if (type.equalsIgnoreCase("cnt")) {
					Intent intent = new Intent();
					intent.setClass(mContext, CommonUtil.isTablet(mContext) ? PeopleSearchTabletActivity.class : SearchPersonActivity.class);
					intent.putExtra(Constant.SAVEDID, mSavedSearch.getId());
					startActivity(intent);
				}
			}
		}
	}

	@Override
	public void onRefresh() {
		if (mainLayout.isShown()) {
			PAGE_NUM_SAVESEARCH = 1;
			getSavedSearches(true, false);
			return;
		}
//		if (isCompany) {
//			PAGE_NUM_COMPANY = 1;
//			searchCompanies(searchEdt.getText().toString() , false);
//		} else if (isPerson) {
//			PAGE_NUM_PERSON = 1;
//			searchPartPersons(searchEdt.getText().toString() , false);
//		}
	}

	@Override
	public void onLoadMore() {
		if (mainLayout.isShown()) {
			getSavedSearches(false, true);
			return;
		}
		if (isCompany) {
			searchCompanies(searchEdt.getText().toString() , true);
		} else if (isPerson) {
			searchPartPersons(searchEdt.getText().toString() , true);
		}
	}
	
//	private void getFilter() {
//		mApiHttp.getFilter(new Listener<JSONObject>() {
//
//			@Override
//			public void onResponse(JSONObject jsonObject) {
//				APIParser parser = new APIParser(jsonObject);
//				if (parser.isOK()) {
//					Constant.MFILTERS = parser.parserFilters();
//				} else {
//					alertMessageForParser(parser);
//				}
//				dismissLoadingDialog();
//			}
//		}, new Response.ErrorListener() {
//
//			@Override
//			public void onErrorResponse(VolleyError error) {
//				showConnectionError();
//			}
//		});
//	}

}
