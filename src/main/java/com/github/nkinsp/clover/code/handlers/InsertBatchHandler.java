package com.github.nkinsp.clover.code.handlers;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.util.CollectionUtils;

import com.github.nkinsp.clover.code.DbContext;
import com.github.nkinsp.clover.table.EntityFieldInfo;
import com.github.nkinsp.clover.table.EntityMapper;
import com.github.nkinsp.clover.table.TableInfo;
import com.github.nkinsp.clover.util.EntityMapperUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InsertBatchHandler<En> implements ExecuteHandler<int[]>{

	private Collection<En> entitys;
	
	@SuppressWarnings("unchecked")
	@Override
	public int[] handle(DbContext context) {

		if (CollectionUtils.isEmpty(entitys)) {
			return null;
		}

		List<En> batchs = entitys.stream().collect(Collectors.toList());

		Class<?> entityClass = batchs.get(0).getClass();
		TableInfo tableInfo = DbContext.getTableInfo(entityClass);
		EntityMapper mapper = EntityMapperUtils.getEntityMapper(entityClass);

		int size = batchs.size();

		Map<String, Object>[] batchData = new LinkedHashMap[batchs.size()];

		for (int i = 0; i < size; i++) {

			En entity = batchs.get(i);
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
			batchData[i] = entityData;
		}

		Map<String, Object> first = batchData[0];

		SimpleJdbcInsert insert = new SimpleJdbcInsert(context)
				.withTableName(tableInfo.getTableName())
				.usingColumns(first.keySet().toArray(new String[first.size()]));
		if(context.isSqlLog()) {
			log.info("===> execute batch [sql={}  size={}]", insert.getInsertString(),batchData.length);
		}
		
		return insert.executeBatch(batchData);

	}

	public InsertBatchHandler(Collection<En> entitys) {
		super();
		this.entitys = entitys;
	}

	
}
