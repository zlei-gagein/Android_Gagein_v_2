package com.gagein.model.filter;

import java.util.List;

public class SearchFilter {
	
	private String filterName;
	
	private List<String> filterValues;

	public String getFilterName() {
		return filterName;
	}

	public void setFilterName(String filterName) {
		this.filterName = filterName;
	}

	public List<String> getFilterValues() {
		return filterValues;
	}

	public void setFilterValues(List<String> filterValues) {
		this.filterValues = filterValues;
	}
	

}
