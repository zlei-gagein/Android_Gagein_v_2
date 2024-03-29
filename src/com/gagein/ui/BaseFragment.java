package com.gagein.ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Response.Listener;
import com.gagein.R;
import com.gagein.component.dialog.ShareDialog;
import com.gagein.http.APIHttp;
import com.gagein.http.APIParser;
import com.gagein.model.Agent;
import com.gagein.model.DataPage;
import com.gagein.model.Update;
import com.gagein.model.filter.FilterItem;
import com.gagein.util.ActivityHelper;
import com.gagein.util.CommonUtil;
import com.gagein.util.Constant;
import com.gagein.util.ConfigurableReceiver.OnReceiveListener;

public class BaseFragment extends Fragment implements OnReceiveListener , OnClickListener{
	
	protected Context mContext = null;
	protected APIHttp mApiHttp;
	protected View view;
	protected RelativeLayout top;
	protected TextView title;
	protected Button rightBtn;
	protected ImageView rightImageBtn;
	protected ImageView rightImageBtn2;
	protected Button leftBtn;
	protected ImageView leftImageBtn;
	protected ShareDialog shareDialog;
	public static final String INDENT_EXTRA_DATA_KEY = "INDENT_BUNDLE_DATA_KEY";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	/**do init in sequence: view, data, listenter */
	protected void doInit() {
		
		mContext = getActivity();
		mApiHttp = new APIHttp(mContext);
		
		initView();
		initData();
		setOnClickListener();
		
	}
	
	protected void initView() {
		top = (RelativeLayout) view.findViewById(R.id.top);
		if (null != top) {
			title = (TextView) top.findViewById(R.id.title);
			rightBtn = (Button)top.findViewById(R.id.rightBtn);
			rightImageBtn = (ImageView)top.findViewById(R.id.rightImageBtn);
			rightImageBtn2 = (ImageView)top.findViewById(R.id.rightImageBtn2);
			leftBtn = (Button)top.findViewById(R.id.leftBtn);
			leftImageBtn = (ImageView)top.findViewById(R.id.leftImageBtn);
		}
	}
	
	protected void setLeftImageButton(int drawableId) {
		leftImageBtn.setVisibility(View.VISIBLE);
		if (0 != drawableId) leftImageBtn.setBackgroundResource(drawableId);
	}
	
	protected void setLeftButton(int stringId) {
		leftBtn.setVisibility(View.VISIBLE);
		leftBtn.setText(stringId);
	}
	
	protected void setRightImageButton(int drawableId) {
		rightImageBtn.setVisibility(View.VISIBLE);
		if (0 != drawableId) rightImageBtn.setBackgroundResource(drawableId);
	}
	
	protected void setRightImageButton2(int drawableId) {
		rightImageBtn2.setVisibility(View.VISIBLE);
		if (0 != drawableId) rightImageBtn2.setBackgroundResource(drawableId);
	}
	
	protected void setRightButton(int stringId) {
		rightBtn.setVisibility(View.VISIBLE);
		rightBtn.setText(stringId);
	}
	
	protected void setLeftImageButtonVisible(int visible) {
		leftImageBtn.setVisibility(visible);
	}
	
	public void setTitle(int stringId) {
		title.setText(stringId);
	}
	
	public void setTitle(String string) {
		title.setText(string);
	}
	
	public void setTitleVisible(int visible) {
		if (null != title) { 
			title.setVisibility(visible);
		}
	}
	public void setLeftBtnVisible(int visible) {
		if (null != leftBtn) { 
			leftBtn.setVisibility(visible);
		}
	}
	public void setRightImageVisible(int visible) {
		if (null != rightImageBtn) { 
			rightImageBtn.setVisibility(visible);
		}
	}
	
	protected void initData() {
	}
	
	protected void setData() {
	}

	protected void setOnClickListener() {
		if (null != rightBtn) rightBtn.setOnClickListener(this);
		if (null != rightImageBtn) rightImageBtn.setOnClickListener(this);
		if (null != rightImageBtn2) rightImageBtn2.setOnClickListener(this);
		if (null != leftBtn) leftBtn.setOnClickListener(this);
		if (null != leftImageBtn) leftImageBtn.setOnClickListener(this);
	}
	
