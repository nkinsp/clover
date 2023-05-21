package com.github.nkinsp.clover.table;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import org.springframework.beans.BeanUtils;

import com.github.nkinsp.clover.annotation.Cascade;
import com.github.nkinsp.clover.annotation.Column;
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
	
	private CascadeInfo cascadeInfo;

	
	public synchronized static EntityFieldInfo create(Class<?> beanClass, Field  field) {
		
		EntityFieldInfo info = new EntityFieldInfo();
		info.setFieldName(field.getName());
		info.setField(field);
		String colunmName = StringUtils.camelToUnder(field.getName());
		info.setColumnName(colunmName);
		Column column = field.getAnnotation(Column.class);
		if(column != null && StringUtils.isEmpty(column.value())) {	
			info.setColumnName(column.value());
			info.setAlias(column.alias());
		}
		Cascade cascade = field.getAnnotation(Cascade.class);
		if(cascade != null) {
			info.setCascade(true);
			CascadeInfo deinfo = new CascadeInfo();
			deinfo.setJoinTable(cascade.joinTable() == void.class ?field.getType():cascade.joinTable());
			deinfo.setJoinType(cascade.joinType());
			deinfo.setJoinColumn(StringUtils.isEmpty(cascade.joinColumn())?field.getName():cascade.joinColumn());
			deinfo.setMiddleTable(cascade.middleTable());
			if(cascade.joinType() == JoinType.MANY_TO_MANY ) {
				if(StringUtils.isEmpty(cascade.middleTable())) {
					throw new RuntimeException(String.format("Entity %s field %s @Cascade middleTable is not set", beanClass,field.getName()));
				}				
			}
			deinfo.setInverseColumn(StringUtils.isEmpty(cascade.inverseJoinColumn())?StringUtils.camelToUnder(deinfo.getJoinTable().getSimpleName())+"_id":cascade.inverseJoinColumn());
			info.setCascadeInfo(deinfo);
		}
		info.setProperty(BeanUtils.getPropertyDescriptor(beanClass, field.getName()));
		return info;
		
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



	
 	
	
}
