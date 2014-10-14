package com.gagein.ui.companies;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.gagein.R;
import com.gagein.adapter.PendingCompaniesAdapter;
import com.gagein.component.dialog.DeleteCompanyDialog;
import com.gagein.component.dialog.PendingCompanyDialog;
import com.gagein.component.dialog.VerifingWebsiteConnectTimeOutDialog;
import com.gagein.component.dialog.VerifyingWebsiteDialog;
import com.gagein.http.APIHttp;
import com.gagein.http.APIHttpMetadata;
import com.gagein.http.APIParser;
import com.gagein.model.Company;
import com.gagein.model.DataPage;
import com.gagein.ui.company.CompanyActivity;
import com.gagein.ui.main.BaseActivity;
import com.gagein.ui.tablet.company.CompanyTabletActivity;
import com.gagein.util.CommonUtil;
import com.gagein.util.Constant;
import com.gagein.util.Log;
import com.gagein.util.MessageCode;
import com.gagein.util.Utils;

public class PendingCompaniesActivity extends BaseActivity implements OnItemClickListener{
	
	private ListView listView;
	private PendingCompaniesAdapter adapter;
	private Boolean edit = false;
	private List<Company> pendingCompanies = new ArrayList<Company>();
	private ImageView selectAllBtn;
	private View headSelectAll;
	private Button deleteBtn;
	private int selectedNum = 0;
	private List<Long> companyIds;
	private int headSelectAllHeight; 
	
	@Override
	protected List<String> observeNotifications() {
		return stringList(Constant.BROADCAST_ADDED_PENDING_COMPANY);
	}
	
	@Override
	public void handleNotifications(Context aContext, Intent intent) {
		super.handleNotifications(aContext, intent);
		String actionName = intent.getAction();
		if (actionName.equals(Constant.BROADCAST_ADDED_PENDING_COMPANY)) {
			getPendingCompany(false);
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pending_companies);
		
		doInit();
	}
	
	@Override
	protected void initView() {
		super.initView();
		setTitle(R.string.pending_companies);
		setLeftImageButton(R.drawable.back_arrow);
		setRightButton(R.string.u_edit);
		
		listView = (ListView) findViewById(R.id.listView);
		deleteBtn = (Button) findViewById(R.id.deleteBtn);
		headSelectAll = LayoutInflater.from(mContext).inflate(R.layout.header_select_all, null);
		headSelectAllHeight = CommonUtil.getViewHeight(headSelectAll);
		setHeadSelectAllHeight(1);
		selectAllBtn = (ImageView) headSelectAll.findViewById(R.id.selectBtn);
	}

	@SuppressWarnings("deprecation")
	private void setHeadSelectAllHeight(int height) {
		LayoutParams layoutParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		layoutParams.height = height;
		headSelectAll.setLayoutParams(layoutParams);
	}
	
	@Override
	protected void initData() {
		super.initData();
		
		getPendingCompany(true);
	}
	
	@Override
	protected void setData() {
		super.setData();
		adapter = new PendingCompaniesAdapter(mContext, pendingCompanies);
		listView.addHeaderView(headSelectAll);
		listView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
	}
	
