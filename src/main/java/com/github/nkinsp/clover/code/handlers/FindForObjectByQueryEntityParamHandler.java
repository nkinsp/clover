package com.github.nkinsp.clover.code.handlers;

import com.github.nkinsp.clover.code.DbContext;
import com.github.nkinsp.clover.query.EntityQuery;
import com.github.nkinsp.clover.query.QueryWrapper;


public class FindForObjectByQueryEntityParamHandler<R,T> implements ExecuteHandler<R> {
	
	
	private Class<R> typeClass;
	
	private EntityQuery<T> entityQuery;
	
	private QueryWrapper<T> queryWrapper;
	
	
	@Override
	public R handle(DbContext context) {
		
		
		context.appendWrapperParmas(queryWrapper, entityQuery);
		
		return new FindForObjectHandler<>(typeClass, queryWrapper).handle(context);
		
	}


	public FindForObjectByQueryEntityParamHandler(Class<R> typeClass, EntityQuery<T> entityQuery,
			QueryWrapper<T> queryWrapper) {
		super();
		this.typeClass = typeClass;
		this.entityQuery = entityQuery;
		this.queryWrapper = queryWrapper;
	}


	


	
	
	
	
	
}
