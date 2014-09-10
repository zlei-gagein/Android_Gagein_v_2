package com.gagein.component.dialog;

import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.gagein.R;
import com.gagein.http.APIHttp;
import com.gagein.http.APIHttpMetadata;
import com.gagein.http.APIParser;
import com.gagein.model.Update;
import com.gagein.ui.main.EditShareActivity;
import com.gagein.util.ApplicationUrl;
import com.gagein.util.CommonUtil;
import com.gagein.util.Constant;
import com.gagein.util.Log;
import com.gagein.util.MessageCode;
import com.gagein.util.RuntimeData;
import com.gagein.util.oauth.OAuthHandler;
import com.gagein.util.oauth.OAuther;

/**
 * 
 * @author silen
 */
@SuppressWarnings("deprecation")
public class ShareDialog implements OnClickListener{
	private Context mContext;
	private Dialog dialog;
	private View view;
	private LayoutInflater inflater;
	private ImageButton message;
	private ImageButton mail;
	private ImageButton facebook;
	private ImageButton twitter;
	private ImageButton salesforce;
	private ImageButton addBookmark;
	private ImageButton coopLink;
	private ImageButton like;
	private ImageButton irrelevant;
	private TextView bookmarkStr;
	private TextView likeStr;
	private TextView flag;
	private Button cancel;
	@SuppressWarnings({ })
	private ClipboardManager clip;
	private APIHttp mApiHttp;
	private Update mUpdate;
	public static final String INDENT_EXTRA_DATA_KEY = "INDENT_BUNDLE_DATA_KEY";
	private OAuther oauther;
	private OAuthHandler authHandler;
	private BroadcastReceiver authReceiver;
	private LinearLayout messageLayout;

	public ShareDialog(Context context) {
		dialog = new Dialog(context, R.style.dialog);
		this.mContext = context;
		mApiHttp = new APIHttp(mContext);
		inflater = LayoutInflater.from(context);
		view = inflater.inflate(R.layout.dialog_share, null);
		cancel = (Button) view.findViewById(R.id.cancel);
		message = (ImageButton) view.findViewById(R.id.message);
		messageLayout = (LinearLayout) view.findViewById(R.id.messageLayout);
		mail = (ImageButton) view.findViewById(R.id.mail);
		facebook = (ImageButton) view.findViewById(R.id.facebook);
		twitter = (ImageButton) view.findViewById(R.id.twitter);
		salesforce = (ImageButton) view.findViewById(R.id.salesforce);
		addBookmark = (ImageButton) view.findViewById(R.id.addBookmark);
		coopLink = (ImageButton) view.findViewById(R.id.coopLink);
		like = (ImageButton) view.findViewById(R.id.like);
		irrelevant = (ImageButton) view.findViewById(R.id.irrelevant);
		bookmarkStr = (TextView) view.findViewById(R.id.bookmarkStr);
		likeStr = (TextView) view.findViewById(R.id.likeStr);
		flag = (TextView) view.findViewById(R.id.flag);
		messageLayout.setVisibility(CommonUtil.isTablet(mContext) ? View.GONE : View.VISIBLE);
		dialog.setContentView(view);
		setOnClickListener();
	}

	public void showDialog() {
		dialog.show();
	}
	
	public void showDialog(Update update) {
		this.mUpdate = update;
		initData();
		setSaveBackground();
		setLikeBackground();
		setIrrelevantBackground();
		dialog.show();
	}
	
