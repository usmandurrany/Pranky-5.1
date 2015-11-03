package com.fournodes.ud.pranky;

public class Category {

	int id;
	int resid;
	
	public Category(int id, int resid) {
		super();
		this.id = id;
		this.resid = resid;
	}
	
	public Category() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Integer getResid() {
		return resid;
	}

	public void setResid(Integer resid) {
		this.resid = resid;
	}
}