	@Override
	protected void setOnClickListener() {
		super.setOnClickListener();
		listView.setOnItemClickListener(this);
		deleteBtn.setOnClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		
		if (edit) {
			if (position == 0) {
				Boolean isSelect = (Boolean) selectAllBtn.getTag(R.id.tag_select);
				selectAllBtn.setTag(R.id.tag_select, !isSelect);
				selectAllBtn.setImageResource(!isSelect ? R.drawable.button_select : R.drawable.button_unselect);
				
				for (int i = 0; i < pendingCompanies.size(); i ++) {
					pendingCompanies.get(i).select = isSelect ? false : true;
				}
				adapter.notifyDataSetChanged();
				setBottomButton(pendingCompanies);
				return;
			}
			
			Boolean select = pendingCompanies.get(position - 1).select;
			pendingCompanies.get(position - 1).select = !select;
			setSelectAllBtnStatus();
			adapter.notifyDataSetChanged();
			setBottomButton(pendingCompanies);
			return;
		}
		
		final Company company = pendingCompanies.get(position - 1);
		if (company.orgID > 0) {
			Intent intent = new Intent();
			intent.setClass(mContext, CommonUtil.isTablet(mContext) ? CompanyTabletActivity.class : CompanyActivity.class);
			intent.putExtra(Constant.COMPANYID, company.orgID);
			startActivity(intent);
			return;
		}
		
		final PendingCompanyDialog dialog = new PendingCompanyDialog(mContext, company.name);
		dialog.showDialog(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				addNewCompany(dialog, company);
				
			}
		});
		
		CommonUtil.showSoftKeyBoard(300);
		
	}
	
	private void addNewCompany(final PendingCompanyDialog dialog, final Company mCompany) {
		ArrayList<String> nameWebsite = dialog.getNameAndWebsite();
		if (null == nameWebsite) return;
		
		final String name = mCompany.name;
		final String website = dialog.getNameAndWebsite().get(1);
		
		if (!website.contains(".")) {
			CommonUtil.showShortToast(mContext.getResources().getString(R.string.enter_valid_url));
			return;
		}
		
		final APIHttp mApiHttp = new APIHttp(mContext);
		Pattern pattern1 = Pattern.compile(Utils.regular_url1);
		Matcher matcher1 = pattern1.matcher(website);
		Pattern pattern2 = Pattern.compile(Utils.regular_url2);
		Matcher matcher2 = pattern2.matcher(website);
		if (matcher1.matches() || matcher2.matches()) {
			CommonUtil.showLoadingDialog(mContext);
			
			mApiHttp.addCompanyWebsite(mCompany.orgID, name , website, false, new Listener<JSONObject>() {

				@Override
				public void onResponse(JSONObject jsonObject) {
					CommonUtil.dissmissLoadingDialog();
					
					APIParser parser = new APIParser(jsonObject);
					
					Log.v("silen", "parser.status() = " + parser.status());
					Log.v("silen", "jsonObject = " + jsonObject.toString());
					
					if (parser.isOK()) {
						
						CommonUtil.hideSoftKeyBoard(mContext, PendingCompaniesActivity.this);
						dialog.dismissDialog();
						showShortToast(R.string.company_added);
						
						List<Company> companies = new ArrayList<Company>();
						DataPage page = parser.parseFollowedCompanies();
						if (page.items != null) {
							for (Object obj : page.items) {
								if (obj instanceof Company) {
									
									Company company = (Company)obj;
									if (company.orgID != 0) {
										companies.add(company);
									}
								}
							}
						}
						if (companies.size() == 0) return;
						Company matchedCompany = companies.get(0);
						for (int i = 0; i < pendingCompanies.size(); i++) {
							Company company = pendingCompanies.get(i);
							if (pendingCompanies.get(i).orgID == mCompany.orgID) {
								company.orgID = matchedCompany.orgID;
								company.name = matchedCompany.name;
								company.website = matchedCompany.website;
								company.followed = true;
								company.address = matchedCompany.address;
								company.city = matchedCompany.city;
								company.country = matchedCompany.country;
							}
						}
						adapter.notifyDataSetChanged();
						
						// sent a broadcast to finish activity and refresh website
						Intent intent = new Intent();
						intent.setAction(Constant.BROADCAST_ADD_NEW_COMPANIES);
						mContext.sendBroadcast(intent);
						
					} else if (parser.messageCode() == MessageCode.CompanyWebConnectFailed){
						
						dialog.dismissDialog();
						
						final VerifingWebsiteConnectTimeOutDialog dialog = new VerifingWebsiteConnectTimeOutDialog(mContext);
						dialog.setCancelable(false);
						dialog.showDialog(website, new OnClickListener() {
							
							@Override
							public void onClick(View arg0) {
								
								dialog.dismissDialog();
							}
						});
						
			            
			        } else if (parser.messageCode() == MessageCode.CompanyWebConnectTimeout) {
			        	
			        	dialog.dismissDialog();
			        	
			        	final VerifyingWebsiteDialog dialog = new VerifyingWebsiteDialog(mContext);
			        	dialog.setCancelable(false);
			        	dialog.showDialog(website, new OnClickListener() {
							
							@Override
							public void onClick(View arg0) {
								
								dialog.dismissDialog();
								
								showLoadingDialog();
								mApiHttp.addCompanyWebsite(mCompany.orgID, name , website, true, new Listener<JSONObject>() {

									@Override
									public void onResponse(JSONObject jsonObject) {
										
										CommonUtil.dissmissLoadingDialog();
										APIParser parser = new APIParser(jsonObject);
										
										if (parser.isOK()) {
											Log.v("silen", "jsonObject = " + jsonObject.toString());
											showShortToast(R.string.company_added);
											
											List<Company> companies = new ArrayList<Company>();
											DataPage page = parser.parseFollowedCompanies();
											if (page.items != null) {
												for (Object obj : page.items) {
													if (obj instanceof Company) {
														
														Company company = (Company)obj;
														if (company.orgID != 0) {
															companies.add(company);
														}
													}
												}
											}
											if (companies.size() == 0) return;
											Company matchedCompany = companies.get(0);
											for (int i = 0; i < pendingCompanies.size(); i++) {
												Company company = pendingCompanies.get(i);
												if (pendingCompanies.get(i).orgID == mCompany.orgID) {
													company.orgID = matchedCompany.orgID;
													company.name = matchedCompany.name;
													company.website = matchedCompany.website;
													company.followed = true;
													company.address = matchedCompany.address;
													company.city = matchedCompany.city;
													company.country = matchedCompany.country;
												}
											}
											adapter.notifyDataSetChanged();
											
											Intent intent = new Intent();
											intent.setAction(Constant.BROADCAST_ADD_NEW_COMPANIES);
											mContext.sendBroadcast(intent);
											
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
	
	public void setBottomButton(List<Company> companies) {
		selectedNum = 0;
		companyIds = new ArrayList<Long>();
		if (null != companies) {
			for (int i = 0 ; i < companies.size(); i ++) {
				if (pendingCompanies.get(i).select) {
					selectedNum ++;
					companyIds.add(companies.get(i).orgID);
				}
			}
		}
		deleteBtn.setVisibility((selectedNum == 0) ? View.GONE : View.VISIBLE);
	}
	
	private void setSelectAllBtnStatus() {
		Boolean isAllSelected = true;
		for (int i = 0; i < pendingCompanies.size(); i ++) {
			if (!pendingCompanies.get(i).select) isAllSelected = false;
		}
		selectAllBtn.setTag(R.id.tag_select, isAllSelected ? true : false);
		selectAllBtn.setImageResource(!isAllSelected ? R.drawable.button_unselect : R.drawable.button_select);
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		if (v == leftImageBtn) {
			finish();
		} else if (v == rightBtn) {
			edit = !edit;
			selectAllBtn.setTag(R.id.tag_select, false);
			selectAllBtn.setImageResource(R.drawable.button_unselect);
			if (edit) {
				for (int i = 0; i < pendingCompanies.size(); i ++) {
					pendingCompanies.get(i).select = false;
				}
			}
			
			adapter.setEdit(edit);
			adapter.notifyDataSetChanged();
			setRightButton(edit ? R.string.done : R.string.edit);
			if (edit) {
				setHeadSelectAllHeight(headSelectAllHeight);
			} else {
				setHeadSelectAllHeight(1);
			}
			
			if (!edit) deleteBtn.setVisibility(View.GONE);
			
		} else if (v == deleteBtn) {
			
			final DeleteCompanyDialog dialog = new DeleteCompanyDialog(mContext);
			dialog.showDialog(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					dialog.dismissDialog();
					removePendingCompany(getSelectedCompaniesIds());
				}
			}, new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					dialog.dismissDialog();
				}
			});
		}
	}
	
	/**
	 * get companies ids from which has selected
	 * @return
	 */
	private ArrayList<String> getSelectedCompaniesIds() {
		ArrayList<String> companiesIds = new ArrayList<String>();
		for (int i = 0; i < pendingCompanies.size(); i ++) {
			if (pendingCompanies.get(i).select) 
				companiesIds.add(pendingCompanies.get(i).orgID + ""); 
		}
		return companiesIds;
	}
	
	private void getPendingCompany(Boolean showDialog) {
		if (showDialog) showLoadingDialog();
		mApiHttp.getFollowedCompanies(0, Constant.INDUSTRYID, APIHttpMetadata.kGGPendingFollowCompanies, false, new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject jsonObject) {
				dismissLoadingDialog();
				APIParser parser = new APIParser(jsonObject);
				if (parser.isOK()) {
					pendingCompanies.clear();
					DataPage page = parser.parseFollowedCompanies();
					if (page.items != null) {
						for (Object obj : page.items) {
							if (obj instanceof Company) {
								pendingCompanies.add((Company)obj);
							}
						}
					}
					Collections.sort(pendingCompanies);
					setData();
				} else {
				}
			}
			
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				showConnectionError();
			}
		});
	}
	
	private void removePendingCompany(final ArrayList<String> companiesIds) {
		
		showLoadingDialog();
		mApiHttp.removePendingCompany(companiesIds, new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject jsonObject) {
				
				APIParser parser = new APIParser(jsonObject);
				dismissLoadingDialog();
				if (parser.isOK()) {
					
					deleteCompanies(companiesIds);
					
					Intent intent = new Intent();
					intent.setAction(Constant.BROADCAST_REMOVE_COMPANIES);
					sendBroadcast(intent);
					
				} else {
					alertMessageForParser(parser);
				}
			}
			
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				showConnectionError();
			}
		});
	}
	
	private void deleteCompanies(ArrayList<String> companiesIds) {
		
		for (int i = 0; i < companiesIds.size(); i ++) {
			String id = companiesIds.get(i);
			for (int j = 0; j < pendingCompanies.size(); j ++) {
				if (id.equalsIgnoreCase(pendingCompanies.get(j).orgID + "")) {
					pendingCompanies.remove(pendingCompanies.get(j));
					adapter.notifyDataSetChanged();
					if (pendingCompanies.size() == 0) finish();
				}
			}
		}
	}

}
