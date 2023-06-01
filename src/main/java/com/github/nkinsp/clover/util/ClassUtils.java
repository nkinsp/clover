package com.github.nkinsp.clover.util;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import org.springframework.beans.BeanUtils;


/**
 * 
 *
 */
public class ClassUtils {
	

	private static Map<String, Map<String,PropertyDescriptor>> pdCacheMap = new ConcurrentHashMap<>();
	
	private static Map<String, Map<String, Field>> fieldCacheMap = new ConcurrentHashMap<>();
	
	private static Map<String, Class<?>> classCacheMap = new ConcurrentHashMap<String, Class<?>>();
	
	private static Map<Class<?>, Object> singletonObjectMap = new ConcurrentHashMap<Class<?>, Object>();
	
	public static Class<?> forName(String name) {
		Class<?> findClass = classCacheMap.get(name);
		if (findClass != null) {
			return findClass;
		}
		try {
			Class<?> forName = Class.forName(name);
			classCacheMap.put(name, forName);
			return forName;

		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
	
	public synchronized static Map<String, Field> getClassFieldMap(Class<?> beanClass) {
		Map<String, Field> cacheFieldMap = fieldCacheMap.get(beanClass.getName());
		if (cacheFieldMap != null) {
			return cacheFieldMap;
		}
		final Map<String, Field> fieldMap = new LinkedHashMap<String, Field>();

		eachClassFields(beanClass, field -> {
			fieldMap.put(field.getName(), field);
			return true;
		});
		fieldCacheMap.put(beanClass.getName(), fieldMap);
		return fieldMap;

	}
	
	
	/**
	 * 循环获取类型的所有字段
	 * @param beanClass
	 * @param fieldCallback
	 * @return
	 */
	public static boolean eachClassFields(Class<?> beanClass,Function<Field,Boolean> fieldCallback) {
		if(beanClass != Object.class) {
			//获取
			boolean next = eachClassFields(beanClass.getSuperclass(), fieldCallback);
			if(!next) {
				return false;
			}
			Field[] fields = beanClass.getDeclaredFields();
			for (Field field : fields) {
				if(!fieldCallback.apply(field)) {
					return false;
				}
			}
		}
		return true;
	}

	
	/**
	 * 创建字段
	 * @param beanClass
	 * @param name
	 * @return
	 */
	public static PropertyDescriptor createProperty(Class<?> beanClass,String name) {
	
		PropertyDescriptor pd = BeanUtils.getPropertyDescriptor(beanClass, name);
		if(pd.getWriteMethod() == null && pd.getReadMethod() == null) {
			return null;
		}
		return pd;
	}
	
	/**
	 * 获取字段
	 * @param name
	 * @param beanClass
	 * @return
	 */
	public static Field findField(String name,Class<?> beanClass) {
		Map<String, Field> dataMap = getClassFieldMap(beanClass);
		return dataMap.get(name);
	}

	/**
	 * 获取字段
	 * @param name
	 * @param beanClass
	 * @return
	 */
	public static PropertyDescriptor getProperty(Class<?> beanClass,String name) {
		
		return getPropertyMap(beanClass).get(name);
		
	}
	
	/**
	 * 获取类的 属性
	 * 
	 * @param beanClass
	 * @return
	 */
	public static Map<String, PropertyDescriptor> getPropertyMap(Class<?> beanClass) {

		synchronized (beanClass) {

			Map<String, PropertyDescriptor> cachePdMap = pdCacheMap.get(beanClass.getName());
			if (cachePdMap != null) {
				return cachePdMap;
			}
			final Map<String, PropertyDescriptor> pdMap = new LinkedHashMap<String, PropertyDescriptor>();
			eachClassFields(beanClass, field -> {

				PropertyDescriptor pd = createProperty(beanClass, field.getName());
				if (pd != null) {
					pdMap.put(pd.getName(), pd);
				}
				return true;
			});
			pdCacheMap.put(beanClass.getName(), pdMap);
			return pdMap;
		}
	}
	
	public static synchronized Collection<PropertyDescriptor> getPropertys(Class<?> beanClass) {
		
		return getPropertyMap(beanClass).values();

	}
	
	/**
	 * 反射 创建一个对象 
	 * @param <T>
	 * @param clasz
	 * @return
	 */
	public static <T> T newInstance(Class<T> clasz) {
		return BeanUtils.instantiateClass(clasz);
	}
	
	
	@SuppressWarnings("unchecked")
	public static <T> T getSingleton(Class<T> entityClass) {
		
		synchronized (entityClass) {
			return (T) singletonObjectMap.computeIfAbsent(entityClass, s->newInstance(entityClass));
		}
		
	}
	
	/**
	 * 是否存在 annotation
	 * @param targetClass
	 * @param anClass
	 * @return
	 */
	public static boolean hasAnnotation(Class<?> targetClass,Class<? extends Annotation> anClass) {
		return targetClass.getAnnotation(anClass) != null;
	}
	
	
}
