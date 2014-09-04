package com.gagein.model.filter;

import java.io.Serializable;
import java.util.List;

public class Industry implements Serializable{
	
	private static final long serialVersionUID = 6654974199964986122L;

	private String key;
	
	private String id;
	
	private String name;
	
	private Boolean checked = false;
	
	private List<Industry> childrens;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Industry> getChildrens() {
		return childrens;
	}

	public void setChildrens(List<Industry> childrens) {
		this.childrens = childrens;
	}

	public Boolean getChecked() {
		return checked;
	}

	public void setChecked(Boolean checked) {
		this.checked = checked;
	}

}
