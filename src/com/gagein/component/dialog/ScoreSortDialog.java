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
import com.gagein.adapter.ScoresSortAdapter;
import com.gagein.model.ScoresSort;
import com.gagein.ui.tablet.scores.ScoresFragment;
import com.gagein.util.CommonUtil;
import com.gagein.util.Constant;

/**
 * 
 * @author silen
 */
public class ScoreSortDialog implements OnItemClickListener{
	
	private Context mContext;
	private Dialog dialog;
	private View view;
	private LayoutInflater inflater;
	private ListView listView; 
	private ScoresSortAdapter adapter;
	private List<Boolean> checkedList = new ArrayList<Boolean>();
	private List<ScoresSort> scoresSorts = new ArrayList<ScoresSort>();
	private Fragment fragment;

	public ScoreSortDialog(Context context, Fragment fragment) {
		dialog = new Dialog(context, R.style.dialog);
		this.mContext = context;
		this.fragment = fragment;
		inflater = LayoutInflater.from(context);
		view = inflater.inflate(R.layout.popwindow_sortby_people, null);
		listView = (ListView) view.findViewById(R.id.listView);
		listView.setOnItemClickListener(this);
		
		dialog.setContentView(view);
	}

	public void showDialog() {
		
		for (int i = 0; i < Constant.scoresSorts.size(); i++) {
			scoresSorts.add(Constant.scoresSorts.get(i));
			checkedList.add(Constant.scoresSorts.get(i).getChecked());
		}
		
		adapter = new ScoresSortAdapter(mContext, scoresSorts, checkedList);
		listView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		adapter.notifyDataSetInvalidated();
		
		CommonUtil.setDialogWith(dialog);
		dialog.show();
		
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		
		Boolean selected = checkedList.get(position);
		if (selected) return;
		for (int i = 0; i < checkedList.size(); i++) {
			checkedList.set(i, false);
		}
		checkedList.set(position, true);
		adapter.notifyDataSetChanged();
		
		for (int i = 0; i < Constant.scoresSorts.size(); i++) {
			Constant.scoresSorts.get(i).setChecked(false);
		}
		for (int i = 0; i < checkedList.size(); i++) {
			if (checkedList.get(i)) Constant.scoresSorts.get(i).setChecked(true);
		}
		
		dismissDialog();
		((ScoresFragment)fragment).getCompaniesOfGroup(true, false, true);
		((ScoresFragment)fragment).setScoresSort();
		
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
