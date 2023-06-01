package com.github.nkinsp.clover.code.handlers;

import com.github.nkinsp.clover.code.DbContext;
import com.github.nkinsp.clover.query.QueryWrapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FindForObjectHandler<R,T> implements ExecuteHandler<R> {

	
	private Class<R> resultType;
	
	private QueryWrapper<T> queryWrapper;
	
	
	@Override
	public R handle(DbContext context) {
		
		String sql = queryWrapper.buildSql();
		
		Object[] params = queryWrapper.getParams().toArray();
		
		if(context.isSqlLog()) {
			log.info("===> execute sql=[{}] params={}   ", sql, params);
		}
		
		return context.queryForObject(sql, resultType,params);
		
	}


	public FindForObjectHandler(Class<R> resultType, QueryWrapper<T> queryWrapper) {
		super();
		this.resultType = resultType;
		this.queryWrapper = queryWrapper;
	}

	
	
	
	
	
}
