package com.gagein.ui.main;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.gagein.R;
import com.gagein.component.EmptyView;
import com.gagein.component.dialog.ShareDialog;
import com.gagein.http.APIHttp;
import com.gagein.http.APIParser;
import com.gagein.model.Update;
import com.gagein.util.ActivityHelper;
import com.gagein.util.CommonUtil;
import com.gagein.util.ConfigurableReceiver;
import com.gagein.util.ConfigurableReceiver.OnReceiveListener;
import com.gagein.util.Constant;
import com.gagein.util.Log;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * base Activity
 * @author Silen
 *
 */
public class BaseActivity extends Activity implements OnReceiveListener, OnClickListener{

	protected RelativeLayout top;
	protected TextView title;
	protected Button rightBtn;
	protected ImageView rightImageBtn;
	protected ImageView rightImageBtn2;
	protected Button leftBtn;
	protected ImageView leftImageBtn;
	protected String TAG;
	public static final String INDENT_EXTRA_DATA_KEY = "INDENT_BUNDLE_DATA_KEY";
	protected Context mContext = null;
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	protected ConfigurableReceiver defaultReceiver;		// default broadcast receiver
	protected APIHttp mApiHttp;
	protected boolean doubleBackToExitPressedOnce = false;
	protected ShareDialog shareDialog;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TAG = tag();
		mContext = this;
		mApiHttp = new APIHttp(mContext);
		
		getWindow().setSoftInputMode(EditorInfo.IME_ACTION_DONE);
		
		if (!CommonUtil.isTablet(mContext)) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		
		defaultReceiver = new ConfigurableReceiver(observeNotifications(), this);
		defaultReceiver.register(mContext);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		defaultReceiver.unregister();
		
		if (null != mApiHttp) mApiHttp.stop();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public void handleNotifications(Context aContext, Intent anIntent) {
		// default: do nothing, subclass override this method, handle receiver actions
	}
	
	protected final List<String> stringList(String... strings) {
		List<String> stringList = new ArrayList<String>();
		Collections.addAll(stringList, strings);
		return stringList;
	}

	protected List<String> observeNotifications() {
		return null;	// default: return null, subclass override this method to provide actions it interested
	}
	
	protected void initView() {
		
		top = (RelativeLayout) findViewById(R.id.top);
		if (null != top) {
			title = (TextView) top.findViewById(R.id.title);
			rightBtn = (Button)top.findViewById(R.id.rightBtn);
			rightImageBtn = (ImageView)top.findViewById(R.id.rightImageBtn);
			rightImageBtn2 = (ImageView)top.findViewById(R.id.rightImageBtn2);
			leftBtn = (Button)top.findViewById(R.id.leftBtn);
			leftImageBtn = (ImageView)top.findViewById(R.id.leftImageBtn);
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
	
	public void setTitle(int stringId) {
		title.setText(stringId);
	}
	
	public void setTitle(String string) {
		title.setText(string);
	}
	
	/**do init in sequence: view, data, listenter */
	protected final void doInit() {
		initView();
		initData();
		setOnClickListener();
	}
	
	protected final String tag() {
		return CommonUtil.tagForClass(this.getClass());
	}
	
	protected final String stringFromResID(int aResID) {
		return CommonUtil.stringFromResID(aResID, this);
	}
	
	/**simple method for activity starting */
	protected final void startActivitySimple(Class<?> aActivityClass) {
		startActivityWithData(aActivityClass, null);
	}
	
	/** start activity with data */
	protected final void startActivityWithData(Class<?> aActivityClass, Object aData) {
		if (aActivityClass != null && Activity.class.isAssignableFrom(aActivityClass)) {
			Intent intent = new Intent(this, aActivityClass);
			if (aData != null && aData instanceof Serializable) {
				intent.putExtra(INDENT_EXTRA_DATA_KEY, (Serializable)aData);
			}
			startActivity(intent);
		}
	}
	
	/**alert message according to message code of the parser*/
	protected final void alertMessageForParser(APIParser parser) {
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
	
	protected final void startCompanyDetailActivity(long aCompanyID) {
//		ActivityHelper.startCompanyDetailActivity(aCompanyID, mContext);//TODO
	}
	
	/** handle message code for empty view */
	protected final void handleMessageCode(int aCode, EmptyView aEmptyView) {
		aEmptyView.applyMessageCode(aCode);
		aEmptyView.getBtnAction().setTag(Integer.valueOf(aCode));
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		CommonUtil.hideSoftKeyBoard(mContext, BaseActivity.this);
	}
	
	protected void showConnectionError() {
		CommonUtil.dissmissLoadingDialog();
		CommonUtil.showLongToast(mContext.getResources().getString(R.string.connection_error));
	}
	
	protected void showLoadingDialog() {
		CommonUtil.showLoadingDialog(mContext);
	}
	
	protected void dismissLoadingDialog() {
		CommonUtil.dissmissLoadingDialog();
	}
	
	protected void showShortToast(int stringId) {
		CommonUtil.showShortToast(stringFromResID(stringId));
	}
	
	protected void showShortToast(String string) {
		CommonUtil.showShortToast(string);
	}
	
	protected void setLeftImageButton(int drawableId) {
		leftImageBtn.setVisibility(View.VISIBLE);
		if (0 != drawableId) leftImageBtn.setBackgroundResource(drawableId);
	}
	
	protected void setLeftButton(int stringId) {
		leftBtn.setVisibility(View.VISIBLE);
		leftBtn.setText(stringFromResID(stringId));
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
		rightBtn.setText(stringFromResID(stringId));
	}
	
	protected void setRightButtonVisible(int visible) {
		rightBtn.setVisibility(visible);
	}
	
	protected void setLeftButtonVisible(int visible) {
		leftBtn.setVisibility(visible);
	}
	
	protected void setLeftImageButtonVisible(int visible) {
		leftImageBtn.setVisibility(visible);
	}
	
	public static void loadImage(String aUrl, ImageView aImageView) {
		CommonUtil.loadImage(aUrl, aImageView);
	}
	
	public void showShareLayout() {
		setShareDialog();
		shareDialog.showDialog();
	}
	
	public void showShareLayout(Update update, Boolean showBookmarks) {
		setShareDialog();
		shareDialog.showDialog(update, showBookmarks);
	}

	private void setShareDialog() {
		shareDialog = new ShareDialog(mContext);
		Window window = shareDialog.getWindow();
		window.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		window.setGravity(Gravity.BOTTOM);
		window.setWindowAnimations(R.style.dialog_animation);
		shareDialog.setCancelable(false);
	}
	
	public void showImageShortToast(int stringId) {
		CommonUtil.showImageShortToast(mContext.getResources().getString(stringId), mContext);
	}
	
	public void showDialog(String msg) {
		CommonUtil.showDialog(msg);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
	}
	
	
	protected void getFilter() {
		Log.v("silen", "filter = 1");
		mApiHttp.getFilter(new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject jsonObject) {
				
				APIParser parser = new APIParser(jsonObject);
				if (parser.isOK()) {
					Constant.MFILTERS = parser.parserFilters();
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
}
