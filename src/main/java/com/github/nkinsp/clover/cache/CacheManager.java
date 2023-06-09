package com.github.nkinsp.clover.cache;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import com.github.nkinsp.clover.table.TableInfo;

/**
 * 缓存管理
 * @author yue
 *
 */
public interface CacheManager {

	/**
	 * 获取缓存
	 * @param <T>
	 * @param tableInfo
	 * @param key
	 * @return
	 */
	<T> T get(TableInfo<T> tableInfo,Object key);
	

	/**
	 * 
	 * @param <T>
	 * @param tableInfo
	 * @param key
	 * @param supplier
	 * @return
	 */
	<T> T getAndSet(TableInfo<T> tableInfo,Object key,Supplier<T> supplier);
	
	/**
	 * @param <T>
	 * @param tableInfo
	 * @param key
	 * @param value
	 */
    <T> void set(TableInfo<T> tableInfo, Object key,T value);
	
    /**
     * 
     * @param <T>
     * @param tableInfo
     * @param keys
     * @return
     */
	<T> List<T> multiGet(TableInfo<T> tableInfo, Collection<Object> keys);
	
	/**
	 * 
	 * @param <T>
	 * @param tableInfo
	 * @param data
	 */
	<T> void multiSet(TableInfo<T> tableInfo,Map<Object, T> data);
	
	/**
	 * @param <T>
	 * @param tableInfo
	 * @param values
	 */
	<T> void multiSet(TableInfo<T> tableInfo,List<T> values);
	
	/**
	 * 批量获取
	 * @param <T>
	 * @param tableInfo
	 * @param keys
	 * @param func
	 * @return
	 */
	<T> List<T> multiGetAndSet(TableInfo<T> tableInfo,Collection<Object> keys,Function<Collection<Object>,List<T>> func);
	
	/**
	 * 删除
	 * @param <T>
	 * @param tableInfo
	 * @param key
	 */
	<T> void delete(TableInfo<T> tableInfo, Object key);
	
	/**
	 * 批量删除
	 * @param <T>
	 * @param tableInfo
	 * @param keys
	 */
	<T> void delete(TableInfo<T> tableInfo,Collection<Object> keys);
	
	
}
