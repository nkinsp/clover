package com.github.nkinsp.clover.query;

import java.util.ArrayList;
import java.util.List;

import com.github.nkinsp.clover.table.EntityMapper;
import com.github.nkinsp.clover.util.EntityMapperUtils;
import com.github.nkinsp.clover.util.StringUtils;

import lombok.Getter;

public class ColumnBuilder {

	@Getter
	private List<String> columns = new ArrayList<String>();
	
	
	public ColumnBuilder add(String column) {
		
		columns.add(column);
		
		return this;
	}
	
	public ColumnBuilder add(String prefix,String column) {
		return add(prefix+"."+column);
	}
	
	public ColumnBuilder add(String prefix,String ...cols) {
		for (String column : cols) {
			add(prefix, column);
		}
		return this;
	}
	
	public ColumnBuilder add(String prefix,String column, String alias) {
		return add(String.format("%s.%s AS %s", prefix,column,alias));
	}
	
	public ColumnBuilder add(Class<?> entityClass) {
		EntityMapper entityMapper = EntityMapperUtils.getEntityMapper(entityClass);
		entityMapper.getEntityFieldInfos().stream().filter(x->!x.isCascade()).forEach(field->{
			if(StringUtils.isEmpty(field.getAlias())) {
				add(field.getColumnName());
				return;
			}
			add(field.getColumnName()+" AS "+field.getAlias());
		});
		return this;
	}
	
	public ColumnBuilder add(String prefix,Class<?> entityClass) {
		EntityMapper entityMapper = EntityMapperUtils.getEntityMapper(entityClass);
		entityMapper.getEntityFieldInfos().stream().filter(x->!x.isCascade()).forEach(field->{
			if(StringUtils.isEmpty(field.getAlias())) {
				add(prefix,field.getColumnName());
				return;
			}
			add(prefix,field.getColumnName(),field.getAlias());
		});
		return this;
	}
	
}
