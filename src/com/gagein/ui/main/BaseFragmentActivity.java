package com.gagein.ui.main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.gagein.util.ConfigurableReceiver;
import com.gagein.util.ConfigurableReceiver.OnReceiveListener;

public class BaseFragmentActivity extends FragmentActivity implements OnReceiveListener {
	
	protected Context mContext;
	protected ConfigurableReceiver defaultReceiver;		// default broadcast receiver
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		mContext = this;
		
		defaultReceiver = new ConfigurableReceiver(observeNotifications(), this);
		defaultReceiver.register(mContext);
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
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		defaultReceiver.unregister();
	}

}
