package com.github.nkinsp.clover.cache.impl;

import com.github.nkinsp.clover.cache.CacheKeyGenerator;
import com.github.nkinsp.clover.table.TableInfo;

public class DefaultCacheKeyGenderatorImpl implements CacheKeyGenerator {

	@Override
	public String createKey(TableInfo<?> tableInfo, Object id) {
		
		return "db:"+tableInfo.getTableName()+":"+String.valueOf(id);
	}

}
