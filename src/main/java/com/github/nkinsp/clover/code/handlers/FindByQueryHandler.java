package com.github.nkinsp.clover.code.handlers;

import java.util.List;

import org.springframework.util.CollectionUtils;

import com.github.nkinsp.clover.code.DbContext;
import com.github.nkinsp.clover.query.QueryWrapper;
import com.github.nkinsp.clover.table.TableInfo;

import lombok.Data;



@Data
public class FindByQueryHandler<E,T>  implements ExecuteHandler<E>{


	
	private Class<E> entityClass;
	
	private QueryWrapper<T> queryWrapper;
	
	
	@Override
	public E handle(DbContext context) {		
	
		List<E> list = new FindEntityRowMapperHandler<E, T>(entityClass, queryWrapper).handle(context);
		
		return CollectionUtils.isEmpty(list)?null:list.get(0);
	}

	public FindByQueryHandler(Class<E> entityClass,QueryWrapper<T> queryWrapper) {
		super();
		this.entityClass = entityClass;
		this.queryWrapper = queryWrapper;
		
	}
	
	@SuppressWarnings("unchecked")
	public FindByQueryHandler(TableInfo tableInfo,QueryWrapper<T> queryWrapper) {
		super();
		this.entityClass = (Class<E>) tableInfo.getEntityClass();
		this.queryWrapper = queryWrapper;
		
	}
	

	protected FindByQueryHandler() {
		super();
	}



	

	
	
	
}
