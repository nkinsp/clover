package com.github.nkinsp.clover.table;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.Getter;

public class EntityMapper {

	@Getter
	private Class<?> entityClass;
	
	@Getter
	private List<EntityFieldInfo> entityFieldInfos;
	
	private Map<String, EntityFieldInfo> filedNameMap;
	
	private Map<String, EntityFieldInfo> columnNameMap;

	public EntityMapper(Class<?> entityClass, List<EntityFieldInfo> entityFieldInfos) {
		super();
		this.entityClass = entityClass;
		this.entityFieldInfos = entityFieldInfos;
		this.filedNameMap = entityFieldInfos.stream().collect(Collectors.toMap(x->x.getFieldName(), v->v));
		this.columnNameMap = entityFieldInfos.stream().collect(Collectors.toMap(x->x.getColumnName(), v->v));
	}
	


	/**
	 * 获取对应的
	 * @return
	 */
	public List<String> getColumns(){
		
		return entityFieldInfos.stream().map(EntityFieldInfo::getColumnName).collect(Collectors.toList());
		
	}
	
	public EntityFieldInfo getByFieldName(String name) {
		
		return filedNameMap.get(name);
	}
	
	public EntityFieldInfo getByColumnName(String name) {
		
		return columnNameMap.get(name);
		
	}
	
	public EntityFieldInfo get(String name) {
		EntityFieldInfo info = getByColumnName(name);
		if(info != null) {
			return info;
		}
		return getByFieldName(name);
		
	}

	
	
}
