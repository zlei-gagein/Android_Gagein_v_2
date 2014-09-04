package com.gagein.util;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class ConfigurableReceiver extends BroadcastReceiver {

	private List<String> actions;
	private OnReceiveListener listenter;
	
	private boolean registered = false;
	private Context registeredContext;
	
	
	
	public ConfigurableReceiver(List<String> aActions, OnReceiveListener aListener) {
		super();
		actions = aActions;
		listenter = aListener;
	}
	
	
	/** register */
	public void register(Context aContext) {
		if (aContext != null && actions != null && actions.size() > 0) {
			
			IntentFilter filter = new IntentFilter();
			for (String action : actions) {
				filter.addAction(action);
			}
			
			aContext.registerReceiver(this, filter);
			registeredContext = aContext;
			registered = true;
		}
	}
	
	
	/** unregister */
	public void unregister() {
		if (registered && registeredContext != null) {
			registeredContext.unregisterReceiver(this);
			registered = false;
		}
	}

	@Override
	public void onReceive(Context arg0, Intent arg1) {
		
		if (listenter != null) {
			listenter.handleNotifications(arg0, arg1);
		}

	}

	
	/** interface to listen to on receive */
	public interface OnReceiveListener {
		public void handleNotifications(Context aContext, Intent anIntent);
	}
}
