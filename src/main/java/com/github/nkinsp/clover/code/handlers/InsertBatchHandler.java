package com.github.nkinsp.clover.code.handlers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.util.CollectionUtils;

import com.github.nkinsp.clover.code.DbContext;
import com.github.nkinsp.clover.table.EntityFieldInfo;
import com.github.nkinsp.clover.table.EntityMapper;
import com.github.nkinsp.clover.table.TableInfo;
import com.github.nkinsp.clover.util.EntityMapperManager;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InsertBatchHandler<En> implements ExecuteHandler<int[]>{

	private Collection<En> entitys;
	
	private TableInfo<En> tableInfo;
	
	@SuppressWarnings("unchecked")
	@Override
	public int[] handle(DbContext context) {

		if (CollectionUtils.isEmpty(entitys)) {
			return new int[0];
		}

		EntityMapper mapper = EntityMapperManager.getEntityMapper(tableInfo.getEntityClass());

		int size = entitys.size();

		List<Map<String, Object>> batchData = new ArrayList<>(size);

		for (En entity : entitys) {
			
			Map<String, Object> entityData = new LinkedHashMap<>();
			for (String column : tableInfo.getColumns()) {
				EntityFieldInfo fieldInfo = mapper.getByColumnName(column);
				if (fieldInfo != null) {
					Object value = fieldInfo.invokeGet(entity);
					if (value != null) {
						entityData.put(column, value);
					}
				}
			}
			batchData.add(entityData);
		}
		
		Map<String, Object> first = batchData.get(0);

		SimpleJdbcInsert insert = new SimpleJdbcInsert(context)
				.withTableName(tableInfo.getTableName())
				.usingColumns(first.keySet().toArray(String[]::new));
		if(context.isSqlLog()) {
			log.info("===> execute batch [sql={}  size={}]", insert.getInsertString(),batchData.size());
		}
		
		return insert.executeBatch(batchData.toArray(LinkedHashMap[]::new));

	}

	public InsertBatchHandler(TableInfo<En> tableInfo,Collection<En> entitys) {
		super();
		this.tableInfo = tableInfo;
		this.entitys = entitys;
	}

	
}
