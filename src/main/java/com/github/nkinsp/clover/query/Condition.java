package com.github.nkinsp.clover.query;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface Condition<S extends Condition<S>> {

	
	/**
	 *  sql 片段
	 * @param sql
	 * @param params
	 * @return
	 */
	S excerpt(String sql, Object... params);

	/**
	 * 所有字段等于
	 * @param allEq
	 * @return
	 */
	S allEq(Map<String, Object> allEq);
	
	/**
	 *  等于 
	 * @param column
	 * @param value
	 * @return
	 */
	S eq(String column, Object value);

	/**
	 * 不等于
	 * @param column
	 * @param value
	 * @return
	 */
	S ne(String column, Object value);

	/**
	 * 大于
	 * @param column
	 * @param value
	 * @return
	 */
	S gt(String column, Object value);

	/**
	 *  大于等于
	 * @param column
	 * @param value
	 * @return
	 */
	S ge(String column, Object value);

	/**
	 * 小于
	 * @param column
	 * @param value
	 * @return
	 */
	S lt(String column, Object value);

	/**
	 * 小于等于
	 * @param column
	 * @param value
	 * @return
	 */
	S le(String column, Object value);
	
	
	/**
	 * where
	 * @return
	 */
	S where();
	
	/**
	 * and 
	 * @return
	 */
	S and();

	/**
	 * and 
	 * @param consumer
	 * @return
	 */
	S and(Consumer<S> consumer);

	/**
	 * AND sql 
	 * @param sql
	 * @param params
	 * @return
	 */
	S and(String sql, Object... params);

	/**
	 * field in (?)
	 * @param column
	 * @param params
	 * @return
	 */
	S in(String column, Object... params);

	/**
	 * field in (sql)
	 * @param <T>
	 * @param column
	 * @param tableClass
	 * @param consumer
	 * @return
	 */
	<T> S in(String column,Class<T> tableClass,Consumer<QueryWrapper<T>> consumer);
	

	/**
	 * 嵌套
	 * @param consumer
	 * @return
	 */
	S nesting(Consumer<S> consumer);
	
	S like(String column,Object value);
	
	S or();
	
	S or(Consumer<S> consumer);
	
	S between(String column,Object v1,Object v2);

	S andThen(boolean condition, Consumer<S> func);

	<V> S andNotEmptyThen(V v, BiConsumer<V, S> consumer);

	S param(Object param);

	S params(Object... params);

}