	/**simple method for activity starting */
	protected final void startActivitySimple(Class<?> aActivityClass) {
		startActivityWithData(aActivityClass, null);
	}
	
	/** start activity with data */
	protected final void startActivityWithData(Class<?> aActivityClass, Object aData) {
		if (aActivityClass != null && Activity.class.isAssignableFrom(aActivityClass)) {
			Intent intent = new Intent(mContext, aActivityClass);
			if (aData != null && aData instanceof Serializable) {
				intent.putExtra(INDENT_EXTRA_DATA_KEY, (Serializable)aData);
			}
			startActivity(intent);
		}
	}
	
	protected void showConnectionError(Context mContext) {
		CommonUtil.dissmissLoadingDialog();
		CommonUtil.showLongToast(mContext.getResources().getString(R.string.connection_error));
	}
	
	protected void showLoadingDialog(Context mContext) {
		CommonUtil.showLoadingDialog(mContext);
	}
	
	protected void dismissLoadingDialog() {
		CommonUtil.dissmissLoadingDialog();
	}
	
	protected void showShortToast(int stringId) {
		CommonUtil.showShortToast(stringFromResID(stringId));
	}
	
	protected final String stringFromResID(int aResID) {
		return CommonUtil.stringFromResID(aResID, mContext);
	}
	
	protected void showShortToast(String string) {
		CommonUtil.showShortToast(string);
	}
	
	protected void alertMessageForParser(APIParser parser) {
		CommonUtil.alertMessageForParser(parser);
	}
	
	/** start web activity */
	protected final void startWebActivity(String url) {
		startWebActivity(url, true);
	}
	
	protected final void startWebActivity(String url, boolean inApp) {
		if (inApp) {
			ActivityHelper.startWebActivity(url, mContext);
		} else {
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        	startActivity(intent);
		}
	}

	@Override
	public void handleNotifications(Context aContext, Intent anIntent) {
		// TODO Auto-generated method stub
		
	}
	
	protected final List<String> stringList(String... strings) {
		List<String> stringList = new ArrayList<String>();
		Collections.addAll(stringList, strings);
		return stringList;
	}

	protected List<String> observeNotifications() {
		return null;	// default: return null, subclass override this method to provide actions it interested
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public static void loadImage(String aUrl, ImageView aImageView) {
		CommonUtil.loadImage(aUrl, aImageView);
	}
	
	public void showShareLayout(Context mContext) {
		setShareDialog(mContext);
		shareDialog.showDialog();
	}
	
	public void showShareLayout(Update update, Context mContext) {
		setShareDialog(mContext);
		shareDialog.showDialog(update, true);
	}

	private void setShareDialog(Context mContext) {
		shareDialog = new ShareDialog(mContext);
		Window window = shareDialog.getWindow();
		window.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		window.setGravity(Gravity.BOTTOM);
		window.setWindowAnimations(R.style.dialog_animation);
		shareDialog.setCancelable(false);
	}
	
	protected void getSelecedNewsTriggers() {
		
		mApiHttp.getAgentFilters(new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject jsonObject) {
				
				List<Agent> agents = new ArrayList<Agent>();
				List<FilterItem> newsTriggers = new ArrayList<FilterItem>();
				
				APIParser parser = new APIParser(jsonObject);
				if (parser.isOK()) {

					DataPage agentsPage = parser.parseGetAgentFiltersList();
					List<Object> items = agentsPage.items;
					if (items != null) {
						
						Agent allTriggers = new Agent();
						allTriggers.agentID = "-10";
						allTriggers.name = "All";
						agents.add(allTriggers);
						
						for (Object obj : items) {
							Agent agent = (Agent)obj;
							if (agent.checked) {
								agents.add(agent);
							}
						}
						
						for (int i = 0; i < agents.size(); i ++) {
							Agent agent = agents.get(i);
							FilterItem newsTrigger = new FilterItem();
							newsTrigger.setKey(agent.agentID);
							newsTrigger.setValue(agent.name);
							newsTriggers.add(newsTrigger);
						}
						
						Constant.MFILTERS.setNewsTriggers(newsTriggers);
					}
					
					if (agents.size() > 0) setData();
					
				} else {
					alertMessageForParser(parser);
				}
				
				dismissLoadingDialog();
			}
			
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				dismissLoadingDialog();
			}
		});
		
	}
	
}
