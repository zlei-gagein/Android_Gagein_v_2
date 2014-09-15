package com.gagein.ui.tablet.companies;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
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
import com.gagein.http.APIHttp;
import com.gagein.http.APIHttpMetadata;
import com.gagein.http.APIParser;
import com.gagein.model.Company;
import com.gagein.model.DataPage;
import com.gagein.model.Facet;
import com.gagein.model.FacetItemIndustry;
import com.gagein.model.Group;
import com.gagein.ui.BaseFragment;
import com.gagein.ui.companies.AddCompaniesFromFollowedCompaniesActivity;
import com.gagein.ui.companies.AddToGroupActivity;
import com.gagein.ui.companies.ImportCompaniesActivity;
import com.gagein.ui.companies.ProfileHelpActivity;
import com.gagein.ui.companies.SuggestedCompaniesActivity;
import com.gagein.ui.company.CompanyActivity;
import com.gagein.ui.tablet.company.CompanyTabletActivity;
import com.gagein.util.CommonUtil;
import com.gagein.util.Constant;

public class CompaniesFragment extends BaseFragment implements OnItemClickListener, IXListViewListener{

	private List<Company> companies = new ArrayList<Company>();
	private XListView noSectionListView;
	private View headView;
	private View headSelectAll;
	public ImageView selectAllBtn;
	public CompaniesNoSectionIndexAdapter noSectionIndexAdapter;
	private TextView filterBtn;
	private Button bottomBtn;
	public Boolean edit = false;
	private Boolean selected = false;
	private int selectedNum = 0;
	private List<Long> companyIds;
	private Facet facet;
	private List<FacetItemIndustry> industryData = new ArrayList<FacetItemIndustry>();
	private int requestCode = 1;
	private LinearLayout noFollowedCompaniesLayout;
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
	public String nextPage = "";
	private OnFilterClickListener filterListener;
	
	public interface OnFilterClickListener {
		public void onFilterClickListener();
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		try {
			filterListener = (OnFilterClickListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + "must implement onFilterClickListener");
		}
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		view = inflater.inflate(R.layout.activity_companies, container, false);
		mContext = getActivity();
		mApiHttp = new APIHttp(mContext);
		
		doInit();
		return view;
		
	}
	
	@Override
	protected void initView() {
		super.initView();
		
		setTitle(R.string.u_followed_companies);
		setRightButton(R.string.edit);
		
		noSectionListView = (XListView) view.findViewById(R.id.noSectionListView);
		headView = LayoutInflater.from(mContext).inflate(R.layout.header_companies, null);
		headSelectAll = LayoutInflater.from(mContext).inflate(R.layout.header_select_all, null);
		selectAllBtn = (ImageView) headSelectAll.findViewById(R.id.selectBtn);
		selectAllBtn.setTag(R.id.tag_select, false);
		filterBtn = (TextView) headView.findViewById(R.id.filter);
		bottomBtn = (Button) view.findViewById(R.id.bottomBtn);
		importBtn = (Button) view.findViewById(R.id.importBtn);
		addCompaniesBtn = (Button) view.findViewById(R.id.addCompaniesBtn);
		noFollowedCompaniesLayout = (LinearLayout) view.findViewById(R.id.noFollowedCompanies);
		addToBtn = (Button) view.findViewById(R.id.addToBtn);
		removeBtn = (Button) view.findViewById(R.id.removeBtn);
		unfollowBtn = (Button) view.findViewById(R.id.unfollowBtn);
		addToBtnLinkedCompanies = (Button) view.findViewById(R.id.addToBtnLinkedCompanies);
		unfollowBtnLinkedCompanies = (Button) view.findViewById(R.id.unfollowBtnLinkedCompanies);
		bottomLayout = (RelativeLayout) view.findViewById(R.id.bottomLayout);
		bottomLayoutLinkedCompanies = (RelativeLayout) view.findViewById(R.id.bottomLayoutLinkedCompanies);
		bottomLayoutIsNotSystem = (LinearLayout) view.findViewById(R.id.bottomLayoutIsNotSystem);
		noCompaniesTitle = (TextView) view.findViewById(R.id.noCompaniesTitle);
		noCompaniesPt = (TextView) view.findViewById(R.id.noCompaniesPt);
		
	}
	
	@Override
	protected void initData() {
		super.initData();
		
		groupId = group.getMogid();
		groupName = group.getName();
		isSystemGroup = group.getIsSsystem();
		if (!TextUtils.isEmpty(groupId)) isLinkedCompanies = CommonUtil.isLinkedCompanies(group);
		
		setTitle(groupName);
		
		getCompaniesOfGroup(true, false);
	}
	
	@Override
	protected void setData() {
		super.setData();
		
		filterBtn.setVisibility((companies.size() == 0) ? View.GONE : View.VISIBLE);
		noSectionListView.setVisibility(View.VISIBLE);
		noSectionIndexAdapter = new CompaniesNoSectionIndexAdapter(mContext, companies);
		noSectionIndexAdapter.setEdit(edit);
		setHeadView(noSectionListView);
		noSectionListView.setAdapter(noSectionIndexAdapter);
		noSectionIndexAdapter.notifyDataSetChanged();
		noSectionIndexAdapter.notifyDataSetInvalidated();
		
	}
	
