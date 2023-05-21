package com.github.nkinsp.clover.util;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;

import com.github.nkinsp.clover.table.EntityFieldInfo;
import com.github.nkinsp.clover.table.EntityMapper;

/**
 *  工具类
 */
public class EntityMapperUtils {

	
	private  static Map<Class<?>, EntityMapper> entityMapperMap = new ConcurrentHashMap<Class<?>, EntityMapper>();
	
	/**
	 * 获取查询实体映射
	 * 
	 * @param entityClass
	 * @return
	 */
	public synchronized static EntityMapper getEntityMapper(Class<?> entityClass) {

		return entityMapperMap.computeIfAbsent(entityClass, clasz -> {
			Map<String, Field> fieldMap = ClassUtils.getClassFieldMap(clasz);
			List<EntityFieldInfo> columnMappers = Arrays.asList(BeanUtils.getPropertyDescriptors(clasz)).stream()
					.map(PropertyDescriptor::getName).filter(name -> fieldMap.containsKey(name))
					.map(name -> fieldMap.get(name)).map(field -> EntityFieldInfo.create(clasz, field))
					.collect(Collectors.toList());
			return new EntityMapper(clasz, columnMappers);
		});

	}
	
}
