package com.github.nkinsp.clover.code.handlers;

import com.github.nkinsp.clover.query.PagingQueryWrapper;

public class FindPagingQueryHandler<E,T> extends FindEntityRowMapperHandler<E, T>{

	

	public FindPagingQueryHandler(Class<E> entityClass, PagingQueryWrapper<T> queryWrapper) {
		super(entityClass, queryWrapper);

	}

	

	
	
}
