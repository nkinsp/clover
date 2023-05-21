package com.github.nkinsp.clover.code;

import java.lang.reflect.ParameterizedType;

import org.springframework.beans.factory.annotation.Autowired;

import com.github.nkinsp.clover.table.TableInfo;

import lombok.Data;

@Data
public abstract class BaseRepositoryBean<Id,En> implements BaseRepository<Id, En> {

	
	@Autowired
	private DbContext dbContext;

	private TableInfo tableInfo;

	
	@Override
	public TableInfo tableInfo() {
		// TODO Auto-generated method stub
		return this.tableInfo;
	}
	
	
	public BaseRepositoryBean() {
		Class<?>tableClass = (Class<?>) (((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
		this.tableInfo = DbContext.getTableInfo(tableClass);
		
	}

	
	@Override
	public DbContext dbContext() {
		// TODO Auto-generated method stub
		return dbContext;
	}
}