	private void initData() {
		oauther = new OAuther(mContext);
		authHandler = new OAuthHandler(mContext, "");
		authReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				final OAuthHandler.OAuthHandlerData data = (OAuthHandler.OAuthHandlerData) intent.getSerializableExtra(OAuthHandler.KEY_INTENT_DATA);
				if (data != null && data.snName.equalsIgnoreCase(authHandler.data.snName) && !TextUtils.isEmpty(data.accessToken)) {
					Log.v("silen", "authReceiver");
					// means message belongs to me
					final int snType = CommonUtil.typeFromSnName(data.snName);
					CommonUtil.showLoadingDialog(mContext);
					switch (snType) {
					case APIHttpMetadata.kGGSnTypeLinkedIn: {
						
						mApiHttp.snSaveLinkedIn(data.accessToken, data.secret,
								new Listener<JSONObject>() {

									@Override
									public void onResponse(JSONObject jsonObject) {
										handleSnSaved(data, snType, jsonObject);
										CommonUtil.dissmissLoadingDialog();
									}

								}, new Response.ErrorListener() {

									@Override
									public void onErrorResponse(VolleyError error) {
										showConnectionError();
									}
								});
					}
						break;

					case APIHttpMetadata.kGGSnTypeSalesforce: {
						
						Log.v("silen", "kGGSnTypeSalesforce");
						mApiHttp.snSaveSalesforce(data.accessToken,
								data.openId, data.refreshToken,
								data.instanceUrl, new Listener<JSONObject>() {

									@Override
									public void onResponse(JSONObject jsonObject) {
										handleSnSaved(data, snType, jsonObject);
										CommonUtil.dissmissLoadingDialog();
									}

								}, new Response.ErrorListener() {

									@Override
									public void onErrorResponse(VolleyError error) {
										showConnectionError();
									}
								});
					}
						break;

					case APIHttpMetadata.kGGSnTypeFacebook: {
						
						mApiHttp.snSaveFacebook(data.accessToken, new Listener<JSONObject>() {

									@Override
									public void onResponse(JSONObject jsonObject) {
										handleSnSaved(data, snType, jsonObject);
										CommonUtil.dissmissLoadingDialog();
									}

								}, new Response.ErrorListener() {

									@Override
									public void onErrorResponse(VolleyError error) {
										showConnectionError();
									}
								});
					}
						break;

					case APIHttpMetadata.kGGSnTypeTwitter: {
						
						mApiHttp.snSaveTwitter(data.accessToken, data.secret, new Listener<JSONObject>() {

									@Override
									public void onResponse(JSONObject jsonObject) {
										handleSnSaved(data, snType, jsonObject);
										CommonUtil.dissmissLoadingDialog();
									}

								}, new Response.ErrorListener() {

									@Override
									public void onErrorResponse(VolleyError error) {
										showConnectionError();
									}
								});
					}
						break;
					}
				}
			}

