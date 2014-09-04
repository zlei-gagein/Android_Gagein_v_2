package com.gagein.model;

import java.util.ArrayList;
import java.util.List;


public class Contact {
	
	private String name;
	
	private List<String> emails = new ArrayList<String>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getEmails() {
		return emails;
	}

	public void setEmails(List<String> emails) {
		this.emails = emails;
	}
	
}
