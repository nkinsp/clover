package com.github.nkinsp.clover.query;

import java.util.stream.Collectors;

import com.github.nkinsp.clover.code.DbContext;
import com.github.nkinsp.clover.enums.SqlKeyword;
import com.github.nkinsp.clover.table.TableInfo;

public class DeleteWrapper extends AbstractWrapper<DeleteWrapper>{

	
	private TableInfo tableInfo;
	
	
	public DeleteWrapper(Class<?> tableClass) {
		this.tableInfo = DbContext.getTableInfo(tableClass);
	}
	
	public DeleteWrapper(TableInfo tableInfo) {
		this.tableInfo = tableInfo;
	}
	
	
	@Override
	public String buildSql() {
		
		StringBuilder sqlBuilder = new StringBuilder(SqlKeyword.DELETE_SQL.format(tableInfo.getTableName())).append(" ");
		if(!getConditions().isEmpty()) {	
			sqlBuilder.append(SqlKeyword.WHERE.value)
						     .append(" ")
						     .append(getConditions().stream().collect(Collectors.joining(" ")))
						     .append(" ");
		}
		return sqlBuilder.toString();
	}

}
