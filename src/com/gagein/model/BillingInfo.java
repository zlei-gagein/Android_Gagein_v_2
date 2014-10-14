package com.gagein.model;

public class BillingInfo {
	
	private String name;
	
	private Boolean expired;
	
	private int trialDaysLeft;
	
	private Boolean isOwner;
	
	private Boolean inTrial;
	
	private Boolean isAssigned;
	
	private Boolean isTeam;
	
	private Boolean canUpgrade;
	
	private Boolean canCancel;
	
	private float balanceDueAmmount;
	
	private String peroid;
	
	private long lastPayDate;
	
	private long endDate;
	
	private String endDateString;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getExpired() {
		return expired;
	}

	public void setExpired(Boolean expired) {
		this.expired = expired;
	}

	public int getTrialDaysLeft() {
		return trialDaysLeft;
	}

	public void setTrialDaysLeft(int trialDaysLeft) {
		this.trialDaysLeft = trialDaysLeft;
	}

	public Boolean getIsOwner() {
		return isOwner;
	}

	public void setIsOwner(Boolean isOwner) {
		this.isOwner = isOwner;
	}

	public Boolean getInTrial() {
		return inTrial;
	}

	public void setInTrial(Boolean inTrial) {
		this.inTrial = inTrial;
	}

	public Boolean getIsAssigned() {
		return isAssigned;
	}

	public void setIsAssigned(Boolean isAssigned) {
		this.isAssigned = isAssigned;
	}

	public Boolean getIsTeam() {
		return isTeam;
	}

	public void setIsTeam(Boolean isTeam) {
		this.isTeam = isTeam;
	}

	public Boolean getCanUpgrade() {
		return canUpgrade;
	}

	public void setCanUpgrade(Boolean canUpgrade) {
		this.canUpgrade = canUpgrade;
	}

	public Boolean getCanCancel() {
		return canCancel;
	}

	public void setCanCancel(Boolean canCancel) {
		this.canCancel = canCancel;
	}

	public float getBalanceDueAmmount() {
		return balanceDueAmmount;
	}

	public void setBalanceDueAmmount(float balanceDueAmmount) {
		this.balanceDueAmmount = balanceDueAmmount;
	}

	public String getPeroid() {
		return peroid;
	}

	public void setPeroid(String peroid) {
		this.peroid = peroid;
	}

	public long getLastPayDate() {
		return lastPayDate;
	}

	public void setLastPayDate(long lastPayDate) {
		this.lastPayDate = lastPayDate;
	}

	public long getEndDate() {
		return endDate;
	}

	public void setEndDate(long endDate) {
		this.endDate = endDate;
	}

	public String getEndDateString() {
		return endDateString;
	}

	public void setEndDateString(String endDateString) {
		this.endDateString = endDateString;
	}
	
}
