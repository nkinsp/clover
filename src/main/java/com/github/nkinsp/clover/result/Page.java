package com.github.nkinsp.clover.result;

import java.util.List;

/**
 * 
 *  分页对象
 * @param <T>
 */
public class Page<T> {

	private long count;
	
	private List<T>  list;

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

	public List<T> getList() {
		return list;
	}

	public void setList(List<T> data) {
		this.list = data;
	}

	public Page(long count, List<T> list) {
		super();
		this.count = count;
		this.list = list;
	}

	public Page() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
	
	
}
