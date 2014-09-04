package com.gagein.component.dialog;

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
import com.gagein.adapter.search.FilterAdapter;
import com.gagein.model.filter.FilterItem;
import com.gagein.ui.tablet.search.company.SearchCompanyResultFragment;
import com.gagein.ui.tablet.search.people.SearchPeopleResultFragment;
import com.gagein.util.CommonUtil;
import com.gagein.util.Constant;

/**
 * 
 * @author silen
 */
public class SortBySearchDialog {
	private Context mContext;
	private Dialog dialog;
	private View view;
	private LayoutInflater inflater;
	private ListView listView;
	private FilterAdapter adapter; 
	private List<FilterItem> sortByItems;
	private Boolean fromCompany;

	public SortBySearchDialog(Context context, final Fragment mFragment, final Boolean fromCompany) {
		dialog = new Dialog(context, R.style.dialog);
		this.mContext = context;
		this.fromCompany = fromCompany;
		inflater = LayoutInflater.from(context);
		view = inflater.inflate(R.layout.popwindow_sortby_people, null);
		listView = (ListView) view.findViewById(R.id.listView);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				for (int i = 0; i < sortByItems.size(); i ++) {
					sortByItems.get(i).setChecked(false);
				}
				sortByItems.get(position).setChecked(true);
				adapter.notifyDataSetChanged();
				dismissDialog();
				if (fromCompany) {
					((SearchCompanyResultFragment)mFragment).initSearch();
					((SearchCompanyResultFragment)mFragment).setSortBy();
				} else {
					((SearchPeopleResultFragment)mFragment).initSearch();
					((SearchPeopleResultFragment)mFragment).setSortBy();
				}
			}
		});
		
		dialog.setContentView(view);
	}

	public void showDialog() {
		sortByItems = fromCompany ? Constant.MFILTERS.getSortByFromBuz() : Constant.MFILTERS.getSortByFromContact();
		adapter = new FilterAdapter(mContext, sortByItems);
		listView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		CommonUtil.setDialogWith(dialog);
		dialog.show();
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
