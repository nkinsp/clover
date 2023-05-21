package com.github.nkinsp.clover.code.handlers;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import com.github.nkinsp.clover.code.DbContext;
import com.github.nkinsp.clover.enums.PrimaryKeyType;
import com.github.nkinsp.clover.query.InsertWrapper;
import com.github.nkinsp.clover.table.EntityFieldInfo;
import com.github.nkinsp.clover.table.EntityMapper;
import com.github.nkinsp.clover.table.TableInfo;
import com.github.nkinsp.clover.util.ConvertUtils;
import com.github.nkinsp.clover.util.EntityMapperUtils;
import com.github.nkinsp.clover.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InsertHandler<R,En> implements ExecuteHandler<R>{

	
	private En entity;

	
	@SuppressWarnings("unchecked")
	@Override
	public R handle(DbContext context) {
		
		TableInfo tableInfo = DbContext.getTableInfo(entity.getClass());
		EntityMapper mapper = EntityMapperUtils.getEntityMapper(entity.getClass());
		Map<String, Object> entityData = new LinkedHashMap<String, Object>();
		
		for (String column : tableInfo.getColumns()) {
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
		
		
		InsertWrapper wrapper = new InsertWrapper(tableInfo, entityData);
		
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
	
	
	public InsertHandler(En enyity) {
		this.entity = enyity;
	}


}
