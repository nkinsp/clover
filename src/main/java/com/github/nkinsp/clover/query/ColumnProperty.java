package com.github.nkinsp.clover.query;

import com.github.nkinsp.clover.enums.SqlKeyword;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ColumnProperty {

	private String column;

	private SqlKeyword sqlKeyword;
	
	private Object value;
}
