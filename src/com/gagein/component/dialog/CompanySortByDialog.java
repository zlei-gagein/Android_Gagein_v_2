package com.gagein.component.dialog;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.gagein.R;
import com.gagein.adapter.FilterSortByAdapter;
import com.gagein.http.APIHttpMetadata;
import com.gagein.ui.tablet.company.CompanyFragment;
import com.gagein.util.CommonUtil;
import com.gagein.util.Constant;

/**
 * 
 * @author silen
 */
public class CompanySortByDialog implements OnItemClickListener{
	private Context mContext;
	private Dialog dialog;
	private View view;
	private LayoutInflater inflater;
	private Boolean isPeople;
	private FilterSortByAdapter adapter;
	private ListView listView;
	private List<Boolean> checkedList;
	private Fragment mFragment;

	public CompanySortByDialog(Context context, final Fragment mFragment, Boolean isPeople) {
		dialog = new Dialog(context, R.style.dialog);
		this.mContext = context;
		this.isPeople = isPeople;
		this.mFragment = mFragment;
		inflater = LayoutInflater.from(context);
		view = inflater.inflate(R.layout.popwindow_sortby_people, null);
		listView = (ListView) view.findViewById(R.id.listView);
		listView.setOnItemClickListener(this);
		
		dialog.setContentView(view);
	}

	public void showDialog() {
		
		if (isPeople) {
			checkedList = new ArrayList<Boolean>();
			for (int i = 0; i < 3; i ++) {
				checkedList.add(false);
			}
			if (Constant.PEOPLE_SORT_BY == APIHttpMetadata.kGGContactsOrderByJobLevel) {
				checkedList.set(0, true);
			} else if (Constant.PEOPLE_SORT_BY == APIHttpMetadata.kGGContactsOrderByName) {
				checkedList.set(1, true);
			} else if (Constant.PEOPLE_SORT_BY == APIHttpMetadata.kGGContactsOrderByRecent) {
				checkedList.set(2, true);
			}
			adapter = new FilterSortByAdapter(mContext, checkedList, Constant.COMPANY_PERSON);
		} else {
			checkedList = new ArrayList<Boolean>();
			for (int i = 0; i < 4; i ++) {
				checkedList.add(false);
			}
			if (Constant.COMPETITOR_SORT_BY == APIHttpMetadata.kCompetitorOrderByEmployeeSize) {
				checkedList.set(0, true);
			} else if (Constant.COMPETITOR_SORT_BY == APIHttpMetadata.kCompetitorOrderByRevenueSize) {
				checkedList.set(1, true);
			} else if (Constant.COMPETITOR_SORT_BY == APIHttpMetadata.kCompetitorOrderByName) {
				checkedList.set(2, true);
			} else if (Constant.COMPETITOR_SORT_BY == APIHttpMetadata.KCOMPETITORORDERBYRELEVANCE) {
				checkedList.set(3, true);
			}
			
			adapter = new FilterSortByAdapter(mContext, checkedList, Constant.COMPANY_COMPETITOR);
		}
		listView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		adapter.notifyDataSetInvalidated();
		CommonUtil.setDialogWith(dialog);
		dialog.show();
		
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		
		for (int i= 0; i < checkedList.size(); i ++) {
			if (i == arg2) {
				checkedList.set(i, true);
			} else {
				checkedList.set(i, false);
			}
		}
		
		adapter.notifyDataSetChanged();
		
		if (isPeople) {
			
			for (int i= 0; i < checkedList.size(); i ++) {
				if (checkedList.get(i) == true) {
					if (i == 0) {
						Constant.PEOPLE_SORT_BY = APIHttpMetadata.kGGContactsOrderByJobLevel;
					} else if (i == 1) {
						Constant.PEOPLE_SORT_BY = APIHttpMetadata.kGGContactsOrderByName;
					} else if (i == 2) {
						Constant.PEOPLE_SORT_BY = APIHttpMetadata.kGGContactsOrderByRecent;
					}
				}
			}
			
			dismissDialog();
			((CompanyFragment)mFragment).getPeopleBySortBy();
			
		} else {
			
			for (int i= 0; i < checkedList.size(); i ++) {
				if (checkedList.get(i) == true) {
					if (i == 0) {
						Constant.COMPETITOR_SORT_BY = APIHttpMetadata.kCompetitorOrderByEmployeeSize;
					} else if (i == 1) {
						Constant.COMPETITOR_SORT_BY = APIHttpMetadata.kCompetitorOrderByRevenueSize;
					} else if (i == 2) {
						Constant.COMPETITOR_SORT_BY = APIHttpMetadata.kCompetitorOrderByName;
					} else if (i == 3) {
						Constant.COMPETITOR_SORT_BY = APIHttpMetadata.KCOMPETITORORDERBYRELEVANCE;
					}
				}
			}
			dismissDialog();
			((CompanyFragment)mFragment).getCompetitorsBySortBy();
		}
	}
	
	public void dismissDialog() {
		dialog.dismiss();
	}

	public boolean isShow() {
		if (dialog.isShowing()) {
			return true;
		} else {
			return false;
		}
	}

	public Dialog getDialog() {
		return dialog;
	}
	
	public Window getWindow() {
		return dialog.getWindow();
	}

	public void setCancelable(boolean is) {
		dialog.setCancelable(is);
	}

}
