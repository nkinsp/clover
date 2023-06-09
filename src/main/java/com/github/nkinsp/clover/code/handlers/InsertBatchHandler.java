package com.github.nkinsp.clover.code.handlers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.CollectionUtils;

import com.github.nkinsp.clover.code.DbContext;
import com.github.nkinsp.clover.query.InsertWrapper;
import com.github.nkinsp.clover.table.EntityFieldInfo;
import com.github.nkinsp.clover.table.EntityMapper;
import com.github.nkinsp.clover.table.TableInfo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InsertBatchHandler<En> implements ExecuteHandler<int[]>{

	private Collection<En> entitys;
	
	private TableInfo<En> tableInfo;
	
	@Override
	public int[] handle(DbContext context) {

		if (CollectionUtils.isEmpty(entitys)) {
			return new int[0];
		}

		EntityMapper mapper = tableInfo.getEntityMapper();

		int size = entitys.size();

		List<Map<String, Object>> batchData = new ArrayList<>(size);

		for (En entity : entitys) {
			
			Map<String, Object> entityData = new LinkedHashMap<>();
			for (String column : tableInfo.getColumns()) {
				EntityFieldInfo fieldInfo = mapper.getByColumnName(column);
				if (fieldInfo != null) {
					Object value = fieldInfo.invokeGet(entity);
					//主键
					if(value == null && fieldInfo.getColumnName().equals(tableInfo.getPrimaryKeyName())) {
						value = tableInfo.getKeyGenerator().createId(context, tableInfo);
					}
					if (value != null) {
						entityData.put(column, value);
					}
				}
			}
			batchData.add(entityData);
		}
		
		Map<String, Object> first = batchData.get(0);
		
		InsertWrapper<En> wrapper = new InsertWrapper<>(tableInfo, first);
		
		String sql = wrapper.buildSql();
		
		List<Object[]> values = batchData.stream().map(data->data.values().toArray()).toList();
		
		if(context.isSqlLog()) {
			log.info("===> execute batch [sql={}  size={}]",sql,batchData.size());
		}
		
		return context.batchUpdate(sql, values);
		

	}

	public InsertBatchHandler(TableInfo<En> tableInfo,Collection<En> entitys) {
		super();
		this.tableInfo = tableInfo;
		this.entitys = entitys;
	}

	
}
