package com.gagein.ui.main;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.gagein.R;
import com.gagein.http.APIHttpMetadata;
import com.gagein.http.APIParser;
import com.gagein.model.Happening;
import com.gagein.model.Update;
import com.gagein.util.CommonUtil;
import com.gagein.util.Log;
import com.gagein.util.RuntimeData;

public class EditShareActivity extends BaseActivity {
	
	public static final String INTENT_KEY_SHARE_TYPE = "SHARE_TYPE";
	public static final int SHARE_UPDATE = 1;
	public static final int SHARE_HAPPENING = 2;
	
	private final int MAX_SUMMARY_LENGTH = 250;
	
	private Update update;
	private Happening happening;
	private int snType;			// social type, linkedin, twitter...etc
	private int shareType;		// share type, what is going to be shared, update or happening...
	
	private EditText edtContent;
	private TextView lblTxtCount;
	private int textLimitation;
	private int setSelection = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_share);
		
		snType = getIntent().getIntExtra(INDENT_EXTRA_DATA_KEY, APIHttpMetadata.kGGSnTypeUnknown);
		shareType = getIntent().getIntExtra(INTENT_KEY_SHARE_TYPE, SHARE_UPDATE);
		
		textLimitation = CommonUtil.maxLengthForSnType(snType);
		
		
		if (shareType == SHARE_UPDATE) {
			update = RuntimeData.updateForShare;
			RuntimeData.updateForShare = null;
		} else if (shareType == SHARE_HAPPENING) {
			happening = RuntimeData.happeningForShare;
			RuntimeData.happeningForShare = null;
		}
				
		getSocialNetworkTypes();
		doInit();
	}
	
	/** 获取社交网络类型 */
	private void getSocialNetworkTypes() {
		mApiHttp.snGetList(new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject jsonObject) {

				APIParser parser = new APIParser(jsonObject);
				if (parser.isOK()) {
					List<String> snTypes = RuntimeData.sharedInstance().getSnTypes();
					snTypes.clear();
					snTypes.addAll(parser.parseSnGetList());
				}
			}
			
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				showConnectionError();
			}
		});
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		if (v == rightBtn) {
			
			String message = messageForShare();
			
			if (TextUtils.isEmpty(message)) {
				CommonUtil.showLongToast("Please write something to share.");
			} else {
				Log.v("silen", "shareType = " + shareType);
				if (shareType == SHARE_UPDATE && update != null) {
					CommonUtil.showLoadingDialog(mContext);
					String summary = !TextUtils.isEmpty(update.content) ? update.content : update.textview;
					if (summary != null && summary.length() > MAX_SUMMARY_LENGTH) {
						summary = summary.substring(0, MAX_SUMMARY_LENGTH);
					}
					String picUrl = (update.pictures != null && update.pictures.size() > 0) ? update.pictures.get(0) : null;
				
					mApiHttp.snShareUpdate(update.newsId, snType, message, update.headline, summary, picUrl, new Listener<JSONObject>() {

						@Override
						public void onResponse(JSONObject jsonObject) {
							handleShareResponse(jsonObject);
							CommonUtil.dissmissLoadingDialog();
						}
						
					}, new Response.ErrorListener() {

						@Override
						public void onErrorResponse(VolleyError error) {
							showConnectionError();
						}
					});
					
				} else if (shareType == SHARE_HAPPENING && happening != null) {
					CommonUtil.showLoadingDialog(mContext);
					if (happening.isPersonEvent()) {
						Log.v("silen", "happening.eventId = " + happening.eventId);
						Log.v("silen", "happening.id = " + happening.id);
						
						mApiHttp.snSharePersonEvent(snType, happening.eventId, message, new Listener<JSONObject>() {

							@Override
							public void onResponse(JSONObject jsonObject) {
								handleShareResponse(jsonObject);
								CommonUtil.dissmissLoadingDialog();
							}
							
						}, new Response.ErrorListener() {

							@Override
							public void onErrorResponse(VolleyError error) {
								showConnectionError();
							}
						});
						
					} else {
						Log.v("silen", "happening.eventId = " + happening.eventId);
						Log.v("silen", "happening.id = " + happening.id);
						
						mApiHttp.snShareCompanyEvent(snType, happening.eventId, message, new Listener<JSONObject>() {

							@Override
							public void onResponse(JSONObject jsonObject) {
								handleShareResponse(jsonObject);
								CommonUtil.dissmissLoadingDialog();
							}
							
						}, new Response.ErrorListener() {

							@Override
							public void onErrorResponse(VolleyError error) {
								showConnectionError();
							}
						});
					}
				}
			}
		}
	}

	

	@Override
	protected void initView() {
		super.initView();
		setTitle(CommonUtil.nameFromSnType(snType));
		setRightButton(R.string.share);
		
		edtContent = (EditText) findViewById(R.id.editor);
		lblTxtCount = (TextView) findViewById(R.id.lblTxtCount);
	}

	@Override
	protected void initData() {
		super.initData();
		
		
		if (shareType == SHARE_UPDATE && update != null) {
			String message = update.headline;
			edtContent.setText(message);
			setEidtSelectionPosition();
		} else if (shareType == SHARE_HAPPENING && happening != null) {
			edtContent.setText(happening.messageStr);
			setEidtSelectionPosition();
		}
		
		InputFilter[] fArray = new InputFilter[1];
		fArray[0] = new InputFilter.LengthFilter(textLimitation);
		edtContent.setFilters(fArray);
		
		lblTxtCount.setText(edtContent.getText().toString().length() + "/" + textLimitation);
		
	}
	
	@SuppressLint("HandlerLeak")
	Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			
			edtContent.setSelection(edtContent.getText().length());
			
		}
		
	};
	
	private void setEidtSelectionPosition() {
		Timer timer = new Timer();
		timer.schedule(new TimerTask(){
		    @Override
		    public void run() {
		    	Message message = new Message();
		    	message.what = setSelection;
		    	handler.sendMessage(message);
		    }
		}, 20);
	}

	@Override
	protected void setOnClickListener() {
		super.setOnClickListener();
		
		edtContent.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				String txt = s.toString();
				lblTxtCount.setText(txt.length() + "/" + textLimitation);
			}
		});
	}

	
	
	/** message for share */
	private String messageForShare() {
		String message;
		int maxLenght = CommonUtil.maxMessageLengthForSharing(snType);
		message = edtContent.getText().toString();
		int messageLen = message.length();
		if (messageLen <= 0) {
			return null;
		} else if (messageLen > maxLenght) {
			message = message.substring(0, message.length() - 3) + "...";
		}
		
		return message;
	}

	/** handle share response */
	private void handleShareResponse(JSONObject jsonObject) {
		APIParser parser = new APIParser(jsonObject);
		if (parser.isOK()) {
			CommonUtil.showImageShortToast(mContext.getResources().getString(R.string.share_sent), mContext);
			finish();
		} else {
			alertMessageForParser(parser);
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mApiHttp.stop();
	}
}
