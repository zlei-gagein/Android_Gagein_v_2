package com.gagein.model.filter;

import java.util.List;

public class QueryInfo {

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
	
	private String queryInfoResult = "";
	
	private String companySearchKeywords = "";
	
	private String JobTitle;
	
	private List<QueryInfoItem> JobLevels;
	
	private List<QueryInfoItem> Locations;
	
	private String location;
	
	private List<QueryInfoItem> FunctionalRoles;
	
	private List<QueryInfoItem> CompaniesForCompany;
	
	private List<QueryInfoItem> CompaniesForPeople;
	
	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
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

	public List<QueryInfoItem> getLocations() {
		return Locations;
	}

	public void setLocations(List<QueryInfoItem> locations) {
		Locations = locations;
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
