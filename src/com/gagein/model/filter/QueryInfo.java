package com.gagein.model.filter;

import java.util.ArrayList;
import java.util.List;

public class QueryInfo {

	private List<QueryInfoItem> NewsTriggers = new ArrayList<QueryInfoItem>();
	
	private List<QueryInfoItem> Ranks = new ArrayList<QueryInfoItem>();
	
	private List<QueryInfoItem> FiscalMonth = new ArrayList<QueryInfoItem>();
	
	private List<QueryInfoItem> MileStoneOccurrenceType = new ArrayList<QueryInfoItem>();
	
	private List<QueryInfoItem> Industries = new ArrayList<QueryInfoItem>();
	
	private List<QueryInfoItem> LocationCode = new ArrayList<QueryInfoItem>();
	
	private List<QueryInfoItem> EmployeeSize = new ArrayList<QueryInfoItem>();
	
	private List<QueryInfoItem> DateRange = new ArrayList<QueryInfoItem>();
	
	private List<QueryInfoItem> MileStoneType = new ArrayList<QueryInfoItem>();
	
	private List<QueryInfoItem> Ownership = new ArrayList<QueryInfoItem>();
	
	private List<QueryInfoItem> RevenueSize = new ArrayList<QueryInfoItem>();
	
	private List<QueryInfoItem> SavedCompany = new ArrayList<QueryInfoItem>();
	
	private QueryInfoItem EventSearchKeywords;	/// 用户自定义trigger
	
	private String queryInfoResult = "";
	
	private String companySearchKeywords = "";
	
	private String JobTitle = "";
	
	private List<QueryInfoItem> JobLevels = new ArrayList<QueryInfoItem>();
	
	private List<QueryInfoItem> FunctionalRoles = new ArrayList<QueryInfoItem>();
	
	private List<QueryInfoItem> CompaniesForCompany = new ArrayList<QueryInfoItem>();
	
	private List<QueryInfoItem> CompaniesForPeople = new ArrayList<QueryInfoItem>();
	
	private List<Location> Locations = new ArrayList<Location>();
	
	private void addItems(List<QueryInfoItem> fromList, List<QueryInfoItem> toList) {
		if (fromList != null && fromList.size() > 0 && toList != null) {
			toList.addAll(fromList);
		}
	}
	public List<QueryInfoItem> allConditions(boolean isCompanySearch) {
		List<QueryInfoItem> conditions = new ArrayList<QueryInfoItem>();
		///
		if (EventSearchKeywords != null) {
			conditions.add(EventSearchKeywords);
		} else {
			addItems(NewsTriggers, conditions);
		}
		
		///
		if (companySearchKeywords != null && !companySearchKeywords.isEmpty()) {
			QueryInfoItem queryInfoItem = new QueryInfoItem();
			queryInfoItem.setName(companySearchKeywords);
			queryInfoItem.setType("company_search_keywords");
			conditions.add(queryInfoItem);
		}
		///
		if (JobTitle != null && !JobTitle.isEmpty()) {
			QueryInfoItem queryInfoItem = new QueryInfoItem();
			queryInfoItem.setName(JobTitle);
			queryInfoItem.setType("dop_title");
			conditions.add(queryInfoItem);
		}
		///
		addItems(Ranks, conditions);
		addItems(FiscalMonth, conditions);
		addItems(MileStoneOccurrenceType, conditions);
		addItems(Industries, conditions);
		addItems(LocationCode, conditions);
		addItems(EmployeeSize, conditions);
		addItems(DateRange, conditions);
		addItems(MileStoneType, conditions);
		addItems(Ownership, conditions);		
		addItems(RevenueSize, conditions);
//		addItems(Locations, conditions);

		///
		if (isCompanySearch) {
			addItems(CompaniesForCompany, conditions);
		} else {
			addItems(JobLevels, conditions);
			addItems(FunctionalRoles, conditions);
			addItems(CompaniesForPeople, conditions);
		}
		
		return conditions;
	}
	
	public List<QueryInfoItem> getSavedCompany() {
		return SavedCompany;
	}
	
	public void setSavedCompany(List<QueryInfoItem> savedCompany) {
		SavedCompany = savedCompany;
	}
	public List<Location> getLocations() {
		return Locations;
	}
	public void setLocations(List<Location> locations) {
		Locations = locations;
	}
	public String getCompanySearchKeywords() {
		return companySearchKeywords;
	}

	public void setCompanySearchKeywords(String companySearchKeywords) {
		this.companySearchKeywords = companySearchKeywords;
	}

	public List<QueryInfoItem> getCompaniesForCompany() {
		return CompaniesForCompany;
	}

	public void setCompaniesForCompany(List<QueryInfoItem> companiesForCompany) {
		CompaniesForCompany = companiesForCompany;
	}

	public List<QueryInfoItem> getCompaniesForPeople() {
		return CompaniesForPeople;
	}

	public void setCompaniesForPeople(List<QueryInfoItem> companiesForPeople) {
		CompaniesForPeople = companiesForPeople;
	}
	
	public String getJobTitle() {
		return JobTitle;
	}

	public void setJobTitle(String jobTitle) {
		JobTitle = jobTitle;
	}

	public List<QueryInfoItem> getJobLevels() {
		return JobLevels;
	}

	public void setJobLevels(List<QueryInfoItem> jobLevels) {
		JobLevels = jobLevels;
	}

	public List<QueryInfoItem> getFunctionalRoles() {
		return FunctionalRoles;
	}

	public void setFunctionalRoles(List<QueryInfoItem> functionalRoles) {
		FunctionalRoles = functionalRoles;
	}

	public String getQueryInfoResult() {
		return queryInfoResult;
	}

	public void setQueryInfoResult(String queryInfoResult) {
		this.queryInfoResult = queryInfoResult;
	}

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
