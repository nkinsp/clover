package com.github.nkinsp.clover.code.handlers;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.github.nkinsp.clover.cache.CacheManager;
import com.github.nkinsp.clover.code.DbContext;
import com.github.nkinsp.clover.query.UpdateWrapper;
import com.github.nkinsp.clover.table.EntityFieldInfo;
import com.github.nkinsp.clover.table.TableInfo;

public class UpdateEntityHandler<T> extends UpdateHandler<T> {

	private T entity;

	private TableInfo<T> tableInfo;

	public UpdateEntityHandler(TableInfo<T> tableInfo, T entity) {
		super(tableInfo);
		this.entity = entity;
		this.tableInfo = tableInfo;
	}

	@Override
	public Integer handle(DbContext context) {

		EntityFieldInfo idField = tableInfo.getEntityMapper().getByColumnName(tableInfo.getPrimaryKeyName());
		Object id = idField.invokeGet(entity);
		UpdateWrapper<T> wrapper = getUpdateWrapper();
		
		Map<String, Object> updateMap = new LinkedHashMap<String, Object>();

		List<EntityFieldInfo> entityFieldInfos = tableInfo.getEntityMapper().getEntityFieldInfos();
		
		for (EntityFieldInfo field : entityFieldInfos) {
			
			if(!field.getFieldName().equals(idField.getFieldName())) {
				Object value = field.invokeGet(entity);
				if (value != null) {
					updateMap.put(field.getColumnName(), value);
				}
			}
		}
		
		updateMap.forEach(wrapper::set);
		
		wrapper.where().eq(idField.getColumnName(), id);
		// 执行数据库
		Integer result = super.handle(context);

		if (!tableInfo.isCache()) {
			return result;
		}

		CacheManager manager = context.getCacheManager();
		if (manager == null) {
			return result;
		}
		T data = manager.get(tableInfo, id);
		if (data == null) {
			return result;
		}

		for (Entry<String, Object> en : updateMap.entrySet()) {

			EntityFieldInfo fieldInfo = tableInfo.getEntityMapper().get(en.getKey());
			if (fieldInfo != null) {
				fieldInfo.invokeSet(data, en.getValue());
			}
		}
		
		manager.set(tableInfo, id, data);
		
		return result;

	}

}
