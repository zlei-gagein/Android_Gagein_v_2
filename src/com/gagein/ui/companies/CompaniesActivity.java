package com.gagein.ui.companies;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.gagein.R;
import com.gagein.adapter.CompaniesNoSectionIndexAdapter;
import com.gagein.component.dialog.CommonDialog;
import com.gagein.component.dialog.ImportDialog;
import com.gagein.component.dialog.RemoveFromGroupDialog;
import com.gagein.component.xlistview.XListView;
import com.gagein.component.xlistview.XListView.IXListViewListener;
import com.gagein.http.APIHttpMetadata;
import com.gagein.http.APIParser;
import com.gagein.model.Company;
import com.gagein.model.DataPage;
import com.gagein.model.Facet;
import com.gagein.model.FacetItemIndustry;
import com.gagein.model.Group;
import com.gagein.ui.company.CompanyActivity;
import com.gagein.ui.main.BaseActivity;
import com.gagein.ui.tablet.company.CompanyTabletActivity;
import com.gagein.util.CommonUtil;
import com.gagein.util.Constant;
import com.gagein.util.Log;

public class CompaniesActivity extends BaseActivity implements OnItemClickListener, IXListViewListener{
	
	private List<Company> companies = new ArrayList<Company>();
	private XListView noSectionListView;
	private View headView;
	private View headSelectAll;
	private ImageView selectAllBtn;
	private CompaniesNoSectionIndexAdapter noSectionIndexAdapter;
	private TextView filterBtn;
	private Button bottomBtn;
	private Boolean edit = false;
	private Boolean selected = false;
	private int selectedNum = 0;
	private List<Long> companyIds;
	private Facet facet;
	private List<FacetItemIndustry> industryData = new ArrayList<FacetItemIndustry>();
	private int requestCode = 1;
	private LinearLayout noFollowedCompanies;
	private LinearLayout bottomLayoutIsNotSystem;
	private RelativeLayout bottomLayoutLinkedCompanies;
	private Group group;
	private String groupId;
	private String groupName;
	private RelativeLayout bottomLayout;
	private Button importBtn;
	private Button addCompaniesBtn;
	private Button addToBtn;
	private Button removeBtn;
	private Button unfollowBtn;
	
