package com.gagein.ui.companies;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
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
import com.gagein.adapter.SearchCompanyAdapter;
import com.gagein.component.dialog.AddNewCompanyDialog;
import com.gagein.component.dialog.DeleteCompanyDialog;
import com.gagein.component.dialog.NewCompanySubmittedPromtDialog;
import com.gagein.component.dialog.PendingCompanyDialog;
import com.gagein.component.dialog.VerifingWebsiteConnectTimeOutDialog;
import com.gagein.component.dialog.VerifyingWebsiteDialog;
import com.gagein.http.APIHttp;
import com.gagein.http.APIHttpMetadata;
import com.gagein.http.APIParser;
import com.gagein.model.Company;
import com.gagein.model.DataPage;
import com.gagein.ui.main.BaseActivity;
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
	private ArrayList<Company> matchedCompanies = new ArrayList<Company>();
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
		final PendingCompanyDialog dialog = new PendingCompanyDialog(mContext, company.name);
		dialog.showDialog(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				addNewCompany(dialog, company);

			}
		});
		
		CommonUtil.showSoftKeyBoard(30);
		
	}
	
	private void addNewCompany(final PendingCompanyDialog dialog, Company mCompany) {
		ArrayList<String> nameWebsite = dialog.getNameAndWebsite();
		if (null == nameWebsite) return;
		
		final String name = mCompany.name;
		final String website = dialog.getNameAndWebsite().get(1);
		
		
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
						
//						noCompanyResultsLayout.setVisibility(View.GONE);
//						companyFoundLayout.setVisibility(View.VISIBLE);
						
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
						//TODO
//						companiesFoundNum.setText(title);
//						companiesFound.setText(message);
						
						SearchCompanyAdapter adapter = new SearchCompanyAdapter(mContext, matchedCompanies);
//						foundCompanyList.setAdapter(adapter);
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
