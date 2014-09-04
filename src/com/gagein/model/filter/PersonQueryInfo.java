package com.gagein.model.filter;

import java.util.List;

public class PersonQueryInfo {

	private List<QueryInfoItem> JobTitles;//TODO
	
	private List<QueryInfoItem> JobLevels;
	
	private List<QueryInfoItem> Locations;
	
	private List<QueryInfoItem> FunctionalRoles;
	
	private List<QueryInfoItem> NewsTriggers;
	
	private List<QueryInfoItem> Ranks;
	
	private List<QueryInfoItem> FiscalMonth;
	
	private List<QueryInfoItem> MileStoneOccurrenceType;
	
	private List<QueryInfoItem> Industries;
	
	private List<QueryInfoItem> LocationCode;
	
	private List<QueryInfoItem> EmployeeSize;
	
	private List<QueryInfoItem> DateRange;
	
	private List<QueryInfoItem> MileStoneType;
	
	private List<QueryInfoItem> Ownership;
	
	private List<QueryInfoItem> RevenueSize;
	
	private QueryInfoItem EventSearchKeywords;
	
	public List<QueryInfoItem> getNewsTriggers() {
		return NewsTriggers;
	}

	public void setNewsTriggers(List<QueryInfoItem> newsTriggers) {
		NewsTriggers = newsTriggers;
	}

	public List<QueryInfoItem> getRanks() {
		return Ranks;
	}

	public void setRanks(List<QueryInfoItem> ranks) {
		Ranks = ranks;
	}

	public List<QueryInfoItem> getFiscalMonth() {
		return FiscalMonth;
	}

	public void setFiscalMonth(List<QueryInfoItem> fiscalMonth) {
		FiscalMonth = fiscalMonth;
	}

	public List<QueryInfoItem> getMileStoneOccurrenceType() {
		return MileStoneOccurrenceType;
	}

	public void setMileStoneOccurrenceType(
			List<QueryInfoItem> mileStoneOccurrenceType) {
		MileStoneOccurrenceType = mileStoneOccurrenceType;
	}

	public List<QueryInfoItem> getIndustries() {
		return Industries;
	}

	public void setIndustries(List<QueryInfoItem> industries) {
		Industries = industries;
	}

	public List<QueryInfoItem> getLocationCode() {
		return LocationCode;
	}

	public void setLocationCode(List<QueryInfoItem> locationCode) {
		LocationCode = locationCode;
	}

	public List<QueryInfoItem> getEmployeeSize() {
		return EmployeeSize;
	}

	public void setEmployeeSize(List<QueryInfoItem> employeeSize) {
		EmployeeSize = employeeSize;
	}

	public List<QueryInfoItem> getDateRange() {
		return DateRange;
	}

	public void setDateRange(List<QueryInfoItem> dateRange) {
		DateRange = dateRange;
	}

	public List<QueryInfoItem> getMileStoneType() {
		return MileStoneType;
	}

	public void setMileStoneType(List<QueryInfoItem> mileStoneType) {
		MileStoneType = mileStoneType;
	}

	public List<QueryInfoItem> getOwnership() {
		return Ownership;
	}

	public void setOwnership(List<QueryInfoItem> ownership) {
		Ownership = ownership;
	}

	public List<QueryInfoItem> getRevenueSize() {
		return RevenueSize;
	}

	public void setRevenueSize(List<QueryInfoItem> revenueSize) {
		RevenueSize = revenueSize;
	}

	public QueryInfoItem getEventSearchKeywords() {
		return EventSearchKeywords;
	}

	public void setEventSearchKeywords(QueryInfoItem eventSearchKeywords) {
		EventSearchKeywords = eventSearchKeywords;
	}

}