	private Button addToBtnLinkedCompanies;
	private Button unfollowBtnLinkedCompanies;
	private Boolean isSystemGroup;
	private Boolean isLinkedCompanies = false;
	private TextView noCompaniesTitle;
	private TextView noCompaniesPt;
	private String nextPage = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_companies);
		
		doInit();
	}
	
	@Override
	public void handleNotifications(Context aContext, Intent intent) {
		super.handleNotifications(aContext, intent);
		
		String actionName = intent.getAction();
		if (actionName.equals(Constant.BROADCAST_REFRESH_COMPANIES) || actionName.equals(Constant.BROADCAST_ADDED_PENDING_COMPANY)) {
			
			edit = false;
			setEditStatus();
			setBottomLayoutStatus();
			nextPage = "";
			getCompaniesOfGroup(false, false);
			
		} else if (actionName.equals(Constant.BROADCAST_ADD_COMPANIES_FROM_FOLLOW_COMPANIES)) {
			
			edit = false;
			setEditStatus();
			setBottomLayoutStatus();
			nextPage = "";
			getCompaniesOfGroup(false, false);
			
		} else if (actionName.equals(Constant.BROADCAST_FOLLOW_COMPANY) || actionName.equals(Constant.BROADCAST_UNFOLLOW_COMPANY)) {
			
			nextPage = "";
			getCompaniesOfGroup(false, false);
			
		} else if (actionName.equals(Constant.BROADCAST_ADD_COMPANIES)) {
			
			setAllCompaniesUnSelect();
			
			selectAllBtn.setTag(R.id.tag_select, false);
			selectAllBtn.setImageResource(R.drawable.button_unselect);
			if (null != noSectionIndexAdapter) noSectionIndexAdapter.notifyDataSetChanged();
			
		}
	}

	private void setAllCompaniesUnSelect() {
		
		for (int i = 0; i < companies.size(); i ++) {
			companies.get(i).select = false;
		}
		
	}
	
	@Override
	protected List<String> observeNotifications() {
		return stringList(Constant.BROADCAST_REFRESH_COMPANIES, Constant.BROADCAST_ADDED_PENDING_COMPANY,
				Constant.BROADCAST_ADD_COMPANIES_FROM_FOLLOW_COMPANIES, Constant.BROADCAST_FOLLOW_COMPANY,
				Constant.BROADCAST_UNFOLLOW_COMPANY, Constant.BROADCAST_ADD_COMPANIES);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		Constant.INDUSTRYID = "0";
		Constant.FILTER_INDUSTRY_NAME = "";
		Constant.industriesItem.clear();
		
	}
	
	@Override
	protected void initView() {
		super.initView();
		
		setLeftImageButton(R.drawable.back_arrow);
		setRightButton(R.string.edit);
		
		noSectionListView = (XListView) findViewById(R.id.noSectionListView);
		headView = LayoutInflater.from(mContext).inflate(R.layout.header_companies, null);
		headSelectAll = LayoutInflater.from(mContext).inflate(R.layout.header_select_all, null);
		selectAllBtn = (ImageView) headSelectAll.findViewById(R.id.selectBtn);
		selectAllBtn.setTag(R.id.tag_select, false);
		filterBtn = (TextView) headView.findViewById(R.id.filter);
		bottomBtn = (Button) findViewById(R.id.bottomBtn);
		importBtn = (Button) findViewById(R.id.importBtn);
		addCompaniesBtn = (Button) findViewById(R.id.addCompaniesBtn);
		noFollowedCompanies = (LinearLayout) findViewById(R.id.noFollowedCompanies);
		addToBtn = (Button) findViewById(R.id.addToBtn);
		removeBtn = (Button) findViewById(R.id.removeBtn);
		unfollowBtn = (Button) findViewById(R.id.unfollowBtn);
		addToBtnLinkedCompanies = (Button) findViewById(R.id.addToBtnLinkedCompanies);
		unfollowBtnLinkedCompanies = (Button) findViewById(R.id.unfollowBtnLinkedCompanies);
		bottomLayout = (RelativeLayout) findViewById(R.id.bottomLayout);
		bottomLayoutLinkedCompanies = (RelativeLayout) findViewById(R.id.bottomLayoutLinkedCompanies);
		bottomLayoutIsNotSystem = (LinearLayout) findViewById(R.id.bottomLayoutIsNotSystem);
		noCompaniesTitle = (TextView) findViewById(R.id.noCompaniesTitle);
		noCompaniesPt = (TextView) findViewById(R.id.noCompaniesPt);
	}
	
	@Override
	protected void initData() {
		super.initData();
		
		group = (Group) getIntent().getSerializableExtra(Constant.GROUP);
		groupId = group.getMogid();
		groupName = group.getName();
		isSystemGroup = group.getIsSsystem();
		if (!TextUtils.isEmpty(groupId)) {
			isLinkedCompanies = CommonUtil.isLinkedCompanies(group);
		}
		
		setTitle(groupName);
		
		getCompaniesOfGroup(true, false);
	}
	
	@Override
	protected void setData() {
		super.setData();
		
		noSectionListView.setVisibility(View.VISIBLE);
		noSectionIndexAdapter = new CompaniesNoSectionIndexAdapter(mContext, companies);
		noSectionIndexAdapter.setEdit(edit);
		setHeadView(noSectionListView);
		noSectionListView.setAdapter(noSectionIndexAdapter);
		noSectionIndexAdapter.notifyDataSetChanged();
		noSectionIndexAdapter.notifyDataSetInvalidated();
	}
	
	private void getCompaniesOfGroup(Boolean showDialog, final Boolean loadMore) {
		
		if (showDialog) showLoadingDialog();
		
		mApiHttp.getCompaniesOfGroupNew(group.getFollowLinkType(), nextPage, groupId, Constant.INDUSTRYID, APIHttpMetadata.kGGExceptPendingFollowCompanies, new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject jsonObject) {
				
				APIParser parser = new APIParser(jsonObject);
				if (parser.isOK()) {
					
					if (!loadMore) companies.clear();
					
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
					
					nextPage = parser.data().optString("next_page");
					
					Boolean haveMoreNews = page.hasMore;
					noSectionListView.setPullLoadEnable(haveMoreNews);
					
					facet = page.facet;
					if (facet != null) {
						industryData = facet.industryFacets;
					}
					
					setListView();
					
					Collections.sort(companies);
					
					filterBtn.setVisibility((companies.size() == 0) ? View.GONE : View.VISIBLE);
					
					if (!loadMore) setData();
					
				} else {
					
					if (companies != null) {
						companies.clear();
						if (null != noSectionIndexAdapter) noSectionIndexAdapter.notifyDataSetChanged();
					}
					noFollowedCompanies.setVisibility(View.VISIBLE);
					setNoCompaniesPromt();
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
	
	private void setNoCompaniesPromt() {
		
		if (isSystemGroup) {
			if (isLinkedCompanies) {
				noCompaniesTitle.setText(R.string.no_linked_companies);
				noCompaniesPt.setText("");
			} else {
				noCompaniesTitle.setText(R.string.no_followed_companies);
				noCompaniesPt.setText(R.string.no_followed_companies_pt);
			}
		} else {
			noCompaniesTitle.setText(R.string.no_companies);
			noCompaniesPt.setText(R.string.no_companies_pt);
		}
	}
	
	/**
	 * set list view and side bar
	 */
	private void setListView() {
		
		if (companies.size() == 0) {
			
			noFollowedCompanies.setVisibility(View.VISIBLE);
			setNoCompaniesPromt();
			
			if (edit) setBottomButton(companies);
			
		} else {
			
			noFollowedCompanies.setVisibility(View.GONE);
			
		}
	}
	
	public void setBottomButton(List<Company> companies) {
		
		selectedNum = 0;
		companyIds = new ArrayList<Long>();
		if (null != companies) {
			for (int i = 0 ; i < companies.size(); i ++) {
				if (companies.get(i).select) {
					selectedNum ++;
					companyIds.add(companies.get(i).orgID);
				}
			}
		}
		if (selectedNum == 0) {
			selected = false;
			if (isSystemGroup) {
				if (isLinkedCompanies) {
					bottomLayout.setVisibility(View.GONE);
					bottomLayoutLinkedCompanies.setVisibility(View.GONE);
				} else {
					bottomBtn.setText(R.string.suggested_companies);
					importBtn.setText(R.string.u_import);
				}
			} else {
				addCompaniesBtn.setVisibility(View.VISIBLE);
				bottomLayoutIsNotSystem.setVisibility(View.GONE);
			}
		} else {
			selected = true;
			if (isSystemGroup) {
				if (isLinkedCompanies) {
					bottomLayout.setVisibility(View.VISIBLE);
					bottomLayoutLinkedCompanies.setVisibility(View.VISIBLE);
				} else {
					bottomBtn.setText(R.string.unfollow);
					importBtn.setText(R.string.add_to);
				}
			} else {
				addCompaniesBtn.setVisibility(View.GONE);
				bottomLayoutIsNotSystem.setVisibility(View.VISIBLE);
			}
		}
	}
	
	@Override
	protected void setOnClickListener() {
		super.setOnClickListener();
		
		filterBtn.setOnClickListener(this);
		bottomBtn.setOnClickListener(this);
		noSectionListView.setOnItemClickListener(this);
		noFollowedCompanies.setOnClickListener(this);
		importBtn.setOnClickListener(this);
		addCompaniesBtn.setOnClickListener(this);
		addToBtn.setOnClickListener(this);
		removeBtn.setOnClickListener(this);
		unfollowBtn.setOnClickListener(this);
		addToBtnLinkedCompanies.setOnClickListener(this);
		unfollowBtnLinkedCompanies.setOnClickListener(this);
		
		noSectionListView.setXListViewListener(this);
		noSectionListView.setPullLoadEnable(true);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (requestCode == this.requestCode && resultCode == RESULT_OK) {
			nextPage = "";
			getCompaniesOfGroup(true, false);
		}
		
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		
		if (v == filterBtn) {
			
			if (industryData.size() == 0) {
				showShortToast(R.string.no_filters);
				return;
			}
			
			Constant.industriesItem = industryData;
			Intent intent = new Intent();
			intent.setClass(mContext, CompaniesFilterActivity.class);
			startActivityForResult(intent, requestCode);
			
		} else if (v == rightBtn) {
			
			edit = !edit;
			selectAllBtn.setTag(R.id.tag_select, false);
			selectAllBtn.setImageResource(R.drawable.button_unselect);
			if (edit) {
				setAllCompaniesUnSelect();
			}
			
			noSectionIndexAdapter.setEdit(edit);
			noSectionIndexAdapter.notifyDataSetChanged();
			
			setEditStatus();
			if (isLinkedCompanies) {
				if (!edit) {
					bottomLayout.setVisibility(View.GONE);
					bottomLayoutLinkedCompanies.setVisibility(View.GONE);
				}
			} else {
				setBottomButton(companies);
				setBottomLayoutStatus();
			}
			setLeftButtonVisible();
			setFiltersVisible();
			
			if (!isSystemGroup) {
				bottomLayoutIsNotSystem.setVisibility(edit ? View.GONE : View.VISIBLE);
			}
			
		} else if (v == bottomBtn) {
			
			if (companies.size() == 0) {
				startToSuggestedCompanyActivity();
				return;
			}
			if (selected) {
				showUnfollowDialog();
			} else {
				startToSuggestedCompanyActivity();
			}
			
		} else if (v == noFollowedCompanies) {
			
			getCompaniesOfGroup(true, false);
			
		} else if (v == leftImageBtn) {
			
			finish();
			
		} else if (v == importBtn) {
			
			if (selected) {//add to
				
				addCompaniesToGroup();
				
			} else {// import
				
				int accessToContactStatus = CommonUtil.getAccessInfo(mContext);
				if (accessToContactStatus != -1) {
					
					if (accessToContactStatus == 0) {//not allow
						showShortToast("Access to contacts denied");
					} else {//allow
						Intent intent = new Intent();
						intent.putExtra(Constant.GROUP, group);
						intent.setClass(mContext, ImportCompaniesActivity.class);
						mContext.startActivity(intent);
					}
					
					return;
				}
				
				final ImportDialog dialog = new ImportDialog(mContext);
				dialog.showDialog(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						dialog.dismissDialog();
						CommonUtil.saveAccessInfo(mContext, 0);
					}
					
				}, new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						dialog.dismissDialog();
						CommonUtil.saveAccessInfo(mContext, 1);
						Intent intent = new Intent();
						intent.putExtra(Constant.GROUP, group);
						intent.setClass(mContext, ImportCompaniesActivity.class);
						mContext.startActivity(intent);
					}
				});
				
			}
		} else if (v == addCompaniesBtn) {
			
			Intent intent = new Intent();
			intent.putExtra(Constant.GROUPID, group.getMogid());
			intent.putExtra(Constant.GROUPNAME, group.getName());
			intent.setClass(mContext, AddCompaniesFromFollowedCompaniesActivity.class);
			startActivity(intent);
			
		} else if (v == addToBtn) {
			
			addCompaniesToGroup();
			
		} else if (v == removeBtn) {
			
			final RemoveFromGroupDialog dialog = new RemoveFromGroupDialog(mContext);
			dialog.setRemovePt(getSelectedCompaniesIds().size() == 1 ? true : false);
			dialog.showDialog(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					dialog.dismissDialog();
					removeFromGroup(getSelectedCompaniesIds());
				}

			});
			
		} else if (v == unfollowBtn) {
			
			showUnfollowDialog();
			
		} else if (v == addToBtnLinkedCompanies) {
			
			addCompaniesToGroup();
			
		} else if (v == unfollowBtnLinkedCompanies) {
			
			showUnfollowDialog();
			
		}
	}

	private void startToSuggestedCompanyActivity() {
		Intent intent = new Intent();
		intent.setClass(mContext, SuggestedCompaniesActivity.class);
		intent.putExtra(Constant.GROUP, group);
		startActivity(intent);
	}

	private void showUnfollowDialog() {
		final CommonDialog dialog = new CommonDialog(mContext);
		dialog.setCancelable(false);
		String message;
		if (selectedNum > 1) {
			message = "Unfollow " + selectedNum + " Companies";
		} else {
			message = "Unfollow";
		}
		dialog.showDialog(message , new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismissDialog();
				showLoadingDialog();
				
				for (int i = 0; i < companyIds.size(); i ++) {
					unfollowCompany(companyIds.get(i));
				}
			}
		}, new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismissDialog();
			}
		});
	}
	
	/**
	 * get companies ids from which has selected
	 * @return
	 */
	private ArrayList<String> getSelectedCompaniesIds() {
		ArrayList<String> companiesIds = new ArrayList<String>();
		for (int i = 0; i < companies.size(); i ++) {
			if (companies.get(i).select) 
				companiesIds.add(companies.get(i).orgID + ""); 
		}
		return companiesIds;
	}
	
	/**
	 * remove companies from group
	 * @param companiesIds companies's id
	 */
	private void removeFromGroup(ArrayList<String> companiesIds) {
		
		showLoadingDialog();
		mApiHttp.removeCompanies(groupId, companiesIds, new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject jsonObject) {
				
				dismissLoadingDialog();
				
				APIParser parser = new APIParser(jsonObject);
				if (parser.isOK()) {
					
					nextPage = "";
					getCompaniesOfGroup(false, false);
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

	/**
	 * add companies to group
	 */
	private void addCompaniesToGroup() {
		
		ArrayList<String> companiesId = new ArrayList<String>();
		for (int i = 0; i < companies.size(); i ++) {
			if (companies.get(i).select) 
				companiesId.add(companies.get(i).orgID + ""); 
		}
		
		Intent intent = new Intent();
		intent.setClass(mContext, AddToGroupActivity.class);
		intent.putExtra(Constant.GROUP, group);
		intent.putStringArrayListExtra(Constant.COMPANIESID, companiesId);
		startActivity(intent);
	}
	
	private void setEditStatus() {
		setRightButton(edit ? R.string.cancel : R.string.edit);
	}

	private void setBottomLayoutStatus() {
		bottomLayout.setVisibility(edit ? View.VISIBLE : View.GONE);
		importBtn.setVisibility(isSystemGroup ? View.VISIBLE : View.GONE);
		bottomBtn.setVisibility(isSystemGroup ? View.VISIBLE : View.GONE);
		addCompaniesBtn.setVisibility(!isSystemGroup ? View.VISIBLE : View.GONE);
		bottomBtn.setText(mContext.getResources().getString(R.string.suggested_companies));
	}
	
	private void setLeftButtonVisible() {
		leftImageBtn.setVisibility(edit ? View.GONE : View.VISIBLE);
	}
	
	/**
	 * set filters status in case of status
	 */
	private void setFiltersVisible() {
		setHeadView(noSectionListView);
	}

	private void setHeadView(ListView listView) {
		if (edit) {
			listView.removeHeaderView(headView);
			listView.removeHeaderView(headSelectAll);
			listView.addHeaderView(headSelectAll);
			if (companies.size() == 0) listView.removeHeaderView(headSelectAll);
		} else {
			listView.removeHeaderView(headSelectAll);
			listView.removeHeaderView(headView);
			listView.addHeaderView(headView);
		}
	}
	
	private void unfollowCompany(final Long mCompanyId) {
		
		mApiHttp.unfollowCompany(mCompanyId, new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject jsonObject) {
				
				APIParser parser = new APIParser(jsonObject);
				if (parser.isOK()) {
					
					selectedNum--;
					
					if (selectedNum == 0) {
						
						if (companies.size() == 0) {
							setFiltersVisible();
							
							Intent intent = new Intent();
							intent.setAction(Constant.BROADCAST_REFRESH_COMPANIES);
							sendBroadcast(intent);
						} else {
							Intent intent = new Intent();
							intent.setAction(Constant.BROADCAST_REFRESH_NEWS);
							sendBroadcast(intent);
						}
						
						for (int i = 0; i < companies.size(); i ++) {
							if (mCompanyId == companies.get(i).orgID) {
								companies.remove(i);
								if (null != noSectionIndexAdapter) noSectionIndexAdapter.notifyDataSetChanged();
							}
						}
						
						Intent intent = new Intent();
						intent.setAction(Constant.BROADCAST_UNFOLLOW_COMPANY);
						sendBroadcast(intent);
						
						setBottomButton(null);
						dismissLoadingDialog();
					}
					
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
	
	private void setSelectAllBtnStatus() {
		Boolean isAllSelected = true;
		for (int i = 0; i < companies.size(); i ++) {
			if (!companies.get(i).select) isAllSelected = false;
		}
		selectAllBtn.setTag(R.id.tag_select, isAllSelected ? true : false);
		selectAllBtn.setImageResource(!isAllSelected ? R.drawable.button_unselect : R.drawable.button_select);
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {
		if (edit) {
			if (position == 1) {
				Boolean isSelect = (Boolean) selectAllBtn.getTag(R.id.tag_select);
				selectAllBtn.setTag(R.id.tag_select, !isSelect);
				selectAllBtn.setImageResource(!isSelect ? R.drawable.button_select : R.drawable.button_unselect);
				
				for (int i = 0; i < companies.size(); i ++) {
					companies.get(i).select = isSelect ? false : true;
				}
				setAdapterNotify();
				setBottomButton(companies);
				return;
			}
			
			Boolean select = companies.get(position - 2).select;
			companies.get(position - 2).select = !select;
			setSelectAllBtnStatus();
			setAdapterNotify();
			setBottomButton(companies);
			return;
		}
		
		if (position == 1) return;
		
		long companyId = companies.get(position - 2).orgID;
//		String website = companies.get(position - 1).website;
		
		if (companyId <= 0) {
			Intent intent = new Intent();
			intent.putExtra(Constant.COMPANYNAME, companies.get(position - 2).name);
			intent.putExtra(Constant.COMPANYID, companyId);
			intent.setClass(mContext, ProfileHelpActivity.class);
			startActivity(intent);
			return;
		}
		Intent intent = new Intent();
		intent.putExtra(Constant.COMPANYID, companyId);
		intent.setClass(mContext, CommonUtil.isTablet(mContext) ? CompanyTabletActivity.class : CompanyActivity.class);
		startActivity(intent);
	} 

	private void setAdapterNotify() {
		noSectionIndexAdapter.notifyDataSetChanged();
	}

	public int alphaIndexer(String s) {
		int position = 0;
		String st = "";
		if (companies != null) {
			for (int i = 0; i < companies.size(); i++) {
				st = companies.get(i).name.trim();
				if (!"".equals(st) && st != null) {
					if (st.startsWith(s)) {
						position = i + 1;
						break;
					}
				}
			}
		}
		return position;
	}

	@Override
	public void onRefresh() {
		nextPage = "";
		getCompaniesOfGroup(true, false);
	}

	@Override
	public void onLoadMore() {
		getCompaniesOfGroup(false, true);
	}
	
}
