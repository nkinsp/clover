package com.github.nkinsp.clover.query;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.github.nkinsp.clover.enums.SqlKeyword;

public class SQLBuilder {

	
	private List<String> columns = new ArrayList<>();
	
	private List<String> conditions = new ArrayList<>();
	
	
	public String buildSetect() {
		
		
//		SqlKeyword.se
		
		String cols = columns.isEmpty()?"*":columns.stream().collect(Collectors.joining(","));
		
		return SqlKeyword.SELECT_SQL.format(cols);
		
	}
	
}
