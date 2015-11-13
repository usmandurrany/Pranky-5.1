package com.fournodes.ud.pranky;

public class GridItems {

	public int id;
	public Integer res;
	public String sound;
	
	public GridItems(int id, Integer res) {
		this.id = id;
		this.res = res;
	}

	public GridItems(int id, Integer res,String sound) {
		this.id = id;
		this.res = res;
		this.sound = sound;
	}
}
