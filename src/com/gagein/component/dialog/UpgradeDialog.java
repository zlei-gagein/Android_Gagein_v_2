package com.gagein.component.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.gagein.R;
import com.gagein.util.CommonUtil;
import com.gagein.util.Log;
import com.gagein.util.billing.IabHelper;
import com.gagein.util.billing.IabResult;
import com.gagein.util.billing.Inventory;
import com.gagein.util.billing.Purchase;

/**
 * 
 * @author silen
 */
public class UpgradeDialog implements OnClickListener {
	private Context mContext;
	private Dialog dialog;
	private View view;
	private LayoutInflater inflater;
	private Button yearly;
	private Button monthly;
	private Button noThanks;
	private String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAliW4mLixZFPvAw41uNgZ1kv4gHC50+EtuxzoSYNFuaY8VPBzfKYh4fxFNiFgfCOr8O6dDc6loqX+J63/d86QYNxm3unGNb3+9QepNz+tNs+2LwQ1xv8sWut/hpNotuK+689npxA4d2F2kk6BWObjPhiOIIzIObVy9N1dJd9eFisQZZLCoB7419kw/jJKv8LSeG1AjhIiuSmubsXtEytb0YsmQmrv0hcwNetm3HZPV3KWSwJidhbpRjmlrq1SiNyba7z15aaRlYRhC0L3Jjv7VdwckjTKfU1MCJx/WrgAPesL+rKS93yg6+8oE0yoCtve7uT1XNzaGE7bG2N2vI+lEQIDAQAB";
	private IabHelper mHelper;
	private static final String proMonthlyId = "com.gagein.pro.monthly"; 
	private static final String proYearlyId = "com.gagein.pro.yearly"; 
	private static final int RC_REQUEST = 10001;

	/**
	 * 
	 * @param mContext
	 */
	public UpgradeDialog(Context mContext) {
		this.mContext = mContext;
		dialog = new Dialog(mContext, R.style.dialog);
		inflater = LayoutInflater.from(mContext);
		view = inflater.inflate(R.layout.dialog_upgrade, null);
		yearly = (Button) view.findViewById(R.id.yearly);
		monthly = (Button) view.findViewById(R.id.monthly);
		noThanks = (Button) view.findViewById(R.id.noThanks);
		yearly.setOnClickListener(this);
		monthly.setOnClickListener(this);
		noThanks.setOnClickListener(this);
		dialog.setContentView(view);
	}

	public void showDialog() {
		mHelper = new IabHelper(mContext, base64EncodedPublicKey);
		
		mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                Log.v("silen", "Setup finished.");

                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    return;
                }

                // Have we been disposed of in the meantime? If so, quit.
                if (mHelper == null) return;

                // IAB is fully set up. Now, let's get an inventory of stuff we own.
                Log.v("silen", "Setup successful. Querying inventory.");
                mHelper.queryInventoryAsync(mGotInventoryListener);
            }
        });
		CommonUtil.setDialogWith(dialog);
		dialog.setCancelable(false);
		dialog.show();
	}

	@Override
	public void onClick(View v) {//TODO
		if (v == yearly) {
			launchPurchaseFlow(proYearlyId);
		} else if (v == monthly) {
			launchPurchaseFlow(proMonthlyId);
		} else if (v == noThanks) {
			dismissDialog();
		}
	}

	private void launchPurchaseFlow(String productId) {
		String payload = "";
		mHelper.launchPurchaseFlow((Activity)mContext, productId, RC_REQUEST, mPurchaseFinishedListener, payload);
	}
	
	 // Listener that's called when we finish querying the items and subscriptions we own
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            Log.v("silen", "Query inventory finished.");

            // Have we been disposed of in the meantime? If so, quit.
            if (mHelper == null) return;

            // Is it a failure?
            if (result.isFailure()) {
                return;
            }

            Log.v("silen", "Query inventory was successful.");

            /*
             * Check for items we own. Notice that for each purchase, we check
             * the developer payload to see if it's correct! See
             * verifyDeveloperPayload().
             */

//            // Do we have the premium upgrade?
//            Purchase premiumPurchase = inventory.getPurchase(proMonthlyId);
//            if (premiumPurchase != null) Log.v("silen", "premiumPurchase != null");
//            mIsPremium = (premiumPurchase != null && verifyDeveloperPayload(premiumPurchase));
//            Log.v("silen", "User is " + (mIsPremium ? "PREMIUM" : "NOT PREMIUM"));

            // Do we have the infinite gas plan?
//            Purchase infiniteGasPurchase = inventory.getPurchase(proMonthlyId);
//            mSubscribedToInfiniteGas = (infiniteGasPurchase != null &&
//                    verifyDeveloperPayload(infiniteGasPurchase));
//            Log.v("silen", "User " + (mSubscribedToInfiniteGas ? "HAS" : "DOES NOT HAVE")
//                        + " infinite gas subscription.");
//            if (mSubscribedToInfiniteGas) mTank = TANK_MAX;

            // Check for gas delivery -- if we own gas, we should fill up the tank immediately
//            Purchase gasPurchase = inventory.getPurchase(proMonthlyId);
//            if (gasPurchase != null && verifyDeveloperPayload(gasPurchase)) {
//                Log.v("silen", "We have gas. Consuming it.");
//                mHelper.consumeAsync(inventory.getPurchase(proMonthlyId), mConsumeFinishedListener);
//                return;
//            }

        }
    };
    
 // Callback for when a purchase is finished
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            Log.v("silen", "Purchase finished: " + result + ", purchase: " + purchase);

            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;

            if (result.isFailure()) {
                return;
            }
            if (!verifyDeveloperPayload(purchase)) {
                return;
            }

            Log.v("silen", "Purchase successful.");

            if (purchase.getSku().equals(proMonthlyId)) {
                // bought 1/4 tank of gas. So consume it.
                Log.v("silen", "Purchase is gas. Starting gas consumption.");
//                mHelper.consumeAsync(purchase, mConsumeFinishedListener);
            }
            else if (purchase.getSku().equals(proMonthlyId)) {
                // bought the premium upgrade!
                Log.v("silen", "Purchase is premium upgrade. Congratulating user.");
            }
            else if (purchase.getSku().equals(proMonthlyId)) {
                // bought the infinite gas subscription
                Log.v("silen", "Infinite gas subscription purchased.");
            }
        }
    };
    
    boolean verifyDeveloperPayload(Purchase p) {
        String payload = p.getDeveloperPayload();

        /*
         * TODO: verify that the developer payload of the purchase is correct. It will be
         * the same one that you sent when initiating the purchase.
         *
         * WARNING: Locally generating a random string when starting a purchase and
         * verifying it here might seem like a good approach, but this will fail in the
         * case where the user purchases an item on one device and then uses your app on
         * a different device, because on the other device you will not have access to the
         * random string you originally generated.
         *
         * So a good developer payload has these characteristics:
         *
         * 1. If two different users purchase an item, the payload is different between them,
         *    so that one user's purchase can't be replayed to another user.
         *
         * 2. The payload must be such that you can verify it even when the app wasn't the
         *    one who initiated the purchase flow (so that items purchased by the user on
         *    one device work on other devices owned by the user).
         *
         * Using your own server to store and verify developer payloads across app
         * installations is recommended.
         */

        return true;
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

	public void setCancelable(boolean is) {
		dialog.setCancelable(is);
	}

}
