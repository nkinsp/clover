package com.github.nkinsp.clover.table;

import com.github.nkinsp.clover.enums.JoinType;

import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
public class CascadeInfo {

	private JoinType joinType;
	
	private Class<?> joinTable;
	
	private String joinColumn;
	
	private String inverseColumn;
	
	private String middleTable;
	
}
