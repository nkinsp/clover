package com.github.nkinsp.clover.query;



public class PagingInfo {

	private Integer pageNum;
	
	private Integer pageSize;

	public PagingInfo(Integer pageNum, Integer pageSize) {
		super();
		this.pageNum = pageNum ;
		this.pageSize = pageSize;
	}

	public Integer getPageNum() {
		
		if(pageNum == null || pageNum < 1) {
			return 1;
		}
		
		
		
		return pageNum;
	}

	public void setPageNum(Integer pageNum) {
		this.pageNum = pageNum;
	}

	public Integer getPageSize() {
		
		if(pageSize == null) {
			return 15;
		}
		
		if(pageSize > 10000) {
			throw new RuntimeException("pageSize Max value 10000");
		}
		
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
	
	
}
