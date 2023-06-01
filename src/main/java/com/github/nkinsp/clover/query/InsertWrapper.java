package com.github.nkinsp.clover.query;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.nkinsp.clover.enums.SqlKeyword;
import com.github.nkinsp.clover.table.TableInfo;

import lombok.Getter;
import lombok.Setter;

public class InsertWrapper<T> implements ISqlGenerate{

	
	private TableInfo<T> tableInfo;
	
	@Setter
	@Getter
	private Map<String, Object> insertData;
	
	
	
	public InsertWrapper<T> set(String name,Object value) {
		
		insertData.put(name, value);
		
		return this;
	}
	 
	public Collection<Object> getValues() {
		
		 return insertData.values();
		
	}

	
	
	@Override
	public String buildSql() {

		Set<String> columnKeys = insertData.keySet();

		String columns = columnKeys.stream().collect(Collectors.joining(","));
		String values = columnKeys.stream().map(s -> "?").collect(Collectors.joining(","));

		return SqlKeyword.INSERT_SQL.format(tableInfo.getTableName(), columns, values);

	}

	public InsertWrapper(TableInfo<T> tableInfo, Map<String, Object> insertData) {
		super();
		this.tableInfo = tableInfo;
		this.insertData = insertData;
	}

	public InsertWrapper(TableInfo<T> tableInfo) {
		super();
		this.tableInfo = tableInfo;
	}
	
	
	
}
