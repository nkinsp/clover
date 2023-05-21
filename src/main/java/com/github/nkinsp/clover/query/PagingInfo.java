package com.github.nkinsp.clover.query;

import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
public class PagingInfo {

	private Long pageNum = 1L;
	
	private Long pageSize = 15L;
	
	
	private PagingInfo(Long pageNum,Long pageSize) {
		
		this.pageNum = pageNum;
		this.pageSize = pageSize;
		
	}
	
	public void setPaging(Long pageNum,Long pageSize) {
		this.pageNum = pageNum;
		this.pageSize = pageSize;
	}
	
	
	public static PagingInfo of(Long pageNum,Long pageSize) {
		
		return new PagingInfo(pageNum, pageSize);
	}
	
}