	public void setAllCompaniesUnSelect() {
		for (int i = 0; i < companies.size(); i ++) {
			companies.get(i).select = false;
		}
	}
	
	public void getCompaniesOfGroup(Boolean showDialog, final Boolean loadMore) {
		
		if (showDialog) CommonUtil.showLoadingDialog(mContext);
		
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
					noFollowedCompaniesLayout.setVisibility(View.VISIBLE);
					setNoCompaniesPromt();
				}
				
				dismissLoadingDialog();
			}


			
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				CommonUtil.dissmissLoadingDialog();
				CommonUtil.showLongToast(mContext.getResources().getString(R.string.connection_error));
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
			
			noFollowedCompaniesLayout.setVisibility(View.VISIBLE);
			setNoCompaniesPromt();
			
			if (edit) setBottomButton(companies);
			
		} else {
			
			noFollowedCompaniesLayout.setVisibility(View.GONE);
			
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
		noFollowedCompaniesLayout.setOnClickListener(this);
		importBtn.setOnClickListener(this);
		addCompaniesBtn.setOnClickListener(this);
		addToBtn.setOnClickListener(this);
		removeBtn.setOnClickListener(this);
		unfollowBtn.setOnClickListener(this);
		addToBtnLinkedCompanies.setOnClickListener(this);
		unfollowBtnLinkedCompanies.setOnClickListener(this);
		
		noSectionListView.setXListViewListener(this);
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		
		if (v == filterBtn) {
			if (industryData.size() == 0) {
				showShortToast(R.string.no_filters);
				return;
			}
			
			filterListener.onFilterClickListener();
			
		} else if (v == rightBtn) {
			
			edit = !edit;
			selectAllBtn.setTag(R.id.tag_select, false);
			selectAllBtn.setImageResource(R.drawable.button_unselect);
			if (edit) {
				for (int i = 0; i < companies.size(); i ++) {
					companies.get(i).select = false;
				}
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
		} else if (v == noFollowedCompaniesLayout) {
			
			getCompaniesOfGroup(true, false);
			
		} else if (v == leftImageBtn) {
			//TODO
//			finish();
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
				CommonUtil.showLoadingDialog(mContext);
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
		
		showLoadingDialog(mContext);
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
					mContext.sendBroadcast(intent);
					
				} else {
					alertMessageForParser(parser);
				}
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				CommonUtil.dissmissLoadingDialog();
				CommonUtil.showLongToast(mContext.getResources().getString(R.string.connection_error));
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
	
	public void setEditStatus() {
		setRightButton(edit ? R.string.cancel : R.string.edit);
	}
	
	public void setNoFollowedCompaniesLayoutGone() {
		if (noFollowedCompaniesLayout.getVisibility() == View.VISIBLE) noFollowedCompaniesLayout.setVisibility(View.GONE);
	}

	public void setBottomLayoutStatus() {
		bottomLayout.setVisibility(edit ? View.VISIBLE : View.GONE);
		importBtn.setVisibility(isSystemGroup ? View.VISIBLE : View.GONE);
		bottomBtn.setVisibility(isSystemGroup ? View.VISIBLE : View.GONE);
		addCompaniesBtn.setVisibility(!isSystemGroup ? View.VISIBLE : View.GONE);
		bottomBtn.setText(mContext.getResources().getString(R.string.suggested_companies));
	}
	
	private void setBottomLayoutStatusForLinkedCompanies() {
		bottomLayout.setVisibility(edit ? View.VISIBLE : View.GONE);
		bottomLayoutLinkedCompanies.setVisibility(edit ? View.VISIBLE : View.GONE);
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
					
					for (int i = 0; i < companies.size(); i ++) {
						if (mCompanyId == companies.get(i).orgID) {
							companies.remove(i);
							if (null != noSectionIndexAdapter) noSectionIndexAdapter.notifyDataSetChanged();
						}
					}
					
					if (companies.size() == 0) {
						setFiltersVisible();
						
						Intent intent = new Intent();
						intent.setAction(Constant.BROADCAST_REFRESH_COMPANIES);
						mContext.sendBroadcast(intent);
					} else {
						Intent intent = new Intent();
						intent.setAction(Constant.BROADCAST_REFRESH_NEWS);
						mContext.sendBroadcast(intent);
					}
					selectedNum--;
					if (selectedNum == 0) {
						Intent intent = new Intent();
						intent.setAction(Constant.BROADCAST_UNFOLLOW_COMPANY);
						mContext.sendBroadcast(intent);
						
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
				CommonUtil.dissmissLoadingDialog();
				CommonUtil.showLongToast(mContext.getResources().getString(R.string.connection_error));
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
		// TODO Auto-generated method stub
		nextPage = "";
		getCompaniesOfGroup(true, false);
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		getCompaniesOfGroup(false, true);
	}
}
