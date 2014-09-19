package com.gagein.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class Contact implements Comparable<Contact> {
	
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
	
	public static class Comparators {
		public static Comparator<Contact> NAME = new Comparator<Contact>() {
			
			@Override
			public int compare(Contact lhs, Contact rhs) {
				return lhs.name.trim().toUpperCase().compareTo(rhs.name.trim().toUpperCase());
			}
		};
	}

	@Override
	public int compareTo(Contact another) {
		return Comparators.NAME.compare(this, another);
	}
	
}
