package com.github.nkinsp.clover.table;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.alibaba.fastjson2.JSON;
import com.github.nkinsp.clover.annotation.CascadeMapperColumn;
import com.github.nkinsp.clover.annotation.Column;
import com.github.nkinsp.clover.code.type.JsonType;
import com.github.nkinsp.clover.enums.JoinType;
import com.github.nkinsp.clover.util.StringUtils;

import lombok.Data;

@Data
public class EntityFieldInfo {

	private String fieldName;
	
	private String columnName;
	
	private PropertyDescriptor property;
	
	private Field field;
	
	private String alias;
	
	private boolean cascade = false;
	
	private boolean json = false;
	
	private CascadeInfo cascadeInfo;

	
	public static EntityFieldInfo create(Class<?> beanClass, Field field) {

		if(field == null){
			return null;
		}

		synchronized (beanClass) {

			EntityFieldInfo fieldInfo = new EntityFieldInfo();
			fieldInfo.setFieldName(field.getName());
			fieldInfo.setField(field);
			fieldInfo.setColumnName(StringUtils.camelToUnder(field.getName()));
			
			
			if(JsonType.class.isAssignableFrom(field.getType())) {
				
				fieldInfo.setJson(true);
				
			}
			
			
			Column column = field.getAnnotation(Column.class);
			if (column != null) {
				
				if(!StringUtils.isEmpty(column.value())) {
					fieldInfo.setColumnName(column.value());
				}
				
				if(!StringUtils.isEmpty(column.alias())) {
					fieldInfo.setAlias(column.alias());
				}
				
				fieldInfo.setJson(column.json());
				
				
			}
			CascadeMapperColumn cascade = field.getAnnotation(CascadeMapperColumn.class);
			if (cascade != null) {
				fieldInfo.setCascade(true);
				CascadeInfo info = new CascadeInfo();
				info.setJoinType(cascade.joinType());
				info.setJoinColumn(StringUtils.isEmpty(cascade.joinColumn()) ? field.getName()+"_id" : cascade.joinColumn());
				info.setResultTypeClass(field.getType());
				if (info.getJoinType() == JoinType.MANY) {
					if (!Collection.class.isAssignableFrom(field.getType())) {
						throw new RuntimeException("field " + field.getName() + " must be AssignableFrom Collection");
					}
					ParameterizedType type = (ParameterizedType) field.getGenericType();
			
					Class<?> typeClass = (Class<?>) type.getActualTypeArguments()[0];
					
					info.setJoinTable(cascade.joinTable() == void.class ? typeClass:cascade.joinTable());
					
					info.setResultTypeClass(typeClass);
				}else {
					info.setJoinTable(cascade.joinTable() == void.class ? field.getType():cascade.joinTable());
				}
				info.setMiddleTable(cascade.joinMiddleTable());
				info.setInverseColumn(StringUtils.isEmpty(cascade.inverseColumn())
						? StringUtils.camelToUnder(info.getJoinTable().getSimpleName()) + "_id"
						: cascade.inverseColumn());
				fieldInfo.setCascadeInfo(info);
			}
			fieldInfo.setProperty(BeanUtils.getPropertyDescriptor(beanClass, field.getName()));
			return fieldInfo;
		}

	}
	


	
	
	public void invokeSet(Object target,Object value) {
		
		try {
			this.property.getWriteMethod().invoke(target, value);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}
		
	}
	
	public Object invokeGet(Object target) {
		try {
			return this.property.getReadMethod().invoke(target);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}
	}

	
	public Object deserializeObject(String json) {
		
	
		
	
		 if(List.class.isAssignableFrom(field.getType())) {
			 
			  Type type = field.getGenericType();
			  
			  if (type instanceof ParameterizedType pt) {
				  Type[] types = pt.getActualTypeArguments();
				  return JSON.parseArray(json, (Class<?>) types[0]);
			  }
			 
			 return JSON.parseArray(json);
			 
		 }
		 
		 return JSON.parseObject(json, field.getType());
		 	  
	}


	public Object serializeValue(Object value) {
		
		if(json) {
			return JSON.toJSONString(value);
		}
		return value;
		
	}
 	
	
}
