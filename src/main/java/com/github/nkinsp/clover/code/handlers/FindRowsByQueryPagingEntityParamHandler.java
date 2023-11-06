package com.github.nkinsp.clover.code.handlers;

import org.springframework.util.StringUtils;

import com.github.nkinsp.clover.enums.DbType;
import com.github.nkinsp.clover.query.PagingEntityQuery;
import com.github.nkinsp.clover.query.PagingInfo;
import com.github.nkinsp.clover.query.PagingQueryWrapper;
import com.github.nkinsp.clover.query.QueryWrapper;
import com.github.nkinsp.clover.table.TableInfo;

public class FindRowsByQueryPagingEntityParamHandler<T,E> extends FindRowsByQueryEntityParamHandler<T,E>{

	private PagingEntityQuery<T> entityQuery;
	
	private DbType dbType;
	

	public FindRowsByQueryPagingEntityParamHandler(Class<E> entityClass,TableInfo<T> tableInfo,DbType dbType, PagingEntityQuery<T> query) {
		super(entityClass,tableInfo, query);
		this.entityQuery = query;
		this.dbType = dbType;
	}
	

	@Override
	public QueryWrapper<T> createQueryWrapper() {
		
		PagingInfo paging = entityQuery.paging();
	
		PagingQueryWrapper<T> wrapper = new PagingQueryWrapper<>(getTableInfo(),this.dbType,paging.getPageNum(),paging.getPageSize());
		
		
		String orderBy = entityQuery.orderBy();
		
		if(StringUtils.hasText(orderBy)) {
			wrapper.orderBy(orderBy);
		}
		
		return wrapper;
		
		
	}

}
