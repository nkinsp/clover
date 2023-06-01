package com.github.nkinsp.clover.code;

import com.github.nkinsp.clover.table.TableInfo;

public class PrimaryKeyGenerator  implements KeyGenerator  {

	@Override
	public Object createId(DbContext dbContext, TableInfo tableInfo) {
		return dbContext.primaryKeyGenerator(tableInfo);
	}



}
