package com.github.nkinsp.clover.code.dialect;

import com.github.nkinsp.clover.code.DbDialectAdapter;
import com.github.nkinsp.clover.enums.DbType;
import com.github.nkinsp.clover.query.QueryWrapper;

public class OracleDbDialectAdapter implements DbDialectAdapter{

	@Override
	public DbType dbType() {
		return DbType.ORACLE;
	}

	@Override
	public String buildPaingSql(QueryWrapper<?> wrapper, String sql, Integer pageNum, Integer pageSize) {
		
		
		
		int start = (pageNum - 1) * pageSize;
		int end = pageNum * pageSize;
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append("SELECT * ");
		sqlBuilder.append("FROM (SELECT ROW_.*, ROWNUM ROWNUM_ ");
		sqlBuilder.append("FROM (");
		sqlBuilder.append(sql);
		sqlBuilder.append(") ROW_");
		sqlBuilder.append("WHERE ROWNUM <= "+end+") ");
		sqlBuilder.append("WHERE ROWNUM_ >= "+start+" ");
		return sql.toString();
	}

	

}
