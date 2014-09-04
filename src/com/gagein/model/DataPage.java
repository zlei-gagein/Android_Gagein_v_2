package com.gagein.model;

import java.io.Serializable;
import java.util.List;

public class DataPage extends DataModel implements Serializable{
	
	private static final long serialVersionUID = 1L;
	/** ---------- member vars ---------*/
	public boolean hasMore;
	public long timestamp = 0;
	public boolean chartEnabled = false;
	public List<Object>	items = null;
	public int pageIndex = 1;	// start from 1
	public Facet facet = null;
}
