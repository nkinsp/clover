package com.github.nkinsp.clover.result;

import java.util.List;

/**
 * 
 *  分页对象
 * @param <T>
 */
public class Page<T> {

	private long count;
	
	private List<T>  data;

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

	public List<T> getData() {
		return data;
	}

	public void setData(List<T> data) {
		this.data = data;
	}

	public Page(long count, List<T> data) {
		super();
		this.count = count;
		this.data = data;
	}

	public Page() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
	
	
}