			/** handle after sn saved to back-end */
			private void handleSnSaved(final OAuthHandler.OAuthHandlerData data, final int snType, JSONObject jsonObject) {
				
				APIParser parser = new APIParser(jsonObject);
				if (parser.isOK()) {
					CommonUtil.addSnType(snType);
					shareWithSnType(snType);
				} else {
					alertMessageForParser(parser);
				}
			}
		};
		mContext.registerReceiver(authReceiver, new IntentFilter(OAuthHandler.KEY_INTENT_ACTION_SUCCESS));
	}
	
	protected final void alertMessageForParser(APIParser parser) {
		CommonUtil.alertMessageForParser(parser);
	}
	
	protected void showConnectionError() {
		CommonUtil.dissmissLoadingDialog();
		CommonUtil.showLongToast(mContext.getResources().getString(R.string.connection_error));
	}
	
	public void setOnClickListener() {
		cancel.setOnClickListener(this);
		message.setOnClickListener(this);
		mail.setOnClickListener(this);
		facebook.setOnClickListener(this);
		twitter.setOnClickListener(this);
		salesforce.setOnClickListener(this);
		addBookmark.setOnClickListener(this);
		coopLink.setOnClickListener(this);
		like.setOnClickListener(this);
		irrelevant.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		if (v == cancel) {
			dismissDialog();
		} else if (v == message) {
			String format = "";
			if (APIHttp.serverURL.equals(ApplicationUrl.ProductionURL)) {
				format = "%s\n\n%s\n\nvia Gagein at www.gagein.com";
			} else {
				format = "%s\n\n%s\n\nvia Gagein at gageincn.dyndns.org:3031";
			}
			
			String info = String.format(format, mUpdate.headline, mUpdate.displayURL());
			CommonUtil.sendSms(info, (Activity) mContext);
			dismissDialog();
		} else if (v == mail) {
			if (null == mUpdate)
				return;
			String contentFormat = "";
			if (APIHttp.serverURL.equals(ApplicationUrl.ProductionURL)) {
				contentFormat = "<div><p>I want to share this update with you.</p>"
						+ "<p><a href=\"%s\">%s</a></p>"
						+ "<p><strong>%s</strong></p>"
						+ "<p><em>%s</em></p>"
						+ "Shared from <a href=\"www.gagein.com\">Gagein</a>, %s </div>";
			} else {
				contentFormat = "<div><p>I want to share this update with you.</p>"
						+ "<p><a href=\"%s\">%s</a></p>"
						+ "<p><strong>%s</strong></p>"
						+ "<p><em>%s</em></p>"
						+ "Shared from <a href=\"gageincn.dyndns.org:3031\">Gagein</a>, %s </div>";
			}
			String content = String.format(contentFormat, mUpdate.displayURL(), mUpdate.displayURL(),
					mUpdate.headline, mUpdate.contentInDetail, Constant.GAGEIN_SLOGAN);
			
			CommonUtil.sendEmail(mUpdate.headline, content, mContext);
			dismissDialog();
		} else if (v == facebook) {
			dismissDialog();
			shareWithSnType(APIHttpMetadata.kGGSnTypeFacebook);
		} else if (v == twitter) {
			dismissDialog();
			shareWithSnType(APIHttpMetadata.kGGSnTypeTwitter);
		} else if (v == salesforce) {
			dismissDialog();
			shareWithSnType(APIHttpMetadata.kGGSnTypeSalesforce);
		} else if (v == addBookmark) {
			dismissDialog();
			if (mUpdate.saved) {
				setUnSave();
			} else {
				setSave();
			}
		} else if (v == coopLink) {
			dismissDialog();
			clip = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
			clip.setText(mUpdate.url);
//			CommonUtil.showShortToast(mContext.getResources().getString(R.string.link_copied));
		} else if (v == like) {
			dismissDialog();
			if (mUpdate.liked) {
				setUnLike();
			} else {
				setLike();
			}
		} else if (v == irrelevant) {
			setIrrelevant(!mUpdate.irrelevant);
		}
	}
	
	private void setUnLike() {
		CommonUtil.showLoadingDialog(mContext);
		mApiHttp.unlikeUpdate(mUpdate.newsId, new Listener<JSONObject>() {
	
			@Override
			public void onResponse(JSONObject jsonObject) {
	
				APIParser parser = new APIParser(jsonObject);
				if (parser.isOK()) {
					Intent intent = new Intent();
					intent.putExtra(Constant.UPDATEID, mUpdate.newsId);
					intent.setAction(Constant.BROADCAST_SET_NEWS_UNLIKE);
					mContext.sendBroadcast(intent);
					
					mUpdate.liked = false;
					setLikeBackground();
					
//					CommonUtil.showImageShortToast(mContext.getResources().getString(R.string.unlike), mContext);
					
				} else {
					String msg = MessageCode.messageForCode(parser.messageCode());
					if (msg != null && msg.length() > 0) {
						CommonUtil.showDialog(msg);
					}
				}
				CommonUtil.dissmissLoadingDialog();
			}
	
		}, new Response.ErrorListener() {
	
			@Override
			public void onErrorResponse(VolleyError error) {
				CommonUtil.dissmissLoadingDialog();
				CommonUtil.showLongToast(mContext.getResources().getString(R.string.connection_error));
			}
		});
	}

	private void setLike() {
		CommonUtil.showLoadingDialog(mContext);
		mApiHttp.likeUpdate(mUpdate.newsId, new Listener<JSONObject>() {
	
			@Override
			public void onResponse(JSONObject jsonObject) {
				APIParser parser = new APIParser(jsonObject);
				if (parser.isOK()) {
					Intent intent = new Intent();
					intent.putExtra(Constant.UPDATEID, mUpdate.newsId);
					intent.setAction(Constant.BROADCAST_SET_NEWS_LIKED);
					mContext.sendBroadcast(intent);
					
					mUpdate.liked = true;
					setLikeBackground();
					
					if (null != parser.data()) {
						
						String msgStatus = parser.data().optString("message_status");
						Log.v("silen", "msgStatus = " + msgStatus);
						if (msgStatus.equals(APIHttpMetadata.LIKED_CLOSER)) {
							LikedTriggerchartDialog closerDialog = new LikedTriggerchartDialog(mContext, false);
							closerDialog.showDialog();
						} else if (msgStatus.equals(APIHttpMetadata.LIKED_ENABLE)){
							LikedTriggerchartDialog closerDialog = new LikedTriggerchartDialog(mContext, true);
							closerDialog.showDialog();
						}
						CommonUtil.dissmissLoadingDialog();
						return;
						
					}
					
//					CommonUtil.showImageShortToast(mContext.getResources().getString(R.string.liked), mContext);
					
				} else {
					String msg = MessageCode.messageForCode(parser.messageCode());
					if (msg != null && msg.length() > 0) {
						CommonUtil.showDialog(msg);
					}
				}
				CommonUtil.dissmissLoadingDialog();
			}
	
		}, new Response.ErrorListener() {
	
			@Override
			public void onErrorResponse(VolleyError error) {
				CommonUtil.dissmissLoadingDialog();
				CommonUtil.showLongToast(mContext.getResources().getString(R.string.connection_error));
			}
		});
	}
	
	/** share the app */
	private void shareWithSnType(final int aSnType) {
		if (CommonUtil.hasLinkedSnType(aSnType)) {

			RuntimeData.updateForShare = mUpdate;
			Intent intent = new Intent(mContext, EditShareActivity.class);
			intent.putExtra(INDENT_EXTRA_DATA_KEY, aSnType);
			intent.putExtra(EditShareActivity.INTENT_KEY_SHARE_TYPE, EditShareActivity.SHARE_UPDATE);
			mContext.startActivity(intent);

		} else {
			switch (aSnType) {
			case APIHttpMetadata.kGGSnTypeSalesforce:
				oauther.authSalesforce(authHandler);
				break;

			case APIHttpMetadata.kGGSnTypeLinkedIn: 
				oauther.authLinkedIn(authHandler);
				break;

			case APIHttpMetadata.kGGSnTypeFacebook:
				oauther.authFacebook(authHandler);
				break;

			case APIHttpMetadata.kGGSnTypeTwitter:
				oauther.authTwitter(authHandler);
				break;

			default:
			}
		}
	}
	
	private void setLikeBackground() {
		like.setBackgroundResource(mUpdate.liked ? R.drawable.share_liked : R.drawable.share_like);
		likeStr.setText(mContext.getResources().getString(mUpdate.liked ? R.string.unlike : R.string.like));
	}
	
	private void setIrrelevantBackground() {
		irrelevant.setBackgroundResource(mUpdate.irrelevant ? R.drawable.share_flaged : R.drawable.share_flag);
		flag.setText(mContext.getResources().getString(mUpdate.irrelevant ? R.string.flagged_as_irrelevant : R.string.flag_as_irrelevant));
	}
	
	private void setSaveBackground() {
		addBookmark.setBackgroundResource(mUpdate.saved ? R.drawable.share_bookmarked : R.drawable.share_bookmark);
		bookmarkStr.setText(mContext.getResources().getString(mUpdate.saved ? R.string.removeBookmark : R.string.addBookmark));
	}
	
	private void setIrrelevant(Boolean flag) {
		CommonUtil.showLoadingDialog(mContext);
		mApiHttp.setNewsToIrrelevant(mUpdate.newsId, flag, new Listener<JSONObject>() {
			
			@Override
			public void onResponse(JSONObject jsonObject) {
				
				APIParser parser = new APIParser(jsonObject);
				if (parser.isOK()) {
					
					mUpdate.irrelevant = !mUpdate.irrelevant;
					setIrrelevantBackground();
//					CommonUtil.showImageShortToast(mContext.getResources().getString(R.string.add_bookmark), mContext);
				} else {
					CommonUtil.alertMessageForParser(parser);
				}
				CommonUtil.dissmissLoadingDialog();
			}
			
		}, new Response.ErrorListener() {
			
			@Override
			public void onErrorResponse(VolleyError error) {
				CommonUtil.dissmissLoadingDialog();
				CommonUtil.showLongToast(mContext.getResources().getString(R.string.connection_error));
			}
		});
	}
	
	private void setSave() {
		CommonUtil.showLoadingDialog(mContext);
		mApiHttp.saveUpdate(mUpdate.newsId, new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject jsonObject) {

				APIParser parser = new APIParser(jsonObject);
				if (parser.isOK()) {
					mUpdate.saved = true;
					setSaveBackground();
					
					Intent intent = new Intent();
					intent.setAction(Constant.BROADCAST_REMOVE_BOOKMARKS);
					mContext.sendBroadcast(intent);
//					CommonUtil.showImageShortToast(mContext.getResources().getString(R.string.add_bookmark), mContext);
				} else {
					CommonUtil.alertMessageForParser(parser);
				}
				CommonUtil.dissmissLoadingDialog();
			}

		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				CommonUtil.dissmissLoadingDialog();
				CommonUtil.showLongToast(mContext.getResources().getString(R.string.connection_error));
			}
		});
	}
	
	private void setUnSave() {
		CommonUtil.showLoadingDialog(mContext);
		mApiHttp.unsaveUpdate(mUpdate.newsId, new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject jsonObject) {

				APIParser parser = new APIParser(jsonObject);
				if (parser.isOK()) {
					mUpdate.saved = false;
					setSaveBackground();
					
					Intent intent = new Intent();
					intent.setAction(Constant.BROADCAST_ADD_BOOKMARKS);
					mContext.sendBroadcast(intent);
//					CommonUtil.showImageShortToast(mContext.getResources().getString(R.string.remove_bookmark), mContext);
				} else {
					CommonUtil.alertMessageForParser(parser);
				}
				CommonUtil.dissmissLoadingDialog();
			}

		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				CommonUtil.dissmissLoadingDialog();
				CommonUtil.showLongToast(mContext.getResources().getString(R.string.connection_error));
			}
		});
	}

	public void dismissDialog() {
		mContext.unregisterReceiver(authReceiver);
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
