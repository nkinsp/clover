package com.github.nkinsp.clover.query;



import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.github.nkinsp.clover.enums.SqlKeyword;
import com.github.nkinsp.clover.table.TableInfo;

import lombok.Getter;

public class UpdateWrapper<T> extends AbstractWrapper<UpdateWrapper<T>>{

	@Getter
	private TableInfo<T> tableInfo;

	private Map<String, String> updateColumns = new LinkedHashMap<String, String>();
	
	public UpdateWrapper(TableInfo<T> tableInfo) {
		this.tableInfo = tableInfo;
	}
	
	
	
	public UpdateWrapper<T> set(String column,Object value) {
		updateColumns.put(colunmFormat(column),"?");
		param(value);
		return this;
	}

	public UpdateWrapper<T> setSqlString(String column,String sql,Object value) {
		updateColumns.put(colunmFormat(column),sql);
		param(value);
		return this;
	}
	
	
	
	@Override
	public String buildSql() {
		
		
		String setStr = updateColumns.keySet().stream().map(k->k+" = "+updateColumns.get(k)).collect(Collectors.joining(","));
		
		StringBuilder sqlBuilder = new StringBuilder(SqlKeyword.UPDATE_SQL.format(tableInfo.getTableName(),setStr));
		if(!getConditions().isEmpty()) {
			sqlBuilder.append(" ")
					  .append(SqlKeyword.WHERE.value)
					  .append(" ")
					  .append(getConditions().stream().collect(Collectors.joining(" ")))
					  .append(" ")
					;
		}
		return sqlBuilder.toString();
	}

	
}
