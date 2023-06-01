package com.github.nkinsp.clover.code;

import com.github.nkinsp.clover.enums.DbType;
import com.github.nkinsp.clover.query.QueryWrapper;

public interface DbDialectAdapter {

	
	DbType dbType();
	
	
	String buildPaingSql(QueryWrapper<?> wrapper,String sql,Integer pageNum,Integer pageSize);
	
}
