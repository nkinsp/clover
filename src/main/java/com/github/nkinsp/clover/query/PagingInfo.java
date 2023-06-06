package com.github.nkinsp.clover.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
@AllArgsConstructor
public class PagingInfo {

	private Integer pageNum = 1;
	
	private Integer pageSize = 15;
	
	
}
