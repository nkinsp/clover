package com.github.nkinsp.clover.query;

import com.github.nkinsp.clover.code.DbContext;
import com.github.nkinsp.clover.code.DbDialectAdapter;
import com.github.nkinsp.clover.enums.DbType;
import com.github.nkinsp.clover.table.TableInfo;

public class PagingQueryWrapper<T> extends QueryWrapper<T>{

	private Integer pageNumber = 1;
	
	private Integer pageSize = 15;
	
	private DbType dbType;
	
	public PagingQueryWrapper( Class<T> tableClass,DbType dbType,Integer pageNumber,Integer pageSize) {
		super(tableClass);
		this.dbType = dbType;
		this.pageNumber = pageNumber;
		this.pageSize = pageSize;
	}
	
	
	public PagingQueryWrapper(TableInfo<T> tableInfo,DbType dbType,Integer pageNumber,Integer pageSize) {
		super(tableInfo);
		this.pageNumber = pageNumber;
		this.pageSize = pageSize;
		this.dbType = dbType;
	}
	
	
	@Override
	public String buildSql() {
		 String sql = super.buildSql();		 
		 DbDialectAdapter adapter = DbContext.getDialectAdapter(dbType); 
		 return adapter.buildPaingSql(this, sql, pageNumber, pageSize);
		 
	}

}
