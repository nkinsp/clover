package com.github.nkinsp.clover.query;

import java.beans.PropertyDescriptor;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.springframework.beans.BeanUtils;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.JdbcUtils;

import com.github.nkinsp.clover.table.EntityFieldInfo;
import com.github.nkinsp.clover.table.EntityMapper;
import com.github.nkinsp.clover.util.EntityMapperManager;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BeanRowMapper<T> implements RowMapper<T>{

	private  Class<T> entityClass;
	
	private EntityMapper entityMapper;
	
	public BeanRowMapper(Class<T> entityClass) {
		super();
		this.entityClass = entityClass;
		this.entityMapper = EntityMapperManager.getEntityMapper(entityClass);
	}

	@Override
	public T mapRow(ResultSet rs, int rowNum) throws SQLException {
		T result = BeanUtils.instantiateClass(entityClass);
		ResultSetMetaData metaData = rs.getMetaData();
		int count = metaData.getColumnCount();
		for (int index = 1; index <= count; index++) {
			String columnName = JdbcUtils.lookupColumnName(metaData, index);
			EntityFieldInfo fieldInfo = entityMapper.getByColumnName(columnName);
			if (fieldInfo == null) {
				if (rowNum == 0) {
					log.debug("Entity [{}] column [{}]  no mapping",entityClass.getName(),columnName);
				}
				continue;
			}
			
			
			
			
			PropertyDescriptor pd = fieldInfo.getProperty();
			try {
				Object value = JdbcUtils.getResultSetValue(rs, index, pd.getPropertyType());
				fieldInfo.invokeSet(result, value);
			} catch (SQLException e) {
				if (rowNum == 0) {
					log.warn("Entity [{}] column {} set to propery {} error {}", entityClass.getName(),columnName, pd.getName(), e.getMessage());
				}
			} catch (Exception e) {
				// TODO: handle exception
				if (rowNum == 0) {
					log.warn("Entity [{}] column {} set to propery {} error {}", entityClass.getName(),columnName, pd.getName(), e.getMessage());
				}
			}
		}
		return result;
	}
}
