package com.github.nkinsp.clover.code.dialect;

import com.github.nkinsp.clover.code.DbDialectAdapter;
import com.github.nkinsp.clover.enums.DbType;
import com.github.nkinsp.clover.query.QueryWrapper;

public class MysqlDbDialectAdapter implements DbDialectAdapter{

	@Override
	public DbType dbType() {
		return DbType.MYSQL;
	}

	@Override
	public String buildPaingSql(QueryWrapper<?> wrapper, String sql, Integer pageNum, Integer pageSize) {
		
		
		StringBuilder builder = new StringBuilder(sql)
			.append(" ")
			.append("LIMIT ? , ?")
		;
		
		int offset =  (pageNum - 1)*pageSize;
		wrapper.params(offset,pageSize);
		
		
		return builder.toString();
	}

	

}
