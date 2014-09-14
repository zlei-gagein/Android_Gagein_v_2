package com.gagein.model.filter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Filters implements Serializable{
	
	private static final long serialVersionUID = 2772820905130705248L;

	private List<FilterItem> Ownerships = new ArrayList<FilterItem>();
	
	private List<FilterItem> PeopleTypesFromPeople = new ArrayList<FilterItem>();
	
	private List<FilterItem> SortByFromBuz = new ArrayList<FilterItem>();
	
	private List<FilterItem> SortByFromContact = new ArrayList<FilterItem>();
	
	private List<FilterItem> CompanyTypesFromPeople = new ArrayList<FilterItem>();
	
	private List<FilterItem> CompanyTypesFromCompany = new ArrayList<FilterItem>();
	
	private List<FilterItem> EmployeeSizeFromBuz = new ArrayList<FilterItem>();
	
	private List<FilterItem> Occurreds = new ArrayList<FilterItem>();

	private List<FilterItem> JobLevel = new ArrayList<FilterItem>();
	
	private List<FilterItem> FunctionalRoles = new ArrayList<FilterItem>();
	
	private List<FilterItem> SalesVolumeFromBuz = new ArrayList<FilterItem>();
	
	private List<FilterItem> MileStones = new ArrayList<FilterItem>();
	
	private List<FilterItem> FiscalYearEndMonths = new ArrayList<FilterItem>();
	
	private List<FilterItem> NewsTriggers = new ArrayList<FilterItem>();
	
	private List<FilterItem> Ranks = new ArrayList<FilterItem>();
	
	private List<FilterItem> mileStoneDateRange = new ArrayList<FilterItem>();
	
	private List<FilterItem> DateRanges = new ArrayList<FilterItem>();
	
	private List<Industry> industries = new ArrayList<Industry>();
	
	private List<Location> headquarters = new ArrayList<Location>();
	
	private List<Location> locations = new ArrayList<Location>();
	
	private List<JobTitle> jobTitles = new ArrayList<JobTitle>();
	
	public List<FilterItem> getOwnerships() {
		return Ownerships;
	}

	public void setOwnerships(List<FilterItem> ownerships) {
		Ownerships = ownerships;
	}

	public List<FilterItem> getPeopleTypesFromPeople() {
		return PeopleTypesFromPeople;
	}

	public void setPeopleTypesFromPeople(List<FilterItem> peopleTypesFromPeople) {
		PeopleTypesFromPeople = peopleTypesFromPeople;
	}

	public List<FilterItem> getSortByFromBuz() {
		return SortByFromBuz;
	}

	public void setSortByFromBuz(List<FilterItem> sortByFromBuz) {
		SortByFromBuz = sortByFromBuz;
	}

	public List<FilterItem> getSortByFromContact() {
		return SortByFromContact;
	}

	public void setSortByFromContact(List<FilterItem> sortByFromContact) {
		SortByFromContact = sortByFromContact;
	}

	public List<FilterItem> getCompanyTypesFromPeople() {
		return CompanyTypesFromPeople;
	}

	public void setCompanyTypesFromPeople(List<FilterItem> companyTypesFromPeople) {
		CompanyTypesFromPeople = companyTypesFromPeople;
	}

	public List<FilterItem> getCompanyTypesFromCompany() {
		return CompanyTypesFromCompany;
	}

	public void setCompanyTypesFromCompany(List<FilterItem> companyTypesFromCompany) {
		CompanyTypesFromCompany = companyTypesFromCompany;
	}

	public List<FilterItem> getEmployeeSizeFromBuz() {
		return EmployeeSizeFromBuz;
	}

	public void setEmployeeSizeFromBuz(List<FilterItem> employeeSizeFromBuz) {
		EmployeeSizeFromBuz = employeeSizeFromBuz;
	}

	public List<FilterItem> getOccurreds() {
		return Occurreds;
	}

	public void setOccurreds(List<FilterItem> occurreds) {
		Occurreds = occurreds;
	}

	public List<FilterItem> getJobLevel() {
		return JobLevel;
	}

	public void setJobLevel(List<FilterItem> jobLevel) {
		JobLevel = jobLevel;
	}

	public List<FilterItem> getSalesVolumeFromBuz() {
		return SalesVolumeFromBuz;
	}

	public void setSalesVolumeFromBuz(List<FilterItem> salesVolumeFromBuz) {
		SalesVolumeFromBuz = salesVolumeFromBuz;
	}

	public List<FilterItem> getMileStones() {
		return MileStones;
	}

	public void setMileStones(List<FilterItem> mileStones) {
		MileStones = mileStones;
	}

	public List<FilterItem> getFiscalYearEndMonths() {
		return FiscalYearEndMonths;
	}

	public void setFiscalYearEndMonths(List<FilterItem> fiscalYearEndMonths) {
		FiscalYearEndMonths = fiscalYearEndMonths;
	}

	public List<FilterItem> getNewsTriggers() {
		return NewsTriggers;
	}

	public void setNewsTriggers(List<FilterItem> newsTriggers) {
		NewsTriggers = newsTriggers;
	}

	public List<FilterItem> getRanks() {
		return Ranks;
	}

	public void setRanks(List<FilterItem> ranks) {
		Ranks = ranks;
	}

	

	public List<Industry> getIndustries() {
		return industries;
	}

	public void setIndustries(List<Industry> industries) {
		this.industries = industries;
	}

	public List<Location> getHeadquarters() {
		return headquarters;
	}

	public void setHeadquarters(List<Location> headquarters) {
		this.headquarters = headquarters;
	}

	public List<FilterItem> getFunctionalRoles() {
		return FunctionalRoles;
	}

	public void setFunctionalRoles(List<FilterItem> functionalRoles) {
		FunctionalRoles = functionalRoles;
	}

	public List<FilterItem> getMileStoneDateRange() {
		return mileStoneDateRange;
	}

	public void setMileStoneDateRange(List<FilterItem> mileStoneDateRange) {
		this.mileStoneDateRange = mileStoneDateRange;
	}

	public List<JobTitle> getJobTitles() {
		return jobTitles;
	}

	public void setJobTitles(List<JobTitle> jobTitles) {
		this.jobTitles = jobTitles;
	}

	public List<Location> getLocations() {
		return locations;
	}

	public void setLocations(List<Location> locations) {
		this.locations = locations;
	}

	public List<FilterItem> getDateRanges() {
		return DateRanges;
	}

	public void setDateRanges(List<FilterItem> dateRanges) {
		DateRanges = dateRanges;
	}
	
}
