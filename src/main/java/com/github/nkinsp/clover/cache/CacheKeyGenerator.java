package com.github.nkinsp.clover.cache;

import com.github.nkinsp.clover.table.TableInfo;

/**
 * 缓存key生成
 */
public interface CacheKeyGenerator {

	String createKey(TableInfo<?> tableInfo,Object id);
}
