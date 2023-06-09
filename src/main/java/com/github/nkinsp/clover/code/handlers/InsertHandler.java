package com.github.nkinsp.clover.code.handlers;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.support.GeneratedKeyHolder;

import com.github.nkinsp.clover.code.DbContext;
import com.github.nkinsp.clover.enums.PrimaryKeyType;
import com.github.nkinsp.clover.query.InsertWrapper;
import com.github.nkinsp.clover.table.EntityFieldInfo;
import com.github.nkinsp.clover.table.EntityMapper;
import com.github.nkinsp.clover.table.TableInfo;
import com.github.nkinsp.clover.util.ConvertUtils;
import com.github.nkinsp.clover.util.EntityMapperManager;
import com.github.nkinsp.clover.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InsertHandler<R,T> implements ExecuteHandler<R>{

	
	private T entity;
	
     
	private TableInfo<T> tableInfo;

	
	@SuppressWarnings("unchecked")
	@Override
	public R handle(DbContext context) {
		
		
	
		EntityMapper mapper = EntityMapperManager.getEntityMapper(entity.getClass());
		List<String> columns = mapper.getColumns();
		Map<String, Object> entityData = new LinkedHashMap<String, Object>(columns.size());
		
		for (String column : columns) {
			EntityFieldInfo fieldInfo = mapper.getByColumnName(column);
			if(fieldInfo != null) {
				Object value = fieldInfo.invokeGet(entity);
				if(value != null) {	
					entityData.put(column, value);
				}
			}
		}	
		
		
	
		R id = (R) tableInfo.getKeyGenerator().createId(context, tableInfo);
		if(!StringUtils.isEmpty(id)) {
			entityData.put(tableInfo.getPrimaryKeyName(),id) ;
		}
		
		InsertWrapper<T> wrapper = new InsertWrapper<T>(tableInfo, entityData);
		
		String sql = wrapper.buildSql();
		Collection<Object> values = wrapper.getValues();
		if(context.isSqlLog()) {
			log.info("===> execute [sql={}  params={}]", sql,values);			
		}
		

		
		if(tableInfo.getPrimaryKeyType() == PrimaryKeyType.AUTO_INCREMENT) {
			GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
			Object[] params =  values.toArray();
			context.update((conn) -> {
				PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				for (int j = 0; j < params.length; j++) {
					statement.setObject((j + 1), params[j]);
				}
				return statement;
			}, keyHolder);
			Number number = keyHolder.getKey();
			EntityFieldInfo fieldInfo = mapper.getByColumnName(tableInfo.getPrimaryKeyName());
			Class<?> idType = fieldInfo.getField().getType();
			
			return (R) ConvertUtils.convertTo(number, idType);
		}
		context.update(sql, values.toArray());
		return id;
	}
	
	
	public InsertHandler(TableInfo<T> tableInfo, T enyity) {
		this.entity = enyity;
		this.tableInfo = tableInfo;
	}


}
