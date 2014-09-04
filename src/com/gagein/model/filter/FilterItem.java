package com.gagein.model.filter;

import java.io.Serializable;
import java.util.Comparator;

public class FilterItem implements Serializable, Comparable<FilterItem>{
	
	private static final long serialVersionUID = 4369214370209798432L;

	private String key;
	
	private String value;
	
	private Boolean checked = false;
	
	private int pastDatePosition;
	
	public Boolean getChecked() {
		return checked;
	}

	public void setChecked(Boolean checked) {
		this.checked = checked;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public int getPastDatePosition() {
		return pastDatePosition;
	}

	public void setPastDatePosition(int pastDatePosition) {
		this.pastDatePosition = pastDatePosition;
	}

	@Override
	public int compareTo(FilterItem another) {
		return Comparators.NAME.compare(this, another);
	}
	
	public static class Comparators {
		public static Comparator<FilterItem> NAME = new Comparator<FilterItem>() {
			
			@Override
			public int compare(FilterItem lhs, FilterItem rhs) {
				int lhsInt = Integer.parseInt(lhs.key.trim());
				int rhsInt = Integer.parseInt(rhs.key.trim());
				int comValue = 0;
				if (lhsInt < rhsInt) {
					comValue = -1;
				} else if (lhsInt == rhsInt) {
					comValue = 0;
				} else {
					comValue = 1;
				}
				return comValue;
			}
		};
	}
	
	
}
