package com.github.nkinsp.clover.code.handlers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.CollectionUtils;

import com.github.nkinsp.clover.annotation.Cascade;
import com.github.nkinsp.clover.code.DbContext;
import com.github.nkinsp.clover.query.BeanRowMapper;
import com.github.nkinsp.clover.query.QueryWrapper;
import com.github.nkinsp.clover.result.Rows;
import com.github.nkinsp.clover.table.TableInfo;
import com.github.nkinsp.clover.util.ClassUtils;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class FindEntityRowMapperHandler<E, T> implements ExecuteHandler<List<E>> {

	private QueryWrapper<T> wrapper;

	private Class<E> entityClass;

	public FindEntityRowMapperHandler(Class<E> entityClass, QueryWrapper<T> wrapper) {
		super();
		this.wrapper = wrapper;
		this.entityClass = entityClass;
	}
	
	@SuppressWarnings("unchecked")
	public FindEntityRowMapperHandler(TableInfo tableInfo) {
		
		this.entityClass = (Class<E>) tableInfo.getEntityClass();
		this.wrapper = new QueryWrapper<>(tableInfo);
	}
	
	@SuppressWarnings("unchecked")
	public FindEntityRowMapperHandler(TableInfo tableInfo,QueryWrapper<T> wrapper) {
		
		this.entityClass = (Class<E>) tableInfo.getEntityClass();
		this.wrapper = wrapper;
	}
	

	@Override
	public List<E> handle(DbContext context) {
		String sql = wrapper.buildSql();
		List<Object> params = wrapper.getParams();
		if (context.isSqlLog()) {
			log.info("===> execute sql=[{}] params={}   ", sql, params);
		}
		List<E> results = context.query(sql, params.toArray(), new BeanRowMapper<E>(entityClass));
		if (CollectionUtils.isEmpty(results)) {
			return Rows.of(new ArrayList<>());
		}
		return ClassUtils.hasAnnotation(entityClass, Cascade.class)
				? context.opsCascadeQuery(sql, params.toArray(), entityClass)
				: Rows.of(results);
	}

}
