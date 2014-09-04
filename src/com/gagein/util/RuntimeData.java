package com.gagein.util;

import java.util.ArrayList;
import java.util.List;

import com.gagein.model.Happening;
import com.gagein.model.Person;
import com.gagein.model.Update;

public class RuntimeData {
	
	// public member
	private List<String> snTypes = new ArrayList<String>();		// social netwrok types
	
	public List<String> getSnTypes() {
		return snTypes;
	}
	
	public static Update updateForShare;
	public static Happening happeningForShare;
	public static Person personForLinkedInAction;

	// singleton
	private static RuntimeData instance;
	
	public static RuntimeData sharedInstance() {
		if (instance ==null) {
			instance = new RuntimeData();
		}
		
		return instance;
	}
	
	private RuntimeData() {
		
	}
}
