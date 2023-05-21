package com.github.nkinsp.clover.query;

import java.util.ArrayList;
import java.util.List;

import com.github.nkinsp.clover.enums.SqlKeyword;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Criteria {


	
	private List<ColumnProperty> columnProperties = new ArrayList<ColumnProperty>();
	
	
	public Criteria add(ColumnProperty property) {
		this.columnProperties.add(property);
		return this;
	}
	
	public  Criteria eq(String column,Object value) {
		return add(new ColumnProperty(column, SqlKeyword.EQ, value));
	}
	
	public  Criteria ge(String column,Object value) {
		return add(new ColumnProperty(column, SqlKeyword.EQ, value));
	}
	
	public  Criteria gt(String column,Object value) {
		return add(new ColumnProperty(column, SqlKeyword.EQ, value));
	}
	
	public  Criteria lt(String column,Object value) {
		return add(new ColumnProperty(column, SqlKeyword.EQ, value));
	}
	public  Criteria le(String column,Object value) {
		return add(new ColumnProperty(column, SqlKeyword.EQ, value));
	}
}
