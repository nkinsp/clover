package com.github.nkinsp.clover.code.handlers;



import com.github.nkinsp.clover.code.DbContext;
import com.github.nkinsp.clover.query.EntityQuery;
import com.github.nkinsp.clover.query.QueryWrapper;
import com.github.nkinsp.clover.result.Rows;
import com.github.nkinsp.clover.table.TableInfo;
import com.github.nkinsp.clover.util.StringUtils;

import lombok.Getter;

public class FindRowsByQueryEntityParamHandler<T,E> implements ExecuteHandler<Rows<E>> {

	@Getter
	private Class<E> entityClass;
	
	@Getter
	private TableInfo<T> tableInfo;
	
	private EntityQuery<T> query;
	
	
	
	
	public QueryWrapper<T> createQueryWrapper(){
		
		return new QueryWrapper<>(tableInfo);
	}
	
	
	@Override
	public Rows<E> handle(DbContext context) {

		QueryWrapper<T> wrapper = createQueryWrapper();
		
		context.appendWrapperParmas(wrapper,this.query);
		
		if(!StringUtils.isEmpty(query.orderBy())){
			wrapper.orderBy(query.orderBy());
		}
		
		
	

		Rows<E> rows = new FindEntityRowMapperHandler<>(entityClass, wrapper).handle(context);
		
		

		return rows;
	}

	public FindRowsByQueryEntityParamHandler(Class<E> entityClass, TableInfo<T> tableInfo, EntityQuery<T> query) {
		super();
		this.entityClass = entityClass;
		this.tableInfo = tableInfo;
		this.query = query;
	}

	

	
	
	
}
