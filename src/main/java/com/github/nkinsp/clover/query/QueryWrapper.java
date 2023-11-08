package com.github.nkinsp.clover.query;


import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.nkinsp.clover.code.DbContext;
import com.github.nkinsp.clover.enums.SqlKeyword;
import com.github.nkinsp.clover.table.TableInfo;
import com.github.nkinsp.clover.util.ObjectUtils;

import lombok.Getter;

public class QueryWrapper<En> extends AbstractWrapper<QueryWrapper<En>>{

	
	@Getter
	private TableInfo<En> tableInfo;
	
	@Getter
	private ColumnBuilder columnBuilder = new ColumnBuilder();
	
	@Getter
	private String orderBy;
	
	@Getter
	private String groupBy;
	
	public QueryWrapper<En> select(String column) {
		return select(new String[] {column});
	}
	
	public QueryWrapper<En> select(String ...columns) {
		for (String column : columns) {
			columnBuilder.add(column);
		}
		return this;
	}

	public QueryWrapper<En> select(String prefix,List<String> columns) {
		columnBuilder.add(prefix, columns.toArray(String[]::new));
		return this;
	}
	
	public <T> QueryWrapper<En> select(Class<T> entityClass){
		columnBuilder.add(entityClass);
		return this;		
	}
	
	public <T> QueryWrapper<En> select(String prefix,Class<T> entityClass){
		columnBuilder.add(prefix,entityClass);
		return this;		
	}
	
	
	public QueryWrapper<En> orderBy(String...cols){
		this.orderBy = SqlKeyword.ORDER_BY.format(Stream.of(cols).collect(Collectors.joining(",")));
		return this;
	}
	
	public QueryWrapper<En> orderByAsc(String...cols){
		return orderBy(Stream.of(cols).map(s->s+" ASC").toArray(String[]::new));
	}
	
	public QueryWrapper<En> orderByDesc(String...cols){
		return orderBy(Stream.of(cols).map(s->s+" DESC").toArray(String[]::new));
	}
	
	public QueryWrapper<En> groupBy(String...cols){
		this.groupBy = SqlKeyword.GROUP_BY.format(Stream.of(cols).collect(Collectors.joining(",")));
		return this;
	}
	
	public QueryWrapper(Class<En> tableClass) {
		this.tableInfo = DbContext.getTableInfo(tableClass);
	}
	
	public QueryWrapper(TableInfo<En> tableInfo) {
		this.tableInfo = tableInfo;
	}
	
	public Class<En> getTableClass(){
		
		return this.tableInfo.getEntityClass();
	}
	
	@Override
	public String buildSql() {
	
		
		String columns =columnBuilder.getColumns().isEmpty()?"*":columnBuilder.getColumns().stream().collect(Collectors.joining(","));
		
		String sql = SqlKeyword.SELECT_SQL.format(columns,tableInfo.getTableName());
		
		StringBuilder sqlBuilder = new StringBuilder(sql).append(" ");
		if(!getConditions().isEmpty()) {
			sqlBuilder.append(SqlKeyword.WHERE.value)
							.append(" ")
							.append(getConditions().stream().collect(Collectors.joining(" ")))
							.append(" ")
							;
		}
		if(!ObjectUtils.isEmpty(groupBy)) {
			sqlBuilder.append(groupBy)
							.append(" ")
							;
		}
		if(!ObjectUtils.isEmpty(orderBy)) {
			sqlBuilder.append(orderBy)
							 .append(" ")
							 ;
		}

		return sqlBuilder.toString();
	}

	
}
